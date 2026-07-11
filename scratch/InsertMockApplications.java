package scratch;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class InsertMockApplications {
    public static void main(String[] args) {
        System.out.println("Starting database initialization for shop applications...");

        Properties properties = new Properties();
        try (InputStream inputStream = InsertMockApplications.class.getClassLoader().getResourceAsStream("ConnectDB.properties")) {
            if (inputStream == null) {
                System.err.println("Could not find ConnectDB.properties. Trying relative path...");
            } else {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }

        // Fallback properties if stream loading fails
        String url = properties.getProperty("url", "jdbc:sqlserver://localhost:1433;databaseName=MODA;trustServerCertificate=true");
        String user = properties.getProperty("userID", "sa");
        String pass = properties.getProperty("password", "123");

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server Driver not found: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to database successfully!");

            // 1. Check if users with specific emails exist, otherwise insert them.
            int userIdAn = getOrCreateUser(conn, "Nguyễn", "Văn An", "an@gmail.com", "0312456789");
            int userIdBich = getOrCreateUser(conn, "Trần", "Thị Bích", "bich@gmail.com", "0108765432");
            int userIdNam = getOrCreateUser(conn, "Lê", "Hoàng Nam", "nam@gmail.com", "0401122334");

            System.out.println("User IDs: An=" + userIdAn + ", Bich=" + userIdBich + ", Nam=" + userIdNam);

            // 2. Insert PENDING shop applications if not already present
            createPendingApplication(conn, userIdAn, "AnFashion VN", "anfashion@gmail.com", "0312456789");
            createPendingApplication(conn, userIdBich, "Bich Store Cosmetics", "bichcosmetics@gmail.com", "0108765432");
            createPendingApplication(conn, userIdNam, "NamTech Electronics", "namtech@gmail.com", "0401122334");

            System.out.println("Database initialization completed successfully!");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int getOrCreateUser(Connection conn, String firstName, String lastName, String email, String phone) throws SQLException {
        String selectSql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }

        String insertSql = "INSERT INTO users (first_name, last_name, email, phone, password_hash, gender, status, created_at) " +
                           "VALUES (?, ?, ?, ?, '123456', 'MALE', 'ACTIVE', GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    // Insert USER role by default
                    assignRoleIfExist(conn, userId, "CUSTOMER");
                    return userId;
                }
            }
        }
        return 0;
    }

    private static void assignRoleIfExist(Connection conn, int userId, String roleName) throws SQLException {
        int roleId = 0;
        String selectSql = "SELECT role_id FROM roles WHERE UPPER(role_name) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roleId = rs.getInt("role_id");
                }
            }
        }
        if (roleId != 0) {
            String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, roleId);
                ps.executeUpdate();
            } catch (SQLException ignored) {} // ignore if already assigned
        }
    }

    private static void createPendingApplication(Connection conn, int userId, String shopName, String email, String taxCode) throws SQLException {
        String selectSql = "SELECT 1 FROM shop_applications WHERE user_id = ? AND shop_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            ps.setString(2, shopName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Application already exists for: " + shopName);
                    return; // Already exists
                }
            }
        }

        String insertSql = "INSERT INTO shop_applications (user_id, shop_name, business_email, tax_code, front_id_image, back_id_image, status, created_at) " +
                           "VALUES (?, ?, ?, ?, 'front_id.jpg', 'back_id.jpg', 'PENDING', GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, email);
            ps.setString(4, taxCode);
            ps.executeUpdate();
            System.out.println("Created PENDING application for: " + shopName);
        }
    }
}
