package vn.edu.fpt.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.common.EmailUtils;
import vn.edu.fpt.common.OtpUtils;
import vn.edu.fpt.common.PasswordUtils;
import vn.edu.fpt.common.UploadImage;
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
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 15 * 1024 * 1024
)
public class RegisterAccountServlet extends HttpServlet {

    private static final String ACCOUNT_TYPE_CUSTOMER = "CUSTOMER";
    private static final String ACCOUNT_TYPE_DELIVERY = "DELIVERY";

    /*
     * Thông báo tổng quát cho các trường unique trong DB:
     * email, phone, id_card_number, license_plate.
     * Không phân biệt PENDING hay ACTIVE để tránh lộ chi tiết tài khoản.
     */
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

    private boolean isValidVietnamIdCardNumber(String idCardNumber) {
        if (idCardNumber == null) {
            return false;
        }

        String value = idCardNumber.trim();

        if (!Pattern.matches("^[0-9]{12}$", value)) {
            return false;
        }

        if (Pattern.matches("^(\\d)\\1{11}$", value)) {
            return false;
        }

        try {
            int provinceCode = Integer.parseInt(value.substring(0, 3));
            return provinceCode >= 1 && provinceCode <= 96;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeLicensePlate(String licensePlate) {
        if (licensePlate == null) {
            return "";
        }

        return licensePlate.trim()
                .toUpperCase()
                .replaceAll("[\\s\\-.]", "");
    }

    private boolean isValidVietnamLicensePlate(String licensePlate) {
        String normalized = normalizeLicensePlate(licensePlate);
        return Pattern.matches("^[1-9][0-9][A-Z][0-9A-Z]?[0-9]{4,5}$", normalized);
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasImageFile(Part part) {
        return part != null && part.getSize() > 0;
    }

    private boolean isValidImageFile(Part part) {
        if (!hasImageFile(part)) {
            return false;
        }

        String contentType = part.getContentType();

        return contentType != null
                && contentType.startsWith("image/")
                && part.getSize() <= 5 * 1024 * 1024;
    }

    private void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void prepareRegisterPage(HttpServletRequest request) {
        UserDAO userDao = new UserDAO();
        request.setAttribute("provinces", userDao.getAllProvincesForRegister());
        request.setAttribute("wards", userDao.getAllWardsForRegister());
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

    private void keepShipperFormData(HttpServletRequest request,
                                     String accountType,
                                     String idCardNumber,
                                     String licensePlate,
                                     String shipperProvinceId,
                                     String shipperWardId) {

        request.setAttribute("accountType", accountType);
        request.setAttribute("idCardNumber", idCardNumber);
        request.setAttribute("licensePlate", licensePlate);
        request.setAttribute("shipperProvinceId", shipperProvinceId);
        request.setAttribute("shipperWardId", shipperWardId);
    }

    private void forwardRegister(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String error,
                                 String accountType,
                                 String firstName,
                                 String lastName,
                                 String phone,
                                 String dob,
                                 String gender,
                                 String email,
                                 String password,
                                 String confirmPassword,
                                 String idCardNumber,
                                 String licensePlate,
                                 String shipperProvinceId,
                                 String shipperWardId)
            throws ServletException, IOException {

        request.setAttribute("error", error);

        keepFormData(request, firstName, lastName, phone, dob, gender, email, password, confirmPassword);
        keepShipperFormData(request, accountType, idCardNumber, licensePlate, shipperProvinceId, shipperWardId);

        prepareRegisterPage(request);

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

        request.setAttribute("accountType", ACCOUNT_TYPE_CUSTOMER);
        prepareRegisterPage(request);
        request.getRequestDispatcher("/public/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        UserDAO userDao = new UserDAO();

        /*
         * Chỉ dọn user chưa xác thực OTP quá 15 phút:
         * users.status = 'PENDING'
         *
         * Không ảnh hưởng shipper chờ duyệt:
         * users.status = 'ACTIVE'
         * shipper_approval_status = 'PENDING'
         */
        userDao.deleteExpiredPendingRegistrations(15);

        String accountType = request.getParameter("accountType");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String genderInput = request.getParameter("gender");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        String idCardNumber = request.getParameter("idCardNumber");
        String licensePlate = request.getParameter("licensePlate");
        String shipperProvinceIdStr = request.getParameter("shipperProvinceId");
        String shipperWardIdStr = request.getParameter("shipperWardId");

        accountType = accountType == null ? ACCOUNT_TYPE_CUSTOMER : accountType.trim().toUpperCase();
        firstName = firstName == null ? "" : firstName.trim();
        lastName = lastName == null ? "" : lastName.trim();
        phone = phone == null ? "" : phone.trim();
        dobStr = dobStr == null ? "" : dobStr.trim();
        genderInput = genderInput == null ? "" : genderInput.trim();
        email = email == null ? "" : email.trim().toLowerCase();
        password = password == null ? "" : password.trim();
        confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        idCardNumber = idCardNumber == null ? "" : idCardNumber.trim();
        licensePlate = normalizeLicensePlate(licensePlate);
        shipperProvinceIdStr = shipperProvinceIdStr == null ? "" : shipperProvinceIdStr.trim();
        shipperWardIdStr = shipperWardIdStr == null ? "" : shipperWardIdStr.trim();

        boolean registerAsShipper = ACCOUNT_TYPE_DELIVERY.equals(accountType);

        if (!ACCOUNT_TYPE_CUSTOMER.equals(accountType) && !ACCOUNT_TYPE_DELIVERY.equals(accountType)) {
            forwardRegister(request, response,
                    "Loại tài khoản đăng ký không hợp lệ.",
                    ACCOUNT_TYPE_CUSTOMER,
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        Part driverLicenseFrontPart = null;
        Part driverLicenseBackPart = null;

        if (registerAsShipper) {
            try {
                driverLicenseFrontPart = request.getPart("driverLicenseFront");
                driverLicenseBackPart = request.getPart("driverLicenseBack");
            } catch (Exception e) {
                forwardRegister(request, response,
                        "Không đọc được ảnh bằng lái xe. Vui lòng chọn lại ảnh.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }
        }

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || phone.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            forwardRegister(request, response,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!isValidName(firstName)) {
            forwardRegister(request, response,
                    "Họ không hợp lệ.",
                    accountType,
                    "", lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!isValidName(lastName)) {
            forwardRegister(request, response,
                    "Tên không hợp lệ.",
                    accountType,
                    firstName, "", phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!isValidEmail(email)) {
            forwardRegister(request, response,
                    "Email không hợp lệ.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!isValidVietnamPhone(phone)) {
            forwardRegister(request, response,
                    "Số điện thoại không hợp lệ.",
                    accountType,
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!isValidPassword(password)) {
            forwardRegister(request, response,
                    "Mật khẩu không hợp lệ. Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, email, "", confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (!password.equals(confirmPassword)) {
            forwardRegister(request, response,
                    "Mật khẩu xác nhận không khớp.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, email, password, "",
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        LocalDate dob = null;

        if (!dobStr.isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr);
            } catch (Exception e) {
                forwardRegister(request, response,
                        "Ngày sinh không hợp lệ.",
                        accountType,
                        firstName, lastName, phone, "", genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (dob.isAfter(LocalDate.now())) {
                forwardRegister(request, response,
                        "Ngày sinh không được lớn hơn ngày hiện tại.",
                        accountType,
                        firstName, lastName, phone, "", genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
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
                    accountType,
                    firstName, lastName, phone, dobStr, "", email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        Integer shipperProvinceId = parseInteger(shipperProvinceIdStr);
        Integer shipperWardId = parseInteger(shipperWardIdStr);

        if (registerAsShipper) {
            if (dobStr.isEmpty() || dob == null) {
                forwardRegister(request, response,
                        "Đối tác giao hàng bắt buộc nhập ngày sinh.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (gender == null) {
                forwardRegister(request, response,
                        "Đối tác giao hàng bắt buộc chọn giới tính.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (idCardNumber.isEmpty()) {
                forwardRegister(request, response,
                        "Đối tác giao hàng bắt buộc nhập số CCCD.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        "", licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (!isValidVietnamIdCardNumber(idCardNumber)) {
                forwardRegister(request, response,
                        "Số CCCD không hợp lệ.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        "", licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (licensePlate.isEmpty()) {
                forwardRegister(request, response,
                        "Đối tác giao hàng bắt buộc nhập biển số xe.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, "", shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (!isValidVietnamLicensePlate(licensePlate)) {
                forwardRegister(request, response,
                        "Biển số xe không hợp lệ.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, "", shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (shipperProvinceId == null || !userDao.isProvinceExist(shipperProvinceId)) {
                forwardRegister(request, response,
                        "Vui lòng chọn tỉnh/thành phố hoạt động hợp lệ.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, "", shipperWardIdStr);
                return;
            }

            if (shipperWardId == null || !userDao.isWardBelongsToProvince(shipperWardId, shipperProvinceId)) {
                forwardRegister(request, response,
                        "Vui lòng chọn xã/phường thuộc đúng tỉnh/thành phố hoạt động.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, "");
                return;
            }

            if (!isValidImageFile(driverLicenseFrontPart)) {
                forwardRegister(request, response,
                        "Ảnh bằng lái xe mặt trước không hợp lệ. Chỉ chấp nhận file ảnh tối đa 5MB.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            if (!isValidImageFile(driverLicenseBackPart)) {
                forwardRegister(request, response,
                        "Ảnh bằng lái xe mặt sau không hợp lệ. Chỉ chấp nhận file ảnh tối đa 5MB.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

        } else {
            idCardNumber = null;
            licensePlate = null;
            shipperProvinceId = null;
            shipperWardId = null;
        }

        /*
         * Check trùng tất cả thuộc tính unique.
         * Không phân biệt PENDING hay ACTIVE.
         * Vì PENDING dưới 15 phút vẫn giữ chỗ trong DB.
         * Nếu người dùng muốn sửa thông tin thì phải bấm link "Sửa thông tin đăng ký" ở màn OTP.
         */
        User existingUserByEmail = userDao.getUserByEmail(email);

        if (existingUserByEmail != null) {
            forwardRegister(request, response,
                    MSG_INFO_USED,
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, "", password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        User existingUserByPhone = userDao.getUserByPhone(phone);

        if (existingUserByPhone != null) {
            forwardRegister(request, response,
                    MSG_INFO_USED,
                    accountType,
                    firstName, lastName, "", dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        if (registerAsShipper) {
            User existingUserByIdCard = userDao.getUserByIdCardNumber(idCardNumber);

            if (existingUserByIdCard != null) {
                forwardRegister(request, response,
                        MSG_INFO_USED,
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        "", licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }

            User existingUserByLicensePlate = userDao.getUserByLicensePlate(licensePlate);

            if (existingUserByLicensePlate != null) {
                forwardRegister(request, response,
                        MSG_INFO_USED,
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, "", shipperProvinceIdStr, shipperWardIdStr);
                return;
            }
        }

        String roleName = registerAsShipper ? "DELIVERY" : "CUSTOMER";
        int roleId = userDao.getRoleIdByName(roleName);

        if (roleId <= 0) {
            forwardRegister(request, response,
                    "Không tìm thấy quyền đăng ký phù hợp trong hệ thống.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
            return;
        }

        String driverLicenseFrontUrl = null;
        String driverLicenseBackUrl = null;

        if (registerAsShipper) {
            try {
                driverLicenseFrontUrl = UploadImage.uploadImage(driverLicenseFrontPart, "driver_licenses");
                driverLicenseBackUrl = UploadImage.uploadImage(driverLicenseBackPart, "driver_licenses");

                if (driverLicenseFrontUrl == null || driverLicenseBackUrl == null) {
                    forwardRegister(request, response,
                            "Upload ảnh bằng lái xe thất bại. Vui lòng chọn lại ảnh.",
                            accountType,
                            firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                            idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();

                forwardRegister(request, response,
                        "Upload ảnh bằng lái xe thất bại. Vui lòng chọn lại ảnh và thử lại.",
                        accountType,
                        firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                        idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
                return;
            }
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

        int userId = userDao.insertUserWithRole(
                user,
                roleId,
                licensePlate,
                idCardNumber,
                shipperProvinceId,
                shipperWardId,
                driverLicenseFrontUrl,
                driverLicenseBackUrl
        );

        if (userId <= 0) {
            forwardRegister(request, response,
                    "Đăng ký thất bại. Vui lòng thử lại.",
                    accountType,
                    firstName, lastName, phone, dobStr, genderInput, email, password, confirmPassword,
                    idCardNumber, licensePlate, shipperProvinceIdStr, shipperWardIdStr);
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