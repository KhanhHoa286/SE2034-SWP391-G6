package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.admin.CustomerDTO;
import vn.edu.fpt.controller.admin.OrderHistoryDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * CustomerDAO
 * Xử lý truy vấn dữ liệu cho màn hình Chi tiết khách hàng (Admin).
 */
public class CustomerDAO extends DBContext {

    // ─────────────────────────────────────────────────────────────
    //  1. LẤY THÔNG TIN CHI TIẾT KHÁCH HÀNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Lấy thông tin cơ bản + thống kê của một khách hàng theo user_id.
     *
     * @param userId  ID của user (role CUSTOMER)
     * @return        CustomerDTO hoặc null nếu không tìm thấy
     */
    public CustomerDTO getCustomerDetail(int userId) {
        String sql =
                "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                        "       u.avatar_url, u.gender, u.date_of_birth, u.status, u.created_at, " +
                        // Tổng đơn hàng (master_orders)
                        "       COUNT(DISTINCT mo.master_order_id) AS total_orders, " +
                        // Tổng chi tiêu = tổng total_amount của master_orders đã PAID
                        "       ISNULL(SUM(CASE WHEN mo.payment_status = 'PAID' THEN mo.total_amount ELSE 0 END), 0) AS total_spent, " +
                        // Tỷ lệ hoàn trả = số sub_orders CANCELLED / tổng sub_orders * 100
                        "       CASE WHEN COUNT(DISTINCT so.sub_order_id) = 0 THEN 0 " +
                        "            ELSE CAST(SUM(CASE WHEN so.status = 'CANCELLED' THEN 1 ELSE 0 END) * 100.0 " +
                        "                 / COUNT(DISTINCT so.sub_order_id) AS DECIMAL(5,2)) END AS return_rate " +
                        "FROM users u " +
                        "LEFT JOIN master_orders mo ON mo.customer_id = u.user_id " +
                        "LEFT JOIN sub_orders so    ON so.master_order_id = mo.master_order_id " +
                        "WHERE u.user_id = ? " +
                        "GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                        "         u.avatar_url, u.gender, u.date_of_birth, u.status, u.created_at";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getCustomerDetail error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    //  2. LẤY LỊCH SỬ MUA HÀNG (có phân trang)
    // ─────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách master_orders của khách hàng, mỗi đơn kèm trạng thái tổng hợp.
     * Trạng thái tổng hợp = trạng thái "xấu nhất" trong sub_orders của đơn:
     *   SHIPPING > PENDING/CONFIRMED/PREPARING > DELIVERED > CANCELLED
     *
     * @param userId   ID khách hàng
     * @param page     Trang hiện tại (bắt đầu từ 1)
     * @param pageSize Số đơn mỗi trang
     * @return         Danh sách OrderHistoryDTO
     */
    public List<OrderHistoryDTO> getOrderHistory(int userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        // Lấy trạng thái tổng hợp: ưu tiên SHIPPING → PENDING → DELIVERED → CANCELLED
        String sql =
                "SELECT mo.master_order_id, mo.created_at, mo.total_amount, " +
                        "       mo.payment_method, mo.payment_status, " +
                        "       CASE " +
                        "           WHEN SUM(CASE WHEN so.status = 'SHIPPING'   THEN 1 ELSE 0 END) > 0 THEN 'SHIPPING' " +
                        "           WHEN SUM(CASE WHEN so.status = 'PREPARING'  THEN 1 ELSE 0 END) > 0 THEN 'PREPARING' " +
                        "           WHEN SUM(CASE WHEN so.status = 'CONFIRMED'  THEN 1 ELSE 0 END) > 0 THEN 'CONFIRMED' " +
                        "           WHEN SUM(CASE WHEN so.status = 'PENDING'    THEN 1 ELSE 0 END) > 0 THEN 'PENDING' " +
                        "           WHEN SUM(CASE WHEN so.status = 'DELIVERED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'DELIVERED' " +
                        "           WHEN SUM(CASE WHEN so.status = 'CANCELLED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'CANCELLED' " +
                        "           ELSE 'PENDING' " +
                        "       END AS agg_status " +
                        "FROM master_orders mo " +
                        "LEFT JOIN sub_orders so ON so.master_order_id = mo.master_order_id " +
                        "WHERE mo.customer_id = ? " +
                        "GROUP BY mo.master_order_id, mo.created_at, mo.total_amount, mo.payment_method, mo.payment_status " +
                        "ORDER BY mo.created_at DESC " +
                        "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<OrderHistoryDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getOrderHistory error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số đơn hàng của khách hàng (dùng cho phân trang).
     */
    public int countOrders(int userId) {
        String sql = "SELECT COUNT(*) FROM master_orders WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] countOrders error: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    //  3. KHÓA / MỞ TÀI KHOẢN KHÁCH HÀNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Cập nhật trạng thái tài khoản khách hàng (ACTIVE / BANNED / SUSPENDED).
     *
     * @param userId    ID người dùng
     * @param newStatus Trạng thái mới
     * @return          true nếu cập nhật thành công
     */
    public boolean updateCustomerStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomerStatus error: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPER: MAPPING ResultSet → DTO
    // ─────────────────────────────────────────────────────────────

    private CustomerDTO mapCustomer(ResultSet rs) throws SQLException {
        CustomerDTO dto = new CustomerDTO();
        int uid = rs.getInt("user_id");
        dto.setUserId(uid);

        // Mã CUS: #CUS-00001
        dto.setCustomerId(String.format("#CUS-%05d", uid));

        dto.setFirstName(rs.getString("first_name"));
        dto.setLastName(rs.getString("last_name"));
        dto.setFullName(rs.getString("first_name") + " " + rs.getString("last_name"));
        dto.setEmail(rs.getString("email"));
        dto.setPhone(rs.getString("phone"));
        dto.setAvatarUrl(rs.getString("avatar_url"));
        dto.setGender(rs.getString("gender"));

        java.sql.Date dob = rs.getDate("date_of_birth");
        if (dob != null) dto.setDateOfBirth(new java.util.Date(dob.getTime()));

        dto.setStatus(rs.getString("status"));

        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) dto.setCreatedAt(new java.util.Date(createdAt.getTime()));

        dto.setTotalOrders(rs.getInt("total_orders"));
        dto.setTotalSpent(rs.getDouble("total_spent"));
        dto.setReturnRate(rs.getDouble("return_rate"));

        return dto;
    }

    private OrderHistoryDTO mapOrder(ResultSet rs) throws SQLException {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        int id = rs.getInt("master_order_id");
        dto.setMasterOrderId(id);

        // Mã đơn: #ORD-001
        dto.setOrderCode(String.format("#ORD-%03d", id));

        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) dto.setCreatedAt(new java.util.Date(createdAt.getTime()));

        dto.setTotalAmount(rs.getDouble("total_amount"));
        dto.setStatus(rs.getString("agg_status"));
        dto.setPaymentMethod(rs.getString("payment_method"));
        dto.setPaymentStatus(rs.getString("payment_status"));

        return dto;
    }
}

