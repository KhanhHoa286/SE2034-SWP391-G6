package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.OrderDAO;

import java.io.IOException;

@WebServlet("/admin/order-detail")
public class GlobalOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        try {
            int masterOrderId = Integer.parseInt(idParam);
            OrderDAO dao = new OrderDAO();
            GlobalOrderDetailDTO orderDetail = dao.getGlobalOrderDetail(masterOrderId);

            if (orderDetail == null) {
                // Not found
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            
            if (orderDetail.getMasterOrder() != null && orderDetail.getMasterOrder().getCreatedAt() != null) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                request.setAttribute("formattedOrderDate", orderDetail.getMasterOrder().getCreatedAt().format(formatter));
            } else {
                request.setAttribute("formattedOrderDate", "");
            }

            request.setAttribute("orderDetail", orderDetail);
            request.getRequestDispatcher("/admin/order_mgt/view-global-order-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }
}
