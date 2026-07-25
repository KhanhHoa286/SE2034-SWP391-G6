package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = {"/seller/customers", "/list-customers"})
public class ListCustomersServlet extends HttpServlet {

    private static final String CUSTOMERS_PAGE = "/seller/customer_mgt/list-customers.jsp";

    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "customers");

        Shop shop = resolveSellerShop(request);
        if (shop == null) {
            request.setAttribute("errorMessage", "Vui long dang nhap bang tai khoan seller da co shop.");
            setEmptyData(request);
            request.getRequestDispatcher(CUSTOMERS_PAGE).forward(request, response);
            return;
        }

        String search = trim(request.getParameter("search"));
        String segment = trim(request.getParameter("segment"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("shop", shop);
        request.setAttribute("search", search);
        request.setAttribute("segment", segment);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try (Connection connection = openConnection()) {
            loadCustomerMetrics(connection, request, shop.getShopId());
            request.setAttribute("customers", loadCustomers(connection, shop.getShopId(), search, segment, dateRange, sort));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Khong the tai danh sach khach hang. Vui long kiem tra ket noi database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(CUSTOMERS_PAGE).forward(request, response);
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

    private void loadCustomerMetrics(Connection connection, HttpServletRequest request, int shopId) throws Exception {
        String sql = """
                WITH customer_stats AS (
                    SELECT mo.customer_id,
                           COUNT(*) AS total_orders,
                           SUM(CASE WHEN so.status <> 'CANCELLED' THEN so.total_amount ELSE 0 END) AS total_spent
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    WHERE so.shop_id = ?
                    GROUP BY mo.customer_id
                )
                SELECT COUNT(*) AS total_customers,
                       COALESCE(SUM(total_spent), 0) AS total_revenue,
                       COALESCE(SUM(CASE WHEN total_orders > 1 THEN 1 ELSE 0 END), 0) AS returning_customers
                FROM customer_stats
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("totalCustomers", rs.getInt("total_customers"));
                    request.setAttribute("totalCustomerRevenue", rs.getBigDecimal("total_revenue"));
                    request.setAttribute("returningCustomers", rs.getInt("returning_customers"));
                    return;
                }
            }
        }

        request.setAttribute("totalCustomers", 0);
        request.setAttribute("totalCustomerRevenue", BigDecimal.ZERO);
        request.setAttribute("returningCustomers", 0);
    }

    private List<SellerCustomerRow> loadCustomers(
            Connection connection,
            int shopId,
            String search,
            String segment,
            String dateRange,
            String sort
    ) throws Exception {
        StringBuilder sql = new StringBuilder("""
                WITH customer_stats AS (
                    SELECT mo.customer_id,
                           COUNT(*) AS total_orders,
                           SUM(CASE WHEN so.status <> 'CANCELLED' THEN so.total_amount ELSE 0 END) AS total_spent,
                           SUM(CASE WHEN so.status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'SHIPPING') THEN 1 ELSE 0 END) AS active_orders,
                           SUM(CASE WHEN so.status = 'DELIVERED' THEN 1 ELSE 0 END) AS delivered_orders,
                           SUM(CASE WHEN so.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_orders,
                           MIN(mo.created_at) AS first_order_at,
                           MAX(mo.created_at) AS last_order_at,
                           COUNT(DISTINCT oi.product_id) AS purchased_products,
                           COALESCE(SUM(oi.quantity), 0) AS total_quantity
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    LEFT JOIN order_items oi ON oi.sub_order_id = so.sub_order_id
                    WHERE so.shop_id = ?
                    GROUP BY mo.customer_id
                )
                SELECT u.user_id,
                       u.first_name + ' ' + u.last_name AS customer_name,
                       u.email,
                       u.phone,
                       u.avatar_url,
                       u.status AS account_status,
                       cs.total_orders,
                       cs.total_spent,
                       cs.active_orders,
                       cs.delivered_orders,
                       cs.cancelled_orders,
                       cs.first_order_at,
                       cs.last_order_at,
                       cs.purchased_products,
                       cs.total_quantity,
                       last_order.sub_order_id AS last_sub_order_id,
                       last_order.status AS last_order_status,
                       last_order.total_amount AS last_order_amount
                FROM customer_stats cs
                INNER JOIN users u ON u.user_id = cs.customer_id
                OUTER APPLY (
                    SELECT TOP 1 so.sub_order_id, so.status, so.total_amount, mo.created_at
                    FROM sub_orders so
                    INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                    WHERE so.shop_id = ?
                      AND mo.customer_id = cs.customer_id
                    ORDER BY mo.created_at DESC, so.sub_order_id DESC
                ) last_order
                WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        params.add(shopId);
        params.add(shopId);

        if (!search.isBlank()) {
            sql.append("""
                    AND (
                        CAST(u.user_id AS VARCHAR(20)) LIKE ?
                        OR u.first_name + ' ' + u.last_name LIKE ?
                        OR u.email LIKE ?
                        OR u.phone LIKE ?
                    )
                    """);
            String keyword = "%" + search + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        if ("active".equals(segment)) {
            sql.append(" AND cs.active_orders > 0 ");
        } else if ("returning".equals(segment)) {
            sql.append(" AND cs.total_orders > 1 ");
        } else if ("completed".equals(segment)) {
            sql.append(" AND cs.delivered_orders > 0 ");
        } else if ("cancelled".equals(segment)) {
            sql.append(" AND cs.cancelled_orders > 0 ");
        }

        if ("today".equals(dateRange)) {
            sql.append(" AND CAST(cs.last_order_at AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if ("7days".equals(dateRange)) {
            sql.append(" AND cs.last_order_at >= DATEADD(DAY, -7, GETDATE()) ");
        } else if ("30days".equals(dateRange)) {
            sql.append(" AND cs.last_order_at >= DATEADD(DAY, -30, GETDATE()) ");
        }

        if ("spend_desc".equals(sort)) {
            sql.append(" ORDER BY cs.total_spent DESC, cs.last_order_at DESC ");
        } else if ("orders_desc".equals(sort)) {
            sql.append(" ORDER BY cs.total_orders DESC, cs.last_order_at DESC ");
        } else if ("name_asc".equals(sort)) {
            sql.append(" ORDER BY customer_name ASC ");
        } else if ("oldest".equals(sort)) {
            sql.append(" ORDER BY cs.last_order_at ASC, u.user_id ASC ");
        } else {
            sql.append(" ORDER BY cs.last_order_at DESC, u.user_id DESC ");
        }

        List<SellerCustomerRow> customers = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerCustomerRow row = new SellerCustomerRow();
                    row.setCustomerId(rs.getInt("user_id"));
                    row.setCustomerName(rs.getString("customer_name"));
                    row.setEmail(rs.getString("email"));
                    row.setPhone(rs.getString("phone"));
                    row.setAvatarUrl(rs.getString("avatar_url"));
                    row.setAccountStatus(rs.getString("account_status"));
                    row.setTotalOrders(rs.getInt("total_orders"));
                    row.setTotalSpent(rs.getBigDecimal("total_spent"));
                    row.setActiveOrders(rs.getInt("active_orders"));
                    row.setDeliveredOrders(rs.getInt("delivered_orders"));
                    row.setCancelledOrders(rs.getInt("cancelled_orders"));
                    row.setFirstOrderAt(rs.getTimestamp("first_order_at"));
                    row.setLastOrderAt(rs.getTimestamp("last_order_at"));
                    row.setPurchasedProducts(rs.getInt("purchased_products"));
                    row.setTotalQuantity(rs.getInt("total_quantity"));
                    row.setLastSubOrderId((Integer) rs.getObject("last_sub_order_id"));
                    row.setLastOrderStatus(rs.getString("last_order_status"));
                    row.setLastOrderAmount(rs.getBigDecimal("last_order_amount"));
                    row.setAverageOrderValue(calculateAverage(row.getTotalSpent(), row.getTotalOrders()));
                    customers.add(row);
                }
            }
        }
        return customers;
    }

    private BigDecimal calculateAverage(BigDecimal totalSpent, int totalOrders) {
        if (totalSpent == null || totalOrders <= 0) {
            return BigDecimal.ZERO;
        }
        return totalSpent.divide(BigDecimal.valueOf(totalOrders), 0, RoundingMode.HALF_UP);
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
        request.setAttribute("customers", List.of());
        request.setAttribute("totalCustomers", 0);
        request.setAttribute("totalCustomerRevenue", BigDecimal.ZERO);
        request.setAttribute("returningCustomers", 0);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerCustomerRow {
        private int customerId;
        private String customerName;
        private String email;
        private String phone;
        private String avatarUrl;
        private String accountStatus;
        private int totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private int activeOrders;
        private int deliveredOrders;
        private int cancelledOrders;
        private Timestamp firstOrderAt;
        private Timestamp lastOrderAt;
        private int purchasedProducts;
        private int totalQuantity;
        private Integer lastSubOrderId;
        private String lastOrderStatus;
        private BigDecimal lastOrderAmount;

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public BigDecimal getTotalSpent() {
            return totalSpent;
        }

        public void setTotalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
        }

        public BigDecimal getAverageOrderValue() {
            return averageOrderValue;
        }

        public void setAverageOrderValue(BigDecimal averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
        }

        public int getActiveOrders() {
            return activeOrders;
        }

        public void setActiveOrders(int activeOrders) {
            this.activeOrders = activeOrders;
        }

        public int getDeliveredOrders() {
            return deliveredOrders;
        }

        public void setDeliveredOrders(int deliveredOrders) {
            this.deliveredOrders = deliveredOrders;
        }

        public int getCancelledOrders() {
            return cancelledOrders;
        }

        public void setCancelledOrders(int cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
        }

        public Timestamp getFirstOrderAt() {
            return firstOrderAt;
        }

        public void setFirstOrderAt(Timestamp firstOrderAt) {
            this.firstOrderAt = firstOrderAt;
        }

        public Timestamp getLastOrderAt() {
            return lastOrderAt;
        }

        public void setLastOrderAt(Timestamp lastOrderAt) {
            this.lastOrderAt = lastOrderAt;
        }

        public int getPurchasedProducts() {
            return purchasedProducts;
        }

        public void setPurchasedProducts(int purchasedProducts) {
            this.purchasedProducts = purchasedProducts;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public Integer getLastSubOrderId() {
            return lastSubOrderId;
        }

        public void setLastSubOrderId(Integer lastSubOrderId) {
            this.lastSubOrderId = lastSubOrderId;
        }

        public String getLastOrderStatus() {
            return lastOrderStatus;
        }

        public void setLastOrderStatus(String lastOrderStatus) {
            this.lastOrderStatus = lastOrderStatus;
        }

        public BigDecimal getLastOrderAmount() {
            return lastOrderAmount;
        }

        public void setLastOrderAmount(BigDecimal lastOrderAmount) {
            this.lastOrderAmount = lastOrderAmount;
        }
    }
}
