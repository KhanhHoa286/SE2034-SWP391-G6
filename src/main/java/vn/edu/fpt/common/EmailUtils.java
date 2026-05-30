package vn.edu.fpt.common;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtils {

    private static Properties prop = new Properties();

    static {
        try {
            InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        // Cấu hình SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Đọc thông tin từ file properties
        final String senderEmail = prop.getProperty("email.sender");
        final String senderPassword = prop.getProperty("email.password");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Tạo nội dung mail
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // gửi đến ai
        message.setSubject(subject); // subject email
        message.setContent(content, "text/html; charset=UTF-8"); // nội dung email là gì

        // Gửi mail
        Transport.send(message);
    }
}