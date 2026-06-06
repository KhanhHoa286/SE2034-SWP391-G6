package vn.edu.fpt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ColorResponse {
    private Integer colorId;
    private String colorName;
}
