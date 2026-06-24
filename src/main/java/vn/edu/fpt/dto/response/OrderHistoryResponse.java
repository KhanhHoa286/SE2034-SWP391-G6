package vn.edu.fpt.dto.response;

import lombok.*;
import vn.edu.fpt.enums.SubOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Integer subOrderId;
    private String shopName;
    private LocalDateTime createdAt;
    private SubOrderStatus status;
    private BigDecimal totalAmount;
}
