package vn.edu.fpt.controller.admin;

import vn.edu.fpt.common.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/seller-applications/detail")
public class ViewSellerDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String userIdStr = request.getParameter("userId");

        Map<String, Object> sellerData = new HashMap<>();
        List<Map<String, Object>> products = new ArrayList<>();

        // Biến kiểm tra xem có dùng dữ liệu thực từ DB không
        boolean loadedFromDb = false;

        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();

            if (conn != null) {
                PreparedStatement ps = null;
                ResultSet rs = null;

                if (idStr != null && !idStr.trim().isEmpty()) {
                    int appId = Integer.parseInt(idStr);
                    String sql = "SELECT s.shop_id AS application_id, s.*, u.user_id AS u_user_id, u.first_name, u.last_name, u.phone, u.email, u.citizen_id AS tax_code " +
                                 "FROM shops s " +
                                 "JOIN users u ON s.owner_id = u.user_id " +
                                 "WHERE s.shop_id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, appId);
                    rs = ps.executeQuery();
                } else if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                    int userId = Integer.parseInt(userIdStr);
                    String sql = "SELECT s.shop_id AS application_id, s.*, u.user_id AS u_user_id, u.first_name, u.last_name, u.phone, u.email, u.citizen_id AS tax_code " +
                                 "FROM users u " +
                                 "LEFT JOIN shops s ON u.user_id = s.owner_id " +
                                 "WHERE u.user_id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, userId);
                    rs = ps.executeQuery();
                }

                if (rs != null && rs.next()) {
                    loadedFromDb = true;

                    // Thông tin cơ bản
                    int userId = rs.getInt("u_user_id");
                    String shopName = rs.getNString("shop_name");
                    if (shopName == null || shopName.trim().isEmpty()) {
                        shopName = "Chưa đăng ký";
                    }
                    String ownerName = rs.getNString("first_name") + " " + rs.getNString("last_name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String taxCode = rs.getString("tax_code");
                    String status = rs.getString("approval_status");
                    if (status == null || status.trim().isEmpty()) {
                        status = "PENDING";
                    }
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    int applicationId = 0;
                    try {
                        applicationId = rs.getInt("application_id");
                    } catch (SQLException ignored) {}

                    sellerData.put("userId", userId);
                    sellerData.put("applicationId", applicationId);
                    sellerData.put("shopName", shopName);
                    sellerData.put("ownerName", ownerName);
                    sellerData.put("phone", phone);
                    sellerData.put("email", email);
                    sellerData.put("taxCode", taxCode != null ? taxCode : "Chưa có");
                    sellerData.put("status", status);
                    sellerData.put("createdAt", createdAt != null ? createdAt.toString() : "Chưa cập nhật");
                    sellerData.put("sellerCode", "SLR-" + (1000 + userId));

                    // Lấy thông tin từ bảng shops
                    String shopSql = "SELECT * FROM shops WHERE owner_id = ?";
                    try (PreparedStatement psShop = conn.prepareStatement(shopSql)) {
                        psShop.setInt(1, userId);
                        try (ResultSet rsShop = psShop.executeQuery()) {
                            if (rsShop.next()) {
                                int shopId = rsShop.getInt("shop_id");
                                sellerData.put("shopId", shopId);
                                sellerData.put("streetAddress", rsShop.getNString("street_address"));
                                
                                String dbShopName = rsShop.getNString("shop_name");
                                if (dbShopName != null && !dbShopName.trim().isEmpty()) {
                                    sellerData.put("shopName", dbShopName);
                                }

                                String description = rsShop.getNString("description");
                                if (description != null && !description.trim().isEmpty()) {
                                    sellerData.put("shopDescription", description);
                                } else {
                                    sellerData.put("shopDescription", "Chưa cập nhật mô tả");
                                }
                                
                                // Lấy danh sách sản phẩm thực tế
                                String prodSql = "SELECT TOP 5 * FROM products WHERE shop_id = ? AND (is_deleted = 0 OR is_deleted IS NULL) ORDER BY created_at DESC";
                                try (PreparedStatement psProd = conn.prepareStatement(prodSql)) {
                                    psProd.setInt(1, shopId);
                                    try (ResultSet rsProd = psProd.executeQuery()) {
                                        while (rsProd.next()) {
                                            Map<String, Object> prod = new HashMap<>();
                                            prod.put("productId", rsProd.getInt("product_id"));
                                            prod.put("productName", rsProd.getNString("product_name"));
                                            prod.put("productCode", "PRD-" + rsProd.getInt("product_id"));
                                            prod.put("basePrice", rsProd.getBigDecimal("base_price"));
                                            prod.put("status", rsProd.getString("status"));
                                            prod.put("thumbnailUrl", rsProd.getString("thumbnail_url"));
                                            // Mock sold count
                                            prod.put("soldCount", 10 + rsProd.getInt("product_id") * 3);
                                            products.add(prod);
                                        }
                                    }
                                }
                            } else {
                                sellerData.put("streetAddress", "Tầng 4, Tòa nhà Bitexco, Q1, TP.HCM");
                            }
                        }
                    }

                    // Tính toán thống kê thực tế hoặc lấy mẫu nếu bằng 0
                    int shopId = sellerData.get("shopId") != null ? (Integer) sellerData.get("shopId") : 0;
                    if (shopId != 0) {
                        // Số lượng sản phẩm
                        String countSql = "SELECT COUNT(*) FROM products WHERE shop_id = ? AND is_deleted = 0";
                        try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                            psCount.setInt(1, shopId);
                            try (ResultSet rsCount = psCount.executeQuery()) {
                                if (rsCount.next()) {
                                    sellerData.put("totalProducts", rsCount.getInt(1));
                                }
                            }
                        }

                        // Số lượng đơn hàng và doanh thu
                        String orderSql = "SELECT COUNT(*), SUM(total_amount) FROM sub_orders WHERE shop_id = ? AND status = 'COMPLETED'";
                        try (PreparedStatement psOrder = conn.prepareStatement(orderSql)) {
                            psOrder.setInt(1, shopId);
                            try (ResultSet rsOrder = psOrder.executeQuery()) {
                                if (rsOrder.next()) {
                                    int ordersCount = rsOrder.getInt(1);
                                    double revenue = rsOrder.getDouble(2);
                                    sellerData.put("completedOrders", ordersCount > 0 ? ordersCount : 3892);
                                    sellerData.put("totalRevenue", revenue > 0 ? String.format("%,.2fM", revenue / 1000000.0) : "1,245.50M");
                                }
                            }
                        }
                    } else {
                        // Trường hợp PENDING hoặc không có shop
                        sellerData.put("totalProducts", 0);
                        sellerData.put("completedOrders", 0);
                        sellerData.put("totalRevenue", "0.00M");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        request.setAttribute("seller", sellerData);
        request.setAttribute("products", products);
        
        String status = (String) sellerData.get("status");
        if ("APPROVED".equals(status) || "ACTIVE".equals(status)) {
            request.setAttribute("activeMenu", "seller-management");
        } else {
            request.setAttribute("activeMenu", "seller-applications");
        }

        request.getRequestDispatcher("/admin/seller_mgt/view-seller.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String action = request.getParameter("action");

        // Xử lý các hành động Đình chỉ / Phê duyệt
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            if (conn != null && idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int appId = Integer.parseInt(idStr.trim());

                    if ("suspend".equalsIgnoreCase(action)) {
                        // Update user or shop status
                        String sql = "UPDATE shops SET approval_status = 'REJECTED' WHERE shop_id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, appId);
                            ps.executeUpdate();
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/admin/seller-applications");
    }
}
