package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dto.request.CartRequest;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HoaNK - HE195013
 * Date: 11/6/2026
 * Description: Thêm 1 sản phẩm vào giỏ hàng
 */
@WebServlet("/api/add-to-cart")
public class AddToCart extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();
    @Override
         protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             CartRequest cartRequest = new CartRequest();
             cartRequest.setQuantity(Integer.parseInt(request.getParameter("quantity")));
             cartRequest.setProductId(Integer.parseInt(request.getParameter("productId")));
             cartRequest.setColorId(Integer.parseInt(request.getParameter("colorId")));
             cartRequest.setSizeId(Integer.parseInt(request.getParameter("sizeId")));
             // lấy ra variant id
        int variantId = productDAO.getVariantById(cartRequest.getProductId(), cartRequest.getSizeId(), cartRequest.getColorId());
        if(variantId == 0) { // lỗi hoặc ko có variant id ném về trang details
            response.sendRedirect(request.getContextPath() + "/product-detail?pid=" + cartRequest.getProductId());
            return;
        }
        // thêm vào giỏ hàng
        boolean checkAddItemCart = handleGuestCart(request,response, cartRequest,variantId);

        // gửi về cho js
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        //
        if(!checkAddItemCart) {
            response.getWriter().write("OVER_STOCK");
            return;
        }
        // lấy số lượng hiển thị trên giỏ hàng
        int numberProductCart = getNumberProductCart(request);
        response.getWriter().write(String.valueOf(numberProductCart));
    }

    // kiểm tra người dùng đã đăng nhập chưa và lưu vào giỏ hàng
    private boolean handleGuestCart(HttpServletRequest request,HttpServletResponse response, CartRequest cartRequest, int variantId) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // lấy ra số lượng hiện tại của biến thể đang định thêm
        int currentStock = productDAO.getVariantStock(cartRequest.getProductId(), cartRequest.getSizeId(), cartRequest.getColorId());
        // check đăng  nhập
        if(user == null) { // chưa đăng nhập
           List<CartRequest> sessionCart = (List<CartRequest>) session.getAttribute("cart");
           if(sessionCart == null) { // chưa đăng nhập và cũng chưa có sản phẩm nào trong giỏ hàng
               sessionCart = new ArrayList<>();
               session.setAttribute("cart", sessionCart);
           }
           //
            int quantitySession = 0; // lấy ra số lượng sản phẩm đang có trong giỏ lưu ở session(chưa login)
            CartRequest item = null;  // dùng để lát lưu sản phẩm đó
           // chưa đăng nhập nhưng có sản phẩm trong giỏ
               for(CartRequest c : sessionCart) {
                   if(c.getProductId().equals(cartRequest.getProductId())
                   && c.getSizeId().equals(cartRequest.getSizeId())
                   && c.getColorId().equals(cartRequest.getColorId())) {
                       quantitySession = c.getQuantity();
                       item = c;
                       break;
                   }
               }

               // số lượng mà người dùng muốn thêm ví dụ: thêm 3 cái xong lại thêm 3 cái nữa
               int targetQuantity = quantitySession + cartRequest.getQuantity();
               if(targetQuantity > currentStock) { // nếu tổng số lượng biến thể người dùng muốn thêm > số lượng kho biến thể
                   return false;
               }
               //ngược lại nếu số lượng người dùng muốn thêm hợp lí kho vẫn còn đủ
            if(item != null) { // khác null tức có trong ss rồi update số lượng
                item.setQuantity(targetQuantity);
            }else{
                sessionCart.add(cartRequest);
            }
        }else{ // đã đăng nhập
            // lấy ra số lượng variant trong giỏ ở database
            int quantityDb = cartDAO.getQuantityAVariantCart(variantId, user.getUserId());
            int targetQuantity = quantityDb + cartRequest.getQuantity();
            if(targetQuantity > currentStock) {
                return false;
            }
            //
            cartDAO.addToCart(user.getUserId(),variantId,cartRequest.getQuantity());
        }
        return true;
    }

    //  trả về số lượng để hiện thị động trên icon giỏ hàng
    private int getNumberProductCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null) {
            return cartDAO.getNumberOfProductCart(user.getUserId());
        }else{ // chauw đang nhập
            List<CartRequest> sessionCart = (List<CartRequest>) session.getAttribute("cart");
            if(sessionCart != null) {
                return sessionCart.size();
            }
            return 0;
        }
    }
}
