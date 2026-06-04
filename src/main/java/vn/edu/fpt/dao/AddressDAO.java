package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.Province;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class AddressDAO extends DBContext {
    /**
     * HoaNK - Lấy tất cả danh sách các tỉnh
     */
    private static final String GET_ALL_PROVINCE = """
               SELECT * FROM provinces;
            """;
    public List<Province> getAllProvince() {
        List<Province> provinces = new ArrayList<>();
        String sql = GET_ALL_PROVINCE;
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Province province = new Province();
                province.setId(rs.getInt("id"));
                province.setName(rs.getString("name"));
                province.setFullName(rs.getString("full_name"));
                province.setType(rs.getString("type"));
                province.setNameSlug(rs.getString("name_slug"));
                provinces.add(province);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provinces;
    }
    }
