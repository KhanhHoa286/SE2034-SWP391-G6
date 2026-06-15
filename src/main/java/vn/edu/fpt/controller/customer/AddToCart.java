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
import java.util.ArrayList;
import java.util.List;

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
             CartRequest cartRequest = new CartRequest();
             cartRequest.setQuantity(Integer.parseInt(request.getParameter("quantity")));
             cartRequest.setProductId(Integer.parseInt(request.getParameter("productId")));
             cartRequest.setColorId(Integer.parseInt(request.getParameter("colorId")));
             cartRequest.setSizeId(Integer.parseInt(request.getParameter("sizeId")));
             // lấy ra variant id
        int variantId = productDAO.getVariantById(cartRequest.getProductId(), cartRequest.getSizeId(), cartRequest.getColorId());
        if(variantId == 0) { // lỗi hoặc ko có variant id ném về trang details
            response.getWriter().write("INVALID_VARIANT");
            return;
        }
        // thêm vào giỏ hàng
        boolean checkAddItemCart = handleMemberCart(request,response, cartRequest,variantId);

        // gửi về cho js
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        // kiểm tra thêm giỏ hàng thành công hay bị vượt quá không
        if(!checkAddItemCart) {
            response.getWriter().write("OVER_STOCK");
            return;
        }
        // lấy số lượng hiển thị trên giỏ hàng
        int numberProductCart = getNumberProductCart(request);
        response.getWriter().write(String.valueOf(numberProductCart));
    }

    // kiểm tra người dùng đã đăng nhập chưa và lưu vào giỏ hàng
    private boolean handleMemberCart(HttpServletRequest request,HttpServletResponse response, CartRequest cartRequest, int variantId) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // đã đăng nhập
        if(user != null) {
            // lấy ra số lượng hiện tại của biến thể đang định thêm
            int currentStock = productDAO.getVariantStock(variantId);
            // lấy ra số lượng variant trong giỏ ở database
            int quantityDb = cartDAO.getQuantityAVariantCart(variantId, user.getUserId());
            // số lượng muốn thêm + số lượng trong giỏ
            int targetQuantity = quantityDb + cartRequest.getQuantity();
            // nếu nó lớn hơn so lượng trong kho đang có
            if (targetQuantity > currentStock) {
                return false;
            }
            // nếu không lớn hơn thì thêm vào giỏ hàng
            cartDAO.addToCart(user.getUserId(), variantId, cartRequest.getQuantity());
            return true;
        }
        return false;
    }

    //  trả về số lượng để hiện thị động trên icon giỏ hàng
    private int getNumberProductCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null) {
            return cartDAO.getNumberOfProductCart(user.getUserId());
        }
            return 0;
    }
}
