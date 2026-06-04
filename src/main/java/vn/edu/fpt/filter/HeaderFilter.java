package vn.edu.fpt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.dao.WishlistDAO;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.lang.reflect.Method;

// Qua các trang này sẽ load dữ liệu lên header
@WebFilter(urlPatterns = {"/home", "/product-list"})
public class HeaderFilter implements Filter {
    private final CartDAO cartDAO = new CartDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // kiểm tra đăng nhập
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        // số lượng của giỏ hàng và wishlist
            int numberProductCart = 0;
            int numberProductWishlist = 0;
        // đã đăng nhập thì load số lượng lên
        if(session.getAttribute("account") != null) {
            Integer userId = extractUserId(session.getAttribute("account"));
            if (userId != null) {
                numberProductCart = cartDAO.getNumberOfProductCart(userId);
                numberProductWishlist = wishlistDAO.getNumberOfProductCart(userId);
            }
        }

        // chưa đăng nhập thì mặc định vẫn là 0
        req.setAttribute("numberProductCart", numberProductCart);
        req.setAttribute("numberProductWishlist", numberProductWishlist);

        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private Integer extractUserId(Object account) {
        if (account instanceof User user) {
            return user.getUserId();
        }

        try {
            Method getter = account.getClass().getMethod("getUserId");
            Object value = getter.invoke(account);
            if (value instanceof Integer userId) {
                return userId;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
