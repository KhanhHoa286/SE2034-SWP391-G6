package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.request.CheckoutRequest;
import vn.edu.fpt.dto.response.*;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HoaNK - HE195013
 * Servlet xử lý trang đặt hàng (add-order):
 * - doGet : Load dữ liệu địa chỉ + sản phẩm checkout → forward vào JSP
 * - doPost: Nhận form submit → gọi transaction tạo đơn → redirect
 */
@WebServlet("/customer/add-order")
public class AddOrderServlet extends HttpServlet {
    private final AddressDAO addressDAO = new AddressDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String type = request.getParameter("type");
        CheckoutResponse checkoutResponse = new CheckoutResponse();

        // Load địa chỉ mặc định của user
        this.loadAddress(user.getUserId(), checkoutResponse);

        // Load dữ liệu sản phẩm theo luồng CART hoặc DETAILS_PRODUCT
        this.loadProductData(request, type, user.getUserId(), checkoutResponse);

        request.setAttribute("checkoutResponse", checkoutResponse);
        request.setAttribute("type", type);
        request.getRequestDispatcher("/customer/checkout/add-order.jsp").forward(request, response);
    }

    // =========================================================================
    // Load địa chỉ mặc định của user cho trang checkout
    // =========================================================================
    private void loadAddress(int userId, CheckoutResponse checkoutResponse) {
        AddressResponse addressResponse = addressDAO.getAddressCheckout(userId);
        if (addressResponse == null) {
            addressResponse = new AddressResponse();
        }
        checkoutResponse.setAddressResponse(addressResponse);
    }

    // =========================================================================
    // Load dữ liệu sản phẩm: phân luồng theo type
    // =========================================================================
    private void loadProductData(HttpServletRequest request, String type, int userId, CheckoutResponse checkoutResponse) {
        if (type == null) return;

        if ("CART".equalsIgnoreCase(type)) {
            // Luồng từ giỏ hàng — đọc danh sách cart_item_id đã chọn
            String cartItemIds = request.getParameter("list_cart_item_id");
            if (cartItemIds == null || cartItemIds.trim().isEmpty()) return;

            List<CartResponse> listCartResponse = cartDAO.getCartItemCheckbox(cartItemIds, userId);
            if (listCartResponse == null || listCartResponse.isEmpty()) return;

            List<ShopCartResponse> listByShop = this.groupByShop(listCartResponse);
            if (!listByShop.isEmpty()) {
                checkoutResponse.setShopCartResponses(listByShop);
                checkoutResponse.setListCartItemIds(cartItemIds);
            }

        } else if ("DETAILS_PRODUCT".equalsIgnoreCase(type)) {
            // Luồng từ trang chi tiết sản phẩm — mua ngay 1 biến thể
            Integer colorId = ParamUtil.getInteger(request, "color_id");
            Integer sizeId = ParamUtil.getInteger(request, "size_id");
            Integer productId = ParamUtil.getInteger(request, "product_id");
            Integer quantity = ParamUtil.getInteger(request, "quantity");

            // Null-guard: tránh NPE khi auto-unboxing Integer -> int
            if (productId == null || sizeId == null || colorId == null || quantity == null) return;

            int variantId = productDAO.getVariantById(productId, sizeId, colorId);
            if (variantId == 0) return;

            SummaryOrderCheckoutResponse summary = productDAO.getVariantInfoForCheckout(variantId);
            if (summary != null) {
                summary.setQuantity(quantity);
                checkoutResponse.setSummary(summary);
                checkoutResponse.setVariantId(variantId);
            }
        }
    }

    // =========================================================================
    // Nhóm danh sách CartResponse theo từng shop
    // =========================================================================
    private List<ShopCartResponse> groupByShop(List<CartResponse> cartResponses) {
        Map<Integer, ShopCartResponse> shopMap = new LinkedHashMap<>();
        for (CartResponse item : cartResponses) {
            int shopId = item.getShopId();
            if (!shopMap.containsKey(shopId)) {
                ShopCartResponse shopGroup = new ShopCartResponse();
                shopGroup.setShopId(shopId);
                shopGroup.setShopName(item.getShopName());
                shopMap.put(shopId, shopGroup);
            }
            shopMap.get(shopId).getItems().add(item);
        }
        return new ArrayList<>(shopMap.values());
    }

    // =========================================================================
    // Nhận form submit từ trang add-order → tạo đơn hàng
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Build CheckoutRequest từ form gửi lên
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setType(request.getParameter("type"));
        checkoutRequest.setPaymentMethod(request.getParameter("payment_method"));
        checkoutRequest.setReceiverName(request.getParameter("receiver_name"));
        checkoutRequest.setReceiverPhone(request.getParameter("receiver_phone"));
        checkoutRequest.setShippingAddress(request.getParameter("shipping_address"));
        checkoutRequest.setTotalAmount(ParamUtil.getBigDecimal(request, "total_amount"));

        if ("CART".equalsIgnoreCase(checkoutRequest.getType())) {
            // Form gửi name="cartItemIds" — chuỗi id cách nhau bằng dấu phẩy
            checkoutRequest.setCartItemIds(request.getParameter("cartItemIds"));
        } else if ("DETAILS_PRODUCT".equalsIgnoreCase(checkoutRequest.getType())) {
            checkoutRequest.setVariantId(ParamUtil.getInteger(request, "variant_id"));
            checkoutRequest.setQuantity(ParamUtil.getInteger(request, "quantity_details_product"));
        }

        // Thực hiện tạo đơn hàng trong 1 transaction
        int masterOrderId = orderDAO.createOrderTransaction(checkoutRequest, user.getUserId());

        if (masterOrderId > 0) {
            // Thành công — chuyển sang trang lịch sử đơn hàng
            response.sendRedirect(request.getContextPath() + "/customer/order-list");
        } else {
            // Thất bại — quay lại trang checkout kèm thông báo lỗi
            response.sendRedirect(request.getContextPath() + "/customer/add-order?error=order_failed");
        }
    }
}
