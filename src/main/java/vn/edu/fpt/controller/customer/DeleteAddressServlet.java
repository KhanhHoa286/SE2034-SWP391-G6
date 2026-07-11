package vn.edu.fpt.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.User;

import java.io.IOException;

/*
 * Servlet xử lý xóa địa chỉ giao hàng của customer.
 *
 * URL:
 * /customer/addresses/delete
 *
 * Luồng:
 * - Nhận addressId từ form POST
 * - Lấy userId từ session
 * - Kiểm tra address có thuộc user không
 * - Nếu user chỉ còn 1 address thì không cho xóa
 * - Nếu xóa address mặc định thì set address khác làm mặc định
 * - Redirect về /customer/addresses
 */
@WebServlet(name = "DeleteAddressServlet", urlPatterns = {"/customer/addresses/delete"})
public class DeleteAddressServlet extends HttpServlet {

    private final AddressDAO addressDAO = new AddressDAO();

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

        int addressId = parseInt(request.getParameter("addressId"));

        if (addressId <= 0) {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        Address address = addressDAO.getAddressByIdAndUserId(addressId, userId);

        if (address == null) {
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        int totalAddress = addressDAO.countAddressesByUserId(userId);

        if (totalAddress <= 1) {
            session.setAttribute("addressError",
                    "Bạn phải có ít nhất một địa chỉ giao hàng.");
            response.sendRedirect(request.getContextPath() + "/customer/addresses");
            return;
        }

        boolean isDefaultAddress = Boolean.TRUE.equals(address.getIsDefault());

        boolean deleted = addressDAO.deleteAddressByIdAndUserId(addressId, userId);

        if (deleted && isDefaultAddress) {
            addressDAO.setFirstAddressAsDefault(userId);
        }

        response.sendRedirect(request.getContextPath() + "/customer/addresses");
    }

    /*
     * Lấy userId từ session.
     * Hỗ trợ cả 2 kiểu session: userId hoặc user.
     */
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

    /*
     * Parse String sang int.
     * Nếu lỗi thì trả về -1.
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }
}