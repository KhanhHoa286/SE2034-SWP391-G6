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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/*
 * Xử lý chức năng quên mật khẩu.
 *
 * GET:
 * - Hiển thị màn nhập email.
 *
 * POST:
 * - Kiểm tra email có tồn tại và đang ACTIVE không.
 * - Tạo OTP.
 * - Lưu OTP vào DB.
 * - Gửi OTP qua email.
 * - Chuyển sang màn verify-otp.jsp với type=forgot.
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private static final String FORGOT_JSP = "/public/auth/forgot-password.jsp";

    private final UserDAO userDao = new UserDAO();
    private final EmailVerificationDAO otpDao = new EmailVerificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setNoCache(response);
        request.getRequestDispatcher(FORGOT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String email = normalizeEmail(request.getParameter("email"));

        if (email.isEmpty()) {
            forwardForgot(request, response, email, "Vui lòng nhập email.");
            return;
        }

        if (!isValidEmail(email)) {
            forwardForgot(request, response, email, "Email không hợp lệ.");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            forwardForgot(request, response, email, "Email này không tồn tại hoặc tài khoản chưa được kích hoạt.");
            return;
        }

        try {
            String otp = OtpUtils.generateOtp();

            otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

            EmailUtils.sendEmail(
                    email,
                    "Đặt lại mật khẩu MODA",
                    "Xin chào,<br><br>"
                            + "Bạn đang yêu cầu đặt lại mật khẩu tại MODA.<br>"
                            + "Mã OTP của bạn là: <b style='font-size:18px;'>" + otp + "</b><br>"
                            + "Mã này có hiệu lực trong 1 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.<br><br>"
                            + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.<br><br>"
                            + "Trân trọng,<br>"
                            + "Đội ngũ MODA"
            );

            HttpSession session = request.getSession();
            session.setAttribute("forgotOtpEmail", email);
            session.removeAttribute("forgotOtpVerified");

            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

            response.sendRedirect(request.getContextPath()
                    + "/public/auth/verify-otp.jsp?type=forgot&email=" + encodedEmail);
        } catch (Exception e) {
            e.printStackTrace();
            forwardForgot(request, response, email, "Không gửi được mã OTP. Vui lòng thử lại.");
        }
    }

    /*
     * Forward lại màn quên mật khẩu khi có lỗi.
     */
    private void forwardForgot(HttpServletRequest request,
                               HttpServletResponse response,
                               String email,
                               String error)
            throws ServletException, IOException {
        request.setAttribute("email", email);
        request.setAttribute("error", error);
        request.getRequestDispatcher(FORGOT_JSP).forward(request, response);
    }

    /*
     * Chuẩn hóa email.
     */
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    /*
     * Validate định dạng email.
     */
    private boolean isValidEmail(String email) {
        return email != null
                && Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
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