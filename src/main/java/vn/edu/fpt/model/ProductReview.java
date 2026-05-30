package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReview {

    private Integer reviewId;

    private Integer productId;
    private Product product;

    private Integer userId;
    private User user;

    private Integer orderItemId;
    private OrderItem orderItem;

    private Integer rating;

    private String comment;

    private String replyComment;

    private LocalDateTime createdAt;

}