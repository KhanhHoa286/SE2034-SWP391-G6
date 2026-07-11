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

        String pageParam = request.getParameter("page");
        int page = 1;
        int pageSize = 10;

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }

        SellerApplicationDAO dao = new SellerApplicationDAO();
        int totalApps = dao.getTotalApplications(search, status, date);
        int totalPages = (int) Math.ceil((double) totalApps / pageSize);

        List<SellerApplication> list = dao.getApplications(search, status, date, page, pageSize);

        request.setAttribute("applicationList", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalApps", totalApps);
        request.setAttribute("search", search);
        request.setAttribute("status", status);
        request.setAttribute("date", date);

        request.getRequestDispatcher("/admin/seller_mgt/list-seller-applications.jsp").forward(request, response);
    }
}
