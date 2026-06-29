package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Ward;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO xử lý dữ liệu địa chỉ giao hàng của customer.
 *
 * File này chỉ làm việc với DB.
 * Không xử lý giao diện.
 * Không điều hướng trang.
 */
public class AddressDAO extends DBContext {

    /*
     * Lấy danh sách địa chỉ giao hàng của một customer.
     *
     * userId lấy từ session đăng nhập, không lấy từ URL.
     *
     * Bảng dùng:
     * - addresses: lưu địa chỉ giao hàng
     * - wards: lấy tên phường/xã
     * - provinces: lấy tỉnh/thành phố
     *
     * Sắp xếp:
     * - Địa chỉ mặc định lên đầu
     * - Địa chỉ mới tạo đứng trước
     */
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
}