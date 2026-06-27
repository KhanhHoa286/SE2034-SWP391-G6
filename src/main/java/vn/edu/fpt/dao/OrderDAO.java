package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.OrderHistoryFilterRequest;
import vn.edu.fpt.dto.response.*;
import vn.edu.fpt.enums.PaymentMethod;
import vn.edu.fpt.enums.PaymentStatus;
import vn.edu.fpt.enums.SubOrderStatus;
import vn.edu.fpt.model.Product;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO extends DBContext {

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
            ORDER BY so.sub_order_id ASC 
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
            oi.quantity, p.discount_percentage,p.base_price,mo.created_at, so.status, mo.receiver_name, mo.receiver_phone,
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
}
