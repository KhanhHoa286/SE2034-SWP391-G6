package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.response.AddressResponse;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Ward;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class AddressDAO extends DBContext {


    public List<Address> getAddressesByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();

        String sql = "SELECT "
                + "a.address_id, "
                + "a.user_id, "
                + "a.receiver_name, "
                + "a.receiver_phone, "
                + "a.street_address, "
                + "a.ward_id, "
                + "a.is_default, "
                + "a.created_at, "

                + "w.id AS ward_id_value, "
                + "w.province_id AS ward_province_id, "
                + "w.name AS ward_name, "
                + "w.name_with_type AS ward_name_with_type, "
                + "w.path_with_type AS ward_path_with_type, "

                + "p.id AS province_id_value, "
                + "p.name AS province_name, "
                + "p.full_name AS province_full_name, "
                + "p.type AS province_type "

                + "FROM addresses a "
                + "JOIN wards w ON w.id = a.ward_id "
                + "JOIN provinces p ON p.id = w.province_id "
                + "WHERE a.user_id = ? "
                + "ORDER BY a.is_default DESC, a.created_at DESC, a.address_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            /*
             * Gán userId vào dấu ? trong SQL.
             * Chỉ lấy địa chỉ của customer đang đăng nhập.
             */
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    /*
                     * Map dữ liệu tỉnh/thành phố.
                     */
                    Province province = Province.builder()
                            .id(rs.getInt("province_id_value"))
                            .name(rs.getString("province_name"))
                            .fullName(rs.getString("province_full_name"))
                            .type(rs.getString("province_type"))
                            .build();

                    /*
                     * Map dữ liệu phường/xã.
                     * Trong ward có gắn thêm province để JSP có thể hiển thị đủ khu vực.
                     */
                    Ward ward = Ward.builder()
                            .id(rs.getInt("ward_id_value"))
                            .provinceId(rs.getInt("ward_province_id"))
                            .name(rs.getString("ward_name"))
                            .nameWithType(rs.getString("ward_name_with_type"))
                            .pathWithType(rs.getString("ward_path_with_type"))
                            .province(province)
                            .build();

                    Timestamp createdAt = rs.getTimestamp("created_at");

                    /*
                     * Map dữ liệu địa chỉ giao hàng.
                     */
                    Address address = Address.builder()
                            .addressId(rs.getInt("address_id"))
                            .userId(rs.getInt("user_id"))
                            .receiverName(rs.getString("receiver_name"))
                            .receiverPhone(rs.getString("receiver_phone"))
                            .streetAddress(rs.getString("street_address"))
                            .wardId(rs.getInt("ward_id"))
                            .ward(ward)
                            .isDefault(rs.getBoolean("is_default"))
                            .createdAt(createdAt == null ? null : createdAt.toLocalDateTime())
                            .build();

                    addresses.add(address);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Nếu user chưa có địa chỉ hoặc query lỗi,
         * trả về list rỗng để JSP hiển thị empty state.
         */
        return addresses;
    }
    /*
     * Đếm số địa chỉ hiện có của customer.
     *
     * Dùng cho màn thêm địa chỉ:
     * - Nếu customer chưa có địa chỉ nào
     * - Thì địa chỉ đầu tiên sẽ tự động là địa chỉ mặc định
     */
    public int countAddressesByUserId(int userId) {
        String sql = "SELECT COUNT(*) AS total "
                + "FROM addresses "
                + "WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * Thêm địa chỉ giao hàng mới cho customer.
     *
     * Nếu địa chỉ mới được chọn làm mặc định:
     * - Update tất cả địa chỉ cũ của user về is_default = 0
     * - Insert địa chỉ mới với is_default = 1
     *
     * Dùng transaction để đảm bảo dữ liệu không bị lỗi trạng thái mặc định.
     */
    public boolean addAddress(Address address) {
        String updateOldDefaultSql = "UPDATE addresses "
                + "SET is_default = 0 "
                + "WHERE user_id = ?";

        String insertSql = "INSERT INTO addresses ("
                + "user_id, "
                + "receiver_name, "
                + "receiver_phone, "
                + "street_address, "
                + "ward_id, "
                + "is_default, "
                + "created_at"
                + ") VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            /*
             * Vì field trong Address là Boolean isDefault,
             * dùng Boolean.TRUE.equals để tránh NullPointerException.
             */
            boolean newAddressIsDefault = Boolean.TRUE.equals(address.getIsDefault());

            /*
             * Nếu địa chỉ mới là mặc định,
             * bỏ mặc định của các địa chỉ cũ trước.
             */
            if (newAddressIsDefault) {
                try (PreparedStatement psUpdate = connection.prepareStatement(updateOldDefaultSql)) {
                    psUpdate.setInt(1, address.getUserId());
                    psUpdate.executeUpdate();
                }
            }

            /*
             * Insert địa chỉ mới.
             */
            try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                psInsert.setInt(1, address.getUserId());
                psInsert.setString(2, address.getReceiverName());
                psInsert.setString(3, address.getReceiverPhone());
                psInsert.setString(4, address.getStreetAddress());
                psInsert.setInt(5, address.getWardId());
                psInsert.setBoolean(6, newAddressIsDefault);

                psInsert.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            e.printStackTrace();

        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * HoaNK - Lấy ra list address với tên tỉnh và tên quận/huyện
     */
    private final String GET_ADDRESS_CHECKOUT = """
            SELECT p.name AS province_name,w.name AS ward_name,a.receiver_name,a.receiver_phone,a.street_address,a.is_default
            FROM addresses a
            JOIN wards w ON a.ward_id = w.id
            JOIN provinces p ON p.id = w.province_id
            WHERE user_id = ?
            """;
   public AddressResponse getAddressCheckout(int userId) {
       String sql = GET_ADDRESS_CHECKOUT;
       AddressResponse addressResponse = new AddressResponse();
       try(PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, userId);
           try(ResultSet rs = stmt.executeQuery()) {
               while(rs.next()) {
                   boolean isDefault = rs.getBoolean("is_default");
                   if(isDefault == true) {
                       addressResponse.setPhone(rs.getString("receiver_phone"));
                       addressResponse.setFullName(rs.getString("receiver_name"));
                       addressResponse.setWardName(rs.getString("ward_name"));
                       addressResponse.setProvinceName(rs.getString("province_name"));
                       addressResponse.setLocalDetail(rs.getString("street_address"));
                       addressResponse.setDefault(isDefault);
                       return addressResponse;
                   }
               }
           }
       }catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }


    /*
     * Lấy một địa chỉ theo addressId và userId.
     *
     * Lý do cần truyền cả userId:
     * - Tránh customer sửa URL để xem/sửa địa chỉ của người khác
     * - Ví dụ: /customer/addresses/edit?id=8
     *
     * Method này dùng cho màn edit address.
     */
    public Address getAddressByIdAndUserId(int addressId, int userId) {
        String sql = "SELECT "
                + "a.address_id, "
                + "a.user_id, "
                + "a.receiver_name, "
                + "a.receiver_phone, "
                + "a.street_address, "
                + "a.ward_id, "
                + "a.is_default, "
                + "a.created_at, "

                + "w.id AS ward_id_value, "
                + "w.province_id AS ward_province_id, "
                + "w.name AS ward_name, "
                + "w.name_with_type AS ward_name_with_type, "
                + "w.path_with_type AS ward_path_with_type, "

                + "p.id AS province_id_value, "
                + "p.name AS province_name, "
                + "p.full_name AS province_full_name, "
                + "p.type AS province_type "

                + "FROM addresses a "
                + "JOIN wards w ON w.id = a.ward_id "
                + "JOIN provinces p ON p.id = w.province_id "
                + "WHERE a.address_id = ? AND a.user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Province province = Province.builder()
                            .id(rs.getInt("province_id_value"))
                            .name(rs.getString("province_name"))
                            .fullName(rs.getString("province_full_name"))
                            .type(rs.getString("province_type"))
                            .build();

                    Ward ward = Ward.builder()
                            .id(rs.getInt("ward_id_value"))
                            .provinceId(rs.getInt("ward_province_id"))
                            .name(rs.getString("ward_name"))
                            .nameWithType(rs.getString("ward_name_with_type"))
                            .pathWithType(rs.getString("ward_path_with_type"))
                            .province(province)
                            .build();

                    Timestamp createdAt = rs.getTimestamp("created_at");

                    return Address.builder()
                            .addressId(rs.getInt("address_id"))
                            .userId(rs.getInt("user_id"))
                            .receiverName(rs.getString("receiver_name"))
                            .receiverPhone(rs.getString("receiver_phone"))
                            .streetAddress(rs.getString("street_address"))
                            .wardId(rs.getInt("ward_id"))
                            .ward(ward)
                            .isDefault(rs.getBoolean("is_default"))
                            .createdAt(createdAt == null ? null : createdAt.toLocalDateTime())
                            .build();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * Cập nhật địa chỉ giao hàng của customer.
     *
     * Nếu địa chỉ được chọn làm mặc định:
     * - Bỏ mặc định toàn bộ địa chỉ cũ của user
     * - Sau đó cập nhật địa chỉ hiện tại là mặc định
     *
     * Dùng transaction để tránh lỗi dữ liệu mặc định.
     */
    public boolean updateAddress(Address address) {
        String updateOldDefaultSql = "UPDATE addresses "
                + "SET is_default = 0 "
                + "WHERE user_id = ?";

        String updateSql = "UPDATE addresses "
                + "SET receiver_name = ?, "
                + "receiver_phone = ?, "
                + "street_address = ?, "
                + "ward_id = ?, "
                + "is_default = ? "
                + "WHERE address_id = ? AND user_id = ?";

        boolean oldAutoCommit = true;

        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            boolean addressIsDefault = Boolean.TRUE.equals(address.getIsDefault());

            if (addressIsDefault) {
                try (PreparedStatement psUpdateDefault = connection.prepareStatement(updateOldDefaultSql)) {
                    psUpdateDefault.setInt(1, address.getUserId());
                    psUpdateDefault.executeUpdate();
                }
            }

            try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
                psUpdate.setString(1, address.getReceiverName());
                psUpdate.setString(2, address.getReceiverPhone());
                psUpdate.setString(3, address.getStreetAddress());
                psUpdate.setInt(4, address.getWardId());
                psUpdate.setBoolean(5, addressIsDefault);
                psUpdate.setInt(6, address.getAddressId());
                psUpdate.setInt(7, address.getUserId());

                int affectedRows = psUpdate.executeUpdate();

                connection.commit();
                return affectedRows > 0;
            }

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            e.printStackTrace();

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
     * Xóa địa chỉ của customer.
     * Chỉ xóa nếu address_id thuộc đúng user_id đang đăng nhập.
     */
    public boolean deleteAddressByIdAndUserId(int addressId, int userId) {
        String sql = "DELETE FROM addresses "
                + "WHERE address_id = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Sau khi xóa địa chỉ mặc định,
     * set địa chỉ còn lại đầu tiên của user làm mặc định.
     */
    public boolean setFirstAddressAsDefault(int userId) {
        String sql = "UPDATE addresses "
                + "SET is_default = 1 "
                + "WHERE address_id = ( "
                + "    SELECT TOP 1 address_id "
                + "    FROM addresses "
                + "    WHERE user_id = ? "
                + "    ORDER BY created_at ASC, address_id ASC "
                + ")";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    /*
     * Cập nhật 1 địa chỉ thành mặc định thông qua API (AJAX).
     * Hàm này sẽ bỏ mặc định của tất cả địa chỉ cũ, sau đó set địa chỉ mới thành mặc định.
     */
    public boolean setDefaultAddress(int addressId, int userId) {
        String updateAllSql = "UPDATE addresses SET is_default = 0 WHERE user_id = ?";
        String updateOneSql = "UPDATE addresses SET is_default = 1 WHERE address_id = ? AND user_id = ?";
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement psAll = connection.prepareStatement(updateAllSql)) {
                psAll.setInt(1, userId);
                psAll.executeUpdate();
            }
            try (PreparedStatement psOne = connection.prepareStatement(updateOneSql)) {
                psOne.setInt(1, addressId);
                psOne.setInt(2, userId);
                psOne.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}