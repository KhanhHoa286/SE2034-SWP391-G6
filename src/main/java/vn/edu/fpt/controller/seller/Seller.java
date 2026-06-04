package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.EmailUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.SecureRandom;
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

@WebServlet(urlPatterns = {"/seller/finance/add-payout-account"})
public class Seller extends HttpServlet {

    private static final String PAYOUT_ACCOUNT_PAGE = "/seller/finance/add-payout-account.jsp";
    private static final String PENDING_PAYOUT_ACCOUNT_SESSION = "pendingPayoutAccountVerification";
    private static final int OTP_TTL_MILLIS = 10 * 60 * 1000;
    private static final SecureRandom OTP_RANDOM = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        preparePage(request);
        request.getRequestDispatcher(PAYOUT_ACCOUNT_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = trim(request.getParameter("action"));
        if ("verifyOtp".equals(action)) {
            handleVerifyOtp(request, response);
            return;
        }

        Map<String, String> errors = new HashMap<>();
        Map<String, String> oldInput = new HashMap<>();

        String bankName = trim(request.getParameter("bankName"));
        String accountNumber = trim(request.getParameter("accountNumber"));
        String accountHolderName = trim(request.getParameter("accountHolderName"));
        String isDefaultRaw = trim(request.getParameter("isDefault"));

        oldInput.put("bankName", bankName);
        oldInput.put("accountNumber", accountNumber);
        oldInput.put("accountHolderName", accountHolderName);
        oldInput.put("isDefault", isDefaultRaw);

        Integer shopId = resolveSellerShopId(request);
        if (shopId == null) {
            errors.put("system", "Không tìm thấy shop của seller đang đăng nhập.");
        }

        validatePayoutAccount(bankName, accountNumber, accountHolderName, isDefaultRaw, errors);

        if (shopId != null && errors.isEmpty()) {
            try (Connection connection = openConnection()) {
                ensurePayoutAccountTable(connection);
                if (existsSameBankAndAccount(connection, shopId, bankName, accountNumber)) {
                    errors.put("accountNumber", "Tài khoản ngân hàng này đã tồn tại trong danh sách nhận tiền của shop.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errors.put("system", "Không thể kiểm tra trùng tài khoản ngân hàng lúc này.");
            }
        }

        String sellerEmail = "";
        if (shopId != null && errors.isEmpty()) {
            sellerEmail = resolveSellerEmail(request, shopId);
            if (sellerEmail.isBlank()) {
                errors.put("system", "Không tìm thấy email seller để gửi mã xác thực.");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInput);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Vui lòng kiểm tra lại thông tin tài khoản nhận tiền.");
            preparePage(request);
            request.getRequestDispatcher(PAYOUT_ACCOUNT_PAGE).forward(request, response);
            return;
        }

        try {
            String otpCode = generateOtpCode();
            PendingPayoutAccountVerification pending = new PendingPayoutAccountVerification(
                    shopId,
                    bankName,
                    accountNumber,
                    accountHolderName.toUpperCase(),
                    Boolean.parseBoolean(isDefaultRaw),
                    sellerEmail,
                    otpCode,
                    System.currentTimeMillis() + OTP_TTL_MILLIS
            );
            request.getSession().setAttribute(PENDING_PAYOUT_ACCOUNT_SESSION, pending);
            sendPayoutAccountOtpEmail(pending);

            request.setAttribute("popupType", "success");
            request.setAttribute("popupMessage", "Đã gửi mã xác thực tới Gmail của seller. Vui lòng nhập mã để hoàn tất lưu tài khoản.");
            request.setAttribute("otpRequired", true);
            request.setAttribute("verifiedEmail", maskEmail(sellerEmail));
            request.setAttribute("oldInput", oldInput);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInput);
            request.setAttribute("popupType", "error");
            String errorMessage = ex.getMessage();
            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = "Kiem tra cau hinh email.";
            }
            request.setAttribute("popupMessage", "Khong the gui ma xac thuc Gmail: " + errorMessage);
        }

        preparePage(request);
        request.getRequestDispatcher(PAYOUT_ACCOUNT_PAGE).forward(request, response);
    }

    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        String otpCode = trim(request.getParameter("otpCode"));
        PendingPayoutAccountVerification pending = getPendingVerification(request);
        Integer shopId = resolveSellerShopId(request);

        if (pending == null) {
            errors.put("otpCode", "Phiên xác thực đã hết hoặc chưa có mã xác thực. Vui lòng gửi lại mã.");
        } else if (shopId == null || !pending.shopId().equals(shopId)) {
            errors.put("otpCode", "Phiên xác thực không khớp với shop hiện tại.");
        } else if (System.currentTimeMillis() > pending.expiresAtMillis()) {
            request.getSession().removeAttribute(PENDING_PAYOUT_ACCOUNT_SESSION);
            errors.put("otpCode", "Mã xác thực đã hết hạn. Vui lòng gửi lại mã mới.");
        } else if (!pending.otpCode().equals(otpCode)) {
            errors.put("otpCode", "Mã xác thực không đúng.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInputFromPending(pending));
            request.setAttribute("otpRequired", pending != null);
            if (pending != null) {
                request.setAttribute("verifiedEmail", maskEmail(pending.sellerEmail()));
            }
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Vui lòng kiểm tra lại mã xác thực Gmail.");
            preparePage(request);
            request.getRequestDispatcher(PAYOUT_ACCOUNT_PAGE).forward(request, response);
            return;
        }

        try {
            savePayoutAccount(
                    pending.shopId(),
                    pending.bankName(),
                    pending.accountNumber(),
                    pending.accountHolderName(),
                    pending.isDefault()
            );
            request.getSession().removeAttribute(PENDING_PAYOUT_ACCOUNT_SESSION);
            request.setAttribute("popupType", "success");
            request.setAttribute("popupMessage", "Đã xác thực Gmail và lưu tài khoản nhận tiền cho shop.");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof IllegalArgumentException) {
                errors.put("accountNumber", ex.getMessage());
            } else {
                errors.put("system", "Không thể lưu tài khoản nhận tiền vào database lúc này.");
            }
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInputFromPending(pending));
            request.setAttribute("otpRequired", true);
            request.setAttribute("verifiedEmail", maskEmail(pending.sellerEmail()));
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", ex instanceof IllegalArgumentException
                    ? ex.getMessage()
                    : "Không thể lưu tài khoản nhận tiền vào database lúc này.");
        }

        preparePage(request);
        request.getRequestDispatcher(PAYOUT_ACCOUNT_PAGE).forward(request, response);
    }

    private void preparePage(HttpServletRequest request) {
        request.setAttribute("activePage", "wallet");

        Integer shopId = resolveSellerShopId(request);
        if (shopId == null) {
            request.setAttribute("payoutAccounts", List.of());
            if (request.getAttribute("popupMessage") == null) {
                request.setAttribute("popupType", "error");
                request.setAttribute("popupMessage", "Vui lòng đăng nhập bằng tài khoản seller đã có shop.");
            }
            return;
        }

        try (Connection connection = openConnection()) {
            ensurePayoutAccountTable(connection);
            request.setAttribute("payoutAccounts", loadPayoutAccounts(connection, shopId));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("payoutAccounts", List.of());
            if (request.getAttribute("popupMessage") == null) {
                request.setAttribute("popupType", "error");
                request.setAttribute("popupMessage", "Không thể tải danh sách tài khoản nhận tiền.");
            }
        }
    }

    private void savePayoutAccount(
            int shopId,
            String bankName,
            String accountNumber,
            String accountHolderName,
            boolean isDefault
    ) throws Exception {
        Connection connection = null;
        try {
            connection = openConnection();
            connection.setAutoCommit(false);
            ensurePayoutAccountTable(connection);

            boolean shouldBeDefault = isDefault || countPayoutAccounts(connection, shopId) == 0;
            if (shouldBeDefault) {
                clearDefaultPayoutAccounts(connection, shopId);
            }

            if (existsSameBankAndAccount(connection, shopId, bankName, accountNumber)) {
                throw new IllegalArgumentException("Tài khoản ngân hàng này đã tồn tại trong danh sách nhận tiền của shop.");
            }

            String insertSql = """
                    INSERT INTO seller_payout_accounts
                    (shop_id, bank_name, account_number, account_holder_name, is_default)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setInt(1, shopId);
                ps.setString(2, bankName);
                ps.setString(3, accountNumber);
                ps.setString(4, accountHolderName);
                ps.setBoolean(5, shouldBeDefault);
                ps.executeUpdate();
            }

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

    private int countPayoutAccounts(Connection connection, int shopId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM seller_payout_accounts WHERE shop_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    private boolean existsSameBankAndAccount(
            Connection connection,
            int shopId,
            String bankName,
            String accountNumber
    ) throws SQLException {
        String sql = """
                SELECT 1
                FROM seller_payout_accounts WITH (UPDLOCK, HOLDLOCK)
                WHERE shop_id = ?
                  AND bank_name = ?
                  AND account_number = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            ps.setString(2, bankName);
            ps.setString(3, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void clearDefaultPayoutAccounts(Connection connection, int shopId) throws SQLException {
        String sql = """
                UPDATE seller_payout_accounts
                SET is_default = 0,
                    updated_at = GETDATE()
                WHERE shop_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            ps.executeUpdate();
        }
    }

    private List<Map<String, Object>> loadPayoutAccounts(Connection connection, int shopId) throws SQLException {
        String sql = """
                SELECT TOP 5 account_id, bank_name, account_number, account_holder_name, is_default, updated_at
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
                    row.put("updatedAt", rs.getTimestamp("updated_at"));
                    accounts.add(row);
                }
            }
        }
        return accounts;
    }

    private Integer resolveSellerShopId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Integer userId = extractUserId(session.getAttribute("account"));
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

    private String resolveSellerEmail(HttpServletRequest request, int shopId) {
        HttpSession session = request.getSession(false);
        String email = extractEmail(session == null ? null : session.getAttribute("account"));
        if (!email.isBlank()) {
            return email;
        }

        String sql = """
                SELECT u.email
                FROM shops s
                INNER JOIN users u ON s.owner_id = u.user_id
                WHERE s.shop_id = ?
                """;
        try (Connection connection = openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return trim(rs.getString("email"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
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

    private String extractEmail(Object account) {
        if (account == null) {
            return "";
        }

        try {
            Method getter = account.getClass().getMethod("getEmail");
            Object value = getter.invoke(account);
            return value == null ? "" : value.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    private PendingPayoutAccountVerification getPendingVerification(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(PENDING_PAYOUT_ACCOUNT_SESSION);
        return value instanceof PendingPayoutAccountVerification pending ? pending : null;
    }

    private Map<String, String> oldInputFromPending(PendingPayoutAccountVerification pending) {
        Map<String, String> oldInput = new HashMap<>();
        if (pending == null) {
            return oldInput;
        }
        oldInput.put("bankName", pending.bankName());
        oldInput.put("accountNumber", pending.accountNumber());
        oldInput.put("accountHolderName", pending.accountHolderName());
        oldInput.put("isDefault", pending.isDefault() ? "true" : "false");
        return oldInput;
    }

    private String generateOtpCode() {
        return String.format("%06d", OTP_RANDOM.nextInt(1_000_000));
    }

    private void sendPayoutAccountOtpEmail(PendingPayoutAccountVerification pending) throws Exception {
        String subject = "MODA - Mã xác thực thêm tài khoản nhận tiền";
        String content = """
                <div style="font-family:Arial,sans-serif;color:#111827;line-height:1.6">
                    <h2 style="margin:0 0 12px">Xác thực tài khoản nhận tiền MODA</h2>
                    <p>Bạn đang thêm tài khoản nhận tiền cho shop trên MODA.</p>
                    <p><strong>Ngân hàng:</strong> %s</p>
                    <p><strong>Số tài khoản:</strong> %s</p>
                    <p><strong>Chủ tài khoản:</strong> %s</p>
                    <p style="font-size:24px;font-weight:800;letter-spacing:6px;margin:20px 0">%s</p>
                    <p>Mã có hiệu lực trong 10 phút. Nếu bạn không thực hiện thao tác này, vui lòng bỏ qua email.</p>
                </div>
                """.formatted(
                escapeHtml(pending.bankName()),
                escapeHtml(pending.accountNumber()),
                escapeHtml(pending.accountHolderName()),
                pending.otpCode()
        );
        EmailUtils.sendEmail(pending.sellerEmail(), subject, content);
    }

    private void validatePayoutAccount(
            String bankName,
            String accountNumber,
            String accountHolderName,
            String isDefaultRaw,
            Map<String, String> errors
    ) {
        if (bankName == null || bankName.isBlank()) {
            errors.put("bankName", "Vui lòng chọn ngân hàng.");
        } else if (bankName.length() > 100) {
            errors.put("bankName", "Tên ngân hàng không được vượt quá 100 ký tự.");
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            errors.put("accountNumber", "Vui lòng nhập số tài khoản.");
        } else if (!accountNumber.matches("\\d{6,50}")) {
            errors.put("accountNumber", "Số tài khoản chỉ gồm 6 đến 50 chữ số.");
        }

        if (accountHolderName == null || accountHolderName.isBlank()) {
            errors.put("accountHolderName", "Vui lòng nhập tên chủ tài khoản.");
        } else if (accountHolderName.length() > 100) {
            errors.put("accountHolderName", "Tên chủ tài khoản không được vượt quá 100 ký tự.");
        }

        if (!"true".equals(isDefaultRaw) && !"false".equals(isDefaultRaw)) {
            errors.put("isDefault", "Vui lòng chọn trạng thái mặc định.");
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

    private String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return email == null ? "" : email;
        }
        int atIndex = email.indexOf('@');
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) {
            return local.charAt(0) + "***" + domain;
        }
        return local.substring(0, 2) + "***" + domain;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private record PendingPayoutAccountVerification(
            Integer shopId,
            String bankName,
            String accountNumber,
            String accountHolderName,
            boolean isDefault,
            String sellerEmail,
            String otpCode,
            long expiresAtMillis
    ) implements Serializable {
    }
}
