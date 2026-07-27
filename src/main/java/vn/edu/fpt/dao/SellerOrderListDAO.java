package vn.edu.fpt.dao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.seller.ListSellerOrdersServlet.SellerOrderRow;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SellerOrderListDAO extends DBContext {
    private static final long SELLER_TOAST_DURATION_MILLIS = 10_000L;
    public void loadOrderMetrics(HttpServletRequest request, int shopId) throws Exception {
            String sql = """
                    SELECT COUNT(*) AS total_orders,
                           COALESCE(SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END), 0) AS pending_orders,
                           COALESCE(SUM(CASE WHEN status IN ('CONFIRMED', 'PREPARING', 'SHIPPING') THEN 1 ELSE 0 END), 0) AS processing_orders,
                           COALESCE(SUM(CASE WHEN status in('DELIVERED', 'COMPLETED') THEN 1 ELSE 0 END), 0) AS delivered_orders,
                           COALESCE(SUM(CASE WHEN status <> 'CANCELLED' THEN total_amount ELSE 0 END), 0) AS gross_amount
                    FROM sub_orders
                    WHERE shop_id = ?
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalOrders", rs.getInt("total_orders"));
                        request.setAttribute("pendingOrders", rs.getInt("pending_orders"));
                        request.setAttribute("processingOrders", rs.getInt("processing_orders"));
                        request.setAttribute("deliveredOrders", rs.getInt("delivered_orders"));
                        request.setAttribute("grossAmount", rs.getBigDecimal("gross_amount"));
                        return;
                    }
                }
            }
    
            request.setAttribute("totalOrders", 0);
            request.setAttribute("pendingOrders", 0);
            request.setAttribute("processingOrders", 0);
            request.setAttribute("deliveredOrders", 0);
            request.setAttribute("grossAmount", BigDecimal.ZERO);
        }

    public List<SellerOrderRow> loadSellerOrders(
                int shopId,
                String search,
                String status,
                String dateRange,
                String sort
        ) throws Exception {
            StringBuilder sql = new StringBuilder("""
                    SELECT so.sub_order_id,
                           so.master_order_id,
                           mo.created_at AS buyer_ordered_at,
                           so.created_at AS seller_ordered_at,
                           so.status,
                           so.sub_total,
                           so.discount_amount,
                           so.total_amount,
                           so.commission_fee,
                           mo.payment_method,
                           mo.payment_status,
                           mo.receiver_name,
                           mo.receiver_phone,
                           mo.shipping_address,
                           u.first_name + ' ' + u.last_name AS customer_name,
                           u.email AS customer_email,
                           (
                               SELECT STRING_AGG(CAST(p.product_name AS NVARCHAR(MAX)) + N' (x' + CAST(oi.quantity AS NVARCHAR(10)) + N')', N', ')
                               FROM order_items oi
                               INNER JOIN products p ON p.product_id = oi.product_id
                               WHERE oi.sub_order_id = so.sub_order_id
                           ) AS products_summary,
                           (
                               SELECT COUNT(*)
                               FROM order_items oi
                               WHERE oi.sub_order_id = so.sub_order_id
                           ) AS item_count,
                           (
                               SELECT COALESCE(SUM(oi.quantity), 0)
                               FROM order_items oi
                               WHERE oi.sub_order_id = so.sub_order_id
                           ) AS total_quantity,
                           delivery.shipper_id AS assigned_shipper_id,
                           delivery.shipper_name,
                           delivery.shipper_phone
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    INNER JOIN users u ON u.user_id = mo.customer_id
                    OUTER APPLY (
                        SELECT TOP 1
                               d.shipper_id,
                               shipper.first_name + ' ' + shipper.last_name AS shipper_name,
                               shipper.phone AS shipper_phone
                        FROM deliveries d
                        INNER JOIN users shipper ON shipper.user_id = d.shipper_id
                        WHERE d.sub_order_id = so.sub_order_id
                          AND d.shipper_id IS NOT NULL
                          AND d.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'COMPLETED')
                        ORDER BY d.delivery_id DESC
                    ) delivery
                    WHERE so.shop_id = ?
                    """);
            List<Object> params = new ArrayList<>();
            params.add(shopId);
    
            if (!search.isBlank()) {
                sql.append("""
                        AND (
                            CAST(so.sub_order_id AS VARCHAR(20)) LIKE ?
                            OR CAST(so.master_order_id AS VARCHAR(20)) LIKE ?
                            OR mo.receiver_name LIKE ?
                            OR mo.receiver_phone LIKE ?
                            OR u.first_name + ' ' + u.last_name LIKE ?
                            OR u.email LIKE ?
                            OR EXISTS (
                                SELECT 1
                                FROM order_items oi
                                INNER JOIN products p ON p.product_id = oi.product_id
                                WHERE oi.sub_order_id = so.sub_order_id
                                  AND p.product_name LIKE ?
                            )
                        )
                        """);
                String keyword = "%" + search + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }
    
            if (!status.isBlank()) {
                sql.append(" AND so.status = ? ");
                params.add(status);
            }
    
            if ("today".equals(dateRange)) {
                sql.append(" AND CAST(mo.created_at AS DATE) = CAST(GETDATE() AS DATE) ");
            } else if ("7days".equals(dateRange)) {
                sql.append(" AND mo.created_at >= DATEADD(DAY, -7, GETDATE()) ");
            } else if ("30days".equals(dateRange)) {
                sql.append(" AND mo.created_at >= DATEADD(DAY, -30, GETDATE()) ");
            }
    
            if ("oldest".equals(sort)) {
                sql.append(" ORDER BY mo.created_at ASC, so.sub_order_id ASC ");
            } else if ("amount_desc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount DESC, mo.created_at DESC ");
            } else if ("amount_asc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount ASC, mo.created_at DESC ");
            } else {
                sql.append(" ORDER BY mo.created_at DESC, so.sub_order_id DESC ");
            }
    
            List<SellerOrderRow> orders = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
    
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SellerOrderRow row = new SellerOrderRow();
                        row.setSubOrderId(rs.getInt("sub_order_id"));
                        row.setMasterOrderId(rs.getInt("master_order_id"));
                        row.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at"));
                        row.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                        row.setStatus(rs.getString("status"));
                        row.setSubTotal(rs.getBigDecimal("sub_total"));
                        row.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                        row.setTotalAmount(rs.getBigDecimal("total_amount"));
                        row.setCommissionFee(rs.getBigDecimal("commission_fee"));
                        row.setPaymentMethod(rs.getString("payment_method"));
                        row.setPaymentStatus(rs.getString("payment_status"));
                        row.setReceiverName(rs.getString("receiver_name"));
                        row.setReceiverPhone(rs.getString("receiver_phone"));
                        row.setShippingAddress(rs.getString("shipping_address"));
                        row.setCustomerName(rs.getString("customer_name"));
                        row.setCustomerEmail(rs.getString("customer_email"));
                        row.setProductsSummary(rs.getString("products_summary"));
                        row.setItemCount(rs.getInt("item_count"));
                        row.setTotalQuantity(rs.getInt("total_quantity"));
                        row.setShipperAssigned(rs.getObject("assigned_shipper_id") != null);
                        row.setShipperName(rs.getString("shipper_name"));
                        row.setShipperPhone(rs.getString("shipper_phone"));
                        orders.add(row);
                    }
                }
            }
            return orders;
        }

    public void preparePendingOrderToast(HttpServletRequest request, int shopId) throws Exception {
            HttpSession session = request.getSession(false);
            if (session == null || Boolean.TRUE.equals(session.getAttribute("sellerPendingOrderToastShown"))) {
                return;
            }
    
            String sql = """
                    SELECT TOP 1 sub_order_id
                    FROM sub_orders
                    WHERE shop_id = ?
                      AND status = 'PENDING'
                    ORDER BY created_at ASC, sub_order_id ASC
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int subOrderId = rs.getInt("sub_order_id");
                        request.setAttribute("pendingOrderToastSubOrderId", subOrderId);
                        request.setAttribute("pendingOrderToastMessage",
                                "#SUB-" + subOrderId + " chưa được xác nhận");
                        session.setAttribute("sellerPendingOrderToastSubOrderId", subOrderId);
                        session.setAttribute("sellerPendingOrderToastMessage",
                                "#SUB-" + subOrderId + " chưa được xác nhận");
                        session.setAttribute("sellerPendingOrderToastExpiresAt",
                                System.currentTimeMillis() + SELLER_TOAST_DURATION_MILLIS);
                        session.setAttribute("sellerPendingOrderToastShown", true);
                        session.removeAttribute("sellerPendingOrderToastAnimated");
                    }
                }
            }
        }
}
