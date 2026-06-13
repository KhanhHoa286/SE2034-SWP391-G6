package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dto.response.CartResponse;
import vn.edu.fpt.dto.response.ShopCartResponse;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.*;

/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Lấy dữ liệu hiển thị cart cho khách hoặc customer
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();

     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             // lấy ra danh sách cart list
         Map<Integer, ShopCartResponse> responseMap = checkCartUser(request);

         //
         request.setAttribute("cartDetail", responseMap);
         request.getRequestDispatcher("/customer/cart/list-cart-items.jsp").forward(request,response);
         }

         // kiểm tra khách hoặc người dùng trả về list cart
    private Map<Integer, ShopCartResponse> checkCartUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //
        Map<Integer, ShopCartResponse> cartResponseMap = new LinkedHashMap<>();
        List<CartResponse> cartResponses = new ArrayList<>();
        if(user != null) { // đã đăng nhập
            cartResponses = cartDAO.getCartForMember(user.getUserId());
        }else{ // chưa đăng nhập
            // khách
        }

        // Duyệt đưa shopid vào làm key
        for(CartResponse c : cartResponses) {
            int shopId = c.getShopId();

            if(cartResponseMap.containsKey(shopId) == false) { // nếu mà trong map chưa có shopid nào như này tưc shop mới
                ShopCartResponse shopCartResponse = new ShopCartResponse();
                shopCartResponse.setShopId(shopId);
                shopCartResponse.setShopName(c.getShopName());
                cartResponseMap.put(shopId, shopCartResponse);
            }
            // sau khi có trong map rồi thì add list vào
            cartResponseMap.get(shopId).getItems().add(c);
        }
        return cartResponseMap;
    }
}
