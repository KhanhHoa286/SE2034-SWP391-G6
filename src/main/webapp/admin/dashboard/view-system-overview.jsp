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
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --sidebar-bg: #111827;
            --sidebar-text: #9ca3af;
            --sidebar-text-hover: #ffffff;
            --sidebar-item-active: #5850ec;
            --sidebar-item-hover: #1f2937;
            --text-primary: #0f172a;
            --text-secondary: #475569;
            --text-muted: #64748b;
            --border-color: #e2e8f0;
            --success: #10b981;
            --success-bg: #ecfdf5;
            --success-text: #047857;
            --danger: #ef4444;
            --danger-bg: #fef2f2;
            --danger-text: #b91c1c;
            --warning: #f59e0b;
            --warning-bg: #fffbeb;
            --warning-text: #b45309;
            --info: #3b82f6;
            --info-bg: #eff6ff;
            --info-text: #1d4ed8;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02);
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar Styles */
        .sidebar-wrapper { width: 260px; background-color: var(--sidebar-bg); flex-shrink: 0; position: sticky; top: 0; height: 100vh; z-index: 100; }
        .sidebar { display: flex; flex-direction: column; height: 100%; padding: 24px 16px; justify-content: space-between; }
        .sidebar-header { padding: 12px 8px 32px 8px; }
        .sidebar-brand-title { font-size: 17px; font-weight: 700; color: #ffffff; letter-spacing: -0.01em; display: block; }
        .sidebar-subtitle { font-size: 11px; color: #4b5563; font-weight: 500; margin-top: 2px; display: block; text-transform: uppercase; letter-spacing: 0.05em; }
        .sidebar-nav-group { display: flex; flex-direction: column; gap: 16px; flex: 1; }
        .sidebar-menu { display: flex; flex-direction: column; gap: 6px; }
        .menu-item a { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; color: var(--sidebar-text); font-size: 14px; font-weight: 500; transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); }
        .menu-item a:hover { color: var(--sidebar-text-hover); background-color: var(--sidebar-item-hover); }
        .menu-item.active a { color: #ffffff; background-color: var(--sidebar-item-active); box-shadow: 0 4px 12px rgba(88, 80, 236, 0.25); }
        .menu-icon { width: 20px; height: 20px; stroke-width: 2px; flex-shrink: 0; }
        .menu-text { white-space: nowrap; }

        /* Main Content & Topbar */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }
        .topbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding-bottom: 8px; }
        .topbar-search { flex: 1; max-width: 560px; position: relative; }
        .topbar-search-icon { position: absolute; left: 16px; top: 50%; transform: translateY(-50%); width: 18px; height: 18px; color: var(--text-muted); pointer-events: none; }
        .topbar-search-input { width: 100%; padding: 12px 16px 12px 48px; border: 1px solid var(--border-color); border-radius: 10px; font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: var(--bg-secondary); box-shadow: var(--shadow-sm); transition: all 0.2s ease; outline: none; }
        .topbar-search-input::placeholder { color: var(--text-muted); }
        .topbar-search-input:focus { border-color: var(--sidebar-item-active); box-shadow: 0 0 0 3px rgba(88, 80, 236, 0.1); }
        .topbar-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
        .topbar-avatar-wrapper { flex-shrink: 0; }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); box-shadow: var(--shadow-sm); cursor: pointer; transition: all 0.2s ease; }
        .topbar-avatar:hover { border-color: var(--sidebar-item-active); box-shadow: 0 0 0 3px rgba(88, 80, 236, 0.1); transform: scale(1.05); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
        .header-info h1 { font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.02em; margin-bottom: 4px; }
        .header-info p { font-size: 14px; color: var(--text-muted); }
        .date-picker-btn { display: flex; align-items: center; gap: 8px; background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 8px; padding: 10px 16px; font-size: 14px; font-weight: 500; color: var(--text-primary); cursor: pointer; transition: all 0.2s ease; box-shadow: var(--shadow-sm); }
        .date-picker-btn:hover { background-color: #f8fafc; border-color: #cbd5e1; }
        .date-icon { width: 16px; height: 16px; color: var(--text-muted); }
        .chevron-icon { width: 16px; height: 16px; color: var(--text-muted); margin-left: 4px; }

        /* Stats Grid */
        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
        .stat-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; justify-content: space-between; gap: 16px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); position: relative; overflow: hidden; }
        .stat-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); border-color: #cbd5e1; }
        .stat-header { display: flex; justify-content: space-between; align-items: center; }
        .stat-title { font-size: 12px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
        .stat-icon-wrapper { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: 8px; background-color: #f8fafc; color: var(--text-muted); }
        .stat-icon { width: 20px; height: 20px; }
        .stat-body { display: flex; justify-content: space-between; align-items: baseline; line-height: 1.2; }
        .stat-value { font-size: 24px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.03em; }
        .trend-badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 8px; border-radius: 9999px; font-size: 12px; font-weight: 600; }
        .trend-badge.positive { background-color: var(--success-bg); color: var(--success-text); }
        .trend-icon { width: 12px; height: 12px; stroke-width: 2.5px; }

        /* Content Card & Chart */
        .dashboard-body-row { display: grid; grid-template-columns: 1fr; gap: 24px; align-items: start; }
        .content-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); padding: 24px; display: flex; flex-direction: column; gap: 20px; width: 100%; }
        .card-header-row { display: flex; justify-content: space-between; align-items: center; }
        .card-title { font-size: 18px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.01em; }
        .chart-container { position: relative; width: 100%; height: 380px; }

        @media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
        @media (max-width: 768px) { .app-container { flex-direction: column; } .sidebar-wrapper { width: 100%; height: auto; } .sidebar { padding: 16px; } .sidebar-header { padding-bottom: 16px; } .main-content { padding: 16px; } .topbar { flex-direction: column; align-items: stretch; } .topbar-search { max-width: 100%; } .topbar-avatar-wrapper { display: flex; justify-content: flex-end; } .page-header { flex-direction: column; gap: 16px; } .date-picker-btn { width: 100%; justify-content: center; } }
        @media (max-width: 480px) { .stats-grid { grid-template-columns: 1fr; } .stat-card { padding: 16px; } }
    </style>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <!-- SIDEBAR CHUẨN ĐỒNG BỘ -->
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
                            <i data-lucide="layout-dashboard" class="menu-icon"></i>
                            <span class="menu-text">Tổng quan</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/user-management">
                            <i data-lucide="users" class="menu-icon"></i>
                            <span class="menu-text">Người dùng</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/seller_mgt/view-seller-list.jsp">
                            <i data-lucide="home" class="menu-icon"></i>
                            <span class="menu-text">Người bán</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/order_mgt/view-global-orders.jsp">
                            <i data-lucide="globe" class="menu-icon"></i>
                            <span class="menu-text">Đơn hàng quốc tế</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                            <i data-lucide="credit-card" class="menu-icon"></i>
                            <span class="menu-text">Tài chính</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </aside>

    <!-- PHẦN NỘI DUNG CHÍNH -->
    <main class="main-content">
        <!-- TOPBAR -->
        <div class="topbar">
            <div class="topbar-search">
                <i data-lucide="search" class="topbar-search-icon"></i>
                <input type="text" class="topbar-search-input" placeholder="Tìm kiếm nhanh hệ thống...">
            </div>
            <div class="topbar-actions">
                <div class="topbar-avatar-wrapper">
                    <c:choose>
                        <c:when test="${sessionScope.account != null}">
                            <img src="${sessionScope.account.avatarUrl}" alt="Avatar" class="topbar-avatar" />
                        </c:when>
                        <c:otherwise>
                            <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="Avatar" class="topbar-avatar" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- HEADER TIÊU ĐỀ -->
        <section class="page-header">
            <div class="header-info">
                <h1>Tổng quan hệ thống</h1>
                <p>Dữ liệu thời gian thực cho ngày hôm nay</p>
            </div>
            <button class="date-picker-btn">
                <i data-lucide="calendar" class="date-icon"></i>
                <span>Hôm nay</span>
                <i data-lucide="chevron-down" class="chevron-icon"></i>
            </button>
        </section>

        <!-- THẺ THỐNG KÊ (4 Ô SẾP HÀNG NGANG) -->
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
                                <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="$" maxFractionDigits="0"/>
                            </c:when>
                            <c:otherwise>$3.170.000</c:otherwise>
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

        <!-- BIỂU ĐỒ -->
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

        const finalLabels = dbLabels.length > 0 ? dbLabels : ['Shop GuThờiTrang', 'Moda Boutique', 'Gentleman Store', 'GenZ Closet'];
        const finalData = dbShopData.length > 0 ? dbShopData : [430000, 280000, 120000, 50000];

        const config = {
            type: 'bar',
            data: {
                labels: finalLabels,
                datasets: [{
                    label: 'Số tiền nhận được ($)',
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
                                return ' Đã nhận: $' + context.parsed.y.toLocaleString('en-US');
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
                            callback: function(value) { return '$' + value.toLocaleString('en-US'); }
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