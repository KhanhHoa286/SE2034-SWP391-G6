package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/customer/view-order")
public class ViewOrderServlet extends HttpServlet {
     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
              request.getRequestDispatcher("/customer/order/view-order.jsp").forward(request,response);
         }
}
