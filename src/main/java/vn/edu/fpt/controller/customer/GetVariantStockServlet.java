package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductDAO;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 04/06/2026
 * Description: Nhả về cho js số lượng biến thể còn trong kho
 */
@WebServlet("/get-variant-stock")
public class GetVariantStockServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy chuỗi thô từ request về trước
        String productIdStr = request.getParameter("product_id");
        String sizeIdStr = request.getParameter("size_id");
        String colorIdStr = request.getParameter("color_id");

        // 2. TẤM LÁ CHẮN: Kiểm tra nếu có bất kỳ ông nào bị null hoặc rỗng ""
        if (productIdStr == null || productIdStr.isEmpty() ||
                sizeIdStr == null || sizeIdStr.isEmpty() ||
                colorIdStr == null || colorIdStr.isEmpty()) {

            // Trả về số 0 lập tức và kết thúc hàm, không cho chạy xuống lệnh parseInt ở dưới
            response.setContentType("text/plain");
            response.getWriter().write("0");
            return;
        }

        // 3. Nếu dữ liệu đã an toàn và sạch sẽ, lúc này mới tự tin ép kiểu số int
        int productId = Integer.parseInt(productIdStr);
        int sizeId = Integer.parseInt(sizeIdStr);
        int colorId = Integer.parseInt(colorIdStr);

        ProductDAO dao = new ProductDAO();
        int stock = dao.getVariantStock(productId, sizeId, colorId);

        response.setContentType("text/plain");
        response.getWriter().write(String.valueOf(stock));
    }
}
