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
public class UserVoucher {

    private Integer userVoucherId;

    private Integer userId;
    private User user;

    private Integer voucherId;
    private Voucher voucher;

    private Boolean isUsed;

    private LocalDateTime savedAt;

}