package vn.edu.fpt.dao;

import jakarta.servlet.http.HttpServletRequest;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.delivery.ListDeliveryServlet.DeliveryRow;
import vn.edu.fpt.controller.delivery.ListShipperOrdersServlet.ShipperOrderRow;
import vn.edu.fpt.controller.delivery.ViewDeliveryServlet.DeliveryDetail;
import vn.edu.fpt.controller.delivery.ViewDeliveryServlet.DeliveryItem;
import vn.edu.fpt.controller.delivery.EditDeliveryStatusServlet.DeliveryStatusDetail;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO extends DBContext {
    private record DeliveryIdentity(int subOrderId, int masterOrderId) {}

    private String buildTrackingNumber(int subOrderId, int masterOrderId) {
        return "MODA-SUB-" + subOrderId + "-MO-" + masterOrderId;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public void loadPublicMetrics(HttpServletRequest request) throws Exception {
            String sql = """
                    SELECT COUNT(*) AS total_waiting,
                           COALESCE(SUM(CASE WHEN CAST(so.created_at AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END), 0) AS today_waiting,
                           COALESCE(SUM(CASE WHEN mo.payment_status = 'PAID' THEN 1 ELSE 0 END), 0) AS paid_orders,
                           COALESCE(SUM(CASE WHEN mo.payment_status <> 'PAID' THEN so.total_amount ELSE 0 END), 0) AS collect_amount
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    OUTER APPLY (
                        SELECT TOP 1 d.delivery_id, d.status
                        FROM deliveries d
                        WHERE d.sub_order_id = so.sub_order_id
                        ORDER BY d.delivery_id DESC
                    ) delivery
                    WHERE so.status = 'PREPARING'
                      AND delivery.delivery_id IS NULL
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalWaiting", rs.getInt("total_waiting"));
                        request.setAttribute("todayWaiting", rs.getInt("today_waiting"));
                        request.setAttribute("paidOrders", rs.getInt("paid_orders"));
                        request.setAttribute("collectAmount", rs.getBigDecimal("collect_amount"));
                        return;
                    }
                }
            }
    
            request.setAttribute("totalWaiting", 0);
            request.setAttribute("todayWaiting", 0);
            request.setAttribute("paidOrders", 0);
            request.setAttribute("collectAmount", BigDecimal.ZERO);
        }

    public List<DeliveryRow> loadPublicDeliveries(
                String search,
                String payment,
                String dateRange,
                String sort
        ) throws Exception {
            StringBuilder sql = new StringBuilder("""
                    SELECT COALESCE(delivery.delivery_id, 0) AS delivery_id,
                           COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) AS tracking_number,
                           COALESCE(delivery.status, 'WAITING') AS delivery_status,
                           delivery.assigned_at,
                           so.sub_order_id,
                           so.master_order_id,
                           so.status AS order_status,
                           so.total_amount,
                           so.created_at AS seller_ordered_at,
                           mo.created_at AS buyer_ordered_at,
                           mo.payment_method,
                           mo.payment_status,
                           mo.receiver_name,
                           mo.receiver_phone,
                           mo.shipping_address,
                           s.shop_id,
                           s.shop_name,
                           owner.phone AS seller_phone,
                           s.street_address + N', ' + w.path_with_type AS pickup_address,
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
                           ) AS total_quantity
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    INNER JOIN shops s ON s.shop_id = so.shop_id
                    INNER JOIN users owner ON owner.user_id = s.owner_id
                    INNER JOIN wards w ON w.id = s.ward_id
                    INNER JOIN users u ON u.user_id = mo.customer_id
                    OUTER APPLY (
                        SELECT TOP 1 d.delivery_id, d.tracking_number, d.status, d.assigned_at
                        FROM deliveries d
                        WHERE d.sub_order_id = so.sub_order_id
                        ORDER BY d.delivery_id DESC
                    ) delivery
                    WHERE so.status = 'PREPARING'
                      AND delivery.delivery_id IS NULL
                    """);
    
            List<Object> params = new ArrayList<>();
    
            if (!search.isBlank()) {
                sql.append("""
                        AND (
                            CAST(COALESCE(delivery.delivery_id, 0) AS VARCHAR(20)) LIKE ?
                            OR COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) LIKE ?
                            OR CAST(so.sub_order_id AS VARCHAR(20)) LIKE ?
                            OR CAST(so.master_order_id AS VARCHAR(20)) LIKE ?
                            OR s.shop_name LIKE ?
                            OR mo.receiver_name LIKE ?
                            OR mo.receiver_phone LIKE ?
                            OR u.first_name + ' ' + u.last_name LIKE ?
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
                for (int i = 0; i < 9; i++) {
                    params.add(keyword);
                }
            }
    
            if ("paid".equals(payment)) {
                sql.append(" AND mo.payment_status = 'PAID' ");
            } else if ("cod".equals(payment)) {
                sql.append(" AND mo.payment_status <> 'PAID' ");
            }
    
            if ("today".equals(dateRange)) {
                sql.append(" AND CAST(so.created_at AS DATE) = CAST(GETDATE() AS DATE) ");
            } else if ("7days".equals(dateRange)) {
                sql.append(" AND so.created_at >= DATEADD(DAY, -7, GETDATE()) ");
            } else if ("30days".equals(dateRange)) {
                sql.append(" AND so.created_at >= DATEADD(DAY, -30, GETDATE()) ");
            }
    
            if ("oldest".equals(sort)) {
                sql.append(" ORDER BY so.created_at ASC, delivery.delivery_id ASC ");
            } else if ("amount_desc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount DESC, so.created_at DESC ");
            } else if ("amount_asc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount ASC, so.created_at DESC ");
            } else {
                sql.append(" ORDER BY so.created_at DESC, delivery.delivery_id DESC ");
            }
    
            List<DeliveryRow> deliveries = new ArrayList<>();
    
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
    
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DeliveryRow row = new DeliveryRow();
                        row.setDeliveryId(rs.getInt("delivery_id"));
                        row.setTrackingNumber(rs.getString("tracking_number"));
                        row.setDeliveryStatus(rs.getString("delivery_status"));
                        row.setAssignedAt(rs.getTimestamp("assigned_at"));
                        row.setSubOrderId(rs.getInt("sub_order_id"));
                        row.setMasterOrderId(rs.getInt("master_order_id"));
                        row.setOrderStatus(rs.getString("order_status"));
                        row.setTotalAmount(rs.getBigDecimal("total_amount"));
                        row.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                        row.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at"));
                        row.setPaymentMethod(rs.getString("payment_method"));
                        row.setPaymentStatus(rs.getString("payment_status"));
                        row.setReceiverName(rs.getString("receiver_name"));
                        row.setReceiverPhone(rs.getString("receiver_phone"));
                        row.setShippingAddress(rs.getString("shipping_address"));
                        row.setShopId(rs.getInt("shop_id"));
                        row.setShopName(rs.getString("shop_name"));
                        row.setSellerPhone(rs.getString("seller_phone"));
                        row.setPickupAddress(rs.getString("pickup_address"));
                        row.setCustomerName(rs.getString("customer_name"));
                        row.setCustomerEmail(rs.getString("customer_email"));
                        row.setProductsSummary(rs.getString("products_summary"));
                        row.setItemCount(rs.getInt("item_count"));
                        row.setTotalQuantity(rs.getInt("total_quantity"));
                        deliveries.add(row);
                    }
                }
            }
    
            return deliveries;
        }

    public void loadShipperMetrics(HttpServletRequest request, int shipperId) throws Exception {
            String sql = """
                    SELECT COUNT(*) AS total_orders,
                           COALESCE(SUM(CASE WHEN d.status = 'ASSIGNED' THEN 1 ELSE 0 END), 0) AS assigned_orders,
                           COALESCE(SUM(CASE WHEN so.status = 'SHIPPING' THEN 1 ELSE 0 END), 0) AS shipping_orders,
                           COALESCE(SUM(CASE WHEN mo.payment_status <> 'PAID' THEN so.total_amount ELSE 0 END), 0) AS collect_amount
                    FROM deliveries d
                    INNER JOIN sub_orders so ON so.sub_order_id = d.sub_order_id
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    WHERE d.shipper_id = ?
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, shipperId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalOrders", rs.getInt("total_orders"));
                        request.setAttribute("assignedOrders", rs.getInt("assigned_orders"));
                        request.setAttribute("shippingOrders", rs.getInt("shipping_orders"));
                        request.setAttribute("collectAmount", rs.getBigDecimal("collect_amount"));
                        return;
                    }
                }
            }
    
            request.setAttribute("totalOrders", 0);
            request.setAttribute("assignedOrders", 0);
            request.setAttribute("shippingOrders", 0);
            request.setAttribute("collectAmount", BigDecimal.ZERO);
        }

    public List<ShipperOrderRow> loadShipperOrders(
                int shipperId,
                String search,
                String status,
                String payment,
                String sort
        ) throws Exception {
            StringBuilder sql = new StringBuilder("""
                    SELECT d.delivery_id,
                           d.tracking_number,
                           d.status AS delivery_status,
                           d.assigned_at,
                           so.sub_order_id,
                           so.master_order_id,
                           so.status AS order_status,
                           so.total_amount,
                           so.created_at AS prepared_at,
                           mo.created_at AS ordered_at,
                           mo.payment_method,
                           mo.payment_status,
                           mo.receiver_name,
                           mo.receiver_phone,
                           mo.shipping_address,
                           s.shop_id,
                           s.shop_name,
                           owner.phone AS seller_phone,
                           s.street_address + N', ' + w.path_with_type AS pickup_address,
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
                           ) AS total_quantity
                    FROM deliveries d
                    INNER JOIN sub_orders so ON so.sub_order_id = d.sub_order_id
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    INNER JOIN shops s ON s.shop_id = so.shop_id
                    INNER JOIN users owner ON owner.user_id = s.owner_id
                    INNER JOIN wards w ON w.id = s.ward_id
                    WHERE d.shipper_id = ?
                    """);
    
            List<Object> params = new ArrayList<>();
            params.add(shipperId);
    
            if (!search.isBlank()) {
                sql.append("""
                        AND (
                            d.tracking_number LIKE ?
                            OR CAST(d.delivery_id AS VARCHAR(20)) LIKE ?
                            OR CAST(so.sub_order_id AS VARCHAR(20)) LIKE ?
                            OR CAST(so.master_order_id AS VARCHAR(20)) LIKE ?
                            OR s.shop_name LIKE ?
                            OR mo.receiver_name LIKE ?
                            OR mo.receiver_phone LIKE ?
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
                for (int i = 0; i < 8; i++) {
                    params.add(keyword);
                }
            }
    
            if ("assigned".equals(status)) {
                sql.append(" AND d.status = 'ASSIGNED' ");
            } else if ("shipping".equals(status)) {
                sql.append(" AND so.status = 'SHIPPING' ");
            } else if ("delivered".equals(status)) {
                sql.append(" AND (d.status = 'DELIVERED' OR so.status = 'DELIVERED') ");
            }
    
            if ("paid".equals(payment)) {
                sql.append(" AND mo.payment_status = 'PAID' ");
            } else if ("cod".equals(payment)) {
                sql.append(" AND mo.payment_status <> 'PAID' ");
            }
    
            if ("oldest".equals(sort)) {
                sql.append(" ORDER BY d.assigned_at ASC, d.delivery_id ASC ");
            } else if ("amount_desc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount DESC, d.assigned_at DESC ");
            } else if ("amount_asc".equals(sort)) {
                sql.append(" ORDER BY so.total_amount ASC, d.assigned_at DESC ");
            } else {
                sql.append(" ORDER BY d.assigned_at DESC, d.delivery_id DESC ");
            }
    
            List<ShipperOrderRow> orders = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
    
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ShipperOrderRow row = new ShipperOrderRow();
                        row.setDeliveryId(rs.getInt("delivery_id"));
                        row.setTrackingNumber(rs.getString("tracking_number"));
                        row.setDeliveryStatus(rs.getString("delivery_status"));
                        row.setAssignedAt(rs.getTimestamp("assigned_at"));
                        row.setSubOrderId(rs.getInt("sub_order_id"));
                        row.setMasterOrderId(rs.getInt("master_order_id"));
                        row.setOrderStatus(rs.getString("order_status"));
                        row.setTotalAmount(rs.getBigDecimal("total_amount"));
                        row.setPreparedAt(rs.getTimestamp("prepared_at"));
                        row.setOrderedAt(rs.getTimestamp("ordered_at"));
                        row.setPaymentMethod(rs.getString("payment_method"));
                        row.setPaymentStatus(rs.getString("payment_status"));
                        row.setReceiverName(rs.getString("receiver_name"));
                        row.setReceiverPhone(rs.getString("receiver_phone"));
                        row.setShippingAddress(rs.getString("shipping_address"));
                        row.setShopId(rs.getInt("shop_id"));
                        row.setShopName(rs.getString("shop_name"));
                        row.setSellerPhone(rs.getString("seller_phone"));
                        row.setPickupAddress(rs.getString("pickup_address"));
                        row.setProductsSummary(rs.getString("products_summary"));
                        row.setItemCount(rs.getInt("item_count"));
                        row.setTotalQuantity(rs.getInt("total_quantity"));
                        orders.add(row);
                    }
                }
            }
    
            return orders;
        }

    public int receiveDelivery(int subOrderId, int shipperId) throws Exception {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
    
            try {
                DeliveryIdentity identity = loadReceivableOrder(subOrderId);
                if (identity == null) {
                    connection.rollback();
                    return 0;
                }
    
                if (hasDelivery(subOrderId)) {
                    connection.rollback();
                    return 0;
                }
    
                int deliveryId = insertDelivery(identity, shipperId);
                connection.commit();
                return deliveryId;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }

    public DeliveryIdentity loadReceivableOrder(int subOrderId) throws Exception {
            String sql = """
                    SELECT so.sub_order_id,
                           so.master_order_id
                    FROM sub_orders so WITH (UPDLOCK, HOLDLOCK)
                    WHERE so.sub_order_id = ?
                      AND so.status = 'PREPARING'
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, subOrderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
    
                    return new DeliveryIdentity(
                            rs.getInt("sub_order_id"),
                            rs.getInt("master_order_id")
                    );
                }
            }
        }

    public boolean hasDelivery(int subOrderId) throws Exception {
            String sql = "SELECT 1 FROM deliveries WITH (UPDLOCK, HOLDLOCK) WHERE sub_order_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, subOrderId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }

    public int insertDelivery(DeliveryIdentity identity, int shipperId) throws Exception {
            String sql = """
                    INSERT INTO deliveries (tracking_number, sub_order_id, shipper_id, status)
                    VALUES (?, ?, ?, 'ASSIGNED')
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, buildTrackingNumber(identity.subOrderId, identity.masterOrderId));
                ps.setInt(2, identity.subOrderId);
                ps.setInt(3, shipperId);
                ps.executeUpdate();
    
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
    
            return loadDeliveryId(identity.subOrderId, shipperId);
        }

    public int loadDeliveryId(int subOrderId, int shipperId) throws Exception {
            String sql = """
                    SELECT TOP 1 delivery_id
                    FROM deliveries
                    WHERE sub_order_id = ?
                      AND shipper_id = ?
                    ORDER BY delivery_id DESC
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, subOrderId);
                ps.setInt(2, shipperId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt("delivery_id") : 0;
                }
            }
        }

    public DeliveryDetail getDeliveryDetail(
                Integer subOrderId,
                Integer deliveryId,
                int shipperId
        ) throws Exception {
            StringBuilder sql = new StringBuilder("""
                    SELECT COALESCE(delivery.delivery_id, 0) AS delivery_id,
                           COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) AS tracking_number,
                           COALESCE(delivery.status, 'WAITING') AS delivery_status,
                           delivery.assigned_at,
                           delivery.shipper_id,
                           so.sub_order_id,
                           so.master_order_id,
                           so.status AS order_status,
                           so.sub_total,
                           so.discount_amount,
                           so.total_amount,
                           so.created_at AS prepared_at,
                           mo.created_at AS ordered_at,
                           mo.payment_method,
                           mo.payment_status,
                           mo.receiver_name,
                           mo.receiver_phone,
                           mo.shipping_address,
                           s.shop_id,
                           s.shop_name,
                           s.street_address + N', ' + w.path_with_type AS pickup_address,
                           owner.first_name + ' ' + owner.last_name AS seller_name,
                           owner.phone AS seller_phone,
                           owner.email AS seller_email,
                           customer.first_name + ' ' + customer.last_name AS customer_name,
                           customer.phone AS customer_phone,
                           customer.email AS customer_email
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    INNER JOIN shops s ON s.shop_id = so.shop_id
                    INNER JOIN wards w ON w.id = s.ward_id
                    INNER JOIN users owner ON owner.user_id = s.owner_id
                    INNER JOIN users customer ON customer.user_id = mo.customer_id
                    OUTER APPLY (
                        SELECT TOP 1 d.delivery_id, d.tracking_number, d.status, d.assigned_at, d.shipper_id
                        FROM deliveries d
                        WHERE d.sub_order_id = so.sub_order_id
                        ORDER BY d.delivery_id DESC
                    ) delivery
                    WHERE 1 = 1
                    """);
    
            List<Object> params = new ArrayList<>();
            if (deliveryId != null) {
                sql.append(" AND delivery.delivery_id = ? AND delivery.shipper_id = ? ");
                params.add(deliveryId);
                params.add(shipperId);
            } else {
                sql.append(" AND so.sub_order_id = ? AND so.status = 'PREPARING' AND delivery.delivery_id IS NULL ");
                params.add(subOrderId);
            }
    
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
    
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
    
                    DeliveryDetail detail = new DeliveryDetail();
                    detail.setDeliveryId(rs.getInt("delivery_id"));
                    detail.setTrackingNumber(rs.getString("tracking_number"));
                    detail.setDeliveryStatus(rs.getString("delivery_status"));
                    detail.setAssignedAt(rs.getTimestamp("assigned_at"));
                    detail.setShipperId((Integer) rs.getObject("shipper_id"));
                    detail.setSubOrderId(rs.getInt("sub_order_id"));
                    detail.setMasterOrderId(rs.getInt("master_order_id"));
                    detail.setOrderStatus(rs.getString("order_status"));
                    detail.setSubTotal(rs.getBigDecimal("sub_total"));
                    detail.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    detail.setTotalAmount(rs.getBigDecimal("total_amount"));
                    detail.setPreparedAt(rs.getTimestamp("prepared_at"));
                    detail.setOrderedAt(rs.getTimestamp("ordered_at"));
                    detail.setPaymentMethod(rs.getString("payment_method"));
                    detail.setPaymentStatus(rs.getString("payment_status"));
                    detail.setReceiverName(rs.getString("receiver_name"));
                    detail.setReceiverPhone(rs.getString("receiver_phone"));
                    detail.setShippingAddress(rs.getString("shipping_address"));
                    detail.setShopId(rs.getInt("shop_id"));
                    detail.setShopName(rs.getString("shop_name"));
                    detail.setPickupAddress(rs.getString("pickup_address"));
                    detail.setSellerName(rs.getString("seller_name"));
                    detail.setSellerPhone(rs.getString("seller_phone"));
                    detail.setSellerEmail(rs.getString("seller_email"));
                    detail.setCustomerName(rs.getString("customer_name"));
                    detail.setCustomerPhone(rs.getString("customer_phone"));
                    detail.setCustomerEmail(rs.getString("customer_email"));
                    detail.setCollectAmount("PAID".equals(detail.getPaymentStatus()) ? BigDecimal.ZERO : safe(detail.getTotalAmount()));
                    return detail;
                }
            }
        }

    public List<DeliveryItem> getDeliveryItems(int subOrderId) throws Exception {
            String sql = """
                    SELECT oi.order_item_id,
                           oi.product_id,
                           oi.variant_id,
                           oi.quantity,
                           oi.price_at_purchase,
                           oi.price_at_purchase * oi.quantity AS line_total,
                           p.product_name,
                           p.thumbnail_url,
                           pv.variant_name,
                           c.color_name,
                           sz.size_name
                    FROM order_items oi
                    INNER JOIN products p ON p.product_id = oi.product_id
                    LEFT JOIN product_variants pv ON pv.variant_id = oi.variant_id
                    LEFT JOIN colors c ON c.color_id = pv.color_id
                    LEFT JOIN sizes sz ON sz.size_id = pv.size_id
                    WHERE oi.sub_order_id = ?
                    ORDER BY oi.order_item_id ASC
                    """;
    
            List<DeliveryItem> items = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, subOrderId);
    
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DeliveryItem item = new DeliveryItem();
                        item.setOrderItemId(rs.getInt("order_item_id"));
                        item.setProductId(rs.getInt("product_id"));
                        item.setVariantId((Integer) rs.getObject("variant_id"));
                        item.setQuantity(rs.getInt("quantity"));
                        item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                        item.setLineTotal(rs.getBigDecimal("line_total"));
                        item.setProductName(rs.getString("product_name"));
                        item.setThumbnailUrl(rs.getString("thumbnail_url"));
                        item.setVariantName(rs.getString("variant_name"));
                        item.setColorName(rs.getString("color_name"));
                        item.setSizeName(rs.getString("size_name"));
                        items.add(item);
                    }
                }
            }
            return items;
        }

    public DeliveryStatusDetail getStatusDetail(int deliveryId, int shipperId) throws Exception {
            String sql = """
                    SELECT d.delivery_id,
                           d.tracking_number,
                           d.status AS delivery_status,
                           d.assigned_at,
                           so.sub_order_id,
                           so.master_order_id,
                           so.status AS order_status,
                           so.total_amount,
                           mo.created_at AS ordered_at,
                           mo.payment_method,
                           mo.payment_status,
                           mo.receiver_name,
                           mo.receiver_phone,
                           mo.shipping_address,
                           s.shop_name,
                           owner.phone AS seller_phone,
                           s.street_address + N', ' + w.path_with_type AS pickup_address,
                           (
                               SELECT STRING_AGG(CAST(p.product_name AS NVARCHAR(MAX)) + N' (x' + CAST(oi.quantity AS NVARCHAR(10)) + N')', N', ')
                               FROM order_items oi
                               INNER JOIN products p ON p.product_id = oi.product_id
                               WHERE oi.sub_order_id = so.sub_order_id
                           ) AS products_summary,
                           (
                               SELECT COALESCE(SUM(oi.quantity), 0)
                               FROM order_items oi
                               WHERE oi.sub_order_id = so.sub_order_id
                           ) AS total_quantity
                    FROM deliveries d
                    INNER JOIN sub_orders so ON so.sub_order_id = d.sub_order_id
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    INNER JOIN shops s ON s.shop_id = so.shop_id
                    INNER JOIN users owner ON owner.user_id = s.owner_id
                    INNER JOIN wards w ON w.id = s.ward_id
                    WHERE d.delivery_id = ?
                      AND d.shipper_id = ?
                    """;
    
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, deliveryId);
                ps.setInt(2, shipperId);
    
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
    
                    DeliveryStatusDetail detail = new DeliveryStatusDetail();
                    detail.setDeliveryId(rs.getInt("delivery_id"));
                    detail.setTrackingNumber(rs.getString("tracking_number"));
                    detail.setDeliveryStatus(rs.getString("delivery_status"));
                    detail.setAssignedAt(rs.getTimestamp("assigned_at"));
                    detail.setSubOrderId(rs.getInt("sub_order_id"));
                    detail.setMasterOrderId(rs.getInt("master_order_id"));
                    detail.setOrderStatus(rs.getString("order_status"));
                    detail.setTotalAmount(rs.getBigDecimal("total_amount"));
                    detail.setOrderedAt(rs.getTimestamp("ordered_at"));
                    detail.setPaymentMethod(rs.getString("payment_method"));
                    detail.setPaymentStatus(rs.getString("payment_status"));
                    detail.setReceiverName(rs.getString("receiver_name"));
                    detail.setReceiverPhone(rs.getString("receiver_phone"));
                    detail.setShippingAddress(rs.getString("shipping_address"));
                    detail.setShopName(rs.getString("shop_name"));
                    detail.setSellerPhone(rs.getString("seller_phone"));
                    detail.setPickupAddress(rs.getString("pickup_address"));
                    detail.setProductsSummary(rs.getString("products_summary"));
                    detail.setTotalQuantity(rs.getInt("total_quantity"));
                    detail.setCollectAmount("PAID".equals(detail.getPaymentStatus())
                            ? BigDecimal.ZERO
                            : safe(detail.getTotalAmount()));
                    return detail;
                }
            }
        }

    public boolean markDelivered(DeliveryStatusDetail detail, int shipperId) throws Exception {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
    
            try {
                ensureDeliveredAtColumn();
    
                String updateDeliverySql = """
                        UPDATE deliveries
                        SET status = 'DELIVERED'
                        WHERE delivery_id = ?
                          AND shipper_id = ?
                          AND status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')
                        """;
                try (PreparedStatement ps = connection.prepareStatement(updateDeliverySql)) {
                    ps.setInt(1, detail.getDeliveryId());
                    ps.setInt(2, shipperId);
                    if (ps.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
    
                String updateOrderSql = """
                        UPDATE sub_orders
                        SET status = 'DELIVERED',
                            delivered_at = COALESCE(delivered_at, GETDATE())
                        WHERE sub_order_id = ?
                          AND status = 'SHIPPING'
                        """;
                try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                    ps.setInt(1, detail.getSubOrderId());
                    if (ps.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
    
                connection.commit();
                return true;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }

    private void ensureDeliveredAtColumn() throws Exception {
            String sql = """
                    IF COL_LENGTH('sub_orders', 'delivered_at') IS NULL
                    BEGIN
                        ALTER TABLE sub_orders ADD delivered_at DATETIME NULL
                    END
                    """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.execute();
            }
        }
}
