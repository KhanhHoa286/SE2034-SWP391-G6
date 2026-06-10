package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.Province;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProvinceDAO extends DBContext {

    public List<Province> getAllProvinces() {

        List<Province> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM provinces
                ORDER BY name
                """;
        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Province p = Province.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .nameSlug(rs.getString("name_slug"))
                        .fullName(rs.getString("full_name"))
                        .type(rs.getString("type"))
                        .build();

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}