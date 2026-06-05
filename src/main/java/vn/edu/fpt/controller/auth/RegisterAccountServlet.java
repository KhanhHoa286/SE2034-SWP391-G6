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

    private boolean isValidEmail(String email) {
        return email != null
                && Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private boolean isValidVietnamPhone(String phone) {
        return phone != null
                && Pattern.matches("^(0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-9]|9[0-9])[0-9]{7}$", phone);
    }

    private boolean isValidName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && name.trim().length() <= 50
                && Pattern.matches("^[\\p{L}\\s'-]+$", name.trim());
    }

    /*
     * Mật khẩu:
     * - Chỉ gồm chữ số 0-9
     * - Dài từ 6 đến 32 số
     * - Không được để trống
     */
    private boolean isValidPassword(String password) {
        return password != null && Pattern.matches("^[0-9]{6,32}$", password);
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
        keepFormData(request, firstName, lastName, phone, dob, gender, email, password, confirmPassword);
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/public/auth/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserDAO userDao = new UserDAO();

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
                    "Họ không hợp lệ. Họ chỉ được chứa chữ cái, khoảng trắng, dấu nháy đơn hoặc dấu gạch ngang và tối đa 50 ký tự.",
                    "", lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidName(lastName)) {
            forwardRegister(request, response,
                    "Tên không hợp lệ. Tên chỉ được chứa chữ cái, khoảng trắng, dấu nháy đơn hoặc dấu gạch ngang và tối đa 50 ký tự.",
                    firstName, "", phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidEmail(email)) {
            forwardRegister(request, response,
                    "Email không hợp lệ. Vui lòng nhập đúng định dạng, ví dụ: example@gmail.com.",
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword);
            return;
        }

        if (!isValidVietnamPhone(phone)) {
            forwardRegister(request, response,
                    "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam gồm 10 chữ số, bắt đầu bằng 0.",
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        if (!isValidPassword(password)) {
            forwardRegister(request, response,
                    "Mật khẩu chỉ được gồm các chữ số 0-9, dài từ 6 đến 32 số và không được để trống.",
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
        }

        User existingUserByEmail = userDao.getUserByEmail(email);
        User existingUserByPhone = userDao.getUserByPhone(phone);

        /*
         * Trường hợp email đã tồn tại.
         */
        if (existingUserByEmail != null) {

            /*
             * Nếu tài khoản đang PENDING:
             * Cho phép đăng ký lại nếu đúng cùng email + cùng số điện thoại.
             * Không insert user mới, chỉ update lại thông tin tạm và gửi OTP mới.
             */
            if (existingUserByEmail.getStatus() == UserStatus.PENDING) {

                boolean samePhone = existingUserByEmail.getPhone() != null
                        && existingUserByEmail.getPhone().equals(phone);

                if (!samePhone) {
                    forwardRegister(request, response,
                            "Email này đang chờ xác thực với một số điện thoại khác. Vui lòng nhập đúng số điện thoại đã đăng ký trước đó.",
                            firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
                    return;
                }

                if (existingUserByPhone != null
                        && existingUserByPhone.getUserId() != null
                        && !existingUserByPhone.getUserId().equals(existingUserByEmail.getUserId())) {

                    forwardRegister(request, response,
                            "Số điện thoại này đã được dùng cho tài khoản khác.",
                            firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
                    return;
                }

                User pendingUserUpdate = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone(phone)
                        .passwordHash(PasswordUtils.hashPassword(password))
                        .gender(gender)
                        .dateOfBirth(dob)
                        .status(UserStatus.PENDING)
                        .createdAt(existingUserByEmail.getCreatedAt())
                        .build();

                boolean updated = userDao.updatePendingUserBeforeResendOtp(
                        existingUserByEmail.getUserId(),
                        pendingUserUpdate
                );

                if (!updated) {
                    forwardRegister(request, response,
                            "Không cập nhật được tài khoản đang chờ xác thực. Vui lòng thử lại.",
                            firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
                    return;
                }

                try {
                    sendOtp(email);

                    String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/public/auth/verify-otp.jsp?email=" + encodedEmail);
                    return;

                } catch (Exception e) {
                    e.printStackTrace();

                    forwardRegister(request, response,
                            "Tài khoản đang chờ xác thực nhưng hệ thống chưa gửi lại được OTP. Vui lòng thử lại sau.",
                            firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
                    return;
                }
            }

            /*
             * Email đã ACTIVE hoặc LOCKED thì không cho đăng ký lại.
             * Chỉ xóa ô email, giữ các ô khác.
             */
            forwardRegister(request, response,
                    "Email đã tồn tại. Vui lòng dùng email khác hoặc đăng nhập.",
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword);
            return;
        }

        /*
         * Email chưa tồn tại nhưng số điện thoại đã tồn tại.
         */
        if (existingUserByPhone != null) {

            if (existingUserByPhone.getStatus() == UserStatus.PENDING) {
                forwardRegister(request, response,
                        "Số điện thoại này đang chờ xác thực với email khác. Vui lòng nhập đúng email đã đăng ký trước đó hoặc dùng số điện thoại khác.",
                        firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword);
                return;
            }

            forwardRegister(request, response,
                    "Số điện thoại đã tồn tại. Vui lòng dùng số điện thoại khác.",
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        int customerRoleId = userDao.getRoleIdByName("CUSTOMER");



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

        int userId = userDao.insertUserWithRole(user, customerRoleId);

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

            forwardRegister(request, response,
                    "Tài khoản đã được tạo nhưng chưa gửi được mã OTP. Vui lòng kiểm tra thư rác hoặc bấm đăng ký lại bằng đúng email và số điện thoại này để gửi lại mã.",
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword);
            return;
        }

        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/public/auth/verify-otp.jsp?email=" + encodedEmail);
    }
}