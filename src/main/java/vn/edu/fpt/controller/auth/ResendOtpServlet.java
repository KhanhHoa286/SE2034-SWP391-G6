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

@WebServlet("/resend-otp")
public class ResendOtpServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();
    private final EmailVerificationDAO otpDao = new EmailVerificationDAO();

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

    private void forwardOtp(HttpServletRequest request,
                            HttpServletResponse response,
                            String email,
                            String error,
                            String success)
            throws ServletException, IOException {

        request.getSession().setAttribute("pendingOtpEmail", normalizeEmail(email));
        request.setAttribute("email", normalizeEmail(email));

        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }

        if (success != null && !success.trim().isEmpty()) {
            request.setAttribute("success", success);
        }

        request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
    }

    private void sendOtp(String email) throws Exception {
        String otp = OtpUtils.generateOtp();

        /*
         * createOtp() trong EmailVerificationDAO đã gọi invalidateOldOtp(email),
         * nên mỗi lần gửi lại mã mới thì mã cũ tự mất hiệu lực.
         */
        otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

        EmailUtils.sendEmail(
                email,
                "Gửi lại mã xác thực tài khoản MODA",
                "Xin chào,<br><br>"
                        + "Mã OTP mới của bạn là: <b style='font-size:18px;'>" + otp + "</b><br>"
                        + "Mã này có hiệu lực trong 1 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.<br><br>"
                        + "Trân trọng,<br>"
                        + "Đội ngũ MODA"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String emailFromSession = getPendingEmailFromSession(request);
        String emailFromRequest = normalizeEmail(request.getParameter("email"));

        /*
         * Ưu tiên email trong session.
         * Hidden input chỉ dùng dự phòng.
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
                    "Phiên gửi lại OTP không hợp lệ.",
                    null
            );
            return;
        }

        /*
         * User PENDING chỉ tồn tại tối đa 15 phút.
         * Trong 15 phút đó, người dùng được gửi lại OTP bất kỳ lúc nào.
         */
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

        try {
            sendOtp(email);

            forwardOtp(
                    request,
                    response,
                    email,
                    null,
                    "Mã OTP mới đã được gửi. Vui lòng kiểm tra email."
            );

        } catch (Exception e) {
            e.printStackTrace();

            forwardOtp(
                    request,
                    response,
                    email,
                    "Không gửi được mã OTP. Vui lòng thử lại.",
                    null
            );
        }
    }
}