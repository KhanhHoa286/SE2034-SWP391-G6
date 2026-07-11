package vn.edu.fpt.controller.admin;
import vn.edu.fpt.dao.SellerApplicationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/seller-applications/approve")
public class ApproveApplicationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String appIdStr = request.getParameter("id");

        if (appIdStr != null && !appIdStr.trim().isEmpty()) {
            try {
                int appId = Integer.parseInt(appIdStr.trim());
                // Gọi lớp xử lý logic duyệt đơn từ DAO
                SellerApplicationDAO dao = new SellerApplicationDAO();
                dao.approveApplication(appId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Quay trở lại trang danh sách sau khi thực hiện xong hành động
        response.sendRedirect(request.getContextPath() + "/admin/seller-applications");
    }
}