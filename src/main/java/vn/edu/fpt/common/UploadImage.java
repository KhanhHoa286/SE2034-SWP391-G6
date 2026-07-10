package vn.edu.fpt.common;

import com.cloudinary.Cloudinary;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.io.IOUtils;
import jakarta.servlet.http.Part;

import java.io.InputStream;
import java.util.*;

public class UploadImage {

    // Biến static lưu instance duy nhất
    private static Cloudinary cloudinary;
    
    // Khởi tạo 1 lần duy nhất khi class được load
    static {
        try {
            Properties prop = new Properties();
            InputStream input = UploadImage.class.getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {
                input = UploadImage.class.getClassLoader().getResourceAsStream("vn/edu/fpt/config.properties");
            }

            if (input != null) {
                prop.load(input);
                String cloudName = prop.getProperty("cloudinary.cloud_name");
                String apiKey = prop.getProperty("cloudinary.api_key");
                String apiSecret = prop.getProperty("cloudinary.api_secret");

                if (cloudName != null && !cloudName.trim().isEmpty()
                        && apiKey != null && !apiKey.trim().isEmpty()
                        && apiSecret != null && !apiSecret.trim().isEmpty()) {
                    Map<String, String> config = new HashMap<>();
                    config.put("cloud_name", cloudName.trim());
                    config.put("api_key", apiKey.trim());
                    config.put("api_secret", apiSecret.trim());
                    cloudinary = new Cloudinary(config);
                } else {
                    System.err.println("[UploadImage] Cloudinary configurations are missing in config.properties. Local fallback will be used.");
                }
            } else {
                System.err.println("[UploadImage] config.properties not found. Local fallback will be used.");
            }
        } catch (Exception e) {
            System.err.println("[UploadImage] Error configuring Cloudinary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Để private vì không cần ai ngoài class này gọi
    private static Cloudinary getInstance() {
        return cloudinary;
    }

    // gọi hàm upload ảnh lên cloudinary từ jakarta.servlet.http.Part
    public static String uploadImage(Part part, String folder) throws Exception {
        if (part == null || part.getSize() == 0) return null;

        // kiểm tra file khi upload chỉ đc up ảnh
        String contentType = part.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("Chỉ được upload file hình ảnh (jpg, png, jpeg, gif)!");
        }

        if (part.getSize() > 5 * 1024 * 1024) {
            throw new Exception("Dung lượng ảnh quá lớn (tối đa 5MB)!");
        }

        Cloudinary cloudinary = getInstance();
        if (cloudinary == null) {
            throw new Exception("Cloudinary chưa được cấu hình!");
        }

        Map options = new HashMap();
        options.put("folder", folder);
        options.put("use_filename", true);
        options.put("unique_filename", true);

        byte[] fileBytes;
        try (InputStream is = part.getInputStream()) {
            fileBytes = IOUtils.toByteArray(is);
        }

        // Upload mảng byte[] lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(fileBytes, options);
        return (String) uploadResult.get("secure_url");
    }

    // gọi hàm upload ảnh lên cloudinary từ FileItem
    public static String uploadImage(FileItem fileItem, String folder) throws Exception {
        if (fileItem == null || fileItem.getSize() == 0) return null;

        // kiểm tra file khi upload chỉ đc up ảnh
        String contentType = fileItem.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("Chỉ được upload file hình ảnh (jpg, png, jpeg, gif)!");
        }

        if (fileItem.getSize() > 5 * 1024 * 1024) {
            throw new Exception("Dung lượng ảnh quá lớn (tối đa 5MB)!");
        }

        Cloudinary cloudinary = getInstance();
        if (cloudinary == null) {
            throw new Exception("Cloudinary chưa được cấu hình!");
        }

        Map options = new HashMap();
        options.put("folder", folder);
        options.put("use_filename", true);
        options.put("unique_filename", true);

        byte[] fileBytes = IOUtils.toByteArray(fileItem.getInputStream());

        // Upload mảng byte[] lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(fileBytes, options);
        return (String) uploadResult.get("secure_url");
    }
}
