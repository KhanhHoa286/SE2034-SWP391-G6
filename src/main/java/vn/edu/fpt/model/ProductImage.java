package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    private Integer imageId;

    private Boolean isPrimary;

    private Integer productId;
    private Product product;

    private String imageUrl;

}