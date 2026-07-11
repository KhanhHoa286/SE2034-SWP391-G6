package scratch;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class CheckProducts {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream inputStream = CheckProducts.class.getClassLoader().getResourceAsStream("ConnectDB.properties")) {
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
            System.out.println("Checking products for ShopID 1...");
            String sql = "SELECT TOP 5 product_id, product_name, base_price, is_deleted, status FROM products WHERE shop_id = 1";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("product_id") + 
                                       ", Name: " + rs.getNString("product_name") + 
                                       ", Price: " + rs.getBigDecimal("base_price") + 
                                       ", IsDeleted: " + rs.getObject("is_deleted") + 
                                       ", Status: " + rs.getString("status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
