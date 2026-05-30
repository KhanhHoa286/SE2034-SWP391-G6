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
public class OrderItem {

    private Integer orderItemId;

    private Integer subOrderId;
    private SubOrder subOrder;

    private Integer productId;
    private Product product;

    private Integer variantId;
    private ProductVariant variant;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

}