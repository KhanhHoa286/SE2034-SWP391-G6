package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.sql.*;

public class UserDAO extends DBContext {

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM users WHERE email=?";

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
        String sql = "SELECT 1 FROM users WHERE phone=?";

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
        String sql = "SELECT role_id FROM roles WHERE role_name=?";

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

    /*
     * DB sql2.pdf không còn cột users.role_id.
     * Method này chỉ insert thông tin user vào bảng users.
     * Muốn gán quyền thì gọi thêm insertUserRole(...) hoặc dùng insertUserWithRole(...).
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

    /*
     * Dùng cho DB mới: user_id và role_id được lưu ở bảng user_roles.
     */
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
     * Insert user + gán role trong cùng transaction.
     * Nếu gán role thất bại thì rollback để tránh user không có quyền.
     */
    public int insertUserWithRole(User user) {
        String insertUserSql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        if (user.getRoleId() == null || user.getRoleId() <= 0) {
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
                psRole.setInt(2, user.getRoleId());

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
        String sql = "SELECT u.*, ur.role_id "
                + "FROM users u "
                + "LEFT JOIN user_roles ur ON u.user_id = ur.user_id "
                + "WHERE u.email=?";

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
        String sql = "SELECT u.*, ur.role_id "
                + "FROM users u "
                + "LEFT JOIN user_roles ur ON u.user_id = ur.user_id "
                + "WHERE u.phone=?";

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
            user.setGender(Gender.valueOf(gender));
        }

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            user.setDateOfBirth(dob.toLocalDate());
        }

        int roleId = rs.getInt("role_id");
        if (!rs.wasNull()) {
            user.setRoleId(roleId);
        }

        String status = rs.getString("status");
        if (status != null && !status.trim().isEmpty()) {
            user.setStatus(UserStatus.valueOf(status));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }

    public boolean updateUserStatus(int userId, UserStatus status) {
        String sql = "UPDATE users SET status=? WHERE user_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}