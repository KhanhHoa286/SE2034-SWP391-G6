package vn.edu.fpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * HoaNK - DTO trả về dữ liệu giỏ hàng theo từng shop
 * Dùng @Getter @Setter thay cho @Data để tránh @RequiredArgsConstructor
 * trùng với constructor thủ công bên dưới
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ShopCartResponse {
    private Integer shopId;
    private String shopName;
    private List<CartResponse> items;

    // Constructor thủ công — luôn khởi tạo items = new ArrayList<>() để tránh NPE khi gọi getItems().add()
    public ShopCartResponse() {
        this.items = new ArrayList<>();
    }

    // Tự động tính tổng tiền tạm tính của RIÊNG SHOP NÀY
    public BigDecimal getShopTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) return total;
        for (CartResponse item : items) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }
}
