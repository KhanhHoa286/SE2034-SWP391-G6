package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
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

    public boolean isIdCardNumberUsedByOther(String idCardNumber, Integer excludedUserId) {
        if (idCardNumber == null || idCardNumber.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE id_card_number = ?";

        if (excludedUserId != null) {
            sql += " AND user_id <> ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idCardNumber.trim());

            if (excludedUserId != null) {
                ps.setInt(2, excludedUserId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isLicensePlateUsedByOther(String licensePlate, Integer excludedUserId) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE license_plate = ?";

        if (excludedUserId != null) {
            sql += " AND user_id <> ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, licensePlate.trim().toUpperCase());

            if (excludedUserId != null) {
                ps.setInt(2, excludedUserId);
            }

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

    public User getUserByIdCardNumber(String idCardNumber) {
        if (idCardNumber == null || idCardNumber.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 * FROM users WHERE id_card_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idCardNumber.trim());

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

    public User getUserByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 * FROM users WHERE license_plate = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, licensePlate.trim().toUpperCase());

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
        return insertUserWithRole(
                user,
                roleId,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public int insertUserWithRole(User user,
                                  int roleId,
                                  String licensePlate,
                                  String idCardNumber,
                                  Integer shipperProvinceId,
                                  Integer shipperWardId) {
        return insertUserWithRole(
                user,
                roleId,
                licensePlate,
                idCardNumber,
                shipperProvinceId,
                shipperWardId,
                null,
                null
        );
    }

    public int insertUserWithRole(User user,
                                  int roleId,
                                  String licensePlate,
                                  String idCardNumber,
                                  Integer shipperProvinceId,
                                  Integer shipperWardId,
                                  String driverLicenseFrontUrl,
                                  String driverLicenseBackUrl) {

        String insertUserSql = "INSERT INTO users "
                + "(first_name, last_name, email, phone, password_hash, gender, date_of_birth, status, "
                + "license_plate, id_card_number, shipper_province_id, shipper_ward_id, "
                + "driver_license_front_url, driver_license_back_url, shipper_approval_status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)";

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
                setFullUserInsertParameters(
                        ps,
                        user,
                        licensePlate,
                        idCardNumber,
                        shipperProvinceId,
                        shipperWardId,
                        driverLicenseFrontUrl,
                        driverLicenseBackUrl
                );

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
     * Giữ lại để tránh vỡ project nếu code cũ còn gọi.
     * Theo logic mới KHÔNG dùng hàm này để sửa đè user PENDING khi đăng ký lại.
     * Muốn sửa thông tin đăng ký thì xóa PENDING cũ rồi đăng ký lại từ đầu.
     */
    public boolean updatePendingUserBeforeResendOtp(int userId, User user) {
        return updatePendingUserBeforeResendOtp(userId, user, 0, null, null, null, null);
    }

    public boolean updatePendingUserBeforeResendOtp(int userId,
                                                    User user,
                                                    int roleId,
                                                    String licensePlate,
                                                    String idCardNumber,
                                                    Integer shipperProvinceId,
                                                    Integer shipperWardId) {

        String sql = "UPDATE users "
                + "SET first_name = ?, "
                + "last_name = ?, "
                + "password_hash = ?, "
                + "gender = ?, "
                + "date_of_birth = ?, "
                + "license_plate = ?, "
                + "id_card_number = ?, "
                + "shipper_province_id = ?, "
                + "shipper_ward_id = ? "
                + "WHERE user_id = ? AND status = 'PENDING'";

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

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

                setNullableString(ps, 6, licensePlate);
                setNullableString(ps, 7, idCardNumber);
                setNullableInteger(ps, 8, shipperProvinceId);
                setNullableInteger(ps, 9, shipperWardId);

                ps.setInt(10, userId);

                if (ps.executeUpdate() <= 0) {
                    connection.rollback();
                    return false;
                }
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

    private void setFullUserInsertParameters(PreparedStatement ps,
                                             User user,
                                             String licensePlate,
                                             String idCardNumber,
                                             Integer shipperProvinceId,
                                             Integer shipperWardId,
                                             String driverLicenseFrontUrl,
                                             String driverLicenseBackUrl) throws SQLException {

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

        setNullableString(ps, 9, licensePlate);
        setNullableString(ps, 10, idCardNumber);
        setNullableInteger(ps, 11, shipperProvinceId);
        setNullableInteger(ps, 12, shipperWardId);
        setNullableString(ps, 13, driverLicenseFrontUrl);
        setNullableString(ps, 14, driverLicenseBackUrl);

        if (user.getCreatedAt() != null) {
            ps.setTimestamp(15, Timestamp.valueOf(user.getCreatedAt()));
        } else {
            ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private void setNullableInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
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

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAvatarUrl(rs.getString("avatar_url"));

        user.setLicensePlate(rs.getString("license_plate"));
        user.setIdCardNumber(rs.getString("id_card_number"));

        int shipperProvinceId = rs.getInt("shipper_province_id");
        if (!rs.wasNull()) {
            user.setShipperProvinceId(shipperProvinceId);
        }

        int shipperWardId = rs.getInt("shipper_ward_id");
        if (!rs.wasNull()) {
            user.setShipperWardId(shipperWardId);
        }

        user.setDriverLicenseFrontUrl(rs.getString("driver_license_front_url"));
        user.setDriverLicenseBackUrl(rs.getString("driver_license_back_url"));
        user.setShipperApprovalStatus(rs.getString("shipper_approval_status"));

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

    public boolean activateUserAfterOtp(int userId) {
        boolean isDelivery = userHasRole(userId, "DELIVERY");

        String sql;

        if (isDelivery) {
            sql = "UPDATE users "
                    + "SET status = 'ACTIVE', shipper_approval_status = 'PENDING' "
                    + "WHERE user_id = ?";
        } else {
            sql = "UPDATE users "
                    + "SET status = 'ACTIVE' "
                    + "WHERE user_id = ?";
        }

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
     * Không xóa shipper chờ duyệt vì shipper chờ duyệt là:
     * users.status = 'ACTIVE' và shipper_approval_status = 'PENDING'.
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
     * thì user có users.status = 'PENDING' quá 15 phút kể từ lần gửi OTP gần nhất sẽ bị xóa.
     *
     * Quan trọng:
     * - Chỉ xóa users.status = 'PENDING'
     * - Không xóa shipper chờ admin duyệt:
     *   users.status = 'ACTIVE', shipper_approval_status = 'PENDING'
     */
    public int deleteExpiredPendingRegistrations(int pendingMinutes) {
        String findSql = "SELECT user_id, email "
                + "FROM users "
                + "WHERE status = 'PENDING' "
                + "AND created_at IS NOT NULL "
                + "AND DATEADD(MINUTE, ?, created_at) < GETDATE()";

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

                try (PreparedStatement psOtp = connection.prepareStatement(deleteOtpSql)) {
                    psOtp.setString(1, email);
                    psOtp.executeUpdate();
                }

                try (PreparedStatement psRole = connection.prepareStatement(deleteRoleSql)) {
                    psRole.setInt(1, userId);
                    psRole.executeUpdate();
                }

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
}