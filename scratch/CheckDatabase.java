package scratch;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class CheckDatabase {
    public static void main(String[] args) {
        System.out.println("Checking database for shops and products...");

        Properties properties = new Properties();
        try (InputStream inputStream = CheckDatabase.class.getClassLoader().getResourceAsStream("ConnectDB.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }

        String url = properties.getProperty("url", "jdbc:sqlserver://localhost:1433;databaseName=MODA;trustServerCertificate=true");
        String user = properties.getProperty("userID", "sa");
        String pass = properties.getProperty("password", "123");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("\n--- SHOPS TABLE ---");
            String shopSql = "SELECT shop_id, owner_id, shop_name, status, approval_status FROM shops";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(shopSql)) {
                while (rs.next()) {
                    System.out.println("ShopID: " + rs.getInt("shop_id") + 
                                       ", OwnerID: " + rs.getInt("owner_id") + 
                                       ", Name: " + rs.getNString("shop_name") + 
                                       ", Status: " + rs.getString("status") + 
                                       ", Approval: " + rs.getString("approval_status"));
                }
            }

            System.out.println("\n--- PRODUCTS TABLE COUNT BY SHOP ---");
            String prodSql = "SELECT shop_id, COUNT(*) as cnt FROM products GROUP BY shop_id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(prodSql)) {
                while (rs.next()) {
                    System.out.println("ShopID: " + rs.getInt("shop_id") + ", Product Count: " + rs.getInt("cnt"));
                }
            }

            System.out.println("\n--- SHOP APPLICATIONS TABLE ---");
            String appSql = "SELECT application_id, user_id, shop_name, status FROM shop_applications";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(appSql)) {
                while (rs.next()) {
                    System.out.println("AppID: " + rs.getInt("application_id") + 
                                       ", UserID: " + rs.getInt("user_id") + 
                                       ", Name: " + rs.getNString("shop_name") + 
                                       ", Status: " + rs.getString("status"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
