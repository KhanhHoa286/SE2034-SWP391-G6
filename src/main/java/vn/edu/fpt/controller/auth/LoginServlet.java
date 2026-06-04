package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.PasswordUtils;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final String LOGIN_PAGE = "/public/auth/login.jsp";
    private static final String DEMO_PASSWORD = "123456";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password"));
        String redirect = trim(request.getParameter("redirect"));

        request.setAttribute("email", email);
        request.setAttribute("redirect", redirect);

        if (email.isBlank() || password.isBlank()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email và mật khẩu.");
            request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
            return;
        }

        try (Connection connection = openConnection()) {
            LoginAccount account = findLoginAccount(connection, email);

            if (account == null || !isPasswordAccepted(password, account.passwordHash())) {
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không đúng.");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(account.status())) {
                request.setAttribute("errorMessage", "Tài khoản chưa ở trạng thái ACTIVE.");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
                return;
            }

            if (!"SELLER".equalsIgnoreCase(account.roleName())) {
                request.setAttribute("errorMessage", "Demo này cần tài khoản SELLER để test payout account.");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
                return;
            }

            if (account.shopId() == null) {
                request.setAttribute("errorMessage", "Seller này chưa có shop nên chưa đủ điều kiện test payout account.");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
                return;
            }

            User loginUser = User.builder()
                    .userId(account.userId())
                    .firstName(account.firstName())
                    .lastName(account.lastName())
                    .email(account.email())
                    .phone(account.phone())
                    .passwordHash(account.passwordHash())
                    .roleId(account.roleId())
                    .role(Role.builder()
                            .roleId(account.roleId())
                            .roleName(account.roleName())
                            .build())
                    .status(UserStatus.valueOf(account.status().toUpperCase()))
                    .build();

            HttpSession session = request.getSession();
            session.setAttribute("account", loginUser);
            session.setAttribute("demoShopId", account.shopId());
            session.setAttribute("demoShopName", account.shopName());

            String target = redirect.isBlank()
                    ? request.getContextPath() + "/seller/finance/view-wallet"
                    : request.getContextPath() + redirect;
            response.sendRedirect(target);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể đăng nhập demo lúc này. Kiểm tra kết nối database.");
            request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
        }
    }

    private LoginAccount findLoginAccount(Connection connection, String email) throws Exception {
        String sql = """
                SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, u.password_hash,
                       u.role_id, u.status, r.role_name, s.shop_id, s.shop_name
                FROM users u
                INNER JOIN roles r ON u.role_id = r.role_id
                LEFT JOIN shops s ON u.user_id = s.owner_id
                WHERE u.email = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer shopId = rs.getObject("shop_id") == null ? null : rs.getInt("shop_id");
                    return new LoginAccount(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("password_hash"),
                            rs.getInt("role_id"),
                            rs.getString("role_name"),
                            rs.getString("status"),
                            shopId,
                            rs.getString("shop_name")
                    );
                }
            }
        }
        return null;
    }

    private boolean isPasswordAccepted(String plainPassword, String passwordHash) {
        return DEMO_PASSWORD.equals(plainPassword)
                || passwordHash.equals(plainPassword)
                || PasswordUtils.checkPassword(plainPassword, passwordHash);
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

    private record LoginAccount(
            int userId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String passwordHash,
            int roleId,
            String roleName,
            String status,
            Integer shopId,
            String shopName
    ) {
    }
}
