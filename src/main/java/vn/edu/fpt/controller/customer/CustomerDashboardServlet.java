package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.dto.response.OrderHistoryResponse;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.List;

/*
 * Servlet xử lý màn Dashboard của customer.
 *
 * URL: /customer/dashboard
 *
 * Màn này chỉ dùng để hiển thị tổng quan:
 * - Tên customer
 * - Tổng đơn hàng
 * - Số đơn đang giao
 * - 5 đơn hàng gần nhất
 *
 * Dashboard chỉ đọc dữ liệu, không update DB,
 * nên servlet này chỉ cần doGet(), không cần doPost().
 */
@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private static final String DASHBOARD_JSP = "/customer/account/view-dashboard.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Set UTF-8 để tránh lỗi tiếng Việt khi forward sang JSP.
         */
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * Lấy session hiện tại.
         * getSession(false) nghĩa là:
         * - Nếu đã có session thì lấy.
         * - Nếu chưa có session thì không tạo session mới.
         */
        HttpSession session = request.getSession(false);

        /*
         * Lấy userId của customer đang đăng nhập.
         * userId được LoginServlet lưu vào session sau khi đăng nhập thành công.
         */
        Integer userId = getLoggedInUserId(session);

        /*
         * Nếu không có userId, nghĩa là user chưa đăng nhập
         * hoặc session đã hết hạn.
         * Khi đó chuyển về trang login.
         */
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Lấy thông tin user từ DB.
         * Không nên chỉ dùng object user trong session,
         * vì dữ liệu trong DB có thể đã được cập nhật.
         */
        User currentUser = userDAO.getUserById(userId);

        /*
         * Nếu session có userId nhưng DB không tìm thấy user,
         * coi như session không hợp lệ và chuyển về login.
         */
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * Build họ tên hiển thị trên dashboard.
         * Ví dụ: first_name = Hoang thanh, last_name = Phuôn
         * => fullNameText = Hoang thanh Phuôn
         */
        String fullNameText = buildFullName(currentUser);

        /*
         * Lấy dữ liệu thống kê đơn hàng từ OrderDAO.
         *
         * totalOrders:
         * - Tổng số sub_order của customer.
         *
         * shippingOrders:
         * - Số sub_order có status = SHIPPING.
         *
         * recentOrders:
         * - 5 sub_order mới nhất của customer.
         */
        int totalOrders = orderDAO.countAllSubOrdersByCustomerId(userId);
        int shippingOrders = orderDAO.countShippingSubOrdersByCustomerId(userId);
        List<OrderHistoryResponse> recentOrders =
                orderDAO.getRecentSubOrdersByCustomerId(userId, 5);
        boolean hasSellerAccount = customerDAO.hasSellerAccount(userId);
        session.setAttribute("hasSellerAccount", hasSellerAccount);

        /*
         * Đẩy dữ liệu sang JSP.
         * JSP chỉ hiển thị dữ liệu, không query DB.
         */
        request.setAttribute("fullNameText", fullNameText);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("shippingOrders", shippingOrders);
        request.setAttribute("recentOrders", recentOrders);
        request.setAttribute("hasSellerAccount", hasSellerAccount);

        if ("1".equals(request.getParameter("sellerRegistered"))) {
            request.setAttribute(
                    "successMessage",
                    "Đăng ký người bán thành công. Bạn có thể vào trang người bán để quản lý đơn hàng."
            );
        }

        /*
         * Forward sang JSP dashboard.
         * Dùng forward để giữ request attribute.
         */
        request.getRequestDispatcher(DASHBOARD_JSP)
                .forward(request, response);
    }

    /*
     * Lấy userId từ session.
     *
     * Project hiện tại có thể lưu userId theo 2 cách:
     * 1. session.setAttribute("userId", userId)
     * 2. session.setAttribute("user", userObject)
     *
     * Method này hỗ trợ cả hai cách để tránh lỗi session không đồng nhất.
     */
    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");

        /*
         * Trường hợp userId được lưu trực tiếp dưới dạng Integer.
         */
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }

        /*
         * Trường hợp userId được lưu dưới dạng String.
         * Ví dụ: "11"
         */
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        /*
         * Trường hợp session lưu cả object User.
         * Khi đó lấy userId từ object User.
         */
        Object rawUser = session.getAttribute("user");

        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        return null;
    }

    /*
     * Ghép firstName và lastName thành họ tên đầy đủ.
     *
     * Nếu user chưa có tên hợp lệ thì trả về "Khách hàng MODA"
     * để giao diện không bị trống.
     */
    private String buildFullName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();

        String fullName = (firstName + " " + lastName).trim();

        if (fullName.isEmpty()) {
            return "Khách hàng MODA";
        }

        return fullName;
    }
}
