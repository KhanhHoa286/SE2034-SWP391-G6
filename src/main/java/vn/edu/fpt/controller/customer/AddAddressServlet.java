package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Servlet xử lý màn hình thêm địa chỉ giao hàng của customer.
 *
 * URL: /customer/addresses/add
 *
 * GET:
 * - Kiểm tra số lượng địa chỉ hiện tại
 * - Nếu đã đủ 5 địa chỉ thì không cho mở form
 * - Nếu chưa đủ 5 địa chỉ thì mở form thêm địa chỉ
 *
 * POST:
 * - Kiểm tra lại số lượng địa chỉ
 * - Nhận dữ liệu từ form
 * - Validate dữ liệu
 * - Insert địa chỉ vào database
 * - Redirect về danh sách địa chỉ
 */
@WebServlet(
        name = "AddAddressServlet",
        urlPatterns = {"/customer/addresses/add"}
)
public class AddAddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final WardDAO wardDAO = new WardDAO();

    /*
     * Đường dẫn đến JSP hiển thị form thêm địa chỉ.
     */
    private static final String ADD_ADDRESS_JSP =
            "/customer/address/add-address.jsp";

    /*
     * Mỗi customer chỉ được lưu tối đa 5 địa chỉ.
     */
    private static final int MAX_ADDRESS_COUNT = 5;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * Lấy thông tin đăng nhập từ session.
         */
        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);

        /*
         * Nếu chưa đăng nhập thì chuyển về trang login.
         */
        if (userId == null || userId <= 0) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        /*
         * Đếm số địa chỉ hiện có của customer.
         */
        int addressCount =
                addressDAO.countAddressesByUserId(userId);

        /*
         * Nếu đã đủ 5 địa chỉ thì không cho mở form thêm mới.
         */
        if (addressCount >= MAX_ADDRESS_COUNT) {
            session.setAttribute(
                    "addressError",
                    "Bạn chỉ được lưu tối đa 5 địa chỉ giao hàng."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/customer/addresses"
            );
            return;
        }

        /*
         * Load danh sách tỉnh/thành phố
         * và số lượng địa chỉ hiện tại.
         */
        loadFormData(request, userId);

        request.getRequestDispatcher(ADD_ADDRESS_JSP)
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * Lấy userId từ session.
         */
        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);

        /*
         * Nếu chưa đăng nhập thì chuyển về login.
         */
        if (userId == null || userId <= 0) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        /*
         * Phải kiểm tra số lượng địa chỉ ở doPost().
         *
         * Không thể chỉ kiểm tra ở doGet() vì người dùng
         * có thể bỏ qua giao diện và gửi request POST trực tiếp.
         */
        int currentAddressCount =
                addressDAO.countAddressesByUserId(userId);

        /*
         * Nếu customer đã có đủ 5 địa chỉ,
         * từ chối tạo thêm địa chỉ mới.
         */
        if (currentAddressCount >= MAX_ADDRESS_COUNT) {
            session.setAttribute(
                    "addressError",
                    "Bạn chỉ được lưu tối đa 5 địa chỉ giao hàng."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/customer/addresses"
            );
            return;
        }

        /*
         * Lấy dữ liệu từ form.
         */
        String receiverName =
                trim(request.getParameter("receiverName"));

        String receiverPhone =
                normalizePhone(
                        request.getParameter("receiverPhone")
                );

        String provinceIdRaw =
                trim(request.getParameter("provinceId"));

        String wardIdRaw =
                trim(request.getParameter("wardId"));

        String streetAddress =
                trim(request.getParameter("streetAddress"));

        /*
         * Nếu checkbox isDefault được chọn,
         * request sẽ có parameter isDefault.
         */
        boolean isDefault =
                request.getParameter("isDefault") != null;

        /*
         * Nếu đây là địa chỉ đầu tiên,
         * tự động đặt địa chỉ này làm mặc định.
         */
        if (currentAddressCount == 0) {
            isDefault = true;
        }

        /*
         * Validate dữ liệu đầu vào.
         */
        Map<String, String> errors = validateInput(
                receiverName,
                receiverPhone,
                provinceIdRaw,
                wardIdRaw,
                streetAddress
        );

        /*
         * Nếu provinceId và wardId hợp lệ về định dạng,
         * kiểm tra phường/xã có thuộc tỉnh/thành phố đã chọn không.
         */
        if (!errors.containsKey("provinceId")
                && !errors.containsKey("wardId")) {

            int provinceId =
                    Integer.parseInt(provinceIdRaw);

            int wardId =
                    Integer.parseInt(wardIdRaw);

            if (!wardDAO.isWardInProvince(
                    wardId,
                    provinceId
            )) {
                errors.put(
                        "wardId",
                        "Phường/xã không thuộc tỉnh/thành phố đã chọn."
                );
            }
        }

        /*
         * Nếu có lỗi validate,
         * trả lại form và giữ nguyên dữ liệu đã nhập.
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
         * Kiểm tra lại số lượng địa chỉ ngay trước khi insert.
         *
         * Việc kiểm tra lần hai giúp giảm nguy cơ vượt quá giới hạn
         * khi người dùng mở nhiều tab và gửi nhiều request gần nhau.
         */
        int latestAddressCount =
                addressDAO.countAddressesByUserId(userId);

        if (latestAddressCount >= MAX_ADDRESS_COUNT) {
            session.setAttribute(
                    "addressError",
                    "Bạn chỉ được lưu tối đa 5 địa chỉ giao hàng."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/customer/addresses"
            );
            return;
        }

        /*
         * Tạo đối tượng Address để truyền xuống DAO.
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
         * Thêm địa chỉ vào database.
         */
        boolean success =
                addressDAO.addAddress(address);

        /*
         * Nếu insert thất bại,
         * trả lại form và hiển thị thông báo lỗi.
         */
        if (!success) {
            errors.put(
                    "general",
                    "Không thể thêm địa chỉ. Vui lòng thử lại."
            );

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
         * Lưu thông báo thành công vào session.
         */
        session.setAttribute(
                "addressSuccess",
                "Thêm địa chỉ giao hàng thành công."
        );

        /*
         * Redirect về danh sách địa chỉ.
         *
         * Dùng redirect để tránh trường hợp người dùng F5
         * làm insert địa chỉ thêm một lần nữa.
         */
        response.sendRedirect(
                request.getContextPath()
                        + "/customer/addresses"
        );
    }

    /*
     * Load dữ liệu cần thiết cho form thêm địa chỉ.
     */
    private void loadFormData(
            HttpServletRequest request,
            int userId
    ) {
        request.setAttribute(
                "provinces",
                provinceDAO.getAllProvinces()
        );

        request.setAttribute(
                "addressCount",
                addressDAO.countAddressesByUserId(userId)
        );

        request.setAttribute(
                "maxAddressCount",
                MAX_ADDRESS_COUNT
        );
    }

    /*
     * Forward về form khi dữ liệu không hợp lệ.
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
         * Giữ lại dữ liệu customer đã nhập.
         */
        request.setAttribute(
                "inputReceiverName",
                receiverName
        );

        request.setAttribute(
                "inputReceiverPhone",
                receiverPhone
        );

        request.setAttribute(
                "selectedProvinceId",
                provinceId
        );

        request.setAttribute(
                "selectedWardId",
                wardId
        );

        request.setAttribute(
                "inputStreetAddress",
                streetAddress
        );

        request.setAttribute(
                "inputIsDefault",
                isDefault
        );

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
        Map<String, String> errors =
                new LinkedHashMap<>();

        /*
         * Validate tên người nhận.
         */
        if (receiverName.isEmpty()) {
            errors.put(
                    "receiverName",
                    "Vui lòng nhập họ và tên người nhận."
            );
        } else if (receiverName.length() > 100) {
            errors.put(
                    "receiverName",
                    "Họ và tên không được quá 100 ký tự."
            );
        }

        /*
         * Validate số điện thoại.
         */
        if (receiverPhone.isEmpty()) {
            errors.put(
                    "receiverPhone",
                    "Vui lòng nhập số điện thoại nhận hàng."
            );
        } else if (!receiverPhone.matches(
                "^0[35789]\\d{8}$"
        )) {
            errors.put(
                    "receiverPhone",
                    "Số điện thoại Việt Nam phải gồm 10 số "
                            + "và bắt đầu bằng 03, 05, 07, 08 hoặc 09."
            );
        }

        /*
         * Validate tỉnh/thành phố.
         */
        if (provinceIdRaw.isEmpty()) {
            errors.put(
                    "provinceId",
                    "Vui lòng chọn tỉnh/thành phố."
            );
        } else {
            try {
                int provinceId =
                        Integer.parseInt(provinceIdRaw);

                if (provinceId <= 0) {
                    errors.put(
                            "provinceId",
                            "Tỉnh/thành phố không hợp lệ."
                    );
                }
            } catch (NumberFormatException e) {
                errors.put(
                        "provinceId",
                        "Tỉnh/thành phố không hợp lệ."
                );
            }
        }

        /*
         * Validate phường/xã.
         */
        if (wardIdRaw.isEmpty()) {
            errors.put(
                    "wardId",
                    "Vui lòng chọn phường/xã."
            );
        } else {
            try {
                int wardId =
                        Integer.parseInt(wardIdRaw);

                if (wardId <= 0) {
                    errors.put(
                            "wardId",
                            "Phường/xã không hợp lệ."
                    );
                }
            } catch (NumberFormatException e) {
                errors.put(
                        "wardId",
                        "Phường/xã không hợp lệ."
                );
            }
        }

        /*
         * Validate địa chỉ chi tiết.
         */
        if (streetAddress.isEmpty()) {
            errors.put(
                    "streetAddress",
                    "Vui lòng nhập địa chỉ chi tiết."
            );
        } else if (streetAddress.length() > 255) {
            errors.put(
                    "streetAddress",
                    "Địa chỉ chi tiết không được quá 255 ký tự."
            );
        }

        return errors;
    }

    /*
     * Cắt khoảng trắng ở đầu và cuối chuỗi.
     */
    private String trim(String value) {
        return value == null
                ? ""
                : value.trim();
    }

    /*
     * Chuẩn hóa số điện thoại.
     *
     * Ví dụ:
     * "098 744 4444" thành "0987444444".
     */
    private String normalizePhone(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .replaceAll("\\s+", "");
    }

    /*
     * Lấy userId từ session.
     *
     * LoginServlet có thể lưu:
     * - session.setAttribute("userId", user.getUserId())
     * - session.setAttribute("user", user)
     *
     * Method này hỗ trợ cả hai trường hợp.
     */
    private Integer getLoggedInUserId(
            HttpSession session
    ) {
        if (session == null) {
            return null;
        }

        /*
         * Ưu tiên lấy trực tiếp từ thuộc tính userId.
         */
        Object rawUserId =
                session.getAttribute("userId");

        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }

        if (rawUserId != null) {
            try {
                return Integer.parseInt(
                        rawUserId.toString()
                );
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        /*
         * Nếu không có userId thì lấy từ object User.
         */
        Object rawUser =
                session.getAttribute("user");

        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        return null;
    }
}