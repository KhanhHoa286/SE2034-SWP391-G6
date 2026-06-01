package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.Category;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class CategoryDAO extends DBContext {
    /**
     * HoaNK - Lấy tất cả category trong db
     */
    private static final String GET_ALL_CATEGORY = """
               SELECT * FROM categories;
            """;

    public List<Category> getAllCategory() {
        List<Category> allCategory = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        String sql = GET_ALL_CATEGORY;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                // Load category cha vào
                int parentIdValue = rs.getInt("parent_id");
                if (rs.wasNull()) {
                    category.setParentId(null);
                } else {
                    category.setParentId(parentIdValue);
                }

                category.setCategoryName(rs.getString("category_name"));

                category.setListChildCategory(new ArrayList<>());
                allCategory.add(category);
            }

            // Tiến hành lồng ghép Cha - Con
            for (Category parent : allCategory) {
                if (parent.getParentId() == null) { // id cha bằng null thì đây là cha
                    for (Category child : allCategory) { // duyệt tìm con có id cha = id cha(null)
                        if (child.getParentId() != null && child.getParentId().equals(parent.getCategoryId())) {
                            parent.getListChildCategory().add(child);
                        }
                    }
                    categories.add(parent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}
