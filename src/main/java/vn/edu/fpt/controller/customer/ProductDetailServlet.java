package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.dao.WishlistDAO;
import vn.edu.fpt.dto.response.ProductDetailResponse;
import vn.edu.fpt.dto.response.ProductResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 03/06/2026
 * Description: Xử lí và trả về các field của trang view product details khi bắt được product-id
 */
@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {
    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // lấy tham số và chuyển đooir
        Integer pid = ParamUtil.getInteger(request, "pid");
        BigDecimal price = ParamUtil.getBigDecimal(request, "final_price");
        String gender = request.getParameter("gender");

        // kiểm tra id hợp lệ
        if (pid == null || pid <= 0) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        // lấy ra san chi tiet san pham tuong ứng voi id
        ProductDetailResponse productDetailResponse = productDAO.getProductDetailByProductId(pid);
        if (productDetailResponse == null) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        // load dữ liệu cho trang product details
        loadProductDetailContext(request, pid, price, gender, productDetailResponse);

        // Đẩy sang trang hiển thị
        request.getRequestDispatcher("/public/product/view-product.jsp").forward(request, response);
    }

    // nạp dữ liệu setattribute
    private void loadProductDetailContext(HttpServletRequest request, Integer pid, BigDecimal price, String gender, ProductDetailResponse productDetailResponse) {
        // lấy ra top 4 sản phẩm liên quan sản phẩm gốc
        List<ProductResponse> productRelatedList = productDAO.getTop4ProductRelated(gender, pid, price);

        // đắp dữ liệu để tô màu tim
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        wishlistDAO.setLikedForProduct(productRelatedList, user);

        // check product của shop seller
        boolean checkProductSeller = checkSeller(request, pid);
        // gửi dữ liệu qua jsp
        request.setAttribute("productDetail", productDetailResponse);
        request.setAttribute("productResponseList", productRelatedList);
        request.setAttribute("checkProductSeller", checkProductSeller);
    }

    // kiểm tra người bán để ko cho mua sản phẩm shop mình
    private boolean checkSeller(HttpServletRequest request, int pid) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return shopDAO.checkProductSeller(pid, user.getUserId());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}