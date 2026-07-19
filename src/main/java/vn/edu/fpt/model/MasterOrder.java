package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterOrder {

    private Integer masterOrderId;

    private Integer customerId;
    private User customer;

    private BigDecimal totalAmount;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private String paymentMethod;

    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

}