package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.response.ProductDetailResponse;
import vn.edu.fpt.dto.response.ProductResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 03/06/2026
 * Description: Xử lí và trả về các field của trang view product details khi bắt được product-id
 */
@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // lấy đường dẫn trang trước đó để gán vào nút quay lại
        String referrer = request.getHeader("referer");
        String defaultBackUrl = request.getContextPath() + "/product-list";

        if (referrer == null || referrer.contains(".jsp") || !referrer.contains(request.getServerName())) {
            referrer = defaultBackUrl;
        }

        // hứng tham số
        String pid_raw = request.getParameter("pid");
        String gender = request.getParameter("gender");
        String price_raw = request.getParameter("final_price");
        //
        Integer pid = null;
        BigDecimal price = null;
        try{
            // parse pid
           pid =  pid_raw != null || !pid_raw.trim().isEmpty() ? Integer.parseInt(pid_raw) : null;
            // parse price
           price =  price_raw != null && !price_raw.trim().isEmpty() ? new BigDecimal(price_raw.trim()) : null;
        }catch (Exception e) {
            e.printStackTrace();
        }
        //
        if(pid == null || pid <= 0) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return; // tra ve trang va dung ngay
        }
        // laays ra san chi tiet san pham tuong ung voi id
        ProductDetailResponse productDetailResponse = productDAO.getProductDetailByProductId(pid);

        if(productDetailResponse == null) { // neu bang null tuc san pham do ko ton tai
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }
        // lấy ra top 4 sản phẩm liên quan sản phẩm gốc
        List<ProductResponse> productRelatedList = productDAO.getTop4ProductRelated(gender, pid, price);
        //
        request.setAttribute("backUrl", referrer);
        request.setAttribute("productDetail", productDetailResponse);
        request.setAttribute("productResponseList", productRelatedList);
        request.getRequestDispatcher("/public/product/view-product.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
