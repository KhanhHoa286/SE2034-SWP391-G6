package vn.edu.fpt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CartDAO;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HoaNK - Load du lieu cart va wishlist len header cho tat cả cac trang co header
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    private final CartDAO cartDAO = new CartDAO();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // kiểm tra đăng nhập
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String path = req.getServletPath();
        User user = (User)session.getAttribute("user");
        // bỏ qua các file tĩnh
        if(path.endsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".webp")) {
            chain.doFilter(request,response);
            return;
        }

        // số lượng của giỏ hàng và wishlist
        int numberProductCart = 0;
        // đã đăng nhập thì load số lượng lên
        if(user != null) {
            Integer userId = extractUserId(session.getAttribute("user"));
            if (userId != null) {
                numberProductCart = cartDAO.getNumberOfProductCart(userId);
            }
        }
        // chưa đăng nhập thì mặc định là 0
        req.setAttribute("numberProductCart", numberProductCart);

        // chưa đăng nhập
        if(user == null) {
            // bắt các api chỉ có thể xài bởi customer
            if(path.startsWith("/api/customer/")){
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{" + "\"status\":\"UNAUTHORIZED\"" + "}");
                return;
            }

            // bắt các đường dẫn vào các trang customer nếu cố tình gõ trên url
            if(path.startsWith("/customer/")) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

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
