package vn.edu.fpt.controller.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.response.ProductResponse;

/**
 * HoaNK - HE195013
 * Date: 31/05/2026
 * Description: Lấy danh sách sản phẩm ưu đãi sâu nhất, danh sách sản phẩm mới, danh sách sản phẩm bán chạy để hiển thị lên trang chủ
 */

@WebServlet(urlPatterns={"/home"})
public class HomeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Name</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Name at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();
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
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}