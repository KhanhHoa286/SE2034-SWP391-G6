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

        // 1. Lấy thông tin user đang đăng nhập
        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        int ownerId = (account != null) ? account.getUserId() : -1;
        Shop shop = null;

        if (ownerId != -1) {
            shop = shopDAO.getShopByOwnerId(ownerId);
        }

        // Nếu chưa đăng nhập hoặc chưa có shop → lấy shop đầu tiên để demo
        if (shop == null) {
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }

        // Nếu hoàn toàn không có shop nào → forward trang trống
        if (shop == null) {
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/list-seller-products.jsp").forward(request, response);
            return;
        }

        int shopId = shop.getShopId();

        // 2. Đọc các tham số lọc và phân trang từ request
        String search = request.getParameter("search");
        String statusFilter = request.getParameter("status"); // all, instock, outofstock, lowstock
        Integer categoryId = ParamUtil.getInteger(request, "cid");
        Integer pageParam = ParamUtil.getInteger(request, "page");
        int currentPage = (pageParam != null && pageParam > 0) ? pageParam : 1;

        // Chuẩn hóa status filter: "all" hoặc null → không lọc
        if ("all".equals(statusFilter)) {
            statusFilter = null;
        }

        // 3. Truy vấn danh sách sản phẩm và tổng số lượng
        List<ProductResponse> products = productDAO.getSellerProductsByShopId(
                shopId, search, statusFilter, categoryId, currentPage, PAGE_SIZE);

        int totalProducts = productDAO.countSellerProducts(shopId, search, statusFilter, categoryId);
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;

        // 4. Lấy danh sách categories cho bộ lọc dropdown
        List<Category> categories = categoryDAO.getAllCategory();

        // 5. Đẩy dữ liệu ra view
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
}
