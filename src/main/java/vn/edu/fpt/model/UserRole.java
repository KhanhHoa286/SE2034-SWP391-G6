package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    private Integer userRoleId;

    private Integer userId;
    private Integer roleId;

    private User user;
    private Role role;
}
