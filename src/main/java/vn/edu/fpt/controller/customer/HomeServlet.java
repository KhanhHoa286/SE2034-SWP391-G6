package vn.edu.fpt.controller.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.User;

/**
 * HoaNK - HE195013
 * Date: 31/05/2026
 * Description: Lấy danh sách sản phẩm ưu đãi sâu nhất, danh sách sản phẩm mới, danh sách sản phẩm bán chạy để hiển thị lên trang chủ
 */

@WebServlet(urlPatterns={"/home"})
public class HomeServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Lấy danh sách sản phẩm ưu đãi sâu nhất
        List<ProductResponse> topDiscountedProducts = productDAO.getTopDiscountedProducts();
        // Lấy danh sách sản phẩm mới
        List<ProductResponse> latestProducts = productDAO.getTopNewProducts();
        // Lấy danh sách sản phẩm bán chạy
        List<ProductResponse> bestSellingProducts = productDAO.getTopBestSellingProducts();
        //
        request.setAttribute("topDiscountedProducts", topDiscountedProducts);
        request.setAttribute("latestProducts", latestProducts);
        request.setAttribute("bestSellingProducts", bestSellingProducts);
        //
        request.getRequestDispatcher("/public/home/view-home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}