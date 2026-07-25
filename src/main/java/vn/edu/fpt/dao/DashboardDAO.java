package vn.edu.fpt.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import vn.edu.fpt.common.DBContext;

public class DashboardDAO extends DBContext {

    // 1. Thống kê tổng doanh thu Admin theo ngày:
    // - Đơn ONLINE (PAID) chưa COMPLETED: Tính 100% tiền đơn hàng Admin tạm giữ
    // - Đơn đã COMPLETED (Cả COD và Online): Tính tiền Hoa hồng (commission_fee) thực nhận của Admin
    public double getTotalRevenue(String date) {
        double total = 0;
        String sql = """
                SELECT 
                    ISNULL(SUM(CASE 
                        WHEN UPPER(LTRIM(RTRIM(mo.payment_method))) != 'COD' 
                             AND UPPER(LTRIM(RTRIM(mo.payment_status))) = 'PAID' 
                             AND UPPER(LTRIM(RTRIM(so.status))) != 'COMPLETED' 
                             AND UPPER(LTRIM(RTRIM(so.status))) != 'CANCELLED'
                        THEN so.total_amount 
                        ELSE 0 
                    END), 0)
                    +
                    ISNULL(SUM(CASE 
                        WHEN UPPER(LTRIM(RTRIM(so.status))) = 'COMPLETED'
                        THEN so.commission_fee 
                        ELSE 0 
                    END), 0) AS total_admin_revenue
                FROM sub_orders so
                JOIN master_orders mo ON so.master_order_id = mo.master_order_id
                WHERE (CAST(so.created_at AS DATE) = ? OR CAST(so.completed_at AS DATE) = ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
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

    // 5. Lấy biểu đồ doanh thu thuần thực nhận theo shop (Chỉ tính các đơn đã COMPLETED)
    public Map<String, Double> getRevenueDataByShop(String date) {
        Map<String, Double> chartData = new LinkedHashMap<>();
        String sql = """
                SELECT 
                    s.shop_name, 
                    COALESCE(SUM(so.total_amount - so.commission_fee), 0) AS revenue
                FROM shops s
                LEFT JOIN sub_orders so ON s.shop_id = so.shop_id 
                    AND (CAST(so.completed_at AS DATE) = ? OR (so.completed_at IS NULL AND CAST(so.created_at AS DATE) = ?))
                    AND so.status = 'COMPLETED'
                GROUP BY s.shop_name
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chartData.put(rs.getString("shop_name"), rs.getDouble("revenue"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return chartData;
    }
}