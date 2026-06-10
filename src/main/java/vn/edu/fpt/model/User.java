package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Integer userId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passwordHash;

    @Builder.Default
    private String avatarUrl = "https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg";

    private Gender gender;
    private LocalDate dateOfBirth;

    // users.status: PENDING / ACTIVE
    private UserStatus status;

    // Thông tin shipper trong bảng users
    private String licensePlate;
    private String idCardNumber;
    private Integer shipperProvinceId;
    private Integer shipperWardId;
    private String driverLicenseFrontUrl;
    private String driverLicenseBackUrl;

    // shipper_approval_status: null / PENDING / APPROVED / REJECTED
    private String shipperApprovalStatus;

    private LocalDateTime createdAt;



}