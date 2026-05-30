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
public class CommissionConfig {

    private Integer configId;

    private BigDecimal commissionRate;

    private LocalDateTime effectiveDate;

    private LocalDateTime createdAt;

}