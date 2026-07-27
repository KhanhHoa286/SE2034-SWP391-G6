package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.SellerCustomerDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/seller/customers", "/list-customers"})
public class ListCustomersServlet extends HttpServlet {

    private static final String CUSTOMERS_PAGE = "/seller/customer_mgt/list-customers.jsp";

    private final ShopDAO shopDAO = new ShopDAO();
    private final SellerCustomerDAO sellerCustomerDAO = new SellerCustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("activePage", "customers");

        Shop shop = resolveSellerShop(request);
        if (shop == null) {
            request.setAttribute("errorMessage", "Vui long dang nhap bang tai khoan seller da co shop.");
            setEmptyData(request);
            request.getRequestDispatcher(CUSTOMERS_PAGE).forward(request, response);
            return;
        }

        String search = trim(request.getParameter("search"));
        String segment = trim(request.getParameter("segment"));
        String dateRange = trim(request.getParameter("dateRange"));
        String sort = trim(request.getParameter("sort"));

        request.setAttribute("shop", shop);
        request.setAttribute("search", search);
        request.setAttribute("segment", segment);
        request.setAttribute("dateRange", dateRange);
        request.setAttribute("sort", sort);

        try {
            sellerCustomerDAO.loadCustomerMetrics(request, shop.getShopId());
            request.setAttribute("customers", sellerCustomerDAO.loadCustomers( shop.getShopId(), search, segment, dateRange, sort));
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Khong the tai danh sach khach hang. Vui long kiem tra ket noi database.");
            setEmptyData(request);
        }

        request.getRequestDispatcher(CUSTOMERS_PAGE).forward(request, response);
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

    

    

    

    

    private void setEmptyData(HttpServletRequest request) {
        request.setAttribute("customers", List.of());
        request.setAttribute("totalCustomers", 0);
        request.setAttribute("totalCustomerRevenue", BigDecimal.ZERO);
        request.setAttribute("returningCustomers", 0);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static class SellerCustomerRow {
        private int customerId;
        private String customerName;
        private String email;
        private String phone;
        private String avatarUrl;
        private String accountStatus;
        private int totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private int activeOrders;
        private int deliveredOrders;
        private int cancelledOrders;
        private Timestamp firstOrderAt;
        private Timestamp lastOrderAt;
        private int purchasedProducts;
        private int totalQuantity;
        private Integer lastSubOrderId;
        private String lastOrderStatus;
        private BigDecimal lastOrderAmount;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public BigDecimal getTotalSpent() {
            return totalSpent;
        }

        public void setTotalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
        }

        public BigDecimal getAverageOrderValue() {
            return averageOrderValue;
        }

        public void setAverageOrderValue(BigDecimal averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
        }

        public int getActiveOrders() {
            return activeOrders;
        }

        public void setActiveOrders(int activeOrders) {
            this.activeOrders = activeOrders;
        }

        public int getDeliveredOrders() {
            return deliveredOrders;
        }

        public void setDeliveredOrders(int deliveredOrders) {
            this.deliveredOrders = deliveredOrders;
        }

        public int getCancelledOrders() {
            return cancelledOrders;
        }

        public void setCancelledOrders(int cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
        }

        public Timestamp getFirstOrderAt() {
            return firstOrderAt;
        }

        public void setFirstOrderAt(Timestamp firstOrderAt) {
            this.firstOrderAt = firstOrderAt;
        }

        public Timestamp getLastOrderAt() {
            return lastOrderAt;
        }

        public void setLastOrderAt(Timestamp lastOrderAt) {
            this.lastOrderAt = lastOrderAt;
        }

        public int getPurchasedProducts() {
            return purchasedProducts;
        }

        public void setPurchasedProducts(int purchasedProducts) {
            this.purchasedProducts = purchasedProducts;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public Integer getLastSubOrderId() {
            return lastSubOrderId;
        }

        public void setLastSubOrderId(Integer lastSubOrderId) {
            this.lastSubOrderId = lastSubOrderId;
        }

        public String getLastOrderStatus() {
            return lastOrderStatus;
        }

        public void setLastOrderStatus(String lastOrderStatus) {
            this.lastOrderStatus = lastOrderStatus;
        }

        public BigDecimal getLastOrderAmount() {
            return lastOrderAmount;
        }

        public void setLastOrderAmount(BigDecimal lastOrderAmount) {
            this.lastOrderAmount = lastOrderAmount;
        }
    }
}

