package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ReviewDAO;
import vn.edu.fpt.dto.request.AddReviewRequest;
import vn.edu.fpt.dto.response.AddReviewResponse;
import vn.edu.fpt.model.User;
import vn.edu.fpt.util.ParamUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * HoaNK - HE195013
 * Date:
 * Description: Thêm mới đánh giá sản phẩm nếu đã đánh giá điều hướng về lại order-list
 */
@WebServlet("/customer/add-product-review")
public class AddProductReviewServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

     @Override
         protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
         response.setHeader("Pragma", "no-cache");
         response.setDateHeader("Expires", 0);
             //
         Integer productId = ParamUtil.getInteger(request,"product_id");
         Integer orderItemId = ParamUtil.getInteger(request, "order_item_id");
         Integer subOrderId = ParamUtil.getInteger(request,"sub_order_id");

         if(productId == null || orderItemId == null || subOrderId == null || productId <= 0 || orderItemId <= 0 || subOrderId <= 0) {
             response.sendRedirect(request.getContextPath() + "/customer/order-list");
             return;
         }

         boolean checkReview = reviewDAO.checkReviewProduct(productId,orderItemId);
         if(checkReview) {
             response.sendRedirect(request.getContextPath() + "/" + "customer/order-list");
             return;
         }

         //
         AddReviewResponse addReviewResponse = productDAO.getFieldsResponseAddReview(productId,orderItemId);
         if(addReviewResponse == null) {
             response.sendRedirect(request.getContextPath() + "/" + "customer/order-list");
             return;
         }
         //
         request.setAttribute("reviewResponse", addReviewResponse);
         request.getRequestDispatcher("/customer/review/add-product-review.jsp").forward(request,response);
         }


        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        addProductReview(request,response);
        }

     private void addProductReview(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
         HttpSession session = request.getSession();
         User user = (User)session.getAttribute("user");
         if (user == null) {
             response.sendRedirect(request.getContextPath() + "/login");
             return;
         }
         Integer productId = ParamUtil.getInteger(request, "product_id");
         Integer orderItemId = ParamUtil.getInteger(request,"order_item_id");
         Integer subOrderId = ParamUtil.getInteger(request, "sub_order_id");
         Integer ratingStar = ParamUtil.getInteger(request,"rating");
         String titleReview = request.getParameter("title_review");
         String comment = request.getParameter("comment");

         Map<String,String> error = new HashMap<>();

         if(ratingStar == null || ratingStar <= 0) {
             error.put("rating","* Vui lòng chọn mức sao đánh giá!");
         }

         if(titleReview == null || titleReview.isEmpty()){
             error.put("titleReview","* Vui lòng viết tiêu đề đánh giá!");
         }

         if(comment == null || comment.isEmpty()) {
             error.put("comment", "* Vui lòng nhập nội dung đánh giá!");
         }
         //

         AddReviewResponse addReviewResponse = null;
             if (!error.isEmpty()) {
                 addReviewResponse = productDAO.getFieldsResponseAddReview(productId,orderItemId);
                 request.setAttribute("error", error);

                 request.setAttribute("oldTitle",titleReview);
                 request.setAttribute("oldComment",comment);
                 request.setAttribute("oldRating",ratingStar);

                 request.setAttribute("reviewResponse",addReviewResponse);
                 request.getRequestDispatcher("/customer/review/add-product-review.jsp").forward(request,response);
                 return;
             }
                 AddReviewRequest addReviewRequest = new AddReviewRequest();
                 addReviewRequest.setProductId(productId);
                 addReviewRequest.setOrderItemId(orderItemId);
                 addReviewRequest.setCommentReview(comment);
                 addReviewRequest.setTitleReview(titleReview);
                 addReviewRequest.setRatingStar(ratingStar);
                 //
               boolean checkAddReview = reviewDAO.addReviewProduct(addReviewRequest,user.getUserId());


         if(checkAddReview == false) {
             session.setAttribute("addFail", "* Đánh giá thất bại! Vui lòng kiểm tra lại!");

             response.sendRedirect(request.getContextPath() + "/customer/add-product-review?product_id=" + productId + "&order_item_id=" + orderItemId);
         }else {
             response.sendRedirect(request.getContextPath() + "/customer/view-order?sub_order_id=" + subOrderId);
         }
     }
}
