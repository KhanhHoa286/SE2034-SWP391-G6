package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.CartRequest;
import vn.edu.fpt.dto.response.CartResponse;
import vn.edu.fpt.model.Product;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class CartDAO extends DBContext {
    /**
     * HoaNK - Đếm số lượng sản phẩm trong giỏ hàng của user
     */
    private final String COUNT_PRODUCT_CART = "SELECT COUNT(*) FROM cart_items WHERE user_id = ?";
    public int getNumberOfProductCart(Integer userId) {
        String sql = COUNT_PRODUCT_CART;
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
     * HoaNK - Kiểm tra xem giỏ hàng có chưa có rồi thì update, chư thì thêm sản phẩm mới vào giỏ hàng
     */
    private final String EDIT_ITEM_CART = """
     MERGE INTO cart_items AS target
                 USING (SELECT ? AS user_id, ? AS variant_id) AS source
                 ON (target.user_id = source.user_id AND target.variant_id = source.variant_id)
                 WHEN MATCHED THEN
                     UPDATE SET quantity = target.quantity + ?
                 WHEN NOT MATCHED THEN
                     INSERT (user_id, variant_id, quantity)
                     VALUES (?, ?, ?);
    """;
    public void addToCart(int userId, int variantId, int quantity) {
        String sql = EDIT_ITEM_CART;
        try(PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, variantId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, userId);
            stmt.setInt(5, variantId);
            stmt.setInt(6, quantity);
            stmt.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HoaNK - Lấy ra số lượng của 1 biến thế trong giỏ hàng để phục vụ cho so sánh
     */
    private final String GET_QUANTITY_VARIANT_CART = """
            SELECT quantity FROM cart_items WHERE variant_id = ? AND user_id = ?;
            """;
    public int getQuantityAVariantCart(int variantId, int userId) {
        String sql = GET_QUANTITY_VARIANT_CART;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            stmt.setInt(2,userId);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HoaNK - Lấy ra các tham số để hiện thị lên trang giỏ hàng đưa vào biến CartResponse để trả về cho bên jsp
     */
    // Các trường thông tin của Sản phẩm, Biến thể và Shop
    private static final String SELECT_PRODUCT_INFO =
            " v.variant_id, s.shop_id, s.shop_name, p.product_id, p.product_name, p.thumbnail_url, " +
                    " co.color_name, sz.size_name, v.price AS variant_price, p.discount_percentage, v.stock_quantity ";

    // Các lệnh JOIN bảng để lấy thông tin sản phẩm
    private static final String INFO_CART_PRODUCT =
                    " JOIN products p ON v.product_id = p.product_id " +
                    " JOIN shops s ON p.shop_id = s.shop_id " +
                    " JOIN colors co ON v.color_id = co.color_id " +
                    " JOIN sizes sz ON v.size_id = sz.size_id ";

    // lấy ra danh sách cho người dùng đã đăng nhập
    public List<CartResponse> getCartForMember(int userId) {
        List<CartResponse> cartResponses = new ArrayList<>();
        String sql = "SELECT c.cart_item_id, c.quantity, c.is_selected, " + SELECT_PRODUCT_INFO
                + " FROM cart_items c "
                + " JOIN product_variants v ON c.variant_id = v.variant_id "
                + INFO_CART_PRODUCT
                + " WHERE c.user_id = ? "
                + " ORDER BY s.shop_id, c.added_at DESC";

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    CartResponse cartResponse = buildCartResponse(rs);
                    cartResponse.setSelected(rs.getBoolean("is_selected"));
                    cartResponse.setCartItemId(rs.getInt("cart_item_id"));
                    cartResponse.setQuantity(rs.getInt("quantity"));
                    cartResponses.add(cartResponse);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
       return cartResponses;
    }

    // lấy dữ liệu hiển thị cart cho gúet
//    public List<CartResponse> getCartForGuest(List<Integer> variantIds) {
//        if (variantIds == null || variantIds.isEmpty()) return new ArrayList<>();
//
//        // Tạo các dấu hỏi chấm động (?, ?, ?) cho lệnh IN
//        String placeholders = String.join(",", Collections.nCopies(variantIds.size(), "?"));
//
//        // Lắp ghép câu SQL hoàn chỉnh cho Guest: Không hề dính dáng tới bảng cart_items
//        String sql = "SELECT " + SELECT_PRODUCT_INFO
//                + FROM_PRODUCT_TABLES
//                + " WHERE v.variant_id IN (" + placeholders + ")"
//                + " ORDER BY s.shop_id";
//
//        // Chạy PreparedStatement nạp list variantIds vào...
//        // Lấy dữ liệu lên thì set quantity ngầm từ Session sang như anh em mình bàn ở câu trước.
//    }

    // build 1 đối tượng cartresspone
    private CartResponse buildCartResponse(ResultSet rs) throws SQLException {
        CartResponse item = new CartResponse();

        // Map những trường CHUNG mà cả Member và Guest đều có trong câu SQL
        item.setVariantId(rs.getInt("variant_id"));
        item.setShopId(rs.getInt("shop_id"));
        item.setShopName(rs.getNString("shop_name"));
        item.setProductId(rs.getInt("product_id"));
        item.setProductName(rs.getNString("product_name"));
        item.setThumbnailUrl(rs.getString("thumbnail_url"));
        item.setColorName(rs.getNString("color_name"));
        item.setSizeName(rs.getString("size_name"));
        item.setStockQuantity(rs.getInt("stock_quantity"));

        // Sử dụng hàm tính giá sau giảm từ product model
        Product p = new Product();
        p.setBasePrice(rs.getBigDecimal("variant_price"));
        p.setDiscountPercentage(rs.getInt("discount_percentage"));
        item.setDiscountPrice(p.getDiscountedPrice());

        return item;
    }
}

