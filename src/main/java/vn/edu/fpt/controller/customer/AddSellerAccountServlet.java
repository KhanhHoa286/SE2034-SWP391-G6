package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.CustomerDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AddSellerAccountServlet", urlPatterns = {
        "/seller-register",
        "/customer/seller-register",
        "/customer/add-seller-account"
})
public class AddSellerAccountServlet extends HttpServlet {

    private static final String FORM_PAGE = "/customer/seller_register/add-seller-account.jsp";

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (customerDAO.hasSellerAccount(userId)) {
            session.setAttribute("hasSellerAccount", true);
            response.sendRedirect(request.getContextPath() + "/seller/orders");
            return;
        }

        forwardForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = getLoggedInUserId(session);
        if (userId == null || userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (customerDAO.hasSellerAccount(userId)) {
            session.setAttribute("hasSellerAccount", true);
            response.sendRedirect(request.getContextPath() + "/seller/orders");
            return;
        }

        String shopName = clean(request.getParameter("shopName"));
        String streetAddress = clean(request.getParameter("streetAddress"));
        String description = clean(request.getParameter("description"));
        Integer provinceId = parsePositiveInt(request.getParameter("provinceId"));
        Integer wardId = parsePositiveInt(request.getParameter("wardId"));

        Map<String, String> errors = new HashMap<>();
        Map<String, String> oldInput = new HashMap<>();
        oldInput.put("shopName", shopName);
        oldInput.put("streetAddress", streetAddress);
        oldInput.put("description", description);
        oldInput.put("provinceId", provinceId == null ? "" : provinceId.toString());
        oldInput.put("wardId", wardId == null ? "" : wardId.toString());

        if (shopName.isEmpty()) {
            errors.put("shopName", "Vui lòng nhập tên cửa hàng.");
        } else if (shopName.length() < 3 || shopName.length() > 100) {
            errors.put("shopName", "Tên cửa hàng phải từ 3 đến 100 ký tự.");
        } else if (customerDAO.shopNameExists(shopName)) {
            errors.put("shopName", "Tên cửa hàng này đã được sử dụng.");
        }

        if (provinceId == null) {
            errors.put("provinceId", "Vui lòng chọn tỉnh hoặc thành phố.");
        }

        if (wardId == null) {
            errors.put("wardId", "Vui lòng chọn phường hoặc xã.");
        }

        if (provinceId != null && wardId != null && !customerDAO.isWardInProvince(wardId, provinceId)) {
            errors.put("wardId", "Phường hoặc xã không thuộc tỉnh/thành đã chọn.");
        }

        if (streetAddress.isEmpty()) {
            errors.put("streetAddress", "Vui lòng nhập địa chỉ lấy hàng.");
        } else if (streetAddress.length() < 5 || streetAddress.length() > 255) {
            errors.put("streetAddress", "Địa chỉ lấy hàng phải từ 5 đến 255 ký tự.");
        }

        if (description.length() > 500) {
            errors.put("description", "Mô tả cửa hàng không được vượt quá 500 ký tự.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInput);
            forwardForm(request, response);
            return;
        }

        CustomerDAO.SellerRegistrationResult result = customerDAO.registerCustomerAsSeller(
                userId,
                shopName,
                streetAddress,
                wardId,
                description
        );

        if (!result.isSuccess()) {
            errors.put("general", result.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("oldInput", oldInput);
            forwardForm(request, response);
            return;
        }

        session.setAttribute("hasSellerAccount", true);
        response.sendRedirect(request.getContextPath() + "/customer/dashboard?sellerRegistered=1");
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("provinces", provinceDAO.getAllProvinces());
        request.getRequestDispatcher(FORM_PAGE).forward(request, response);
    }

    private Integer getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }

        if (rawUserId != null) {
            try {
                return Integer.parseInt(rawUserId.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        Object rawUser = session.getAttribute("user");
        if (rawUser instanceof User) {
            return ((User) rawUser).getUserId();
        }

        return null;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(clean(value));
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
