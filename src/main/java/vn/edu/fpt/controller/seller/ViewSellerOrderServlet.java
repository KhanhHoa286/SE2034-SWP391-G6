package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = {"/seller/order/view", "/view-seller-order"})
public class ViewSellerOrderServlet extends HttpServlet {

    private static final String ORDER_DETAIL_PAGE = "/seller/order/view-seller-order.jsp";

    private final ShopDAO shopDAO = new ShopDAO();

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

        try (Connection connection = openConnection()) {
            SellerOrderDetail orderDetail = loadOrderDetail(connection, shop.getShopId(), subOrderId);
            if (orderDetail == null) {
                request.setAttribute("errorMessage", "Khong tim thay don hang hoac don hang khong thuoc shop cua ban.");
            } else {
                orderDetail.setItems(loadOrderItems(connection, subOrderId));
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

    private SellerOrderDetail loadOrderDetail(Connection connection, int shopId, int subOrderId) throws Exception {
        String sql = """
                SELECT so.sub_order_id,
                       so.master_order_id,
                       so.shop_id,
                       s.shop_name,
                       mo.created_at AS buyer_ordered_at,
                       so.created_at AS seller_ordered_at,
                       so.status,
                       so.sub_total,
                       so.discount_amount,
                       so.total_amount,
                       so.commission_fee,
                       mo.payment_method,
                       mo.payment_status,
                       mo.transaction_code,
                       mo.bank_name,
                       mo.payment_date,
                       mo.receiver_name,
                       mo.receiver_phone,
                       mo.shipping_address,
                       u.user_id AS customer_id,
                       u.first_name + ' ' + u.last_name AS customer_name,
                       u.email AS customer_email,
                       u.phone AS customer_phone
                FROM sub_orders so
                INNER JOIN shops s ON s.shop_id = so.shop_id
                INNER JOIN master_orders mo ON mo.master_order_id = so.master_order_id
                INNER JOIN users u ON u.user_id = mo.customer_id
                WHERE so.sub_order_id = ?
                  AND so.shop_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                SellerOrderDetail detail = new SellerOrderDetail();
                detail.setSubOrderId(rs.getInt("sub_order_id"));
                detail.setMasterOrderId(rs.getInt("master_order_id"));
                detail.setShopId(rs.getInt("shop_id"));
                detail.setShopName(rs.getString("shop_name"));
                detail.setBuyerOrderedAt(rs.getTimestamp("buyer_ordered_at"));
                detail.setSellerOrderedAt(rs.getTimestamp("seller_ordered_at"));
                detail.setStatus(rs.getString("status"));
                detail.setSubTotal(rs.getBigDecimal("sub_total"));
                detail.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                detail.setTotalAmount(rs.getBigDecimal("total_amount"));
                detail.setCommissionFee(rs.getBigDecimal("commission_fee"));
                detail.setSellerReceivable(safe(rs.getBigDecimal("total_amount")).subtract(safe(rs.getBigDecimal("commission_fee"))));
                detail.setPaymentMethod(rs.getString("payment_method"));
                detail.setPaymentStatus(rs.getString("payment_status"));
                detail.setTransactionCode(rs.getString("transaction_code"));
                detail.setBankName(rs.getString("bank_name"));
                detail.setPaymentDate(rs.getTimestamp("payment_date"));
                detail.setReceiverName(rs.getString("receiver_name"));
                detail.setReceiverPhone(rs.getString("receiver_phone"));
                detail.setShippingAddress(rs.getString("shipping_address"));
                detail.setCustomerId(rs.getInt("customer_id"));
                detail.setCustomerName(rs.getString("customer_name"));
                detail.setCustomerEmail(rs.getString("customer_email"));
                detail.setCustomerPhone(rs.getString("customer_phone"));
                return detail;
            }
        }
    }

    private List<SellerOrderItem> loadOrderItems(Connection connection, int subOrderId) throws Exception {
        String sql = """
                SELECT oi.order_item_id,
                       oi.product_id,
                       oi.variant_id,
                       oi.quantity,
                       oi.price_at_purchase,
                       oi.price_at_purchase * oi.quantity AS line_total,
                       p.product_name,
                       p.thumbnail_url,
                       pv.variant_name,
                       c.color_name,
                       c.color_code,
                       sz.size_name
                FROM order_items oi
                INNER JOIN products p ON p.product_id = oi.product_id
                LEFT JOIN product_variants pv ON pv.variant_id = oi.variant_id
                LEFT JOIN colors c ON c.color_id = pv.color_id
                LEFT JOIN sizes sz ON sz.size_id = pv.size_id
                WHERE oi.sub_order_id = ?
                ORDER BY oi.order_item_id ASC
                """;

        List<SellerOrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subOrderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerOrderItem item = new SellerOrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setVariantId((Integer) rs.getObject("variant_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                    item.setLineTotal(rs.getBigDecimal("line_total"));
                    item.setProductName(rs.getString("product_name"));
                    item.setThumbnailUrl(rs.getString("thumbnail_url"));
                    item.setVariantName(rs.getString("variant_name"));
                    item.setColorName(rs.getString("color_name"));
                    item.setColorCode(rs.getString("color_code"));
                    item.setSizeName(rs.getString("size_name"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Connection openConnection() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Khong tim thay ConnectDB.properties.");
            }
            properties.load(inputStream);
        }

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("userID"),
                properties.getProperty("password")
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
        private String transactionCode;
        private String bankName;
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

        public String getTransactionCode() {
            return transactionCode;
        }

        public void setTransactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
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
