package vn.edu.fpt;

import vn.edu.fpt.common.DBContext;
import java.sql.Connection;
import java.sql.Statement;

public class FixDB extends DBContext {
    public static void main(String[] args) {
        FixDB fix = new FixDB();
        Connection conn = fix.getConnection();
        if (conn == null) {
            System.out.println("Connection is null");
            return;
        }
        try {
            Statement stmt = conn.createStatement();
            int deleted = stmt.executeUpdate("DELETE FROM commission_configs WHERE commission_rate < 1");
            System.out.println("Deleted " + deleted + " rows.");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
