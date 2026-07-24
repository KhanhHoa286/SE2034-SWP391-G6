package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.common.UploadImage;
import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "AddSellerAccountServlet", urlPatterns = {
        "/seller-register",
        "/customer/seller-register",
        "/customer/add-seller-account"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 20 * 1024 * 1024,
        maxRequestSize = 45 * 1024 * 1024
)
public class AddSellerAccountServlet extends HttpServlet {

    private static final String FORM_PAGE = "/customer/seller_register/add-seller-account.jsp";
    private static final long MAX_ID_IMAGE_SIZE = 5L * 1024L * 1024L;

    private final CustomerDAO customerDAO = new CustomerDAO();

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

        if (redirectIfRegistrationAlreadyHandled(session, userId, request, response)) {
            return;
        }

        if (customerDAO.hasCompletedSellerIdentity(userId)) {
            response.sendRedirect(request.getContextPath() + "/add-shop");
            return;
        }

        request.getRequestDispatcher(FORM_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (redirectIfRegistrationAlreadyHandled(session, userId, request, response)) {
            return;
        }

        Map<String, String> errors = new HashMap<>();
        Map<String, String> oldInput = new HashMap<>();

        Collection<Part> formParts = getPartsSafely(request, errors);
        if (!errors.isEmpty()) {
            forwardForm(request, response, errors, oldInput);
            return;
        }

        String legalFullName = readFormValue(formParts, request, "legalFullName");
        String citizenId = readFormValue(formParts, request, "citizenId");
        String issueDateRaw = readFormValue(formParts, request, "citizenIdIssueDate");
        String citizenIdIssuePlace = readFormValue(formParts, request, "citizenIdIssuePlace");
        String permanentAddress = readFormValue(formParts, request, "permanentAddress");

        oldInput.put("legalFullName", legalFullName);
        oldInput.put("citizenId", citizenId);
        oldInput.put("citizenIdIssueDate", issueDateRaw);
        oldInput.put("citizenIdIssuePlace", citizenIdIssuePlace);
        oldInput.put("permanentAddress", permanentAddress);

        LocalDate citizenIdIssueDate = parseIssueDate(issueDateRaw, errors);

        validateText(errors, "legalFullName", legalFullName, "Vui lòng nhập họ tên theo căn cước công dân.", 3, 120);

        if (citizenId.isEmpty()) {
            errors.put("citizenId", "Vui lòng nhập số căn cước công dân.");
        } else if (!citizenId.matches("\\d{12}")) {
            errors.put("citizenId", "Căn cước công dân phải gồm đúng 12 chữ số.");
        } else if (customerDAO.citizenIdExistsForOtherUser(citizenId, userId)) {
            errors.put("citizenId", "Số căn cước công dân này đã được sử dụng.");
        }

        validateText(errors, "citizenIdIssuePlace", citizenIdIssuePlace, "Vui lòng nhập nơi cấp căn cước công dân.", 3, 255);
        validateText(errors, "permanentAddress", permanentAddress, "Vui lòng nhập địa chỉ thường trú.", 5, 500);

        Part frontPart = getPartSafely(formParts, "frontIdImage");
        Part backPart = getPartSafely(formParts, "backIdImage");
        if (!hasFile(frontPart)) {
            errors.put("frontIdImage", "Vui lòng tải lên ảnh mặt trước căn cước công dân.");
        } else {
            validateImagePart(errors, frontPart, "frontIdImage", "Ảnh mặt trước căn cước công dân");
        }

        if (!hasFile(backPart)) {
            errors.put("backIdImage", "Vui lòng tải lên ảnh mặt sau căn cước công dân.");
        } else {
            validateImagePart(errors, backPart, "backIdImage", "Ảnh mặt sau căn cước công dân");
        }

        String frontImageUrl = null;
        String backImageUrl = null;
        if (errors.isEmpty()) {
            try {
                if (hasFile(frontPart)) {
                    frontImageUrl = saveIdImage(frontPart, "front");
                    if (frontImageUrl == null || frontImageUrl.trim().isEmpty()) {
                        errors.put("frontIdImage", "Không thể tải ảnh mặt trước lên hệ thống.");
                    }
                }
                if (hasFile(backPart)) {
                    backImageUrl = saveIdImage(backPart, "back");
                    if (backImageUrl == null || backImageUrl.trim().isEmpty()) {
                        errors.put("backIdImage", "Không thể tải ảnh mặt sau lên hệ thống.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors.put("general", "Không thể tải ảnh căn cước lên hệ thống. Vui lòng thử lại sau.");
            }
        }

        if (!errors.isEmpty()) {
            forwardForm(request, response, errors, oldInput);
            return;
        }

        boolean updated = customerDAO.updateSellerIdentity(
                userId,
                legalFullName,
                citizenId,
                citizenIdIssueDate,
                citizenIdIssuePlace,
                permanentAddress,
                frontImageUrl,
                backImageUrl
        );

        if (!updated) {
            errors.put("general", "Không thể lưu thông tin người bán. Vui lòng thử lại sau.");
            forwardForm(request, response, errors, oldInput);
            return;
        }

        session.setAttribute("sellerIdentityCompleted", true);
        response.sendRedirect(request.getContextPath() + "/add-shop");
    }

    private boolean redirectIfRegistrationAlreadyHandled(HttpSession session,
                                                         int userId,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws IOException {
        if (customerDAO.hasSellerAccount(userId)) {
            session.setAttribute("hasSellerAccount", true);
            session.setAttribute("hasPendingSellerRegistration", false);
            response.sendRedirect(request.getContextPath() + "/seller/orders");
            return true;
        }

        boolean retryingRejectedRegistration =
                Boolean.TRUE.equals(session.getAttribute("sellerRegistrationRetry"));
        if (customerDAO.hasPendingSellerRegistration(userId) && !retryingRejectedRegistration) {
            session.setAttribute("hasSellerAccount", false);
            session.setAttribute("hasPendingSellerRegistration", true);
            response.sendRedirect(request.getContextPath() + "/customer/profile?shopPending=1");
            return true;
        }

        return false;
    }

    private void forwardForm(HttpServletRequest request,
                             HttpServletResponse response,
                             Map<String, String> errors,
                             Map<String, String> oldInput) throws ServletException, IOException {
        request.setAttribute("errors", errors);
        request.setAttribute("oldInput", oldInput);
        request.getRequestDispatcher(FORM_PAGE).forward(request, response);
    }

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

        Object rawAccount = session.getAttribute("account");
        if (rawAccount instanceof User) {
            return ((User) rawAccount).getUserId();
        }

        return null;
    }

    private LocalDate parseIssueDate(String rawValue, Map<String, String> errors) {
        if (rawValue == null || rawValue.isEmpty()) {
            return null;
        }

        try {
            LocalDate parsed = LocalDate.parse(rawValue);
            if (parsed.isAfter(LocalDate.now())) {
                errors.put("citizenIdIssueDate", "Ngày cấp không được lớn hơn ngày hiện tại.");
            }
            return parsed;
        } catch (DateTimeParseException e) {
            errors.put("citizenIdIssueDate", "Ngày cấp căn cước không hợp lệ.");
            return null;
        }
    }

    private void validateText(Map<String, String> errors,
                              String field,
                              String value,
                              String requiredMessage,
                              int minLength,
                              int maxLength) {
        if (value == null || value.isEmpty()) {
            errors.put(field, requiredMessage);
        } else if (value.length() < minLength || value.length() > maxLength) {
            errors.put(field, "Độ dài phải từ " + minLength + " đến " + maxLength + " ký tự.");
        }
    }

    private Collection<Part> getPartsSafely(HttpServletRequest request, Map<String, String> errors) {
        try {
            return request.getParts();
        } catch (IllegalStateException e) {
            errors.put("general", "Dung lượng ảnh tải lên quá lớn. Mỗi ảnh căn cước tối đa 5MB, vui lòng chọn ảnh nhỏ hơn.");
            return Collections.emptyList();
        } catch (Exception e) {
            errors.put("general", "Không thể đọc dữ liệu đăng ký. Vui lòng kiểm tra lại thông tin và thử lại.");
            return Collections.emptyList();
        }
    }

    private Part getPartSafely(Collection<Part> parts, String name) {
        if (parts == null || parts.isEmpty()) {
            return null;
        }

        for (Part part : parts) {
            if (name.equals(part.getName())) {
                return part;
            }
        }

        return null;
    }

    private String readFormValue(Collection<Part> parts, HttpServletRequest request, String name) {
        if (parts != null && !parts.isEmpty()) {
            for (Part part : parts) {
                if (!name.equals(part.getName()) || part.getSubmittedFileName() != null) {
                    continue;
                }

                try (InputStream inputStream = part.getInputStream()) {
                    return clean(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                    break;
                }
            }
        }

        return clean(request.getParameter(name));
    }

    private void validateImagePart(Map<String, String> errors,
                                   Part part,
                                   String field,
                                   String label) {
        if (!hasFile(part)) {
            return;
        }

        String contentType = part.getContentType();
        boolean supported = contentType != null
                && (contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/jpg"));

        if (!supported) {
            errors.put(field, label + " chỉ hỗ trợ định dạng JPG hoặc PNG.");
        } else if (part.getSize() > MAX_ID_IMAGE_SIZE) {
            errors.put(field, label + " không được vượt quá 5MB.");
        }
    }

    private boolean hasFile(Part part) {
        return part != null && part.getSize() > 0;
    }

    private String saveIdImage(Part part, String prefix) throws Exception {
        if (!hasFile(part)) {
            return null;
        }

        // 1. Cố gắng upload lên Cloudinary trước
        try {
            String url = UploadImage.uploadImage(part, "moda/seller/id-cards");
            if (url != null && !url.trim().isEmpty()) {
                return url;
            }
        } catch (Exception cloudinaryEx) {
            System.err.println("[AddSellerAccountServlet] Cloudinary upload failed, using local fallback: " + cloudinaryEx.getMessage());
        }

        // 2. Lưu cục bộ (Local Fallback) nếu Cloudinary thất bại
        String uploadRoot = null;
        try {
            uploadRoot = getServletContext().getRealPath("/");
            if (uploadRoot != null) {
                uploadRoot = uploadRoot + File.separator + "uploads" + File.separator + "seller-id-card";
            }
        } catch (Exception ignored) {
        }

        if (uploadRoot == null || uploadRoot.trim().isEmpty()) {
            String userHome = System.getProperty("user.home");
            uploadRoot = userHome + File.separator + "moda_uploads" + File.separator + "seller-id-card";
        }

        File dir = new File(uploadRoot);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created && !dir.exists()) {
                throw new IOException("Không thể tạo thư mục lưu ảnh: " + uploadRoot);
            }
        }

        String extension = resolveImageExtension(part);
        String fileName = prefix + "-" + UUID.randomUUID() + extension;
        File targetFile = new File(dir, fileName);

        try (InputStream inputStream = part.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return getServletContext().getContextPath() + "/uploads/seller-id-card/" + fileName;
    }

    private String resolveImageExtension(Part part) {
        String submittedFileName = part.getSubmittedFileName();
        if (submittedFileName != null) {
            int dotIndex = submittedFileName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < submittedFileName.length() - 1) {
                String extension = submittedFileName.substring(dotIndex).toLowerCase();
                if (".jpg".equals(extension) || ".jpeg".equals(extension) || ".png".equals(extension)) {
                    return extension;
                }
            }
        }

        String contentType = part.getContentType();
        if ("image/png".equals(contentType)) {
            return ".png";
        }

        return ".jpg";
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
