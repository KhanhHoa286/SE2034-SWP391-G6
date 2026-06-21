package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.dto.request.OrderHistoryFilterRequest;
import vn.edu.fpt.dto.response.OrderHistoryFilterResponse;
import vn.edu.fpt.dto.response.OrderHistoryResponse;
import vn.edu.fpt.enums.SubOrderStatus;

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
            select so.sub_order_id, so.created_at, so.status,so.total_amount,s.shop_name from sub_orders so
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
            sql += " AND mo.created_at BETWEEN CAST(? AS DATETIME) + ' 00:00:00' AND CAST(? AS DATETIME) + ' 23:59:59' ";
        }
        // lọc theo trạng thái đơn hàng
        if(orderRequest.getStatus() != null && !orderRequest.getStatus().isEmpty()) {
            sql += " AND so.status = ?";
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

}
