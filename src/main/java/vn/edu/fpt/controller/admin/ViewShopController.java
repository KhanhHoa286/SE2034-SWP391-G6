package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.Shop;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ViewShopController", urlPatterns = {"/admin/shop-management/detail"})
public class ViewShopController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int shopId = Integer.parseInt(request.getParameter("shopId"));
            
            ShopDAO shopDAO = new ShopDAO();
            Shop shop = shopDAO.getShopDetailById(shopId);

            if (shop == null) {
                response.sendRedirect(request.getContextPath() + "/admin/shop-management?error=notfound");
                return;
            }

            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (NumberFormatException ignored) {}
            }
            int pageSize = 5;

            ProductDAO productDAO = new ProductDAO();
            List<ProductResponse> products = productDAO.getSellerProductsByShopId(shopId, "", "all", null, page, pageSize);
            int totalProducts = productDAO.countSellerProducts(shopId, "", "all", null);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            if (totalPages == 0) totalPages = 1;

            request.setAttribute("tag", page);
            request.setAttribute("endP", totalPages);

            request.setAttribute("shop", shop);
            request.setAttribute("products", products);

            request.getRequestDispatcher("/admin/shop_mgt/view-shop-for-admin.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/shop-management?error=invalid");
        }
    }
}
