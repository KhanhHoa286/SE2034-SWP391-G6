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

        String sql = "SELECT s.shop_id AS application_id, s.owner_id AS user_id, (u.first_name + ' ' + u.last_name) AS owner_name, " +
                "s.shop_name, u.email AS business_email, u.citizen_id AS tax_code, u.front_id_image, u.back_id_image, s.approval_status AS status, " +
                "0 AS resolved_by, s.created_at " +
                "FROM shops s " +
                "JOIN users u ON s.owner_id = u.user_id ";

        boolean filterStatus = status != null && !"ALL".equalsIgnoreCase(status);
        boolean filterDate = date != null && !date.trim().isEmpty();

        if (filterStatus) {
            sql += "WHERE s.approval_status = ? ";
        } else {
            sql += "WHERE 1=1 ";
        }

        if (filterDate) {
            sql += "AND CAST(s.created_at AS DATE) = ? ";
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += "AND (s.shop_name LIKE ? OR CAST(s.shop_id AS VARCHAR) LIKE ?) ";
        }
        sql += " ORDER BY s.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

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
        String sql = "SELECT s.shop_id AS application_id, s.owner_id AS user_id, (u.first_name + ' ' + u.last_name) AS owner_name, " +
                "s.shop_name, u.email AS business_email, u.citizen_id AS tax_code, u.front_id_image, u.back_id_image, s.approval_status AS status, " +
                "0 AS resolved_by, s.created_at " +
                "FROM shops s " +
                "JOIN users u ON s.owner_id = u.user_id " +
                "WHERE s.shop_id = ?";
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
        String selectSql = "SELECT owner_id AS user_id FROM shops WHERE shop_id = ?";
        String updateAppSql = "UPDATE shops SET approval_status = 'APPROVED' WHERE shop_id = ?";
        String checkRoleSql = "SELECT 1 FROM user_roles WHERE user_id = ? AND role_id = 3";
        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, 3)";

        try {
            connection.setAutoCommit(false);
            int userId = 0;

            try (PreparedStatement psSelect = connection.prepareStatement(selectSql)) {
                psSelect.setInt(1, appId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }

            if (userId != 0) {
                try (PreparedStatement psUpdate = connection.prepareStatement(updateAppSql)) {
                    psUpdate.setInt(1, appId);
                    psUpdate.executeUpdate();
                }

                boolean hasRole = false;
                try (PreparedStatement psCheck = connection.prepareStatement(checkRoleSql)) {
                    psCheck.setInt(1, userId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        hasRole = rs.next();
                    }
                }

                if (!hasRole) {
                    try (PreparedStatement psRole = connection.prepareStatement(insertRoleSql)) {
                        psRole.setInt(1, userId);
                        psRole.executeUpdate();
                    }
                }

                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
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
                "FROM shops s " +
                "JOIN users u ON s.owner_id = u.user_id ";

        boolean filterStatus = status != null && !"ALL".equalsIgnoreCase(status);
        boolean filterDate = date != null && !date.trim().isEmpty();

        if (filterStatus) {
            sql += "WHERE s.approval_status = ? ";
        } else {
            sql += "WHERE 1=1 ";
        }

        if (filterDate) {
            sql += "AND CAST(s.created_at AS DATE) = ? ";
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += "AND (s.shop_name LIKE ? OR CAST(s.shop_id AS VARCHAR) LIKE ?) ";
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
