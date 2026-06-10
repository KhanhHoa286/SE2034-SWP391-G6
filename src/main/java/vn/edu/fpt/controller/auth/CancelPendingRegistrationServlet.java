package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.UserDAO;

import java.io.IOException;

@WebServlet("/cancel-pending-registration")
public class CancelPendingRegistrationServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        /*
         * PENDING chỉ có hạn 15 phút.
         * Dọn bản ghi quá hạn trước.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase();

        HttpSession session = request.getSession(false);
        String pendingOtpEmail = session == null
                ? null
                : (String) session.getAttribute("pendingOtpEmail");

        boolean isSamePendingSession = pendingOtpEmail != null
                && pendingOtpEmail.trim().equalsIgnoreCase(email);

        if (!email.isEmpty() && isSamePendingSession) {
            /*
             * Chỉ xóa user chưa xác thực OTP:
             * users.status = 'PENDING'.
             * Không xóa shipper chờ admin duyệt vì shipper chờ duyệt là:
             * users.status = 'ACTIVE' và shipper_approval_status = 'PENDING'.
             */
            userDao.deletePendingRegistrationByEmail(email);
        }

        if (session != null) {
            session.removeAttribute("pendingOtpEmail");
        }

        response.sendRedirect(request.getContextPath() + "/register");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setNoCache(response);
        response.sendRedirect(request.getContextPath() + "/register");
    }
}