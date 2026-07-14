package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.admin.CustomerDTO;
import vn.edu.fpt.controller.admin.OrderHistoryDTO;
import java.sql.*;
import java.time.LocalDate;
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

    public boolean hasSellerAccount(int userId) {
        String sql = """
                SELECT 1
                FROM shops s
                WHERE s.owner_id = ?
                  AND UPPER(LTRIM(RTRIM(s.approval_status))) = 'APPROVED'
                  AND UPPER(LTRIM(RTRIM(s.status))) = 'ACTIVE'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] hasSellerAccount error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPendingSellerRegistration(int userId) {
        String sql = """
                SELECT 1
                FROM shops s
                WHERE s.owner_id = ?
                  AND UPPER(LTRIM(RTRIM(s.approval_status))) = 'PENDING'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] hasPendingSellerRegistration error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean shopNameExists(String shopName) {
        String sql = "SELECT 1 FROM shops WHERE LOWER(LTRIM(RTRIM(shop_name))) = LOWER(LTRIM(RTRIM(?)))";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, shopName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] shopNameExists error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWardInProvince(int wardId, int provinceId) {
        String sql = """
                SELECT 1
                FROM wards
                WHERE id = ?
                  AND province_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, wardId);
            ps.setInt(2, provinceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] isWardInProvince error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasCompletedSellerIdentity(int userId) {
        String sql = """
                SELECT 1
                FROM users
                WHERE user_id = ?
                  AND citizen_id IS NOT NULL
                  AND LTRIM(RTRIM(citizen_id)) <> ''
                  AND legal_full_name IS NOT NULL
                  AND LTRIM(RTRIM(legal_full_name)) <> ''
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] hasCompletedSellerIdentity error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean citizenIdExistsForOtherUser(String citizenId, int currentUserId) {
        String sql = """
                SELECT 1
                FROM users
                WHERE citizen_id = ?
                  AND user_id <> ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, citizenId);
            ps.setInt(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] citizenIdExistsForOtherUser error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSellerIdentity(
            int userId,
            String legalFullName,
            String citizenId,
            LocalDate citizenIdIssueDate,
            String citizenIdIssuePlace,
            String permanentAddress,
            String frontIdImage,
            String backIdImage
    ) {
        String sql = """
                UPDATE users
                SET legal_full_name = ?,
                    citizen_id = ?,
                    citizen_id_issue_date = ?,
                    citizen_id_issue_place = ?,
                    permanent_address = ?,
                    front_id_image = COALESCE(?, front_id_image),
                    back_id_image = COALESCE(?, back_id_image),
                    updated_at = GETDATE()
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, legalFullName);
            ps.setString(2, citizenId);
            if (citizenIdIssueDate != null) {
                ps.setDate(3, Date.valueOf(citizenIdIssueDate));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, citizenIdIssuePlace);
            ps.setString(5, permanentAddress);
            if (frontIdImage == null || frontIdImage.trim().isEmpty()) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, frontIdImage);
            }
            if (backIdImage == null || backIdImage.trim().isEmpty()) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, backIdImage);
            }
            ps.setInt(8, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateSellerIdentity error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public SellerRegistrationResult registerCustomerAsSeller(
            int userId,
            String shopName,
            String streetAddress,
            int wardId,
            String description
    ) {
        String sellerRoleSql = """
                SELECT TOP 1 role_id
                FROM roles
                WHERE UPPER(LTRIM(RTRIM(role_name))) = 'SELLER'
                """;
        String hasShopSql = "SELECT 1 FROM shops WHERE owner_id = ?";
        String hasSellerRoleSql = """
                SELECT 1
                FROM user_roles
                WHERE user_id = ?
                  AND role_id = ?
                """;
        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        String insertShopSql = """
                INSERT INTO shops
                    (owner_id, shop_name, logo_url, description, ward_id, street_address, approval_status, status)
                VALUES
                    (?, ?, NULL, ?, ?, ?, 'APPROVED', 'ACTIVE')
                """;

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int sellerRoleId = 0;
            try (PreparedStatement ps = connection.prepareStatement(sellerRoleSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sellerRoleId = rs.getInt("role_id");
                }
            }

            if (sellerRoleId <= 0) {
                connection.rollback();
                return SellerRegistrationResult.fail("Không tìm thấy role SELLER trong database.");
            }

            try (PreparedStatement ps = connection.prepareStatement(hasShopSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        connection.rollback();
                        return SellerRegistrationResult.fail("Tài khoản này đã có cửa hàng người bán.");
                    }
                }
            }

            boolean hasSellerRole = false;
            try (PreparedStatement ps = connection.prepareStatement(hasSellerRoleSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, sellerRoleId);
                try (ResultSet rs = ps.executeQuery()) {
                    hasSellerRole = rs.next();
                }
            }

            if (!hasSellerRole) {
                try (PreparedStatement ps = connection.prepareStatement(insertRoleSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, sellerRoleId);
                    if (ps.executeUpdate() == 0) {
                        connection.rollback();
                        return SellerRegistrationResult.fail("Không thể cấp quyền người bán cho tài khoản.");
                    }
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(insertShopSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, shopName);
                ps.setString(3, description);
                ps.setInt(4, wardId);
                ps.setString(5, streetAddress);

                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return SellerRegistrationResult.fail("Không thể tạo cửa hàng người bán.");
                }

                int shopId = 0;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        shopId = rs.getInt(1);
                    }
                }

                connection.commit();
                return SellerRegistrationResult.ok(shopId);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            System.err.println("[CustomerDAO] registerCustomerAsSeller error: " + e.getMessage());
            e.printStackTrace();
            return SellerRegistrationResult.fail("Không thể đăng ký người bán. Vui lòng thử lại sau.");
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException ignored) {
            }
        }
    }

    public static class SellerRegistrationResult {
        private final boolean success;
        private final String message;
        private final int shopId;

        private SellerRegistrationResult(boolean success, String message, int shopId) {
            this.success = success;
            this.message = message;
            this.shopId = shopId;
        }

        public static SellerRegistrationResult ok(int shopId) {
            return new SellerRegistrationResult(true, "Đăng ký người bán thành công.", shopId);
        }

        public static SellerRegistrationResult fail(String message) {
            return new SellerRegistrationResult(false, message, 0);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getShopId() {
            return shopId;
        }
    }

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

