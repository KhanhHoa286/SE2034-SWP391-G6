package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.UserDAO;

import java.io.IOException;

@WebServlet("/cancel-pending-registration")
public class CancelPendingRegistrationServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase();

        if (!email.isEmpty()) {
            /*
             * Chỉ xóa user chưa xác thực OTP:
             * users.status = 'PENDING'
             *
             * Không xóa shipper chờ admin duyệt:
             * users.status = 'ACTIVE'
             * shipper_approval_status = 'PENDING'
             */
            userDao.deletePendingRegistrationByEmail(email);
        }

        response.sendRedirect(request.getContextPath() + "/register");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/register");
    }
}