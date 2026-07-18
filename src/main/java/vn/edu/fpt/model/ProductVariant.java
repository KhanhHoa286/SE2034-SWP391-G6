package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    private Integer variantId;

    private Integer productId;
    private Product product;

    private Integer colorId;
    private Color color;

    private Integer sizeId;
    private Size size;

    private String variantName;

    private Integer stockQuantity;

}