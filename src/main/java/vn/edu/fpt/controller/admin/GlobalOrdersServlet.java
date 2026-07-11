package vn.edu.fpt.controller.admin;

import vn.edu.fpt.dao.OrderDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class GlobalOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String search = request.getParameter("search");
        String status = request.getParameter("status");
        String pageParam = request.getParameter("page");

        if (status == null || status.trim().isEmpty()) {
            status = "all";
        }

        int page = 1;
        int pageSize = 10;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }

        OrderDAO orderDAO = new OrderDAO();
        int totalOrders = orderDAO.getTotalGlobalOrders(search, status);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        List<OrderHistoryDTO> orderList = orderDAO.getGlobalOrders(search, status, page, pageSize);

        request.setAttribute("orderList", orderList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("search", search);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/admin/order_mgt/view-global-orders.jsp").forward(request, response);
    }
}
