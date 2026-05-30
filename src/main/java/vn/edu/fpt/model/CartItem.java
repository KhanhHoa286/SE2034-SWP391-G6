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
public class CartItem {

    private Integer cartItemId;

    private Integer userId;
    private User user;

    private Integer productId;
    private Product product;

    private Integer variantId;
    private ProductVariant variant;

    private Integer quantity;

    private Boolean isSelected;

    private LocalDateTime addedAt;

}