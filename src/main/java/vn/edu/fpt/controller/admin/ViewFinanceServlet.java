package vn.edu.fpt.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.CommissionConfigDAO;
import vn.edu.fpt.model.CommissionConfig;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/finance/view-finance")
public class ViewFinanceServlet extends HttpServlet {
    private final CommissionConfigDAO commissionConfigDAO = new CommissionConfigDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommissionConfig latestConfig = commissionConfigDAO.getLatestConfig();
        List<CommissionConfig> configs = commissionConfigDAO.getAllConfigs();

        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (latestConfig != null) {
            req.setAttribute("currentRatePct", latestConfig.getCommissionRate().intValue());
            if (latestConfig.getEffectiveDate() != null) {
                req.setAttribute("currentDate", latestConfig.getEffectiveDate().format(dateFormatter));
            }
        } else {
            req.setAttribute("currentRatePct", 10);
            req.setAttribute("currentDate", "");
        }

        List<java.util.Map<String, Object>> historyList = new java.util.ArrayList<>();
        for (int i = 0; i < configs.size(); i++) {
            CommissionConfig current = configs.get(i);
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("time", current.getCreatedAt() != null ? current.getCreatedAt().format(timeFormatter) : "");
            
            int currentPct = current.getCommissionRate().intValue();
            int prevPct = currentPct;
            if (i < configs.size() - 1) {
                prevPct = configs.get(i + 1).getCommissionRate().intValue();
            }
            
            if (i == configs.size() - 1) {
                map.put("change", currentPct + "% (Khởi tạo)");
            } else {
                map.put("change", prevPct + "% → " + currentPct + "%");
            }
            historyList.add(map);
        }

        req.setAttribute("historyList", historyList);
        req.getRequestDispatcher("/admin/finance/view-finance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String commissionRateStr = req.getParameter("commissionRate");
            String effectiveDateStr = req.getParameter("effectiveDate"); // yyyy-MM-dd

            java.math.BigDecimal rate = new java.math.BigDecimal(commissionRateStr);
            
            java.time.LocalDate date = java.time.LocalDate.parse(effectiveDateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            java.time.LocalDateTime effectiveDate = date.atStartOfDay();

            CommissionConfig latestConfig = commissionConfigDAO.getLatestConfig();
            if (latestConfig == null || latestConfig.getCommissionRate().compareTo(rate) != 0) {
                commissionConfigDAO.insertConfig(rate, effectiveDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/finance/view-finance");
    }
}
