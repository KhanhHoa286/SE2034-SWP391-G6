package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Lấy chuỗi thô từ request về trước
        Integer productId = ParamUtil.getInteger(request,"product_id");
        Integer sizeId = ParamUtil.getInteger(request,"size_id");
        Integer colorId = ParamUtil.getInteger(request,"color_id");

        //kiểm tra nếu có bất kỳ ông nào bị null hoặc rỗng ""
        if (productId == null || sizeId == null || colorId == null ) {

            // Trả về số 0 lập tức và kết thúc hàm, không cho chạy xuống lệnh parseInt ở dưới
            response.setContentType("text/plain");
            response.getWriter().write("0");
            return;
        }

        //Nếu dữ liệu đã an toàn và sạch sẽ, lúc này mới tự tin ép kiểu số int
        int stock = productDAO.getVariantStock(productId, sizeId, colorId);

        response.setContentType("text/plain");
        response.getWriter().write(String.valueOf(stock));
    }
}
