package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/edit-shop")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class EditShopServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final WardDAO wardDAO = new WardDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
        }

        // Fetch provinces for dropdown
        List<Province> provinces = provinceDAO.getAllProvinces();
        request.setAttribute("provinces", provinces);

        // Fetch wards for the currently selected province (if any) to pre-populate
        if (shop.getWard() != null && shop.getWard().getProvinceId() != null) {
            List<Ward> wards = wardDAO.getWardsByProvinceId(shop.getWard().getProvinceId());
            request.setAttribute("wards", wards);
        }

        request.setAttribute("shop", shop);
        request.setAttribute("activePage", "view-shop");

        request.getRequestDispatcher("/seller/shop/edit-shop.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Shop currentShop = resolveCurrentShop(session);

        if (currentShop == null) {
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Không tìm thấy cửa hàng nào để cập nhật!");
            doGet(request, response);
            return;
        }

        Map<String, String> oldInput = new HashMap<>();
        oldInput.put("shopName", request.getParameter("shopName"));
        oldInput.put("description", request.getParameter("description"));
        oldInput.put("phone", request.getParameter("phone"));
        oldInput.put("email", request.getParameter("email"));
        oldInput.put("provinceId", request.getParameter("provinceId"));
        oldInput.put("wardId", request.getParameter("wardId"));
        oldInput.put("streetAddress", request.getParameter("streetAddress"));
        request.setAttribute("oldInput", oldInput);

        Map<String, String> errors = new HashMap<>();

        String shopName = request.getParameter("shopName");
        if (shopName == null || shopName.trim().isEmpty()) {
            errors.put("shopName", "Tên cửa hàng không được để trống.");
        } else if (shopName.length() > 100) {
            errors.put("shopName", "Tên cửa hàng không được vượt quá 100 ký tự.");
        }

        String description = request.getParameter("description");
        if (description != null && description.length() > 500) {
            errors.put("description", "Giới thiệu cửa hàng không được vượt quá 500 ký tự.");
        }

        String phone = request.getParameter("phone");
        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Số điện thoại không được để trống.");
        }

        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống.");
        }

        String wardIdStr = request.getParameter("wardId");
        int wardId = -1;
        if (wardIdStr == null || wardIdStr.trim().isEmpty()) {
            errors.put("wardId", "Vui lòng chọn Phường/Xã.");
        } else {
            try {
                wardId = Integer.parseInt(wardIdStr);
            } catch (NumberFormatException e) {
                errors.put("wardId", "Phường/Xã không hợp lệ.");
            }
        }

        String streetAddress = request.getParameter("streetAddress");
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            errors.put("streetAddress", "Số nhà, tên đường không được để trống.");
        }

        Part logoPart = request.getPart("logo");
        String logoUrl = currentShop.getLogoUrl();

        if (logoPart != null && logoPart.getSize() > 0) {
            String contentType = logoPart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("logo", "Chỉ được upload file hình ảnh.");
            } else if (logoPart.getSize() > 2 * 1024 * 1024) {
                errors.put("logo", "Dung lượng ảnh quá lớn (tối đa 2MB).");
            } else {
                try {
                    logoUrl = vn.edu.fpt.common.UploadImage.uploadImage(logoPart, "shops");
                } catch (Exception e) {
                    errors.put("logo", "Tải ảnh lên Cloudinary thất bại: " + e.getMessage());
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Vui lòng kiểm tra lại thông tin chỉnh sửa.");
            doGet(request, response);
            return;
        }

        try {
            // Update shop record
            currentShop.setShopName(shopName.trim());
            currentShop.setDescription(description != null ? description.trim() : "");
            currentShop.setLogoUrl(logoUrl);
            currentShop.setWardId(wardId);
            currentShop.setStreetAddress(streetAddress.trim());

            boolean isShopUpdated = shopDAO.updateShop(currentShop);
            
            // Update user contact info
            boolean isUserUpdated = userDAO.updateUserContact(currentShop.getOwnerId(), email.trim(), phone.trim());

            if (isShopUpdated && isUserUpdated) {
                request.setAttribute("popupType", "success");
                request.setAttribute("popupMessage", "Cập nhật hồ sơ cửa hàng thành công!");
                request.removeAttribute("oldInput");
            } else {
                throw new Exception("Lưu thông tin cập nhật thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }

        doGet(request, response);
    }

    private Shop resolveCurrentShop(HttpSession session) {
        Integer ownerId = getLoggedInUserId(session);
        return ownerId == null ? null : shopDAO.getShopWithAddressAndOwnerByOwnerId(ownerId);
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
}
