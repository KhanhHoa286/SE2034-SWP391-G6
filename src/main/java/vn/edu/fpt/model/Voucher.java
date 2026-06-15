package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.VoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    private Integer voucherId;

    private Integer shopId;
    private Shop shop;

    private String voucherCode;

    private String description;

    private BigDecimal discountAmount;

    private BigDecimal minOrderValue;

    private Integer quantity;

    private LocalDateTime expirationDate;

    private VoucherStatus status;

    private Boolean isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}