package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.response.CartResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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
            Integer shopId = ParamUtil.getInteger(request, "shop_id");
            // kiểm tra người dùng đăng nhập
         HttpSession session = request.getSession();
         User user = (User)session.getAttribute("user");
         if(user == null) { // bắn về js để điều phối đến login
             response.getWriter().write("{\"status\":\"UNAUTHORIZED\"}");
             return;
         }
            // nếu quantity < 1
         if(quantityItem < 1) {
             response.getWriter().write("{\"status\":\"INVALID_STOCK\"}");
             return;
         }
         // lấy ra số sản phẩm trong kho của item đó tránh việc + giỏ hàng nó hơn
             int currentStock = productDAO.getVariantStock(variantId);
         // nếu quantity + mà vượt mức cái trong kho thì bắn lỗi về
           if(quantityItem > currentStock) {
               response.getWriter().write("{\"status\":\"OVER_STOCK\"}");
               return;
           }
           // nếu không vượt quá thì cho vào update
        boolean checkUpdate = cartDAO.updateQuantityItem(quantityItem, cartItemId, user.getUserId());
         // trả về cho js để load lại trang
           if(!checkUpdate) {
               response.getWriter().write("{\"status\":\"ERROR\"}");
           }else {
               // nếu update thành công thì lấy ra list sản phẩm trong giỏ hàng của memeber đó
               List<CartResponse> cartResponses = cartDAO.getCartForMember(user.getUserId());

               BigDecimal newShopTotal = new BigDecimal(BigInteger.ZERO); // tiền tất cả sản phẩm biến thể trog giỏ của shop đó
               BigDecimal newAllShopTotal = new BigDecimal(BigInteger.ZERO); // tiền hcung trong giỏ tất cả sản phẩm có trong giỏ

               // duyệt qua từng cart items để cộng tiền lại => tiền chung
               if (cartResponses != null) {
                   for (CartResponse c : cartResponses) {
                       newAllShopTotal = newAllShopTotal.add(c.getTotalPrice());

                       if (c.getShopId() == shopId) {
                           newShopTotal = newShopTotal.add(c.getTotalPrice());
                       }
                   }
               }

               String shopTotalStr = "Tạm tính đơn hàng: " + String.format("%,d",newShopTotal.longValue()) + " đ";
               String shopAllTotalStr = String.format("%,d", newAllShopTotal.longValue()) + " đ";
                   // Trả về cho js
                   String jsonResponse = "{"
                           + "\"status\":\"SUCCESS\","
                           + "\"newShopTotal\":\"" + shopTotalStr + "\","
                           + "\"newAllShopTotal\":\"" + shopAllTotalStr + "\""
                           + "}";

                   response.getWriter().write(jsonResponse);
               }
     }
}
