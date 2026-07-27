package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.SellerOrderDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/seller/order/view", "/view-seller-order"})
public class ViewSellerOrderServlet extends HttpServlet {

    private static final String ORDER_DETAIL_PAGE = "/seller/order/view-seller-order.jsp";

    private final ShopDAO shopDAO = new ShopDAO();
    private final SellerOrderDAO sellerOrderDAO = new SellerOrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "orders");
        if ("1".equals(trim(request.getParameter("statusUpdated")))) {
            request.setAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công.");
        }

        Shop shop = resolveSellerShop(request);
        if (shop == null) {
            request.setAttribute("errorMessage", "Vui long dang nhap bang tai khoan seller da co shop.");
            request.getRequestDispatcher(ORDER_DETAIL_PAGE).forward(request, response);
            return;
        }

        Integer subOrderId = parseSubOrderId(request);
        if (subOrderId == null) {
            request.setAttribute("errorMessage", "Ma don hang khong hop le.");
            request.getRequestDispatcher(ORDER_DETAIL_PAGE).forward(request, response);
            return;
        }

        request.setAttribute("shop", shop);

        try {
            SellerOrderDetail orderDetail = sellerOrderDAO.getOrderDetail(shop.getShopId(), subOrderId);
            if (orderDetail == null) {
                request.setAttribute("errorMessage", "Khong tim thay don hang hoac don hang khong thuoc shop cua ban.");
            } else {
                
                request.setAttribute("orderDetail", orderDetail);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Khong the tai chi tiet don hang. Vui long kiem tra ket noi database.");
        }

        request.getRequestDispatcher(ORDER_DETAIL_PAGE).forward(request, response);
    }

    private Shop resolveSellerShop(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object accountObject = session.getAttribute("account");
        if (!(accountObject instanceof User)) {
            accountObject = session.getAttribute("user");
        }

        if (!(accountObject instanceof User account) || account.getUserId() == null) {
            return null;
        }

        return shopDAO.getShopByOwnerId(account.getUserId());
    }

    private Integer parseSubOrderId(HttpServletRequest request) {
        String rawValue = trim(request.getParameter("subOrderId"));
        if (rawValue.isBlank()) {
            rawValue = trim(request.getParameter("id"));
        }

        try {
            int value = Integer.parseInt(rawValue);
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    

    

    

    

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerOrderDetail {
        private int subOrderId;
        private int masterOrderId;
        private int shopId;
        private String shopName;
        private Timestamp buyerOrderedAt;
        private Timestamp sellerOrderedAt;
        private String status;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal commissionFee;
        private BigDecimal sellerReceivable;
        private String paymentMethod;
        private String paymentStatus;
        private Timestamp paymentDate;
        private String receiverName;
        private String receiverPhone;
        private String shippingAddress;
        private int customerId;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private List<SellerOrderItem> items = List.of();

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

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
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

        public BigDecimal getSellerReceivable() {
            return sellerReceivable;
        }

        public void setSellerReceivable(BigDecimal sellerReceivable) {
            this.sellerReceivable = sellerReceivable;
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

        public Timestamp getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(Timestamp paymentDate) {
            this.paymentDate = paymentDate;
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

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
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

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public List<SellerOrderItem> getItems() {
            return items;
        }

        public void setItems(List<SellerOrderItem> items) {
            this.items = items;
        }
    }

    public static class SellerOrderItem {
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
        private String colorCode;
        private String sizeName;

        public int getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(int orderItemId) {
            this.orderItemId = orderItemId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public Integer getVariantId() {
            return variantId;
        }

        public void setVariantId(Integer variantId) {
            this.variantId = variantId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPriceAtPurchase() {
            return priceAtPurchase;
        }

        public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
            this.priceAtPurchase = priceAtPurchase;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }

        public void setLineTotal(BigDecimal lineTotal) {
            this.lineTotal = lineTotal;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getVariantName() {
            return variantName;
        }

        public void setVariantName(String variantName) {
            this.variantName = variantName;
        }

        public String getColorName() {
            return colorName;
        }

        public void setColorName(String colorName) {
            this.colorName = colorName;
        }

        public String getColorCode() {
            return colorCode;
        }

        public void setColorCode(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getSizeName() {
            return sizeName;
        }

        public void setSizeName(String sizeName) {
            this.sizeName = sizeName;
        }
    }
}

