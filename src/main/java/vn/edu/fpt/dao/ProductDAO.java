package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.ProductFilterRequest;
import vn.edu.fpt.dto.response.ProductDetailResponse;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.dto.response.SizeResponse;
import vn.edu.fpt.dto.response.ColorResponse;
import vn.edu.fpt.dto.response.ImageResponse;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.ShopApplicationStatus;
import vn.edu.fpt.enums.ShopStatus;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.model.Product;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDateTime;
public class ProductDAO extends DBContext {
    /**
     * HoaNK - Lấy ra danh sách sản phẩm ưu đãi sâu nhất
     */
    private static final String BASE_PRODUCT_QUERY = """
    SELECT p.product_id, s.shop_name, s.shop_id, pr.name AS province_name,
            p.product_name,p.gender, p.base_price, p.discount_percentage,p.description, p.thumbnail_url, p.created_at, 
            SUM(pa.stock_quantity) AS total_stock,ISNULL(sold_data.total_sold, 0) AS total_sold
    FROM products p
             JOIN shops s ON p.shop_id = s.shop_id
             JOIN wards w ON s.ward_id = w.id
             JOIN provinces pr ON w.province_id = pr.id
             JOIN product_variants pa ON p.product_id = pa.product_id
             LEFT JOIN (  -- Bổ sung bộ đếm hàng đã giao thành công
                  SELECT od.product_id, SUM(od.quantity) AS total_sold
                  FROM order_items od
                  JOIN sub_orders so ON od.sub_order_id = so.sub_order_id
                  WHERE so.status = 'DELIVERED'
                  GROUP BY od.product_id
             ) sold_data ON p.product_id = sold_data.product_id
    WHERE p.is_active = 1 AND p.is_deleted = 0 AND s.status = 'ACTIVE' AND s.approval_status = 'APPROVED'
""";

    private static final String GROUP_PRODUCT = """
           GROUP BY p.product_id, s.shop_name, s.shop_id, pr.name ,
           p.product_name,p.gender, p.base_price, p.discount_percentage,p.description, p.thumbnail_url, p.created_at, sold_data.total_sold
""";

    private static final String PAGINATION_PRODUCT = """
                         OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;

    public List<ProductResponse> getTopDiscountedProducts() {
        List<ProductResponse> products = new ArrayList<>();
        String sql = BASE_PRODUCT_QUERY + GROUP_PRODUCT + " ORDER BY p.discount_percentage DESC";
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(buildProductResponse(rs));
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
        String sql = BASE_PRODUCT_QUERY + GROUP_PRODUCT + " ORDER BY p.created_at DESC";
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(buildProductResponse(rs));
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

    public List<ProductResponse> getTopBestSellingProducts() {
        List<ProductResponse> products = new ArrayList<>();
        String sql = BASE_PRODUCT_QUERY + GROUP_PRODUCT + " ORDER BY sold_data.total_sold DESC ";
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(buildProductResponse(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * HoaNK - Lấy product theo giới tính, cate, province, giá, tìm kiếm, xem tất cả .. ở trang product list
     */
    public List<ProductResponse> getAllProductByFilter(ProductFilterRequest productFilterRequest) {
        List<ProductResponse> products = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String sql = BASE_PRODUCT_QUERY;

        if( productFilterRequest.getShopId() != null) { // lọc shop
            sql += " AND s.shop_id = ? ";
            params.add(productFilterRequest.getShopId());
        }

        if (productFilterRequest.getType() != null && !productFilterRequest.getType().trim().isEmpty()) { // loc theo gioi tinh
            if (Gender.UNISEX.name().equalsIgnoreCase(productFilterRequest.getType().trim())) {
                sql += " AND p.gender = ? ";
                params.add(productFilterRequest.getType());
            } else {
                sql += " AND (p.gender = ? OR p.gender = 'UNISEX') ";
                params.add(productFilterRequest.getType());
            }
        }
        if (productFilterRequest.getCid() != null) { // loc theo category
            sql += " AND p.category_id IN (SELECT category_id FROM categories where category_id = ? OR parent_id = ?)";
            params.add(productFilterRequest.getCid());
            params.add(productFilterRequest.getCid());
        }
        if (productFilterRequest.getTextSearch() != null && !productFilterRequest.getTextSearch().trim().isEmpty()) { // loc theo search
            sql += " AND (p.product_name LIKE ? OR p.description LIKE ?)";
            params.add("%" + productFilterRequest.getTextSearch() + "%");
            params.add("%" + productFilterRequest.getTextSearch() + "%");
        }
        if (productFilterRequest.getProvinceId() != null) { // loc theo tinh thanh
            sql += " AND pr.id = ? ";
            params.add(productFilterRequest.getProvinceId());
        }

        String sqlFinalPrice = "(p.base_price - (p.base_price * (p.discount_percentage / 100.0)))"; // giá cuối sau giảm giá
        if (productFilterRequest.getPriceFrom() != null && productFilterRequest.getPriceTo() != null) {
            sql += " AND " + sqlFinalPrice + " BETWEEN ? AND ? ";
            params.add(productFilterRequest.getPriceFrom());
            params.add(productFilterRequest.getPriceTo());
        }

        sql += GROUP_PRODUCT;

        // lọc theo view all hoặc giá tăng dần giảm dần
        if ("discount".equals(productFilterRequest.getSortBy())) { // uus dai nhieu nhat
            sql += " ORDER BY p.discount_percentage DESC ";
        } else if ("best_seller".equals(productFilterRequest.getSortBy())) { // ban chay nhat
            sql += " ORDER BY total_sold DESC ";
        } else if ("high_price".equals(productFilterRequest.getSortBy())) {
            sql += " ORDER BY " + sqlFinalPrice + " DESC";
        } else if ("low_price".equals(productFilterRequest.getSortBy())) {
            sql += " ORDER BY " + sqlFinalPrice + " ASC";
        } else { // mặc định sẽ sắp xếp theo mới nhất để còn phục vụ cho phân trang nếu ko có order nào đc chọn
            sql += " ORDER BY p.created_at DESC ";
        }

        // phân trang (bắt buộc có order)
        int offset = (productFilterRequest.getPage() - 1) * productFilterRequest.getPageSize();
        params.add(offset);
        params.add(productFilterRequest.getPageSize()); // 1 trang 8 sản phẩm
        sql += PAGINATION_PRODUCT;
        //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // đưa dữ liệu vào ?
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            //
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(buildProductResponse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * HoaNK - Đếm số lượng sản phẩm khi lọc
     */
    private static final String BASE_COUNT_PRODUCT = """
    SELECT COUNT(DISTINCT p.product_id)
    FROM products p
             JOIN shops s ON p.shop_id = s.shop_id
             JOIN wards w ON s.ward_id = w.id
             JOIN provinces pr ON w.province_id = pr.id
             JOIN product_variants pa ON p.product_id = pa.product_id
             LEFT JOIN (
                  SELECT od.product_id, SUM(od.quantity) AS total_sold
                  FROM order_items od
                  JOIN sub_orders so ON od.sub_order_id = so.sub_order_id
                  WHERE so.status = 'DELIVERED'
                  GROUP BY od.product_id
             ) sold_data ON p.product_id = sold_data.product_id
    WHERE p.is_active = 1 AND p.is_deleted = 0 AND s.status = 'ACTIVE' AND s.approval_status = 'APPROVED'
""";

    public int getTotalProductFilter(ProductFilterRequest productFilterRequest) {
        List<ProductResponse> products = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String sql = BASE_COUNT_PRODUCT;

        if(productFilterRequest.getShopId() != null) {
            sql += " AND s.shop_id = ? ";
            params.add(productFilterRequest.getShopId());
        }

        if (productFilterRequest.getType() != null && !productFilterRequest.getType().trim().isEmpty()) { // loc theo gioi tinh
            if (Gender.UNISEX.name().equalsIgnoreCase(productFilterRequest.getType().trim())) {
                sql += " AND p.gender = ? ";
                params.add(productFilterRequest.getType());
            } else {
                sql += " AND (p.gender = ? OR p.gender = 'UNISEX') ";
                params.add(productFilterRequest.getType());
            }
        }
        if (productFilterRequest.getCid() != null) { // loc theo category
            sql += " AND p.category_id IN (SELECT category_id FROM categories where category_id = ? OR parent_id = ?)";
            params.add(productFilterRequest.getCid());
            params.add(productFilterRequest.getCid());
        }
        if (productFilterRequest.getTextSearch() != null && !productFilterRequest.getTextSearch().trim().isEmpty()) { // loc theo search
            sql += " AND (p.product_name LIKE ? OR p.description LIKE ?)";
            params.add("%" + productFilterRequest.getTextSearch() + "%");
            params.add("%" + productFilterRequest.getTextSearch() + "%");
        }
        if (productFilterRequest.getProvinceId() != null) { // loc theo tinh thanh
            sql += " AND pr.id = ? ";
            params.add(productFilterRequest.getProvinceId());
        }

        String sqlFinalPrice = "(p.base_price - (p.base_price * (p.discount_percentage / 100.0)))"; // giá cuối sau giảm giá
        if (productFilterRequest.getPriceFrom() != null && productFilterRequest.getPriceTo() != null) {
            sql += " AND " + sqlFinalPrice + " BETWEEN ? AND ? ";
            params.add(productFilterRequest.getPriceFrom());
            params.add(productFilterRequest.getPriceTo());
        }

        if ("bestSeller".equals(productFilterRequest.getSortBy())) {
            sql += " AND sold_data.total_sold IS NOT NULL AND sold_data.total_sold > 0 \n";
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // đưa dữ liệu vào ?
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            //
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HoaNK - Method dùng chung để đổ dữ liệu vào productResponse
     */
    private ProductResponse buildProductResponse(ResultSet rs) throws SQLException {
        ProductResponse product = new ProductResponse();

        // đôẻ ữ liệu vào
        product.setProductId(rs.getInt("product_id"));
        product.setShopName(rs.getString("shop_name"));
        product.setShopId(rs.getInt("shop_id"));
        product.setProvinceName(rs.getString("province_name"));
        product.setProductName(rs.getString("product_name"));
        product.setGender(Gender.valueOf(rs.getString("gender")));
        product.setBasePrice(rs.getBigDecimal("base_price"));
        product.setDiscountPercentage(rs.getInt("discount_percentage"));
        product.setThumbnailUrl(rs.getString("thumbnail_url"));
        product.setTotalStock(rs.getInt("total_stock"));

        // set totalSold if it exists in ResultSet
        try {
            product.setTotalSold(rs.getInt("total_sold"));
        } catch (SQLException e) {
            product.setTotalSold(0);
        }

        // tính khuyến mãi
        Product p = new Product();
        p.setDiscountPercentage(rs.getInt("discount_percentage"));
        p.setBasePrice(rs.getBigDecimal("base_price"));
        product.setFinalPrice(p.getDiscountedPrice());

        return product;
    }

    /**
     * HoaNK - Lay ra thong tin chi tiet san pham tu product_id
     */
    private final String GET_PRODUCT_DETAIL_BY_PRODUCTID = """
            SELECT p.product_id,p.product_name,p.gender,p.base_price,p.discount_percentage,p.description,s.shop_id,s.shop_name,s.logo_url,ISNULL(review_data.avg_rating, 0.0) AS average_rating,ISNULL(review_data.total_reviews, 0) AS total_reviews, ISNULL(sold_data.total_sold, 0) AS total_sold
            FROM products p
            JOIN shops s ON p.shop_id = s.shop_id
            LEFT JOIN ( -- tính trung bình sao đánh gia và đếm số đánh giá
                SELECT product_id,\s
                       AVG(CAST(rating AS DECIMAL(3,2))) AS avg_rating,\s
                       COUNT(review_id) AS total_reviews
                FROM product_reviews
                GROUP BY product_id
            ) review_data ON p.product_id = review_data.product_id
            LEFT JOIN ( -- tính tổng số đơn hàng đã bán được
                SELECT od.product_id,\s
                       SUM(od.quantity) AS total_sold
                FROM order_items od
                JOIN sub_orders so ON od.sub_order_id = so.sub_order_id
                WHERE so.status = ?
                GROUP BY od.product_id
            ) sold_data ON p.product_id = sold_data.product_id
            WHERE p.product_id = ? AND s.status = ? AND s.approval_status = ?;
            """;

    public ProductDetailResponse getProductDetailByProductId(Integer productId) {
        String sql = GET_PRODUCT_DETAIL_BY_PRODUCTID;
        ProductDetailResponse response = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, SubOrderStatus.DELIVERED.name());
            stmt.setInt(2, productId);
            stmt.setString(3, ShopStatus.ACTIVE.name());
            stmt.setString(4, ShopApplicationStatus.APPROVED.name());
            //
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    response = new ProductDetailResponse();
                    response.setProductId(rs.getInt("product_id"));
                    response.setProductName(rs.getString("product_name"));
                    response.setBasePrice(rs.getBigDecimal("base_price"));
                    response.setDiscountPercentage(rs.getInt("discount_percentage"));
                    response.setDescription(rs.getString("description"));
                    response.setShopId(rs.getInt("shop_id"));
                    response.setShopName(rs.getString("shop_name"));
                    response.setLogoUrl(rs.getString("logo_url"));
                    response.setGender(Gender.valueOf(rs.getString("gender")));
                    // trung binh danh gai, tong review, va tong don hang da ban
                    response.setAverageRating(rs.getDouble("average_rating"));
                    response.setTotalReview(rs.getInt("total_reviews"));
                    response.setTotalSold(rs.getInt("total_sold"));

                    // 3 list hiển thị size, màu va ảnh chính phụ
                    response.setSizes(getSizesByProductId(productId));
                    response.setColors(getColorsByProductId(productId));
                    response.setUrlImageDetails(getImagesByProductId(productId));

                    // set gia sau khi giam %
                    Product p = new Product();
                    p.setDiscountPercentage(rs.getInt("discount_percentage"));
                    p.setBasePrice(rs.getBigDecimal("base_price"));
                    response.setFinalPrice(p.getDiscountedPrice());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * HoaNK - Lấy ra list size theo productId
     */
    private final String GET_SIZE_BY_PRODUCTID = """
            SELECT DISTINCT s.size_id, s.size_name
                    FROM product_variants pv
                    JOIN sizes s ON pv.size_id = s.size_id
                    WHERE pv.product_id = ?
            """;

    private List<SizeResponse> getSizesByProductId(Integer productId) {
        String sql = GET_SIZE_BY_PRODUCTID;
        List<SizeResponse> sizes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SizeResponse response = new SizeResponse();
                    response.setSizeId(rs.getInt("size_id"));
                    response.setSizeName(rs.getString("size_name"));
                    sizes.add(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sizes;
    }

    /**
     * HoaNK - Lấy ra list color theo productId
     */
    private final String GET_COLOR_BY_PRODUCTID = """
            SELECT DISTINCT c.color_id, c.color_name
                    FROM product_variants pv
                    JOIN colors c ON pv.color_id = c.color_id
                    WHERE pv.product_id = ?
            """;

    private List<ColorResponse> getColorsByProductId(Integer productId) {
        String sql = GET_COLOR_BY_PRODUCTID;
        List<ColorResponse> colors = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ColorResponse response = new ColorResponse();
                    response.setColorId(rs.getInt("color_id"));
                    response.setColorName(rs.getString("color_name"));
                    colors.add(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colors;
    }

    /**
     * HoaNK - Lấy ra list image details theo productId
     */
    private final String GET_IMAGES_BY_PRODUCTID = """
                    SELECT image_id, image_url, is_primary 
                    FROM product_images 
                    WHERE product_id = ? ORDER BY is_primary DESC
    """;

    private List<ImageResponse> getImagesByProductId(Integer productId) {
        String sql = GET_IMAGES_BY_PRODUCTID;
        List<ImageResponse> images = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ImageResponse response = new ImageResponse();
                    response.setImageId(rs.getInt("image_id"));
                    response.setImageUrl(rs.getString("image_url"));
                    response.setIsPrimary(rs.getBoolean("is_primary"));
                    images.add(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    public List<ProductResponse> getShopBestSellingProducts(int shopId, int limit) {
        List<ProductResponse> products = new ArrayList<>();

        // Truy vấn đơn giản hóa: loại bỏ JOIN wards/provinces để tránh kết quả rỗng
        // khi shop.ward_id không khớp với bảng wards
        String sql = """
            SELECT TOP (?)
                p.product_id,
                s.shop_name,
                s.shop_id,
                '' AS province_name,
                p.product_name,
                p.gender,
                p.base_price,
                p.discount_percentage,
                p.description,
                p.thumbnail_url,
                p.created_at,
                ISNULL(stock_data.total_stock, 0) AS total_stock,
                ISNULL(sold_data.total_sold, 0) AS total_sold
            FROM products p
            JOIN shops s ON p.shop_id = s.shop_id
            LEFT JOIN (
                SELECT product_id, SUM(stock_quantity) AS total_stock
                FROM product_variants
                GROUP BY product_id
            ) stock_data ON p.product_id = stock_data.product_id
            LEFT JOIN (
                SELECT od.product_id, SUM(od.quantity) AS total_sold
                FROM order_items od
                JOIN sub_orders so ON od.sub_order_id = so.sub_order_id
                WHERE so.status = 'DELIVERED'
                GROUP BY od.product_id
            ) sold_data ON p.product_id = sold_data.product_id
            WHERE p.shop_id = ? AND p.is_deleted = 0
            ORDER BY ISNULL(sold_data.total_sold, 0) DESC
            """;

        System.err.println("[DEBUG] getShopBestSellingProducts -> shopId=" + shopId + ", limit=" + limit);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, limit);
            stmt.setInt(2, shopId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(buildProductResponse(rs));
            }
            System.err.println("[DEBUG] Bestsellers found: " + products.size());
        } catch (SQLException e) {
            System.err.println("[DEBUG] Bestseller query error: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * HoaNK - Lấy ra so lượng biến thể còn trong kho
     */
    private final String GET_VARIANT_STOCK = """
        SELECT pv.stock_quantity FROM product_variants pv
        WHERE pv.variant_id = ?;
    """;
    public int getVariantStock(int variantId) {
        String sql = GET_VARIANT_STOCK;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
            return 0;
    }

    /**
     * HoaNK - Lấy ra 4 sản phẩm có giá , giới tính gần với sản phẩm chi tiết nhất
     */
    public List<ProductResponse> getTop4ProductRelated(String gender, int productId,BigDecimal price) {
        List<ProductResponse> products = new ArrayList<>();

        String sql = BASE_PRODUCT_QUERY
                + " AND p.gender = ? AND p.product_id != ? "
                + GROUP_PRODUCT
                + " ORDER BY ABS(p.base_price - ?) ASC "
                + PAGINATION_PRODUCT; // 2 ? cần truyền

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1,gender);
            stmt.setInt(2, productId);
            stmt.setBigDecimal(3, price);
            stmt.setInt(4, 0);
            stmt.setInt(5,4);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(buildProductResponse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public int countActiveProductsByShopId(int shopId) {
        String sql = "SELECT COUNT(*) FROM products WHERE shop_id = ? AND is_active = 1 AND is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, shopId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HoaNK - Lấy ra id product variant từ product id, size id và color id
     */
    private final String GET_VARIANT_ID = """
            SELECT variant_id FROM product_variants
        WHERE product_id = ? AND color_id = ? AND size_id = ?
    """;
    public int getVariantById(int pid, int sizeId, int colorId) {
        String sql = GET_VARIANT_ID;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pid);
            stmt.setInt(2, colorId);
            stmt.setInt(3, sizeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("variant_id");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

