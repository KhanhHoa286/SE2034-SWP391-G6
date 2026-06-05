package vn.edu.fpt.common;

import java.util.Random;

public class OtpUtils {
    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6 số
        return String.valueOf(otp);
    }
}