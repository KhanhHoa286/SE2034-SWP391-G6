package vn.edu.fpt.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;

@WebFilter("/logistics/delivery/*")
public class DeliveryAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();

        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        Object userObj = session.getAttribute("user");
        Object roleObj = session.getAttribute("roleId");

        if (!(userObj instanceof User) || roleObj == null) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        User user = (User) userObj;

        int roleId;

        try {
            if (roleObj instanceof Integer) {
                roleId = (Integer) roleObj;
            } else {
                roleId = Integer.parseInt(roleObj.toString());
            }
        } catch (Exception e) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        /*
         * Chỉ user có role DELIVERY mới được vào trang giao hàng.
         * roleId = 4 tương ứng DELIVERY.
         */
        if (roleId != 4) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        /*
         * Đã bỏ luồng duyệt shipper.
         * Không check shipperApprovalStatus nữa.
         */
        if (user.getStatus() != UserStatus.ACTIVE) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}