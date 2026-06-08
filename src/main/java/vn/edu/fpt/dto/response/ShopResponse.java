package vn.edu.fpt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ShopResponse {
    private Integer shopId;
    private String shopName;
    private String logoUrl;
    private String fullAddress;
    private String description;
}
