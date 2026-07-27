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

@WebServlet(urlPatterns = {"/logistics/delivery/my-orders", "/logistics/delivery/list-shipper-orders"})
public class ListShipperOrdersServlet extends HttpServlet {

    private final DeliveryDAO deliveryDAO = new DeliveryDAO();

    private static final String LIST_PAGE = "/logistics/delivery/list-shipper-orders.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-my-orders");

        if ("1".equals(request.getParameter("received"))) {
            request.setAttribute("successMessage", "Nhận đơn hàng thành công. Đơn đã được chuyển vào danh sách của bạn.");
        }

        Integer shipperId = resolveShipperId(request);
        if (shipperId == null) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            setEmptyData(request);
            request.getRequestDispatcher(LIST_PAGE).forward(request, response);
            return;
        }

        String search = trim(request.getParameter("search"));
        String status = trim(request.getParameter("status"));
        String payment = trim(request.getParameter("payment"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("payment", payment);
        request.setAttribute("sort", sort);

        try {
            deliveryDAO.loadShipperMetrics(request, shipperId);
            request.setAttribute("orders", deliveryDAO.loadShipperOrders(shipperId, search, status, payment, sort));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách đơn vận chuyển của bạn. Vui lòng kiểm tra kết nối database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(LIST_PAGE).forward(request, response);
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

    

    private void setEmptyData(HttpServletRequest request) {
        request.setAttribute("orders", List.of());
        request.setAttribute("totalOrders", 0);
        request.setAttribute("assignedOrders", 0);
        request.setAttribute("shippingOrders", 0);
        request.setAttribute("collectAmount", BigDecimal.ZERO);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class ShipperOrderRow {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal totalAmount;
        private Timestamp preparedAt;
        private Timestamp orderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private int shopId;
        private String shopName;
        private String sellerPhone;
        private String pickupAddress;
        private String productsSummary;
        private int itemCount;
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
        public Timestamp getPreparedAt() { return preparedAt; }
        public void setPreparedAt(Timestamp preparedAt) { this.preparedAt = preparedAt; }
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
        public int getShopId() { return shopId; }
        public void setShopId(int shopId) { this.shopId = shopId; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
        public String getProductsSummary() { return productsSummary; }
        public void setProductsSummary(String productsSummary) { this.productsSummary = productsSummary; }
        public int getItemCount() { return itemCount; }
        public void setItemCount(int itemCount) { this.itemCount = itemCount; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

        public BigDecimal getCollectAmount() {
            return "PAID".equals(paymentStatus) ? BigDecimal.ZERO : totalAmount;
        }
    }
}

