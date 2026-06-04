package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;

import java.sql.*;
import java.time.LocalDateTime;

public class EmailVerificationDAO extends DBContext {

    public void createOtp(String email, String otpCode, LocalDateTime expiredAt) {
        invalidateOldOtp(email);

        String sql = "INSERT INTO email_verifications (email, otp_code, expired_at) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, otpCode);
            ps.setTimestamp(3, Timestamp.valueOf(expiredAt));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOtp(String email, String otpCode) {
        String sql = "SELECT TOP 1 verification_id "
                + "FROM email_verifications "
                + "WHERE email = ? "
                + "AND otp_code = ? "
                + "AND is_verified = 0 "
                + "AND expired_at > GETDATE() "
                + "ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, otpCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int verificationId = rs.getInt("verification_id");
                    markOtpVerified(verificationId);
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void invalidateOldOtp(String email) {
        String sql = "UPDATE email_verifications SET is_verified = 1 WHERE email = ? AND is_verified = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markOtpVerified(int verificationId) {
        String sql = "UPDATE email_verifications SET is_verified = 1 WHERE verification_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, verificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}