package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.util.List;
import java.io.IOException;

@WebServlet("/view-shop")
public class ViewShopServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User account = (User) session.getAttribute("account");

        int ownerId = (account != null) ? account.getUserId() : -1;
        Shop shop = null;
        if (ownerId != -1) {
            shop = shopDAO.getShopWithAddressAndOwnerByOwnerId(ownerId);
        }

        if (shop == null) {
            // Thử lấy shop đầu tiên trong hệ thống làm demo (không redirect)
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = shopDAO.getShopWithAddressAndOwnerByOwnerId(allShops.get(0).getOwnerId());
            }
        }

        if (shop == null) {
            // Nếu hoàn toàn không có shop nào trong DB, sử dụng dummy/placeholder
            vn.edu.fpt.model.Province province = vn.edu.fpt.model.Province.builder()
                    .name("Hà Nội")
                    .build();
            vn.edu.fpt.model.Ward ward = vn.edu.fpt.model.Ward.builder()
                    .name("Dịch Vọng Hậu")
                    .province(province)
                    .build();
            User owner = User.builder()
                    .userId(-1)
                    .email("demo@vinastudio.com")
                    .phone("0987654321")
                    .build();
            shop = Shop.builder()
                    .shopId(-1)
                    .ownerId(-1)
                    .owner(owner)
                    .shopName("Atelier Luxe")
                    .description("Atelier Luxe là biểu tượng của sự tinh tế và tối giản trong thời trang cao cấp. Chúng tôi tập trung vào những thiết kế có cấu trúc rõ ràng, chất liệu thượng hạng và bảng màu monochrome bất hủ. Mỗi sản phẩm tại Atelier Luxe không chỉ là trang phục, mà là một tác phẩm kiến trúc dành cho cơ thể, tôn vinh vẻ đẹp và sự sang trọng thầm lặng của người mặc hiện đại.")
                    .wardId(-1)
                    .ward(ward)
                    .streetAddress("Số 1 Cầu Giấy")
                    .averageRating(new java.math.BigDecimal("4.9"))
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
        }

        // Generate initials for the shop name
        String shopName = shop.getShopName();
        String shopInitials = "SH";
        if (shopName != null && !shopName.trim().isEmpty()) {
            String[] words = shopName.trim().split("\\s+");
            if (words.length >= 2) {
                shopInitials = (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase();
            } else if (words.length == 1) {
                shopInitials = words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
            }
        }

        // Count active products
        int activeProductsCount = productDAO.countActiveProductsByShopId(shop.getShopId());

        // Format join date
        String joinedDate = "12/2022";
        if (shop.getCreatedAt() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM/yyyy");
            joinedDate = shop.getCreatedAt().format(formatter);
        }

        // Set attributes
        request.setAttribute("shop", shop);
        request.setAttribute("shopInitials", shopInitials);
        request.setAttribute("activeProductsCount", activeProductsCount);
        request.setAttribute("joinedDate", joinedDate);
        request.setAttribute("activePage", "view-shop");

        request.getRequestDispatcher("/seller/shop/view-shop.jsp").forward(request, response);
    }
}
