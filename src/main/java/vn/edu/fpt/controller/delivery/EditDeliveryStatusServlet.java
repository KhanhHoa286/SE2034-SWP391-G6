package vn.edu.fpt.controller.delivery;

import vn.edu.fpt.dao.DeliveryDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/logistics/delivery/status", "/logistics/delivery/edit-delivery-status"})
public class EditDeliveryStatusServlet extends HttpServlet {

    private final DeliveryDAO deliveryDAO = new DeliveryDAO();

    private static final String STATUS_PAGE = "/logistics/delivery/edit-delivery-status.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-my-orders");

        if ("1".equals(request.getParameter("updated"))) {
            request.setAttribute("successMessage", "Cập nhật trạng thái giao hàng thành công.");
        }

        renderStatusPage(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-my-orders");

        Integer shipperId = resolveShipperId(request);
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));
        String newStatus = trim(request.getParameter("newStatus")).toUpperCase();

        if (shipperId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (deliveryId == null) {
            renderStatusPage(request, response, "Mã vận đơn không hợp lệ.");
            return;
        }

        if (!"DELIVERED".equals(newStatus)) {
            renderStatusPage(request, response, "Trạng thái mới không hợp lệ.");
            return;
        }

        try {
            DeliveryStatusDetail detail = deliveryDAO.getStatusDetail(deliveryId, shipperId);
            if (detail == null) {
                renderStatusPage(request, response, "Không tìm thấy đơn vận chuyển hoặc đơn không thuộc tài khoản giao hàng của bạn.");
                return;
            }

            if (!canMarkDelivered(detail)) {
                renderLoadedPage(request, response, detail,
                        "Chỉ có thể cập nhật đã giao khi người bán đã chuyển đơn sang trạng thái đang giao.");
                return;
            }

            boolean updated = deliveryDAO.markDelivered(detail, shipperId);
            if (!updated) {
                renderStatusPage(request, response, "Trạng thái đơn đã thay đổi. Vui lòng tải lại và thử lại.");
                return;
            }

            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/status?deliveryId=" + deliveryId
                    + "&updated=1");
        } catch (Exception ex) {
            ex.printStackTrace();
            renderStatusPage(request, response, "Không thể cập nhật trạng thái giao hàng. Vui lòng kiểm tra kết nối database.");
        }
    }

    private void renderStatusPage(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("activePage", "delivery-my-orders");

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        Integer shipperId = resolveShipperId(request);
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));

        if (shipperId == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        if (deliveryId == null) {
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "Mã vận đơn không hợp lệ.");
            }
            request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
            return;
        }

        try {
            DeliveryStatusDetail detail = deliveryDAO.getStatusDetail(deliveryId, shipperId);
            if (detail == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn vận chuyển hoặc đơn không thuộc tài khoản giao hàng của bạn.");
            } else {
                renderLoadedPage(request, response, detail, errorMessage);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải đơn vận chuyển. Vui lòng kiểm tra kết nối database.");
        }

        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    private void renderLoadedPage(HttpServletRequest request, HttpServletResponse response,
                                  DeliveryStatusDetail detail, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("deliveryStatus", detail);
        request.setAttribute("statusSteps", buildStatusSteps(detail));
        request.setAttribute("canMarkDelivered", canMarkDelivered(detail));
        request.setAttribute("lockedMessage", buildLockedMessage(detail));
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        request.getRequestDispatcher(STATUS_PAGE).forward(request, response);
    }

    

    

    

    private boolean canMarkDelivered(DeliveryStatusDetail detail) {
        if (detail == null) {
            return false;
        }
        boolean orderIsShipping = "SHIPPING".equals(detail.getOrderStatus());
        boolean deliveryCanClose = "ASSIGNED".equals(detail.getDeliveryStatus())
                || "PICKED_UP".equals(detail.getDeliveryStatus())
                || "IN_TRANSIT".equals(detail.getDeliveryStatus());
        return orderIsShipping && deliveryCanClose;
    }

    private String buildLockedMessage(DeliveryStatusDetail detail) {
        if (detail == null) {
            return "";
        }
        if ("DELIVERED".equals(detail.getDeliveryStatus()) || "DELIVERED".equals(detail.getOrderStatus())) {
            return "Đơn vận chuyển này đã hoàn tất giao hàng.";
        }
        if ("FAILED".equals(detail.getDeliveryStatus())) {
            return "Đơn vận chuyển này đang ở trạng thái giao thất bại.";
        }
        if (!"SHIPPING".equals(detail.getOrderStatus())) {
            return "Người bán chưa chuyển đơn sang trạng thái đang giao, nên chưa thể xác nhận đã giao cho người mua.";
        }
        return "";
    }

    private List<StatusStep> buildStatusSteps(DeliveryStatusDetail detail) {
        String deliveryStatus = detail.getDeliveryStatus();
        String orderStatus = detail.getOrderStatus();
        List<StatusStep> steps = new ArrayList<>();
        steps.add(new StatusStep("Đã nhận đơn", "ASSIGNED".equals(deliveryStatus)
                || "PICKED_UP".equals(deliveryStatus)
                || "IN_TRANSIT".equals(deliveryStatus)
                || "DELIVERED".equals(deliveryStatus),
                "Shipper đã nhận đơn từ danh sách chung."));
        steps.add(new StatusStep("Người bán bàn giao", "SHIPPING".equals(orderStatus)
                || "DELIVERED".equals(orderStatus),
                "Người bán đã chuyển đơn sang trạng thái đang giao."));
        steps.add(new StatusStep("Đã giao cho người mua", "DELIVERED".equals(deliveryStatus)
                || "DELIVERED".equals(orderStatus),
                "Người mua đã nhận hàng, đơn vận chuyển hoàn tất."));
        return steps;
    }

    private Integer resolveShipperId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object userObject = session.getAttribute("user");
        if (userObject instanceof User user && user.getUserId() != null) {
            return user.getUserId();
        }

        Object userIdObject = session.getAttribute("userId");
        if (userIdObject == null) {
            return null;
        }

        try {
            if (userIdObject instanceof Integer) {
                return (Integer) userIdObject;
            }
            return Integer.parseInt(userIdObject.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    

    private Integer parsePositiveInt(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        try {
            int value = Integer.parseInt(rawValue.trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static class DeliveryStatusDetail {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private BigDecimal collectAmount;
        private Timestamp orderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private String shopName;
        private String sellerPhone;
        private String pickupAddress;
        private String productsSummary;
        private int totalQuantity;

        public int getDeliveryId() { return deliveryId; }
        public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public Timestamp getAssignedAt() { return assignedAt; }
        public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
        public int getSubOrderId() { return subOrderId; }
        public void setSubOrderId(int subOrderId) { this.subOrderId = subOrderId; }
        public int getMasterOrderId() { return masterOrderId; }
        public void setMasterOrderId(int masterOrderId) { this.masterOrderId = masterOrderId; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getCollectAmount() { return collectAmount; }
        public void setCollectAmount(BigDecimal collectAmount) { this.collectAmount = collectAmount; }
        public Timestamp getOrderedAt() { return orderedAt; }
        public void setOrderedAt(Timestamp orderedAt) { this.orderedAt = orderedAt; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
        public String getProductsSummary() { return productsSummary; }
        public void setProductsSummary(String productsSummary) { this.productsSummary = productsSummary; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    }

    public static class StatusStep {
        private final String title;
        private final boolean completed;
        private final String description;

        public StatusStep(String title, boolean completed, String description) {
            this.title = title;
            this.completed = completed;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getDescription() {
            return description;
        }
    }
}

