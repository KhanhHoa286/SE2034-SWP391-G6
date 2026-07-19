package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.response.ShopResponse;
import vn.edu.fpt.enums.ApprovalStatus;
import vn.edu.fpt.enums.ShopApplicationStatus;
import vn.edu.fpt.enums.ShopStatus;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.model.Province;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO extends DBContext {

    public boolean existsByOwnerId(int ownerId) {

        String sql =
                "SELECT 1 FROM shops WHERE owner_id = ?";

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setInt(1, ownerId);

            ResultSet rs =
                    ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByShopName(String shopName) {

        String sql =
                "SELECT 1 FROM shops WHERE shop_name = ?";

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setString(1, shopName);

            ResultSet rs =
                    ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getAvailableOwnerId() {

        String sql = """
        SELECT TOP 1 u.user_id
        FROM users u
        LEFT JOIN shops s
            ON u.user_id = s.owner_id
        WHERE s.owner_id IS NULL
        ORDER BY u.user_id
        """;

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean insertShop(Shop shop) {

        String sql =
                """
                INSERT INTO shops
                (
                    owner_id,
                    shop_name,
                    logo_url,
                    description,
                    ward_id,
                    street_address,
                    approval_status,
                    status
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try {

            PreparedStatement ps =
                    connection.prepareStatement(sql);

            ps.setInt(1, shop.getOwnerId());
            ps.setString(2, shop.getShopName());
            ps.setString(3, shop.getLogoUrl());
            ps.setString(4, shop.getDescription());
            ps.setInt(5, shop.getWardId());
            ps.setString(6, shop.getStreetAddress());
            ps.setString(7, ApprovalStatus.PENDING.name());
            ps.setString(8, ShopStatus.ACTIVE.name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createShopAndGrantSellerRole(Shop shop) {
        String insertShopSql =
                """
                INSERT INTO shops
                (
                    owner_id,
                    shop_name,
                    logo_url,
                    description,
                    ward_id,
                    street_address,
                    approval_status,
                    status
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sellerRoleSql = """
                SELECT TOP 1 role_id
                FROM roles
                WHERE UPPER(LTRIM(RTRIM(role_name))) = 'SELLER'
                """;

        String hasSellerRoleSql = """
                SELECT 1
                FROM user_roles
                WHERE user_id = ?
                  AND role_id = ?
                """;

        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(insertShopSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, shop.getOwnerId());
                ps.setString(2, shop.getShopName());
                ps.setString(3, shop.getLogoUrl());
                ps.setString(4, shop.getDescription());
                ps.setInt(5, shop.getWardId());
                ps.setString(6, shop.getStreetAddress());
                ps.setString(7, ApprovalStatus.PENDING.name());
                ps.setString(8, ShopStatus.ACTIVE.name());

                if (ps.executeUpdate() <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            int sellerRoleId = 0;
            try (PreparedStatement ps = connection.prepareStatement(sellerRoleSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sellerRoleId = rs.getInt("role_id");
                }
            }

            if (sellerRoleId <= 0) {
                connection.rollback();
                return false;
            }

            boolean hasSellerRole = false;
            try (PreparedStatement ps = connection.prepareStatement(hasSellerRoleSql)) {
                ps.setInt(1, shop.getOwnerId());
                ps.setInt(2, sellerRoleId);
                try (ResultSet rs = ps.executeQuery()) {
                    hasSellerRole = rs.next();
                }
            }

            if (!hasSellerRole) {
                try (PreparedStatement ps = connection.prepareStatement(insertRoleSql)) {
                    ps.setInt(1, shop.getOwnerId());
                    ps.setInt(2, sellerRoleId);
                    if (ps.executeUpdate() <= 0) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    public Shop getShopByOwnerId(int ownerId) {
        String sql = "SELECT * FROM shops WHERE owner_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Shop.builder()
                        .shopId(rs.getInt("shop_id"))
                        .ownerId(rs.getInt("owner_id"))
                        .shopName(rs.getString("shop_name"))
                        .logoUrl(rs.getString("logo_url"))
                        .description(rs.getString("description"))
                        .wardId(rs.getInt("ward_id"))
                        .streetAddress(rs.getString("street_address"))
                        .approvalStatus(ApprovalStatus.valueOf(rs.getString("approval_status")))
                        .status(ShopStatus.valueOf(rs.getString("status")))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Shop getShopWithAddressAndOwnerByOwnerId(int ownerId) {
        String sql = "SELECT s.*, w.name AS ward_name, w.province_id AS province_id, p.name AS province_name, " +
                     "u.email AS owner_email, u.phone AS owner_phone " +
                     "FROM shops s " +
                     "LEFT JOIN wards w ON s.ward_id = w.id " +
                     "LEFT JOIN provinces p ON w.province_id = p.id " +
                     "LEFT JOIN users u ON s.owner_id = u.user_id " +
                     "WHERE s.owner_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Province province = Province.builder()
                        .id(rs.getInt("province_id"))
                        .name(rs.getString("province_name"))
                        .build();
                
                Ward ward = Ward.builder()
                        .id(rs.getInt("ward_id"))
                        .provinceId(rs.getInt("province_id"))
                        .name(rs.getString("ward_name"))
                        .province(province)
                        .build();
                
                User owner = User.builder()
                        .userId(rs.getInt("owner_id"))
                        .email(rs.getString("owner_email"))
                        .phone(rs.getString("owner_phone"))
                        .build();
                
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                java.time.LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                return Shop.builder()
                        .shopId(rs.getInt("shop_id"))
                        .ownerId(rs.getInt("owner_id"))
                        .owner(owner)
                        .shopName(rs.getString("shop_name"))
                        .logoUrl(rs.getString("logo_url"))
                        .description(rs.getString("description"))
                        .wardId(rs.getInt("ward_id"))
                        .ward(ward)
                        .streetAddress(rs.getString("street_address"))
                        .createdAt(createdAt)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Shop> getAllShops() {
        String sql = "SELECT * FROM shops";
        List<Shop> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(Shop.builder()
                        .shopId(rs.getInt("shop_id"))
                        .ownerId(rs.getInt("owner_id"))
                        .shopName(rs.getString("shop_name"))
                        .logoUrl(rs.getString("logo_url"))
                        .description(rs.getString("description"))
                        .wardId(rs.getInt("ward_id"))
                        .streetAddress(rs.getString("street_address"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HoaNK - Kiểm tra xem sản phẩm có thuộc shop người bán hay không
     */
    private final String CHECK_PRODUCT_SELLER = """
            SELECT 1 FROM products p
            JOIN shops s ON p.shop_id = s.shop_id
            WHERE p.product_id = ? AND s.owner_id = ? AND s.status = ? AND s.approval_status = ?;
            """;
    public boolean checkProductSeller(int pid, int ownerId) {
        String sql = CHECK_PRODUCT_SELLER;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pid);
            stmt.setInt(2,ownerId);
            stmt.setString(3, ShopStatus.ACTIVE.name());
            stmt.setString(4, ShopApplicationStatus.APPROVED.name());
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) {
                    return (rs.getInt(1) == 1);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateShop(Shop shop) {
        String sql = "UPDATE shops SET shop_name = ?, logo_url = ?, description = ?, ward_id = ?, street_address = ? WHERE shop_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, shop.getShopName());
            ps.setString(2, shop.getLogoUrl());
            ps.setString(3, shop.getDescription());
            ps.setInt(4, shop.getWardId());
            ps.setString(5, shop.getStreetAddress());
            ps.setInt(6, shop.getShopId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * HoaNK - Lấy shop bởi shopid
     */
    private final String GET_SHOP_BY_ID = """
            SELECT * FROM shops WHERE shop_id = ? AND status = ? AND approval_status = ?;
            """;
    public ShopResponse getShopById(int shopId) {
        String sql = GET_SHOP_BY_ID;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, shopId);
            stmt.setString(2, ShopStatus.ACTIVE.name());
            stmt.setString(3, ShopApplicationStatus.APPROVED.name());
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    ShopResponse response = new ShopResponse();
                    response.setShopId(rs.getInt("shop_id"));
                    response.setShopName(rs.getString("shop_name"));
                    response.setLogoUrl(rs.getString("logo_url"));
                    response.setFullAddress(rs.getString("street_address"));
                    response.setDescription(rs.getString("description"));
                    return response;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
