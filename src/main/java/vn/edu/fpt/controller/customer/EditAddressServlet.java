package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Servlet xử lý màn chỉnh sửa địa chỉ giao hàng của customer.
 *
 * URL: /customer/addresses/edit
 *
 * GET:
 * - Lấy addressId từ URL
 * - Kiểm tra địa chỉ có thuộc customer đang đăng nhập không
 * - Load dữ liệu cũ lên form edit
 *
 * POST:
 * - Nhận dữ liệu form
 * - Validate dữ liệu
 * - Update bảng addresses
 * - Redirect về /customer/addresses
 */
@WebServlet(name = "EditAddressServlet", urlPatterns = {"/customer/addresses/edit"})
public class EditAddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final WardDAO wardDAO = new WardDAO();

    private static final String EDIT_ADDRESS_JSP = "/customer/address/edit-address.jsp";

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

        int addressId = parseInt(request.getParameter("id"));

        if (addressId <= 0) {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        Address address = addressDAO.getAddressByIdAndUserId(addressId, userId);

        if (address == null) {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        loadFormData(request);

        request.setAttribute("address", address);

        request.getRequestDispatcher(EDIT_ADDRESS_JSP)
                .forward(request, response);
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

        String addressIdRaw = trim(request.getParameter("addressId"));
        String receiverName = trim(request.getParameter("receiverName"));
        String receiverPhone = normalizePhone(request.getParameter("receiverPhone"));
        String provinceIdRaw = trim(request.getParameter("provinceId"));
        String wardIdRaw = trim(request.getParameter("wardId"));
        String streetAddress = trim(request.getParameter("streetAddress"));

        boolean isDefault = request.getParameter("isDefault") != null;

        int addressId = parseInt(addressIdRaw);

        Address oldAddress = addressDAO.getAddressByIdAndUserId(addressId, userId);

        if (oldAddress == null) {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        Map<String, String> errors = validateInput(
                receiverName,
                receiverPhone,
                provinceIdRaw,
                wardIdRaw,
                streetAddress
        );

        if (!errors.containsKey("provinceId") && !errors.containsKey("wardId")) {
            int provinceId = Integer.parseInt(provinceIdRaw);
            int wardId = Integer.parseInt(wardIdRaw);

            if (!wardDAO.isWardInProvince(wardId, provinceId)) {
                errors.put("wardId", "Phường/xã không thuộc tỉnh/thành phố đã chọn.");
            }
        }

        if (!errors.isEmpty()) {
            forwardBackToForm(
                    request,
                    response,
                    oldAddress,
                    errors,
                    receiverName,
                    receiverPhone,
                    provinceIdRaw,
                    wardIdRaw,
                    streetAddress,
                    isDefault
            );
            return;
        }

        Address address = Address.builder()
                .addressId(addressId)
                .userId(userId)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .streetAddress(streetAddress)
                .wardId(Integer.parseInt(wardIdRaw))
                .isDefault(isDefault)
                .build();

        boolean success = addressDAO.updateAddress(address);

        if (!success) {
            errors.put("general", "Không thể cập nhật địa chỉ. Vui lòng thử lại.");

            forwardBackToForm(
                    request,
                    response,
                    oldAddress,
                    errors,
                    receiverName,
                    receiverPhone,
                    provinceIdRaw,
                    wardIdRaw,
                    streetAddress,
                    isDefault
            );
            return;
        }

        /*
         * Thành công:
         * 1. Nếu người dùng chọn địa chỉ này làm mặc định (isDefault == true):
         *    Nếu có CHECKOUT_REFERER trong session -> chuyển hướng ngay về trang thanh toán.
         * 2. Ngược lại (người dùng không chọn mặc định):
         *    Gửi về trang danh sách địa chỉ. Giữ CHECKOUT_REFERER trong session để khi họ bấm "Thiết lập mặc định" tại danh sách mới chuyển về trang thanh toán.
         */
        String checkoutReferer = (String) session.getAttribute("CHECKOUT_REFERER");
        if (checkoutReferer != null && !checkoutReferer.trim().isEmpty() && isDefault) {
            session.removeAttribute("CHECKOUT_REFERER");
            response.sendRedirect(checkoutReferer);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
        }
    }

    /*
     * Load dữ liệu tỉnh/thành phố cho form.
     */
    private void loadFormData(HttpServletRequest request) {
        request.setAttribute("provinces", provinceDAO.getAllProvinces());
    }

    /*
     * Forward lại form edit khi validate lỗi hoặc update lỗi.
     */
    private void forwardBackToForm(
            HttpServletRequest request,
            HttpServletResponse response,
            Address oldAddress,
            Map<String, String> errors,
            String receiverName,
            String receiverPhone,
            String provinceId,
            String wardId,
            String streetAddress,
            boolean isDefault
    ) throws ServletException, IOException {
        request.setAttribute("errors", errors);
        request.setAttribute("address", oldAddress);

        request.setAttribute("inputReceiverName", receiverName);
        request.setAttribute("inputReceiverPhone", receiverPhone);
        request.setAttribute("selectedProvinceId", provinceId);
        request.setAttribute("selectedWardId", wardId);
        request.setAttribute("inputStreetAddress", streetAddress);
        request.setAttribute("inputIsDefault", isDefault);

        loadFormData(request);

        request.getRequestDispatcher(EDIT_ADDRESS_JSP)
                .forward(request, response);
    }

    /*
     * Validate dữ liệu form edit address.
     */
    private Map<String, String> validateInput(
            String receiverName,
            String receiverPhone,
            String provinceIdRaw,
            String wardIdRaw,
            String streetAddress
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (receiverName.isEmpty()) {
            errors.put("receiverName", "Vui lòng nhập họ và tên người nhận.");
        } else if (receiverName.length() > 100) {
            errors.put("receiverName", "Họ và tên không được quá 100 ký tự.");
        }

        if (receiverPhone.isEmpty()) {
            errors.put("receiverPhone", "Vui lòng nhập số điện thoại nhận hàng.");
        } else if (!receiverPhone.matches("^0[35789]\\d{8}$")) {
            errors.put("receiverPhone", "Số điện thoại Việt Nam phải gồm 10 số và bắt đầu bằng 03, 05, 07, 08 hoặc 09.");
        }

        if (provinceIdRaw.isEmpty()) {
            errors.put("provinceId", "Vui lòng chọn tỉnh/thành phố.");
        } else if (parseInt(provinceIdRaw) <= 0) {
            errors.put("provinceId", "Tỉnh/thành phố không hợp lệ.");
        }

        if (wardIdRaw.isEmpty()) {
            errors.put("wardId", "Vui lòng chọn phường/xã.");
        } else if (parseInt(wardIdRaw) <= 0) {
            errors.put("wardId", "Phường/xã không hợp lệ.");
        }

        if (streetAddress.isEmpty()) {
            errors.put("streetAddress", "Vui lòng nhập địa chỉ chi tiết.");
        } else if (streetAddress.length() > 255) {
            errors.put("streetAddress", "Địa chỉ chi tiết không được quá 255 ký tự.");
        }

        return errors;
    }

    /*
     * Parse String sang int.
     * Nếu lỗi thì trả về -1.
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    /*
     * Cắt khoảng trắng đầu/cuối.
     */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /*
     * Chuẩn hóa số điện thoại.
     */
    private String normalizePhone(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", "");
    }

    /*
     * Lấy userId từ session.
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
}