package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.OrderDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/sellerDashboard")
public class SellerDashboardServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        int ownerId = -1;
        if (account != null) {
            ownerId = account.getUserId();
        }
        // Đặt activePage để sidebar highlight đúng mục "Tổng quan"
        request.setAttribute("activePage", "dashboard");

        Shop shop = shopDAO.getShopByOwnerId(ownerId);

        // Nếu chưa có shop, thử tìm bất kỳ shop nào tồn tại để hiển thị demo dashboard
        if (shop == null) {
            // Lấy shop bất kỳ
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }

        // Nếu hoàn toàn không có shop nào trong hệ thống, sử dụng shop demo để tránh redirect
        if (shop == null) {
            shop = Shop.builder()
                    .shopId(-1)
                    .shopName("Cửa hàng Demo")
                    .description("Đây là cửa hàng demo chưa cấu hình.")
                    .streetAddress("123 Đường Demo")
                    .build();
        }

        int shopId = shop.getShopId();

        // 1. Doanh thu hôm nay
        BigDecimal todayRevenue = (shopId == -1) ? BigDecimal.ZERO : orderDAO.getTodayRevenue(shopId);

        // 2. Tốc độ tăng trưởng doanh thu so với ngày hôm qua (%)
        double revenueTrend = (shopId == -1) ? 0.0 : orderDAO.getRevenueTrend(shopId);

        // 3. Đơn hàng mới hôm nay
        int todayNewOrders = (shopId == -1) ? 0 : orderDAO.getTodayNewOrders(shopId);

        // 4. Số lượng đơn hàng chênh lệch so với hôm qua
        int ordersTrendCount = (shopId == -1) ? 0 : orderDAO.getOrdersTrendCount(shopId);

        // 5. Doanh thu 7 ngày qua để vẽ biểu đồ
        List<Map<String, Object>> revenueLast7Days = (shopId == -1) ? new java.util.ArrayList<>() : orderDAO.getRevenueLast7Days(shopId);

        // 6. Sản phẩm bán chạy nhất (Top 5)
        List<ProductResponse> bestsellers = (shopId == -1) ? new java.util.ArrayList<>() : productDAO.getShopBestSellingProducts(shopId, 5);

        // 7. Đơn hàng gần đây nhất (Top 5)
        List<Map<String, Object>> recentOrders = (shopId == -1) ? new java.util.ArrayList<>() : orderDAO.getRecentSubOrders(shopId, 5);

        // Đẩy dữ liệu ra view
        request.setAttribute("shop", shop);
        request.setAttribute("todayRevenue", todayRevenue);
        request.setAttribute("revenueTrend", revenueTrend);
        request.setAttribute("todayNewOrders", todayNewOrders);
        request.setAttribute("ordersTrendCount", ordersTrendCount);
        request.setAttribute("revenueLast7Days", revenueLast7Days);
        request.setAttribute("bestsellers", bestsellers);
        request.setAttribute("recentOrders", recentOrders);

        // Forward tới trang dashboard JSP
        request.getRequestDispatcher("/seller/dashboard/view-seller-dashboard.jsp").forward(request, response);
    }
}
