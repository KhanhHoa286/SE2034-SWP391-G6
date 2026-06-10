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

    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setNoCache(response);

        /*
         * PENDING chỉ có hạn 15 phút.
         * Quá 15 phút thì xóa user PENDING + OTP + role để đăng ký lại từ đầu.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        String email = request.getParameter("email");

        if ((email == null || email.trim().isEmpty()) && request.getSession(false) != null) {
            email = (String) request.getSession(false).getAttribute("pendingOtpEmail");
        }

        email = email == null ? "" : email.trim().toLowerCase();

        if (email.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingOtpEmail");
            }

            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingOtpEmail");
            }

            response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp?verified=true");
            return;
        }

        if (user.getStatus() != UserStatus.PENDING) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingOtpEmail");
            }

            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        request.getSession().setAttribute("pendingOtpEmail", email);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

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

        /*
         * PENDING chỉ có hạn 15 phút.
         * Nếu quá hạn thì xóa trước khi kiểm tra OTP.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingOtpEmail");
            }

            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingOtpEmail");
            }

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

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("pendingOtpEmail");
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/public/auth/login.jsp?verified=true"
        );
    }
}