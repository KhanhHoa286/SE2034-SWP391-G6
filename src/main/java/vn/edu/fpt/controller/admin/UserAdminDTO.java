package vn.edu.fpt.controller.admin;



import java.sql.Timestamp;

public class UserAdminDTO {
    private int userId;
    private String avatar;
    private String fullName;
    private String email;
    private String roleNames; // Chứa chuỗi role gộp lại VD: "ADMIN, SELLER"
    private Timestamp createdAt;
    private String status;

    public UserAdminDTO() {}

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRoleNames() { return roleNames; }
    public void setRoleNames(String roleNames) { this.roleNames = roleNames; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
