package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.ShopApplicationStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopApplication {

    private Integer applicationId;

    private Integer userId;
    private User user;

    private String shopName;

    private String businessEmail;

    private String taxCode;

    private String frontIdImage;

    private String backIdImage;

    private ShopApplicationStatus status;

    private Integer resolvedBy;
    private User resolver;

    private LocalDateTime createdAt;

}