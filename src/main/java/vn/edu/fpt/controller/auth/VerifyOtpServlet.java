package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;

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

        userDao.deleteExpiredPendingRegistrations(15);

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            request.setAttribute("error", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/register.jsp").forward(request, response);
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            request.setAttribute("error", "Trạng thái tài khoản không hợp lệ để xác thực OTP.");
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

        boolean activated = userDao.activateUserAfterOtp(user.getUserId());

        if (!activated) {
            request.setAttribute("error", "Không cập nhật được trạng thái tài khoản sau xác thực OTP.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/public/auth/login.jsp?verified=true"
        );
    }
}