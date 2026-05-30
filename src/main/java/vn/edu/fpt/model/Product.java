package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private Integer productId;

    private Integer shopId;
    private Shop shop;

    private Integer categoryId;
    private Category category;

    private Gender gender;

    private String productName;

    private String description;

    private BigDecimal basePrice;

    private Integer discountPercentage;

    private String thumbnailUrl;

    private Boolean isActive;

    private Boolean isDeleted;

    private ProductStatus status;

    private LocalDateTime createdAt;

}