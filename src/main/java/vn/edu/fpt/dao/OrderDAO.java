package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.CheckoutRequest;
import vn.edu.fpt.dto.request.OrderHistoryFilterRequest;
import vn.edu.fpt.dto.response.*;
import vn.edu.fpt.enums.PaymentMethod;
import vn.edu.fpt.enums.PaymentStatus;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.model.Product;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class OrderDAO extends DBContext {
    private final CartDAO cartDAO = new CartDAO();

    /**
     * HoaNK - Lấy ra commission rate mới nhất từ bảng
     */
    private final String GET_LATEST_COMMISSION_RATE = """
        SELECT TOP 1 commission_rate
        FROM commission_configs
        WHERE effective_date <= GETDATE()
        ORDER BY effective_date DESC
        """;
    public BigDecimal getLatestCommissionRate() {
        String sql = GET_LATEST_COMMISSION_RATE;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("commission_rate").divide(BigDecimal.valueOf(100));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //nếu chưa có dữ liệu trong bảng thì mặc định 1.5%
        return BigDecimal.valueOf(0.015);
    }

    public BigDecimal getTodayRevenue(int shopId) {
        String sql = """
            SELECT SUM(total_amount) AS today_revenue
            FROM sub_orders
            WHERE shop_id = ? 
              AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)
              AND status != 'CANCELLED'
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal("today_revenue");
                return val != null ? val : BigDecimal.ZERO;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public double getRevenueTrend(int shopId) {
        String sql = """
            SELECT 
                SUM(CASE WHEN CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) THEN total_amount ELSE 0 END) AS today_rev,
                SUM(CASE WHEN CAST(created_at AS DATE) = CAST(DATEADD(day, -1, GETDATE()) AS DATE) THEN total_amount ELSE 0 END) AS yesterday_rev
            FROM sub_orders
            WHERE shop_id = ? AND status != 'CANCELLED'
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal today = rs.getBigDecimal("today_rev");
                BigDecimal yesterday = rs.getBigDecimal("yesterday_rev");
                double t = today != null ? today.doubleValue() : 0.0;
                double y = yesterday != null ? yesterday.doubleValue() : 0.0;
                if (y == 0.0) {
                    return t > 0.0 ? 100.0 : 0.0;
                }
                return ((t - y) / y) * 100.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTodayNewOrders(int shopId) {
        String sql = """
            SELECT COUNT(*) AS today_orders
            FROM sub_orders
            WHERE shop_id = ? 
              AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("today_orders");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOrdersTrendCount(int shopId) {
        String sql = """
            SELECT 
                SUM(CASE WHEN CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) -
                SUM(CASE WHEN CAST(created_at AS DATE) = CAST(DATEADD(day, -1, GETDATE()) AS DATE) THEN 1 ELSE 0 END) AS trend
            FROM sub_orders
            WHERE shop_id = ?
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("trend");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, Object>> getRevenueLast7Days(int shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT 
                FORMAT(date_range.d, 'dd/MM') AS date_label,
                ISNULL(SUM(so.total_amount), 0) AS daily_revenue
            FROM (
                SELECT CAST(GETDATE() AS DATE) AS d
                UNION ALL SELECT DATEADD(day, -1, CAST(GETDATE() AS DATE))
                UNION ALL SELECT DATEADD(day, -2, CAST(GETDATE() AS DATE))
                UNION ALL SELECT DATEADD(day, -3, CAST(GETDATE() AS DATE))
                UNION ALL SELECT DATEADD(day, -4, CAST(GETDATE() AS DATE))
                UNION ALL SELECT DATEADD(day, -5, CAST(GETDATE() AS DATE))
                UNION ALL SELECT DATEADD(day, -6, CAST(GETDATE() AS DATE))
            ) date_range
            LEFT JOIN sub_orders so 
              ON CAST(so.created_at AS DATE) = date_range.d 
             AND so.shop_id = ? 
             AND so.status != 'CANCELLED'
            GROUP BY date_range.d
            ORDER BY date_range.d ASC
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("label", rs.getString("date_label"));
                map.put("revenue", rs.getBigDecimal("daily_revenue"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getRecentSubOrders(int shopId, int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT TOP (?) 
                so.sub_order_id,
                u.first_name + ' ' + u.last_name AS customer_name,
                u.email AS customer_email,
                (
                    SELECT STRING_AGG(p.product_name + ' (x' + CAST(oi.quantity AS VARCHAR) + ')', ', ')
                    FROM order_items oi
                    JOIN products p ON oi.product_id = p.product_id
                    WHERE oi.sub_order_id = so.sub_order_id
                ) AS products_summary,
                so.total_amount,
                so.status
            FROM sub_orders so
            JOIN master_orders mo ON so.master_order_id = mo.master_order_id
            JOIN users u ON mo.customer_id = u.user_id
            WHERE so.shop_id = ?
            ORDER BY so.created_at DESC
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setInt(2, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("subOrderId", rs.getInt("sub_order_id"));
                map.put("customerName", rs.getString("customer_name"));
                map.put("customerEmail", rs.getString("customer_email"));
                map.put("productsSummary", rs.getString("products_summary"));
                map.put("totalAmount", rs.getBigDecimal("total_amount"));
                map.put("status", rs.getString("status"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HoaNK - Lấy ra danh sách suborder của người dùng
     */
    private final String GET_SUBORDER_BY_CUSTOMERID = """
            select so.sub_order_id, so.created_at, so.status,so.total_amount,s.shop_name,mo.payment_method,mo.master_order_id from sub_orders so
            JOIN shops s ON s.shop_id = so.shop_id
            JOIN master_orders mo ON mo.master_order_id = so.master_order_id
            WHERE mo.customer_id = ?
            """;
    private final String PAGING = """
            ORDER BY so.created_at DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
    public List<OrderHistoryResponse> getSubOrderByCustomerId(int customer_id, OrderHistoryFilterRequest orderRequest, int pageSize) {
        String sql = GET_SUBORDER_BY_CUSTOMERID;
        List<OrderHistoryResponse> orderResponse = new ArrayList<>();
        // check điều kiện order từ ngày nào đến ngày nào
        if(orderRequest.getFromDate() != null && orderRequest.getToDate() != null && !orderRequest.getFromDate().isEmpty()  && !orderRequest.getToDate().isEmpty()) {
            sql += " AND so.created_at BETWEEN CAST(? AS DATETIME) + ' 00:00:00' AND CAST(? AS DATETIME) + ' 23:59:59' ";
        }
        // lọc theo trạng thái đơn hàng
        if(orderRequest.getStatus() != null && !orderRequest.getStatus().isEmpty()) {
            sql += " AND so.status = ? ";
        }
        sql += PAGING;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            stmt.setInt(index++, customer_id);
            if(orderRequest.getFromDate() != null && orderRequest.getToDate() != null && !orderRequest.getFromDate().isEmpty()  && !orderRequest.getToDate().isEmpty()) {
                stmt.setString(index++, orderRequest.getFromDate());
                stmt.setString(index++, orderRequest.getToDate());
            }
            if(orderRequest.getStatus() != null && !orderRequest.getStatus().isEmpty()) {
                stmt.setString(index++, orderRequest.getStatus());
            }
            stmt.setInt(index++,(orderRequest.getPageNumber() - 1) * pageSize);
            stmt.setInt(index++, pageSize);
            //
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    OrderHistoryResponse order = new OrderHistoryResponse();
                    order.setSubOrderId(rs.getInt("sub_order_id"));
                    order.setShopName(rs.getString("shop_name"));
                    order.setStatus(SubOrderStatus.valueOf(rs.getString("status")));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));
                    order.setMasterOrderId(rs.getInt("master_order_id"));
                    orderResponse.add(order);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return orderResponse;
    }

    /**
     * Đếm số lượng đơn hàng của customer để phục vụ phân trang
     */
    private final String COUNT_SUBORDER_BY_CUSTOMERID = """
             SELECT COUNT(*) FROM sub_orders so
            JOIN shops s ON s.shop_id = so.shop_id
            JOIN master_orders mo ON mo.master_order_id = so.master_order_id
            WHERE mo.customer_id = ?
            """;
    public int countSubOrderByCustomerId(int customer_id, OrderHistoryFilterRequest orderRequest) {
        String sql = COUNT_SUBORDER_BY_CUSTOMERID;

        if(orderRequest.getFromDate() != null && orderRequest.getToDate() != null && !orderRequest.getFromDate().isEmpty()  && !orderRequest.getToDate().isEmpty()) {
            sql += " AND mo.created_at BETWEEN CAST(? AS DATETIME) + ' 00:00:00' AND CAST(? AS DATETIME) + ' 23:59:59' ";
        }
        // lọc theo trạng thái đơn hàng
        if(orderRequest.getStatus() != null && !orderRequest.getStatus().isEmpty()) {
            sql += " AND so.status = ?";
        }
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            stmt.setInt(index++, customer_id);
            if(orderRequest.getFromDate() != null && orderRequest.getToDate() != null && !orderRequest.getFromDate().isEmpty()  && !orderRequest.getToDate().isEmpty()) {
                stmt.setString(index++, orderRequest.getFromDate());
                stmt.setString(index++, orderRequest.getToDate());
            }
            if(orderRequest.getStatus() != null && !orderRequest.getStatus().isEmpty()) {
                stmt.setString(index++, orderRequest.getStatus());
            }

            //
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * HoaNK - Update status của suborder khi khách nhận được hàng chuyển sang delivered
     */
    private final String UPDATE_STATUS_ORDER = """
            UPDATE sub_orders SET status = 'DELIVERED' WHERE sub_order_id = ?;
            """;
    public boolean updateStatusOrder(int subOrderId) {
        String sql = UPDATE_STATUS_ORDER;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subOrderId);
            stmt.executeUpdate();
            return true;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HoaNK - Update Payment status khi khách nhận được hàng chuyển sang PAID nếu là COD
     */
    private final String UPDATE_PAYMENT_METHOD = """
            UPDATE master_orders SET payment_status = 'PAID' WHERE master_order_id = ?;
            """;
    public boolean updatePaymentMethod(int masterOrderId) {
        String sql = UPDATE_PAYMENT_METHOD;
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, masterOrderId);
            stmt.executeUpdate();
            return true;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HoaNK - Trả về 1 đối tượng orderItemResponse chứa bên trong 1 list shopOrderResponse (chứa bên trong 1 list orderItemDetailResponse)
     */
    private final String GET_ORDER_ITEMS = """
            SELECT so.sub_order_id,p.product_id,p.product_name,p.thumbnail_url,s.shop_id,s.shop_name,c.color_name, si.size_name,
            oi.quantity,oi.order_item_id, p.discount_percentage,p.base_price,mo.created_at, so.status, mo.receiver_name, mo.receiver_phone,
            mo.shipping_address, mo.payment_method, mo.payment_status, ISNULL(pr.review_id,0) AS reviewed
            FROM sub_orders so
            JOIN master_orders mo ON so.master_order_id = mo.master_order_id
            JOIN order_items oi ON oi.sub_order_id = so.sub_order_id
            JOIN shops s ON s.shop_id = so.shop_id
            JOIN products p ON oi.product_id = p.product_id
            JOIN product_variants pv ON pv.variant_id = oi.variant_id
            JOIN colors c ON c.color_id = pv.color_id
            JOIN sizes si ON si.size_id = pv.size_id
            LEFT JOIN product_reviews pr ON pr.order_item_id = oi.order_item_id
            WHERE so.sub_order_id = ?
            """;
    public OrderItemResponse getSubOrderDetail(Integer subOrderId) {
        String sql = GET_ORDER_ITEMS;

        //
        OrderItemResponse orderItemResponse = null;
        Map<Integer,ShopOrderResponse> mapShopOrder = new HashMap<>();
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subOrderId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    if(orderItemResponse == null) {
                        orderItemResponse = new OrderItemResponse();
                        orderItemResponse.setStatusOrder(SubOrderStatus.valueOf(rs.getString("status")));
                        orderItemResponse.setSubOrderId(rs.getInt("sub_order_id"));
                        orderItemResponse.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status")));
                        orderItemResponse.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));
                        orderItemResponse.setReceiverName(rs.getString("receiver_name"));
                        orderItemResponse.setReceiverPhone(rs.getString("receiver_phone"));
                        orderItemResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        orderItemResponse.setShippingAddress(rs.getString("shipping_address"));
                    }
                    //
                    int shopId = rs.getInt("shop_id");
                    ShopOrderResponse shopOrderResponse = null;
                    if(mapShopOrder.containsKey(shopId)) {
                        shopOrderResponse = mapShopOrder.get(shopId);
                    }else{
                        shopOrderResponse = new ShopOrderResponse();
                        shopOrderResponse.setShopId(rs.getInt("shop_id"));
                        shopOrderResponse.setShopName(rs.getString("shop_name"));
                        mapShopOrder.put(shopId,shopOrderResponse);
                    }
                    //
                    OrderItemDetailResponse orderItemDetailResponse = new OrderItemDetailResponse();
                    orderItemDetailResponse.setOrderItemId(rs.getInt("order_item_id"));
                    orderItemDetailResponse.setSubOrderId(rs.getInt("sub_order_id"));
                    orderItemDetailResponse.setThumbnail(rs.getString("thumbnail_url"));
                    orderItemDetailResponse.setProductId(rs.getInt("product_id"));
                    orderItemDetailResponse.setProductName(rs.getString("product_name"));
                    orderItemDetailResponse.setShopId(rs.getInt("shop_id"));
                    orderItemDetailResponse.setColorName(rs.getString("color_name"));
                    orderItemDetailResponse.setSizeName(rs.getString("size_name"));
                    orderItemDetailResponse.setQuantity(rs.getInt("quantity"));
                    orderItemDetailResponse.setReviewed((rs.getInt("reviewed") > 0 ? true : false));

                    Product product = new Product();
                    product.setDiscountPercentage(rs.getInt("discount_percentage"));
                    product.setBasePrice(rs.getBigDecimal("base_price"));
                    orderItemDetailResponse.setDiscountPrice(product.getDiscountedPrice());
                    //
                    shopOrderResponse.getItems().add(orderItemDetailResponse);
                }
                if(orderItemResponse != null) {
                    List<ShopOrderResponse> listShopOrder = new ArrayList<>(mapShopOrder.values());
                    orderItemResponse.setShopOrders(listShopOrder);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return orderItemResponse;
    }
    /*
     * Đếm tổng số đơn hàng con (sub_order) của một customer.
     *
     * Vì hệ thống MODA là multi-vendor:
     * - 1 master_order là đơn tổng của khách hàng.
     * - 1 master_order có thể tách thành nhiều sub_orders theo từng shop.
     *
     * Dashboard customer đang hiển thị đơn theo sub_order,
     * nên ở đây đếm sub_orders chứ không đếm master_orders.
     */
    public int countAllSubOrdersByCustomerId(int customerId) {
        String sql = "SELECT COUNT(*) AS total "
                + "FROM sub_orders so "
                + "JOIN master_orders mo ON mo.master_order_id = so.master_order_id "
                + "WHERE mo.customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            /*
             * customerId lấy từ session userId của customer đang đăng nhập.
             * Không lấy từ request parameter để tránh user tự sửa id trên URL.
             */
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Nếu có lỗi DB hoặc không lấy được dữ liệu,
         * trả về 0 để dashboard vẫn hiển thị được thay vì bị chết trang.
         */
        return 0;
    }
    /*
     * Đếm số đơn hàng đang giao của customer.
     *
     * Trạng thái đơn hàng được lưu trong bảng sub_orders.status.
     * Với dashboard, "Đang giao" tương ứng status = 'SHIPPING'.
     */
    public int countShippingSubOrdersByCustomerId(int customerId) {
        String sql = "SELECT COUNT(*) AS total "
                + "FROM sub_orders so "
                + "JOIN master_orders mo ON mo.master_order_id = so.master_order_id "
                + "WHERE mo.customer_id = ? "
                + "AND so.status = 'SHIPPING'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    /*
     * Lấy danh sách đơn hàng gần nhất của customer để hiển thị trên dashboard.
     *
     * Dashboard chỉ cần xem nhanh nên không lấy toàn bộ đơn hàng.
     * Số lượng đơn được truyền qua biến limit, ví dụ limit = 5.
     *
     * Dữ liệu lấy từ:
     * - sub_orders: mã đơn con, trạng thái, tổng tiền, ngày tạo
     * - master_orders: xác định đơn thuộc customer nào
     * - shops: lấy tên cửa hàng
     *
     * Trả về List<OrderHistoryResponse> để tận dụng DTO có sẵn của màn order-list.
     */
    public List<OrderHistoryResponse> getRecentSubOrdersByCustomerId(int customerId, int limit) {
        List<OrderHistoryResponse> orders = new ArrayList<>();

        /*
         * Chặn limit quá nhỏ hoặc quá lớn.
         * Tránh việc truyền limit bất thường làm query lấy quá nhiều dữ liệu.
         */
        int safeLimit = Math.max(1, Math.min(limit, 20));

        /*
         * SQL Server không cho bind parameter trực tiếp cho TOP bằng dấu ?
         * nên safeLimit được nối vào SQL.
         * safeLimit đã được giới hạn từ 1 đến 20 nên an toàn.
         */
        String sql = "SELECT TOP " + safeLimit + " "
                + "so.sub_order_id, "
                + "so.created_at, "
                + "so.status, "
                + "so.total_amount, "
                + "s.shop_name, "
                + "mo.payment_method, "
                + "mo.master_order_id "
                + "FROM sub_orders so "
                + "JOIN shops s ON s.shop_id = so.shop_id "
                + "JOIN master_orders mo ON mo.master_order_id = so.master_order_id "
                + "WHERE mo.customer_id = ? "
                + "ORDER BY so.created_at DESC, so.sub_order_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderHistoryResponse order = new OrderHistoryResponse();

                    /*
                     * Map dữ liệu từ ResultSet sang DTO.
                     * DTO này sẽ được Servlet truyền sang JSP để hiển thị.
                     */
                    order.setSubOrderId(rs.getInt("sub_order_id"));
                    order.setShopName(rs.getString("shop_name"));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    /*
                     * status trong DB là VARCHAR, ví dụ: SHIPPING.
                     * Trong Java chuyển về enum SubOrderStatus để JSP có thể gọi displayName.
                     */
                    order.setStatus(SubOrderStatus.valueOf(rs.getString("status")));

                    order.setTotalAmount(rs.getBigDecimal("total_amount"));

                    /*
                     * payment_method trong DB là COD hoặc BANK.
                     * Chuyển về enum PaymentMethod để đồng bộ với các màn order khác.
                     */
                    order.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));

                    order.setMasterOrderId(rs.getInt("master_order_id"));

                    orders.add(order);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Nếu không có đơn hoặc lỗi query,
         * trả về list rỗng để JSP hiển thị "Bạn chưa có đơn hàng nào."
         */
        return orders;
    }


    /**
     * HoaNK - Tạo 1 đơn hàng trong 1 transaction
     */
    public int createOrderTransaction(CheckoutRequest req, int userId) {
        int masterOrderId = 0;
        try {
            connection.setAutoCommit(false); // BẮT ĐẦU TRANSACTION
            // 1. Lấy tỷ lệ hoa hồng hiện hành từ bảng commission_configs
            BigDecimal commissionRate = this.getLatestCommissionRate();

            // 2. Tạo đơn hàng tổng (master_order)
            masterOrderId = this.insertMasterOrder(userId, req);

            // 3. Phân luồng xử lý chi tiết theo loại checkout
            if ("CART".equalsIgnoreCase(req.getType())) {
                // Luồng từ giỏ hàng — lấy danh sách item đã tick chọn
                List<CartResponse> listCartItems = cartDAO.getCartItemCheckbox(req.getCartItemIds(), userId);
                List<ShopCartResponse> shopsData = this.groupCartByShop(listCartItems);

                for (ShopCartResponse shop : shopsData) {
                    // Tính tổng tiền của shop hiện tại
                    BigDecimal subTotal = BigDecimal.ZERO;
                    for (CartResponse item : shop.getItems()) {
                        // discountPrice đã là BigDecimal, getQuantity() là int cần valueOf()
                        BigDecimal itemPrice    = item.getDiscountPrice();
                        BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
                        subTotal = subTotal.add(itemPrice.multiply(itemQuantity));
                    }

                    // Tính phí hoa hồng theo tỷ lệ lấy từ DB
                    BigDecimal commissionFee = subTotal.multiply(commissionRate);

                    // Tạo sub_order cho từng shop
                    int subOrderId = this.insertSubOrder(masterOrderId, shop.getShopId(), subTotal, commissionFee);

                    // Tạo order_item và trừ tồn kho cho từng sản phẩm
                    for (CartResponse item : shop.getItems()) {
                        // discountPrice đã là BigDecimal, truyền thẳng không cần valueOf()
                        this.insertOrderItem(subOrderId, item.getProductId(), item.getVariantId(), item.getQuantity(), item.getDiscountPrice());
                        this.updateDecreaseStock(item.getVariantId(), item.getQuantity());
                    }
                }
                // Xóa các item đã checkout khỏi giỏ hàng
                this.deleteCartItemsByListId(req.getCartItemIds());

            } else if ("DETAILS_PRODUCT".equalsIgnoreCase(req.getType())) {
                // Luồng từ trang chi tiết sản phẩm — mua ngay 1 sản phẩm
                SummaryOrderCheckoutResponse summary = this.getVariantInfoForCheckout(req.getVariantId());

                if (summary != null) {
                    summary.setQuantity(req.getQuantity());
                    BigDecimal subTotal = summary.getTotalPrice();

                    // Tính phí hoa hồng theo tỷ lệ lấy từ DB
                    BigDecimal commissionFee = subTotal.multiply(commissionRate);

                    int subOrderId = this.insertSubOrder(masterOrderId, summary.getShopId(), subTotal, commissionFee);
                    this.insertOrderItem(subOrderId, summary.getProductId(), req.getVariantId(), req.getQuantity(), summary.getPrice());
                    this.updateDecreaseStock(req.getVariantId(), req.getQuantity());
                    // Xóa item khỏi giỏ hàng nếu sản phẩm đã có trong giỏ
                    this.removeCartItemIfExist(userId, req.getVariantId());
                }
            }

            connection.commit(); // COMMIT TRANSACTION
            return masterOrderId;

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return 0;
        }
    }

    /**
     * HoaNK - Insert 1 đơn hàng tổng
     */
    private final String INSERT_MASTER_ORDER = """
    INSERT INTO master_orders (customer_id, total_amount, receiver_name, receiver_phone, shipping_address, payment_method, payment_status, created_at) 
    VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE());
    """;
    public int insertMasterOrder(int customerId, CheckoutRequest req) throws SQLException {
        String sql = INSERT_MASTER_ORDER;
        String paymentStatus = "BANK".equalsIgnoreCase(req.getPaymentMethod()) ? "PAID" : "PENDING";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setBigDecimal(2, req.getTotalAmount());
            stmt.setString(3, req.getReceiverName());
            stmt.setString(4, req.getReceiverPhone());
            stmt.setString(5, req.getShippingAddress());
            stmt.setString(6, req.getPaymentMethod());
            stmt.setString(7, paymentStatus);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Thêm đơn hàng tổng thất bại!");
    }

    /**
     * HoaNK - Insert 1 dơn hàng con(của 1 shop)
     */
    private final String INSERT_SUB_ORDER = """
        INSERT INTO sub_orders (master_order_id, shop_id, sub_total, discount_amount, total_amount, commission_fee, status, created_at) 
        VALUES (?, ?, ?, 0, ?, ?, 'PENDING', GETDATE());
        """;
    public int insertSubOrder(int masterOrderId, int shopId, BigDecimal subTotal, BigDecimal commissionFee) throws SQLException {
        String sql = INSERT_SUB_ORDER;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, masterOrderId);
            stmt.setInt(2, shopId);
            stmt.setBigDecimal(3, subTotal);
            stmt.setBigDecimal(4, subTotal);
            stmt.setBigDecimal(5, commissionFee);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Thêm đơn hàng theo Shop thất bại!");
    }

    /**
     * HoaNK - Insert từng sản phẩm của 1 suborder(1 sản phẩm 1 shop)
     */
    private final String INSERT_ORDER_ITEM = """
        INSERT INTO order_items (sub_order_id, product_id, variant_id, quantity, price_at_purchase) 
        VALUES (?, ?, ?, ?, ?);
        """;
    public void insertOrderItem(int subOrderId, int productId, int variantId, int quantity, BigDecimal priceAtPurchase) throws SQLException {
        String sql = INSERT_ORDER_ITEM;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subOrderId);
            stmt.setInt(2, productId);
            stmt.setInt(3, variantId);
            stmt.setInt(4, quantity);
            stmt.setBigDecimal(5, priceAtPurchase);
            stmt.executeUpdate();
        }
    }

    /**
     * HoaNK - Sau khi mua n ố lượng biến thể th trừ trong kho đi
     */
    private final String UPDATE_STOCK_VARIANT = """
        UPDATE product_variants SET stock_quantity = stock_quantity - ? WHERE variant_id = ?;
        """;
    public void updateDecreaseStock(int variantId, int quantity) throws SQLException {
        String sql = UPDATE_STOCK_VARIANT;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, variantId);
            stmt.executeUpdate();
        }
    }

    /**
     * HoaNK - Dọn dẹp giỏ hàng khi bieens thể đó đã dcd mua ở trang giỏ hàng tích
     */
    public void deleteCartItemsByListId(String cartItemIds) throws SQLException {
        if (cartItemIds == null || !cartItemIds.matches("^[0-9,]+$")) return;
        String sql = "DELETE FROM cart_items WHERE cart_item_id IN (" + cartItemIds + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * HoaNK - Dọn dẹp giỏ hàng khi biến thể đó đã đc mua ở trang chi tiết
     */
    private final String DELETE_CART_ITEM_BUY_NOW = """
        DELETE FROM cart_items WHERE user_id = ? AND variant_id = ?;
        """;
    public void removeCartItemIfExist(int userId, int variantId) throws SQLException {
        String sql = DELETE_CART_ITEM_BUY_NOW;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, variantId);
            stmt.executeUpdate();
        }
    }

    /**
     * HoaNK - Lấy ra thông tin chi tiết của biến th sản phẩm khi nhấn mua ngay
     */
    private final String GET_VARIANT_INFO_BUY_NOW = """
        SELECT p.product_id, p.shop_id, (p.base_price * (100 - p.discount_percentage) / 100) AS discount_price 
        FROM product_variants v 
        JOIN products p ON v.product_id = p.product_id 
        WHERE v.variant_id = ?;
        """;
    public SummaryOrderCheckoutResponse getVariantInfoForCheckout(int variantId) throws SQLException {
        String sql = GET_VARIANT_INFO_BUY_NOW;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SummaryOrderCheckoutResponse summary = new SummaryOrderCheckoutResponse();
                    summary.setProductId(rs.getInt("product_id"));
                    summary.setShopId(rs.getInt("shop_id"));
                    summary.setPrice(rs.getBigDecimal("discount_price"));
                    return summary;
                }
            }
        }
        return null;
    }

    /**
     * HoaNK - Gom nhóm lại những sản phẩm đc tích chia về shop của sản phẩm đó
     */
    public List<ShopCartResponse> groupCartByShop(List<CartResponse> cartResponses) {
        Map<Integer, ShopCartResponse> map = new LinkedHashMap<>();
        for (CartResponse c : cartResponses) {
            int shopId = c.getShopId();
            if (!map.containsKey(shopId)) {
                ShopCartResponse src = new ShopCartResponse();
                src.setShopId(shopId);
                src.setShopName(c.getShopName());
                map.put(shopId, src);
            }
            map.get(shopId).getItems().add(c);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * HoaNK - Hàm tổng xử lý Hủy đơn hàng con và hoàn kho trong 1 Transaction
     */
    public boolean cancelSubOrderTransaction(int subOrderId) {
        try{
            connection.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            //Cập nhật trạng thái sub_order thành CANCELLED
            updateSubOrderStatusToCancelled(subOrderId);

            //Lấy danh sách sản phẩm cần hoàn kho nhét vào Map
            Map<Integer, Integer> itemMap = this.getOrderItemsToReturn(subOrderId);

            //Tiến hành cộng trả lại kho hàng bằng addBatch
            if (!itemMap.isEmpty()) {
                this.executeReturnStockBatch(itemMap);
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    /**
     * HoaNK - Cập nhật trạng thái sub_order sau khi hủy
     */
    private final String UPDATE_SUB_ORDER_CANCLE = """
    UPDATE sub_orders SET status = 'CANCELLED' WHERE sub_order_id = ?;
    """;
    public void updateSubOrderStatusToCancelled(int subOrderId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SUB_ORDER_CANCLE)) {
            stmt.setInt(1, subOrderId);
            stmt.executeUpdate();
        }
    }

    /**
     * HoaNK - Lấy danh sách các biến thể được mua rồi nhét vào trong map để + lại số lượng khi hyur
     */
    private final String SELECT_ORDER_ITEMS_BY_SUB_ORDER = """
    SELECT variant_id, quantity FROM order_items WHERE sub_order_id = ?;
    """;
    public Map<Integer, Integer> getOrderItemsToReturn(int subOrderId) throws SQLException {
        Map<Integer, Integer> mapOrderItem = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ORDER_ITEMS_BY_SUB_ORDER)) {
            stmt.setInt(1, subOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int variantId = rs.getInt("variant_id");
                    int quantity = rs.getInt("quantity");
                    mapOrderItem.put(variantId, quantity);
                }
            }
        }
        return mapOrderItem;
    }

    /**
     * HoaNK - Gom nhóm các biến thể cần update sau khi hủy để + lại số lượng cho nó
     */
    private final String UPDATE_RETURN_STOCK_VARIANT = """
    UPDATE product_variants SET stock_quantity = stock_quantity + ? WHERE variant_id = ?;
    """;
    public void executeReturnStockBatch(Map<Integer, Integer> mapOrderItem) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_RETURN_STOCK_VARIANT)) {
            for (Map.Entry<Integer, Integer> entry : mapOrderItem.entrySet()) {
                stmt.setInt(1, entry.getValue());
                stmt.setInt(2, entry.getKey());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
