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

@WebServlet(urlPatterns = {"/logistics/delivery/view", "/logistics/delivery/view-delivery"})
public class ViewDeliveryServlet extends HttpServlet {

    private final DeliveryDAO deliveryDAO = new DeliveryDAO();

    private static final String VIEW_PAGE = "/logistics/delivery/view-delivery.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "delivery-list");

        String receiveError = request.getParameter("receiveError");
        if ("taken".equals(receiveError)) {
            request.setAttribute("errorMessage", "Đơn này đã được shipper khác nhận. Vui lòng chọn đơn khác.");
        } else if ("system".equals(receiveError)) {
            request.setAttribute("errorMessage", "Không thể nhận đơn hàng. Vui lòng thử lại sau.");
        }

        Integer shipperId = resolveShipperId(request);
        if (shipperId == null) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản giao hàng.");
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
            return;
        }

        Integer subOrderId = parsePositiveInt(request.getParameter("subOrderId"));
        Integer deliveryId = parsePositiveInt(request.getParameter("deliveryId"));

        if (subOrderId == null && deliveryId == null) {
            request.setAttribute("errorMessage", "Không tìm thấy mã đơn giao hàng.");
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
            return;
        }

        try {
            DeliveryDetail detail = deliveryDAO.getDeliveryDetail(subOrderId, deliveryId, shipperId);
            if (detail == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn giao hàng hoặc đơn đã được shipper khác nhận.");
            } else {
                detail.setItems(deliveryDAO.getDeliveryItems(detail.getSubOrderId()));
                request.setAttribute("deliveryDetail", detail);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải chi tiết đơn giao hàng. Vui lòng kiểm tra kết nối database.");
        }

        request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer shipperId = resolveShipperId(request);
        Integer subOrderId = parsePositiveInt(request.getParameter("subOrderId"));

        if (shipperId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (subOrderId == null) {
            response.sendRedirect(request.getContextPath() + "/logistics/delivery/list?receiveError=invalid");
            return;
        }

        try {
            int deliveryId = deliveryDAO.receiveDelivery(subOrderId, shipperId);
            if (deliveryId <= 0) {
                response.sendRedirect(request.getContextPath()
                        + "/logistics/delivery/view?subOrderId=" + subOrderId
                        + "&receiveError=taken");
                return;
            }

            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/my-orders?received=1&deliveryId=" + deliveryId);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + "/logistics/delivery/view?subOrderId=" + subOrderId
                    + "&receiveError=system");
        }
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

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String buildTrackingNumber(int subOrderId, int masterOrderId) {
        return "MODA-SUB-" + subOrderId + "-MO-" + masterOrderId;
    }

    private record DeliveryIdentity(int subOrderId, int masterOrderId) {
    }

    public static class DeliveryDetail {
        private int deliveryId;
        private String trackingNumber;
        private String deliveryStatus;
        private Timestamp assignedAt;
        private Integer shipperId;
        private int subOrderId;
        private int masterOrderId;
        private String orderStatus;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal collectAmount;
        private Timestamp preparedAt;
        private Timestamp orderedAt;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private int shopId;
        private String shopName;
        private String pickupAddress;
        private String sellerName;
        private String sellerPhone;
        private String sellerEmail;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private List<DeliveryItem> items = List.of();

        public int getDeliveryId() { return deliveryId; }
        public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public Timestamp getAssignedAt() { return assignedAt; }
        public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
        public Integer getShipperId() { return shipperId; }
        public void setShipperId(Integer shipperId) { this.shipperId = shipperId; }
        public int getSubOrderId() { return subOrderId; }
        public void setSubOrderId(int subOrderId) { this.subOrderId = subOrderId; }
        public int getMasterOrderId() { return masterOrderId; }
        public void setMasterOrderId(int masterOrderId) { this.masterOrderId = masterOrderId; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public BigDecimal getSubTotal() { return subTotal; }
        public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getCollectAmount() { return collectAmount; }
        public void setCollectAmount(BigDecimal collectAmount) { this.collectAmount = collectAmount; }
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
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        public String getSellerPhone() { return sellerPhone; }
        public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
        public String getSellerEmail() { return sellerEmail; }
        public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        public List<DeliveryItem> getItems() { return items; }
        public void setItems(List<DeliveryItem> items) { this.items = items; }
    }

    public static class DeliveryItem {
        private int orderItemId;
        private int productId;
        private Integer variantId;
        private int quantity;
        private BigDecimal priceAtPurchase;
        private BigDecimal lineTotal;
        private String productName;
        private String thumbnailUrl;
        private String variantName;
        private String colorName;
        private String sizeName;

        public int getOrderItemId() { return orderItemId; }
        public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
        public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getVariantName() { return variantName; }
        public void setVariantName(String variantName) { this.variantName = variantName; }
        public String getColorName() { return colorName; }
        public void setColorName(String colorName) { this.colorName = colorName; }
        public String getSizeName() { return sizeName; }
        public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    }
}

