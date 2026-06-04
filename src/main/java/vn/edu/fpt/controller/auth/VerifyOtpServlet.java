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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String otpInput = request.getParameter("otp");

        email = email == null ? "" : email.trim().toLowerCase();
        otpInput = otpInput == null ? "" : otpInput.trim();

        if (email.isEmpty() || otpInput.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mã OTP.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        boolean isValid = otpDao.verifyOtp(email, otpInput);

        if (!isValid) {
            request.setAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            request.setAttribute("error", "Không tìm thấy tài khoản cần xác thực.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        userDao.updateUserStatus(user.getUserId(), UserStatus.ACTIVE);

        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        response.sendRedirect(
                request.getContextPath()
                        + "/public/auth/login.jsp?verified=true&email="
                        + encodedEmail
        );
    }
}