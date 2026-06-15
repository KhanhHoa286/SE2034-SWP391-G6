package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 12/06/2026
 * Description: Cộng trừ giá trị số lượng items trong giỏ hàng
 */
@WebServlet("/api/customer/update-cart")
public class UpdateCart extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();

     @Override
         protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
             //
            Integer cartItemId = ParamUtil.getInteger(request, "cart_item_id");
            Integer quantityItem = ParamUtil.getInteger(request, "quantity_item");
            Integer variantId = ParamUtil.getInteger(request, "variant_id");
            // nếu quantity < 1
         if(quantityItem < 1) {
             response.getWriter().write("INVALID_STOCK");
             return;
         }
            // lấy ra số sản phẩm trong kho của item đó tránh việc + giỏ hàng nó hơn
             int currentStock = productDAO.getVariantStock(variantId);
         // nếu quantity + mà vượt mức cái trong kho thì bắn lỗi về
           if(quantityItem > currentStock) {
               response.getWriter().write("OVER_STOCK");
               return;
           }
           // nếu không vượt quá thì cho vào update
        boolean checkUpdate = updateQuantity(request, cartItemId, quantityItem);
         // trả về cho js để load lại trang
           if(!checkUpdate) {
               response.getWriter().write("ERROR");
           }else{
               response.getWriter().write("SUCCESS");
           }
         }

        // cập nhật số lượng
    private boolean updateQuantity(HttpServletRequest request,int cartItemId, int quantity) {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if(user != null) {
            return cartDAO.updateQuantityItem(quantity, cartItemId, user.getUserId());
        }
        return false;
    }
}
