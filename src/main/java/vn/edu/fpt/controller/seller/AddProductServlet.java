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
        request.setAttribute("colors", productDAO.getAllColors());
        request.setAttribute("sizes", productDAO.getAllSizes());
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

        java.util.Map<String, String> errors = new java.util.HashMap<>();
        java.util.Map<String, String> oldInput = new java.util.HashMap<>();

        String productName = request.getParameter("productName");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String genderStr = request.getParameter("gender");

        oldInput.put("productName", productName);
        oldInput.put("description", description);
        oldInput.put("categoryId", categoryIdStr);
        oldInput.put("gender", genderStr);

        if (productName == null || productName.trim().isEmpty()) {
            errors.put("productName", "Tên sản phẩm không được để trống.");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Mô tả sản phẩm không được để trống.");
        }
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            errors.put("categoryId", "Vui lòng chọn danh mục sản phẩm.");
        }
        if (genderStr == null || genderStr.trim().isEmpty()) {
            errors.put("gender", "Vui lòng chọn giới tính.");
        } else {
            try {
                Gender.valueOf(genderStr);
            } catch (Exception e) {
                errors.put("gender", "Giới tính không hợp lệ.");
            }
        }

        // 2. Xử lý các file ảnh tải lên Cloudinary
        List<String> imageUrls = new ArrayList<>();
        try {
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
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imageUrls.isEmpty()) {
            errors.put("images", "Vui lòng tải lên ít nhất ảnh chính của sản phẩm.");
        }

        // 3. Đọc dữ liệu biến thể
        String[] variantColors = request.getParameterValues("variantColor");
        String[] variantSizes = request.getParameterValues("variantSize");
        String[] variantPricesRaw = request.getParameterValues("variantPriceRaw");
        String[] variantStocks = request.getParameterValues("variantStock");

        if (variantColors == null || variantColors.length == 0 ||
            variantSizes == null || variantSizes.length == 0 ||
            variantPricesRaw == null || variantPricesRaw.length == 0 ||
            variantStocks == null || variantStocks.length == 0) {
            errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể.");
        } else {
            for (int i = 0; i < variantColors.length; i++) {
                if (variantColors[i] == null || variantColors[i].trim().isEmpty()) {
                    errors.put("variants", "Vui lòng chọn màu sắc cho tất cả biến thể.");
                    break;
                }
                if (i >= variantSizes.length || variantSizes[i] == null || variantSizes[i].trim().isEmpty()) {
                    errors.put("variants", "Vui lòng chọn kích thước cho tất cả biến thể.");
                    break;
                }
                if (i >= variantPricesRaw.length || variantPricesRaw[i] == null || variantPricesRaw[i].trim().isEmpty()) {
                    errors.put("variants", "Giá bán biến thể không được để trống.");
                    break;
                }
                if (i >= variantStocks.length || variantStocks[i] == null || variantStocks[i].trim().isEmpty()) {
                    errors.put("variants", "Số lượng biến thể không được để trống.");
                    break;
                }
                try {
                    new BigDecimal(variantPricesRaw[i]);
                } catch (Exception e) {
                    errors.put("variants", "Giá bán biến thể không hợp lệ.");
                    break;
                }
                try {
                    int stock = Integer.parseInt(variantStocks[i]);
                    if (stock < 0) {
                        errors.put("variants", "Số lượng biến thể không được âm.");
                        break;
                    }
                } catch (Exception e) {
                    errors.put("variants", "Số lượng biến thể không hợp lệ.");
                    break;
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInput);
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);

            // Tính giá bán cơ sở (basePrice) là giá trị của biến thể đầu tiên
            BigDecimal basePrice = new BigDecimal(variantPricesRaw[0]);

            // 4. Khởi tạo và lưu sản phẩm
            Product product = Product.builder()
                    .shopId(shop.getShopId())
                    .categoryId(categoryId)
                    .gender(Gender.valueOf(genderStr))
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
                BigDecimal price = new BigDecimal(variantPricesRaw[i]);
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
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
        }
    }
}
