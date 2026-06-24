package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Shop;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/edit-shipping-settings")
public class EditShippingSettingsServlet extends HttpServlet {

    private final ShopDAO shopDAO = new ShopDAO();

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
            // Try to find any shop for demo mode
            List<Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = shopDAO.getShopWithAddressAndOwnerByOwnerId(allShops.get(0).getOwnerId());
            }
        }

        if (shop == null) {
            // Fallback mock shop for demo
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
                    .wardId(-1)
                    .ward(ward)
                    .streetAddress("Số 1 Cầu Giấy")
                    .build();
        }

        // Set attributes
        request.setAttribute("shop", shop);
        request.setAttribute("activePage", "shipping-settings");

        request.getRequestDispatcher("/seller/config/edit-shipping-settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // For now, simple redirect back to GET upon saving changes
        doGet(request, response);
    }
}
