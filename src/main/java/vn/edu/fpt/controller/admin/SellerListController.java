package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
// Thay đổi import này cho đúng với cấu trúc thực tế Object DTO và DAO của bạn
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.controller.admin.UserAdminDTO;


@WebServlet(name = "SellerListController", urlPatterns = {"/admin/seller-management-old"})
public class SellerListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDAO dao = new UserDAO();

        // 1. Nhận tham số lọc và phân trang từ Client
        String txtSearch = request.getParameter("search");
        if (txtSearch == null) txtSearch = "";

        String roleFilter = "SELLER";

        String statusFilter = request.getParameter("status");
        if (statusFilter == null || statusFilter.isEmpty()) statusFilter = "all";

        int pageSize = 5;
        int pageIndex = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                pageIndex = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }

        // 2. Lấy dữ liệu và số lượng người dùng sau khi lọc từ DAO
        List<UserAdminDTO> userList = dao.getFilteredUsers(txtSearch, roleFilter, statusFilter, pageIndex, pageSize);
        int totalRecords = dao.getTotalFilteredUsers(txtSearch, roleFilter, statusFilter);

        // Tính toán số trang thực tế
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;

        // 3. Đưa dữ liệu lên attributes (Đồng bộ đặt tên biến cho JSP)
        request.setAttribute("userList", userList);
        request.setAttribute("totalUsers", totalRecords);
        request.setAttribute("endP", totalPages);
        request.setAttribute("tag", pageIndex);

        // Đẩy lại dữ liệu cũ để giữ trạng thái ô chọn trên giao diện JSP
        request.setAttribute("saveSearch", txtSearch);
        request.setAttribute("saveRole", roleFilter);
        request.setAttribute("saveStatus", statusFilter);

        // KÍCH HOẠT BẪY REDIRECT ĐỒNG BỘ: Đánh dấu dữ liệu User đã được nạp thành công
        request.setAttribute("userListLoaded", true);

        // 4. Chuyển hướng sang file JSP hiển thị
        request.getRequestDispatcher("/admin/seller_mgt/list-sellers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            String newStatus = "ban".equals(action) ? "BANNED" : "ACTIVE";

            UserDAO dao = new UserDAO();
            dao.updateStatus(userId, newStatus);

            // POST-Redirect-GET Pattern: Tránh lỗi lặp submit form hành động khi F5 trang
            response.sendRedirect(request.getContextPath() + "/admin/seller-management");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/seller-management?error=1");
        }
    }
}