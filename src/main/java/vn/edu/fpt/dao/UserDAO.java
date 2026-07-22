package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.controller.admin.UserAdminDTO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends DBContext {

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isPhoneExist(String phone) {
        String sql = "SELECT 1 FROM users WHERE phone = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public boolean isProvinceExist(int provinceId) {
        String sql = "SELECT 1 FROM provinces WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, provinceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isWardBelongsToProvince(int wardId, int provinceId) {
        String sql = "SELECT 1 FROM wards WHERE id = ? AND province_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, wardId);
            ps.setInt(2, provinceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Map<String, Object>> getAllProvincesForRegister() {
        List<Map<String, Object>> provinces = new ArrayList<>();

        String sql = "SELECT id, COALESCE(full_name, name) AS province_name "
                + "FROM provinces "
                + "ORDER BY province_name";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("name", rs.getString("province_name"));
                provinces.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return provinces;
    }

    public List<Map<String, Object>> getAllWardsForRegister() {
        List<Map<String, Object>> wards = new ArrayList<>();

        String sql = "SELECT id, province_id, COALESCE(name_with_type, name) AS ward_name "
                + "FROM wards "
                + "ORDER BY ward_name";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("provinceId", rs.getInt("province_id"));
                item.put("name", rs.getString("ward_name"));
                wards.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wards;
    }

    public int getRoleIdByName(String roleName) {
        String sql = "SELECT TOP 1 role_id FROM roles "
                + "WHERE UPPER(LTRIM(RTRIM(role_name))) = UPPER(LTRIM(RTRIM(?)))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("role_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Integer getRoleIdByUserId(int userId) {
        String sql = "SELECT TOP 1 ur.role_id "
                + "FROM user_roles ur "
                + "JOIN roles r ON ur.role_id = r.role_id "
                + "WHERE ur.user_id = ? "
                + "ORDER BY CASE UPPER(LTRIM(RTRIM(r.role_name))) "
                + "WHEN 'ADMIN' THEN 1 "
                + "WHEN 'SELLER' THEN 2 "
                + "WHEN 'DELIVERY' THEN 3 "
                + "WHEN 'CUSTOMER' THEN 4 "
                + "ELSE 5 END";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("role_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean userHasRole(int userId, String roleName) {
        String sql = "SELECT 1 "
                + "FROM user_roles ur "
                + "JOIN roles r ON ur.role_id = r.role_id "
                + "WHERE ur.user_id = ? "
                + "AND UPPER(LTRIM(RTRIM(r.role_name))) = UPPER(LTRIM(RTRIM(?)))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public int insertUser(User user) {
        String sql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBasicUserInsertParameters(ps, user);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean insertUserRole(int userId, int roleId) {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int insertUserWithRole(User user, int roleId) {
        String insertUserSql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        if (roleId <= 0) {
            return 0;
        }

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int userId = 0;

            try (PreparedStatement ps = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                setBasicUserInsertParameters(ps, user);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId <= 0) {
                connection.rollback();
                return 0;
            }

            try (PreparedStatement psRole = connection.prepareStatement(insertRoleSql)) {
                psRole.setInt(1, userId);
                psRole.setInt(2, roleId);

                if (psRole.executeUpdate() <= 0) {
                    connection.rollback();
                    return 0;
                }
            }

            connection.commit();
            return userId;

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /*
     * Overload cũ, giữ lại để tránh lỗi compile.
     * Logic mới bỏ shipper nên các tham số shipper bị bỏ qua.
     */
    public int insertUserWithRole(User user,
                                  int roleId,
                                  String licensePlate,
                                  String idCardNumber,
                                  Integer shipperProvinceId,
                                  Integer shipperWardId) {
        return insertUserWithRole(user, roleId);
    }

    /*
     * Overload cũ, giữ lại để tránh lỗi compile.
     * Logic mới bỏ shipper nên các tham số shipper bị bỏ qua.
     */
    public int insertUserWithRole(User user,
                                  int roleId,
                                  String licensePlate,
                                  String idCardNumber,
                                  Integer shipperProvinceId,
                                  Integer shipperWardId,
                                  String driverLicenseFrontUrl,
                                  String driverLicenseBackUrl) {
        return insertUserWithRole(user, roleId);
    }

    public boolean updatePendingUserBeforeResendOtp(int userId, User user) {
        String sql = "UPDATE users "
                + "SET first_name = ?, "
                + "last_name = ?, "
                + "password_hash = ?, "
                + "gender = ?, "
                + "date_of_birth = ? "
                + "WHERE user_id = ? AND status = 'PENDING'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPasswordHash());

            if (user.getGender() != null) {
                ps.setString(4, user.getGender().name());
            } else {
                ps.setNull(4, Types.NVARCHAR);
            }

            if (user.getDateOfBirth() != null) {
                ps.setDate(5, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Overload cũ, giữ lại để tránh lỗi compile.
     * Logic mới bỏ shipper nên các tham số shipper bị bỏ qua.
     */
    public boolean updatePendingUserBeforeResendOtp(int userId,
                                                    User user,
                                                    int roleId,
                                                    String licensePlate,
                                                    String idCardNumber,
                                                    Integer shipperProvinceId,
                                                    Integer shipperWardId) {

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            boolean updated = updatePendingUserBeforeResendOtp(userId, user);

            if (!updated) {
                connection.rollback();
                return false;
            }

            if (roleId > 0) {
                replaceUserRole(userId, roleId);
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void replaceUserRole(int userId, int roleId) throws SQLException {
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }

    private void setBasicUserInsertParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getPasswordHash());

        if (user.getGender() != null) {
            ps.setString(6, user.getGender().name());
        } else {
            ps.setNull(6, Types.NVARCHAR);
        }

        if (user.getDateOfBirth() != null) {
            ps.setDate(7, Date.valueOf(user.getDateOfBirth()));
        } else {
            ps.setNull(7, Types.DATE);
        }

        if (user.getStatus() != null) {
            ps.setString(8, user.getStatus().name());
        } else {
            ps.setString(8, UserStatus.PENDING.name());
        }

        if (user.getCreatedAt() != null) {
            ps.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));
        } else {
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
        }
    }

    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 * FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User getUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 * FROM users WHERE phone = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public User getUserById(int userId) {
        if (userId <= 0) {
            return null;
        }

        String sql = "SELECT TOP 1 * FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setLegalFullName(rs.getString("legal_full_name"));
        user.setCitizenId(rs.getString("id_card_number"));        user.setPermanentAddress(rs.getString("permanent_address"));
        user.setFrontIdImage(rs.getString("front_id_image"));
        user.setBackIdImage(rs.getString("back_id_image"));

        String gender = rs.getString("gender");
        if (gender != null && !gender.trim().isEmpty()) {
            try {
                user.setGender(Gender.valueOf(gender.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                user.setGender(null);
            }
        }

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            user.setDateOfBirth(dob.toLocalDate());
        }

        // Date citizenIssueDate = rs.getDate("citizen_id_issue_date");
        // if (citizenIssueDate != null) {
        //     user.setCitizenIdIssueDate(citizenIssueDate.toLocalDate());
        // }

        String status = rs.getString("status");
        if (status != null && !status.trim().isEmpty()) {
            try {
                user.setStatus(UserStatus.valueOf(status.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                user.setStatus(null);
            }
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }

    public boolean updateUserStatus(int userId, UserStatus status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================================================================
    // PHẦN BỔ SUNG: PHỤC VỤ TRANG QUẢN LÝ USER ADMIN
    // =========================================================================

    public List<UserAdminDTO> getFilteredUsers(String search,
                                               String role,
                                               String status,
                                               int pageIndex,
                                               int pageSize) {
        List<UserAdminDTO> list = new ArrayList<>();

        String sql = "SELECT u.user_id, u.avatar_url, (u.first_name + ' ' + u.last_name) AS full_name, "
                + "u.email, u.status, u.created_at, "
                + "(SELECT STRING_AGG(r.role_name, ', ') "
                + "FROM user_roles ur "
                + "JOIN roles r ON ur.role_id = r.role_id "
                + "WHERE ur.user_id = u.user_id) AS role_names "
                + "FROM users u "
                + "WHERE (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ?) ";

        if (!"all".equals(status)) {
            sql += " AND u.status = ? ";
        }

        if (!"all".equals(role)) {
            sql += " AND EXISTS (SELECT 1 "
                    + "FROM user_roles ur2 "
                    + "JOIN roles r2 ON ur2.role_id = r2.role_id "
                    + "WHERE ur2.user_id = u.user_id AND r2.role_name = ?) ";
        }
        
        sql += " AND NOT EXISTS (SELECT 1 FROM user_roles ur3 JOIN roles r3 ON ur3.role_id = r3.role_id WHERE ur3.user_id = u.user_id AND r3.role_name = 'ADMIN') ";

        sql += " ORDER BY u.user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            String searchPattern = "%" + search + "%";

            ps.setString(paramIndex++, searchPattern);
            ps.setString(paramIndex++, searchPattern);
            ps.setString(paramIndex++, searchPattern);

            if (!"all".equals(status)) {
                ps.setString(paramIndex++, status);
            }

            if (!"all".equals(role)) {
                ps.setString(paramIndex++, role);
            }

            ps.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserAdminDTO dto = new UserAdminDTO();
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setAvatar(rs.getString("avatar_url"));
                    dto.setFullName(rs.getString("full_name"));
                    dto.setEmail(rs.getString("email"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));

                    String roles = rs.getString("role_names");
                    dto.setRoleNames(roles != null ? roles : "CUSTOMER");

                    list.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalFilteredUsers(String search, String role, String status) {
        String sql = "SELECT COUNT(*) "
                + "FROM users u "
                + "WHERE (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ?) ";

        if (!"all".equals(status)) {
            sql += " AND u.status = ? ";
        }

        if (!"all".equals(role)) {
            sql += " AND EXISTS (SELECT 1 "
                    + "FROM user_roles ur2 "
                    + "JOIN roles r2 ON ur2.role_id = r2.role_id "
                    + "WHERE ur2.user_id = u.user_id AND r2.role_name = ?) ";
        }
        
        sql += " AND NOT EXISTS (SELECT 1 FROM user_roles ur3 JOIN roles r3 ON ur3.role_id = r3.role_id WHERE ur3.user_id = u.user_id AND r3.role_name = 'ADMIN') ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            String searchPattern = "%" + search + "%";

            ps.setString(paramIndex++, searchPattern);
            ps.setString(paramIndex++, searchPattern);
            ps.setString(paramIndex++, searchPattern);

            if (!"all".equals(status)) {
                ps.setString(paramIndex++, status);
            }

            if (!"all".equals(role)) {
                ps.setString(paramIndex++, role);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updateStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateUserContact(int userId, String email, String phone) {
        String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean activateUserAfterOtp(int userId) {
        /*
         * Logic mới:
         * OTP xác thực xong thì ACTIVE tài khoản.
         * Không update shipper_approval_status.
         */
        String sql = "UPDATE users SET status = 'ACTIVE' WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Dùng khi người dùng bấm "Sửa thông tin đăng ký".
     * Chỉ xóa user chưa xác thực OTP: users.status = 'PENDING'.
     */
    public boolean deletePendingRegistrationByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim().toLowerCase();

        User user = getUserByEmail(email);

        if (user == null || user.getStatus() != UserStatus.PENDING) {
            return false;
        }

        boolean oldAutoCommit = true;

        String deleteOtpSql = "DELETE FROM email_verifications WHERE email = ?";
        String deleteRoleSql = "DELETE FROM user_roles WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE user_id = ? AND status = 'PENDING'";

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement psOtp = connection.prepareStatement(deleteOtpSql)) {
                psOtp.setString(1, email);
                psOtp.executeUpdate();
            }

            try (PreparedStatement psRole = connection.prepareStatement(deleteRoleSql)) {
                psRole.setInt(1, user.getUserId());
                psRole.executeUpdate();
            }

            try (PreparedStatement psUser = connection.prepareStatement(deleteUserSql)) {
                psUser.setInt(1, user.getUserId());

                if (psUser.executeUpdate() <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /*
     * Dọn các tài khoản chưa xác thực OTP quá hạn.
     * Ví dụ gọi deleteExpiredPendingRegistrations(15)
     * thì user có users.status = 'PENDING' quá 15 phút kể từ created_at sẽ bị xóa.
     */
    /*
     * Dọn các tài khoản PENDING quá hạn OTP.
     *
     * Logic cũ:
     * - Tìm user status = PENDING quá hạn.
     * - Xóa email_verifications.
     * - Xóa user_roles.
     * - Xóa users.
     *
     * Vấn đề:
     * - Có user đang bị bảng deliveries tham chiếu qua shipper_id.
     * - Nếu cố DELETE users thì SQL Server báo lỗi khóa ngoại.
     *
     * Logic mới:
     * - Vẫn dọn user PENDING quá hạn.
     * - Nhưng chỉ xóa user nào KHÔNG bị deliveries tham chiếu.
     * - Như vậy không phá logic login/OTP, cũng không làm lỗi FK.
     */
    public int deleteExpiredPendingRegistrations(int pendingMinutes) {
        String findSql = "SELECT u.user_id, u.email "
                + "FROM users u "
                + "WHERE u.status = 'PENDING' "
                + "AND u.created_at IS NOT NULL "
                + "AND DATEADD(MINUTE, ?, u.created_at) < GETDATE() "
                + "AND NOT EXISTS ( "
                + "    SELECT 1 FROM deliveries d "
                + "    WHERE d.shipper_id = u.user_id "
                + ")";

        String deleteOtpSql = "DELETE FROM email_verifications WHERE email = ?";
        String deleteRoleSql = "DELETE FROM user_roles WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE user_id = ? AND status = 'PENDING'";

        List<Integer> userIds = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(findSql)) {
            ps.setInt(1, pendingMinutes);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                    emails.add(rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        if (userIds.isEmpty()) {
            return 0;
        }

        boolean oldAutoCommit = true;
        int deletedCount = 0;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            for (int i = 0; i < userIds.size(); i++) {
                int userId = userIds.get(i);
                String email = emails.get(i);

                /*
                 * Xóa OTP trước vì email_verifications phụ thuộc email đăng ký.
                 */
                try (PreparedStatement psOtp = connection.prepareStatement(deleteOtpSql)) {
                    psOtp.setString(1, email);
                    psOtp.executeUpdate();
                }

                /*
                 * Xóa role trước khi xóa user.
                 */
                try (PreparedStatement psRole = connection.prepareStatement(deleteRoleSql)) {
                    psRole.setInt(1, userId);
                    psRole.executeUpdate();
                }

                /*
                 * Xóa user cuối cùng.
                 * User nào đang bị deliveries tham chiếu đã bị loại ngay từ findSql.
                 */
                try (PreparedStatement psUser = connection.prepareStatement(deleteUserSql)) {
                    psUser.setInt(1, userId);
                    deletedCount += psUser.executeUpdate();
                }
            }

            connection.commit();
            return deletedCount;

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
    /*
     * Method này dùng cho màn edit profile.
     *
     * Mục đích:
     * - Kiểm tra số điện thoại user nhập có bị trùng với tài khoản khác không.
     *
     * Vì sao không dùng isPhoneExist(phone)?
     *
     * Ví dụ:
     * - User A có phone = 0957777777.
     * - User A vào edit profile nhưng không đổi số điện thoại.
     * - Nếu dùng SELECT 1 FROM users WHERE phone = ?
     *   thì DB vẫn báo số này đã tồn tại.
     *
     * Nhưng số đó là của chính User A, không phải tài khoản khác.
     *
     * Vì vậy phải thêm:
     * AND user_id <> ?
     */


    /*
     * Kiểm tra số điện thoại này có thuộc tài khoản khác không.
     *
     * Dùng cho màn edit profile.
     *
     * Vì khi user sửa profile:
     * - Nếu giữ nguyên số điện thoại của chính mình thì được phép.
     * - Nhưng nếu nhập số điện thoại của tài khoản khác thì phải báo lỗi.
     *
     * Method này trả về status của tài khoản khác đang dùng số điện thoại đó:
     * - PENDING
     * - ACTIVE
     * - LOCKED
     *
     * Nếu không có tài khoản khác dùng số này thì trả về null.
     */
    public String findPhoneOwnerStatusForOtherUser(String phone, int currentUserId) {
        String sql = "SELECT status "
                + "FROM users "
                + "WHERE phone = ? "
                + "AND user_id <> ? "
                + "AND status IN ('PENDING', 'ACTIVE', 'LOCKED')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setInt(2, currentUserId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * Method update profile cho bảng users.
     *
     * Chỉ update các cột thuộc màn chỉnh sửa hồ sơ:
     * - first_name
     * - last_name
     * - phone
     * - gender
     * - date_of_birth
     * - avatar_url
     *
     * Không update:
     * - email
     * - password_hash
     * - status
     * - created_at
     */
    public boolean updateProfile(int userId,
                                 String firstName,
                                 String lastName,
                                 String phone,
                                 Gender gender,
                                 java.time.LocalDate dateOfBirth,
                                 String avatarUrl) {

        /*
         * SQL update đúng bảng users.
         *
         * WHERE user_id = ?
         * rất quan trọng.
         * Nếu thiếu WHERE thì sẽ update toàn bộ users.
         */
        String sql = "UPDATE users "
                + "SET first_name = ?, "
                + "last_name = ?, "
                + "phone = ?, "
                + "gender = ?, "
                + "date_of_birth = ?, "
                + "avatar_url = ? "
                + "WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            /*
             * Dấu ? số 1: first_name
             */
            ps.setString(1, firstName);

            /*
             * Dấu ? số 2: last_name
             */
            ps.setString(2, lastName);

            /*
             * Dấu ? số 3: phone
             */
            ps.setString(3, phone);

            /*
             * Dấu ? số 4: gender
             *
             * Java dùng enum Gender.
             * DB lưu chuỗi:
             * - NAM
             * - NU
             * - UNISEX
             */
            if (gender != null) {
                ps.setString(4, gender.name());
            } else {
                ps.setNull(4, Types.NVARCHAR);
            }

            /*
             * Dấu ? số 5: date_of_birth
             *
             * Java dùng LocalDate.
             * JDBC cần java.sql.Date.
             */
            if (dateOfBirth != null) {
                ps.setDate(5, Date.valueOf(dateOfBirth));
            } else {
                ps.setNull(5, Types.DATE);
            }

            /*
             * Dấu ? số 6: avatar_url
             */
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                ps.setString(6, avatarUrl.trim());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            /*
             * Dấu ? số 7: user_id trong WHERE.
             */
            ps.setInt(7, userId);

            /*
             * executeUpdate dùng cho UPDATE/INSERT/DELETE.
             * Nếu số dòng ảnh hưởng > 0 nghĩa là update thành công.
             */
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Cập nhật mật khẩu mới theo email.
     *
     * Dùng cho chức năng quên mật khẩu.
     */
    public boolean updatePasswordByEmail(String email, String passwordHash) {
        if (email == null || email.trim().isEmpty()
                || passwordHash == null || passwordHash.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE users "
                + "SET password_hash = ? "
                + "WHERE email = ? AND status = 'ACTIVE'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, email.trim().toLowerCase());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
