package vn.edu.fpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private int cartItemId;
    private boolean selected;
    private int quantity;
    private int shopId;
    private String shopName;
    private int productId;
    private String productName;
    private String thumbnailUrl;
    private int variantId;
    private String colorName;
    private String sizeName;

    private BigDecimal discountPrice;
    private int stockQuantity;

    // tính toán giá tiền tổng sau khi lấy giá tiền sản phẩm đó * số lượng đang có trong giỏ
    public BigDecimal getTotalPrice() {
        if (discountPrice == null) return BigDecimal.ZERO;
        return discountPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
