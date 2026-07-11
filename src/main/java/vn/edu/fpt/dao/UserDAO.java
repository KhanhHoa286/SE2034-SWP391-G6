package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;
import vn.edu.fpt.controller.admin.UserAdminDTO;
import java.sql.*;

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
        String sql = "SELECT TOP 1 role_id FROM user_roles WHERE user_id = ?";

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

    /*
     * DB sql2.pdf:
     * Bảng users không còn cột role_id.
     */
    public int insertUser(User user) {
        String sql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserInsertParameters(ps, user);
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

    /*
     * Đăng ký user + gán role CUSTOMER trong cùng transaction.
     * Không sửa User.java, không dùng user.getRoleId().
     */
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
                setUserInsertParameters(ps, user);
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
     * Cho phép đăng ký lại tài khoản đang PENDING:
     * Không tạo user mới vì email/phone là UNIQUE.
     * Chỉ update lại thông tin tạm, rồi gửi OTP mới.
     */
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

    private void setUserInsertParameters(PreparedStatement ps, User user) throws SQLException {
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
        String sql = "SELECT TOP 1 * FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

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
        String sql = "SELECT TOP 1 * FROM users WHERE phone = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);

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
    // PHẦN BỔ SUNG: PHỤC VỤ TRANG QUẢN LÝ USER ADMIN (ĐỒNG BỘ 100% DB HIỆN TẠI)
    // =========================================================================

    public java.util.List<UserAdminDTO> getFilteredUsers(String search, String role, String status, int pageIndex, int pageSize) {
        java.util.List<UserAdminDTO> list = new java.util.ArrayList<>();

        // Câu lệnh SQL truy vấn động (Dùng cú pháp OFFSET FETCH của SQL Server tương ứng với TOP trong dự án của bạn)
        // Ghép first_name và last_name thành fullName, gộp các role tương ứng của user lại
        String sql = "SELECT u.user_id, u.avatar_url, (u.first_name + ' ' + u.last_name) AS full_name, "
                + "u.email, u.status, u.created_at, "
                + "(SELECT STRING_AGG(r.role_name, ', ') FROM user_roles ur JOIN roles r ON ur.role_id = r.role_id WHERE ur.user_id = u.user_id) AS role_names "
                + "FROM users u "
                + "WHERE (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ?) ";

        if (!"all".equals(status)) {
            sql += " AND u.status = ? ";
        }
        if (!"all".equals(role)) {
            sql += " AND EXISTS (SELECT 1 FROM user_roles ur2 JOIN roles r2 ON ur2.role_id = r2.role_id WHERE ur2.user_id = u.user_id AND r2.role_name = ?) ";
        }
        
        sql += " AND NOT EXISTS (SELECT 1 FROM user_roles ur3 JOIN roles r3 ON ur3.role_id = r3.role_id WHERE ur3.user_id = u.user_id AND r3.role_name = 'ADMIN') ";

        // Thực hiện phân trang an toàn
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

            // Tính toán số hàng bỏ qua để phân trang
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
        String sql = "SELECT COUNT(*) FROM users u WHERE (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ?) ";

        if (!"all".equals(status)) {
            sql += " AND u.status = ? ";
        }
        if (!"all".equals(role)) {
            sql += " AND EXISTS (SELECT 1 FROM user_roles ur2 JOIN roles r2 ON ur2.role_id = r2.role_id WHERE ur2.user_id = u.user_id AND r2.role_name = ?) ";
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

    // Hàm thực hiện cập nhật trạng thái người dùng khi ấn Block/Unblock từ giao diện admin
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
}