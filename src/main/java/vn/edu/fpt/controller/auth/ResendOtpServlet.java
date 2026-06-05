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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Không tìm thấy email để gửi lại OTP.");
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        email = email.trim();

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            request.setAttribute("error", "Email chưa được đăng ký.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp");
            return;
        }

        String otp = OtpUtils.generateOtp();

        try {
            otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

            EmailUtils.sendEmail(
                    email,
                    "Gửi lại mã OTP MODA",
                    "Mã OTP mới của bạn là: <b>" + otp + "</b>. Mã có hiệu lực trong 1 phút."
            );

            request.setAttribute("message", "Đã gửi lại mã OTP. Vui lòng kiểm tra email.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không gửi lại được OTP. Vui lòng thử lại.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
        }
    }
}