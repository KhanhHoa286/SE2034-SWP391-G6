package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerWallet {

    private Integer walletId;

    private Integer shopId;
    private Shop shop;

    private BigDecimal availableBalance;

    private BigDecimal pendingBalance;

    private LocalDateTime updatedAt;

}