package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ShopDAO;
import vn.edu.fpt.model.Shop;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ShopListController", urlPatterns = {"/admin/shop-management"})
public class ShopListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ShopDAO dao = new ShopDAO();

        String txtSearch = request.getParameter("search");
        if (txtSearch == null) txtSearch = "";

        String statusFilter = request.getParameter("status");
        if (statusFilter == null || statusFilter.isEmpty()) statusFilter = "all";

        int pageSize = 5;
        int pageIndex = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                pageIndex = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }

        List<Shop> shopList = dao.getFilteredShops(txtSearch, statusFilter, pageIndex, pageSize);
        int totalRecords = dao.getTotalFilteredShops(txtSearch, statusFilter);

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;

        request.setAttribute("shopList", shopList);
        request.setAttribute("totalShops", totalRecords);
        request.setAttribute("endP", totalPages);
        request.setAttribute("tag", pageIndex);
        request.setAttribute("saveSearch", txtSearch);
        request.setAttribute("saveStatus", statusFilter);

        request.getRequestDispatcher("/admin/shop_mgt/list-shops.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int shopId = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            String newStatus = "ban".equals(action) ? "SUSPENDED" : "ACTIVE";

            ShopDAO dao = new ShopDAO();
            dao.updateShopStatus(shopId, newStatus);

            response.sendRedirect(request.getContextPath() + "/admin/shop-management");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/shop-management?error=1");
        }
    }
}
