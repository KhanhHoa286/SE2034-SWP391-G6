package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.dao.ProductDAO;
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

@WebServlet(urlPatterns = {"/seller/orders", "/list-seller-orders"})
public class ListSellerOrdersServlet extends HttpServlet {

    private static final String ORDERS_PAGE = "/seller/order/list-seller-orders.jsp";

    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "orders");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = getLoggedInUserId(session);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        if (!customerDAO.hasCompletedSellerIdentity(userId)) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
        }

        Shop shop = shopDAO.getShopByOwnerId(userId);
        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/add-shop");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        int totalProducts = productDAO.countSellerProducts(shop.getShopId(), "", null, null);
        if (totalProducts == 0) {
            session.setAttribute("toastMessage", "Bạn cần tạo ít nhất một sản phẩm để quản lý đơn hàng.");
            session.setAttribute("toastType", "error");
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        String search = trim(request.getParameter("search"));
        String status = trim(request.getParameter("status"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("shop", shop);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try (Connection connection = openConnection()) {
            loadOrderMetrics(connection, request, shop.getShopId());
            List<SellerOrderRow> sellerOrders = loadSellerOrders(connection, shop.getShopId(), search, status, dateRange, sort);
            request.setAttribute("sellerOrders", sellerOrders);
            prepareAssignedDeliveryToast(request, sellerOrders);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách đơn hàng. Vui lòng kiểm tra kết nối database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(ORDERS_PAGE).forward(request, response);
    }

    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        Object rawAccount = session.getAttribute("account");
        if (rawAccount instanceof User) {
            return ((User) rawAccount).getUserId();
        }

        return null;
    }

    private void loadOrderMetrics(Connection connection, HttpServletRequest request, int shopId) throws Exception {
        String sql = """
                SELECT COUNT(*) AS total_orders,
                       COALESCE(SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END), 0) AS pending_orders,
                       COALESCE(SUM(CASE WHEN status IN ('CONFIRMED', 'PREPARING', 'SHIPPING') THEN 1 ELSE 0 END), 0) AS processing_orders,
                       COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END), 0) AS delivered_orders,
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

    private List<SellerOrderRow> loadSellerOrders(
            Connection connection,
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
                      AND d.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED')
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

    private void prepareAssignedDeliveryToast(HttpServletRequest request, List<SellerOrderRow> orders) {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.TRUE.equals(session.getAttribute("sellerAssignedDeliveryToastShown"))) {
            return;
        }

        for (SellerOrderRow order : orders) {
            if (order.isShipperAssigned() && "PREPARING".equalsIgnoreCase(order.getStatus())) {
                request.setAttribute("assignedDeliveryToastMessage",
                        "#SUB-" + order.getSubOrderId() + " đã được nhận giao");
                session.setAttribute("sellerAssignedDeliveryToastShown", true);
                return;
            }
        }
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

    private void setEmptyData(HttpServletRequest request) {
        request.setAttribute("sellerOrders", List.of());
        request.setAttribute("totalOrders", 0);
        request.setAttribute("pendingOrders", 0);
        request.setAttribute("processingOrders", 0);
        request.setAttribute("deliveredOrders", 0);
        request.setAttribute("grossAmount", BigDecimal.ZERO);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerOrderRow {
        private int subOrderId;
        private int masterOrderId;
        private Timestamp buyerOrderedAt;
        private Timestamp sellerOrderedAt;
        private String status;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal commissionFee;
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
        private boolean shipperAssigned;
        private String shipperName;
        private String shipperPhone;

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

        public BigDecimal getSubTotal() {
            return subTotal;
        }

        public void setSubTotal(BigDecimal subTotal) {
            this.subTotal = subTotal;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
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

        public boolean isShipperAssigned() {
            return shipperAssigned;
        }

        public void setShipperAssigned(boolean shipperAssigned) {
            this.shipperAssigned = shipperAssigned;
        }

        public String getShipperName() {
            return shipperName;
        }

        public void setShipperName(String shipperName) {
            this.shipperName = shipperName;
        }

        public String getShipperPhone() {
            return shipperPhone;
        }

        public void setShipperPhone(String shipperPhone) {
            this.shipperPhone = shipperPhone;
        }
    }
}
