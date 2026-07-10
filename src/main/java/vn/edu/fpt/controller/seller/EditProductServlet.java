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
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductImage;
import vn.edu.fpt.model.ProductVariant;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/edit-product")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 5,       // 5MB
        maxRequestSize = 1024 * 1024 * 25    // 25MB
)
public class EditProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);
        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr.trim());
            Product product = productDAO.getProductById(productId);
            if (product == null || (shop != null && !product.getShopId().equals(shop.getShopId()))) {
                response.sendRedirect(request.getContextPath() + "/list-seller-products");
                return;
            }

            List<ProductVariant> variants = productDAO.getVariantsByProductId(productId);
            List<ProductImage> images = productDAO.getProductImagesByProductId(productId);

            request.setAttribute("product", product);
            request.setAttribute("productVariants", variants);
            request.setAttribute("productImagesList", images);
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("discounts", productDAO.getDiscountPercentages());
            request.setAttribute("activePage", "products");

            request.getRequestDispatcher("/seller/product/edit-product.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);
        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        int productId = Integer.parseInt(productIdStr.trim());
        Product existingProduct = productDAO.getProductById(productId);
        if (existingProduct == null || (shop != null && !existingProduct.getShopId().equals(shop.getShopId()))) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
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

        // Xử lý ảnh tải lên
        List<String> imageUrls = new ArrayList<>();
        List<ProductImage> existingImages = productDAO.getProductImagesByProductId(productId);
        
        // Đọc các ảnh hiện tại giữ lại hoặc thay mới
        String keepImage0 = request.getParameter("keepImage0");
        String keepImage1 = request.getParameter("keepImage1");
        String keepImage2 = request.getParameter("keepImage2");
        String keepImage3 = request.getParameter("keepImage3");

        try {
            Collection<Part> parts = request.getParts();
            int partIndex = 0;
            // Map file input với index
            // imgInput0 -> index 0, imgInput1 -> index 1...
            for (int i = 0; i < 4; i++) {
                Part part = request.getPart("productImages" + i); // Hoặc lọc theo thứ tự
                if (part == null) {
                    // fallback check all parts
                    for (Part p : parts) {
                        if (p.getName().equals("productImages") && p.getSize() > 0) {
                            if (partIndex == i) {
                                part = p;
                                break;
                            }
                            partIndex++;
                        }
                    }
                }

                if (part != null && part.getSize() > 0) {
                    String imageUrl = UploadImage.uploadImage(part, "products");
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                } else {
                    // Không upload mới thì giữ ảnh cũ nếu có
                    String keepUrl = null;
                    if (i == 0) keepUrl = keepImage0;
                    else if (i == 1) keepUrl = keepImage1;
                    else if (i == 2) keepUrl = keepImage2;
                    else if (i == 3) keepUrl = keepImage3;

                    if (keepUrl != null && !keepUrl.trim().isEmpty()) {
                        imageUrls.add(keepUrl);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu hoàn toàn không có ảnh nào được giữ hoặc upload mới
        if (imageUrls.isEmpty()) {
            // Fallback giữ lại thumbnail hiện tại
            if (existingProduct.getThumbnailUrl() != null) {
                imageUrls.add(existingProduct.getThumbnailUrl());
            } else {
                errors.put("images", "Vui lòng tải lên ít nhất ảnh chính của sản phẩm.");
            }
        }

        // Đọc dữ liệu biến thể
        String[] variantColors = request.getParameterValues("variantColor");
        String[] variantSizes = request.getParameterValues("variantSize");
        String[] variantStocks = request.getParameterValues("variantStock");
        String[] variantIds = request.getParameterValues("variantId");

        if (variantColors == null || variantSizes == null || variantStocks == null) {
            errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể.");
        } else {
            int variantCount = Math.min(variantColors.length, Math.min(variantSizes.length, variantStocks.length));
            if (variantCount == 0) {
                errors.put("variants", "Sản phẩm phải chứa ít nhất 1 biến thể.");
            } else {
                boolean hasValidVariant = false;
                for (int i = 0; i < variantCount; i++) {
                    boolean colorEmpty = (variantColors[i] == null || variantColors[i].trim().isEmpty());
                    boolean sizeEmpty = (variantSizes[i] == null || variantSizes[i].trim().isEmpty());
                    boolean stockEmpty = (variantStocks[i] == null || variantStocks[i].trim().isEmpty());

                    if (colorEmpty && sizeEmpty && stockEmpty) continue;

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
            request.setAttribute("product", existingProduct);
            request.setAttribute("productVariants", productDAO.getVariantsByProductId(productId));
            request.setAttribute("productImagesList", existingImages);
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("discounts", productDAO.getDiscountPercentages());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/edit-product.jsp").forward(request, response);
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            BigDecimal basePrice = new BigDecimal(basePriceRaw.trim());

            // Cập nhật thông tin cơ bản sản phẩm
            existingProduct.setCategoryId(categoryId);
            existingProduct.setGender(Gender.valueOf(genderStr));
            existingProduct.setProductName(productName.trim());
            existingProduct.setDescription(description.trim());
            existingProduct.setBasePrice(basePrice);
            existingProduct.setDiscountPercentage(discountPercentage);
            existingProduct.setThumbnailUrl(imageUrls.get(0)); // ảnh chính đầu tiên

            productDAO.updateProduct(existingProduct);

            // Cập nhật bảng ảnh (xóa ảnh cũ và insert lại)
            productDAO.deleteImagesByProductId(productId);
            for (int i = 0; i < imageUrls.size(); i++) {
                ProductImage productImage = ProductImage.builder()
                        .productId(productId)
                        .imageUrl(imageUrls.get(i))
                        .isPrimary(i == 0)
                        .build();
                productDAO.insertProductImage(productImage);
            }

            // Cập nhật biến thể cũ theo variantId để tránh tạo tồn kho trùng.
            List<ProductVariant> existingVariants = productDAO.getVariantsByProductId(productId);
            Set<Integer> submittedVariantIds = new HashSet<>();
            int variantCount = Math.min(variantColors.length, Math.min(variantSizes.length, variantStocks.length));
            for (int i = 0; i < variantCount; i++) {
                if (variantColors[i] == null || variantColors[i].trim().isEmpty()) continue;
                if (variantSizes[i] == null || variantSizes[i].trim().isEmpty()) continue;
                if (variantStocks[i] == null || variantStocks[i].trim().isEmpty()) continue;

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

                Integer variantId = null;
                if (variantIds != null && i < variantIds.length && variantIds[i] != null && !variantIds[i].trim().isEmpty()) {
                    variantId = Integer.parseInt(variantIds[i].trim());
                }

                if (variantId != null) {
                    variant.setVariantId(variantId);
                    if (!productDAO.updateProductVariant(variant)) {
                        throw new IllegalStateException("Không thể cập nhật biến thể sản phẩm.");
                    }
                    submittedVariantIds.add(variantId);
                } else if (!productDAO.insertProductVariant(variant)) {
                    throw new IllegalStateException("Không thể thêm biến thể sản phẩm.");
                }
            }

            for (ProductVariant existingVariant : existingVariants) {
                Integer existingVariantId = existingVariant.getVariantId();
                if (existingVariantId != null && !submittedVariantIds.contains(existingVariantId)) {
                    if (!productDAO.deleteVariantById(existingVariantId, productId)
                            && !productDAO.retireVariantById(existingVariantId, productId)) {
                        throw new IllegalStateException("Không thể xóa biến thể sản phẩm.");
                    }
                }
            }
            session.setAttribute("toastMessage", "Cập nhật sản phẩm thành công!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/list-seller-products");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("product", existingProduct);
            request.setAttribute("productVariants", productDAO.getVariantsByProductId(productId));
            request.setAttribute("productImagesList", existingImages);
            request.setAttribute("shop", shop);
            request.setAttribute("categories", categoryDAO.getAllCategory());
            request.setAttribute("colors", productDAO.getAllColors());
            request.setAttribute("sizes", productDAO.getAllSizes());
            request.setAttribute("discounts", productDAO.getDiscountPercentages());
            request.setAttribute("activePage", "products");
            request.getRequestDispatcher("/seller/product/edit-product.jsp").forward(request, response);
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
