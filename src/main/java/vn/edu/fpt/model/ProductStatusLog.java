package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.ProductStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatusLog {

    private Integer logId;

    private Integer productId;
    private Product product;

    private Integer actorId;
    private User actor;

    private ProductStatus oldStatus;

    private ProductStatus newStatus;

    private String note;

    private LocalDateTime createdAt;

}