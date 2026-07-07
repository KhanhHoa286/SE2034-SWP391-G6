package vn.edu.fpt.dao;
import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.ProductFilterRequest;
import vn.edu.fpt.dto.response.*;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.ShopApplicationStatus;
import vn.edu.fpt.enums.ShopStatus;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductVariant;
import vn.edu.fpt.model.ProductImage;
import vn.edu.fpt.model.Color;
import vn.edu.fpt.model.Size;
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
     * HoaNK - Lấy top sản phẩm bán chạy nhất
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
                    response.setAverageRating(Math.floor(rs.getDouble("average_rating") * 10.0) / 10.0);
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

    /**
     * Lấy danh sách sản phẩm của shop (dành cho Seller) với thông tin category, variant, stock.
     * Hỗ trợ tìm kiếm, lọc trạng thái, lọc danh mục, và phân trang.
     */
    public List<ProductResponse> getSellerProductsByShopId(int shopId, String search, String statusFilter,
                                                            Integer categoryId, int page, int pageSize) {
        List<ProductResponse> products = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String sql = """
            SELECT p.product_id, s.shop_name, s.shop_id,
                   '' AS province_name,
                   p.product_name, p.gender, p.base_price, p.discount_percentage,
                   p.description, p.thumbnail_url, p.created_at,
                   c.category_name,
                   ISNULL(stock_data.total_stock, 0) AS total_stock,
                   ISNULL(sold_data.total_sold, 0) AS total_sold
            FROM products p
            JOIN shops s ON p.shop_id = s.shop_id
            LEFT JOIN categories c ON p.category_id = c.category_id
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
            """;
        params.add(shopId);

        // Tìm kiếm theo tên sản phẩm
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (p.product_name LIKE ? OR CAST(p.product_id AS VARCHAR) LIKE ?) ";
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }

        // Lọc theo danh mục (chấp nhận cả danh mục cha và danh mục con của nó)
        if (categoryId != null) {
            sql += " AND (p.category_id = ? OR p.category_id IN (SELECT category_id FROM categories WHERE parent_id = ?)) ";
            params.add(categoryId);
            params.add(categoryId);
        }

        // Lọc theo trạng thái tồn kho
        if (statusFilter != null && !statusFilter.isEmpty()) {
            switch (statusFilter) {
                case "instock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) > 15 ";
                    break;
                case "outofstock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) = 0 ";
                    break;
                case "lowstock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) > 0 AND ISNULL(stock_data.total_stock, 0) <= 15 ";
                    break;
            }
        }

        sql += " ORDER BY p.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ";
        int offset = (page - 1) * pageSize;
        params.add(offset);
        params.add(pageSize);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductResponse pr = buildProductResponse(rs);
                    // Gán thêm thông tin category vào description (tạm dùng field provinceName)
                    String catName = rs.getString("category_name");
                    pr.setProvinceName(catName != null ? catName : "Chưa phân loại");
                    products.add(pr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Đếm tổng sản phẩm của shop (dành cho Seller) theo bộ lọc tương ứng.
     */
    public int countSellerProducts(int shopId, String search, String statusFilter, Integer categoryId) {
        List<Object> params = new ArrayList<>();

        String sql = """
            SELECT COUNT(DISTINCT p.product_id)
            FROM products p
            LEFT JOIN (
                SELECT product_id, SUM(stock_quantity) AS total_stock
                FROM product_variants
                GROUP BY product_id
            ) stock_data ON p.product_id = stock_data.product_id
            WHERE p.shop_id = ? AND p.is_deleted = 0
            """;
        params.add(shopId);

        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (p.product_name LIKE ? OR CAST(p.product_id AS VARCHAR) LIKE ?) ";
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }

        if (categoryId != null) {
            sql += " AND (p.category_id = ? OR p.category_id IN (SELECT category_id FROM categories WHERE parent_id = ?)) ";
            params.add(categoryId);
            params.add(categoryId);
        }

        if (statusFilter != null && !statusFilter.isEmpty()) {
            switch (statusFilter) {
                case "instock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) > 15 ";
                    break;
                case "outofstock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) = 0 ";
                    break;
                case "lowstock":
                    sql += " AND ISNULL(stock_data.total_stock, 0) > 0 AND ISNULL(stock_data.total_stock, 0) <= 15 ";
                    break;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
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

    public int getOrCreateColorId(String colorName) {
        if (colorName == null || colorName.trim().isEmpty()) {
            return 1;
        }
        colorName = colorName.trim();
        String selectSql = "SELECT color_id FROM colors WHERE LOWER(color_name) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, colorName.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("color_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO colors (color_name, color_code) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, colorName);
            stmt.setString(2, "");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getOrCreateSizeId(String sizeName) {
        if (sizeName == null || sizeName.trim().isEmpty()) {
            return 1;
        }
        sizeName = sizeName.trim();
        String selectSql = "SELECT size_id FROM sizes WHERE LOWER(size_name) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, sizeName.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("size_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO sizes (size_name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sizeName);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int insertProduct(Product product) {
        String sql = """
            INSERT INTO products (shop_id, category_id, gender, product_name, description, base_price, discount_percentage, thumbnail_url, is_active, is_deleted, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, product.getShopId());
            if (product.getCategoryId() != null) {
                stmt.setInt(2, product.getCategoryId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, product.getGender() != null ? product.getGender().name() : "UNISEX");
            stmt.setString(4, product.getProductName());
            stmt.setString(5, product.getDescription());
            stmt.setBigDecimal(6, product.getBasePrice());
            stmt.setInt(7, product.getDiscountPercentage() != null ? product.getDiscountPercentage() : 0);
            stmt.setString(8, product.getThumbnailUrl());
            stmt.setBoolean(9, product.getIsActive() != null ? product.getIsActive() : true);
            stmt.setBoolean(10, product.getIsDeleted() != null ? product.getIsDeleted() : false);
            stmt.setString(11, product.getStatus() != null ? product.getStatus().name() : "ACTIVE");
            stmt.setObject(12, product.getCreatedAt() != null ? product.getCreatedAt() : LocalDateTime.now());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean insertProductVariant(ProductVariant variant) {
        String sql = """
            INSERT INTO product_variants (product_id, color_id, size_id, variant_name, stock_quantity)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, variant.getProductId());
            if (variant.getColorId() != null) {
                stmt.setInt(2, variant.getColorId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (variant.getSizeId() != null) {
                stmt.setInt(3, variant.getSizeId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, variant.getVariantName());
            stmt.setInt(5, variant.getStockQuantity());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertProductImage(ProductImage image) {
        String sql = """
            INSERT INTO product_images (product_id, image_url, is_primary)
            VALUES (?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, image.getProductId());
            stmt.setString(2, image.getImageUrl());
            stmt.setBoolean(3, image.getIsPrimary());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Color> getAllColors() {
        List<Color> list = new ArrayList<>();
        String sql = "SELECT color_id, color_name, color_code FROM colors ORDER BY color_name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Color(
                    rs.getInt("color_id"),
                    rs.getString("color_name"),
                    rs.getString("color_code")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Size> getAllSizes() {
        List<Size> list = new ArrayList<>();
        String sql = "SELECT size_id, size_name FROM sizes ORDER BY size_name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Size(
                    rs.getInt("size_id"),
                    rs.getString("size_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteProduct(int productId, int shopId) {
        String sql = "UPDATE products SET is_deleted = 1 WHERE product_id = ? AND shop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, shopId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * HoaNK - Lấy các thuộc tính hiển thị cho trang add-product-review
     */
    private final String GET_FIELDS_RESPONSE_ADD_REVIEW = """
            SELECT p.product_id,p.thumbnail_url,p.product_name,p.base_price,p.discount_percentage, oi.order_item_id,so.sub_order_id
            FROM products p
            JOIN order_items oi ON oi.product_id = p.product_id
            JOIN sub_orders so ON so.sub_order_id = oi.sub_order_id
            WHERE p.product_id = ? AND oi.order_item_id = ? 
            """;
    public AddReviewResponse getFieldsResponseAddReview(int productId, int orderItemId) {
        String sql = GET_FIELDS_RESPONSE_ADD_REVIEW;

        AddReviewResponse addReviewResponse = new AddReviewResponse();
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2,orderItemId);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    addReviewResponse.setProductId(rs.getInt("product_id"));
                    addReviewResponse.setOrderItemId(rs.getInt("order_item_id"));
                    addReviewResponse.setSubOrderId(rs.getInt("sub_order_id"));
                    addReviewResponse.setProductName(rs.getString("product_name"));
                    addReviewResponse.setThumbnail(rs.getString("thumbnail_url"));

                    Product product = new Product();
                    product.setBasePrice(rs.getBigDecimal("base_price"));
                    product.setDiscountPercentage(rs.getInt("discount_percentage"));

                    addReviewResponse.setDiscountedPrice(product.getDiscountedPrice());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return addReviewResponse;
    }

    public List<Integer> getDiscountPercentages() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT discount_percentage FROM products ORDER BY discount_percentage";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("discount_percentage"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Fallback to standard discount rates if database has none
        if (list.isEmpty()) {
            list.add(0);
            list.add(5);
            list.add(10);
            list.add(15);
            list.add(20);
        }
        return list;
    }

        /**
     * HoaNK - Lấy sản phẩm mua ngay ở trang details
     */
    private final String GET_VARIANT_PRODUCT_DETAILS = """
                SELECT pv.variant_id,p.thumbnail_url,p.product_name,p.product_id,p.base_price,p.discount_percentage,s.size_name, c.color_name, sh.shop_name,sh.shop_id
                FROM product_variants pv
                JOIN products p ON p.product_id = pv.product_id
                JOIN sizes s ON s.size_id = pv.size_id
                JOIN colors c ON c.color_id = pv.color_id
                JOIN shops sh ON sh.shop_id = p.shop_id
                WHERE variant_id = ?
                """;
    public SummaryOrderCheckoutResponse getVariantInfoForCheckout(int variantId) {
        String sql = GET_VARIANT_PRODUCT_DETAILS;

        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, variantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SummaryOrderCheckoutResponse summary = new SummaryOrderCheckoutResponse();
                    summary.setVariantId(rs.getInt("variant_id"));
                    summary.setColorName(rs.getString("color_name"));
                    summary.setSizeName(rs.getString("size_name"));
                    summary.setProductName(rs.getString("product_name"));
                    summary.setShopName(rs.getString("shop_name"));
                    summary.setShopId(rs.getInt("shop_id"));
                    summary.setProductId(rs.getInt("product_id"));
                    summary.setThumbnail(rs.getString("thumbnail_url"));
                    //
                    Product product = new Product();
                    product.setBasePrice(rs.getBigDecimal("base_price"));
                    product.setDiscountPercentage(rs.getInt("discount_percentage"));
                    summary.setPrice(product.getDiscountedPrice());
                    //
                    return summary;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ? AND is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Product.builder()
                            .productId(rs.getInt("product_id"))
                            .shopId(rs.getInt("shop_id"))
                            .categoryId(rs.getObject("category_id") != null ? rs.getInt("category_id") : null)
                            .gender(rs.getString("gender") != null ? Gender.valueOf(rs.getString("gender")) : null)
                            .productName(rs.getString("product_name"))
                            .description(rs.getString("description"))
                            .basePrice(rs.getBigDecimal("base_price"))
                            .discountPercentage(rs.getInt("discount_percentage"))
                            .thumbnailUrl(rs.getString("thumbnail_url"))
                            .isActive(rs.getBoolean("is_active"))
                            .isDeleted(rs.getBoolean("is_deleted"))
                            .status(rs.getString("status") != null ? vn.edu.fpt.enums.ProductStatus.valueOf(rs.getString("status")) : null)
                            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductVariant> getVariantsByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = """
            SELECT pv.variant_id, pv.product_id, pv.color_id, pv.size_id, pv.variant_name, pv.stock_quantity,
                   c.color_name, c.color_code, s.size_name
            FROM product_variants pv
            LEFT JOIN colors c ON pv.color_id = c.color_id
            LEFT JOIN sizes s ON pv.size_id = s.size_id
            WHERE pv.product_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Color color = null;
                    if (rs.getObject("color_id") != null) {
                        color = new Color(rs.getInt("color_id"), rs.getString("color_name"), rs.getString("color_code"));
                    }
                    Size size = null;
                    if (rs.getObject("size_id") != null) {
                        size = new Size(rs.getInt("size_id"), rs.getString("size_name"));
                    }
                    list.add(ProductVariant.builder()
                            .variantId(rs.getInt("variant_id"))
                            .productId(rs.getInt("product_id"))
                            .colorId(rs.getObject("color_id") != null ? rs.getInt("color_id") : null)
                            .color(color)
                            .sizeId(rs.getObject("size_id") != null ? rs.getInt("size_id") : null)
                            .size(size)
                            .variantName(rs.getString("variant_name"))
                            .stockQuantity(rs.getInt("stock_quantity"))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ProductImage> getProductImagesByProductId(int productId) {
        List<ProductImage> list = new ArrayList<>();
        String sql = "SELECT * FROM product_images WHERE product_id = ? ORDER BY is_primary DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(ProductImage.builder()
                            .imageId(rs.getInt("image_id"))
                            .productId(rs.getInt("product_id"))
                            .imageUrl(rs.getString("image_url"))
                            .isPrimary(rs.getBoolean("is_primary"))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateProduct(Product product) {
        String sql = """
            UPDATE products
            SET category_id = ?, gender = ?, product_name = ?, description = ?, base_price = ?, discount_percentage = ?, thumbnail_url = ?
            WHERE product_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (product.getCategoryId() != null) {
                stmt.setInt(1, product.getCategoryId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, product.getGender() != null ? product.getGender().name() : "UNISEX");
            stmt.setString(3, product.getProductName());
            stmt.setString(4, product.getDescription());
            stmt.setBigDecimal(5, product.getBasePrice());
            stmt.setInt(6, product.getDiscountPercentage() != null ? product.getDiscountPercentage() : 0);
            stmt.setString(7, product.getThumbnailUrl());
            stmt.setInt(8, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteVariantsByProductId(int productId) {
        String sql = "DELETE FROM product_variants WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteImagesByProductId(int productId) {
        String sql = "DELETE FROM product_images WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

