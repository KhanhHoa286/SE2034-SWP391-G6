package vn.edu.fpt.dto.response;

import lombok.*;
import vn.edu.fpt.enums.PaymentMethod;
import vn.edu.fpt.enums.PaymentStatus;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.util.ParamUtil;

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
    private SubOrderStatus statusOrder;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private List<ShopOrderResponse> shopOrders = new ArrayList();

    public BigDecimal getTotalAllShop() {
        BigDecimal total = BigDecimal.ZERO;
        for(ShopOrderResponse shop : shopOrders) {
            total = total.add(shop.getShopTotal());
        }
        return total;
    }

    public String getDateFormatted(){
        return ParamUtil.getDate(createdAt);
    }
}
