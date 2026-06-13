package vn.edu.fpt.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import vn.edu.fpt.common.DBContext;

public class DashboardDAO extends DBContext {

    // 1. Thống kê tổng doanh thu thuần túy (Các đơn đã thanh toán)
    public double getTotalRevenue() {
        double total = 0;
        String sql = "SELECT SUM(total_amount) FROM master_orders WHERE payment_status = 'PAID'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // 2. Thống kê tổng số lượng người dùng
    public int getNewUsersCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM users";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 3. Thống kê tổng số đơn hàng (Trừ các đơn đã hủy)
    public int getTotalOrdersCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM sub_orders WHERE status != 'CANCELLED'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 4. Số lượng sản phẩm chờ duyệt
    public int getPendingProductsCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM products WHERE status = 'PENDING' AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 5. Lấy biểu đồ doanh thu theo shop
    public Map<String, Double> getRevenueDataByShop() {
        Map<String, Double> chartData = new LinkedHashMap<>();
        String sql = "SELECT \n" +
                "    s.shop_name, \n" +
                "    COALESCE(SUM(so.total_amount), 0) AS revenue -- Nếu không có đơn hàng thì mặc định bằng 0\n" +
                "FROM shops s\n" +
                "LEFT JOIN sub_orders so ON s.shop_id = so.shop_id\n" +
                "GROUP BY s.shop_name";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                chartData.put(rs.getString("shop_name"), rs.getDouble("total_amount"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return chartData;
    }
}