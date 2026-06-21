package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewResponse {
    private Integer productId;
    private String productName;
    private BigDecimal price;

    private Double averageRating;
    private Integer totalReview;
    private List<ReviewDetailResponse> reviewResponse = new ArrayList<>();
    private int pageNumber;
    private int totalPage;
}
