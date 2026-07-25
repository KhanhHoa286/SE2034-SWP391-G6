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

import java.io.IOException;

@WebServlet("/view-shop")
public class ViewShopServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Shop shop = resolveCurrentShop(session);

        if (shop == null) {
            response.sendRedirect(request.getContextPath() + "/seller-register");
            return;
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

    private Shop resolveCurrentShop(HttpSession session) {
        Integer ownerId = getLoggedInUserId(session);
        return ownerId == null ? null : shopDAO.getShopWithAddressAndOwnerByOwnerId(ownerId);
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
