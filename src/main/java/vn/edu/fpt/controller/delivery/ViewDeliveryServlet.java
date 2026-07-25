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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = {"/logistics/delivery/view", "/logistics/delivery/view-delivery"})
public class ViewDeliveryServlet extends HttpServlet {

    private static final String VIEW_PAGE = "/logistics/delivery/view-delivery.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-list");

        String receiveError = request.getParameter("receiveError");
        if ("taken".equals(receiveError)) {
            request.setAttribute("errorMessage", "Đơn này đã được shipper khác nhận. Vui lòng chọn đơn khác.");
        } else if ("system".equals(receiveError)) {
            request.setAttribute("errorMessage", "Không thể nhận đơn hàng. Vui lòng thử lại sau.");
        }

        Integer shipperId = resolveShipperId(request);
        if (shipperId == null) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
            return;
        }

        Integer subOrderId = parsePositiveInt(request.getParameter("subOrderId"));
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));

        if (subOrderId == null && deliveryId == null) {
            request.setAttribute("errorMessage", "Không tìm thấy mã đơn giao hàng.");
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
            return;
        }

        try (Connection connection = openConnection()) {
            DeliveryDetail detail = loadDeliveryDetail(connection, subOrderId, deliveryId, shipperId);
            if (detail == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn giao hàng hoặc đơn đã được shipper khác nhận.");
            } else {
                detail.setItems(loadDeliveryItems(connection, detail.getSubOrderId()));
                request.setAttribute("deliveryDetail", detail);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải chi tiết đơn giao hàng. Vui lòng kiểm tra kết nối database.");
        }

        request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer shipperId = resolveShipperId(request);
        Integer subOrderId = parsePositiveInt(request.getParameter("subOrderId"));

        if (shipperId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (subOrderId == null) {
            response.sendRedirect(request.getContextPath() + "/logistics/delivery/list?receiveError=invalid");
            return;
        }

        try (Connection connection = openConnection()) {
            int deliveryId = receiveDelivery(connection, subOrderId, shipperId);
            if (deliveryId <= 0) {
                response.sendRedirect(request.getContextPath()
                        + "/logistics/delivery/view?subOrderId=" + subOrderId
                        + "&receiveError=taken");
                return;
            }

            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/my-orders?received=1&deliveryId=" + deliveryId);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/view?subOrderId=" + subOrderId
                    + "&receiveError=system");
        }
    }

    private int receiveDelivery(Connection connection, int subOrderId, int shipperId) throws Exception {
        boolean oldAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            DeliveryIdentity identity = loadReceivableOrder(connection, subOrderId);
            if (identity == null) {
                connection.rollback();
                return 0;
            }

            if (hasDelivery(connection, subOrderId)) {
                connection.rollback();
                return 0;
            }

            int deliveryId = insertDelivery(connection, identity, shipperId);
            insertDeliveryLog(connection, deliveryId, shipperId, identity.pickupAddress);

            connection.commit();
            return deliveryId;
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private DeliveryIdentity loadReceivableOrder(Connection connection, int subOrderId) throws Exception {
        String sql = """
                SELECT so.sub_order_id,
                       so.master_order_id,
                       s.street_address + N', ' + w.path_with_type AS pickup_address
                FROM sub_orders so WITH (UPDLOCK, HOLDLOCK)
                INNER JOIN shops s ON s.shop_id = so.shop_id
                INNER JOIN wards w ON w.id = s.ward_id
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
                        rs.getInt("master_order_id"),
                        rs.getString("pickup_address")
                );
            }
        }
    }

    private boolean hasDelivery(Connection connection, int subOrderId) throws Exception {
        String sql = "SELECT 1 FROM deliveries WITH (UPDLOCK, HOLDLOCK) WHERE sub_order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int insertDelivery(Connection connection, DeliveryIdentity identity, int shipperId) throws Exception {
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

        return loadDeliveryId(connection, identity.subOrderId, shipperId);
    }

    private int loadDeliveryId(Connection connection, int subOrderId, int shipperId) throws Exception {
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

    private void insertDeliveryLog(Connection connection, int deliveryId, int shipperId, String pickupAddress) throws Exception {
        String sql = """
                INSERT INTO delivery_logs (delivery_id, shipper_id, new_status, current_location)
                VALUES (?, ?, 'ASSIGNED', ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, deliveryId);
            ps.setInt(2, shipperId);
            ps.setString(3, pickupAddress == null || pickupAddress.isBlank()
                    ? "Shipper đã nhận đơn"
                    : pickupAddress);
            ps.executeUpdate();
        }
    }

    private DeliveryDetail loadDeliveryDetail(
            Connection connection,
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

    private List<DeliveryItem> loadDeliveryItems(Connection connection, int subOrderId) throws Exception {
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

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String buildTrackingNumber(int subOrderId, int masterOrderId) {
        return "MODA-SUB-" + subOrderId + "-MO-" + masterOrderId;
    }

    private record DeliveryIdentity(int subOrderId, int masterOrderId, String pickupAddress) {
    }

    public static class DeliveryDetail {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private Integer shipperId;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal collectAmount;
        private Timestamp preparedAt;
        private Timestamp orderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private int shopId;
        private String shopName;
        private String pickupAddress;
        private String sellerName;
        private String sellerPhone;
        private String sellerEmail;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private List<DeliveryItem> items = List.of();

        public int getDeliveryId() { return deliveryId; }
        public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public Timestamp getAssignedAt() { return assignedAt; }
        public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
        public Integer getShipperId() { return shipperId; }
        public void setShipperId(Integer shipperId) { this.shipperId = shipperId; }
        public int getSubOrderId() { return subOrderId; }
        public void setSubOrderId(int subOrderId) { this.subOrderId = subOrderId; }
        public int getMasterOrderId() { return masterOrderId; }
        public void setMasterOrderId(int masterOrderId) { this.masterOrderId = masterOrderId; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public BigDecimal getSubTotal() { return subTotal; }
        public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getCollectAmount() { return collectAmount; }
        public void setCollectAmount(BigDecimal collectAmount) { this.collectAmount = collectAmount; }
        public Timestamp getPreparedAt() { return preparedAt; }
        public void setPreparedAt(Timestamp preparedAt) { this.preparedAt = preparedAt; }
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
        public int getShopId() { return shopId; }
        public void setShopId(int shopId) { this.shopId = shopId; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
        public String getSellerEmail() { return sellerEmail; }
        public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        public List<DeliveryItem> getItems() { return items; }
        public void setItems(List<DeliveryItem> items) { this.items = items; }
    }

    public static class DeliveryItem {
        private int orderItemId;
        private int productId;
        private Integer variantId;
        private int quantity;
        private BigDecimal priceAtPurchase;
        private BigDecimal lineTotal;
        private String productName;
        private String thumbnailUrl;
        private String variantName;
        private String colorName;
        private String sizeName;

        public int getOrderItemId() { return orderItemId; }
        public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
        public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getVariantName() { return variantName; }
        public void setVariantName(String variantName) { this.variantName = variantName; }
        public String getColorName() { return colorName; }
        public void setColorName(String colorName) { this.colorName = colorName; }
        public String getSizeName() { return sizeName; }
        public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    }
}
