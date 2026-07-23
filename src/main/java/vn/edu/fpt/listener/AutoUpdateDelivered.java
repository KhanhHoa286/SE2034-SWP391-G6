package vn.edu.fpt.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.edu.fpt.dao.OrderDAO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class AutoUpdateDelivered implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //tự chạy khi server đc bật
        scheduler = Executors.newSingleThreadScheduledExecutor();

        //thời gian chờ ban đầu: 0 phút (chạy luôn khi bật server)
        //chu kỳ lặp lại: 1 giờ (cứ 1 tiếng quét 1 lần)
        scheduler.scheduleAtFixedRate(() -> {
            orderDAO.autoUpdateDelevered();
        }, 1, 1, TimeUnit.MINUTES);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Hàm này tự động chạy KHI TẮT SERVER
        if (scheduler != null) {
            scheduler.shutdownNow(); //tatws để dọn ram, tránh memory leak
        }
    }
}
