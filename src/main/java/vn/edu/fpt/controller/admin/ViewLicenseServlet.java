package vn.edu.fpt.controller.admin;

import vn.edu.fpt.dao.SellerApplicationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/seller-applications/license")
public class ViewLicenseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int appId = Integer.parseInt(idStr);
                SellerApplicationDAO dao = new SellerApplicationDAO();
                SellerApplication app = dao.getApplicationById(appId);

                if (app != null) {
                    request.setAttribute("app", app);
                    request.getRequestDispatcher("/admin/seller_mgt/view-license.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/seller-applications");
    }
}
