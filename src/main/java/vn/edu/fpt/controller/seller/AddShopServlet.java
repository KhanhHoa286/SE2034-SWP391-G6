package vn.edu.fpt.controller.seller;

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
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/add-shop")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class AddShopServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer ownerId = getLoggedInUserId(session);
        if (ownerId == null || ownerId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Shop existingShop = shopDAO.getShopByOwnerId(ownerId);
        if (existingShop != null) {
            if (existingShop.getApprovalStatus().name().equals("APPROVED")) {
                session.setAttribute("hasSellerAccount", true);
                response.sendRedirect(request.getContextPath() + "/seller/orders");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/dashboard?shopPending=1");
            }
            return;
        }
        if (!customerDAO.hasCompletedSellerIdentity(ownerId)) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
        }

        List<Province> provinces = provinceDAO.getAllProvinces();
        request.setAttribute("provinces", provinces);
        request.getRequestDispatcher("/seller/shop/add-shop.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer ownerId = getLoggedInUserId(session);
        if (ownerId == null || ownerId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!customerDAO.hasCompletedSellerIdentity(ownerId)) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
        }

        Shop existingShop = shopDAO.getShopByOwnerId(ownerId);
        if (existingShop != null) {
            if (existingShop.getApprovalStatus().name().equals("APPROVED")) {
                session.setAttribute("hasSellerAccount", true);
                response.sendRedirect(request.getContextPath() + "/seller/orders");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/dashboard?shopPending=1");
            }
            return;
        }
        Map<String, String> errors = new HashMap<>();
        Map<String, String> oldInput = new HashMap<>();

        String shopName = clean(request.getParameter("shopName"));
        String provinceId = clean(request.getParameter("provinceId"));
        String wardIdRaw = clean(request.getParameter("wardId"));
        String streetAddress = clean(request.getParameter("streetAddress"));
        String description = clean(request.getParameter("description"));

        oldInput.put("shopName", shopName);
        oldInput.put("provinceId", provinceId);
        oldInput.put("wardId", wardIdRaw);
        oldInput.put("streetAddress", streetAddress);
        oldInput.put("description", description);
        request.setAttribute("oldInput", oldInput);

        int wardId = parseWardId(wardIdRaw, errors);
        validateShopName(shopName, errors);
        validateRequired(provinceId, "provinceId", "Vui lòng chọn Tỉnh/Thành phố.", errors);
        validateRequired(streetAddress, "streetAddress", "Địa chỉ không được để trống.", errors);

        if (description.length() > 250) {
            errors.put("description", "Mô tả ngắn không được vượt quá 250 ký tự.");
        }

        Part logoPart = request.getPart("logo");
        validateLogo(logoPart, errors);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Vui lòng kiểm tra lại thông tin đăng ký.");
            doGet(request, response);
            return;
        }

        try {
            String logoUrl = saveShopLogo(logoPart);
            if (logoUrl == null || logoUrl.trim().isEmpty()) {
                throw new Exception("Tải ảnh lên hệ thống thất bại.");
            }

            Shop shop = Shop.builder()
                    .ownerId(ownerId)
                    .shopName(shopName)
                    .logoUrl(logoUrl)
                    .description(description)
                    .wardId(wardId)
                    .streetAddress(streetAddress)
                    .build();

            boolean success = shopDAO.insertShop(shop);
            if (!success) {
                throw new Exception("Không thể lưu thông tin cửa hàng.");
            }

            response.sendRedirect(request.getContextPath() + "/customer/dashboard?shopPending=1");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", e.getMessage());
            doGet(request, response);
        }
    }

    private void validateShopName(String shopName, Map<String, String> errors) {
        if (shopName.isEmpty()) {
            errors.put("shopName", "Tên cửa hàng không được để trống.");
        } else if (shopName.length() > 100) {
            errors.put("shopName", "Tên cửa hàng không được vượt quá 100 ký tự.");
        } else if (shopDAO.existsByShopName(shopName)) {
            errors.put("shopName", "Tên cửa hàng đã tồn tại.");
        }
    }

    private void validateRequired(String value, String field, String message, Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(field, message);
        }
    }

    private int parseWardId(String wardIdRaw, Map<String, String> errors) {
        if (wardIdRaw == null || wardIdRaw.trim().isEmpty()) {
            errors.put("wardId", "Vui lòng chọn Phường/Xã.");
            return -1;
        }

        try {
            return Integer.parseInt(wardIdRaw);
        } catch (NumberFormatException e) {
            errors.put("wardId", "Phường/Xã không hợp lệ.");
            return -1;
        }
    }

    private void validateLogo(Part logoPart, Map<String, String> errors) {
        if (logoPart == null || logoPart.getSize() == 0) {
            errors.put("logo", "Vui lòng tải lên logo cửa hàng.");
            return;
        }

        String contentType = logoPart.getContentType();
        boolean validType = contentType != null
                && (contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/jpg"));

        if (!validType) {
            errors.put("logo", "Chỉ hỗ trợ định dạng JPG hoặc PNG.");
        } else if (logoPart.getSize() > 2 * 1024 * 1024) {
            errors.put("logo", "Kích thước file không được vượt quá 2MB.");
        }
    }

    private String saveShopLogo(Part logoPart) throws Exception {
        if (logoPart == null || logoPart.getSize() == 0) {
            return null;
        }

        try {
            String url = UploadImage.uploadImage(logoPart, "shops");
            if (url != null && !url.trim().isEmpty()) {
                return url;
            }
        } catch (Exception cloudinaryEx) {
            System.err.println("[AddShopServlet] Cloudinary upload failed, using local fallback: " + cloudinaryEx.getMessage());
        }

        String uploadRoot = null;
        try {
            uploadRoot = getServletContext().getRealPath("/");
            if (uploadRoot != null) {
                uploadRoot = uploadRoot + File.separator + "uploads" + File.separator + "shops";
            }
        } catch (Exception ignored) {
        }

        if (uploadRoot == null || uploadRoot.trim().isEmpty()) {
            String userHome = System.getProperty("user.home");
            uploadRoot = userHome + File.separator + "moda_uploads" + File.separator + "shops";
        }

        File dir = new File(uploadRoot);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created && !dir.exists()) {
                throw new IOException("Cannot create upload directory: " + uploadRoot);
            }
        }

        String extension = resolveImageExtension(logoPart);
        String fileName = "shop-logo-" + UUID.randomUUID() + extension;
        File targetFile = new File(dir, fileName);

        try (InputStream inputStream = logoPart.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return getServletContext().getContextPath() + "/uploads/shops/" + fileName;
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

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
