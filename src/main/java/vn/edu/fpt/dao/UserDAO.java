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

//    public int getRoleIdByName(String roleName) {
//        String sql = "SELECT role_id FROM roles WHERE role_name=?";
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, roleName);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("role_id");
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return 0;
//    }

    public int insertUser(User user) {
        String sql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, role_id, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

//            ps.setInt(8, user.getRoleId());
            ps.setString(9, user.getStatus().name());

            if (user.getCreatedAt() != null) {
                ps.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));
            } else {
                ps.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            }

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

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

//                    user.setRoleId(rs.getInt("role_id"));

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public User getUserByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

//                    user.setRoleId(rs.getInt("role_id"));

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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