package vn.edu.fpt.common;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hàm băm mật khẩu trước khi lưu vào DB
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Hàm kiểm tra mật khẩu dùng khi đăng nhập ( so với khi băm )
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}