package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.ShopStatus;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "ViewProfileServlet", urlPatterns = {"/customer/profile"})
public class ViewProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    private static final String DEFAULT_AVATAR =
            "https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

        String fullNameText = buildFullName(profileUser);
        String emailText = displayText(profileUser.getEmail());
        String phoneText = displayText(profileUser.getPhone());
        String genderText = displayGender(profileUser.getGender());

        String dateOfBirthText = profileUser.getDateOfBirth() == null
                ? "Chưa cập nhật"
                : profileUser.getDateOfBirth().format(DATE_FORMAT);

        String createdAtText = profileUser.getCreatedAt() == null
                ? "Chưa cập nhật"
                : profileUser.getCreatedAt().format(DATE_TIME_FORMAT);

        String avatarUrl = isBlank(profileUser.getAvatarUrl())
                ? DEFAULT_AVATAR
                : profileUser.getAvatarUrl();

        request.setAttribute("profileUser", profileUser);
        request.setAttribute("fullNameText", fullNameText);
        request.setAttribute("emailText", emailText);
        request.setAttribute("phoneText", phoneText);
        request.setAttribute("genderText", genderText);
        request.setAttribute("dateOfBirthText", dateOfBirthText);
        request.setAttribute("createdAtText", createdAtText);
        request.setAttribute("avatarUrl", avatarUrl);
        request.setAttribute("defaultAvatar", DEFAULT_AVATAR);

        request.setAttribute("phoneMissing", isBlank(profileUser.getPhone()));
        request.setAttribute("genderMissing", profileUser.getGender() == null
                || (profileUser.getGender() != Gender.NAM && profileUser.getGender() != Gender.NU));
        request.setAttribute("dateOfBirthMissing", profileUser.getDateOfBirth() == null);
        request.setAttribute("createdAtMissing", profileUser.getCreatedAt() == null);

        /*
         * Cập nhật lại session để public/header.jsp dùng avatar/name mới nhất.
         */
        session.setAttribute("user", profileUser);
        session.setAttribute("userId", profileUser.getUserId());
        session.setAttribute("fullName", fullNameText);
        boolean hasSellerAccount = customerDAO.hasSellerAccount(profileUser.getUserId());
        boolean hasPendingSellerRegistration = !hasSellerAccount && customerDAO.hasPendingSellerRegistration(profileUser.getUserId());
        boolean hasRejectedSellerRegistration = !hasSellerAccount
                && customerDAO.hasRejectedSellerRegistration(profileUser.getUserId());

        // Kiểm tra nếu shop bị admin khóa (SUSPENDED):
        // hasSellerAccount() chỉ trả về true khi status=ACTIVE, nên cần query riêng
        boolean shopSuspended = false;
        if (!hasSellerAccount && !hasPendingSellerRegistration) {
            Shop shop = shopDAO.getShopByOwnerId(profileUser.getUserId());
            if (shop != null && ShopStatus.SUSPENDED.equals(shop.getStatus())) {
                // Shop tồn tại nhưng bị khóa — vẫn coi là có tài khoản người bán
                hasSellerAccount = true;
                shopSuspended = true;
            }
        }

        session.setAttribute("hasSellerAccount", hasSellerAccount);
        session.setAttribute("hasPendingSellerRegistration", hasPendingSellerRegistration);
        session.setAttribute("hasRejectedSellerRegistration", hasRejectedSellerRegistration);
        request.setAttribute("hasSellerAccount", hasSellerAccount);
        request.setAttribute("hasPendingSellerRegistration", hasPendingSellerRegistration);
        request.setAttribute("hasRejectedSellerRegistration", hasRejectedSellerRegistration);
        request.setAttribute("shopSuspended", shopSuspended);
        session.setAttribute("shopSuspended", shopSuspended);

        if ("1".equals(request.getParameter("shopCreated")) || "1".equals(request.getParameter("shopPending"))) {
            request.setAttribute("successMessage", "Tạo shop thành công, yêu cầu tạo đang được kiểm duyệt.");
        }

        request.getRequestDispatcher("/customer/account/view-profile.jsp")
                .forward(request, response);
    }

    private Integer getLoggedInUserId(HttpSession session) {
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

    private String buildFullName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();

        String fullName = (firstName + " " + lastName).trim();

        return fullName.isEmpty() ? "Khách hàng MODA" : fullName;
    }

    private String displayText(String value) {
        return isBlank(value) ? "Chưa cập nhật" : value.trim();
    }

    /*
     * Chuyển enum Gender thành text tiếng Việt để hiển thị ở view-profile.jsp.
     *
     * DB/model lưu:
     * - NAM
     * - NU
     * - UNISEX
     *
     * UI hiển thị:
     * - Nam
     * - Nữ
     * - Khác
     */
    private String displayGender(Gender gender) {
        if (gender == null) {
            return "Chưa cập nhật";
        }

        if (gender == Gender.NAM) {
            return "Nam";
        }

        if (gender == Gender.NU) {
            return "Nữ";
        }

        if (gender == Gender.UNISEX) {
            return "Khác";
        }

        return "Chưa cập nhật";
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null : getLoggedInUserId(session);
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"confirmRejectedShop".equals(request.getParameter("action"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (customerDAO.cancelRejectedSellerRegistration(userId)) {
            session.removeAttribute("sellerIdentityCompleted");
            session.setAttribute("hasSellerAccount", false);
            session.setAttribute("hasPendingSellerRegistration", false);
            session.setAttribute("hasRejectedSellerRegistration", false);
            response.sendRedirect(request.getContextPath() + "/customer/profile?retryConfirmed=1");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/customer/profile?retryError=1");
    }
}
