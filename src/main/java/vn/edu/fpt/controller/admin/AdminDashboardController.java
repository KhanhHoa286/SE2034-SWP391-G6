package vn.edu.fpt.controller.admin;

import vn.edu.fpt.dao.DashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard/overview"})
public class AdminDashboardController extends HttpServlet {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Gọi DAO lấy các số liệu từ Database
        double totalRevenue = dashboardDAO.getTotalRevenue();
        int newUsers = dashboardDAO.getNewUsersCount();
        int totalOrders = dashboardDAO.getTotalOrdersCount();
        int pendingProducts = dashboardDAO.getPendingProductsCount();
        Map<String, Double> shopChartData = dashboardDAO.getRevenueDataByShop();

        // 2. Gửi dữ liệu sang JSP
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("newUsers", newUsers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("pendingProducts", pendingProducts);
        request.setAttribute("shopChartData", shopChartData);

        // KÍCH HOẠT BẪY REDIRECT: Đánh dấu dữ liệu đã được nạp từ Servlet thành công
        request.setAttribute("dashboardLoaded", true);

        // 3. Đẩy request sang giao diện JSP
        request.getRequestDispatcher("/admin/dashboard/view-system-overview.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}