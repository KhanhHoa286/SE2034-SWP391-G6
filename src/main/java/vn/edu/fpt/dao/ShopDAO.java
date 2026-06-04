package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.enums.ApprovalStatus;
import vn.edu.fpt.enums.ShopStatus;
import vn.edu.fpt.model.Shop;

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
}