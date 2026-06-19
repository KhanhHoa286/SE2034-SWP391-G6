package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ReviewDAO;
import vn.edu.fpt.dto.response.ProductReviewResponse;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;

/**
 * HoaNK - HE195013
 * Date: 18/6/2026
 * Description: Load lên danh sách các đánh giá từ csdl hiển thị lên trang
 */

@WebServlet("/product-review")
public class ProductReviewsServlet extends HttpServlet {
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final int PAGE_SIZE_REVIEW = 5;
     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         //
         Integer productId = ParamUtil.getInteger(request, "pid");
         Integer page = ParamUtil.getInteger(request, "page");
         Integer star = ParamUtil.getInteger(request, "star");
         // nếu id không tồn tại đá ra lỗi 404 kèm message
         if(productId == null) {
             response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sản phẩm không tồn tại!");
         }
         // kiểm tra nếu page null hoặc <=0 thì luôn cho page = 1
         if(page == null || page <= 0) {
             page = 1;
         }
         //
         ProductReviewResponse productReviewResponse = reviewDAO.getProductReviewList(productId, page, PAGE_SIZE_REVIEW, star);
         //
         if(productReviewResponse != null) {
             request.setAttribute("productReview", productReviewResponse);
         }
         //
             request.getRequestDispatcher("/customer/review/list-product-reviews.jsp").forward(request,response);
         }
}
