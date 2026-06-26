package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Integer subOrderId;
    private LocalDateTime createdAt;
    private String statusOrder;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private String paymentMethod;
    private String paymentStatus;

    private List<ShopOrderResponse> shopOrders = new ArrayList();

    public BigDecimal getTotalAllShop() {
        BigDecimal total = BigDecimal.ZERO;
        for(ShopOrderResponse shop : shopOrders) {
            total = total.add(shop.getShopTotal());
        }
        return total;
    }
}
