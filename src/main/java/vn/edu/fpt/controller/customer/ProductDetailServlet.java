package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy đường dẫn quay lại
        String referrer = getBackUrl(request);

        // lấy tham số và chuyển đooir
        Integer pid = ParamUtil.getInteger(request,"pid");
        BigDecimal price =  ParamUtil.getBigDecimal(request,"final_price");
        String gender = request.getParameter("gender");

        // kiểm tra id hợp lệ
        if(pid == null || pid <= 0) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return; // tra ve trang va dung ngay
        }

        // lấy ra san chi tiet san pham tuong ung voi id
        ProductDetailResponse productDetailResponse = productDAO.getProductDetailByProductId(pid);
        if(productDetailResponse == null) { // neu bang null tuc san pham do ko ton tai
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        // lấy ra top 4 sản phẩm liên quan sản phẩm gốc
        List<ProductResponse> productRelatedList = productDAO.getTop4ProductRelated(gender, pid, price);

        // check product của shop seller
        boolean checkProductSeller = checkSeller(request, pid);

        //
        request.setAttribute("backUrl", referrer);
        request.setAttribute("productDetail", productDetailResponse);
        request.setAttribute("productResponseList", productRelatedList);
        request.setAttribute("checkProductSeller", checkProductSeller);
        //
        request.getRequestDispatcher("/public/product/view-product.jsp").forward(request,response);
    }

    // lấy đường dẫn trước đó
    private String getBackUrl(HttpServletRequest request) {
        String referrer = request.getHeader("referer");
        String defaultBackUrl = request.getContextPath() + "/product-list";
        // kiểm tra đường dẫn
        if (referrer == null || referrer.contains(".jsp") || !referrer.contains(request.getServerName())) {
            referrer = defaultBackUrl;
        }
        return referrer;
    }

    // kiểm tra người bán để ko cho mua sản phẩm shop mình
    private boolean checkSeller(HttpServletRequest request, int pid) {
        HttpSession session = request.getSession(false);
        if(session== null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }

        return shopDAO.checkProductSeller(pid,user.getUserId());
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
