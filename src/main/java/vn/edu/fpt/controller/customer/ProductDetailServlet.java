package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.response.ProductDetailResponse;

import java.io.IOException;

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
        // hứng tham số
        String pid_raw = request.getParameter("pid");
        Integer pid = null;
        try{
            // parse pid
            if (pid_raw != null || !pid_raw.trim().isEmpty()) {
              pid =  Integer.parseInt(pid_raw);
            }

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
        //
        request.setAttribute("productDetail", productDetailResponse);
        request.getRequestDispatcher("/public/product/view-product.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
