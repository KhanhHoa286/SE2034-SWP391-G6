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

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String getPendingEmailFromSession(HttpSession session) {
        if (session == null) {
            return "";
        }

        Object value = session.getAttribute("pendingOtpEmail");
        return value == null ? "" : normalizeEmail(String.valueOf(value));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        userDao.deleteExpiredPendingRegistrations(15);

        HttpSession session = request.getSession(false);
        String emailFromSession = getPendingEmailFromSession(session);
        String emailFromRequest = normalizeEmail(request.getParameter("email"));

        /*
         * Nút QUAY LẠI trên màn OTP:
         * xóa tài khoản PENDING để người dùng nhập lại form đăng ký.
         * Ưu tiên email trong session, hidden email chỉ dùng khi session bị mất.
         */
        String emailToDelete = !emailFromSession.isEmpty() ? emailFromSession : emailFromRequest;

        if (!emailToDelete.isEmpty()) {
            userDao.deletePendingRegistrationByEmail(emailToDelete);
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