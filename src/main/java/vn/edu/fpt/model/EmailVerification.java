package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {
    private Integer verificationId;
    private String email;
    private String otpCode;
    private LocalDateTime expiredAt;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}