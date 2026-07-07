package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dto.response.CartResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 13/06/2026
 * Description: Xóa 1 items trong giỏ hàng bằng cartItemId và userId
 */

@WebServlet("/api/customer/delete-cart-item")
public class DeleteCartItem extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();
     @Override
         protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             response.setContentType("text/plain");
             response.setCharacterEncoding("UTF-8");
         //
         Integer cartItemId = ParamUtil.getInteger(request, "cart_item_id");
         //
         HttpSession session = request.getSession();
         User user = (User)session.getAttribute("user");
         if(user != null) { // đăng nhập rồi thì lấy id ra và gọi hàm xóa item cart đó theo cartItemId và userId
            int userId = user.getUserId();
           boolean checkDelete =  cartDAO.deleteCartItem(cartItemId, userId);
            if(checkDelete){
                // nếu update thành công thì lấy ra list sản phẩm trong giỏ hàng của memeber đó
                List<CartResponse> cartResponses = cartDAO.getCartForMember(user.getUserId());

                BigDecimal newAllShopTotal = new BigDecimal(BigInteger.ZERO); // tiền hcung trong giỏ tất cả sản phẩm có trong giỏ

                // duyệt qua từng cart items để cộng tiền lại => tiền chung
                if (cartResponses != null) {
                    for (CartResponse c : cartResponses) {
                        newAllShopTotal = newAllShopTotal.add(c.getTotalPrice());
                    }
                }

                String shopAllTotalStr = String.format("%,d", newAllShopTotal.longValue()) + " đ";
                // Trả về cho js
                String jsonResponse = "{"
                        + "\"status\":\"SUCCESS\","
                        + "\"newAllShopTotal\":\"" + shopAllTotalStr + "\""
                        + "}";

                response.getWriter().write(jsonResponse);
            }else {
                response.getWriter().write("{\"status\":\"ERROR\"}");
            }
         }else{
             response.getWriter().write("{\"status\":\"UNAUTHORIZED\"}");
         }
         //
         }
}
