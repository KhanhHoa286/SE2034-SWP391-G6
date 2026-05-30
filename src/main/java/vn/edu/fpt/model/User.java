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
    private String avatarUrl;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Integer roleId;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;

}