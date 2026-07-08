package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CategoryDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.Category;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/list-seller-products")
public class ListSellerProductsServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

        String search = request.getParameter("search");
        String statusFilter = request.getParameter("status"); // all, instock, outofstock, lowstock
        Integer categoryId = ParamUtil.getInteger(request, "cid");
        Integer pageParam = ParamUtil.getInteger(request, "page");
        int currentPage = (pageParam != null && pageParam > 0) ? pageParam : 1;

        // Chuẩn hóa status filter: "all" hoặc null → không lọc
        if ("all".equals(statusFilter)) {
            statusFilter = null;
        }

        List<ProductResponse> products = java.util.Collections.emptyList();
        int totalProducts = 0;
        if (shop != null) {
            int shopId = shop.getShopId();
            products = productDAO.getSellerProductsByShopId(
                    shopId, search, statusFilter, categoryId, currentPage, PAGE_SIZE);
            totalProducts = productDAO.countSellerProducts(shopId, search, statusFilter, categoryId);
        }

        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;

        List<Category> categories = categoryDAO.getAllCategory();

        request.setAttribute("shop", shop);
        request.setAttribute("products", products);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("categories", categories);
        request.setAttribute("activePage", "products");

        // Giữ lại các giá trị filter để hiển thị trên form
        request.setAttribute("searchValue", search != null ? search : "");
        request.setAttribute("statusValue", request.getParameter("status") != null ? request.getParameter("status") : "all");
        request.setAttribute("cidValue", categoryId);

        // Forward tới trang JSP
        request.getRequestDispatcher("/seller/product/list-seller-products.jsp").forward(request, response);
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
