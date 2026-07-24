package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.List;

/*
 * Servlet hiển thị danh sách địa chỉ giao hàng của customer.
 *
 * URL: /customer/addresses
 *
 * Màn này chỉ đọc dữ liệu:
 * - Không insert
 * - Không update
 * - Không delete
 *
 * Vì vậy chỉ cần doGet().
 */
@WebServlet(name = "CustomerAddressServlet", urlPatterns = {"/customer/addresses"})
public class CustomerAddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();

    private static final String LIST_ADDRESS_JSP = "/customer/address/list-addresses.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Set UTF-8 để tránh lỗi tiếng Việt khi hiển thị tên người nhận, địa chỉ.
         */
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * Lấy session hiện tại.
         * false nghĩa là nếu chưa có session thì không tạo session mới.
         */
        HttpSession session = request.getSession(false);

        /*
         * Lấy userId của customer đang đăng nhập.
         */
        Integer userId = getLoggedInUserId(session);

        /*
         * Nếu không có userId nghĩa là user chưa đăng nhập
         * hoặc session đã hết hạn.
         */
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Nhận diện luồng từ trang Checkout.
         * Lưu lại URL để sau khi set default / thêm / sửa có thể redirect về.
         */
        String type = request.getParameter("type");
        if ("checkout".equals(type)) {
            String referer = request.getHeader("referer");
            if (referer != null && !referer.trim().isEmpty() && referer.contains("add-order")) {
                session.setAttribute("CHECKOUT_REFERER", referer);
            } else {
                session.setAttribute("CHECKOUT_REFERER", request.getContextPath() + "/customer/add-order");
            }
        }

        /*
         * Gọi DAO để lấy danh sách địa chỉ của customer.
         */
        List<Address> addresses = addressDAO.getAddressesByUserId(userId);

        /*
         * Đưa dữ liệu sang JSP.
         * JSP chỉ hiển thị, không query DB.
         */
        request.setAttribute("addresses", addresses);
        request.setAttribute("addressCount", addresses.size());

        /*
         * Forward sang JSP.
         * Dùng forward để giữ request attribute.
         */
        request.getRequestDispatcher(LIST_ADDRESS_JSP)
                .forward(request, response);
    }

    /*
     * Lấy userId từ session.
     *
     * Project có thể lưu đăng nhập theo 2 kiểu:
     * 1. session.setAttribute("userId", userId)
     * 2. session.setAttribute("user", userObject)
     *
     * Method này hỗ trợ cả 2 để tránh lỗi lệch cách lưu session.
     */
    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");

        /*
         * Trường hợp userId được lưu trực tiếp là Integer.
         */
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }

        /*
         * Trường hợp userId được lưu dạng String.
         */
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        /*
         * Trường hợp session lưu nguyên object User.
         */
        Object rawUser = session.getAttribute("user");

        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        return null;
    }
}