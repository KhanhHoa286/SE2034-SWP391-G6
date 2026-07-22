package vn.edu.fpt.controller.customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.model.User;
import java.io.IOException;
import java.io.PrintWriter;
@WebServlet(name = "SetDefaultAddressServlet", urlPatterns = {"/api/customer/addresses/set-default"})
public class SetDefaultAddressServlet extends HttpServlet {
    private final AddressDAO addressDAO = new AddressDAO();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);
        if (userId == null || userId <= 0) {
            out.write("{\"success\":false, \"message\":\"Bạn chưa đăng nhập.\"}");
            return;
        }
        try {
            int addressId = Integer.parseInt(request.getParameter("addressId"));
            boolean isUpdated = addressDAO.setDefaultAddress(addressId, userId);
            if (isUpdated) {
                String redirectUrl = (String) session.getAttribute("CHECKOUT_REFERER");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("CHECKOUT_REFERER");
                    out.write("{\"success\":true, \"redirectUrl\":\"" + redirectUrl + "\"}");
                } else {
                    out.write("{\"success\":true, \"redirectUrl\":\"" + request.getContextPath() + "/customer/addresses\"}");
                }
            } else {
                out.write("{\"success\":false, \"message\":\"Cập nhật thất bại. Vui lòng thử lại.\"}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false, \"message\":\"Yêu cầu không hợp lệ.\"}");
        }
    }
    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) return null;
        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) return (Integer) rawUserId;
        if (rawUserId != null) {
            try { return Integer.parseInt(rawUserId.toString()); }
            catch (NumberFormatException ignored) {}
        }
        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) return ((User) rawUser).getUserId();
        return null;
    }
}
