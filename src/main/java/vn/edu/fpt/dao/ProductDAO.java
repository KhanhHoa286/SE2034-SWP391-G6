package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.Product;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class ProductDAO extends DBContext {
    /**
     * HoaNK - Lấy ra danh sách sản phẩm ưu đãi sâu nhất
     */
    private static final String GET_TOP_PRODUCT = """
                                                SELECT p.product_id, s.shop_name, s.shop_id, pr.name AS province_name,
                                                p.product_name, p.base_price, p.discount_percentage, p.thumbnail_url, p.created_at
                                                FROM products p
                                                JOIN shops s ON p.shop_id = s.shop_id
                                                JOIN wards w ON s.ward_id = w.id
                                                JOIN provinces pr ON w.province_id = pr.id
                                                WHERE p.is_active = 1 AND p.is_deleted = 0    
                                                """;

    public List<ProductResponse> getTopDiscountedProducts() {
        List<ProductResponse> products = new ArrayList<>();
        String sql = GET_TOP_PRODUCT + " ORDER BY p.discount_percentage DESC";
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductResponse product = new ProductResponse();
                product.setProductId(rs.getInt("product_id"));
                product.setShopName(rs.getString("shop_name"));
                product.setShopId(rs.getInt("shop_id"));
                product.setProvinceName(rs.getString("province_name"));
                product.setProductName(rs.getString("product_name"));
                product.setBasePrice(rs.getBigDecimal("base_price"));
                product.setDiscountPercentage(rs.getInt("discount_percentage"));
                product.setThumbnailUrl(rs.getString("thumbnail_url"));
                // Tính giá sau khi áp dụng khuyến mãi
                Product p = new Product();
                p.setDiscountPercentage(rs.getInt("discount_percentage"));
                p.setBasePrice(rs.getBigDecimal("base_price"));
                //
                product.setFinalPrice(p.getDiscountedPrice());
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * HoaNK - Lấy top sản phẩm mới nhất
     */
    public List<ProductResponse> getTopNewProducts() {
        List<ProductResponse> products = new ArrayList<>();
        String sql = GET_TOP_PRODUCT + " ORDER BY p.created_at DESC";
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductResponse product = new ProductResponse();
                product.setProductId(rs.getInt("product_id"));
                product.setShopName(rs.getString("shop_name"));
                product.setShopId(rs.getInt("shop_id"));
                product.setProvinceName(rs.getString("province_name"));
                product.setProductName(rs.getString("product_name"));
                product.setBasePrice(rs.getBigDecimal("base_price"));
                product.setDiscountPercentage(rs.getInt("discount_percentage"));
                product.setThumbnailUrl(rs.getString("thumbnail_url"));
                // Tính giá sau khi áp dụng khuyến mãi
                Product p = new Product();
                p.setDiscountPercentage(rs.getInt("discount_percentage"));
                p.setBasePrice(rs.getBigDecimal("base_price"));
                //
                product.setFinalPrice(p.getDiscountedPrice());
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * HoaNK - Lấy top sản phẩm bán chạy nhất (JOIN product với shops, wards, provinces, order_details, orders)
     * Gom nhóm và tính tổng các sản phẩm đã được giao và đơn đã hoàn thành và sắp xếp tổng theo thứ tự giảm dần
     */
    private static final String GET_TOP_SELLING_PRODUCT = """
            SELECT p.product_id, s.shop_name, s.shop_id, pr.name AS province_name,
                   p.product_name, p.base_price, p.discount_percentage, p.thumbnail_url, p.created_at,
                   SUM(od.quantity) AS total_sold
            FROM products p
                     JOIN shops s ON p.shop_id = s.shop_id
                     JOIN wards w ON s.ward_id = w.id
                     JOIN provinces pr ON w.province_id = pr.id
                     JOIN order_items od ON p.product_id = od.product_id
                     JOIN sub_orders so ON od.sub_order_id = so.sub_order_id
            WHERE p.is_active = 1 AND p.is_deleted = 0 AND so.status IN ('DELIVERED')
            GROUP BY p.product_id, s.shop_name, s.shop_id, pr.name, p.product_name, p.base_price, p.discount_percentage, p.thumbnail_url, p.created_at
            ORDER BY total_sold DESC
                                                """;
    public List<ProductResponse> getTopBestSellingProducts() {
        List<ProductResponse> products = new ArrayList<>();
        String sql = GET_TOP_SELLING_PRODUCT;
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductResponse product = new ProductResponse();
                product.setProductId(rs.getInt("product_id"));
                product.setShopName(rs.getString("shop_name"));
                product.setShopId(rs.getInt("shop_id"));
                product.setProvinceName(rs.getString("province_name"));
                product.setProductName(rs.getString("product_name"));
                product.setBasePrice(rs.getBigDecimal("base_price"));
                product.setDiscountPercentage(rs.getInt("discount_percentage"));
                product.setThumbnailUrl(rs.getString("thumbnail_url"));
                // Tính giá sau khi áp dụng khuyến mãi
                Product p = new Product();
                p.setDiscountPercentage(rs.getInt("discount_percentage"));
                p.setBasePrice(rs.getBigDecimal("base_price"));
                //
                product.setFinalPrice(p.getDiscountedPrice());
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
