package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Shop;
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

public class EditSellerStatusServlet extends HttpServlet {

    private static final String STATUS_PAGE = "/seller/order/edit-seller-status.jsp";

    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String successMessage = null;
        if ("1".equals(request.getParameter("labelReady"))) {
            successMessage = "Đã chuyển sang trạng thái đang chuẩn bị hàng. Phiếu vận đơn đã sẵn sàng để in.";
        }
        renderStatusPage(request, response, null, successMessage);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "orders");

        Shop shop = resolveSellerShop(request);
        if (shop == null) {
            renderStatusPage(request, response, "Vui lòng đăng nhập bằng tài khoản người bán đã có cửa hàng.", null);
            return;
        }

        Integer subOrderId = parseSubOrderId(request);
        if (subOrderId == null) {
            renderStatusPage(request, response, "Mã đơn hàng không hợp lệ.", null);
            return;
        }

        String newStatus = trim(request.getParameter("newStatus")).toUpperCase();
        request.setAttribute("selectedStatus", newStatus);

        try (Connection connection = openConnection()) {
            SellerStatusOrder order = loadOrder(connection, shop.getShopId(), subOrderId);
            if (order == null) {
                renderStatusPage(request, response, "Không tìm thấy đơn hàng hoặc đơn hàng không thuộc cửa hàng của bạn.", null);
                return;
            }

            List<StatusOption> nextStatuses = buildNextStatusOptions(order.getStatus());
            if (nextStatuses.isEmpty()) {
                renderLoadedPage(request, response, shop, order, "Trạng thái hiện tại không còn thuộc phần người bán được phép cập nhật.");
                return;
            }

            if (!isAllowedStatus(nextStatuses, newStatus)) {
                renderLoadedPage(request, response, shop, order, "Trạng thái mới không hợp lệ với trạng thái hiện tại của đơn hàng.");
                return;
            }

            boolean updated = updateOrderStatus(connection, order, newStatus);
            if (!updated) {
                renderStatusPage(request, response, "Trạng thái đơn hàng đã thay đổi. Vui lòng tải lại và thử lại.", null);
                return;
            }

            if ("PREPARING".equals(newStatus)) {
                response.sendRedirect(request.getContextPath()
                        + "/seller/order/status?subOrderId=" + subOrderId
                        + "&labelReady=1");
                return;
            }

            response.sendRedirect(request.getContextPath()
                    + "/seller/order/view?subOrderId=" + subOrderId
                    + "&statusUpdated=1");
        } catch (Exception ex) {
            ex.printStackTrace();
            renderStatusPage(request, response, "Không thể cập nhật trạng thái đơn hàng. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", null);
        }
    }

    private void renderStatusPage(HttpServletRequest request, HttpServletResponse response, String errorMessage, String successMessage)
            throws ServletException, IOException {
        request.setAttribute("activePage", "orders");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
        }

        Shop shop = resolveSellerShop(request);
        if (shop == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản người bán đã có cửa hàng.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        Integer subOrderId = parseSubOrderId(request);
        if (subOrderId == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        try (Connection connection = openConnection()) {
            SellerStatusOrder order = loadOrder(connection, shop.getShopId(), subOrderId);
            if (order == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn hàng hoặc đơn hàng không thuộc cửa hàng của bạn.");
            } else {
                renderLoadedPage(request, response, shop, order, errorMessage);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải đơn hàng. Vui lòng kiểm tra kết nối cơ sở dữ liệu.");
        }

        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    private void renderLoadedPage(HttpServletRequest request, HttpServletResponse response, Shop shop, SellerStatusOrder order, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("shop", shop);
        request.setAttribute("orderStatus", order);
        request.setAttribute("nextStatuses", buildNextStatusOptions(order.getStatus()));
        request.setAttribute("statusSteps", buildStatusSteps(order.getStatus()));
        request.setAttribute("lockedMessage", buildLockedMessage(order.getStatus()));
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    private Shop resolveSellerShop(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object accountObject = session.getAttribute("account");
        if (!(accountObject instanceof User)) {
            accountObject = session.getAttribute("user");
        }

        if (!(accountObject instanceof User account) || account.getUserId() == null) {
            return null;
        }

        return shopDAO.getShopByOwnerId(account.getUserId());
    }

    private Integer parseSubOrderId(HttpServletRequest request) {
        String rawValue = trim(request.getParameter("subOrderId"));
        if (rawValue.isBlank()) {
            rawValue = trim(request.getParameter("id"));
        }

        try {
            int value = Integer.parseInt(rawValue);
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private SellerStatusOrder loadOrder(Connection connection, int shopId, int subOrderId) throws Exception {
        String sql = """
                SELECT so.sub_order_id,
                       so.master_order_id,
                       so.shop_id,
                       s.shop_name,
                       owner.phone AS seller_phone,
                       s.street_address + N', ' + w.path_with_type AS pickup_address,
                       mo.created_at AS buyer_ordered_at,
                       so.created_at AS seller_ordered_at,
                       so.status,
                       so.total_amount,
                       so.commission_fee,
                       COALESCE(delivery.tracking_number, CONCAT('MODA-SUB-', so.sub_order_id, '-MO-', so.master_order_id)) AS tracking_number,
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
                       ) AS total_quantity
                FROM sub_orders so
                INNER JOIN shops s ON s.shop_id = so.shop_id
                INNER JOIN users owner ON owner.user_id = s.owner_id
                INNER JOIN wards w ON w.id = s.ward_id
                INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                INNER JOIN users u ON u.user_id = mo.customer_id
                OUTER APPLY (
                    SELECT TOP 1 d.tracking_number
                    FROM deliveries d
                    WHERE d.sub_order_id = so.sub_order_id
                    ORDER BY d.delivery_id DESC
                ) delivery
                WHERE so.sub_order_id = ?
                  AND so.shop_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                SellerStatusOrder order = new SellerStatusOrder();
                order.setSubOrderId(rs.getInt("sub_order_id"));
                order.setMasterOrderId(rs.getInt("master_order_id"));
                order.setShopId(rs.getInt("shop_id"));
                order.setShopName(rs.getString("shop_name"));
                order.setSellerPhone(rs.getString("seller_phone"));
                order.setPickupAddress(rs.getString("pickup_address"));
                order.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at"));
                order.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setCommissionFee(rs.getBigDecimal("commission_fee"));
                order.setTrackingNumber(rs.getString("tracking_number"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setCustomerEmail(rs.getString("customer_email"));
                order.setProductsSummary(rs.getString("products_summary"));
                order.setItemCount(rs.getInt("item_count"));
                order.setTotalQuantity(rs.getInt("total_quantity"));
                return order;
            }
        }
    }

    private boolean updateOrderStatus(Connection connection, SellerStatusOrder order, String newStatus) throws Exception {
        String sql = """
                UPDATE sub_orders
                SET status = ?
                WHERE sub_order_id = ?
                  AND shop_id = ?
                  AND status = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, order.getSubOrderId());
            ps.setInt(3, order.getShopId());
            ps.setString(4, order.getStatus());
            return ps.executeUpdate() == 1;
        }
    }

    private List<StatusOption> buildNextStatusOptions(String currentStatus) {
        List<StatusOption> options = new ArrayList<>();
        String status = trim(currentStatus).toUpperCase();

        if ("PENDING".equals(status)) {
            options.add(new StatusOption("PREPARING", "Đang chuẩn bị hàng", "Cửa hàng bắt đầu đóng gói và chuẩn bị bàn giao đơn."));
        } else if ("CONFIRMED".equals(status)) {
            options.add(new StatusOption("PREPARING", "Đang chuẩn bị hàng", "Cửa hàng bắt đầu đóng gói và chuẩn bị bàn giao đơn."));
        } else if ("PREPARING".equals(status)) {
            options.add(new StatusOption("SHIPPING", "Đã giao cho bên vận chuyển", "Đơn đã được bàn giao cho bộ phận vận chuyển."));
        }

        return options;
    }

    private List<StatusStep> buildStatusSteps(String currentStatus) {
        List<StatusStep> steps = new ArrayList<>();
        String status = trim(currentStatus).toUpperCase();
        int currentIndex = 0;
        if ("PREPARING".equals(status) || "CONFIRMED".equals(status)) {
            currentIndex = 1;
        } else if ("SHIPPING".equals(status) || "DELIVERED".equals(status)) {
            currentIndex = 2;
        } else if ("CANCELLED".equals(status)) {
            currentIndex = -1;
        }

        steps.add(new StatusStep("PENDING", "Chờ xác nhận", currentIndex >= 0));
        steps.add(new StatusStep("PREPARING", "Đang chuẩn bị", currentIndex >= 1));
        steps.add(new StatusStep("SHIPPING", "Đã giao vận chuyển", currentIndex >= 2));
        return steps;
    }

    private String buildLockedMessage(String currentStatus) {
        String status = trim(currentStatus).toUpperCase();
        if ("SHIPPING".equals(status)) {
            return "Đơn hàng đã giao cho bên vận chuyển. Các trạng thái tiếp theo sẽ do bộ phận vận chuyển cập nhật.";
        }
        if ("DELIVERED".equals(status)) {
            return "Đơn hàng đã giao thành công. Trạng thái này do bộ phận vận chuyển hoặc người mua xác nhận.";
        }
        if ("CANCELLED".equals(status)) {
            return "Đơn hàng đã hủy nên người bán không thể chuyển trạng thái.";
        }
        return "";
    }

    private boolean isAllowedStatus(List<StatusOption> options, String newStatus) {
        for (StatusOption option : options) {
            if (option.getValue().equals(newStatus)) {
                return true;
            }
        }
        return false;
    }

    private Connection openConnection() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Khong tim thay ConnectDB.properties.");
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerStatusOrder {
        private int subOrderId;
        private int masterOrderId;
        private int shopId;
        private String shopName;
        private String sellerPhone;
        private String pickupAddress;
        private Timestamp buyerOrderedAt;
        private Timestamp sellerOrderedAt;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal commissionFee;
        private String trackingNumber;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private String customerName;
        private String customerEmail;
        private String productsSummary;
        private int itemCount;
        private int totalQuantity;

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

        public Timestamp getBuyerOrderedAt() {
            return buyerOrderedAt;
        }

        public void setBuyerOrderedAt(Timestamp buyerOrderedAt) {
            this.buyerOrderedAt = buyerOrderedAt;
        }

        public Timestamp getSellerOrderedAt() {
            return sellerOrderedAt;
        }

        public void setSellerOrderedAt(Timestamp sellerOrderedAt) {
            this.sellerOrderedAt = sellerOrderedAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getCommissionFee() {
            return commissionFee;
        }

        public void setCommissionFee(BigDecimal commissionFee) {
            this.commissionFee = commissionFee;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
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

        public BigDecimal getCollectAmount() {
            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                return BigDecimal.ZERO;
            }
            return totalAmount == null ? BigDecimal.ZERO : totalAmount;
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
    }

    public static class StatusOption {
        private final String value;
        private final String label;
        private final String description;

        public StatusOption(String value, String label, String description) {
            this.value = value;
            this.label = label;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class StatusStep {
        private final String value;
        private final String label;
        private final boolean completed;

        public StatusStep(String value, String label, boolean completed) {
            this.value = value;
            this.label = label;
            this.completed = completed;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}
