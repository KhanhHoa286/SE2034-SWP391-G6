package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
@Builder
public class ProductDetailResponse {
    private Integer productId;
    private String productName;
    private Double averageRating;
    private Integer totalReview;
    private Integer totalSold;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    private Integer discountPercentage;
    private String shopName;
    private Integer shopId;
    private String logoUrl;
    private List<SizeResponse> sizes;
    private List<ColorResponse> colors;
    private String description;
    private List<ImageResponse> urlImageDetails;
}
