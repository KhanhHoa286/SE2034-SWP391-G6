package vn.edu.fpt.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ColorResponse {
    private int colorId;
    private String colorName;
}
