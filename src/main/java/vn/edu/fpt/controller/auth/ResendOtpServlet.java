package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.common.EmailUtils;
import vn.edu.fpt.common.OtpUtils;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Gửi lại OTP.
 *
 * Dùng chung cho:
 * - Đăng ký tài khoản: type=register
 * - Quên mật khẩu: type=forgot
 */
@WebServlet("/resend-otp")
public class ResendOtpServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();
    private final EmailVerificationDAO otpDao = new EmailVerificationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String type = request.getParameter("type");

        if ("forgot".equalsIgnoreCase(type)) {
            resendForgotOtp(request, response);
            return;
        }

        resendRegisterOtp(request, response);
    }

    /*
     * Gửi lại OTP đăng ký tài khoản.
     */
    private void resendRegisterOtp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = getEmailForRegister(request);

        if (email.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        userDao.deleteExpiredPendingRegistrations(15);

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            response.sendRedirect(request.getContextPath()
                    + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        try {
            String otp = OtpUtils.generateOtp();

            otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

            EmailUtils.sendEmail(
                    email,
                    "Gửi lại mã xác thực tài khoản MODA",
                    "Xin chào,<br><br>"
                            + "Mã OTP mới của bạn là: <b style='font-size:18px;'>" + otp + "</b><br>"
                            + "Mã này có hiệu lực trong 1 phút.<br><br>"
                            + "Trân trọng,<br>"
                            + "Đội ngũ MODA"
            );

            forwardOtp(request, response, email, "register",
                    null,
                    "Mã OTP mới đã được gửi. Vui lòng kiểm tra email.");

        } catch (Exception e) {
            e.printStackTrace();

            forwardOtp(request, response, email, "register",
                    "Không gửi được mã OTP. Vui lòng thử lại.",
                    null);
        }
    }

    /*
     * Gửi lại OTP quên mật khẩu.
     */
    private void resendForgotOtp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = getEmailForForgot(request);

        if (email.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        try {
            String otp = OtpUtils.generateOtp();

            otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

            EmailUtils.sendEmail(
                    email,
                    "Gửi lại mã đặt lại mật khẩu MODA",
                    "Xin chào,<br><br>"
                            + "Mã OTP mới để đặt lại mật khẩu của bạn là: "
                            + "<b style='font-size:18px;'>" + otp + "</b><br>"
                            + "Mã này có hiệu lực trong 1 phút.<br><br>"
                            + "Trân trọng,<br>"
                            + "Đội ngũ MODA"
            );

            forwardOtp(request, response, email, "forgot",
                    null,
                    "Mã OTP mới đã được gửi. Vui lòng kiểm tra email.");

        } catch (Exception e) {
            e.printStackTrace();

            forwardOtp(request, response, email, "forgot",
                    "Không gửi được mã OTP. Vui lòng thử lại.",
                    null);
        }
    }

    /*
     * Lấy email cho resend OTP đăng ký.
     */
    private String getEmailForRegister(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("pendingOtpEmail") != null) {
            return normalizeEmail(String.valueOf(session.getAttribute("pendingOtpEmail")));
        }

        return normalizeEmail(request.getParameter("email"));
    }

    /*
     * Lấy email cho resend OTP quên mật khẩu.
     */
    private String getEmailForForgot(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("forgotOtpEmail") != null) {
            return normalizeEmail(String.valueOf(session.getAttribute("forgotOtpEmail")));
        }

        return normalizeEmail(request.getParameter("email"));
    }

    /*
     * Forward lại màn verify-otp.jsp.
     */
    private void forwardOtp(HttpServletRequest request,
                            HttpServletResponse response,
                            String email,
                            String type,
                            String error,
                            String success)
            throws ServletException, IOException {

        email = normalizeEmail(email);

        if ("forgot".equalsIgnoreCase(type)) {
            request.getSession().setAttribute("forgotOtpEmail", email);
        } else {
            request.getSession().setAttribute("pendingOtpEmail", email);
        }

        request.setAttribute("email", email);
        request.setAttribute("type", type);

        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }

        if (success != null && !success.trim().isEmpty()) {
            request.setAttribute("success", success);
        }

        request.getRequestDispatcher("/public/auth/verify-otp.jsp")
                .forward(request, response);
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