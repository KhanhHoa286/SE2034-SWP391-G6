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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@WebServlet(urlPatterns = {"/seller/finance/add-payout-request"})
public class AddPayoutRequestServlet extends HttpServlet {

    private static final String REQUEST_PAGE = "/seller/finance/add-payout-request.jsp";
    private static final BigDecimal MIN_PAYOUT_AMOUNT = new BigDecimal("10000");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        preparePage(request);
        request.getRequestDispatcher(REQUEST_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Map<String, String> errors = new HashMap<>();
        Map<String, String> oldInput = new HashMap<>();

        String payoutAccountIdRaw = trim(request.getParameter("payoutAccountId"));
        String amountRaw = trim(request.getParameter("amount"));
        String withdrawalNote = trim(request.getParameter("withdrawalNote"));
        String confirmRaw = trim(request.getParameter("confirm"));
        boolean confirm = "on".equals(confirmRaw) || "true".equals(confirmRaw);

        oldInput.put("payoutAccountId", payoutAccountIdRaw);
        oldInput.put("amount", amountRaw);
        oldInput.put("withdrawalNote", withdrawalNote);
        request.setAttribute("oldInput", oldInput);

        Integer shopId = resolveSellerShopId(request);
        if (shopId == null) {
            errors.put("system", "Vui lòng đăng nhập bằng tài khoản seller đã có shop.");
        }

        Integer payoutAccountId = parseInteger(payoutAccountIdRaw, "payoutAccountId", "Vui lòng chọn tài khoản nhận tiền.", errors);
        BigDecimal amount = parseAmount(amountRaw, errors);

        if (!confirm) {
            errors.put("confirm", "Bạn phải xác nhận yêu cầu rút tiền trước khi gửi.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Vui lòng kiểm tra lại thông tin yêu cầu rút tiền.");
            preparePage(request);
            request.getRequestDispatcher(REQUEST_PAGE).forward(request, response);
            return;
        }

        try {
            createPayoutRequest(shopId, payoutAccountId, amount, withdrawalNote);
            request.setAttribute("popupType", "success");
            request.setAttribute("popupMessage", "Đã gửi yêu cầu rút tiền. Số tiền đã được chuyển sang trạng thái chờ xử lý.");
            request.removeAttribute("oldInput");
        } catch (IllegalArgumentException ex) {
            errors.put("amount", ex.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errors", errors);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Không thể gửi yêu cầu rút tiền lúc này.");
        }

        preparePage(request);
        request.getRequestDispatcher(REQUEST_PAGE).forward(request, response);
    }

    private void preparePage(HttpServletRequest request) {
        Integer shopId = resolveSellerShopId(request);
        if (shopId == null) {
            request.setAttribute("availableBalance", BigDecimal.ZERO);
            request.setAttribute("pendingBalance", BigDecimal.ZERO);
            request.setAttribute("payoutAccounts", List.of());
            if (request.getAttribute("popupMessage") == null) {
                request.setAttribute("popupType", "error");
                request.setAttribute("popupMessage", "Vui lòng đăng nhập bằng tài khoản seller đã có shop.");
            }
            return;
        }

        try (Connection connection = openConnection()) {
            ensurePayoutAccountTable(connection);
            loadWallet(connection, shopId, request);
            request.setAttribute("payoutAccounts", loadPayoutAccounts(connection, shopId));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("availableBalance", BigDecimal.ZERO);
            request.setAttribute("pendingBalance", BigDecimal.ZERO);
            request.setAttribute("payoutAccounts", List.of());
            if (request.getAttribute("popupMessage") == null) {
                request.setAttribute("popupType", "error");
                request.setAttribute("popupMessage", "Không thể tải dữ liệu rút tiền.");
            }
        }
    }

    private void createPayoutRequest(int shopId, int payoutAccountId, BigDecimal amount, String withdrawalNote) throws Exception {
        Connection connection = null;
        try {
            connection = openConnection();
            connection.setAutoCommit(false);
            ensurePayoutAccountTable(connection);

            PayoutAccount account = findPayoutAccount(connection, shopId, payoutAccountId);
            if (account == null) {
                throw new IllegalArgumentException("Tài khoản nhận tiền không hợp lệ.");
            }

            BigDecimal availableBalance = getLockedAvailableBalance(connection, shopId);
            if (availableBalance == null) {
                throw new IllegalArgumentException("Shop chưa có ví seller.");
            }
            if (amount.compareTo(availableBalance) > 0) {
                throw new IllegalArgumentException("Số tiền rút không được lớn hơn số dư khả dụng.");
            }

            String updateWalletSql = """
                    UPDATE seller_wallets
                    SET available_balance = available_balance - ?,
                        pending_balance = pending_balance + ?,
                        updated_at = GETDATE()
                    WHERE shop_id = ?
                      AND available_balance >= ?
                    """;
            try (PreparedStatement ps = connection.prepareStatement(updateWalletSql)) {
                ps.setBigDecimal(1, amount);
                ps.setBigDecimal(2, amount);
                ps.setInt(3, shopId);
                ps.setBigDecimal(4, amount);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalArgumentException("Số dư khả dụng không đủ để rút.");
                }
            }

            insertPayoutRequest(connection, shopId, account, amount, withdrawalNote);

            connection.commit();
        } catch (Exception ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw ex;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void insertPayoutRequest(
            Connection connection,
            int shopId,
            PayoutAccount account,
            BigDecimal amount,
            String withdrawalNote
    ) throws SQLException {
        boolean hasWithdrawalNote = hasColumn(connection, "payout_requests", "withdrawal_note");
        String insertSql = hasWithdrawalNote
                ? """
                INSERT INTO payout_requests
                (shop_id, bank_code, amount, bank_name, account_holder_name, account_number, status, withdrawal_note)
                VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?)
                """
                : """
                INSERT INTO payout_requests
                (shop_id, bank_code, amount, bank_name, account_holder_name, account_number, status)
                VALUES (?, ?, ?, ?, ?, ?, 'PENDING')
                """;
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setInt(1, shopId);
            ps.setString(2, bankCodeOf(account.bankName()));
            ps.setBigDecimal(3, amount);
            ps.setString(4, account.bankName());
            ps.setString(5, account.accountHolderName());
            ps.setString(6, account.accountNumber());
            if (hasWithdrawalNote) {
                ps.setString(7, withdrawalNote == null || withdrawalNote.isBlank() ? null : withdrawalNote);
            }
            ps.executeUpdate();
        }
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT 1
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void ensurePayoutAccountTable(Connection connection) throws SQLException {
        String sql = """
                IF OBJECT_ID('seller_payout_accounts', 'U') IS NULL
                BEGIN
                    CREATE TABLE seller_payout_accounts (
                        account_id INT IDENTITY(1,1) PRIMARY KEY,
                        shop_id INT NOT NULL REFERENCES shops(shop_id),
                        bank_name NVARCHAR(100) NOT NULL,
                        account_number VARCHAR(50) NOT NULL,
                        account_holder_name NVARCHAR(100) NOT NULL,
                        is_default BIT NOT NULL DEFAULT 0,
                        created_at DATETIME DEFAULT GETDATE(),
                        updated_at DATETIME DEFAULT GETDATE()
                    )
                END
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.execute();
        }
    }

    private void loadWallet(Connection connection, int shopId, HttpServletRequest request) throws SQLException {
        String sql = """
                SELECT available_balance, pending_balance
                FROM seller_wallets
                WHERE shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
    }

    private List<Map<String, Object>> loadPayoutAccounts(Connection connection, int shopId) throws SQLException {
        String sql = """
                SELECT account_id, bank_name, account_number, account_holder_name, is_default
                FROM seller_payout_accounts
                WHERE shop_id = ?
                ORDER BY is_default DESC, updated_at DESC, account_id DESC
                """;
        List<Map<String, Object>> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("accountId", rs.getInt("account_id"));
                    row.put("bankName", rs.getString("bank_name"));
                    row.put("accountNumber", rs.getString("account_number"));
                    row.put("accountHolderName", rs.getString("account_holder_name"));
                    row.put("isDefault", rs.getBoolean("is_default"));
                    accounts.add(row);
                }
            }
        }
        return accounts;
    }

    private PayoutAccount findPayoutAccount(Connection connection, int shopId, int accountId) throws SQLException {
        String sql = """
                SELECT account_id, bank_name, account_number, account_holder_name
                FROM seller_payout_accounts
                WHERE shop_id = ?
                  AND account_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            ps.setInt(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PayoutAccount(
                            rs.getInt("account_id"),
                            rs.getString("bank_name"),
                            rs.getString("account_number"),
                            rs.getString("account_holder_name")
                    );
                }
            }
        }
        return null;
    }

    private BigDecimal getLockedAvailableBalance(Connection connection, int shopId) throws SQLException {
        String sql = """
                SELECT available_balance
                FROM seller_wallets WITH (UPDLOCK, ROWLOCK)
                WHERE shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("available_balance");
                }
            }
        }
        return null;
    }

    private BigDecimal parseAmount(String amountRaw, Map<String, String> errors) {
        if (amountRaw == null || amountRaw.isBlank()) {
            errors.put("amount", "Vui lòng nhập số tiền muốn rút.");
            return BigDecimal.ZERO;
        }

        try {
            String normalized = normalizeMoney(amountRaw);
            BigDecimal amount = new BigDecimal(normalized);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("amount", "Số tiền rút phải lớn hơn 0.");
            } else if (amount.compareTo(MIN_PAYOUT_AMOUNT) < 0) {
                errors.put("amount", "Số tiền rút tối thiểu là 10.000đ.");
            }
            return amount;
        } catch (NumberFormatException ex) {
            errors.put("amount", "Số tiền rút không hợp lệ.");
            return BigDecimal.ZERO;
        }
    }

    private String normalizeMoney(String value) {
        String normalized = value
                .replace("đ", "")
                .replace("Đ", "")
                .replace("VND", "")
                .replace("vnd", "")
                .replaceAll("\\s+", "");

        int lastDot = normalized.lastIndexOf('.');
        int lastComma = normalized.lastIndexOf(',');
        int lastSeparator = Math.max(lastDot, lastComma);
        if (lastSeparator > -1) {
            String decimalPart = normalized.substring(lastSeparator + 1);
            String integerPart = normalized.substring(0, lastSeparator).replaceAll("[.,]", "");
            if (decimalPart.matches("\\d{1,2}") && integerPart.matches("\\d+")) {
                return integerPart + "." + decimalPart;
            }
        }

        return normalized.replaceAll("[^\\d]", "");
    }

    private Integer parseInteger(String raw, String key, String message, Map<String, String> errors) {
        if (raw == null || raw.isBlank()) {
            errors.put(key, message);
            return null;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            errors.put(key, message);
            return null;
        }
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

    private String bankCodeOf(String bankName) {
        Map<String, String> codes = Map.ofEntries(
                Map.entry("Vietcombank", "VCB"),
                Map.entry("VietinBank", "CTG"),
                Map.entry("BIDV", "BIDV"),
                Map.entry("Agribank", "AGR"),
                Map.entry("Techcombank", "TCB"),
                Map.entry("MB Bank", "MBB"),
                Map.entry("ACB", "ACB"),
                Map.entry("Sacombank", "STB"),
                Map.entry("VPBank", "VPB"),
                Map.entry("TPBank", "TPB"),
                Map.entry("HDBank", "HDB"),
                Map.entry("VIB", "VIB"),
                Map.entry("SHB", "SHB"),
                Map.entry("OCB", "OCB"),
                Map.entry("MSB", "MSB"),
                Map.entry("SeABank", "SSB"),
                Map.entry("LPBank", "LPB"),
                Map.entry("Eximbank", "EIB"),
                Map.entry("Nam A Bank", "NAB"),
                Map.entry("PVcomBank", "PVCB"),
                Map.entry("Bac A Bank", "BAB"),
                Map.entry("ABBANK", "ABB"),
                Map.entry("KienlongBank", "KLB"),
                Map.entry("VietBank", "VBB"),
                Map.entry("Saigonbank", "SGB"),
                Map.entry("BaoViet Bank", "BVB"),
                Map.entry("NCB", "NCB"),
                Map.entry("PGBank", "PGB")
        );
        String fallback = bankName.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return codes.getOrDefault(bankName, fallback.length() > 20 ? fallback.substring(0, 20) : fallback);
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

    private record PayoutAccount(
            int accountId,
            String bankName,
            String accountNumber,
            String accountHolderName
    ) {
    }
}
