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

    private void sendOtp(String email) throws Exception {
        String otp = OtpUtils.generateOtp();

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

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase();

        if (email.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy email cần gửi lại OTP.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        /*
         * Chỉ dọn user chưa xác thực OTP quá 15 phút.
         * Không ảnh hưởng shipper chờ admin duyệt vì shipper chờ duyệt là:
         * users.status = ACTIVE
         * shipper_approval_status = PENDING
         */
        userDao.deleteExpiredPendingRegistrations(15);

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            request.setAttribute("error", "Yêu cầu đăng ký đã hết hạn. Vui lòng đăng ký lại từ đầu.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/register.jsp").forward(request, response);
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            request.setAttribute("error", "Trạng thái tài khoản không hợp lệ để gửi lại OTP.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        try {
            sendOtp(email);

            request.setAttribute("success", "Mã OTP mới đã được gửi. Vui lòng kiểm tra email.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("error", "Không gửi được mã OTP. Vui lòng thử lại.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
        }
    }
}