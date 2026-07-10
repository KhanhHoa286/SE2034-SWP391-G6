package vn.edu.fpt.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data // Dùng Lombok để tự sinh Getter, Setter, ToString cho nhanh sạch code
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    // 1. Phân luồng hệ thống
    private String type;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private String paymentMethod;
    private BigDecimal totalAmount;

    private String cartItemIds;

    private Integer variantId;
    private Integer quantity;
}