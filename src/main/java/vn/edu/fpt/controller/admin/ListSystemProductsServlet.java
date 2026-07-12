package vn.edu.fpt.controller.admin;

import vn.edu.fpt.dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/products"})
public class ListSystemProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");
        String status = "PENDING"; // Chỉ lấy các sản phẩm chờ duyệt
        
        int page = 1;
        int pageSize = 10;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }
        
        ProductDAO productDAO = new ProductDAO();
        List<Map<String, Object>> products = productDAO.getAllSystemProducts(search, status, page, pageSize);
        int totalProducts = productDAO.getTotalSystemProducts(search, status);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        request.setAttribute("products", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/admin/product_mgt/list-products.jsp").forward(request, response);
    }
}
