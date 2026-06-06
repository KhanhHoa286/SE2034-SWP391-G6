package vn.edu.fpt.controller.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CategoryDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.Category;
import vn.edu.fpt.model.ProductReview;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.util.ParamUtil;

/**
 * HoaNK - HE195013
 * Date: 31/05/2026
 * Description: Load lên danh sách tỉnh, category, lọc sản phẩm, gửi trả trạng thái về bên jsp
 */
@WebServlet(urlPatterns={"/product-list"})
public class ProductListServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private static final int PAGE_SIZE = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // hứng dữ liệu
        String type = request.getParameter("type");
        String sortBy = request.getParameter("sort_by");
        String textSearch = request.getParameter("text_search");
        //
        Integer cid = ParamUtil.getInteger(request, "cid");
        Integer provinceId = ParamUtil.getInteger(request, "province_id");

        // mặc định là 1 nếu bị null
        Integer pageObj = ParamUtil.getInteger(request, "page");
        int page = (pageObj != null && pageObj > 0) ? pageObj : 1;

        //ép kiểu sang BigDecimal cho bộ lọc giá
        BigDecimal priceFrom = ParamUtil.getBigDecimal(request, "price_from");
        BigDecimal priceTo = ParamUtil.getBigDecimal(request, "price_to");

        //tính toán phân trang
        int numberOfProduct = productDAO.getTotalProductFilter(type, cid, textSearch, provinceId, sortBy, priceFrom, priceTo);
        int totalPages = (int) Math.ceil((double) numberOfProduct / PAGE_SIZE);

        // list danh sách filter
        List<ProductResponse> listProductFilter = productDAO.getAllProductByFilter(
                type, cid, textSearch, provinceId, sortBy, page, PAGE_SIZE, priceFrom, priceTo
        );

        //đẩy ngược dữ liệu về lại trang JSP
        request.setAttribute("type", type);
        request.setAttribute("categoryId", cid); // Đồng bộ lại biến cid chuẩn bài
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("textSearch", textSearch);
        request.setAttribute("priceFrom", priceFrom);
        request.setAttribute("priceTo", priceTo);
        request.setAttribute("provinceId", provinceId);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("listProductFilter", listProductFilter);

        //load danh sách tĩnh dùng chung cho thanh Sidebar
        request.setAttribute("categoryList", getCategoryList());
        request.setAttribute("provinceList", getProvinceList());

        //sang trang hiển thị
        request.getRequestDispatcher("/public/product/list-products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }


    // lay danh sach category
    private List<Category> getCategoryList(){
        return categoryDAO.getAllCategory();
    }

    // lay danh sach tinh thanh
    private List<Province> getProvinceList() {
        return provinceDAO.getAllProvinces();
    }

}
