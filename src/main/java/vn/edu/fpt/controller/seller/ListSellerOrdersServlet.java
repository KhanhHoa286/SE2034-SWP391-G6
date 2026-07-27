package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.SellerOrderListDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/seller/orders", "/list-seller-orders"})
public class ListSellerOrdersServlet extends HttpServlet {

    private static final String ORDERS_PAGE = "/seller/order/list-seller-orders.jsp";
    private static final long SELLER_TOAST_DURATION_MILLIS = 10_000L;

    private final ShopDAO shopDAO = new ShopDAO();
    private final SellerOrderListDAO sellerOrderListDAO = new SellerOrderListDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "orders");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = getLoggedInUserId(session);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Shop shop = shopDAO.getShopByOwnerId(userId);
        if (shop == null) {
            CustomerDAO customerDAO = new CustomerDAO();
            if (!customerDAO.hasCompletedSellerIdentity(userId)) {
                response.sendRedirect(request.getContextPath() + "/seller-register");
            } else {
                response.sendRedirect(request.getContextPath() + "/add-shop");
            }
            return;
        }

        String search = trim(request.getParameter("search"));
        String status = trim(request.getParameter("status"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("shop", shop);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try {
            sellerOrderListDAO.loadOrderMetrics(request, shop.getShopId());
            List<SellerOrderRow> sellerOrders = sellerOrderListDAO.loadSellerOrders( shop.getShopId(), search, status, dateRange, sort);
            request.setAttribute("sellerOrders", sellerOrders);
            sellerOrderListDAO.preparePendingOrderToast(request, shop.getShopId());
            prepareAssignedDeliveryToast(request, sellerOrders);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách đơn hàng. Vui lòng kiểm tra kết nối database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(ORDERS_PAGE).forward(request, response);
    }

    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        Object rawAccount = session.getAttribute("account");
        if (rawAccount instanceof User) {
            return ((User) rawAccount).getUserId();
        }

        return null;
    }

    

    

    private void prepareAssignedDeliveryToast(HttpServletRequest request, List<SellerOrderRow> orders) {
        HttpSession session = request.getSession(false);
        if (session == null || Boolean.TRUE.equals(session.getAttribute("sellerAssignedDeliveryToastShown"))) {
            return;
        }

        for (SellerOrderRow order : orders) {
            if (order.isShipperAssigned() && "PREPARING".equalsIgnoreCase(order.getStatus())) {
                request.setAttribute("assignedDeliveryToastSubOrderId", order.getSubOrderId());
                request.setAttribute("assignedDeliveryToastMessage",
                        "#SUB-" + order.getSubOrderId() + " đã được shipper nhận giao");
                session.setAttribute("sellerAssignedDeliveryToastSubOrderId", order.getSubOrderId());
                session.setAttribute("sellerAssignedDeliveryToastMessage",
                        "#SUB-" + order.getSubOrderId() + " đã được shipper nhận giao");
                session.setAttribute("sellerAssignedDeliveryToastExpiresAt",
                        System.currentTimeMillis() + SELLER_TOAST_DURATION_MILLIS);
                session.setAttribute("sellerAssignedDeliveryToastShown", true);
                session.removeAttribute("sellerAssignedDeliveryToastAnimated");
                return;
            }
        }
    }

    

    

    private void setEmptyData(HttpServletRequest request) {
        request.setAttribute("sellerOrders", List.of());
        request.setAttribute("totalOrders", 0);
        request.setAttribute("pendingOrders", 0);
        request.setAttribute("processingOrders", 0);
        request.setAttribute("deliveredOrders", 0);
        request.setAttribute("grossAmount", BigDecimal.ZERO);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerOrderRow {
        private int subOrderId;
        private int masterOrderId;
        private Timestamp buyerOrderedAt;
        private Timestamp sellerOrderedAt;
        private String status;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal commissionFee;
        private String paymentMethod;
        private String paymentStatus;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private String customerName;
        private String customerEmail;
        private String productsSummary;
        private int itemCount;
        private int totalQuantity;
        private boolean shipperAssigned;
        private String shipperName;
        private String shipperPhone;

        public int getSubOrderId() {
            return subOrderId;
        }

        public void setSubOrderId(int subOrderId) {
            this.subOrderId = subOrderId;
        }

        public int getMasterOrderId() {
            return masterOrderId;
        }

        public void setMasterOrderId(int masterOrderId) {
            this.masterOrderId = masterOrderId;
        }

        public Timestamp getBuyerOrderedAt() {
            return buyerOrderedAt;
        }

        public void setBuyerOrderedAt(Timestamp buyerOrderedAt) {
            this.buyerOrderedAt = buyerOrderedAt;
        }

        public Timestamp getSellerOrderedAt() {
            return sellerOrderedAt;
        }

        public void setSellerOrderedAt(Timestamp sellerOrderedAt) {
            this.sellerOrderedAt = sellerOrderedAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getSubTotal() {
            return subTotal;
        }

        public void setSubTotal(BigDecimal subTotal) {
            this.subTotal = subTotal;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getCommissionFee() {
            return commissionFee;
        }

        public void setCommissionFee(BigDecimal commissionFee) {
            this.commissionFee = commissionFee;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        public String getProductsSummary() {
            return productsSummary;
        }

        public void setProductsSummary(String productsSummary) {
            this.productsSummary = productsSummary;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public boolean isShipperAssigned() {
            return shipperAssigned;
        }

        public void setShipperAssigned(boolean shipperAssigned) {
            this.shipperAssigned = shipperAssigned;
        }

        public String getShipperName() {
            return shipperName;
        }

        public void setShipperName(String shipperName) {
            this.shipperName = shipperName;
        }

        public String getShipperPhone() {
            return shipperPhone;
        }

        public void setShipperPhone(String shipperPhone) {
            this.shipperPhone = shipperPhone;
        }
    }
}

