package vn.edu.fpt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import vn.edu.fpt.enums.UserStatus;
import vn.edu.fpt.model.User;

import java.io.IOException;

@WebFilter(urlPatterns = {"/logistics/delivery/*"})
public class DeliveryAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();

        if (session == null) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        Integer roleId = (Integer) session.getAttribute("roleId");

        if (user == null || roleId == null) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        if (roleId != 4) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        String approvalStatus = user.getShipperApprovalStatus();

        if (!"APPROVED".equalsIgnoreCase(approvalStatus)) {
            resp.sendRedirect(contextPath + "/public/auth/login.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}