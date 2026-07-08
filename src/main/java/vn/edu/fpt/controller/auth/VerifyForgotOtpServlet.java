package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;

/*
 * Xác thực OTP cho chức năng quên mật khẩu.
 *
 * OTP đúng:
 * - Không active user như đăng ký.
 * - Chỉ set session forgotOtpVerified = true.
 * - Chuyển sang màn reset-password.
 */
@WebServlet("/verify-forgot-otp")
public class VerifyForgotOtpServlet extends HttpServlet {

    private final EmailVerificationDAO otpDao = new EmailVerificationDAO();
    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String emailFromSession = getForgotEmailFromSession(request);
        String emailFromRequest = normalizeEmail(request.getParameter("email"));
        String otpInput = request.getParameter("otp") == null ? "" : request.getParameter("otp").trim();

        String email = !emailFromSession.isEmpty() ? emailFromSession : emailFromRequest;

        if (email.isEmpty()) {
            clearForgotSession(request);
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        if (!emailFromSession.isEmpty()
                && !emailFromRequest.isEmpty()
                && !emailFromSession.equals(emailFromRequest)) {
            forwardOtp(request, response, emailFromSession,
                    "Phiên xác thực OTP không hợp lệ. Vui lòng nhập mã cho email hiện tại.");
            return;
        }

        if (otpInput.isEmpty()) {
            forwardOtp(request, response, email, "Vui lòng nhập đầy đủ mã OTP.");
            return;
        }

        if (!otpInput.matches("^[0-9]{6}$")) {
            forwardOtp(request, response, email, "Mã OTP phải gồm đúng 6 chữ số.");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            clearForgotSession(request);
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        boolean valid = otpDao.verifyOtp(email, otpInput);

        if (!valid) {
            forwardOtp(request, response, email, "Mã OTP không hợp lệ hoặc đã hết hạn.");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("forgotOtpEmail", email);
        session.setAttribute("forgotOtpVerified", true);

        response.sendRedirect(request.getContextPath() + "/reset-password");
    }

    /*
     * Forward lại màn OTP khi OTP sai hoặc lỗi validate.
     */
    private void forwardOtp(HttpServletRequest request,
                            HttpServletResponse response,
                            String email,
                            String error)
            throws ServletException, IOException {
        request.getSession().setAttribute("forgotOtpEmail", normalizeEmail(email));
        request.setAttribute("email", normalizeEmail(email));
        request.setAttribute("type", "forgot");
        request.setAttribute("error", error);

        request.getRequestDispatcher("/public/auth/verify-otp.jsp")
                .forward(request, response);
    }

    /*
     * Lấy email quên mật khẩu từ session.
     */
    private String getForgotEmailFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "";
        }

        Object value = session.getAttribute("forgotOtpEmail");
        return value == null ? "" : normalizeEmail(String.valueOf(value));
    }

    /*
     * Xóa session quên mật khẩu.
     */
    private void clearForgotSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute("forgotOtpEmail");
            session.removeAttribute("forgotOtpVerified");
        }
    }

    /*
     * Chuẩn hóa email.
     */
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
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