package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
/**
 * HoaNK - HE195013
 * Date:
 * Description: Khi đã nhận được hàng thì cập nhật trạng thái giao và thanh toán
 */
@WebServlet("/api/customer/update-status-order")
public class UpdateStatusOrder extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
         @Override
         protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             //
             response.setContentType("application/json");
             response.setCharacterEncoding("UTF-8");
             //
             boolean checkUpdateStatusOrder = false;
             boolean checkUpdatePaymentMethod = false;
             String result = "";
             //
             Integer subOrderId = ParamUtil.getInteger(request, "sub_order_id");
             Integer masterOrderId = ParamUtil.getInteger(request, "master_order_id");
             String paymentMethod = request.getParameter("payment_method");
             if(subOrderId == null || masterOrderId == null || paymentMethod == null || paymentMethod.isEmpty()) {
                  result = "{\"status\":\"ERROR\"}";
                 response.getWriter().write(result);
             }else{
               checkUpdateStatusOrder =  orderDAO.updateStatusOrder(subOrderId);
               if("COD".equals(paymentMethod)){
                   checkUpdatePaymentMethod = orderDAO.updatePaymentMethod(masterOrderId);
               }else if("PAID".equals(paymentMethod)) {
                   checkUpdatePaymentMethod = true;
               }
             }
             if(checkUpdateStatusOrder == true && checkUpdatePaymentMethod == true) {
                  result = "{\"status\":\"SUCCESS\"}";
                  response.getWriter().write(result);
             }
         }
}
