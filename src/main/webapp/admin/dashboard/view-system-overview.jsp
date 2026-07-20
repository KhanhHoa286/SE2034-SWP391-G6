<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- BẪY TỰ ĐỘNG CHẠY SAI: Nếu mở trực tiếp file JSP này, hệ thống tự đẩy về Servlet --%>
<c:if test="${dashboardLoaded == null}">
    <c:redirect url="/admin/dashboard/overview"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Tổng Quan Hệ Thống - MODA Super Admin</title>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        :root {
            --bg-primary: #ffffff;
            --bg-secondary: #ffffff;
            --sidebar-bg: #ffffff;
            --sidebar-text: #4c4546;
            --sidebar-text-hover: #121c28;
            --sidebar-item-active: #000000;
            --sidebar-item-hover: #f5f5f5;
            --text-primary: #121c28;
            --text-secondary: #5c5f60;
            --text-muted: #4c4546;
            --border-color: #e0e0e0;
            --success: #10b981;
            --success-bg: #eefaf1;
            --success-text: #146c2e;
            --danger: #ba1a1a;
            --danger-bg: #fef2f2;
            --danger-text: #b91c1c;
            --warning: #f59e0b;
            --warning-bg: #fffbeb;
            --warning-text: #b45309;
            --info: #3b82f6;
            --info-bg: #eff6ff;
            --info-text: #1d4ed8;
            --shadow-sm: none;
            --shadow-md: none;
            --shadow-lg: none;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar Styles */
        .sidebar-wrapper { width: 280px; background-color: var(--sidebar-bg); flex-shrink: 0; position: sticky; top: 0; height: 100vh; z-index: 100; border-right: 1px solid var(--border-color); }
        .sidebar { display: flex; flex-direction: column; height: 100%; padding: 40px 0 24px 0; justify-content: space-between; }
        .sidebar-header { padding: 0 32px 24px 32px; }
        .sidebar-brand-title { font-size: 24px; font-weight: 700; color: #000000; line-height: 1.25; display: block; }
        .sidebar-subtitle { font-size: 14px; color: var(--text-muted); line-height: 1.5; margin-top: 4px; display: block; text-transform: none; letter-spacing: normal; font-weight: 400; }
        .sidebar-nav-group { display: flex; flex-direction: column; flex: 1; }
        .sidebar-menu { display: flex; flex-direction: column; gap: 0; }
        .menu-item a { display: flex; align-items: center; gap: 16px; padding: 0 32px; height: 56px; color: var(--sidebar-text); transition: background 0.2s ease, color 0.2s ease; }
        .menu-item a:hover { background-color: var(--sidebar-item-hover); color: var(--sidebar-text) !important; }
        .menu-item.active a { color: #ffffff !important; background-color: var(--sidebar-item-active); box-shadow: none; }
        .menu-icon { width: 20px; height: 20px; stroke-width: 2px; flex-shrink: 0; color: inherit; }
        .menu-text { white-space: nowrap; font-size: 12px; font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; color: inherit; }
        .menu-item.active .menu-icon, .menu-item.active .menu-text { color: #ffffff !important; }

        /* Logout border top */
        .sidebar-logout { border-top: 1px solid var(--border-color); margin-top: auto; }
        .sidebar-logout .menu-item a { height: 72px; }
        .sidebar-logout .menu-item a:hover { color: var(--danger) !important; }
        .sidebar-logout .menu-item a:hover .menu-icon, .sidebar-logout .menu-item a:hover .menu-text { color: var(--danger) !important; }

        /* Main Content & Topbar */
        .main-content { flex: 1; padding: 64px; display: flex; flex-direction: column; gap: 48px; overflow-x: hidden; background: var(--bg-primary); }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 16px;
            padding-bottom: 8px;
        }

        .topbar-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
        .topbar-avatar-wrapper { flex-shrink: 0; }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); cursor: pointer; transition: all 0.2s ease; }
        .topbar-avatar:hover { opacity: 0.8; }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
        .header-info h1 { font-size: 40px; font-weight: 700; color: #000000; letter-spacing: -0.02em; margin-bottom: 8px; line-height: 1.15; }
        .header-info p { font-size: 16px; color: var(--text-muted); line-height: 1.5; margin: 0; }
        
        .date-picker-btn { display: inline-flex; align-items: center; justify-content: center; gap: 10px; background-color: #000000; border: 1px solid #000000; padding: 16px 32px; font-size: 12px; font-weight: 700; color: #ffffff; cursor: pointer; transition: opacity 0.2s ease; letter-spacing: 0.1em; text-transform: uppercase; white-space: nowrap; min-height: 52px; }
        .date-picker-btn:hover { opacity: 0.9; }
        .date-picker-container:hover .date-picker-btn { opacity: 0.9; }
        .date-icon { width: 18px; height: 18px; color: #ffffff; }
        .chevron-icon { width: 18px; height: 18px; color: #ffffff; margin-left: 4px; }

        /* Stats Grid */
        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 0; border: 1px solid var(--border-color); background: var(--bg-secondary); }
        .stat-card { border-right: 1px solid var(--border-color); padding: 32px; display: flex; flex-direction: column; justify-content: space-between; gap: 16px; position: relative; overflow: hidden; }
        .stat-card:last-child { border-right: none; }
        .stat-header { display: flex; justify-content: space-between; align-items: center; }
        .stat-title { font-size: 11px; font-weight: 700; color: #000000; text-transform: uppercase; letter-spacing: 0.1em; }
        .stat-icon-wrapper { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: 50%; background-color: #f5f5f5; color: #000000; border: 1px solid var(--border-color); }
        .stat-icon { width: 18px; height: 18px; }
        .stat-body { display: flex; justify-content: space-between; align-items: baseline; line-height: 1.2; margin-top: 10px; }
        .stat-value { font-size: 24px; font-weight: 700; color: #000000; letter-spacing: -0.01em; }
        .trend-badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 8px; border-radius: 9999px; font-size: 12px; font-weight: 600; }
        .trend-badge.positive { background-color: var(--success-bg); color: var(--success-text); }
        .trend-icon { width: 12px; height: 12px; stroke-width: 2.5px; }

        /* Content Card & Chart */
        .dashboard-body-row { display: grid; grid-template-columns: 1fr; gap: 24px; align-items: start; }
        .content-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; display: flex; flex-direction: column; gap: 24px; width: 100%; }
        .card-header-row { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20px; border-bottom: 1px solid var(--border-color); }
        .card-title { font-size: 18px; font-weight: 700; color: #000000; letter-spacing: -0.01em; text-transform: uppercase; }
        .chart-container { position: relative; width: 100%; height: 400px; padding-top: 20px; }

        @media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } .stat-card { border-bottom: 1px solid var(--border-color); } .stat-card:nth-child(2), .stat-card:nth-child(4) { border-right: none; } }
        @media (max-width: 768px) {
            .app-container { flex-direction: column; }
            .sidebar-wrapper { width: 100%; height: auto; position: relative; }
            .sidebar { padding: 24px 0; }
            .main-content { padding: 32px 20px; gap: 32px; }
            .page-header { flex-direction: column; gap: 24px; }
            .date-picker-btn { width: 100%; }
            .stats-grid { grid-template-columns: 1fr; }
            .stat-card { border-right: none; border-bottom: 1px solid var(--border-color); }
            .stat-card:last-child { border-bottom: none; }
        }
    </style>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>

<div class="app-container">
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <div class="sidebar-nav-group">
                <div class="sidebar-header">
                    <span class="sidebar-brand-title">MODA Admin</span>
                    <span class="sidebar-subtitle">Bảng điều khiển Super Admin</span>
                </div>
                <ul class="sidebar-menu">
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/admin/dashboard/overview">
                            <span class="menu-text">Tổng quan</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/user-management">
                            <span class="menu-text">Người dùng</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/seller-management">
                            <span class="menu-text">Người bán</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications">
                            <span class="menu-text">Duyệt đăng ký</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                            <span class="menu-text">Tài chính</span>
                        </a>
                    </li>
                </ul>
            </div>
            <div class="sidebar-logout">
                <ul class="sidebar-menu">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/logout">
                            <span class="menu-text">Đăng xuất</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </aside>

    <main class="main-content">


        <section class="page-header">
            <div class="header-info">
                <h1>Tổng quan hệ thống</h1>
                <p>Dữ liệu thời gian thực cho <c:choose><c:when test="${dateLabel == 'Hôm nay'}">ngày hôm nay</c:when><c:otherwise>ngày ${dateLabel}</c:otherwise></c:choose></p>
            </div>
            <div class="date-picker-container" style="position: relative; display: inline-block;">
                <input type="date" id="dashboard-date-input" value="${selectedDate}" 
                       style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; opacity: 0; pointer-events: none; z-index: -1;" 
                       onchange="updateDashboardDate(this.value)" />
                <button class="date-picker-btn" onclick="triggerDatePicker()" style="position: relative;">
                    <i data-lucide="calendar" class="date-icon"></i>
                    <span>${dateLabel}</span>
                    <i data-lucide="chevron-down" class="chevron-icon"></i>
                </button>
            </div>
        </section>

        <section class="stats-grid">
            <article class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Doanh thu</span>
                    <div class="stat-icon-wrapper"><i data-lucide="credit-card" class="stat-icon"></i></div>
                </div>
                <div class="stat-body">
                    <span class="stat-value">
                        <c:choose>
                            <c:when test="${not empty totalRevenue}">
                                <fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/> VND
                            </c:when>
                            <c:otherwise>3.170.000 VND</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </article>

            <article class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Người dùng mới</span>
                    <div class="stat-icon-wrapper"><i data-lucide="user-plus" class="stat-icon"></i></div>
                </div>
                <div class="stat-body">
                    <span class="stat-value">
                        <c:choose>
                            <c:when test="${not empty newUsers}">
                                <fmt:formatNumber value="${newUsers}" maxFractionDigits="0"/>
                            </c:when>
                            <c:otherwise>7</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </article>

            <article class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Đơn hàng</span>
                    <div class="stat-icon-wrapper"><i data-lucide="shopping-bag" class="stat-icon"></i></div>
                </div>
                <div class="stat-body">
                    <span class="stat-value">
                        <c:choose>
                            <c:when test="${not empty totalOrders}">
                                <fmt:formatNumber value="${totalOrders}" maxFractionDigits="0"/>
                            </c:when>
                            <c:otherwise>7</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </article>

            <article class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">Sản phẩm chờ</span>
                    <div class="stat-icon-wrapper"><i data-lucide="package" class="stat-icon"></i></div>
                </div>
                <div class="stat-body">
                    <span class="stat-value">
                        <c:choose>
                            <c:when test="${not empty pendingProducts}">
                                <c:out value="${pendingProducts}"/>
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </span>
                    <span class="trend-badge positive">
                        <i data-lucide="refresh-cw" class="trend-icon"></i>
                        <span>Realtime</span>
                    </span>
                </div>
            </article>
        </section>

        <section class="dashboard-body-row">
            <article class="content-card">
                <div class="card-header-row">
                    <h2 class="card-title">Biểu đồ Doanh thu theo Cửa hàng</h2>
                </div>
                <div class="chart-container">
                    <canvas id="revenueChartCanvas"></canvas>
                </div>
            </article>
        </section>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof lucide !== 'undefined') { lucide.createIcons(); }
        initShopRevenueChart();
    });

    function updateDashboardDate(val) {
        if (val) {
            window.location.href = "${pageContext.request.contextPath}/admin/dashboard/overview?date=" + val;
        }
    }

    function triggerDatePicker() {
        const dateInput = document.getElementById('dashboard-date-input');
        if (dateInput) {
            if (typeof dateInput.showPicker === 'function') {
                dateInput.showPicker();
            } else {
                dateInput.click();
            }
        }
    }

    let shopChart = null;
    const dbLabels = [];
    const dbShopData = [];

    <c:forEach var="entry" items="${shopChartData}">
    dbLabels.push("${entry.key}");
    dbShopData.push(${entry.value});
    </c:forEach>

    function initShopRevenueChart() {
        const ctx = document.getElementById('revenueChartCanvas');
        if (!ctx) return;

        // Nếu tất cả các giá trị doanh thu bằng 0 hoặc rỗng, hiển thị biểu đồ trống
        const hasData = dbShopData.length > 0 && dbShopData.some(val => val > 0);
        const finalLabels = hasData ? dbLabels : [];
        const finalData = hasData ? dbShopData : [];

        const config = {
            type: 'bar',
            data: {
                labels: finalLabels,
                datasets: [{
                    label: 'Số tiền nhận được (VND)',
                    data: finalData,
                    backgroundColor: [
                        '#5850ec', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6'
                    ],
                    borderRadius: 6,
                    maxBarThickness: 50
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: true, position: 'top' },
                    tooltip: {
                        backgroundColor: '#0f172a',
                        callbacks: {
                            label: function(context) {
                                return ' Đã nhận: ' + context.parsed.y.toLocaleString('vi-VN') + ' VND';
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { color: '#64748b', maxRotation: 30, minRotation: 0 }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(226, 232, 240, 0.6)' },
                        ticks: {
                            color: '#64748b',
                            callback: function(value) { return value.toLocaleString('vi-VN') + ' VND'; }
                        }
                    }
                }
            }
        };

        shopChart = new Chart(ctx, config);
    }
</script>
</body>
</html>
