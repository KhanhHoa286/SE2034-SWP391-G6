package vn.edu.fpt.dao;



import vn.edu.fpt.common.DBContext; // Thay thế bằng class kết nối DB của dự án bạn
import vn.edu.fpt.controller.admin.SellerApplication; // Thay thế bằng class DTO của bạn
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerApplicationDAO extends DBContext {

    public List<SellerApplication> getPendingApplications(String searchQuery) {
        return getApplications(searchQuery, "PENDING", null, 1, 1000);
    }

    public List<SellerApplication> getApplications(String searchQuery, String status) {
        return getApplications(searchQuery, status, null, 1, 1000);
    }

    public List<SellerApplication> getApplications(String searchQuery, String status, String date) {
        return getApplications(searchQuery, status, date, 1, 1000);
    }

    public List<SellerApplication> getApplications(String searchQuery, String status, String date, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<SellerApplication> list = new ArrayList<>();

        // Câu lệnh SQL JOIN lấy dữ liệu
        String sql = "SELECT sa.*, (u.first_name + ' ' + u.last_name) AS owner_name " +
                "FROM shop_applications sa " +
                "JOIN users u ON sa.user_id = u.user_id ";

        boolean filterStatus = status != null && !"ALL".equalsIgnoreCase(status);
        boolean filterDate = date != null && !date.trim().isEmpty();

        if (filterStatus) {
            sql += "WHERE sa.status = ? ";
        } else {
            sql += "WHERE 1=1 ";
        }

        if (filterDate) {
            sql += "AND CAST(sa.created_at AS DATE) = ? ";
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += "AND (sa.shop_name LIKE ? OR sa.tax_code LIKE ? OR CAST(sa.application_id AS VARCHAR) LIKE ?) ";
        }
        sql += " ORDER BY sa.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (filterStatus) {
                ps.setString(index++, status.toUpperCase());
            }
            if (filterDate) {
                ps.setString(index++, date);
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String p = "%" + searchQuery.trim() + "%";
                ps.setString(index++, p);
                ps.setString(index++, p);
                ps.setString(index++, p);
            }
            
            ps.setInt(index++, offset);
            ps.setInt(index++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SellerApplication app = new SellerApplication();
                    app.setApplicationId(rs.getInt("application_id"));
                    app.setUserId(rs.getInt("user_id"));
                    app.setOwnerName(rs.getNString("owner_name"));
                    app.setShopName(rs.getNString("shop_name"));
                    app.setBusinessEmail(rs.getString("business_email"));
                    app.setTaxCode(rs.getString("tax_code"));
                    app.setFrontIdImage(rs.getString("front_id_image"));
                    app.setBackIdImage(rs.getString("back_id_image"));
                    app.setStatus(rs.getString("status"));
                    app.setResolvedBy(rs.getInt("resolved_by"));
                    app.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(app);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SellerApplication getApplicationById(int appId) {
        String sql = "SELECT sa.*, (u.first_name + ' ' + u.last_name) AS owner_name " +
                "FROM shop_applications sa " +
                "JOIN users u ON sa.user_id = u.user_id " +
                "WHERE sa.application_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, appId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SellerApplication app = new SellerApplication();
                    app.setApplicationId(rs.getInt("application_id"));
                    app.setUserId(rs.getInt("user_id"));
                    app.setOwnerName(rs.getNString("owner_name"));
                    app.setShopName(rs.getNString("shop_name"));
                    app.setBusinessEmail(rs.getString("business_email"));
                    app.setTaxCode(rs.getString("tax_code"));
                    app.setFrontIdImage(rs.getString("front_id_image"));
                    app.setBackIdImage(rs.getString("back_id_image"));
                    app.setStatus(rs.getString("status"));
                    app.setResolvedBy(rs.getInt("resolved_by"));
                    app.setCreatedAt(rs.getTimestamp("created_at"));
                    return app;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm xử lý duyệt đơn đăng ký (Chạy Transaction trực tiếp trên connection)
    public boolean approveApplication(int appId) {
        String selectSql = "SELECT user_id, shop_name FROM shop_applications WHERE application_id = ?";
        String updateAppSql = "UPDATE shop_applications SET status = 'APPROVED' WHERE application_id = ?";
        String insertShopSql = "INSERT INTO shops (owner_id, shop_name, ward_id, street_address, approval_status, status) " +
                "VALUES (?, ?, 267, N'Chưa cập nhật', 'APPROVED', 'ACTIVE')";
        String updateRoleSql = "UPDATE user_roles SET role_id = 3 WHERE user_id = ?";

        try {
            // Tắt Auto Commit để quản lý Transaction an toàn
            connection.setAutoCommit(false);

            int userId = 0;
            String shopName = "";

            // 1. Lấy thông tin đơn đăng ký
            try (PreparedStatement psSelect = connection.prepareStatement(selectSql)) {
                psSelect.setInt(1, appId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                        shopName = rs.getNString("shop_name");
                    }
                }
            }

            if (userId != 0) {
                // 2. Cập nhật trạng thái sang APPROVED
                try (PreparedStatement psUpdate = connection.prepareStatement(updateAppSql)) {
                    psUpdate.setInt(1, appId);
                    psUpdate.executeUpdate();
                }

                // 3. Tự động thêm mới vào bảng shops
                try (PreparedStatement psInsert = connection.prepareStatement(insertShopSql)) {
                    psInsert.setInt(1, userId);
                    psInsert.setNString(2, shopName);
                    psInsert.executeUpdate();
                }

                // 4. Cập nhật vai trò thành SELLER
                try (PreparedStatement psRole = connection.prepareStatement(updateRoleSql)) {
                    psRole.setInt(1, userId);
                    psRole.executeUpdate();
                }

                // Lưu lại mọi thay đổi vào DB
                connection.commit();
                return true;
            }

        } catch (SQLException e) {
            try {
                // Nếu có bất kỳ lỗi nào xảy ra, hủy bỏ toàn bộ thao tác (Rollback)
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                // Trả lại trạng thái autoCommit mặc định cho kết nối
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getTotalApplications(String searchQuery, String status, String date) {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total " +
                "FROM shop_applications sa " +
                "JOIN users u ON sa.user_id = u.user_id ";

        boolean filterStatus = status != null && !"ALL".equalsIgnoreCase(status);
        boolean filterDate = date != null && !date.trim().isEmpty();

        if (filterStatus) {
            sql += "WHERE sa.status = ? ";
        } else {
            sql += "WHERE 1=1 ";
        }

        if (filterDate) {
            sql += "AND CAST(sa.created_at AS DATE) = ? ";
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += "AND (sa.shop_name LIKE ? OR sa.tax_code LIKE ? OR CAST(sa.application_id AS VARCHAR) LIKE ?) ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (filterStatus) {
                ps.setString(index++, status.toUpperCase());
            }
            if (filterDate) {
                ps.setString(index++, date);
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String p = "%" + searchQuery.trim() + "%";
                ps.setString(index++, p);
                ps.setString(index++, p);
                ps.setString(index++, p);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}