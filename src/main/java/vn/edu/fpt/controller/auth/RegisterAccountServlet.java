package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.common.EmailUtils;
import vn.edu.fpt.common.OtpUtils;
import vn.edu.fpt.common.PasswordUtils;
import vn.edu.fpt.dao.EmailVerificationDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterAccountServlet extends HttpServlet {

    private static final String REGISTER_ROLE = "CUSTOMER";

    private static final String MSG_INFO_USED =
            "Thông tin đăng ký đã được sử dụng hoặc đang chờ xác thực. Vui lòng kiểm tra lại.";

    private boolean isValidEmail(String email) {
        return email != null
                && Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private boolean isValidVietnamPhone(String phone) {
        return phone != null
                && Pattern.matches("^(0)(3[2-9]|5[689]|7[06-9]|8[0-9]|9[0-9])[0-9]{7}$", phone);
    }

    private boolean isValidName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && name.trim().length() <= 50
                && Pattern.matches("^[\\p{L}\\s'-]+$", name.trim());
    }

    private boolean isValidPassword(String password) {
        return password != null && Pattern.matches("^[0-9]{6,32}$", password);
    }

    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void keepFormData(HttpServletRequest request,
                              String firstName,
                              String lastName,
                              String phone,
                              String dob,
                              String gender,
                              String email,
                              String password,
                              String confirmPassword) {

        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("phone", phone);
        request.setAttribute("dob", dob);
        request.setAttribute("gender", gender);
        request.setAttribute("email", email);
        request.setAttribute("password", password);
        request.setAttribute("confirmPassword", confirmPassword);
    }

    private void forwardRegister(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String error,
                                 String firstName,
                                 String lastName,
                                 String phone,
                                 String dob,
                                 String gender,
                                 String email,
                                 String password,
                                 String confirmPassword)
            throws ServletException, IOException {

        request.setAttribute("error", error);

        keepFormData(
                request,
                firstName,
                lastName,
                phone,
                dob,
                gender,
                email,
                password,
                confirmPassword
        );

        request.getRequestDispatcher("/public/auth/register.jsp").forward(request, response);
    }

    private void sendOtp(String email) throws Exception {
        EmailVerificationDAO otpDao = new EmailVerificationDAO();

        String otp = OtpUtils.generateOtp();

        otpDao.createOtp(email, otp, LocalDateTime.now().plusMinutes(1));

        EmailUtils.sendEmail(
                email,
                "Xác thực tài khoản MODA",
                "Xin chào,<br><br>"
                        + "Bạn đang thực hiện đăng ký tài khoản tại MODA.<br>"
                        + "Mã xác thực OTP của bạn là: <b style='font-size:18px;'>" + otp + "</b><br>"
                        + "Mã này có hiệu lực trong 1 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.<br><br>"
                        + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.<br><br>"
                        + "Trân trọng,<br>"
                        + "Đội ngũ MODA"
        );
    }

    private void redirectToVerifyOtp(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String email)
            throws IOException {

        request.getSession().setAttribute("pendingOtpEmail", email);

        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        response.sendRedirect(request.getContextPath()
                + "/verify-otp?email=" + encodedEmail);
    }

    private void forwardVerifyOtp(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String email,
                                  String error)
            throws ServletException, IOException {

        request.getSession().setAttribute("pendingOtpEmail", email);
        request.setAttribute("email", email);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/public/auth/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setNoCache(response);

        UserDAO userDao = new UserDAO();

        /*
         * PENDING chỉ giữ tối đa 15 phút.
         * Quá 15 phút thì xóa để người dùng đăng ký lại từ đầu.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        HttpSession session = request.getSession(false);

        if (session != null) {
            String pendingOtpEmail = (String) session.getAttribute("pendingOtpEmail");

            if (pendingOtpEmail != null && !pendingOtpEmail.trim().isEmpty()) {
                pendingOtpEmail = pendingOtpEmail.trim().toLowerCase();

                User pendingUser = userDao.getUserByEmail(pendingOtpEmail);

                if (pendingUser != null && pendingUser.getStatus() == UserStatus.PENDING) {
                    redirectToVerifyOtp(request, response, pendingOtpEmail);
                    return;
                }

                session.removeAttribute("pendingOtpEmail");
            }
        }

        request.getRequestDispatcher("/public/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        UserDAO userDao = new UserDAO();

        /*
         * Chỉ dọn tài khoản chưa xác thực OTP quá 15 phút.
         * Không xử lý đăng ký shipper ở màn này nữa.
         */
        userDao.deleteExpiredPendingRegistrations(15);

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String genderInput = request.getParameter("gender");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        firstName = firstName == null ? "" : firstName.trim();
        lastName = lastName == null ? "" : lastName.trim();
        phone = phone == null ? "" : phone.trim();
        dobStr = dobStr == null ? "" : dobStr.trim();
        genderInput = genderInput == null ? "" : genderInput.trim();
        email = email == null ? "" : email.trim().toLowerCase();
        password = password == null ? "" : password.trim();
        confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || phone.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            forwardRegister(request, response,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc.",
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidName(firstName)) {
            forwardRegister(request, response,
                    "Họ không hợp lệ.",
                    "", lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidName(lastName)) {
            forwardRegister(request, response,
                    "Tên không hợp lệ.",
                    firstName, "", phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidEmail(email)) {
            forwardRegister(request, response,
                    "Email không hợp lệ.",
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword);
            return;
        }

        if (!isValidVietnamPhone(phone)) {
            forwardRegister(request, response,
                    "Số điện thoại không hợp lệ.",
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidPassword(password)) {
            forwardRegister(request, response,
                    "Mật khẩu không hợp lệ. Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.",
                    firstName, lastName, phone, dobStr, genderInput, email, "", confirmPassword);
            return;
        }

        if (!password.equals(confirmPassword)) {
            forwardRegister(request, response,
                    "Mật khẩu xác nhận không khớp.",
                    firstName, lastName, phone, dobStr, genderInput, email, password, "");
            return;
        }

        LocalDate dob = null;

        if (!dobStr.isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr);
            } catch (Exception e) {
                forwardRegister(request, response,
                        "Ngày sinh không hợp lệ.",
                        firstName, lastName, phone, "", genderInput, email, password, confirmPassword);
                return;
            }

            if (dob.isAfter(LocalDate.now())) {
                forwardRegister(request, response,
                        "Ngày sinh không được lớn hơn ngày hiện tại.",
                        firstName, lastName, phone, "", genderInput, email, password, confirmPassword);
                return;
            }
        }

        Gender gender = null;

        if ("nam".equalsIgnoreCase(genderInput)) {
            gender = Gender.NAM;
        } else if ("nu".equalsIgnoreCase(genderInput)) {
            gender = Gender.NU;
        } else if (!genderInput.isEmpty()) {
            forwardRegister(request, response,
                    "Giới tính không hợp lệ.",
                    firstName, lastName, phone, dobStr, "", email, password, confirmPassword);
            return;
        }

        /*
         * Check trùng email và số điện thoại.
         * Không phân biệt PENDING hay ACTIVE vì PENDING vẫn đang giữ chỗ trong DB.
         */
        User existingUserByEmail = userDao.getUserByEmail(email);

        if (existingUserByEmail != null) {
            forwardRegister(request, response,
                    MSG_INFO_USED,
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword);
            return;
        }

        User existingUserByPhone = userDao.getUserByPhone(phone);

        if (existingUserByPhone != null) {
            forwardRegister(request, response,
                    MSG_INFO_USED,
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        /*
         * Màn register bây giờ chỉ tạo CUSTOMER.
         * Không nhận DELIVERY từ form dù form có gửi accountType.
         */
        int roleId = userDao.getRoleIdByName(REGISTER_ROLE);

        if (roleId <= 0) {
            forwardRegister(request, response,
                    "Không tìm thấy quyền CUSTOMER trong hệ thống.",
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .passwordHash(PasswordUtils.hashPassword(password))
                .gender(gender)
                .dateOfBirth(dob)
                .status(UserStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        int userId = userDao.insertUserWithRole(user, roleId);

        if (userId <= 0) {
            forwardRegister(request, response,
                    "Đăng ký thất bại. Vui lòng thử lại.",
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        try {
            sendOtp(email);
        } catch (Exception e) {
            e.printStackTrace();

            forwardVerifyOtp(request, response,
                    email,
                    "Tài khoản đã được tạo nhưng chưa gửi được mã OTP. Vui lòng bấm Gửi lại mã.");
            return;
        }

        redirectToVerifyOtp(request, response, email);
    }
}