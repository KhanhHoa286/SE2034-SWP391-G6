package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/delete-product")
public class DeleteProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy thông tin user đăng nhập để xác định shop
        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

        if (shop == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không tìm thấy Shop hợp lệ!");
            return;
        }

        int shopId = shop.getShopId();
        Integer productId = ParamUtil.getInteger(request, "id");

        if (productId != null) {
            boolean success = productDAO.deleteProduct(productId, shopId);
            if (success) {
                session.setAttribute("toastMessage", "Xóa sản phẩm thành công!");
                session.setAttribute("toastType", "success");
            } else {
                session.setAttribute("toastMessage", "Xóa sản phẩm thất bại!");
                session.setAttribute("toastType", "error");
            }
        } else {
            session.setAttribute("toastMessage", "Mã sản phẩm không hợp lệ!");
            session.setAttribute("toastType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/list-seller-products");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Shop resolveCurrentShop(HttpSession session) {
        Integer ownerId = getLoggedInUserId(session);
        return ownerId == null ? null : shopDAO.getShopByOwnerId(ownerId);
    }

    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        Object rawAccount = session.getAttribute("account");
        if (rawAccount instanceof User) {
            return ((User) rawAccount).getUserId();
        }

        return null;
    }
}
