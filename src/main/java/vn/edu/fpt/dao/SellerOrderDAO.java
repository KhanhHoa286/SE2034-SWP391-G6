package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.seller.EditSellerStatusServlet.SellerStatusOrder;
import vn.edu.fpt.controller.seller.ViewSellerOrderServlet.SellerOrderDetail;
import vn.edu.fpt.controller.seller.ViewSellerOrderServlet.SellerOrderItem;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** Database operations used by the seller order screens. */
public class SellerOrderDAO extends DBContext {

    public SellerOrderDetail getOrderDetail(int shopId, int subOrderId) throws Exception {
        String sql = """
                SELECT so.sub_order_id, so.master_order_id, so.shop_id, s.shop_name,
                       mo.created_at AS buyer_ordered_at, so.created_at AS seller_ordered_at,
                       so.status, so.sub_total, so.discount_amount, so.total_amount, so.commission_fee,
                       mo.payment_method, mo.payment_status, mo.payment_date,
                       mo.receiver_name, mo.receiver_phone, mo.shipping_address,
                       u.user_id AS customer_id, u.first_name + ' ' + u.last_name AS customer_name,
                       u.email AS customer_email, u.phone AS customer_phone
                FROM sub_orders so
                INNER JOIN shops s ON s.shop_id = so.shop_id
                INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                INNER JOIN users u ON u.user_id = mo.customer_id
                WHERE so.sub_order_id = ? AND so.shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            ps.setInt(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                SellerOrderDetail d = new SellerOrderDetail();
                d.setSubOrderId(rs.getInt("sub_order_id")); d.setMasterOrderId(rs.getInt("master_order_id"));
                d.setShopId(rs.getInt("shop_id")); d.setShopName(rs.getString("shop_name"));
                d.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at")); d.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                d.setStatus(rs.getString("status")); d.setSubTotal(rs.getBigDecimal("sub_total"));
                d.setDiscountAmount(rs.getBigDecimal("discount_amount")); d.setTotalAmount(rs.getBigDecimal("total_amount"));
                d.setCommissionFee(rs.getBigDecimal("commission_fee"));
                d.setSellerReceivable(safe(d.getTotalAmount()).subtract(safe(d.getCommissionFee())));
                d.setPaymentMethod(rs.getString("payment_method")); d.setPaymentStatus(rs.getString("payment_status"));
                d.setPaymentDate(rs.getTimestamp("payment_date")); d.setReceiverName(rs.getString("receiver_name"));
                d.setReceiverPhone(rs.getString("receiver_phone")); d.setShippingAddress(rs.getString("shipping_address"));
                d.setCustomerId(rs.getInt("customer_id")); d.setCustomerName(rs.getString("customer_name"));
                d.setCustomerEmail(rs.getString("customer_email")); d.setCustomerPhone(rs.getString("customer_phone"));
                d.setItems(getOrderItems(subOrderId));
                return d;
            }
        }
    }

    public List<SellerOrderItem> getOrderItems(int subOrderId) throws Exception {
        String sql = """
                SELECT oi.order_item_id, oi.product_id, oi.variant_id, oi.quantity, oi.price_at_purchase,
                       oi.price_at_purchase * oi.quantity AS line_total, p.product_name, p.thumbnail_url,
                       pv.variant_name, c.color_name, c.color_code, sz.size_name
                FROM order_items oi
                INNER JOIN products p ON p.product_id = oi.product_id
                LEFT JOIN product_variants pv ON pv.variant_id = oi.variant_id
                LEFT JOIN colors c ON c.color_id = pv.color_id
                LEFT JOIN sizes sz ON sz.size_id = pv.size_id
                WHERE oi.sub_order_id = ? ORDER BY oi.order_item_id
                """;
        List<SellerOrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerOrderItem i = new SellerOrderItem();
                    i.setOrderItemId(rs.getInt("order_item_id")); i.setProductId(rs.getInt("product_id"));
                    i.setVariantId((Integer) rs.getObject("variant_id")); i.setQuantity(rs.getInt("quantity"));
                    i.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase")); i.setLineTotal(rs.getBigDecimal("line_total"));
                    i.setProductName(rs.getString("product_name")); i.setThumbnailUrl(rs.getString("thumbnail_url"));
                    i.setVariantName(rs.getString("variant_name")); i.setColorName(rs.getString("color_name"));
                    i.setColorCode(rs.getString("color_code")); i.setSizeName(rs.getString("size_name"));
                    items.add(i);
                }
            }
        }
        return items;
    }

    public SellerStatusOrder getStatusOrder(int shopId, int subOrderId) throws Exception {
        String sql = """
                SELECT so.sub_order_id, so.master_order_id, so.shop_id, s.shop_name,
                       owner.phone AS seller_phone, s.street_address + N', ' + w.path_with_type AS pickup_address,
                       mo.created_at AS buyer_ordered_at, so.created_at AS seller_ordered_at, so.status,
                       so.total_amount, so.commission_fee,
                       COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) AS tracking_number,
                       delivery.shipper_id AS assigned_shipper_id, delivery.shipper_name, delivery.shipper_phone,
                       mo.payment_method, mo.payment_status, mo.receiver_name, mo.receiver_phone, mo.shipping_address,
                       u.first_name + ' ' + u.last_name AS customer_name, u.email AS customer_email,
                       (SELECT STRING_AGG(CAST(p.product_name AS NVARCHAR(MAX)) + N' (x' + CAST(oi.quantity AS NVARCHAR(10)) + N')', N', ')
                          FROM order_items oi INNER JOIN products p ON p.product_id = oi.product_id WHERE oi.sub_order_id = so.sub_order_id) AS products_summary,
                       (SELECT COUNT(*) FROM order_items oi WHERE oi.sub_order_id = so.sub_order_id) AS item_count,
                       (SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi WHERE oi.sub_order_id = so.sub_order_id) AS total_quantity
                FROM sub_orders so
                INNER JOIN shops s ON s.shop_id = so.shop_id INNER JOIN users owner ON owner.user_id = s.owner_id
                INNER JOIN wards w ON w.id = s.ward_id INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                INNER JOIN users u ON u.user_id = mo.customer_id
                OUTER APPLY (SELECT TOP 1 d.tracking_number, d.shipper_id,
                    shipper.first_name + ' ' + shipper.last_name AS shipper_name, shipper.phone AS shipper_phone
                    FROM deliveries d LEFT JOIN users shipper ON shipper.user_id = d.shipper_id
                    WHERE d.sub_order_id = so.sub_order_id ORDER BY d.delivery_id DESC) delivery
                WHERE so.sub_order_id = ? AND so.shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId); ps.setInt(2, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                SellerStatusOrder o = new SellerStatusOrder();
                o.setSubOrderId(rs.getInt("sub_order_id")); o.setMasterOrderId(rs.getInt("master_order_id"));
                o.setShopId(rs.getInt("shop_id")); o.setShopName(rs.getString("shop_name"));
                o.setSellerPhone(rs.getString("seller_phone")); o.setPickupAddress(rs.getString("pickup_address"));
                o.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at")); o.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                o.setStatus(rs.getString("status")); o.setTotalAmount(rs.getBigDecimal("total_amount"));
                o.setCommissionFee(rs.getBigDecimal("commission_fee")); o.setTrackingNumber(rs.getString("tracking_number"));
                o.setShipperAssigned(rs.getObject("assigned_shipper_id") != null); o.setShipperName(rs.getString("shipper_name"));
                o.setShipperPhone(rs.getString("shipper_phone")); o.setPaymentMethod(rs.getString("payment_method"));
                o.setPaymentStatus(rs.getString("payment_status")); o.setReceiverName(rs.getString("receiver_name"));
                o.setReceiverPhone(rs.getString("receiver_phone")); o.setShippingAddress(rs.getString("shipping_address"));
                o.setCustomerName(rs.getString("customer_name")); o.setCustomerEmail(rs.getString("customer_email"));
                o.setProductsSummary(rs.getString("products_summary")); o.setItemCount(rs.getInt("item_count"));
                o.setTotalQuantity(rs.getInt("total_quantity"));
                return o;
            }
        }
    }

    public boolean updateStatus(SellerStatusOrder order, String newStatus) throws Exception {
        String sql = "UPDATE sub_orders SET status = ? WHERE sub_order_id = ? AND shop_id = ? AND status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus); ps.setInt(2, order.getSubOrderId());
            ps.setInt(3, order.getShopId()); ps.setString(4, order.getStatus());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean hasAssignedDelivery(int subOrderId) throws Exception {
        String sql = "SELECT TOP 1 1 FROM deliveries WHERE sub_order_id = ? AND shipper_id IS NOT NULL AND status IN ('ASSIGNED','PICKED_UP','IN_TRANSIT','DELIVERED','COMPLETED')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private BigDecimal safe(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
}
