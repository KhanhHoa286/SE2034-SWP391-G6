package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.User;

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
    private final String CHECK_WISHLIST = "SELECT 1 FROM wishlists WHERE user_id = ? AND product_id = ?;";
    private final String INSERT_WISHLIST = "INSERT INTO wishlists (user_id, product_id) VALUES (?, ?);";
    private final String DELETE_WISHLIST = "DELETE FROM wishlists WHERE user_id = ? AND product_id = ?;";
        public String toggleWishlist(int userId, int productId) {
            String checkSql = CHECK_WISHLIST;
            String insertSql = INSERT_WISHLIST;
            String deleteSql = DELETE_WISHLIST;

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

    /**
     * HoaNK - Kiểm tra sản phẩm yêu thích của sản phẩm của mỗi customer
     */
    private final String GET_PRODUCTID_WISHLIST =  "SELECT product_id FROM wishlists WHERE user_id = ?;";
    private List<Integer> getProductIdWishList(int userId) {
        List<Integer> listProductId = new ArrayList<>();
        String sql = GET_PRODUCTID_WISHLIST;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    listProductId.add(rs.getInt("product_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listProductId;
    }

    /**
     * HoaNK - Hàm gán giá trị wishlist cho từng sản phẩm
      */
    public void setLikedForProduct(List<ProductResponse> responses, User user) {
        // nếu danh sách rỗng, và chưa đăng nhập thì thôi ko làm gì cả
        if(responses == null || responses.isEmpty() || user == null) {
            return;
        }
        // nếu đã đăng nhập
        int userId = user.getUserId();
        List<Integer> listProductId = this.getProductIdWishList(userId);
        for (ProductResponse p : responses) {
            if (listProductId.contains(p.getProductId())) {
                p.setLiked(true);
            }
        }
    }
}
