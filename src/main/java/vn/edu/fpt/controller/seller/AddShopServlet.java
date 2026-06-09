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
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/add-shop")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class AddShopServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<Province> provinces =
                provinceDAO.getAllProvinces();

        request.setAttribute("provinces", provinces);

        request.getRequestDispatcher(
                "/seller/shop/add-shop.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        Map<String, String> oldInput = new HashMap<>();
        oldInput.put("shopName", request.getParameter("shopName"));
        oldInput.put("shopEmail", request.getParameter("shopEmail"));
        oldInput.put("provinceId", request.getParameter("provinceId"));
        oldInput.put("wardId", request.getParameter("wardId"));
        oldInput.put("streetAddress", request.getParameter("streetAddress"));
        oldInput.put("description", request.getParameter("description"));
        request.setAttribute("oldInput", oldInput);

        Map<String, String> errors = new HashMap<>();

        try {
            String shopName = request.getParameter("shopName");
            if (shopName == null || shopName.trim().isEmpty()) {
                errors.put("shopName", "Tên cửa hàng không được để trống.");
            } else if (shopName.length() > 100) {
                errors.put("shopName", "Tên cửa hàng không được vượt quá 100 ký tự.");
            } else if (shopDAO.existsByShopName(shopName.trim())) {
                errors.put("shopName", "Tên cửa hàng đã tồn tại.");
            }

//            String shopEmail = request.getParameter("shopEmail");
//            if (shopEmail == null || shopEmail.trim().isEmpty()) {
//                errors.put("shopEmail", "Email cửa hàng không được để trống.");
//            } else if (!shopEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
//                errors.put("shopEmail", "Email không hợp lệ.");
//            }

            String provinceIdStr = request.getParameter("provinceId");
            if (provinceIdStr == null || provinceIdStr.trim().isEmpty()) {
                errors.put("provinceId", "Vui lòng chọn Tỉnh/Thành phố.");
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
                errors.put("streetAddress", "Địa chỉ không được để trống.");
            }

            String description = request.getParameter("description");
            if (description != null && description.length() > 250) {
                errors.put("description", "Mô tả ngắn không được vượt quá 250 ký tự.");
            }

            Part logoPart = request.getPart("logo");
            if (logoPart == null || logoPart.getSize() == 0) {
                errors.put("logo", "Vui lòng tải lên logo cửa hàng.");
            } else {
                String contentType = logoPart.getContentType();
                boolean isJpgOrPng = contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"));
                if (!isJpgOrPng) {
                    errors.put("logo", "Chỉ hỗ trợ định dạng JPG, PNG");
                } else if (logoPart.getSize() > 2 * 1024 * 1024) {
                    errors.put("logo", "Kích thích file không được vượt quá 2MB");
                }
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("popupType", "error");
                request.setAttribute("popupMessage", "Vui lòng kiểm tra lại thông tin đăng ký.");
                doGet(request, response);
                return;
            }

            // Tiến hành upload logo lên Cloudinary
            String logoUrl = vn.edu.fpt.common.UploadImage.uploadImage(logoPart, "shops");
            if (logoUrl == null || logoUrl.isEmpty()) {
                throw new Exception("Tải ảnh lên Cloudinary thất bại.");
            }

            // Lấy owner_id
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("account");
            int ownerId;
            if (user != null) {
                ownerId = user.getUserId();
            } else {
                ownerId = shopDAO.getAvailableOwnerId();
            }

            if (ownerId == -1) {
                throw new Exception("Không tìm thấy tài khoản người dùng hợp lệ để liên kết cửa hàng!");
            }

            if (shopDAO.existsByOwnerId(ownerId)) {
                throw new Exception("Tài khoản này đã có cửa hàng đăng ký!");
            }

            Shop shop = Shop.builder()
                    .ownerId(ownerId)
                    .shopName(shopName.trim())
                    .logoUrl(logoUrl)
                    .description(description != null ? description.trim() : "")
                    .wardId(wardId)
                    .streetAddress(streetAddress.trim())
                    .build();

            boolean success = shopDAO.insertShop(shop);

            if (success) {
                request.setAttribute("popupType", "success");
                request.setAttribute("popupMessage", "Công bố hồ sơ cửa hàng thành công!");
                request.removeAttribute("oldInput");
            } else {
                throw new Exception("Lưu thông tin cửa hàng vào cơ sở dữ liệu thất bại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("popupType", "error");
            request.setAttribute("popupMessage", e.getMessage());
        }

        doGet(request, response);
    }
}