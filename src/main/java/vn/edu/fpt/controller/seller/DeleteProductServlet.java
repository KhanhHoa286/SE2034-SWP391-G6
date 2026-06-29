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
        User account = (User) session.getAttribute("account");

        int ownerId = (account != null) ? account.getUserId() : -1;
        Shop shop = null;

        if (ownerId != -1) {
            shop = shopDAO.getShopByOwnerId(ownerId);
        }

        // Demo fallback: lấy shop đầu tiên nếu chưa đăng nhập/chưa có shop
        if (shop == null) {
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }

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
}
