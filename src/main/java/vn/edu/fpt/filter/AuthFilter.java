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
 * Filter kiểm tra đăng nhập, phân quyền theo role và tự động chuyển hướng trang chủ khi khởi chạy web
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    private final CartDAO cartDAO = new CartDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());
        
        User user = (User) session.getAttribute("user");
        Integer roleId = (Integer) session.getAttribute("roleId");

        // 1. Bỏ qua các file tĩnh (CSS, JS, Hình ảnh, Font, Web Uploads)
        if (path.endsWith("/assets/") || path.contains("/assets/") || path.contains("/uploads/")
                || path.endsWith(".css") || path.endsWith(".js")
                || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg")
                || path.endsWith(".webp") || path.endsWith(".svg") || path.endsWith(".ico")
                || path.endsWith(".gif") || path.endsWith(".ttf") || path.endsWith(".woff") || path.endsWith(".woff2")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Chạy web lên tự động chuyển vào trang home (khi truy cập đường dẫn gốc /, "", /index.html, /index.jsp)
        if (path == null || path.equals("/") || path.isEmpty() || path.equals("/index.html") || path.equals("/index.jsp")) {
            resp.sendRedirect(contextPath + "/home");
            return;
        }

        // 3. Hiển thị số lượng giỏ hàng trên header
        int numberProductCart = 0;
        if (user != null) {
            Integer userId = extractUserId(session.getAttribute("user"));
            if (userId != null) {
                numberProductCart = cartDAO.getNumberOfProductCart(userId);
            }
        }
        req.setAttribute("numberProductCart", numberProductCart);

        // 4. Kiểm tra phân quyền và chặn các đường dẫn chưa đăng nhập hoặc sai role

        // --- Quyền ADMIN (roleId = 1) ---
        if (path.startsWith("/admin/")) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (roleId == null || roleId != 1) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }

        // --- Quyền SELLER (roleId = 3) ---
        if (path.startsWith("/seller/")
                || path.equals("/sellerDashboard")
                || path.equals("/add-product")
                || path.equals("/edit-product")
                || path.equals("/delete-product")
                || path.equals("/list-seller-products")
                || path.equals("/view-seller-product")
                || path.equals("/add-shop")
                || path.equals("/edit-shop")
                || path.equals("/edit-seller-status")
                || path.equals("/load-wards")) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (roleId == null || roleId != 3) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }

        // --- Quyền DELIVERY / SHIPPER (roleId = 4) ---
        if (path.startsWith("/logistics/") || path.startsWith("/delivery/")) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (roleId == null || roleId != 4) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }

        // --- Quyền CUSTOMER (chưa đăng nhập thì chặn) ---
        if (user == null) {
            // Chặn API customer
            if (path.startsWith("/api/customer/")) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"status\":\"UNAUTHORIZED\"}");
                return;
            }

            // Chặn trang customer
            if (path.startsWith("/customer/")) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        chain.doFilter(request, response);
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
