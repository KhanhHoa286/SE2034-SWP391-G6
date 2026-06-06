package vn.edu.fpt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SizeResponse {
    private Integer sizeId;
    private String sizeName;
}
