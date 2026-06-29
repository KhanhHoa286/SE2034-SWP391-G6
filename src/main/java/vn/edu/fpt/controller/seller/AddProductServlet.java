package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.common.UploadImage;
import vn.edu.fpt.dao.CategoryDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.enums.Gender;
import vn.edu.fpt.enums.ProductStatus;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductImage;
import vn.edu.fpt.model.ProductVariant;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/add-product")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 5,       // 5MB
        maxRequestSize = 1024 * 1024 * 25    // 25MB
)
public class AddProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        int ownerId = (account != null) ? account.getUserId() : -1;
        Shop shop = null;

        if (ownerId != -1) {
            shop = shopDAO.getShopByOwnerId(ownerId);
        }

        // Nếu chưa có shop -> lấy shop đầu tiên để demo
        if (shop == null) {
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }

        request.setAttribute("shop", shop);
        request.setAttribute("categories", categoryDAO.getAllCategory());
        request.setAttribute("activePage", "products");

        request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        int ownerId = (account != null) ? account.getUserId() : -1;
        Shop shop = null;

        if (ownerId != -1) {
            shop = shopDAO.getShopByOwnerId(ownerId);
        }

        // Demo fallback
        if (shop == null) {
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }

        if (shop == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không tìm thấy Shop hợp lệ để đăng sản phẩm!");
            return;
        }

        try {
            // 1. Đọc các tham số cơ bản
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String categoryIdStr = request.getParameter("categoryId");

            if (productName == null || productName.trim().isEmpty() ||
                    description == null || description.trim().isEmpty() ||
                    categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                throw new Exception("Vui lòng nhập đầy đủ các trường bắt buộc.");
            }

            Integer categoryId = Integer.parseInt(categoryIdStr);

            // 2. Xử lý các file ảnh tải lên Cloudinary
            List<String> imageUrls = new ArrayList<>();
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if (part.getName().equals("productImages") && part.getSize() > 0) {
                    try {
                        String imageUrl = UploadImage.uploadImage(part, "products");
                        if (imageUrl != null) {
                            imageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Bỏ qua ảnh lỗi hoặc ném ra exception tùy thiết kế
                    }
                }
            }

            // Nếu không có ảnh nào -> sử dụng ảnh mặc định
            if (imageUrls.isEmpty()) {
                imageUrls.add("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500");
            }

            // 3. Đọc dữ liệu biến thể
            String[] variantColors = request.getParameterValues("variantColor");
            String[] variantSizes = request.getParameterValues("variantSize");
            String[] variantPrices = request.getParameterValues("variantPrice");
            String[] variantStocks = request.getParameterValues("variantStock");

            if (variantColors == null || variantColors.length == 0) {
                throw new Exception("Sản phẩm phải chứa ít nhất 1 biến thể.");
            }

            // Tính giá bán cơ sở (basePrice) làm giá trị của biến thể đầu tiên
            BigDecimal basePrice = new BigDecimal(variantPrices[0]);

            // 4. Khởi tạo và lưu sản phẩm
            Product product = Product.builder()
                    .shopId(shop.getShopId())
                    .categoryId(categoryId)
                    .gender(Gender.UNISEX)
                    .productName(productName.trim())
                    .description(description.trim())
                    .basePrice(basePrice)
                    .discountPercentage(0)
                    .thumbnailUrl(imageUrls.get(0)) // Ảnh chính là ảnh đầu tiên
                    .isActive(true)
                    .isDeleted(false)
                    .status(ProductStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            int productId = productDAO.insertProduct(product);

            if (productId == -1) {
                throw new Exception("Lưu sản phẩm thất bại.");
            }

            // 5. Lưu danh sách ảnh vào bảng product_images
            for (int i = 0; i < imageUrls.size(); i++) {
                ProductImage productImage = ProductImage.builder()
                        .productId(productId)
                        .imageUrl(imageUrls.get(i))
                        .isPrimary(i == 0)
                        .build();
                productDAO.insertProductImage(productImage);
            }

            // 6. Lưu danh sách các biến thể sản phẩm
            for (int i = 0; i < variantColors.length; i++) {
                String colorName = variantColors[i].trim();
                String sizeName = variantSizes[i].trim();
                BigDecimal price = new BigDecimal(variantPrices[i]);
                int stock = Integer.parseInt(variantStocks[i]);

                int colorId = productDAO.getOrCreateColorId(colorName);
                int sizeId = productDAO.getOrCreateSizeId(sizeName);

                ProductVariant variant = ProductVariant.builder()
                        .productId(productId)
                        .colorId(colorId)
                        .sizeId(sizeId)
                        .variantName(productName + " (" + colorName + " / " + sizeName + ")")
                        .price(price)
                        .stockQuantity(stock)
                        .build();

                productDAO.insertProductVariant(variant);
            }

            // Thành công -> chuyển hướng về trang danh sách sản phẩm
            response.sendRedirect(request.getContextPath() + "/list-seller-products");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
        }
    }
}
