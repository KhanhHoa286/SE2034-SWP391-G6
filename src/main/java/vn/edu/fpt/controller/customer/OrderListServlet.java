package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.dto.request.OrderHistoryFilterRequest;
import vn.edu.fpt.dto.response.OrderHistoryFilterResponse;
import vn.edu.fpt.dto.response.OrderHistoryResponse;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 21/6/2026
 * Description: Load lên danh sách đơn hàng, lọc theo ngày và trạng thái
 */
@WebServlet("/customer/order-list")
public class OrderListServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
    private final int PAGE_SIZE = 5;
     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         //
         HttpSession session = request.getSession();
         User user = (User) session.getAttribute("user");
         if(user != null) {
             //
             OrderHistoryFilterRequest orderRequest = this.getOrderHistoryFilterRequest(request);
             //
             OrderHistoryFilterResponse orderResponse = this.getOrderHistoryFilterResponse(user.getUserId(),orderRequest);
             //
             request.setAttribute("subOrderStatus", SubOrderStatus.values());
             request.setAttribute("orderRequest", orderRequest);
             request.setAttribute("orderResponse", orderResponse);
             request.getRequestDispatcher("/customer/order/list-orders.jsp").forward(request, response);
         }else{
             response.sendRedirect(request.getContextPath() + "/" + "login");
         }
         }

         // gaán tham số vào order history request
         private OrderHistoryFilterRequest getOrderHistoryFilterRequest(HttpServletRequest request) {
             //
             String fromDate = request.getParameter("from_date");
             String toDate = request.getParameter("to_date");
             String status = request.getParameter("status");
             Integer pageNumber = ParamUtil.getInteger(request,"page");
             if(pageNumber == null) {
                 pageNumber = 1;
             }
             //
             return new OrderHistoryFilterRequest(fromDate,toDate,status,pageNumber);
         }

         // gán tham số vào order his tory response
         private OrderHistoryFilterResponse getOrderHistoryFilterResponse(int customerId, OrderHistoryFilterRequest orderRequest) {
         // lấy ra tổng số trang
             int numberOfOrder = orderDAO.countSubOrderByCustomerId(customerId, orderRequest);
             int currentPage = orderRequest.getPageNumber();
             // tính toán số trang
             int totalPage = (int) Math.ceil((double) numberOfOrder / PAGE_SIZE);
             // list order
             List<OrderHistoryResponse> orderHistoryResponses = orderDAO.getSubOrderByCustomerId(customerId,orderRequest,PAGE_SIZE);
             //
             return new OrderHistoryFilterResponse(orderHistoryResponses,currentPage,totalPage);
         }
}
