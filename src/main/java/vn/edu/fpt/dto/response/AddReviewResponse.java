package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddReviewResponse {
    private Integer productId;
    private Integer orderItemId;
    private Integer subOrderId;
    private String thumbnail;
    private String productName;
    private BigDecimal discountedPrice;
}
