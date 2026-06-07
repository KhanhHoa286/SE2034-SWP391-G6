package vn.edu.fpt.controller.customer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

/**
 * HoaNK - HE195013
 * Date: 31/05/2026
 * Description: Lấy ra danh sách sản phẩm khi search, chọn nam, nữ,unisex ở navabar, lọc, xem tất cả ở home.
 */
@WebServlet(urlPatterns={"/product-list"})
public class ProductListServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private static final int PAGE_SIZE = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // dữ liệu trang product list
        loadFilterProducts(request);
        loadFilterData(request);

        //
        request.getRequestDispatcher("/public/product/list-products.jsp").forward(request, response);
    }

    // load lên danh sách sản phẩm lọc
    private void loadFilterProducts(HttpServletRequest request) {
        // Hứng dữ liệu từ bộ lọc URL
        String type = request.getParameter("type");
        String sortBy = request.getParameter("sort_by");
        String textSearch = request.getParameter("text_search");

        Integer cid = ParamUtil.getInteger(request, "cid");
        Integer provinceId = ParamUtil.getInteger(request, "province_id");

        // Xử lý trang mặc định nếu bị null
        Integer pageObj = ParamUtil.getInteger(request, "page");
        int page = (pageObj != null && pageObj > 0) ? pageObj : 1;

        // Ép kiểu phục vụ bộ lọc khoảng giá
        BigDecimal priceFrom = ParamUtil.getBigDecimal(request, "price_from");
        BigDecimal priceTo = ParamUtil.getBigDecimal(request, "price_to");

        // Tính toán phân trang
        int numberOfProduct = productDAO.getTotalProductFilter(null, type, cid, textSearch, provinceId, sortBy, priceFrom, priceTo);
        int totalPages = (int) Math.ceil((double) numberOfProduct / PAGE_SIZE);

        // Lấy danh sách sản phẩm thỏa mãn điều kiện lọc
        List<ProductResponse> listProductFilter = productDAO.getAllProductByFilter(
                null, type, cid, textSearch, provinceId, sortBy, page, PAGE_SIZE, priceFrom, priceTo
        );

        // đắp trạng thái tim đỏ yêu thích
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        wishlistDAO.setLikedForProduct(listProductFilter, user);

        // giữ trạng thái bộ lọc hiển thị
        request.setAttribute("type", type);
        request.setAttribute("categoryId", cid);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("textSearch", textSearch);
        request.setAttribute("priceFrom", priceFrom);
        request.setAttribute("priceTo", priceTo);
        request.setAttribute("provinceId", provinceId);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("listProductFilter", listProductFilter);
    }

    // load lên category và province
    private void loadFilterData(HttpServletRequest request) {
        request.setAttribute("categoryList", categoryDAO.getAllCategory());
        request.setAttribute("provinceList", provinceDAO.getAllProvinces());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}