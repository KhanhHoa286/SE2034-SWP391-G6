package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.WishlistDAO;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 5/6/2026
 * Description: Bật tắt thêm vào sản phẩm yêu thích
 */

@WebServlet("/toggle-wishlist")
public class ToggleWishlistServlet extends HttpServlet {
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        // Check xem đăng nhập chưa, chưa đăng nhập thì trả về chữ "UNAUTHORIZED"
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.getWriter().write("UNAUTHORIZED");
            return;
        }

        User user = (User) session.getAttribute("user");
        Integer pid = ParamUtil.getInteger(request, "product_id");

        if (pid == null || pid <= 0) {
            response.getWriter().write("ERROR");
            return;
        }

        //Gọi DAO xử lý và trả thẳng kết quả ("INSERTED" hoặc "DELETED") về cho Ajax
        String result = wishlistDAO.toggleWishlist(user.getUserId(), pid);
        response.getWriter().write(result);
    }
}
