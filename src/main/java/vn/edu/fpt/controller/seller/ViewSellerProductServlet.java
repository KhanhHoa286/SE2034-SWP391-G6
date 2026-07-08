package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CategoryDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Category;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductImage;
import vn.edu.fpt.model.ProductVariant;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@WebServlet("/view-seller-product")
public class ViewSellerProductServlet extends HttpServlet {

    private static final DateTimeFormatter VIEW_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Integer productId = parseProductId(request.getParameter("id"));
        if (productId == null) {
            redirectToProductList(request, response);
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        ShopDAO shopDAO = new ShopDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        Shop shop = resolveCurrentShop(request.getSession(false), shopDAO);
        if (shop == null) {
            redirectToProductList(request, response);
            return;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null || !Objects.equals(product.getShopId(), shop.getShopId())) {
            redirectToProductList(request, response);
            return;
        }

        List<ProductVariant> variants = safeList(productDAO.getVariantsByProductId(productId));
        List<ProductImage> images = safeList(productDAO.getProductImagesByProductId(productId));
        Category category = findCategoryById(categoryDAO.getAllCategory(), product.getCategoryId());
        if (category != null) {
            product.setCategory(category);
        }

        int totalStock = calculateTotalStock(variants);
        String mainProductImageUrl = resolveMainImage(product, images);

        request.setAttribute("product", product);
        request.setAttribute("productVariants", variants);
        request.setAttribute("productImagesList", images);
        request.setAttribute("shop", shop);
        request.setAttribute("activePage", "products");

        request.setAttribute("mainProductImageUrl", mainProductImageUrl);
        request.setAttribute("hasMainProductImage", mainProductImageUrl != null && !mainProductImageUrl.isBlank());
        request.setAttribute("productImageCount", images.size());
        request.setAttribute("extraImageCount", Math.max(images.size() - 4, 0));
        request.setAttribute("productVariantCount", variants.size());
        request.setAttribute("totalStock", totalStock);
        request.setAttribute("stockStatusText", getStockStatusText(totalStock));
        request.setAttribute("stockStatusClass", getStockStatusClass(totalStock));
        request.setAttribute("categoryName", category != null ? category.getCategoryName() : "Chưa phân loại");
        request.setAttribute("genderText", getGenderText(product));
        request.setAttribute("activeText", Boolean.TRUE.equals(product.getIsActive()) ? "Đang hoạt động" : "Ngừng bán");
        request.setAttribute("activeClass", Boolean.TRUE.equals(product.getIsActive()) ? "view-status-active" : "view-status-inactive");
        request.setAttribute("approvalText", getApprovalText(product));
        request.setAttribute("approvalClass", getApprovalClass(product));
        request.setAttribute("formattedCreatedAt", formatCreatedAt(product));
        request.setAttribute("formattedProductCode", String.format("PRD-%05d", product.getProductId()));
        request.setAttribute("discountedPrice", calculateDiscountedPrice(product));

        request.getRequestDispatcher("/seller/product/view-seller-product.jsp").forward(request, response);
    }

    private Integer parseProductId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return null;
        }
        try {
            int id = Integer.parseInt(rawId.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Shop resolveCurrentShop(HttpSession session, ShopDAO shopDAO) {
        User account = session != null ? (User) session.getAttribute("account") : null;
        Shop shop = account != null ? shopDAO.getShopByOwnerId(account.getUserId()) : null;

        if (shop != null) {
            return shop;
        }

        List<Shop> allShops = shopDAO.getAllShops();
        return allShops != null && !allShops.isEmpty() ? allShops.get(0) : null;
    }

    private <T> List<T> safeList(List<T> source) {
        return source == null ? Collections.emptyList() : source;
    }

    private int calculateTotalStock(List<ProductVariant> variants) {
        int total = 0;
        for (ProductVariant variant : variants) {
            if (variant.getStockQuantity() != null) {
                total += variant.getStockQuantity();
            }
        }
        return total;
    }

    private String resolveMainImage(Product product, List<ProductImage> images) {
        for (ProductImage image : images) {
            if (image != null && Boolean.TRUE.equals(image.getIsPrimary())
                    && image.getImageUrl() != null && !image.getImageUrl().isBlank()) {
                return image.getImageUrl();
            }
        }
        for (ProductImage image : images) {
            if (image != null && image.getImageUrl() != null && !image.getImageUrl().isBlank()) {
                return image.getImageUrl();
            }
        }
        return product.getThumbnailUrl() != null && !product.getThumbnailUrl().isBlank()
                ? product.getThumbnailUrl()
                : "";
    }

    private Category findCategoryById(List<Category> categories, Integer categoryId) {
        if (categories == null || categoryId == null) {
            return null;
        }

        List<Category> stack = new ArrayList<>(categories);
        while (!stack.isEmpty()) {
            Category current = stack.remove(0);
            if (Objects.equals(current.getCategoryId(), categoryId)) {
                return current;
            }
            if (current.getListChildCategory() != null) {
                stack.addAll(current.getListChildCategory());
            }
        }
        return null;
    }

    private String getStockStatusText(int totalStock) {
        if (totalStock > 15) {
            return "Còn hàng";
        }
        if (totalStock > 0) {
            return "Sắp hết hàng";
        }
        return "Hết hàng";
    }

    private String getStockStatusClass(int totalStock) {
        if (totalStock > 15) {
            return "stock-badge-instock";
        }
        if (totalStock > 0) {
            return "stock-badge-lowstock";
        }
        return "stock-badge-outofstock";
    }

    private String getGenderText(Product product) {
        if (product.getGender() == null) {
            return "Unisex";
        }
        return switch (product.getGender().name()) {
            case "NAM" -> "Nam";
            case "NU" -> "Nữ";
            default -> "Unisex";
        };
    }

    private String getApprovalText(Product product) {
        if (product.getStatus() == null) {
            return "Chưa có trạng thái";
        }
        return switch (product.getStatus().name()) {
            case "APPROVED" -> "Đã duyệt";
            case "PENDING" -> "Chờ duyệt";
            case "REJECTED" -> "Bị từ chối";
            default -> product.getStatus().name();
        };
    }

    private String getApprovalClass(Product product) {
        if (product.getStatus() == null) {
            return "view-status-pending";
        }
        return switch (product.getStatus().name()) {
            case "APPROVED" -> "view-status-active";
            case "REJECTED" -> "view-status-inactive";
            default -> "view-status-pending";
        };
    }

    private String formatCreatedAt(Product product) {
        if (product.getCreatedAt() == null) {
            return "Chưa có thông tin";
        }
        return product.getCreatedAt().format(VIEW_DATE_FORMAT);
    }

    private BigDecimal calculateDiscountedPrice(Product product) {
        BigDecimal basePrice = product.getBasePrice() != null ? product.getBasePrice() : BigDecimal.ZERO;
        int discount = product.getDiscountPercentage() != null ? product.getDiscountPercentage() : 0;
        BigDecimal discountAmount = basePrice
                .multiply(BigDecimal.valueOf(discount))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return basePrice.subtract(discountAmount);
    }

    private void redirectToProductList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/list-seller-products");
    }
}
