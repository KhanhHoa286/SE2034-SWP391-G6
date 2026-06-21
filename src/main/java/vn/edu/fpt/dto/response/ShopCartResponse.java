package vn.edu.fpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShopCartResponse {
    private Integer shopId;
    private String shopName;

    private List<CartResponse> items = new ArrayList<>();

    // Tự động tính tổng tiền tạm tính của RIÊNG SHOP NÀY
    public BigDecimal getShopTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartResponse item : items) {
            if (item.isSelected()) { // Chỉ tính những món được tích chọn
                total = total.add(item.getTotalPrice());
            }
        }
        return total;
    }
}
