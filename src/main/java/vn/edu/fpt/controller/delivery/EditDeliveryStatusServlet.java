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

@WebServlet(urlPatterns = {"/logistics/delivery/status", "/logistics/delivery/edit-delivery-status"})
public class EditDeliveryStatusServlet extends HttpServlet {

    private static final String STATUS_PAGE = "/logistics/delivery/edit-delivery-status.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-my-orders");

        if ("1".equals(request.getParameter("updated"))) {
            request.setAttribute("successMessage", "Cập nhật trạng thái giao hàng thành công.");
        }

        renderStatusPage(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-my-orders");

        Integer shipperId = resolveShipperId(request);
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));
        String newStatus = trim(request.getParameter("newStatus")).toUpperCase();
        String note = trim(request.getParameter("note"));

        if (shipperId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (deliveryId == null) {
            renderStatusPage(request, response, "Mã vận đơn không hợp lệ.");
            return;
        }

        if (!"DELIVERED".equals(newStatus)) {
            renderStatusPage(request, response, "Trạng thái mới không hợp lệ.");
            return;
        }

        try (Connection connection = openConnection()) {
            DeliveryStatusDetail detail = loadDeliveryDetail(connection, deliveryId, shipperId);
            if (detail == null) {
                renderStatusPage(request, response, "Không tìm thấy đơn vận chuyển hoặc đơn không thuộc tài khoản giao hàng của bạn.");
                return;
            }

            if (!canMarkDelivered(detail)) {
                renderLoadedPage(request, response, detail,
                        "Chỉ có thể cập nhật đã giao khi người bán đã chuyển đơn sang trạng thái đang giao.");
                return;
            }

            boolean updated = markDelivered(connection, detail, shipperId, note);
            if (!updated) {
                renderStatusPage(request, response, "Trạng thái đơn đã thay đổi. Vui lòng tải lại và thử lại.");
                return;
            }

            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/status?deliveryId=" + deliveryId
                    + "&updated=1");
        } catch (Exception ex) {
            ex.printStackTrace();
            renderStatusPage(request, response, "Không thể cập nhật trạng thái giao hàng. Vui lòng kiểm tra kết nối database.");
        }
    }

    private void renderStatusPage(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("activePage", "delivery-my-orders");

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        Integer shipperId = resolveShipperId(request);
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));

        if (shipperId == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        if (deliveryId == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Mã vận đơn không hợp lệ.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        try (Connection connection = openConnection()) {
            DeliveryStatusDetail detail = loadDeliveryDetail(connection, deliveryId, shipperId);
            if (detail == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn vận chuyển hoặc đơn không thuộc tài khoản giao hàng của bạn.");
            } else {
                renderLoadedPage(request, response, detail, errorMessage);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải đơn vận chuyển. Vui lòng kiểm tra kết nối database.");
        }

        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    private void renderLoadedPage(HttpServletRequest request, HttpServletResponse response,
                                  DeliveryStatusDetail detail, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("deliveryStatus", detail);
        request.setAttribute("statusSteps", buildStatusSteps(detail));
        request.setAttribute("canMarkDelivered", canMarkDelivered(detail));
        request.setAttribute("lockedMessage", buildLockedMessage(detail));
        request.setAttribute("logs", detail.getLogs());
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    private DeliveryStatusDetail loadDeliveryDetail(Connection connection, int deliveryId, int shipperId) throws Exception {
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
                detail.setLogs(loadLogs(connection, deliveryId));
                return detail;
            }
        }
    }

    private List<DeliveryLogRow> loadLogs(Connection connection, int deliveryId) throws Exception {
        String sql = """
                SELECT new_status, current_location, created_at
                FROM delivery_logs
                WHERE delivery_id = ?
                ORDER BY created_at DESC, log_id DESC
                """;

        List<DeliveryLogRow> logs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, deliveryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DeliveryLogRow row = new DeliveryLogRow();
                    row.setNewStatus(rs.getString("new_status"));
                    row.setCurrentLocation(rs.getString("current_location"));
                    row.setCreatedAt(rs.getTimestamp("created_at"));
                    logs.add(row);
                }
            }
        }
        return logs;
    }

    private boolean markDelivered(Connection connection, DeliveryStatusDetail detail, int shipperId, String note) throws Exception {
        boolean oldAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            ensureDeliveredAtColumn(connection);

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

            String logSql = """
                    INSERT INTO delivery_logs (delivery_id, shipper_id, new_status, current_location)
                    VALUES (?, ?, 'DELIVERED', ?)
                    """;
            try (PreparedStatement ps = connection.prepareStatement(logSql)) {
                ps.setInt(1, detail.getDeliveryId());
                ps.setInt(2, shipperId);
                ps.setString(3, note.isBlank()
                        ? "Đã giao hàng cho người nhận tại " + detail.getShippingAddress()
                        : note);
                ps.executeUpdate();
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

    private void ensureDeliveredAtColumn(Connection connection) throws Exception {
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

    private boolean canMarkDelivered(DeliveryStatusDetail detail) {
        if (detail == null) {
            return false;
        }
        boolean orderIsShipping = "SHIPPING".equals(detail.getOrderStatus());
        boolean deliveryCanClose = "ASSIGNED".equals(detail.getDeliveryStatus())
                || "PICKED_UP".equals(detail.getDeliveryStatus())
                || "IN_TRANSIT".equals(detail.getDeliveryStatus());
        return orderIsShipping && deliveryCanClose;
    }

    private String buildLockedMessage(DeliveryStatusDetail detail) {
        if (detail == null) {
            return "";
        }
        if ("DELIVERED".equals(detail.getDeliveryStatus()) || "DELIVERED".equals(detail.getOrderStatus())) {
            return "Đơn vận chuyển này đã hoàn tất giao hàng.";
        }
        if ("FAILED".equals(detail.getDeliveryStatus())) {
            return "Đơn vận chuyển này đang ở trạng thái giao thất bại.";
        }
        if (!"SHIPPING".equals(detail.getOrderStatus())) {
            return "Người bán chưa chuyển đơn sang trạng thái đang giao, nên chưa thể xác nhận đã giao cho người mua.";
        }
        return "";
    }

    private List<StatusStep> buildStatusSteps(DeliveryStatusDetail detail) {
        String deliveryStatus = detail.getDeliveryStatus();
        String orderStatus = detail.getOrderStatus();
        List<StatusStep> steps = new ArrayList<>();
        steps.add(new StatusStep("Đã nhận đơn", "ASSIGNED".equals(deliveryStatus)
                || "PICKED_UP".equals(deliveryStatus)
                || "IN_TRANSIT".equals(deliveryStatus)
                || "DELIVERED".equals(deliveryStatus),
                "Shipper đã nhận đơn từ danh sách chung."));
        steps.add(new StatusStep("Người bán bàn giao", "SHIPPING".equals(orderStatus)
                || "DELIVERED".equals(orderStatus),
                "Người bán đã chuyển đơn sang trạng thái đang giao."));
        steps.add(new StatusStep("Đã giao cho người mua", "DELIVERED".equals(deliveryStatus)
                || "DELIVERED".equals(orderStatus),
                "Người mua đã nhận hàng, đơn vận chuyển hoàn tất."));
        return steps;
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

    private Integer parsePositiveInt(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        try {
            int value = Integer.parseInt(rawValue.trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static class DeliveryStatusDetail {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private BigDecimal collectAmount;
        private Timestamp orderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private String shopName;
        private String sellerPhone;
        private String pickupAddress;
        private String productsSummary;
        private int totalQuantity;
        private List<DeliveryLogRow> logs = List.of();

        public int getDeliveryId() { return deliveryId; }
        public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public Timestamp getAssignedAt() { return assignedAt; }
        public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
        public int getSubOrderId() { return subOrderId; }
        public void setSubOrderId(int subOrderId) { this.subOrderId = subOrderId; }
        public int getMasterOrderId() { return masterOrderId; }
        public void setMasterOrderId(int masterOrderId) { this.masterOrderId = masterOrderId; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getCollectAmount() { return collectAmount; }
        public void setCollectAmount(BigDecimal collectAmount) { this.collectAmount = collectAmount; }
        public Timestamp getOrderedAt() { return orderedAt; }
        public void setOrderedAt(Timestamp orderedAt) { this.orderedAt = orderedAt; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
        public String getProductsSummary() { return productsSummary; }
        public void setProductsSummary(String productsSummary) { this.productsSummary = productsSummary; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public List<DeliveryLogRow> getLogs() { return logs; }
        public void setLogs(List<DeliveryLogRow> logs) { this.logs = logs; }
    }

    public static class DeliveryLogRow {
        private String newStatus;
        private String currentLocation;
        private Timestamp createdAt;

        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }

    public static class StatusStep {
        private final String title;
        private final boolean completed;
        private final String description;

        public StatusStep(String title, boolean completed, String description) {
            this.title = title;
            this.completed = completed;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getDescription() {
            return description;
        }
    }
}
