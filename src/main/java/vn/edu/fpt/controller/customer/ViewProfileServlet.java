package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "ViewProfileServlet", urlPatterns = {"/customer/profile"})
public class ViewProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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

        return "Chưa cập nhật";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}