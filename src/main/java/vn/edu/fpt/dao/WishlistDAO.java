package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class WishlistDAO extends DBContext {
    /**
     * HoaNK - Đếm số lượng sản phẩm trong wishlisst
     */
    private final String COUNT_PRODUCT_WISHLIST = "SELECT COUNT(*) FROM wishlists WHERE user_id = ?";
    public int getNumberOfProductWishlist(Integer userId) {
        String sql = COUNT_PRODUCT_WISHLIST;
        try(PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
