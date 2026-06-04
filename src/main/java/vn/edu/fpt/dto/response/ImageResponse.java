package vn.edu.fpt.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ImageResponse {
    private Integer imageId;

    private String imageUrl;

    private Boolean isPrimary;
}
