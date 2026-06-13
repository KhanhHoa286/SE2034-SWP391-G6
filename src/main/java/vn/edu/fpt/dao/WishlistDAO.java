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
    private final String TOGGLE_WISHLIST_QUERY = """
                                    DECLARE @uid INT = ?;
                                        DECLARE @pid INT = ?;
                                        IF EXISTS (SELECT 1 FROM wishlists WHERE user_id = @uid AND product_id = @pid)
                                        BEGIN
                                            DELETE FROM wishlists WHERE user_id = @uid AND product_id = @pid;
                                            SELECT 'DELETED' AS result;
                                        END
                                        ELSE
                                        BEGIN
                                            INSERT INTO wishlists (user_id, product_id) VALUES (@uid, @pid);
                                            SELECT 'INSERTED' AS result;
                                        END   
""";
    public String toggleWishlist(int userId, int productId) {
        // Khai báo biến trong SQL để chỉ cần truyền đúng 2 tham số đầu vào
        String sql = TOGGLE_WISHLIST_QUERY;
        // Vì câu SQL này có trả về kết quả ('DELETED' hoặc 'INSERTED') nên ta dùng executeQuery()
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Chỉ cần nạp đúng 2 tham số gọn gàng
            ps.setInt(1, userId);
            ps.setInt(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("result"); // Trả về "DELETED" hoặc "INSERTED" từ DB
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
