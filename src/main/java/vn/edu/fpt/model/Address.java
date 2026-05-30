package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private Integer addressId;

    private Integer userId;
    private User user;

    private String receiverName;
    private String receiverPhone;
    private String streetAddress;

    private Integer wardId;
    private Ward ward;

    private Boolean isDefault;
    private LocalDateTime createdAt;

}