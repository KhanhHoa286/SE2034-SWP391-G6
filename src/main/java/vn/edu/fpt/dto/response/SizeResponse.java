package vn.edu.fpt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SizeResponse {
    private int sizeId;
    private String sizeName;
}
