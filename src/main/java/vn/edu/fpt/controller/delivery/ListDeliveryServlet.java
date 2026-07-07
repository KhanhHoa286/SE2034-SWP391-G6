package vn.edu.fpt.controller.delivery;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = {"/logistics/delivery/list", "/logistics/delivery/list-deliveries"})
public class ListDeliveryServlet extends HttpServlet {

    private static final String LIST_PAGE = "/logistics/delivery/list-deliveries.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-list");

        Integer shipperId = resolveShipperId(request);
        if (shipperId == null) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            setEmptyData(request);
            request.getRequestDispatcher(LIST_PAGE).forward(request, response);
            return;
        }

        String search = trim(request.getParameter("search"));
        String payment = trim(request.getParameter("payment"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("search", search);
        request.setAttribute("payment", payment);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try (Connection connection = openConnection()) {
            loadMetrics(connection, request);
            request.setAttribute("deliveries", loadDeliveries(connection, search, payment, dateRange, sort));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách đơn giao hàng. Vui lòng kiểm tra kết nối database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(LIST_PAGE).forward(request, response);
    }

    private Integer resolveShipperId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userObject = session.getAttribute("user");
        if (userObject instanceof User user && user.getUserId() != null) {
            return user.getUserId();
        }

        Object userIdObject = session.getAttribute("userId");
        if (userIdObject == null) {
            return null;
        }

        try {
            if (userIdObject instanceof Integer) {
                return (Integer) userIdObject;
            }
            return Integer.parseInt(userIdObject.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    private void loadMetrics(Connection connection, HttpServletRequest request) throws Exception {
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
                  AND (delivery.delivery_id IS NULL OR delivery.status = 'ASSIGNED')
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

    private List<DeliveryRow> loadDeliveries(
            Connection connection,
            String search,
            String payment,
            String dateRange,
            String sort
    ) throws Exception {
        StringBuilder sql = new StringBuilder("""
                SELECT COALESCE(delivery.delivery_id, 0) AS delivery_id,
                       COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) AS tracking_number,
                       COALESCE(delivery.status, 'ASSIGNED') AS delivery_status,
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
                  AND (delivery.delivery_id IS NULL OR delivery.status = 'ASSIGNED')
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

    private Connection openConnection() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Không tìm thấy ConnectDB.properties.");
            }
            properties.load(inputStream);
        }

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("userID"),
                properties.getProperty("password")
        );
    }

    private void setEmptyData(HttpServletRequest request) {
        request.setAttribute("deliveries", List.of());
        request.setAttribute("totalWaiting", 0);
        request.setAttribute("todayWaiting", 0);
        request.setAttribute("paidOrders", 0);
        request.setAttribute("collectAmount", BigDecimal.ZERO);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class DeliveryRow {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private Timestamp sellerOrderedAt;
        private Timestamp buyerOrderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private int shopId;
        private String shopName;
        private String sellerPhone;
        private String pickupAddress;
        private String customerName;
        private String customerEmail;
        private String productsSummary;
        private int itemCount;
        private int totalQuantity;

        public int getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(int deliveryId) {
            this.deliveryId = deliveryId;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getDeliveryStatus() {
            return deliveryStatus;
        }

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }

        public Timestamp getAssignedAt() {
            return assignedAt;
        }

        public void setAssignedAt(Timestamp assignedAt) {
            this.assignedAt = assignedAt;
        }

        public int getSubOrderId() {
            return subOrderId;
        }

        public void setSubOrderId(int subOrderId) {
            this.subOrderId = subOrderId;
        }

        public int getMasterOrderId() {
            return masterOrderId;
        }

        public void setMasterOrderId(int masterOrderId) {
            this.masterOrderId = masterOrderId;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Timestamp getSellerOrderedAt() {
            return sellerOrderedAt;
        }

        public void setSellerOrderedAt(Timestamp sellerOrderedAt) {
            this.sellerOrderedAt = sellerOrderedAt;
        }

        public Timestamp getBuyerOrderedAt() {
            return buyerOrderedAt;
        }

        public void setBuyerOrderedAt(Timestamp buyerOrderedAt) {
            this.buyerOrderedAt = buyerOrderedAt;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getSellerPhone() {
            return sellerPhone;
        }

        public void setSellerPhone(String sellerPhone) {
            this.sellerPhone = sellerPhone;
        }

        public String getPickupAddress() {
            return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        public String getProductsSummary() {
            return productsSummary;
        }

        public void setProductsSummary(String productsSummary) {
            this.productsSummary = productsSummary;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public BigDecimal getCollectAmount() {
            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                return BigDecimal.ZERO;
            }
            return totalAmount == null ? BigDecimal.ZERO : totalAmount;
        }
    }
}
