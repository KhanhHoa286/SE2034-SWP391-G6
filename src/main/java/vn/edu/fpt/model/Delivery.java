package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.DeliveryStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    private Integer deliveryId;

    private String trackingNumber;

    private Integer subOrderId;
    private SubOrder subOrder;

    private Integer shipperId;
    private User shipper;

    private DeliveryStatus status;

    private LocalDateTime assignedAt;

}