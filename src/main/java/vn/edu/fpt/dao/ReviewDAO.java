package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.response.ProductReviewResponse;
import vn.edu.fpt.dto.response.ReviewDetailResponse;
import vn.edu.fpt.model.Product;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class ReviewDAO extends DBContext {
    /**
     * HoaNK - Lấy ra dữ liệu trả veef cho trang product reivew
     */
    private final String GET_INFO_REVIEW_PRODUCT = """
    SELECT 
        p.product_id, p.product_name, p.base_price, p.discount_percentage, 
        pr.created_at, pr.rating, pr.comment, u.avatar_url, 
        (u.first_name + ' ' + u.last_name) AS user_name,
        
        -- Đếm tổng số review của bộ lọc hiện tại
        COUNT(pr.review_id) OVER() AS total_review,
        
        -- Tính trung bình sao TỔNG của sản phẩm 
        (SELECT ISNULL(AVG(rating), 0) FROM product_reviews WHERE product_id = p.product_id) AS average_rating
        
    FROM products p
    LEFT JOIN product_reviews pr ON p.product_id = pr.product_id %s
    LEFT JOIN users u ON pr.user_id = u.user_id
    WHERE p.product_id = ?
""";

    private final String PAGING_REVIEW_PRODUCT = """
    ORDER BY pr.created_at DESC
    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
""";

    public ProductReviewResponse getProductReviewList(int productId, int page, int pageSize, Integer star) {
        // Xử lý chuỗi điều kiện lọc sao động
        String starCondition = "";
        boolean checkStar = (star != null && star >= 1 && star <= 5); // ĐÃ SỬA: Lấy từ 1 đến 5 sao
        if (checkStar) {
            starCondition = " AND pr.rating = " + star;
        }

        // Định dạng lại chuỗi SQL tổng chỉnh
        String sql = String.format(GET_INFO_REVIEW_PRODUCT, starCondition) + PAGING_REVIEW_PRODUCT;
        ProductReviewResponse productReviewResponse = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            stmt.setInt(index++, productId);

            int offsetPage = (page - 1) * pageSize;
            stmt.setInt(index++, offsetPage);
            stmt.setInt(index++, pageSize); // Chỉ lấy đúng số lượng pageSize (5), không nhân với page

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    if (productReviewResponse == null) {
                        productReviewResponse = new ProductReviewResponse();
                        productReviewResponse.setProductId(rs.getInt("product_id"));
                        productReviewResponse.setProductName(rs.getString("product_name"));
                        productReviewResponse.setAverageRating(rs.getDouble("average_rating"));

                        // Nếu bộ lọc rỗng, cột total_review từ LEFT JOIN sẽ bằng 0
                        int totalReview = rs.getInt("total_review");
                        productReviewResponse.setTotalReview(totalReview);

                        int totalPage = (int) Math.ceil((double) totalReview / pageSize);
                        productReviewResponse.setTotalPage(totalPage);
                        productReviewResponse.setPageNumber(page);

                        Product product = new Product();
                        product.setBasePrice(rs.getBigDecimal("base_price"));
                        product.setDiscountPercentage(rs.getInt("discount_percentage"));
                        productReviewResponse.setPrice(product.getDiscountedPrice());
                    }

                    // Nếu mức sao này không có ai đánh giá = kết thúc
                    if (rs.getObject("rating") == null) {
                        break;
                    }

                    ReviewDetailResponse detail = new ReviewDetailResponse();
                    if (rs.getTimestamp("created_at") != null) {
                        detail.setCreatedAt(new java.util.Date(rs.getTimestamp("created_at").getTime()));
                    }
                    detail.setRating(rs.getInt("rating"));
                    detail.setComment(rs.getString("comment"));
                    detail.setAvatarUrl(rs.getString("avatar_url"));
                    detail.setUserName(rs.getString("user_name"));

                    productReviewResponse.getReviewResponse().add(detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productReviewResponse;
    }
}
