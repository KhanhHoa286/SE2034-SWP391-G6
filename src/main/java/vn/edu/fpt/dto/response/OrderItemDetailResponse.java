package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDetailResponse {
    private Integer orderItemId;
    private Integer subOrderId;

    private String thumbnail;

    private Integer productId;
    private String productName;

    private Integer shopId;

    private String colorName;
    private String sizeName;

    private Integer quantity;

    private BigDecimal discountPrice;
    private boolean reviewed;

    public BigDecimal getTotalPrice() {
        if(discountPrice == null) {
            return BigDecimal.ZERO;
        }
        return discountPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
