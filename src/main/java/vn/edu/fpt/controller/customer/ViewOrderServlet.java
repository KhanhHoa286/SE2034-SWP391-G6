package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.dto.response.OrderItemResponse;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 26/06/2026
 * Description: Load dữ liệu lên trang xem chi tiết của 1 đơn hàng(suborder)
 */
@WebServlet("/customer/view-order")
public class ViewOrderServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         //
         Integer subOrderId = ParamUtil.getInteger(request, "sub_order_id");
         if(subOrderId == null || subOrderId <= 0) {
             response.sendRedirect(request.getContextPath() + "/" + "customer/order-list");
             return;
         }
         OrderItemResponse orderItemResponse = orderDAO.getSubOrderDetail(subOrderId);

         if(orderItemResponse == null){
             response.sendRedirect(request.getContextPath() + "/" + "customer/order-list");
             return;
         }

         request.setAttribute("subOrderDetail",orderItemResponse);
         //
              request.getRequestDispatcher("/customer/order/view-order.jsp").forward(request,response);
         }
}
