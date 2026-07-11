package vn.edu.fpt.controller.admin;

import java.util.Date;

public class OrderHistoryDTO {
    private int masterOrderId;
    private String orderCode;       // dạng #ORD-001
    private Date createdAt;
    private double totalAmount;
    private String status;          // trạng thái hiển thị: THÀNH CÔNG, ĐANG GIAO, ĐÃ HỦY, ...
    private String paymentMethod;
    private String paymentStatus;
    private String customerName;
    private int customerId;

    public OrderHistoryDTO() {}

    // Getters & Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    // Getters & Setters
    public int getMasterOrderId() { return masterOrderId; }
    public void setMasterOrderId(int masterOrderId) { this.masterOrderId = masterOrderId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    /**
     * Trả về nhãn hiển thị tiếng Việt cho trạng thái đơn hàng.
     * Logic: Nếu tất cả sub_orders là DELIVERED → THÀNH CÔNG
     *        Nếu có sub_order là SHIPPING        → ĐANG GIAO
     *        Nếu tất cả sub_orders là CANCELLED  → ĐÃ HỦY
     *        Còn lại                             → ĐANG XỬ LÝ
     */
    public String getStatusLabel() {
        if (status == null) return "ĐANG XỬ LÝ";
        switch (status.toUpperCase()) {
            case "DELIVERED":   return "THÀNH CÔNG";
            case "SHIPPING":    return "ĐANG GIAO";
            case "CANCELLED":   return "ĐÃ HỦY";
            case "CONFIRMED":
            case "PREPARING":   return "ĐANG XỬ LÝ";
            case "PENDING":     return "CHỜ XÁC NHẬN";
            default:            return status;
        }
    }
}
