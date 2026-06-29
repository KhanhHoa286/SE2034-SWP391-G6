package vn.edu.fpt.controller.customer;

/*
 * Các import jakarta.servlet này thuộc dependency:
 *
 * <dependency>
 *     <groupId>jakarta.servlet</groupId>
 *     <artifactId>jakarta.servlet-api</artifactId>
 * </dependency>
 *
 * Dùng cho Servlet, request, response, session, upload file.
 */
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/*
 * Các class này là class có sẵn trong project.
 */
import vn.edu.fpt.common.UploadImage;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.model.User;

/*
 * Các import này là thư viện gốc của Java.
 * Không cần thêm Maven dependency.
 */
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
 * @WebServlet:
 * - Khai báo URL servlet xử lý.
 * - Khi user vào /customer/profile/edit thì Tomcat gọi servlet này.
 *
 * URL này phải trùng với:
 * - href của nút "Chỉnh sửa hồ sơ" trong view-profile.jsp
 * - action của form trong edit-profile.jsp
 *
 * Thuộc dependency:
 * - jakarta.servlet:jakarta.servlet-api
 */
@WebServlet(name = "EditProfileServlet", urlPatterns = {"/customer/profile/edit"})

/*
 * @MultipartConfig:
 * - Bắt buộc có nếu form upload file.
 * - Vì edit-profile.jsp có input type="file" name="avatar".
 *
 * Nếu thiếu:
 * - request.getPart("avatar") sẽ lỗi.
 *
 * Thuộc dependency:
 * - jakarta.servlet:jakarta.servlet-api
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class EditProfileServlet extends HttpServlet {

    /*
     * UserDAO dùng để thao tác với bảng users.
     *
     * Servlet không viết SQL trực tiếp.
     * Servlet chỉ nhận request, validate dữ liệu rồi gọi DAO.
     */
    private final UserDAO userDAO = new UserDAO();

    /*
     * Đường dẫn JSP nội bộ để forward.
     *
     * Đây không phải URL user gõ trên trình duyệt.
     */
    private static final String EDIT_PROFILE_JSP = "/customer/account/edit-profile.jsp";

    /*
     * Avatar mặc định.
     *
     * Dùng khi:
     * - user chưa có avatar_url
     * - user bấm Gỡ bỏ
     * - avatar_url trong DB bị null/rỗng
     */
    private static final String DEFAULT_AVATAR =
            "https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg";

    /*
     * doGet chạy khi user mở trang:
     *
     * GET /customer/profile/edit
     *
     * Nhiệm vụ:
     * 1. Kiểm tra login.
     * 2. Lấy userId từ session.
     * 3. Lấy user mới nhất từ DB.
     * 4. Set dữ liệu cho JSP.
     * 5. Forward sang edit-profile.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        Integer userId = getLoggedInUserId(session);

        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User profileUser = userDAO.getUserById(userId);

        if (profileUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        prepareEditProfileData(request, profileUser);

        request.getRequestDispatcher(EDIT_PROFILE_JSP)
                .forward(request, response);
    }

    /*
     * doPost chạy khi user bấm nút "Lưu thay đổi".
     *
     * POST /customer/profile/edit
     *
     * Lý do tách logic ra processUpdateProfile():
     * - doPost chỉ phụ trách bắt lỗi tổng.
     * - processUpdateProfile xử lý logic thật.
     * - Nếu có lỗi bất ngờ, user sẽ được forward lại form,
     *   không bị Chrome báo ERR_CONNECTION_RESET.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            processUpdateProfile(request, response);

        } catch (Exception e) {
            /*
             * In lỗi ra console Tomcat để debug.
             */
            e.printStackTrace();

            HttpSession session = request.getSession(false);
            Integer userId = getLoggedInUserId(session);

            User profileUser = null;

            if (userId != null && userId > 0) {
                profileUser = userDAO.getUserById(userId);
            }

            /*
             * Nếu không lấy được user thì quay về login.
             */
            if (profileUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            /*
             * Set lỗi để JSP hiển thị.
             */
            request.setAttribute("errorMessage",
                    "Có lỗi xảy ra khi cập nhật hồ sơ: " + e.getMessage());

            prepareEditProfileData(request, profileUser);

            request.getRequestDispatcher(EDIT_PROFILE_JSP)
                    .forward(request, response);
        }
    }

    /*
     * Method xử lý logic update profile thật.
     *
     * Luồng:
     * 1. Lấy user từ session.
     * 2. Lấy dữ liệu form.
     * 3. Validate.
     * 4. Upload avatar nếu có.
     * 5. Update DB.
     * 6. Update session.
     * 7. Redirect về /customer/profile.
     */
    private void processUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        Integer userId = getLoggedInUserId(session);

        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Lấy user hiện tại từ DB.
         *
         * Cần currentUser để:
         * - giữ email vì email không cho sửa.
         * - giữ avatar cũ nếu user không upload ảnh mới.
         * - giữ status, createdAt khi render lại form nếu lỗi.
         */
        User currentUser = userDAO.getUserById(userId);

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Lấy dữ liệu từ form.
         *
         * Các name này phải trùng với edit-profile.jsp:
         * - fullName
         * - phone
         * - gender
         * - dateOfBirth
         * - removeAvatar
         */
        String fullName = trim(request.getParameter("fullName"));
        String phone = normalizePhone(request.getParameter("phone"));
        String genderRaw = trim(request.getParameter("gender"));
        String dateOfBirthRaw = trim(request.getParameter("dateOfBirth"));
        String removeAvatarRaw = trim(request.getParameter("removeAvatar"));

        /*
         * Validate họ tên và số điện thoại trước.
         */
        String errorMessage = validateBasicInput(fullName, phone);

        Gender gender = null;
        LocalDate dateOfBirth = null;

        /*
         * Parse gender từ String sang enum Gender.
         */
        if (errorMessage == null) {
            try {
                gender = parseGender(genderRaw);
            } catch (IllegalArgumentException e) {
                errorMessage = "Giới tính không hợp lệ.";
            }
        }

        /*
         * Parse ngày sinh từ String yyyy-MM-dd sang LocalDate.
         */
        if (errorMessage == null) {
            try {
                dateOfBirth = parseDateOfBirth(dateOfBirthRaw);
            } catch (IllegalArgumentException e) {
                errorMessage = e.getMessage();
            }
        }

        /*
         * Check số điện thoại có bị trùng với user khác không.
         *
         * Không được dùng isPhoneExist(phone) thường,
         * vì nếu user giữ nguyên số điện thoại cũ thì vẫn bị báo trùng chính mình.
         */
        /*
         * Validate số điện thoại có đang thuộc tài khoản khác không.
         *
         * Check cả:
         * - PENDING: tài khoản đang chờ xác thực
         * - ACTIVE: tài khoản đang hoạt động
         * - LOCKED: tài khoản bị khóa
         *
         * Không được cho dùng lại số điện thoại của bất kỳ tài khoản nào khác,
         * vì cột phone trong DB đang là UNIQUE.
         */
        if (errorMessage == null) {
            String phoneOwnerStatus = userDAO.findPhoneOwnerStatusForOtherUser(phone, userId);

            if (phoneOwnerStatus != null) {
                if ("PENDING".equalsIgnoreCase(phoneOwnerStatus)) {
                    errorMessage = "Số điện thoại này đang được dùng bởi một tài khoản đang chờ xác thực.";
                } else if ("ACTIVE".equalsIgnoreCase(phoneOwnerStatus)) {
                    errorMessage = "Số điện thoại này đã được dùng bởi một tài khoản đang hoạt động.";
                } else if ("LOCKED".equalsIgnoreCase(phoneOwnerStatus)) {
                    errorMessage = "Số điện thoại này đã được dùng bởi một tài khoản đang bị khóa.";
                } else {
                    errorMessage = "Số điện thoại này đã được dùng bởi tài khoản khác.";
                }
            }
        }

        /*
         * Avatar mặc định ban đầu là avatar hiện tại trong DB.
         */
        String avatarUrl = currentUser.getAvatarUrl();

        if (isBlank(avatarUrl)) {
            avatarUrl = DEFAULT_AVATAR;
        }

        boolean removeAvatar = "true".equalsIgnoreCase(removeAvatarRaw);

        /*
         * Xử lý upload avatar.
         *
         * Nếu upload lỗi:
         * - Không crash app.
         * - Gán errorMessage.
         * - Forward lại form.
         */
        if (errorMessage == null) {
            try {
                Part avatarPart = request.getPart("avatar");

                if (removeAvatar) {
                    avatarUrl = DEFAULT_AVATAR;

                } else if (hasUploadedFile(avatarPart)) {
                    avatarUrl = UploadImage.uploadImage(avatarPart, "moda/users/avatar");
                }

            } catch (Throwable e) {
                /*
                 * Dùng Throwable để bắt cả lỗi Cloudinary/config nếu UploadImage lỗi lúc khởi tạo.
                 * Nếu chỉ catch Exception thì một số lỗi kiểu ExceptionInInitializerError sẽ không bắt được.
                 */
                e.printStackTrace();

                Throwable rootCause = e.getCause() != null ? e.getCause() : e;

                errorMessage = "Upload ảnh thất bại: " + rootCause.getMessage();
            }
        }

        /*
         * DB users tách first_name và last_name.
         * UI chỉ có một ô Họ và tên.
         */
        String[] nameParts = splitFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        /*
         * Nếu có lỗi validate hoặc upload:
         * - Không update DB.
         * - Giữ lại dữ liệu user vừa nhập.
         * - Forward lại edit-profile.jsp.
         */
        if (errorMessage != null) {
            User formUser = new User();

            formUser.setUserId(currentUser.getUserId());
            formUser.setFirstName(firstName);
            formUser.setLastName(lastName);
            formUser.setEmail(currentUser.getEmail());
            formUser.setPhone(phone);
            formUser.setAvatarUrl(avatarUrl);
            formUser.setGender(gender);
            formUser.setDateOfBirth(dateOfBirth);
            formUser.setStatus(currentUser.getStatus());
            formUser.setCreatedAt(currentUser.getCreatedAt());

            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("dateOfBirthValue", dateOfBirthRaw);

            prepareEditProfileData(request, formUser);

            request.getRequestDispatcher(EDIT_PROFILE_JSP)
                    .forward(request, response);
            return;
        }

        /*
         * Gọi DAO update DB.
         *
         * Method này chỉ update:
         * - first_name
         * - last_name
         * - phone
         * - gender
         * - date_of_birth
         * - avatar_url
         */
        boolean updated = userDAO.updateProfile(
                userId,
                firstName,
                lastName,
                phone,
                gender,
                dateOfBirth,
                avatarUrl
        );

        /*
         * Nếu update thất bại thì forward lại JSP.
         */
        if (!updated) {
            request.setAttribute("errorMessage", "Cập nhật hồ sơ thất bại. Vui lòng thử lại.");
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("dateOfBirthValue", dateOfBirthRaw);

            prepareEditProfileData(request, currentUser);

            request.getRequestDispatcher(EDIT_PROFILE_JSP)
                    .forward(request, response);
            return;
        }

        /*
         * Sau khi update thành công:
         * - Lấy lại user mới nhất từ DB.
         * - Cập nhật session.
         *
         * Nếu không update session:
         * - header/avatar vẫn hiện dữ liệu cũ.
         */
        User updatedUser = userDAO.getUserById(userId);

        if (updatedUser != null) {
            session.setAttribute("user", updatedUser);
            session.setAttribute("userId", updatedUser.getUserId());
            session.setAttribute("fullName", buildFullName(updatedUser));
        }

        /*
         * Redirect về trang xem profile.
         *
         * Dùng redirect sau POST để tránh F5 submit lại form.
         */
        response.sendRedirect(request.getContextPath() + "/customer/profile");
    }

    /*
     * Chuẩn bị dữ liệu cho edit-profile.jsp.
     *
     * Cả doGet và doPost khi lỗi đều dùng method này.
     */
    private void prepareEditProfileData(HttpServletRequest request, User profileUser) {
        String fullName = buildFullName(profileUser);

        String avatarUrl = isBlank(profileUser.getAvatarUrl())
                ? DEFAULT_AVATAR
                : profileUser.getAvatarUrl();

        String genderValue = profileUser.getGender() == null
                ? ""
                : profileUser.getGender().name();

        String dateOfBirthValue = profileUser.getDateOfBirth() == null
                ? ""
                : profileUser.getDateOfBirth().toString();

        request.setAttribute("profileUser", profileUser);
        request.setAttribute("fullNameValue", fullName);
        request.setAttribute("avatarUrl", avatarUrl);
        request.setAttribute("defaultAvatar", DEFAULT_AVATAR);
        request.setAttribute("genderValue", genderValue);
        request.setAttribute("dateOfBirthValue", dateOfBirthValue);
    }

    /*
     * Lấy userId từ session.
     *
     * Project có thể lưu:
     * - session.setAttribute("userId", userId)
     * hoặc:
     * - session.setAttribute("user", userObject)
     */
    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");

        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }

        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        Object rawUser = session.getAttribute("user");

        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        return null;
    }

    /*
     * Validate họ tên và số điện thoại.
     */
    private String validateBasicInput(String fullName, String phone) {
        if (isBlank(fullName)) {
            return "Họ và tên không được để trống.";
        }

        if (fullName.length() > 100) {
            return "Họ và tên không được vượt quá 100 ký tự.";
        }

        if (isBlank(phone)) {
            return "Số điện thoại không được để trống.";
        }

        /*
         * Validate số điện thoại di động Việt Nam.
         *
         * Hợp lệ:
         * - 03xxxxxxxx
         * - 05xxxxxxxx
         * - 07xxxxxxxx
         * - 08xxxxxxxx
         * - 09xxxxxxxx
         *
         * Tổng cộng đúng 10 chữ số.
         */
        if (!phone.matches("^(03|05|07|08|09)\\d{8}$")) {
            return "Số điện thoại không hợp lệ. Vui lòng nhập số di động Việt Nam gồm 10 chữ số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.";
        }

        return null;
    }

    /*
     * Parse gender từ String sang enum Gender.
     */
    private Gender parseGender(String genderRaw) {
        if (isBlank(genderRaw)) {
            return null;
        }

        return Gender.valueOf(genderRaw.trim().toUpperCase());
    }

    /*
     * Parse ngày sinh từ String yyyy-MM-dd sang LocalDate.
     */
    private LocalDate parseDateOfBirth(String dateOfBirthRaw) {
        if (isBlank(dateOfBirthRaw)) {
            return null;
        }

        try {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthRaw);

            if (dateOfBirth.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại.");
            }

            if (dateOfBirth.isBefore(LocalDate.now().minusYears(120))) {
                throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
            }

            return dateOfBirth;

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày sinh không đúng định dạng.");
        }
    }

    /*
     * Check user có thật sự upload file không.
     */
    private boolean hasUploadedFile(Part part) {
        return part != null
                && part.getSize() > 0
                && part.getSubmittedFileName() != null
                && !part.getSubmittedFileName().trim().isEmpty();
    }

    /*
     * Tách fullName thành firstName và lastName.
     *
     * Ví dụ:
     * "Nguyễn Văn A"
     *
     * firstName = "Nguyễn Văn"
     * lastName  = "A"
     */
    private String[] splitFullName(String fullName) {
        String normalized = fullName == null
                ? ""
                : fullName.trim().replaceAll("\\s+", " ");

        if (normalized.isEmpty()) {
            return new String[]{"", ""};
        }

        int lastSpaceIndex = normalized.lastIndexOf(" ");

        if (lastSpaceIndex < 0) {
            return new String[]{normalized, ""};
        }

        String firstName = normalized.substring(0, lastSpaceIndex).trim();
        String lastName = normalized.substring(lastSpaceIndex + 1).trim();

        return new String[]{firstName, lastName};
    }

    /*
     * Ghép firstName + lastName thành fullName để hiển thị.
     */
    private String buildFullName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();

        String fullName = (firstName + " " + lastName).trim();

        return fullName.isEmpty() ? "Khách hàng MODA" : fullName;
    }

    /*
     * Chuẩn hóa số điện thoại.
     *
     * User có thể nhập:
     * 095 777 7777
     * 095-777-7777
     * 095.777.7777
     *
     * Server lưu thành:
     * 0957777777
     */
    private String normalizePhone(String phone) {
        if (phone == null) {
            return "";
        }

        return phone.trim().replaceAll("[\\s.\\-]", "");
    }

    /*
     * trim an toàn.
     */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /*
     * Check chuỗi null hoặc rỗng.
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}