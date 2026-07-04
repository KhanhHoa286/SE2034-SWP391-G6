package vn.edu.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResponse {
    private AddressResponse addressResponse;
    private List<ShopCartResponse> shopCartResponses;
    private SummaryOrderCheckoutResponse summary;
    private String listCartItemIds;
    private Integer variantId;

    // tỉnh tổng tiền của tất cả các shop
    public BigDecimal getAllShopTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if(shopCartResponses != null) {
            for (ShopCartResponse item : shopCartResponses) {
                total = total.add(item.getShopTotal());
            }
        }else if(summary != null) {
            total = summary.getTotalPrice();
        }
        return total;
    }
}
