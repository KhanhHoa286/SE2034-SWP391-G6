package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.ApprovalStatus;
import vn.edu.fpt.enums.ShopStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {

    private Integer shopId;

    private Integer ownerId;
    private User owner;

    private String shopName;
    private String logoUrl;
    private String description;
    private Integer wardId;
    private Ward ward;
    private String streetAddress;

    private ApprovalStatus approvalStatus;
    private ShopStatus status;

    private LocalDateTime createdAt;

}