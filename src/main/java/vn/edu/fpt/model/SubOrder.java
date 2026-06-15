package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.SubOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubOrder {

    private Integer subOrderId;

    private Integer masterOrderId;
    private MasterOrder masterOrder;

    private Integer shopId;
    private Shop shop;

    private Integer voucherId;
    private Voucher voucher;

    private BigDecimal subTotal;

    private BigDecimal shippingFee;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private BigDecimal commissionFee;

    private SubOrderStatus status;

    private LocalDateTime createdAt;

}