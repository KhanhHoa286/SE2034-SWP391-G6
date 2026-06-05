package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.common.EmailUtils;
import vn.edu.fpt.common.OtpUtils;
import vn.edu.fpt.common.PasswordUtils;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private boolean isValidEmail(String email) {
        return email != null
                && Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private void forwardLogin(HttpServletRequest request,
                              HttpServletResponse response,
                              String error,
                              String email)
            throws ServletException, IOException {

        request.setAttribute("error", error);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/public/auth/login.jsp").forward(request, response);
    }

    private void sendOtp(String email) throws Exception {
        EmailVerificationDAO otpDao = new EmailVerificationDAO();

        String otp = OtpUtils.generateOtp();

        otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

        EmailUtils.sendEmail(
                email,
                "Xác thực tài khoản MODA",
                "Xin chào,<br><br>"
                        + "Bạn đang tiếp tục xác thực tài khoản tại MODA.<br>"
                        + "Mã xác thực OTP của bạn là: <b style='font-size:18px;'>" + otp + "</b><br>"
                        + "Mã này có hiệu lực trong 1 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.<br><br>"
                        + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.<br><br>"
                        + "Trân trọng,<br>"
                        + "Đội ngũ MODA"
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/public/auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        /*
         * Không để UserDAO là biến global/final ở đầu Servlet.
         * Tạo mới trong từng request để tránh giữ connection null.
         */
        UserDAO userDao = new UserDAO();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        email = email == null ? "" : email.trim().toLowerCase();
        password = password == null ? "" : password;

        if (email.isEmpty() || password.trim().isEmpty()) {
            forwardLogin(request, response,
                    "Vui lòng nhập đầy đủ email và mật khẩu.",
                    email);
            return;
        }

        if (!isValidEmail(email)) {
            forwardLogin(request, response,
                    "Email không hợp lệ. Vui lòng nhập đúng định dạng, ví dụ: example@gmail.com.",
                    email);
            return;
        }

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            forwardLogin(request, response,
                    "Email hoặc mật khẩu không đúng.",
                    email);
            return;
        }

        boolean passwordMatched = PasswordUtils.checkPassword(password, user.getPasswordHash());

        if (!passwordMatched) {
            forwardLogin(request, response,
                    "Email hoặc mật khẩu không đúng.",
                    email);
            return;
        }

        if (user.getStatus() == UserStatus.PENDING) {
            try {
                sendOtp(email);

                String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/public/auth/verify-otp.jsp?email=" + encodedEmail);
                return;

            } catch (Exception e) {
                e.printStackTrace();

                forwardLogin(request, response,
                        "Tài khoản chưa xác thực OTP nhưng hệ thống chưa gửi lại được mã. Vui lòng kiểm tra thư rác hoặc thử lại sau.",
                        email);
                return;
            }
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            forwardLogin(request, response,
                    "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.",
                    email);
            return;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            forwardLogin(request, response,
                    "Trạng thái tài khoản không hợp lệ.",
                    email);
            return;
        }

        Integer roleId = userDao.getRoleIdByUserId(user.getUserId());

        if (roleId == null || roleId <= 0) {
            forwardLogin(request, response,
                    "Tài khoản chưa được gán quyền. Vui lòng liên hệ quản trị viên.",
                    email);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("roleId", roleId);
        session.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());

        String contextPath = request.getContextPath();

        if (roleId == 1) {
            response.sendRedirect(contextPath + "/admin/dashboard/view-system-overview.jsp");
        } else if (roleId == 2) {
            response.sendRedirect(contextPath + "/public/home/view-home.jsp");
        } else if (roleId == 3) {
            response.sendRedirect(contextPath + "/seller/dashboard/view-seller-dashboard.jsp");
        } else if (roleId == 4) {
            response.sendRedirect(contextPath + "/logistics/delivery/list-deliveries.jsp");
        } else {
            response.sendRedirect(contextPath + "/public/home/view-home.jsp");
        }
    }
}