package vn.edu.fpt.controller.seller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Ward;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet xử lý yêu cầu AJAX tải danh sách phường/xã theo tỉnh/thành phố.
 * URL: GET /load-wards?provinceId={id}
 * Response: JSON array [{"id": 1, "name": "Phường ABC"}, ...]
 */
@WebServlet("/load-wards")
public class LoadWardServlet extends HttpServlet {

    private final WardDAO wardDAO = new WardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        PrintWriter out = response.getWriter();

        String provinceIdStr = request.getParameter("provinceId");

        // Kiểm tra tham số đầu vào
        if (provinceIdStr == null || provinceIdStr.trim().isEmpty()) {
            out.print("[]");
            return;
        }

        int provinceId;
        try {
            provinceId = Integer.parseInt(provinceIdStr.trim());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("[]");
            return;
        }

        try {
            List<Ward> wards = wardDAO.getWardsByProvinceId(provinceId);
            out.print(toJsonArray(wards));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }

    /**
     * Chuyển đổi danh sách Ward thành chuỗi JSON array.
     */
    private String toJsonArray(List<Ward> wards) {
        if (wards == null || wards.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < wards.size(); i++) {
            Ward ward = wards.get(i);
            json.append("{")
                    .append("\"id\":").append(ward.getId()).append(",")
                    .append("\"name\":\"").append(escapeJson(ward.getName())).append("\"")
                    .append("}");
            if (i < wards.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Escape các ký tự đặc biệt trong chuỗi JSON.
     */
    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}