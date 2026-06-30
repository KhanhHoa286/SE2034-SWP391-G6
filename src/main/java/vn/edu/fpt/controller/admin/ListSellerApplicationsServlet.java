package vn.edu.fpt.controller.admin;
import vn.edu.fpt.dao.SellerApplicationDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@WebServlet("/admin/seller-applications")
public class ListSellerApplicationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String status = request.getParameter("status");
        String date = request.getParameter("date");
        if (status == null || status.trim().isEmpty()) {
            status = "PENDING";
        }

        // Khởi tạo DAO (Lúc này DBContext cha sẽ tự tạo kết nối connection)
        SellerApplicationDAO dao = new SellerApplicationDAO();
        List<SellerApplication> list = dao.getApplications(search, status, date);

        request.setAttribute("applicationList", list);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("date", date);

        request.getRequestDispatcher("/admin/seller_mgt/list-seller-applications.jsp").forward(request, response);
    }
}
