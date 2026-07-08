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
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 5, // 5MB
        maxRequestSize = 1024 * 1024 * 25 // 25MB
)
public class AddProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
        }

        request.setAttribute("shop", shop);
        request.setAttribute("categories", categoryDAO.getAllCategory());
        request.setAttribute("colors", productDAO.getAllColors());
        request.setAttribute("sizes", productDAO.getAllSizes());
        request.setAttribute("discounts", productDAO.getDiscountPercentages());
        request.setAttribute("activePage", "products");

        request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

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
        String basePriceRaw = request.getParameter("basePriceRaw");
        String discountPercentageStr = request.getParameter("discountPercentage");

        oldInput.put("productName", productName);
        oldInput.put("description", description);
        oldInput.put("categoryId", categoryIdStr);
        oldInput.put("gender", genderStr);
        oldInput.put("basePriceRaw", basePriceRaw);
        oldInput.put("discountPercentage", discountPercentageStr);

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
        if (basePriceRaw == null || basePriceRaw.trim().isEmpty()) {
            errors.put("basePrice", "Giá bán sản phẩm không được để trống.");
        } else {
            try {
                BigDecimal basePrice = new BigDecimal(basePriceRaw.trim());
                if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
                    errors.put("basePrice", "Giá bán sản phẩm không được âm.");
                }
            } catch (Exception e) {
                errors.put("basePrice", "Giá bán sản phẩm không hợp lệ.");
            }
        }
        int discountPercentage = 0;
        if (discountPercentageStr != null && !discountPercentageStr.trim().isEmpty()) {
            try {
                discountPercentage = Integer.parseInt(discountPercentageStr.trim());
                if (discountPercentage < 0 || discountPercentage > 100) {
                    errors.put("discountPercentage", "Phần trăm giảm giá phải từ 0 đến 100.");
                }
            } catch (Exception e) {
                errors.put("discountPercentage", "Phần trăm giảm giá không hợp lệ.");
            }
        }

        // 2. Xử lý các file ảnh tải lên Cloudinary
        List<String> imageUrls = new ArrayList<>();
        try {
            Part mainImagePart = request.getPart("mainProductImage");
            if (mainImagePart != null && mainImagePart.getSize() > 0) {
                try {
                    String mainImageUrl = UploadImage.uploadImage(mainImagePart, "products");
                    if (mainImageUrl != null && !mainImageUrl.isBlank()) {
                        imageUrls.add(mainImageUrl);
                    } else {
                        errors.put("images", "Không thể tải ảnh chính lên hệ thống. Vui lòng thử lại.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.put("images", "Không thể tải ảnh chính lên hệ thống: " + e.getMessage());
                }
            } else {
                errors.put("images", "Vui lòng tải lên ít nhất ảnh chính của sản phẩm.");
            }

            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if (part.getName().equals("productImages") && part.getSize() > 0) {
                    try {
                        String imageUrl = UploadImage.uploadImage(part, "products");
                        if (imageUrl != null && !imageUrl.isBlank()) {
                            imageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errors.putIfAbsent("images", "Có ảnh phụ không thể tải lên hệ thống: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.putIfAbsent("images", "Không thể đọc file ảnh từ form. Vui lòng chọn lại ảnh và thử lại.");
        }

        if (imageUrls.isEmpty() && !errors.containsKey("images")) {
            errors.put("images", "Vui lòng tải lên ít nhất ảnh chính của sản phẩm.");
        }

        // 3. Đọc dữ liệu biến thể
        String[] variantColors = request.getParameterValues("variantColor");
        String[] variantSizes = request.getParameterValues("variantSize");
        String[] variantStocks = request.getParameterValues("variantStock");

        if (variantColors == null || variantSizes == null || variantStocks == null) {
            errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể.");
        } else {
            // Sử dụng min length để tránh ArrayIndexOutOfBoundsException
            int variantCount = Math.min(variantColors.length, Math.min(variantSizes.length, variantStocks.length));
            if (variantCount == 0) {
                errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể.");
            } else {
                boolean hasValidVariant = false;
                for (int i = 0; i < variantCount; i++) {
                    // Bỏ qua biến thể chưa chọn (giá trị rỗng)
                    boolean colorEmpty = (variantColors[i] == null || variantColors[i].trim().isEmpty());
                    boolean sizeEmpty = (variantSizes[i] == null || variantSizes[i].trim().isEmpty());
                    boolean stockEmpty = (variantStocks[i] == null || variantStocks[i].trim().isEmpty());

                    if (colorEmpty && sizeEmpty && stockEmpty) {
                        continue; // biến thể hoàn toàn rỗng, bỏ qua
                    }

                    if (colorEmpty) {
                        errors.put("variants", "Vui lòng chọn màu sắc cho tất cả biến thể.");
                        break;
                    }
                    if (sizeEmpty) {
                        errors.put("variants", "Vui lòng chọn kích thước cho tất cả biến thể.");
                        break;
                    }
                    if (stockEmpty) {
                        errors.put("variants", "Số lượng biến thể không được để trống.");
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
                    hasValidVariant = true;
                }
                if (!hasValidVariant && !errors.containsKey("variants")) {
                    errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể hợp lệ.");
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
            request.setAttribute("discounts", productDAO.getDiscountPercentages());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            BigDecimal basePrice = new BigDecimal(basePriceRaw.trim());

            // 4. Khởi tạo và lưu sản phẩm
            Product product = Product.builder()
                    .shopId(shop.getShopId())
                    .categoryId(categoryId)
                    .gender(Gender.valueOf(genderStr))
                    .productName(productName.trim())
                    .description(description.trim())
                    .basePrice(basePrice)
                    .discountPercentage(discountPercentage)
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
            int variantCount = Math.min(variantColors.length, Math.min(variantSizes.length, variantStocks.length));
            for (int i = 0; i < variantCount; i++) {
                // Bỏ qua biến thể rỗng (chưa chọn)
                if (variantColors[i] == null || variantColors[i].trim().isEmpty())
                    continue;
                if (variantSizes[i] == null || variantSizes[i].trim().isEmpty())
                    continue;
                if (variantStocks[i] == null || variantStocks[i].trim().isEmpty())
                    continue;

                String colorName = variantColors[i].trim();
                String sizeName = variantSizes[i].trim();
                int stock = Integer.parseInt(variantStocks[i]);

                int colorId = productDAO.getOrCreateColorId(colorName);
                int sizeId = productDAO.getOrCreateSizeId(sizeName);

                ProductVariant variant = ProductVariant.builder()
                        .productId(productId)
                        .colorId(colorId)
                        .sizeId(sizeId)
                        .variantName(productName + " (" + colorName + " / " + sizeName + ")")
                        .price(basePrice)
                        .stockQuantity(stock)
                        .build();

                productDAO.insertProductVariant(variant);
            }

            // Thành công -> chuyển hướng về trang danh sách sản phẩm
            session.setAttribute("toastMessage", "Thêm sản phẩm thành công!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/list-seller-products");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("discounts", productDAO.getDiscountPercentages());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/add-product.jsp").forward(request, response);
        }
    }

    private Shop resolveCurrentShop(HttpSession session) {
        Integer ownerId = getLoggedInUserId(session);
        return ownerId == null ? null : shopDAO.getShopByOwnerId(ownerId);
    }

    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }
        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        Object rawAccount = session.getAttribute("account");
        if (rawAccount instanceof User) {
            return ((User) rawAccount).getUserId();
        }

        return null;
    }
}
