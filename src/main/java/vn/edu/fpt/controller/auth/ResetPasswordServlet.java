package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.common.PasswordUtils;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.regex.Pattern;

/*
 * Xử lý đặt lại mật khẩu sau khi OTP quên mật khẩu đã đúng.
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final String RESET_JSP = "/public/auth/reset-password.jsp";

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setNoCache(response);

        HttpSession session = request.getSession(false);

        if (!isForgotVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.getRequestDispatcher(RESET_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        HttpSession session = request.getSession(false);

        if (!isForgotVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String email = String.valueOf(session.getAttribute("forgotOtpEmail"));
        String password = trim(request.getParameter("password"));
        String confirmPassword = trim(request.getParameter("confirmPassword"));

        User user = userDao.getUserByEmail(email);

        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            clearForgotSession(session);
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            forwardReset(request, response, "Vui lòng nhập đầy đủ mật khẩu mới.");
            return;
        }

        if (!isValidPassword(password)) {
            forwardReset(request, response,
                    "Mật khẩu chỉ gồm chữ số 0-9 và dài từ 6 đến 32 số.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            forwardReset(request, response, "Mật khẩu xác nhận không khớp.");
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);

        boolean updated = userDao.updatePasswordByEmail(email, passwordHash);

        if (!updated) {
            forwardReset(request, response, "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            return;
        }

        clearForgotSession(session);

        response.sendRedirect(request.getContextPath()
                + "/public/auth/login.jsp?resetPassword=true");
    }

    /*
     * Validate mật khẩu giống đăng ký.
     * Mật khẩu chỉ gồm số 0-9, dài 6 đến 32 số.
     */
    private boolean isValidPassword(String password) {
        return password != null && Pattern.matches("^[0-9]{6,32}$", password);
    }

    /*
     * Kiểm tra đã xác thực OTP quên mật khẩu hay chưa.
     */
    private boolean isForgotVerified(HttpSession session) {
        return session != null
                && session.getAttribute("forgotOtpEmail") != null
                && Boolean.TRUE.equals(session.getAttribute("forgotOtpVerified"));
    }

    /*
     * Xóa session quên mật khẩu sau khi reset xong.
     */
    private void clearForgotSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute("forgotOtpEmail");
            session.removeAttribute("forgotOtpVerified");
        }
    }

    /*
     * Forward lại màn reset khi có lỗi.
     */
    private void forwardReset(HttpServletRequest request,
                              HttpServletResponse response,
                              String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher(RESET_JSP).forward(request, response);
    }

    /*
     * Cắt khoảng trắng đầu/cuối.
     */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /*
     * Chặn cache màn auth.
     */
    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}