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
        String sql = "SELECT s.shop_name AS shop_name, SUM(p.amount) AS total_amount "
                + "FROM payout_requests p "
                + "JOIN shops s ON p.shop_id = s.shop_id "
                + "WHERE p.status = 'APPROVED' "
                + "GROUP BY s.shop_name "
                + "ORDER BY total_amount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                chartData.put(rs.getString("shop_name"), rs.getDouble("total_amount"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return chartData;
    }
}