package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.PayoutRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequest {

    private Integer requestId;

    private Integer shopId;
    private Shop shop;

    private BigDecimal amount;

    private String bankName;
    private String bankCode;

    private String accountNumber;
    private String accountHolderName;

    private PayoutRequestStatus status;

    private Integer resolvedBy;
    private User resolver;

    private LocalDateTime createdAt;

}