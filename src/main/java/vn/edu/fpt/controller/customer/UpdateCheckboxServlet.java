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
import java.util.List;

@WebServlet("/api/customer/update-checkbox")
public class UpdateCheckboxServlet extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //
        Integer cartItemId = ParamUtil.getInteger(request, "cart_item_id");
        String isSelected = request.getParameter("is_selected");
        if (cartItemId == null || cartItemId <= 0 ) {
            response.sendRedirect(request.getContextPath() + "/customer/cart");
            return;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean checkUpdateCheckbox = cartDAO.updateCheckbox(isSelected, cartItemId);
            if (checkUpdateCheckbox == true) {
                List<CartResponse> cartResponseList = cartDAO.getCartForMember(user.getUserId());
                //
                BigDecimal totalPriceAllShop = BigDecimal.ZERO;
                //
                for(CartResponse c : cartResponseList) {
                    if(c.isSelected() == true) {
                        totalPriceAllShop = totalPriceAllShop.add(c.getTotalPrice());
                    }
                }
                //
                String shopAllTotalStr = String.format("%,d", totalPriceAllShop.longValue()) + " đ";

                response.getWriter().write("{\"status\":\"SUCCESS\"," +
                        "\"totalPriceAllShop\":\""+shopAllTotalStr+"\""
                        +"}");
            } else {
                response.getWriter().write("{\"status\":\"ERROR\"}");
            }
            //
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
