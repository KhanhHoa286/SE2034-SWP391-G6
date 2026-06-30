package vn.edu.fpt.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import vn.edu.fpt.common.DBContext;

public class DashboardDAO extends DBContext {

    // 1. Thống kê tổng doanh thu thuần túy theo ngày (Các đơn đã thanh toán)
    public double getTotalRevenue(String date) {
        double total = 0;
        String sql = "SELECT SUM(total_amount) FROM master_orders WHERE payment_status = 'PAID' AND CAST(created_at AS DATE) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // 2. Thống kê tổng số lượng người dùng mới theo ngày
    public int getNewUsersCount(String date) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM users WHERE CAST(created_at AS DATE) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 3. Thống kê tổng số đơn hàng theo ngày (Trừ các đơn đã hủy)
    public int getTotalOrdersCount(String date) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM sub_orders WHERE status != 'CANCELLED' AND CAST(created_at AS DATE) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 4. Số lượng sản phẩm chờ duyệt theo ngày
    public int getPendingProductsCount(String date) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM products WHERE status = 'PENDING' AND is_deleted = 0 AND CAST(created_at AS DATE) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) count = rs.getInt("total");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 5. Lấy biểu đồ doanh thu theo shop theo ngày
    public Map<String, Double> getRevenueDataByShop(String date) {
        Map<String, Double> chartData = new LinkedHashMap<>();
        String sql = "SELECT \n" +
                "    s.shop_name, \n" +
                "    COALESCE(SUM(so.total_amount), 0) AS revenue\n" +
                "FROM shops s\n" +
                "LEFT JOIN sub_orders so ON s.shop_id = so.shop_id AND CAST(so.created_at AS DATE) = ? AND so.status != 'CANCELLED'\n" +
                "GROUP BY s.shop_name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chartData.put(rs.getString("shop_name"), rs.getDouble("revenue"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return chartData;
    }
}