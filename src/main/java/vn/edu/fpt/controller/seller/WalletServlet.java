package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@WebServlet(urlPatterns = {"/seller/finance/view-wallet"})
public class WalletServlet extends HttpServlet {

    private static final String WALLET_PAGE = "/seller/finance/view-wallet.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer shopId = resolveSellerShopId(request);
        if (shopId == null) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản seller đã có shop.");
            setEmptyWalletData(request);
            request.getRequestDispatcher(WALLET_PAGE).forward(request, response);
            return;
        }

        String search = trim(request.getParameter("search"));
        String status = trim(request.getParameter("status"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try (Connection connection = openConnection()) {
            loadWalletMetrics(connection, shopId, request);
            request.setAttribute("payoutRequests", loadPayoutRequests(connection, shopId, search, status, dateRange, sort));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu ví seller. Kiểm tra kết nối database.");
            setEmptyWalletData(request);
        }

        request.getRequestDispatcher(WALLET_PAGE).forward(request, response);
    }

    private void loadWalletMetrics(Connection connection, int shopId, HttpServletRequest request) throws Exception {
        String walletSql = """
                SELECT available_balance, pending_balance
                FROM seller_wallets
                WHERE shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(walletSql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("availableBalance", rs.getBigDecimal("available_balance"));
                    request.setAttribute("pendingBalance", rs.getBigDecimal("pending_balance"));
                } else {
                    request.setAttribute("availableBalance", BigDecimal.ZERO);
                    request.setAttribute("pendingBalance", BigDecimal.ZERO);
                }
            }
        }

        String revenueSql = """
                SELECT COALESCE(SUM(total_amount - commission_fee), 0) AS revenue
                FROM sub_orders
                WHERE shop_id = ?
                  AND status = 'DELIVERED'
                """;
        try (PreparedStatement ps = connection.prepareStatement(revenueSql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                request.setAttribute("revenue", rs.next() ? rs.getBigDecimal("revenue") : BigDecimal.ZERO);
            }
        }

        String orderSql = """
                SELECT COUNT(*) AS total_orders,
                       SUM(CASE WHEN status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'SHIPPING') THEN 1 ELSE 0 END) AS urgent_orders
                FROM sub_orders
                WHERE shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(orderSql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("totalOrders", rs.getInt("total_orders"));
                    request.setAttribute("urgentOrders", rs.getInt("urgent_orders"));
                } else {
                    request.setAttribute("totalOrders", 0);
                    request.setAttribute("urgentOrders", 0);
                }
            }
        }

        String ratingSql = """
                SELECT COALESCE(AVG(CAST(pr.rating AS DECIMAL(10,2))), 0) AS average_rating,
                       COUNT(*) AS review_count
                FROM product_reviews pr
                INNER JOIN products p ON pr.product_id = p.product_id
                WHERE p.shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(ratingSql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("averageRating", rs.getBigDecimal("average_rating"));
                    request.setAttribute("reviewCount", rs.getInt("review_count"));
                } else {
                    request.setAttribute("averageRating", BigDecimal.ZERO);
                    request.setAttribute("reviewCount", 0);
                }
            }
        }
    }

    private List<Map<String, Object>> loadPayoutRequests(
            Connection connection,
            int shopId,
            String search,
            String status,
            String dateRange,
            String sort
    ) throws Exception {
        StringBuilder sql = new StringBuilder("""
                SELECT request_id, amount, bank_name, account_holder_name, account_number, status, created_at
                FROM payout_requests
                WHERE shop_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(shopId);

        if (!search.isBlank()) {
            sql.append("""
                    AND (
                        CAST(request_id AS VARCHAR(20)) LIKE ?
                        OR bank_name LIKE ?
                        OR account_holder_name LIKE ?
                        OR account_number LIKE ?
                    )
                    """);
            String keyword = "%" + search + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        if (!status.isBlank()) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        if ("today".equals(dateRange)) {
            sql.append(" AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if ("7days".equals(dateRange)) {
            sql.append(" AND created_at >= DATEADD(DAY, -7, GETDATE()) ");
        } else if ("30days".equals(dateRange)) {
            sql.append(" AND created_at >= DATEADD(DAY, -30, GETDATE()) ");
        }

        if ("amount_desc".equals(sort)) {
            sql.append(" ORDER BY amount DESC, created_at DESC ");
        } else if ("amount_asc".equals(sort)) {
            sql.append(" ORDER BY amount ASC, created_at DESC ");
        } else if ("oldest".equals(sort)) {
            sql.append(" ORDER BY created_at ASC, request_id ASC ");
        } else {
            sql.append(" ORDER BY created_at DESC, request_id DESC ");
        }

        List<Map<String, Object>> requests = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("code", "PO-" + String.format("%04d", rs.getInt("request_id")));
                    row.put("owner", rs.getString("account_holder_name"));
                    row.put("amount", rs.getBigDecimal("amount"));
                    row.put("bank", rs.getString("bank_name"));
                    row.put("status", rs.getString("status"));
                    requests.add(row);
                }
            }
        }
        return requests;
    }

    private void setEmptyWalletData(HttpServletRequest request) {
        request.setAttribute("availableBalance", BigDecimal.ZERO);
        request.setAttribute("pendingBalance", BigDecimal.ZERO);
        request.setAttribute("revenue", BigDecimal.ZERO);
        request.setAttribute("totalOrders", 0);
        request.setAttribute("urgentOrders", 0);
        request.setAttribute("averageRating", BigDecimal.ZERO);
        request.setAttribute("reviewCount", 0);
        request.setAttribute("payoutRequests", List.of());
    }

    private Integer resolveSellerShopId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object account = session.getAttribute("account");
        if (account == null) {
            account = session.getAttribute("user");
        }

        Integer userId = extractUserId(account);
        if (userId == null) {
            return null;
        }

        String sql = "SELECT shop_id FROM shops WHERE owner_id = ?";
        try (Connection connection = openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("shop_id");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Integer extractUserId(Object account) {
        if (account == null) {
            return null;
        }

        try {
            Method getter = account.getClass().getMethod("getUserId");
            Object value = getter.invoke(account);
            if (value instanceof Integer userId) {
                return userId;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
