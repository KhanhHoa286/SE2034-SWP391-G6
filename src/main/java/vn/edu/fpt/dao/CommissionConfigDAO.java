package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.CommissionConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommissionConfigDAO extends DBContext {

    public CommissionConfig getLatestConfig() {
        String sql = "SELECT TOP 1 * FROM commission_configs ORDER BY effective_date DESC, created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToConfig(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CommissionConfig> getAllConfigs() {
        List<CommissionConfig> list = new ArrayList<>();
        String sql = "SELECT * FROM commission_configs ORDER BY effective_date DESC, created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToConfig(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertConfig(java.math.BigDecimal commissionRate, java.time.LocalDateTime effectiveDate) {
        String sql = "INSERT INTO commission_configs (commission_rate, effective_date, created_at) VALUES (?, ?, GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, commissionRate);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(effectiveDate));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CommissionConfig mapRowToConfig(ResultSet rs) throws SQLException {
        CommissionConfig config = new CommissionConfig();
        config.setConfigId(rs.getInt("config_id"));
        config.setCommissionRate(rs.getBigDecimal("commission_rate"));
        
        Timestamp effectiveDate = rs.getTimestamp("effective_date");
        if (effectiveDate != null) {
            config.setEffectiveDate(effectiveDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            config.setCreatedAt(createdAt.toLocalDateTime());
        }
        return config;
    }
}
