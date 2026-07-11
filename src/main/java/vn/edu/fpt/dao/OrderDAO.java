package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;

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

    public List<vn.edu.fpt.controller.admin.OrderHistoryDTO> getGlobalOrders(String search, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<vn.edu.fpt.controller.admin.OrderHistoryDTO> list = new ArrayList<>();
        
        String subQuery = 
            "SELECT mo.master_order_id, mo.created_at, mo.total_amount, " +
            "       mo.payment_method, mo.payment_status, " +
            "       u.first_name + ' ' + u.last_name AS customer_name, " +
            "       mo.customer_id, " +
            "       CASE " +
            "           WHEN SUM(CASE WHEN so.status = 'SHIPPING'   THEN 1 ELSE 0 END) > 0 THEN 'SHIPPING' " +
            "           WHEN SUM(CASE WHEN so.status = 'PREPARING'  THEN 1 ELSE 0 END) > 0 THEN 'PREPARING' " +
            "           WHEN SUM(CASE WHEN so.status = 'CONFIRMED'  THEN 1 ELSE 0 END) > 0 THEN 'CONFIRMED' " +
            "           WHEN SUM(CASE WHEN so.status = 'PENDING'    THEN 1 ELSE 0 END) > 0 THEN 'PENDING' " +
            "           WHEN SUM(CASE WHEN so.status = 'DELIVERED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'DELIVERED' " +
            "           WHEN SUM(CASE WHEN so.status = 'CANCELLED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'CANCELLED' " +
            "           ELSE 'PENDING' " +
            "       END AS agg_status " +
            "FROM master_orders mo " +
            "LEFT JOIN sub_orders so ON so.master_order_id = mo.master_order_id " +
            "LEFT JOIN users u ON mo.customer_id = u.user_id " +
            "GROUP BY mo.master_order_id, mo.created_at, mo.total_amount, mo.payment_method, mo.payment_status, mo.customer_id, u.first_name, u.last_name";

        StringBuilder sql = new StringBuilder("SELECT * FROM (" + subQuery + ") AS BaseQuery WHERE 1=1 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (customer_name LIKE ? OR ('#ORD-' + RIGHT('000' + CAST(master_order_id AS VARCHAR(3)), 3)) LIKE ?) ");
        }

        if (status != null && !status.equalsIgnoreCase("all") && !status.trim().isEmpty()) {
            if (status.equalsIgnoreCase("success")) {
                sql.append(" AND agg_status = 'DELIVERED' ");
            } else if (status.equalsIgnoreCase("delivering")) {
                sql.append(" AND agg_status = 'SHIPPING' ");
            } else if (status.equalsIgnoreCase("canceled")) {
                sql.append(" AND agg_status = 'CANCELLED' ");
            } else if (status.equalsIgnoreCase("pending")) {
                sql.append(" AND agg_status IN ('PENDING', 'CONFIRMED', 'PREPARING') ");
            }
        }

        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vn.edu.fpt.controller.admin.OrderHistoryDTO dto = new vn.edu.fpt.controller.admin.OrderHistoryDTO();
                dto.setMasterOrderId(rs.getInt("master_order_id"));
                dto.setOrderCode(String.format("#ORD-%03d", rs.getInt("master_order_id")));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                dto.setTotalAmount(rs.getDouble("total_amount"));
                dto.setStatus(rs.getString("agg_status"));
                dto.setPaymentMethod(rs.getString("payment_method"));
                dto.setPaymentStatus(rs.getString("payment_status"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setCustomerId(rs.getInt("customer_id"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalGlobalOrders(String search, String status) {
        int total = 0;
        String subQuery = 
            "SELECT mo.master_order_id, u.first_name + ' ' + u.last_name AS customer_name, " +
            "       CASE " +
            "           WHEN SUM(CASE WHEN so.status = 'SHIPPING'   THEN 1 ELSE 0 END) > 0 THEN 'SHIPPING' " +
            "           WHEN SUM(CASE WHEN so.status = 'PREPARING'  THEN 1 ELSE 0 END) > 0 THEN 'PREPARING' " +
            "           WHEN SUM(CASE WHEN so.status = 'CONFIRMED'  THEN 1 ELSE 0 END) > 0 THEN 'CONFIRMED' " +
            "           WHEN SUM(CASE WHEN so.status = 'PENDING'    THEN 1 ELSE 0 END) > 0 THEN 'PENDING' " +
            "           WHEN SUM(CASE WHEN so.status = 'DELIVERED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'DELIVERED' " +
            "           WHEN SUM(CASE WHEN so.status = 'CANCELLED'  THEN 1 ELSE 0 END) = COUNT(so.sub_order_id) THEN 'CANCELLED' " +
            "           ELSE 'PENDING' " +
            "       END AS agg_status " +
            "FROM master_orders mo " +
            "LEFT JOIN sub_orders so ON so.master_order_id = mo.master_order_id " +
            "LEFT JOIN users u ON mo.customer_id = u.user_id " +
            "GROUP BY mo.master_order_id, mo.customer_id, u.first_name, u.last_name";

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM (" + subQuery + ") AS BaseQuery WHERE 1=1 ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (customer_name LIKE ? OR ('#ORD-' + RIGHT('000' + CAST(master_order_id AS VARCHAR(3)), 3)) LIKE ?) ");
        }

        if (status != null && !status.equalsIgnoreCase("all") && !status.trim().isEmpty()) {
            if (status.equalsIgnoreCase("success")) {
                sql.append(" AND agg_status = 'DELIVERED' ");
            } else if (status.equalsIgnoreCase("delivering")) {
                sql.append(" AND agg_status = 'SHIPPING' ");
            } else if (status.equalsIgnoreCase("canceled")) {
                sql.append(" AND agg_status = 'CANCELLED' ");
            } else if (status.equalsIgnoreCase("pending")) {
                sql.append(" AND agg_status IN ('PENDING', 'CONFIRMED', 'PREPARING') ");
            }
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public vn.edu.fpt.controller.admin.GlobalOrderDetailDTO getGlobalOrderDetail(int masterOrderId) {
        vn.edu.fpt.controller.admin.GlobalOrderDetailDTO dto = new vn.edu.fpt.controller.admin.GlobalOrderDetailDTO();
        
        String sqlMaster = "SELECT mo.*, u.first_name, u.last_name, u.email, u.phone " +
                           "FROM master_orders mo " +
                           "LEFT JOIN users u ON mo.customer_id = u.user_id " +
                           "WHERE mo.master_order_id = ?";
                           
        String sqlSub = "SELECT so.*, sh.shop_name " +
                        "FROM sub_orders so " +
                        "LEFT JOIN shops sh ON so.shop_id = sh.shop_id " +
                        "WHERE so.master_order_id = ?";
                        

        try {
            // 1. Fetch MasterOrder
            PreparedStatement psMaster = connection.prepareStatement(sqlMaster);
            psMaster.setInt(1, masterOrderId);
            ResultSet rsMaster = psMaster.executeQuery();
            if (rsMaster.next()) {
                vn.edu.fpt.model.MasterOrder mo = new vn.edu.fpt.model.MasterOrder();
                mo.setMasterOrderId(rsMaster.getInt("master_order_id"));
                mo.setCustomerId(rsMaster.getInt("customer_id"));
                mo.setTotalAmount(rsMaster.getBigDecimal("total_amount"));
                mo.setShippingAddress(rsMaster.getString("shipping_address"));
                mo.setPaymentMethod(rsMaster.getString("payment_method"));
                String pStatusStr = rsMaster.getString("payment_status");
                if (pStatusStr != null) {
                    try { mo.setPaymentStatus(vn.edu.fpt.enums.PaymentStatus.valueOf(pStatusStr)); } catch (Exception ignored) {}
                }
                java.sql.Timestamp ca = rsMaster.getTimestamp("created_at");
                if (ca != null) mo.setCreatedAt(ca.toLocalDateTime());

                dto.setMasterOrder(mo);
                
                String fName = rsMaster.getString("first_name");
                String lName = rsMaster.getString("last_name");
                String name = "";
                if (lName != null && fName != null) name = lName + " " + fName;
                else if (fName != null) name = fName;
                else if (lName != null) name = lName;
                
                dto.setCustomerName(name);
                dto.setCustomerEmail(rsMaster.getString("email"));
                dto.setCustomerPhone(rsMaster.getString("phone"));
            } else {
                return null; // not found
            }

            // 2. Fetch SubOrders
            java.util.List<vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.SubOrderDetail> subOrders = new java.util.ArrayList<>();
            PreparedStatement psSub = connection.prepareStatement(sqlSub);
            psSub.setInt(1, masterOrderId);
            ResultSet rsSub = psSub.executeQuery();
            
            while (rsSub.next()) {
                vn.edu.fpt.model.SubOrder so = new vn.edu.fpt.model.SubOrder();
                so.setSubOrderId(rsSub.getInt("sub_order_id"));
                so.setShopId(rsSub.getInt("shop_id"));
                so.setTotalAmount(rsSub.getBigDecimal("total_amount"));
                String soStatusStr = rsSub.getString("status");
                if (soStatusStr != null) {
                    try { so.setStatus(vn.edu.fpt.enums.SubOrderStatus.valueOf(soStatusStr)); } catch (Exception ignored) {}
                }
                
                vn.edu.fpt.model.Shop shop = new vn.edu.fpt.model.Shop();
                shop.setShopId(so.getShopId());
                shop.setShopName(rsSub.getString("shop_name"));
                
                vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.SubOrderDetail subDetail = new vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.SubOrderDetail();
                subDetail.setSubOrder(so);
                subDetail.setShop(shop);
                subDetail.setItems(new java.util.ArrayList<>());
                subOrders.add(subDetail);
            }
            
            // 3. Fetch Items for each SubOrder
            for (vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.SubOrderDetail subDetail : subOrders) {
                String sqlItem = "SELECT oi.*, p.product_name, p.thumbnail_url, p.base_price " +
                                 "FROM order_items oi " +
                                 "LEFT JOIN products p ON oi.product_id = p.product_id " +
                                 "WHERE oi.sub_order_id = ?";
                PreparedStatement psItem = connection.prepareStatement(sqlItem);
                psItem.setInt(1, subDetail.getSubOrder().getSubOrderId());
                ResultSet rsItem = psItem.executeQuery();
                while (rsItem.next()) {
                    vn.edu.fpt.model.OrderItem oi = new vn.edu.fpt.model.OrderItem();
                    try { oi.setOrderItemId(rsItem.getInt("order_item_id")); } catch (Exception ignored) {}
                    try { oi.setProductId(rsItem.getInt("product_id")); } catch (Exception ignored) {}
                    try { oi.setQuantity(rsItem.getInt("quantity")); } catch (Exception ignored) {}
                    
                    try { 
                        oi.setPriceAtPurchase(rsItem.getBigDecimal("price_at_purchase")); 
                    } catch (Exception e1) {
                        try {
                            java.math.BigDecimal base = rsItem.getBigDecimal("base_price");
                            oi.setPriceAtPurchase(base != null ? base : java.math.BigDecimal.ZERO);
                        } catch (Exception e2) {
                            oi.setPriceAtPurchase(java.math.BigDecimal.ZERO);
                        }
                    }
                    
                    vn.edu.fpt.model.Product p = new vn.edu.fpt.model.Product();
                    p.setProductId(oi.getProductId());
                    try { p.setProductName(rsItem.getString("product_name")); } catch (Exception ignored) {}
                    try { p.setThumbnailUrl(rsItem.getString("thumbnail_url")); } catch (Exception ignored) {}
                    
                    vn.edu.fpt.model.ProductVariant pv = new vn.edu.fpt.model.ProductVariant();
                    pv.setVariantId(oi.getVariantId());
                    
                    vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.OrderItemDetail itemDetail = new vn.edu.fpt.controller.admin.GlobalOrderDetailDTO.OrderItemDetail();
                    itemDetail.setOrderItem(oi);
                    itemDetail.setProduct(p);
                    itemDetail.setVariant(pv);
                    try { itemDetail.setColorName(rsItem.getString("color_name")); } catch (Exception ignored) {}
                    try { itemDetail.setSizeName(rsItem.getString("size_name")); } catch (Exception ignored) {}
                    
                    if (oi.getPriceAtPurchase() != null) {
                        itemDetail.setSubTotal(oi.getPriceAtPurchase().multiply(java.math.BigDecimal.valueOf(oi.getQuantity())));
                    }
                    
                    subDetail.getItems().add(itemDetail);
                }
            }
            
            dto.setSubOrders(subOrders);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi SQL khi lấy chi tiết đơn hàng: " + e.getMessage(), e);
        }
        
        return dto;
    }
}
