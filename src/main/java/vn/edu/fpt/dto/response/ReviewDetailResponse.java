package vn.edu.fpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailResponse {
    private Date createdAt;
    private Integer rating;
    private String comment;

    private String avatarUrl;
    private String userName;
}
