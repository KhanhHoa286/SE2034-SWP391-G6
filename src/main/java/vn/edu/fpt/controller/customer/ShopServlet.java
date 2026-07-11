package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.dto.request.ProductFilterRequest;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.dto.response.ShopResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 07/06/2026
 * Description: Load lên thông tin shop và sản phẩm của shop, lọc sản phẩm của shop
 */

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {
    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer shopId = ParamUtil.getInteger(request, "shop_id");

        // Vì đây là trang shop ko có shop id thì đá về trang home
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // load lên từng phần của trang shop
        loadShopProfile(request, shopId); // thông tin của shop
        loadShopProductsFilter(request, shopId); //
        loadCategory(request);
        // Đẩy giao diện
        request.getRequestDispatcher("/public/shop/view-shop.jsp").forward(request, response);
    }

    // load lên thông tin shop
    private void loadShopProfile(HttpServletRequest request, Integer shopId) {
        ShopResponse shopResponse = shopDAO.getShopById(shopId);
        request.setAttribute("shopInfo", shopResponse);
    }

    // load lên danh sách sản  phẩm lọc
    private void loadShopProductsFilter(HttpServletRequest request, Integer shopId) {
        // lấy đống request gán vào đối tượng
        ProductFilterRequest productFilterRequest = ProductFilterRequest.fromRequest(request);
        productFilterRequest.setShopId(shopId);
        //tính toán phân trang
        int numberOfProduct = productDAO.getTotalProductFilter(productFilterRequest);
        int totalPages = (int) Math.ceil((double) numberOfProduct / productFilterRequest.getPageSize());

        // danh sách sản phẩm sau khi lọc
        List<ProductResponse> listProductFilter = productDAO.getAllProductByFilter(productFilterRequest);

        // trạng thái trái tim yêu thích
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        //giữ trạng thái bộ lọc trên JSP
        request.setAttribute("filter", productFilterRequest);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("listProductFilter", listProductFilter);
    }

   // load lên danh sách category
    private void loadCategory(HttpServletRequest request) {
        request.setAttribute("categoryList", categoryDAO.getAllCategory());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}