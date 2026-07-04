package vn.edu.fpt.dto.response;

import lombok.*;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Ward;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private String fullName;
    private String phone;
    private String provinceName;
    private String wardName;
    private String localDetail;
    private boolean isDefault;
}
