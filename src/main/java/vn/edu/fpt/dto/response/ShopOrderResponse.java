package vn.edu.fpt.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopOrderResponse {
    private Integer shopId;
    private String shopName;

    private List<OrderItemDetailResponse> items = new ArrayList();

    public BigDecimal getShopTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDetailResponse item : items) {
                total = total.add(item.getTotalPrice());
        }
        return total;
    }
}
