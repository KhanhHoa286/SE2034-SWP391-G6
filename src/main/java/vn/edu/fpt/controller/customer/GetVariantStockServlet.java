package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 04/06/2026
 * Description: Nhả về cho js số lượng biến thể còn trong kho
 */
@WebServlet("/api/get-variant-stock")
public class GetVariantStockServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //Lấy chuỗi thô từ request về trước
        Integer productId = ParamUtil.getInteger(request,"product_id");
        Integer sizeId = ParamUtil.getInteger(request,"size_id");
        Integer colorId = ParamUtil.getInteger(request,"color_id");

        //kiểm tra nếu có bất kỳ tham số nào bị null hoặc rỗng ""
        if (productId == null || sizeId == null || colorId == null ) {
            // Trả về số 0 lập tức và kết thúc hàm, không cho chạy xuống lệnh parseInt ở dưới
            response.getWriter().write("{\"status\":\"INVALID_VARIANT\"}");
            return;
        }
        // chặn người bán
        if(user != null && shopDAO.checkProductSeller(productId,user.getUserId())) {
            response.getWriter().write("{\"status\":\"OWN_PRODUCT\"}");
            return;
        }
        //Dữ liệu ổn thì lấy variantId rồi lấy stock
        int variantId = productDAO.getVariantById(productId,sizeId,colorId);
        int stock = productDAO.getVariantStock(variantId);
        if(stock == 0) {
            response.getWriter().write("{\"status\":\"OUT_OF_STOCK\"}");
            return;
        }else if(stock > 0) {
            response.getWriter().write("{\"status\":\"SUCCESS\",\"stock\":\"" + stock + "\"}");
        }
    }
}
