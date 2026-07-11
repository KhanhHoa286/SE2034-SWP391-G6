package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/verify-otp")
public class VerifyOtpServlet extends HttpServlet {

    private final EmailVerificationDAO otpDao = new EmailVerificationDAO();
    private final UserDAO userDao = new UserDAO();

    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String getPendingEmailFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "";
        }

        Object value = session.getAttribute("pendingOtpEmail");
        return value == null ? "" : normalizeEmail(String.valueOf(value));
    }

    private void clearPendingSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute("pendingOtpEmail");
        }
    }

    private void savePendingSession(HttpServletRequest request, String email) {
        request.getSession().setAttribute("pendingOtpEmail", normalizeEmail(email));
    }

    private void redirectVerify(HttpServletRequest request,
                                HttpServletResponse response,
                                String email)
            throws IOException {

        String encodedEmail = URLEncoder.encode(normalizeEmail(email), StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/verify-otp?email=" + encodedEmail);
    }

    private void forwardOtp(HttpServletRequest request,
                            HttpServletResponse response,
                            String email,
                            String error)
            throws ServletException, IOException {

        savePendingSession(request, email);
        request.setAttribute("email", normalizeEmail(email));

        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }

        request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setNoCache(response);

        /*
         * Chỉ xóa tài khoản PENDING quá 15 phút.
         * Nếu người dùng còn trong hạn thì vẫn giữ lại để nhập OTP.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        String emailFromParam = normalizeEmail(request.getParameter("email"));
        String emailFromSession = getPendingEmailFromSession(request);

        String email;

        if (!emailFromSession.isEmpty()) {
            /*
             * Session là nguồn chính.
             * Nếu user tự sửa email trên URL thì kéo về email đang chờ OTP trong session.
             */
            email = emailFromSession;

            if (!emailFromParam.isEmpty() && !emailFromParam.equals(emailFromSession)) {
                redirectVerify(request, response, emailFromSession);
                return;
            }
        } else {
            email = emailFromParam;
        }

        if (email.isEmpty()) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        forwardOtp(request, response, email, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String emailFromSession = getPendingEmailFromSession(request);
        String emailFromRequest = normalizeEmail(request.getParameter("email"));
        String otpInput = request.getParameter("otp");

        otpInput = otpInput == null ? "" : otpInput.trim();

        /*
         * POST xác nhận OTP phải đi theo session đang chờ OTP.
         * Không tin hoàn toàn hidden email để tránh verify nhầm tài khoản.
         */
        String email = !emailFromSession.isEmpty() ? emailFromSession : emailFromRequest;

        if (email.isEmpty()) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (!emailFromSession.isEmpty()
                && !emailFromRequest.isEmpty()
                && !emailFromSession.equals(emailFromRequest)) {

            forwardOtp(
                    request,
                    response,
                    emailFromSession,
                    "Phiên xác thực OTP không hợp lệ. Vui lòng nhập mã cho email hiện tại."
            );
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

        userDao.deleteExpiredPendingRegistrations(15);

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            clearPendingSession(request);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        boolean isValid = otpDao.verifyOtp(email, otpInput);

        if (!isValid) {
            forwardOtp(request, response, email, "Mã OTP không hợp lệ hoặc đã hết hạn.");
            return;
        }

        boolean activated = userDao.activateUserAfterOtp(user.getUserId());

        if (!activated) {
            forwardOtp(request, response, email, "Không cập nhật được trạng thái tài khoản sau xác thực OTP.");
            return;
        }

        clearPendingSession(request);

        response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
    }
}