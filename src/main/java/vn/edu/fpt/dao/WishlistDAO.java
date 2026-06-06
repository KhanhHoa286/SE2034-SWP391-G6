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

    /**
     * HoaNK - Thay đổi trạng thái của thêm xóa sản phẩm yeu thich
     */
        public String toggleWishlist(int userId, int productId) {
            String checkSql = "SELECT 1 FROM wishlists WHERE user_id = ? AND product_id = ?";
            String insertSql = "INSERT INTO wishlists (user_id, product_id) VALUES (?, ?)";
            String deleteSql = "DELETE FROM wishlists WHERE user_id = ? AND product_id = ?";

            //Kiểm tra xem người dùng đã thích sản phẩm này chưa
            try (PreparedStatement psCheck = connection.prepareStatement(checkSql)) {
                psCheck.setInt(1, userId);
                psCheck.setInt(2, productId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        // ĐÃ THÍCH RỒI -> Tiến hành XÓA (Delete)
                        try (PreparedStatement psDelete = connection.prepareStatement(deleteSql)) {
                            psDelete.setInt(1, userId);
                            psDelete.setInt(2, productId);
                            psDelete.executeUpdate();
                            return "DELETED";
                        }
                    } else {
                        // CHƯA THÍCH -> Tiến hành THÊM (Insert)
                        try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                            psInsert.setInt(1, userId);
                            psInsert.setInt(2, productId);
                            psInsert.executeUpdate();
                            return "INSERTED";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "ERROR";
        }
}
