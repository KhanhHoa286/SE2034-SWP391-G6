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
public class DeliveryLog {

    private Integer logId;

    private Integer deliveryId;
    private Delivery delivery;

    private Integer shipperId;
    private User shipper;

    private DeliveryStatus newStatus;

    private String currentLocation;

    private LocalDateTime createdAt;

}