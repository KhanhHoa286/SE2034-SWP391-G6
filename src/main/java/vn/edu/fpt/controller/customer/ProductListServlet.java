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

/**
 * HoaNK - HE195013
 * Date: 31/05/2026
 * Description: Method_01-private. Load lên danh sách sản phẩm (lọc theo nam, nữ, phụ kiện, sale off) của navbar
 *              -> Retrun List<ProductResponse>
 *              Method_02-private. Load lên danh sách sản phẩm cho phần XEM TẤT CẢ của trang home
 *              -> Return List<ProductResponse>
 *              Method_03-private. Load lên danh sách category
 *              -> List<Category>
 *              Method_04-private. Load lên danh sách tỉnh thành
 *              -> List<Province>
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
        //
        // bắt request để kiểm tra
        String type = request.getParameter("type");
        String sortBy = request.getParameter("sort_by");
        String textSearch = request.getParameter("text_search");
        String categoryId_raw = request.getParameter("cid");
        String page_raw = request.getParameter("page");
        String priceFrom_raw = request.getParameter("price_from");
        String priceTo_raw = request.getParameter("price_to");
        String provinceId_raw = request.getParameter("province_id");
        // parse dữ liệu
        Integer cid = null; Integer provinceId = null;
        BigDecimal priceFrom = null; BigDecimal priceTo = null;
        int page = 1;
        try {
            cid = categoryId_raw != null && !categoryId_raw.trim().isEmpty() ? Integer.parseInt(categoryId_raw) : null;
            provinceId = provinceId_raw != null && !provinceId_raw.trim().isEmpty() ? Integer.parseInt(provinceId_raw) : null;

            page = page_raw != null && !page_raw.trim().isEmpty() ? Integer.parseInt(page_raw) : 1;
            priceFrom = priceFrom_raw != null && !priceFrom_raw.trim().isEmpty() ? new BigDecimal(priceFrom_raw.trim()) : null;
            priceTo = priceTo_raw != null && !priceTo_raw.trim().isEmpty() ? new BigDecimal(priceTo_raw.trim()) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // đếm số lượng sản phẩm
        int numberOfProduct = productDAO.getTotalProductFilter(type, cid, textSearch, provinceId, sortBy, priceFrom, priceTo);
        int totalPages = (int) Math.ceil((double) numberOfProduct / PAGE_SIZE);
        // danh sách sản phẩm sau khi search
        List<ProductResponse> listProductFilter = productDAO.getAllProductByFilter(type,cid,textSearch,provinceId,sortBy,page,PAGE_SIZE,priceFrom,priceTo);
        // gửi sang thông tin của trang
        request.setAttribute("type", type);
        request.setAttribute("categoryId", cid);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("textSearch", textSearch);
        request.setAttribute("priceFrom", priceFrom);
        request.setAttribute("priceTo", priceTo);
        request.setAttribute("provinceId", provinceId);
        request.setAttribute("page", page);
        //
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("listProductFilter", listProductFilter);
        // lấy danh sách category, province
        request.setAttribute("categoryList", getCategoryList());
        request.setAttribute("provinceList", getProvinceList());
        //
        request.getRequestDispatcher("/public/product/list-products.jsp").forward(request,response);
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
