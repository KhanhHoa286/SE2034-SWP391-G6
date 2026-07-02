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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Lấy dữ liệu hiển thị cart cho khách hoặc customer
 */
@WebServlet("/customer/cart")
public class CartServlet extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) { // đã đăng nhập
            // xuống db lấy sản phẩm trong giỏ để hiển thị
            List<CartResponse> cartResponses = cartDAO.getCartForMember(user.getUserId());
            // lấy ra danh sách cart list
            Map<Integer, ShopCartResponse> responseMap = checkCartUser(cartResponses);
            // lấy ra tổng tiền từ các shop trong giỏ
            BigDecimal newShopAllTotal = getShopAllTotal(cartResponses);

            request.setAttribute("shopAllTotal", newShopAllTotal);
            request.setAttribute("cartDetail", responseMap);
        }
        //
        request.getRequestDispatcher("/customer/cart/list-cart-items.jsp").forward(request, response);
    }

    // kiểm tra người dùng trả về list cart
    private Map<Integer, ShopCartResponse> checkCartUser(List<CartResponse> cartResponses) {
        //
        Map<Integer, ShopCartResponse> cartResponseMap = new LinkedHashMap<>();

            // Duyệt đưa shopid vào làm key
            for (CartResponse c : cartResponses) {
                int shopId = c.getShopId();

                if (cartResponseMap.containsKey(shopId) == false) { // nếu mà trong map chưa có shopid nào như này tưc shop mới
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

    private BigDecimal getShopAllTotal(List<CartResponse> cartResponses) {
        BigDecimal newShopAllTotal = BigDecimal.ZERO;
        if(cartResponses != null) {
            for (CartResponse c : cartResponses) {
                if(c.isSelected() == true) {
                    newShopAllTotal = newShopAllTotal.add(c.getTotalPrice());
                }
            }
        }
        return newShopAllTotal;
    }
}
