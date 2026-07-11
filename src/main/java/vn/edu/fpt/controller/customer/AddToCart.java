package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.request.CartRequest;
import vn.edu.fpt.model.User;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đặt content type trước khi write bất kỳ response nào
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // JS gửi params với tên: product_id, color_id, size_id, quantity
        String rawProductId = request.getParameter("product_id");
        String rawColorId   = request.getParameter("color_id");
        String rawSizeId    = request.getParameter("size_id");
        String rawQuantity  = request.getParameter("quantity");

        // Kiểm tra đầu vào trước khi parse để tránh NullPointerException
        if (rawProductId == null || rawColorId == null || rawSizeId == null || rawQuantity == null) {
            response.getWriter().write("INVALID_VARIANT");
            return;
        }

        CartRequest cartRequest = new CartRequest();
        try {
            cartRequest.setQuantity(Integer.parseInt(rawQuantity.trim()));
            cartRequest.setProductId(Integer.parseInt(rawProductId.trim()));
            cartRequest.setColorId(Integer.parseInt(rawColorId.trim()));
            cartRequest.setSizeId(Integer.parseInt(rawSizeId.trim()));
        } catch (NumberFormatException e) {
            response.getWriter().write("INVALID_VARIANT");
            return;
        }

        // Lấy ra variant id tương ứng với combination màu + cỡ + sản phẩm
        int variantId = productDAO.getVariantById(cartRequest.getProductId(), cartRequest.getSizeId(), cartRequest.getColorId());
        if (variantId == 0) { // không tìm thấy variant hợp lệ
            response.getWriter().write("INVALID_VARIANT");
            return;
        }

        // Thêm vào giỏ hàng — kiểm tra kho trong hàm này
        boolean checkAddItemCart = handleMemberCart(request, response, cartRequest, variantId);

        // Vượt quá tồn kho — báo lỗi về JS để hiển thị thông báo
        if (!checkAddItemCart) {
            response.getWriter().write("OVER_STOCK");
            return;
        }

        // Thành công — trả số lượng sản phẩm hiện có trong giỏ để JS cập nhật badge
        int numberProductCart = getNumberProductCart(request);
        response.getWriter().write(String.valueOf(numberProductCart));
    }

    // Kiểm tra người dùng đã đăng nhập chưa và lưu vào giỏ hàng
    private boolean handleMemberCart(HttpServletRequest request, HttpServletResponse response, CartRequest cartRequest, int variantId) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // chưa đăng nhập thì không thêm được
        if (user == null) {
            return false;
        }
        // lấy ra số lượng hiện tại của biến thể trong kho
        int currentStock = productDAO.getVariantStock(variantId);
        // lấy ra số lượng variant đang có trong giỏ ở database
        int quantityDb = cartDAO.getQuantityAVariantCart(variantId, user.getUserId());
        // số lượng muốn thêm + số lượng đã có trong giỏ
        int targetQuantity = quantityDb + cartRequest.getQuantity();
        // nếu tổng vượt quá tồn kho thì từ chối
        if (targetQuantity > currentStock) {
            return false;
        }
        // đủ hàng — thêm vào giỏ
        cartDAO.addToCart(user.getUserId(), variantId, cartRequest.getQuantity());
        return true;
    }

    // Trả về số lượng để hiển thị động trên icon giỏ hàng
    private int getNumberProductCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return cartDAO.getNumberOfProductCart(user.getUserId());
        }
        return 0;
    }
}
