package vn.edu.fpt.dto.request;

import lombok.*;

@Setter@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewRequest {
    private Integer productId;
    private Integer orderItemId;
    private Integer ratingStar;
    private String titleReview;
    private String commentReview;
}
