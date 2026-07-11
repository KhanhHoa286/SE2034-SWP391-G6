package vn.edu.fpt.controller.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.model.MasterOrder;
import vn.edu.fpt.model.SubOrder;
import vn.edu.fpt.model.OrderItem;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductVariant;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalOrderDetailDTO {
    private MasterOrder masterOrder;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Each SubOrder, along with its Shop and List of OrderItems
    private List<SubOrderDetail> subOrders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubOrderDetail {
        private SubOrder subOrder;
        private Shop shop;
        private List<OrderItemDetail> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {
        private OrderItem orderItem;
        private Product product;
        private ProductVariant variant;
        private String colorName;
        private String sizeName;
        private java.math.BigDecimal subTotal;
    }
}
