package vn.edu.fpt.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailResponse {
    private Date createdAt;
    private Integer rating;
    private String comment;
    private String reviewTitle;
    private String avatarUrl;
    private String userName;
}
