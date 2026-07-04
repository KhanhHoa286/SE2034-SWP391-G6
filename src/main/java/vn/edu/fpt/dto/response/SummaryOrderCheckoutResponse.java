package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummaryOrderCheckoutResponse {
    private Integer variantId;
    private String shopName;
    private Integer shopId;
    private Integer productId;
    private String thumbnail;
    private Integer quantity;
    private String productName;
    private BigDecimal price;
    private String sizeName;
    private String colorName;

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
