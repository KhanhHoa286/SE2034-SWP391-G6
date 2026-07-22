package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dto.request.CartRequest;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 11/6/2026
 * Description: Thêm 1 sản phẩm vào giỏ hàng
 */
@WebServlet("/api/customer/add-to-cart")
public class AddToCart extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
            //
            //product_id, color_id, size_id, quantity
            Integer productId = ParamUtil.getInteger(request, "product_id");
            Integer colorId = ParamUtil.getInteger(request, "color_id");
            Integer sizeId = ParamUtil.getInteger(request, "size_id");
            Integer quantity = ParamUtil.getInteger(request, "quantity");

            // Kiểm tra đầu vào trước khi parse để tránh NullPointerException
            if (productId == null || colorId == null || sizeId == null || quantity == null) {
                response.getWriter().write("{\"status\":\"INVALID_VARIANT\"}");
                return;
            }

            CartRequest cartRequest = new CartRequest();
            try {
                cartRequest.setQuantity(quantity);
                cartRequest.setProductId(productId);
                cartRequest.setColorId(colorId);
                cartRequest.setSizeId(sizeId);
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"status\":\"INVALID_VARIANT\"}");
                return;
            }

            // Lấy ra variant id tương ứng với combination màu + cỡ + sản phẩm
            int variantId = productDAO.getVariantById(cartRequest.getProductId(), cartRequest.getSizeId(), cartRequest.getColorId());
            if (variantId == 0) { // không tìm thấy variant hợp lệ
                response.getWriter().write("{\"status\":\"INVALID_VARIANT\"}");
                return;
            }
            // chặn chủ shop
        if(shopDAO.checkProductSeller(productId, user.getUserId())) {
            response.getWriter().write("{\"status\":\"OWN_PRODUCT\"}");
            return;
        }
            // Thêm vào giỏ hàng kiểm tra kho trong hàm này
            String checkAddItemCart = handleMemberCart(request, response, cartRequest, variantId);

            // Vượt quá tồn kho báo lỗi về JS để hiển thị thông báo
            if (!"SUCCESS".equals(checkAddItemCart)) {
                response.getWriter().write("{\"status\":\"" +checkAddItemCart +"\"}");
                return;
            }

            // Thành công — trả số lượng sản phẩm hiện có trong giỏ để JS cập nhật badge
        int numberProductCart = getNumberProductCart(user);
        response.getWriter().write("{\"status\":\"" + checkAddItemCart + "\", \"numberProductCart\":" + numberProductCart + "}");
    }

    // Kiểm tra người dùng đã đăng nhập chưa và lưu vào giỏ hàng
    private String handleMemberCart(HttpServletRequest request, HttpServletResponse response, CartRequest cartRequest, int variantId) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // lấy ra số lượng hiện tại của biến thể trong kho
        int currentStock = productDAO.getVariantStock(variantId);
        if(currentStock == 0) {
            return "OUT_OF_STOCK";
        }
        // lấy ra số lượng variant đang có trong giỏ ở database
        int quantityDb = cartDAO.getQuantityAVariantCart(variantId, user.getUserId());
        // số lượng muốn thêm + số lượng đã có trong giỏ
        int targetQuantity = quantityDb + cartRequest.getQuantity();
        // nếu tổng vượt quá tồn kho thì từ chối
        if (targetQuantity > currentStock) {
            return "OVER_STOCK";
        }
        // đủ hàng,thêm vào giỏ
        cartDAO.addToCart(user.getUserId(), variantId, cartRequest.getQuantity());
        return "SUCCESS";
    }

    // Trả về số lượng để hiển thị động trên icon giỏ hàng
    private int getNumberProductCart(User user) {
        if (user != null) {
            return cartDAO.getNumberOfProductCart(user.getUserId());
        }
        return 0;
    }
}
