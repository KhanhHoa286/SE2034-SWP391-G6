<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển người bán - MODA</title>

    <!-- Nhúng file CSS dùng chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">

    <!-- Tải Lucide Icons qua CDN để sử dụng icon hiện đại và sắc nét -->
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
</head>

<body>

<div class="app-container">

    <!-- MAIN LAYOUT (Sidebar trái + Khung nội dung phải) -->
    <div class="main-layout">

        <!-- NHÚNG SIDEBAR TỪ FILE TÁCH RIÊNG (taskbar-seller.jsp) -->
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <!-- KHUNG CHỨA NỘI DUNG VÀ HEADER -->
        <div class="content-container">

            <!-- TOP HEADER (Chỉ chứa Avatar ở góc phải theo mockup mới) -->
            <header class="top-header">
                <div class="profile-section">
                    <span class="profile-name">${shop.shopName}</span>
                    <img src="${not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                         alt="Seller Profile" class="profile-avatar">
                </div>
            </header>

            <!-- VÙNG NỘI DUNG CHÍNH (Scrollable Area) -->
            <main class="content-wrapper">

                <!-- TIÊU ĐỀ TRANG DASHBOARD -->
                <div class="dashboard-header">
                    <div class="dashboard-title-area">
                        <h1 class="page-title">Bảng điều khiển người bán</h1>
                        <p class="page-subtitle"><span id="currentDate">Hôm nay</span></p>
                    </div>
                    <button class="btn-header-action" onclick="location.href='${pageContext.request.contextPath}/seller/product/add'">
                        <i data-lucide="plus"></i>
                        Thêm sản phẩm
                    </button>
                </div>

                <!-- THẺ THỐNG KÊ CHỈ SỐ CHÍNH (Metric Cards Row) -->
                <div class="metrics-row">
                    <!-- Thống kê doanh thu -->
                    <div class="metric-card">
                        <div class="metric-header">
                            <span class="metric-title">Doanh thu hôm nay</span>
                            <span class="metric-trend ${revenueTrend >= 0 ? 'trend-up' : 'trend-down'}">
                                <c:choose>
                                    <c:when test="${revenueTrend >= 0}">+</c:when>
                                </c:choose>
                                <fmt:formatNumber value="${revenueTrend}" maxFractionDigits="1"/>%
                                <i data-lucide="${revenueTrend >= 0 ? 'trending-up' : 'trending-down'}" style="width: 14px; height: 14px;"></i>
                            </span>
                        </div>
                        <span class="metric-value"><fmt:formatNumber value="${todayRevenue}" type="number" maxFractionDigits="0"/>đ</span>
                        <div class="metric-accent-line"></div>
                    </div>

                    <!-- Thống kê đơn hàng mới -->
                    <div class="metric-card">
                        <div class="metric-header">
                            <span class="metric-title">Đơn hàng mới hôm nay</span>
                            <span class="metric-trend ${ordersTrendCount >= 0 ? 'trend-up' : 'trend-down'}">
                                <c:choose>
                                    <c:when test="${ordersTrendCount >= 0}">+</c:when>
                                </c:choose>
                                ${ordersTrendCount}
                                <i data-lucide="${ordersTrendCount >= 0 ? 'trending-up' : 'trending-down'}" style="width: 14px; height: 14px;"></i>
                            </span>
                        </div>
                        <span class="metric-value">${todayNewOrders}</span>
                        <div class="metric-accent-line"></div>
                    </div>
                </div>

                <!-- BỐ CỤC LƯỚI ĐỒ THỊ & SẢN PHẨM BÁN CHẠY -->
                <div class="dashboard-grid">

                    <!-- Cột Trái: Đồ thị hiệu suất doanh thu -->
                    <section class="card" style="padding: 24px;">
                        <div class="chart-card-header">
                            <h3 class="chart-card-title">Hiệu suất doanh thu (7 ngày qua)</h3>
                        </div>

                        <!-- Đồ thị SVG được vẽ động mượt mà và trực quan -->
                        <div class="chart-svg-container">
                            <svg viewBox="0 0 600 250" id="revenueChart">
                                <!-- Sẽ được sinh động bằng JS -->
                            </svg>
                        </div>
                    </section>

                    <!-- Cột Phải: Danh sách sản phẩm bán chạy -->
                    <aside class="best-sellers-card">
                        <h3 class="bestsellers-title">Sản phẩm bán chạy</h3>

                        <div class="bestseller-list">
                            <c:choose>
                                <c:when test="${empty bestsellers}">
                                    <div style="text-align: center; color: #71717a; padding: 48px 0;">Chưa có dữ liệu bán hàng</div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${bestsellers}" var="prod">
                                        <div class="bestseller-item">
                                            <img src="${not empty prod.thumbnailUrl ? prod.thumbnailUrl : 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=150&q=80'}"
                                                 alt="${prod.productName}" class="bestseller-img">
                                            <div class="bestseller-info">
                                                <h4 class="bestseller-title">${prod.productName}</h4>
                                                <span class="bestseller-meta">Đã bán: ${not empty prod.totalSold ? prod.totalSold : 0} &bull; Kho: ${prod.totalStock}</span>
                                                <span class="bestseller-price"><fmt:formatNumber value="${prod.finalPrice}" type="number" maxFractionDigits="0"/>đ</span>
                                            </div>
                                            <div class="bestseller-trend" style="color: #10b981;">
                                                <i data-lucide="trending-up"></i>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <button class="btn-bestsellers-all"
                                onclick="location.href='${pageContext.request.contextPath}/seller/products'">
                            Tất cả sản phẩm
                        </button>
                    </aside>

                </div>

                <!-- BẢNG ĐƠN HÀNG GẦN ĐÂY -->
                <section class="orders-card">
                    <div class="orders-card-header">
                        <h3 class="orders-card-title">Đơn hàng gần đây</h3>
                        <a href="${pageContext.request.contextPath}/seller/orders" class="orders-header-link">
                            Xem tất cả đơn hàng
                        </a>
                    </div>

                    <div class="table-responsive">
                        <table class="orders-table">
                            <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Khách hàng</th>
                                <th>Sản phẩm</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty recentOrders}">
                                    <tr>
                                        <td colspan="5" style="text-align: center; color: #71717a; padding: 24px;">Chưa có đơn hàng nào</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${recentOrders}" var="ord">
                                        <tr>
                                            <td class="order-id">#SUB-${ord.subOrderId}</td>
                                            <td>
                                                <div class="customer-name">${ord.customerName}</div>
                                                <div class="customer-email">${ord.customerEmail}</div>
                                            </td>
                                            <td class="order-product">${ord.productsSummary}</td>
                                            <td class="order-total"><fmt:formatNumber value="${ord.totalAmount}" type="number" maxFractionDigits="0"/>đ</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${ord.status == 'PENDING'}">
                                                        <span class="badge badge-processing" style="background-color: #fef3c7; color: #d97706; border-radius: 4px; padding: 4px 8px;">Chờ xác nhận</span>
                                                    </c:when>
                                                    <c:when test="${ord.status == 'CONFIRMED' || ord.status == 'PREPARING' || ord.status == 'SHIPPING'}">
                                                        <span class="badge badge-processing" style="background-color: #dbeafe; color: #2563eb; border-radius: 4px; padding: 4px 8px;">Đang xử lý</span>
                                                    </c:when>
                                                    <c:when test="${ord.status == 'DELIVERED'}">
                                                        <span class="badge badge-delivered" style="background-color: #d1fae5; color: #059669; border-radius: 4px; padding: 4px 8px;">Đã giao</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-cancelled" style="background-color: #fee2e2; color: #dc2626; border-radius: 4px; padding: 4px 8px;">Đã hủy</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </section>

            </main>

        </div>
    </div>
</div>

<!-- Tác vụ JavaScript khởi chạy các icon và vẽ biểu đồ -->
<script>
    // Khởi tạo các Lucide Icons
    lucide.createIcons();

    // Hiển thị ngày hôm nay tiếng Việt
    const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
    const today = new Date();
    const dayName = days[today.getDay()];
    const dateStr = "Hôm nay là " + dayName + ", ngày " + today.getDate() + " tháng " + (today.getMonth() + 1) + " năm " + today.getFullYear();
    document.getElementById('currentDate').innerText = dateStr;

    // Dữ liệu doanh thu 7 ngày lấy từ Servlet
    const chartData = [
        <c:forEach items="${revenueLast7Days}" var="item" varStatus="loop">
            {
                label: "${item.label}",
                revenue: ${item.revenue}
            }${not loop.last ? ',' : ''}
        </c:forEach>
    ];

    // Vẽ biểu đồ SVG động
    function renderChart(data) {
        const svg = document.getElementById("revenueChart");
        if (!svg || data.length === 0) return;

        svg.innerHTML = ""; // Xóa dữ liệu cũ

        // Tìm doanh thu lớn nhất để scale tỷ lệ biểu đồ (tối thiểu 1,000,000 để tránh lỗi chia 0)
        let maxRevenue = Math.max(...data.map(d => d.revenue), 1000000);
        
        const paddingLeft = 60;
        const paddingRight = 40;
        const paddingTop = 30;
        const paddingBottom = 40;
        const width = 600;
        const height = 250;
        
        const chartWidth = width - paddingLeft - paddingRight;
        const chartHeight = height - paddingTop - paddingBottom;

        // Vẽ các đường lưới ngang và nhãn trục Y (5 mốc)
        for (let i = 0; i <= 4; i++) {
            const y = paddingTop + (chartHeight * i / 4);
            const gridVal = maxRevenue * (4 - i) / 4;
            
            // Đường lưới
            const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
            line.setAttribute("x1", paddingLeft);
            line.setAttribute("y1", y);
            line.setAttribute("x2", width - paddingRight);
            line.setAttribute("y2", y);
            line.setAttribute("stroke", "#e4e4e7");
            line.setAttribute("stroke-width", "1");
            if (i === 4) {
                line.setAttribute("stroke", "#18181b");
                line.setAttribute("stroke-width", "1.5");
            }
            svg.appendChild(line);

            // Chữ trục Y
            const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
            text.setAttribute("x", paddingLeft - 10);
            text.setAttribute("y", y + 4);
            text.setAttribute("text-anchor", "end");
            text.style.fontSize = "10px";
            text.style.fill = "#71717a";
            text.style.fontFamily = "sans-serif";
            
            let labelStr = "";
            if (gridVal >= 1000000) {
                labelStr = (gridVal / 1000000).toFixed(1) + "M";
            } else if (gridVal >= 1000) {
                labelStr = (gridVal / 1000).toFixed(0) + "K";
            } else {
                labelStr = gridVal.toFixed(0);
            }
            text.textContent = labelStr;
            svg.appendChild(text);
        }

        // Tạo danh sách điểm tọa độ và vẽ nhãn trục X
        const points = [];
        data.forEach((d, idx) => {
            const x = paddingLeft + (chartWidth * idx / (data.length - 1));
            const y = (height - paddingBottom) - (chartHeight * d.revenue / maxRevenue);
            points.push({ x, y, label: d.label, revenue: d.revenue });

            // Chữ trục X
            const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
            text.setAttribute("x", x);
            text.setAttribute("y", height - 15);
            text.setAttribute("text-anchor", "middle");
            text.style.fontSize = "10px";
            text.style.fill = "#71717a";
            text.style.fontFamily = "sans-serif";
            text.textContent = d.label;
            svg.appendChild(text);
        });

        // Vẽ đường cong Bezier nối các điểm
        let pathD = "";
        points.forEach((p, idx) => {
            if (idx === 0) {
                pathD += "M " + p.x + "," + p.y;
            } else {
                const prev = points[idx - 1];
                const cpX1 = prev.x + (p.x - prev.x) / 3;
                const cpY1 = prev.y;
                const cpX2 = prev.x + (p.x - prev.x) * 2 / 3;
                const cpY2 = p.y;
                pathD += " C " + cpX1 + "," + cpY1 + " " + cpX2 + "," + cpY2 + " " + p.x + "," + p.y;
            }
        });

        const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.setAttribute("d", pathD);
        path.setAttribute("fill", "none");
        path.setAttribute("stroke", "#1f2937");
        path.setAttribute("stroke-width", "3");
        svg.appendChild(path);

        // Vẽ các chấm tròn tương ứng với các ngày
        points.forEach((p) => {
            const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
            circle.setAttribute("cx", p.x);
            circle.setAttribute("cy", p.y);
            circle.setAttribute("r", "5");
            circle.setAttribute("fill", "#1f2937");
            circle.setAttribute("stroke", "#ffffff");
            circle.setAttribute("stroke-width", "2");
            circle.style.cursor = "pointer";

            // TOOLTIP KHI HOVER VÀO CHẤM TRÒN
            const title = document.createElementNS("http://www.w3.org/2000/svg", "title");
            // Sửa thành cộng chuỗi bằng dấu + để JSP không nhận nhầm thành thẻ EL
            title.textContent = p.label + ": " + new Intl.NumberFormat('vi-VN').format(p.revenue) + "đ";
            circle.appendChild(title);

            svg.appendChild(circle);
        });
    }

    // Thực hiện vẽ biểu đồ
    renderChart(chartData);
</script>
</body>

</html>
