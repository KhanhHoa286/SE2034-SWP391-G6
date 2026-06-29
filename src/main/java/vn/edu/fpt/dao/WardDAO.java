package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.Ward;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WardDAO extends DBContext {

    public List<Ward> getWardsByProvinceId(int provinceId) {

        List<Ward> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM wards
                WHERE province_id = ?
                ORDER BY name
                """;

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setInt(1, provinceId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ward ward = Ward.builder()
                        .id(rs.getInt("id"))
                        .provinceId(rs.getInt("province_id"))
                        .name(rs.getString("name"))
                        .build();

                list.add(ward);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * Kiểm tra phường/xã có thật sự thuộc tỉnh/thành phố đã chọn không.
     *
     * Lý do cần check:
     * - User có thể sửa HTML trên trình duyệt
     * - Ví dụ chọn provinceId = 11 nhưng tự sửa wardId của tỉnh khác
     *
     * Nếu không check, dữ liệu địa chỉ có thể bị lệch.
     */
    public boolean isWardInProvince(int wardId, int provinceId) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM wards
            WHERE id = ?
              AND province_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, wardId);
            ps.setInt(2, provinceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}