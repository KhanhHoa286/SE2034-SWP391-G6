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
import vn.edu.fpt.dao.ShopDAO;
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
 * Date: 4/7/2026
 * Description: Load ln địa chỉ nhận hàng, sản phaamr được choọn từ trang chi tiết hoặc sản phẩm được tích từ trang giỏ hàng
 * qua trang thanh toán, tiến hành thanh toán và dọn dẹp biến thể trong giỏ hàng
 */
@WebServlet("/customer/add-order")
public class AddOrderServlet extends HttpServlet {
    private final AddressDAO addressDAO = new AddressDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ShopDAO shopDAO = new ShopDAO();

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

        //load địa chỉ mặc định của user
        this.loadAddress(user.getUserId(), checkoutResponse);
        //load dữ liệu sản phẩm theo luồng CART hoặc DETAILS_PRODUCT
        this.loadProductData(request, type, user.getUserId(), checkoutResponse);

        request.setAttribute("checkoutResponse", checkoutResponse);
        request.setAttribute("type", type);
        request.getRequestDispatcher("/customer/checkout/add-order.jsp").forward(request, response);
    }

    // Load địa chỉ mặc định của user cho trang checkout
    private void loadAddress(int userId, CheckoutResponse checkoutResponse) {
        AddressResponse addressResponse = addressDAO.getAddressCheckout(userId);
//        if (addressResponse == null) {
//            addressResponse = new AddressResponse();
//        }
        checkoutResponse.setAddressResponse(addressResponse);
    }

    // Load dữ liệu sản phẩm: phân luồng theo type
    private void loadProductData(HttpServletRequest request, String type, int userId, CheckoutResponse checkoutResponse) {
        if (type == null) return;

        if ("CART".equalsIgnoreCase(type)) { // luồng giỏ hàng ăn theo list cart items id
            // luồng từ giỏ hàng, đọc danh sách cart_item_id đã chọn và bắt bằng js been front gửi sang
            String cartItemIds = request.getParameter("list_cart_item_id");
            if (cartItemIds == null || cartItemIds.trim().isEmpty()) return;

            List<CartResponse> listCartResponse = cartDAO.getCartItemCheckbox(cartItemIds, userId);
            if (listCartResponse == null || listCartResponse.isEmpty()) return;

            List<ShopCartResponse> listByShop = this.groupByShop(listCartResponse);
            if (!listByShop.isEmpty()) {
                checkoutResponse.setShopCartResponses(listByShop);
                checkoutResponse.setListCartItemIds(cartItemIds);
            }

        } else if ("DETAILS_PRODUCT".equalsIgnoreCase(type)) { // luồng chi tiết ăn theo variant Id
            // Luồng từ trang chi tiết sản phẩm — mua ngay 1 biến thể
            Integer colorId = ParamUtil.getInteger(request, "color_id");
            Integer sizeId = ParamUtil.getInteger(request, "size_id");
            Integer productId = ParamUtil.getInteger(request, "product_id");
            Integer quantity = ParamUtil.getInteger(request, "quantity");

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
        checkoutResponse.setUserId(userId);
    }

    //nhóm danh sách CartResponse theo từng shop cho phần giỏ hàng qua
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setType(request.getParameter("type"));
        checkoutRequest.setPaymentMethod(request.getParameter("payment_method"));
        checkoutRequest.setReceiverName(request.getParameter("receiver_name"));
        checkoutRequest.setReceiverPhone(request.getParameter("receiver_phone"));
        checkoutRequest.setShippingAddress(request.getParameter("shipping_address"));
        checkoutRequest.setTotalAmount(ParamUtil.getBigDecimal(request, "total_amount"));

        //kiểm tra rỗng địa chỉ
        if (checkoutRequest.getReceiverName() == null || checkoutRequest.getReceiverName().trim().isEmpty() ||
            checkoutRequest.getReceiverPhone() == null || checkoutRequest.getReceiverPhone().trim().isEmpty() ||
            checkoutRequest.getShippingAddress() == null || checkoutRequest.getShippingAddress().trim().isEmpty()) {
            
            redirectBackWithError(request, response, "empty_address");
            return;
        }

        if ("CART".equalsIgnoreCase(checkoutRequest.getType())) {
            checkoutRequest.setCartItemIds(request.getParameter("cartItemIds"));
        } else if ("DETAILS_PRODUCT".equalsIgnoreCase(checkoutRequest.getType())) {
            checkoutRequest.setVariantId(ParamUtil.getInteger(request, "variant_id"));
            checkoutRequest.setQuantity(ParamUtil.getInteger(request, "quantity_details_product"));

            if (checkoutRequest.getVariantId() == null || checkoutRequest.getQuantity() == null) {
                redirectBackWithError(request, response, "invalid_product");
                return;
            }

            //lấy thông tin biến thể để lấy productId
            SummaryOrderCheckoutResponse summary = productDAO.getVariantInfoForCheckout(checkoutRequest.getVariantId());
            if (summary == null) {
                redirectBackWithError(request, response, "invalid_product");
                return;
            }

            //kiểm tra chủ shop
            if (shopDAO.checkProductSeller(summary.getProductId(), user.getUserId())) {
                redirectBackWithError(request, response, "self_buy");
                return;
            }

            //kiểm tra số lượng tồn kho
            int currentStock = productDAO.getVariantStock(checkoutRequest.getVariantId());
            if (currentStock == 0 || checkoutRequest.getQuantity() > currentStock) {
                redirectBackWithError(request, response, "out_of_stock");
                return;
            }
        }

        // Thực hiện tạo đơn hàng trong 1 transaction
        int masterOrderId = orderDAO.createOrderTransaction(checkoutRequest, user.getUserId());

        if (masterOrderId > 0) {
            // Thành công — chuyển sang trang lịch sử đơn hàng
            response.sendRedirect(request.getContextPath() + "/customer/order-list");
        } else {
            // Thất bại — quay lại trang checkout kèm thông báo lỗi
            redirectBackWithError(request, response, "order_failed");
        }
    }

    // Quay về trang trước kèm param error
    private void redirectBackWithError(HttpServletRequest request, HttpServletResponse response, String errorCode) throws IOException {
        String referer = request.getHeader("referer");
        if (referer == null || referer.isEmpty()) {
            referer = request.getContextPath() + "/customer/add-order";
        }
        
        // Loại bỏ param error cũ nếu có
        referer = referer.replaceAll("([&?])error=[^&]*", "$1")
                         .replaceAll("&+$", "")
                         .replaceAll("\\?$", "");

        String separator = referer.contains("?") ? "&" : "?";
        response.sendRedirect(referer + separator + "error=" + errorCode);
    }
}
