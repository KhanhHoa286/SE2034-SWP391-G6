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
import java.util.List;

@WebServlet("/view-seller-product")
public class ViewSellerProductServlet extends HttpServlet {

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

        // Lấy product ID từ request parameter
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr.trim());
            Product product = productDAO.getProductById(productId);

            // Kiểm tra sản phẩm tồn tại và thuộc shop hiện tại
            if (product == null || (shop != null && !product.getShopId().equals(shop.getShopId()))) {
                response.sendRedirect(request.getContextPath() + "/list-seller-products");
                return;
            }

            // Lấy danh sách biến thể và ảnh sản phẩm
            List<ProductVariant> variants = productDAO.getVariantsByProductId(productId);
            List<ProductImage> images = productDAO.getProductImagesByProductId(productId);

            // Lấy thông tin category nếu có
            if (product.getCategoryId() != null) {
                List<Category> allCategories = categoryDAO.getAllCategory();
                Category matchedCategory = findCategoryById(allCategories, product.getCategoryId());
                if (matchedCategory != null) {
                    product.setCategory(matchedCategory);
                }
            }

            // Định dạng ngày tạo ở Controller để hiển thị ở JSP
            String formattedCreatedAt = "Chưa có thông tin";
            if (product.getCreatedAt() != null) {
                try {
                    formattedCreatedAt = product.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                } catch (Exception e) {
                    formattedCreatedAt = product.getCreatedAt().toString();
                }
            }

            // Đặt các attributes để JSP sử dụng
            request.setAttribute("product", product);
            request.setAttribute("productVariants", variants);
            request.setAttribute("productImagesList", images);
            request.setAttribute("shop", shop);
            request.setAttribute("activePage", "products");
            request.setAttribute("formattedCreatedAt", formattedCreatedAt);

            // Forward đến trang view-seller-product.jsp
            request.getRequestDispatcher("/seller/product/view-seller-product.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/list-seller-products");
        }
    }

    /**
     * Tìm Category theo ID trong danh sách phẳng (bao gồm cả child categories).
     */
    private Category findCategoryById(List<Category> categories, int categoryId) {
        if (categories == null) return null;
        for (Category cat : categories) {
            if (cat.getCategoryId() != null && cat.getCategoryId() == categoryId) {
                return cat;
            }
            // Tìm trong danh mục con
            if (cat.getListChildCategory() != null) {
                for (Category child : cat.getListChildCategory()) {
                    if (child.getCategoryId() != null && child.getCategoryId() == categoryId) {
                        return child;
                    }
                }
            }
        }
        return null;
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
