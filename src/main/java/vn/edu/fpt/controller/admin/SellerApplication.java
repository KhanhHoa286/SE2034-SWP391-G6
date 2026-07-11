package vn.edu.fpt.controller.admin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class SellerApplication {
    private int applicationId;
    private int userId;
    private String ownerName; // Lấy từ bảng users (first_name + last_name)
    private String shopName;
    private String businessEmail;
    private String taxCode;
    private String frontIdImage;
    private String backIdImage;
    private String status;
    private int resolvedBy;
    private Timestamp createdAt;

    public SellerApplication() {}

    // Getter đặc biệt để map với ${app.id} trên JSP
    public int getId() { return applicationId; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getBusinessEmail() { return businessEmail; }
    public void setBusinessEmail(String businessEmail) { this.businessEmail = businessEmail; }

    // Getter đặc biệt để map với ${app.mst} trên JSP
    public String getMst() { return taxCode; }
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getFrontIdImage() { return frontIdImage; }
    public void setFrontIdImage(String frontIdImage) { this.frontIdImage = frontIdImage; }

    public String getBackIdImage() { return backIdImage; }
    public void setBackIdImage(String backIdImage) { this.backIdImage = backIdImage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(int resolvedBy) { this.resolvedBy = resolvedBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Định dạng ngày hiển thị cho ${app.registeredDate} (dd/MM/yyyy)
    public String getRegisteredDate() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(createdAt);
    }

    // Định dạng giờ hiển thị cho ${app.registeredTime} (HH:mm)
    public String getRegisteredTime() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("HH:mm").format(createdAt);
    }
}
