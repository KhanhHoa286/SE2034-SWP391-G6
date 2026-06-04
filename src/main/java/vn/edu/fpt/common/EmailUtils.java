package vn.edu.fpt.common;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtils {

    private static final Properties prop = new Properties();

    static {
        try (InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                prop.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        final String senderEmail = valueOrEnv(prop.getProperty("email.sender"), "MODA_EMAIL_SENDER");
        final String senderPassword = valueOrEnv(prop.getProperty("email.password"), "MODA_EMAIL_PASSWORD");
        if (senderEmail == null || senderEmail.isBlank() || senderPassword == null || senderPassword.isBlank()) {
            throw new MessagingException("Thieu cau hinh Gmail. Them email.sender/email.password trong config.properties hoac bien moi truong MODA_EMAIL_SENDER/MODA_EMAIL_PASSWORD.");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message email = new MimeMessage(session);
        email.setFrom(new InternetAddress(senderEmail));
        email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        email.setSubject(subject);
        email.setContent(content, "text/html; charset=UTF-8");

        try {
            Transport.send(email);
        } catch (AuthenticationFailedException ex) {
            throw new MessagingException("Gmail tu choi dang nhap SMTP. Hay bat 2-Step Verification va dung Gmail App Password 16 ky tu thay cho mat khau dang nhap Gmail.", ex);
        } catch (NoSuchProviderException ex) {
            throw new MessagingException("Thieu thu vien gui mail runtime. Kiem tra dependency org.eclipse.angus:angus-mail da duoc deploy vao WEB-INF/lib.", ex);
        } catch (MessagingException ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = "Loi SMTP khong ro nguyen nhan.";
            }
            throw new MessagingException(message, ex);
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("StreamProvider")) {
                throw new MessagingException("Thieu Jakarta Mail implementation. Kiem tra org.eclipse.angus:angus-mail trong Maven va redeploy Tomcat artifact.", ex);
            }
            throw ex;
        }
    }

    private static String valueOrEnv(String value, String envName) {
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return System.getenv(envName);
    }
}
