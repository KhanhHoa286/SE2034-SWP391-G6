package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

@WebServlet("/api/customer/cancle-order")
public class CancleOrderServlet extends HttpServlet {
          private final OrderDAO orderDAO = new OrderDAO();
         @Override
         protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             //
             response.setContentType("application/json");
             response.setCharacterEncoding("UTF-8");
             //
             Integer subOrderId = ParamUtil.getInteger(request,"sub_order_id");
             //
             if(subOrderId != null && subOrderId >= 0) {
                 boolean checkCancel = orderDAO.cancleOrder(subOrderId);
                 if(checkCancel) {
                     response.getWriter().write("{\"status\":\"SUCCESS\"}");
                     return;
                 }
             }
             response.getWriter().write("{\"status\":\"ERROR\"}");
         }
}
