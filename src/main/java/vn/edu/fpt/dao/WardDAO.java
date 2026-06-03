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
}