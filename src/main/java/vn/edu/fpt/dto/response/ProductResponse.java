package vn.edu.fpt.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Integer productId;

    private String shopName;
    private Integer shopId;

    private String provinceName;

    private String productName;

    private BigDecimal basePrice;

    private BigDecimal finalPrice;

    private Integer discountPercentage;

    private String thumbnailUrl;

    private Integer totalStock;

    private Integer totalSold;
}
