package vn.edu.fpt;

import vn.edu.fpt.common.DBContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class CheckSchema extends DBContext {
    public static void main(String[] args) {
        CheckSchema checker = new CheckSchema();
        Connection conn = checker.getConnection();
        if (conn == null) {
            System.out.println("Connection is null");
            return;
        }
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM commission_configs");
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                System.out.println(meta.getColumnName(i) + " : " + meta.getColumnTypeName(i));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
