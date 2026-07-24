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

        // 1. Lấy tham số ngày từ request
        String dateParam = request.getParameter("date");
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate selectedDateVal;

        if (dateParam == null || dateParam.trim().isEmpty()) {
            selectedDateVal = today;
        } else {
            try {
                selectedDateVal = java.time.LocalDate.parse(dateParam);
            } catch (Exception e) {
                selectedDateVal = today;
            }
        }

        String selectedDateStr = selectedDateVal.toString(); // yyyy-MM-dd

        // Tạo nhãn hiển thị cho button chọn ngày
        String dateLabel;
        if (selectedDateVal.equals(today)) {
            dateLabel = "Hôm nay";
        } else {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateLabel = selectedDateVal.format(formatter);
        }

        // 2. Gọi DAO lấy các số liệu từ Database theo ngày đã chọn
        double totalRevenue = dashboardDAO.getTotalRevenue(selectedDateStr);
        int newUsers = dashboardDAO.getNewUsersCount(selectedDateStr);
        int totalOrders = dashboardDAO.getTotalOrdersCount(selectedDateStr);
        int pendingProducts = dashboardDAO.getPendingProductsCount(selectedDateStr);
        Map<String, Double> shopChartData = dashboardDAO.getRevenueDataByShop(selectedDateStr);

        // 3. Gửi dữ liệu sang JSP
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("newUsers", newUsers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("pendingProducts", pendingProducts);
        request.setAttribute("shopChartData", shopChartData);
        request.setAttribute("selectedDate", selectedDateStr);
        request.setAttribute("dateLabel", dateLabel);

        // KÍCH HOẠT BẪY REDIRECT: Đánh dấu dữ liệu đã được nạp từ Servlet thành công
        request.setAttribute("dashboardLoaded", true);

        // 4. Đẩy request sang giao diện JSP
        request.getRequestDispatcher("/admin/dashboard/view-system-overview.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}