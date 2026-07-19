package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.CustomerDAO;

import java.io.IOException;
import java.util.List;

/**
 * ViewCustomerServlet
 *
 * URL pattern : /admin/user_mgt/view-customer
 *
 * Xử lý 2 method:
 *   GET  → Hiển thị chi tiết khách hàng + lịch sử mua hàng
 *   POST → Khóa / Mở tài khoản khách hàng
 */
@WebServlet("/admin/user_mgt/view-customer")
public class ViewCustomerServlet extends HttpServlet {

    private static final int PAGE_SIZE = 6; // số đơn hàng hiển thị mỗi trang

    private final CustomerDAO customerDAO = new CustomerDAO();

    // ─────────────────────────────────────────────────────────────
    //  GET: Hiển thị chi tiết khách hàng
    // ─────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── 1. Lấy tham số ──
        String userIdParam = req.getParameter("id");
        String pageParam   = req.getParameter("page");

        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-management");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-management");
            return;
        }

        int currentPage = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try { currentPage = Math.max(1, Integer.parseInt(pageParam.trim())); }
            catch (NumberFormatException ignored) {}
        }

        // ── 3. Truy vấn dữ liệu ──
        CustomerDTO customer = customerDAO.getCustomerDetail(userId);
        if (customer == null) {
            req.setAttribute("errorMsg", "Không tìm thấy khách hàng có ID = " + userId);
            req.getRequestDispatcher("/admin/error.jsp").forward(req, resp);
            return;
        }

        int totalOrders = customerDAO.countOrders(userId);
        int totalPages  = (int) Math.ceil((double) totalOrders / PAGE_SIZE);


        // ── 4. Đẩy dữ liệu ra view ──
        req.setAttribute("customer",     customer);
        req.setAttribute("currentPage",  currentPage);
        req.setAttribute("totalPages",   totalPages);
        req.setAttribute("totalOrders",  totalOrders);

        req.getRequestDispatcher("/admin/user_mgt/view-customer.jsp")
                .forward(req, resp);
    }

    // ─────────────────────────────────────────────────────────────
    //  POST: Khóa / Mở tài khoản
    // ─────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String action      = req.getParameter("action");   // "lock"/"ban" hoặc "unlock"/"unban"
        String userIdParam = req.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            userIdParam = req.getParameter("id");
        }

        if (action == null || userIdParam == null || userIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-management");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-management");
            return;
        }

        // ── 2. Xác định trạng thái mới ──
        String newStatus;
        if ("lock".equalsIgnoreCase(action) || "ban".equalsIgnoreCase(action)) {
            newStatus = "BANNED";
        } else if ("unlock".equalsIgnoreCase(action) || "unban".equalsIgnoreCase(action)) {
            newStatus = "ACTIVE";
        } else {
            resp.sendRedirect(req.getContextPath() +
                    "/admin/user_mgt/view-customer?id=" + userId);
            return;
        }

        // ── 3. Cập nhật DB ──
        boolean success = customerDAO.updateCustomerStatus(userId, newStatus);

        // ── 4. Redirect kèm thông báo ──
        String msg = success
                ? (newStatus.equals("BANNED") ? "Đã khóa tài khoản thành công." : "Đã mở khóa tài khoản thành công.")
                : "Có lỗi xảy ra, vui lòng thử lại.";

        resp.sendRedirect(req.getContextPath() +
                "/admin/user_mgt/view-customer?id=" + userId +
                "&msg=" + java.net.URLEncoder.encode(msg, "UTF-8") +
                "&success=" + success);
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────────────────────

    /**
     * Kiểm tra xem session hiện tại có phải Admin không.
     * Giả sử session lưu attribute "role" = "ADMIN" hoặc object User có method getRole().
     * Điều chỉnh logic này cho phù hợp với cách project của bạn lưu session.
     */
    private boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        Object user = session.getAttribute("account");
        if (user instanceof UserAdminDTO) {
            String roles = ((UserAdminDTO) user).getRoleNames();
            return roles != null && roles.toUpperCase().contains("ADMIN");
        }
        return false;
    }
}

