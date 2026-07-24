package vn.edu.fpt.controller.customer;

/*
 * Các import jakarta.servlet này thuộc dependency:
 *
 * <dependency>
 *     <groupId>jakarta.servlet</groupId>
 *     <artifactId>jakarta.servlet-api</artifactId>
 * </dependency>
 *
 * Dùng cho Servlet, request, response, session.
 */
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Các class DAO/model có sẵn trong project.
 */
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.User;

/*
 * Các import Java gốc.
 * Không cần thêm dependency Maven.
 */
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Servlet xử lý màn thêm địa chỉ giao hàng của customer.
 *
 * URL: /customer/addresses/add
 *
 * GET:
 * - Mở form thêm địa chỉ
 *
 * POST:
 * - Nhận dữ liệu form
 * - Validate dữ liệu
 * - Insert vào bảng addresses
 * - Redirect về /customer/addresses
 */
@WebServlet(name = "AddAddressServlet", urlPatterns = {"/customer/addresses/add"})
public class AddAddressServlet extends HttpServlet {

    /*
     * Tận dụng DAO đã có trong project.
     */
    private final AddressDAO addressDAO = new AddressDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final WardDAO wardDAO = new WardDAO();

    /*
     * Đường dẫn JSP nội bộ.
     * Đây không phải URL người dùng gõ trên trình duyệt.
     */
    private static final String ADD_ADDRESS_JSP = "/customer/address/add-address.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Set UTF-8 để tránh lỗi tiếng Việt.
         */
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * Lấy userId từ session.
         */
        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);

        /*
         * Nếu chưa đăng nhập thì đưa về login.
         */
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Load dữ liệu tỉnh/thành phố cho combobox.
         * Phường/xã sẽ dùng /load-wards có sẵn trong project để load theo tỉnh.
         */
        loadFormData(request, userId);

        request.getRequestDispatcher(ADD_ADDRESS_JSP)
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

        /*
         * Lấy dữ liệu từ form.
         *
         * Lưu ý:
         * - Không lấy users.phone làm số giao hàng.
         * - Số giao hàng là receiverPhone user nhập riêng.
         */
        String receiverName = trim(request.getParameter("receiverName"));
        String receiverPhone = normalizePhone(request.getParameter("receiverPhone"));
        String provinceIdRaw = trim(request.getParameter("provinceId"));
        String wardIdRaw = trim(request.getParameter("wardId"));
        String streetAddress = trim(request.getParameter("streetAddress"));

        /*
         * Checkbox:
         * - Nếu tick thì request có parameter isDefault
         * - Nếu không tick thì null
         */
        boolean isDefault = request.getParameter("isDefault") != null;

        /*
         * Nếu là địa chỉ đầu tiên của customer,
         * bắt buộc set làm mặc định.
         */
        int addressCount = addressDAO.countAddressesByUserId(userId);
        if (addressCount == 0) {
            isDefault = true;
        }

        /*
         * Validate dữ liệu.
         */
        Map<String, String> errors = validateInput(
                receiverName,
                receiverPhone,
                provinceIdRaw,
                wardIdRaw,
                streetAddress
        );

        /*
         * Nếu provinceId và wardId đều parse được,
         * kiểm tra ward có thuộc province đã chọn không.
         */
        if (!errors.containsKey("provinceId") && !errors.containsKey("wardId")) {
            int provinceId = Integer.parseInt(provinceIdRaw);
            int wardId = Integer.parseInt(wardIdRaw);

            if (!wardDAO.isWardInProvince(wardId, provinceId)) {
                errors.put("wardId", "Phường/xã không thuộc tỉnh/thành phố đã chọn.");
            }
        }

        /*
         * Nếu có lỗi validate, quay lại form và giữ dữ liệu user đã nhập.
         */
        if (!errors.isEmpty()) {
            forwardBackToForm(
                    request,
                    response,
                    userId,
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
         * Build object Address để truyền xuống DAO.
         */
        Address address = Address.builder()
                .userId(userId)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .streetAddress(streetAddress)
                .wardId(Integer.parseInt(wardIdRaw))
                .isDefault(isDefault)
                .build();

        /*
         * Insert DB.
         */
        boolean success = addressDAO.addAddress(address);

        /*
         * Nếu insert lỗi, quay lại form.
         */
        if (!success) {
            errors.put("general", "Không thể thêm địa chỉ. Vui lòng thử lại.");

            forwardBackToForm(
                    request,
                    response,
                    userId,
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
         * 1. Nếu chưa có địa chỉ nào (addressCount == 0) hoặc người dùng chọn làm mặc định (isDefault == true):
         *    Nếu có CHECKOUT_REFERER trong session -> chuyển hướng ngay về trang thanh toán.
         * 2. Ngược lại (người dùng không chọn mặc định):
         *    Gửi về trang danh sách địa chỉ. Giữ CHECKOUT_REFERER trong session để khi họ bấm "Thiết lập mặc định" tại danh sách mới chuyển về trang thanh toán.
         */
        String checkoutReferer = (String) session.getAttribute("CHECKOUT_REFERER");
        if (checkoutReferer != null && !checkoutReferer.trim().isEmpty() && (addressCount == 0 || isDefault)) {
            session.removeAttribute("CHECKOUT_REFERER");
            response.sendRedirect(checkoutReferer);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
        }
    }

    /*
     * Load dữ liệu cần thiết cho form.
     */
    private void loadFormData(HttpServletRequest request, int userId) {
        request.setAttribute("provinces", provinceDAO.getAllProvinces());
        request.setAttribute("addressCount", addressDAO.countAddressesByUserId(userId));
    }

    /*
     * Forward lại form khi có lỗi.
     */
    private void forwardBackToForm(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId,
            Map<String, String> errors,
            String receiverName,
            String receiverPhone,
            String provinceId,
            String wardId,
            String streetAddress,
            boolean isDefault
    ) throws ServletException, IOException {

        request.setAttribute("errors", errors);

        /*
         * Giữ lại dữ liệu user đã nhập.
         */
        request.setAttribute("inputReceiverName", receiverName);
        request.setAttribute("inputReceiverPhone", receiverPhone);
        request.setAttribute("selectedProvinceId", provinceId);
        request.setAttribute("selectedWardId", wardId);
        request.setAttribute("inputStreetAddress", streetAddress);
        request.setAttribute("inputIsDefault", isDefault);

        loadFormData(request, userId);

        request.getRequestDispatcher(ADD_ADDRESS_JSP)
                .forward(request, response);
    }

    /*
     * Validate dữ liệu từ form.
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
        } else {
            try {
                int provinceId = Integer.parseInt(provinceIdRaw);
                if (provinceId <= 0) {
                    errors.put("provinceId", "Tỉnh/thành phố không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                errors.put("provinceId", "Tỉnh/thành phố không hợp lệ.");
            }
        }

        if (wardIdRaw.isEmpty()) {
            errors.put("wardId", "Vui lòng chọn phường/xã.");
        } else {
            try {
                int wardId = Integer.parseInt(wardIdRaw);
                if (wardId <= 0) {
                    errors.put("wardId", "Phường/xã không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                errors.put("wardId", "Phường/xã không hợp lệ.");
            }
        }

        if (streetAddress.isEmpty()) {
            errors.put("streetAddress", "Vui lòng nhập địa chỉ chi tiết.");
        } else if (streetAddress.length() > 255) {
            errors.put("streetAddress", "Địa chỉ chi tiết không được quá 255 ký tự.");
        }

        return errors;
    }

    /*
     * Cắt khoảng trắng đầu/cuối.
     */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /*
     * Chuẩn hóa số điện thoại.
     * Ví dụ: "098 744 4444" -> "0987444444"
     */
    private String normalizePhone(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", "");
    }

    /*
     * Lấy userId từ session.
     *
     * LoginServlet của project đã lưu:
     * - session.setAttribute("user", user)
     * - session.setAttribute("userId", user.getUserId())
     *
     * Method này hỗ trợ cả 2 cách.
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