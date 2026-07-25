<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <!-- MODA Admin hiển thị đầu thanh taskbar -->
    <title>MODA Admin - Cấu hình hoa hồng</title>

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
            --info-bg: #dbeafe;
            --info-text: #1e40af;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            --shadow-sm: none;
            --sidebar-w: 280px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }
        button, input, select { font-family: inherit; outline: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar Styles */
        .sidebar-wrapper { width: var(--sidebar-w); background-color: var(--sidebar-bg); flex-shrink: 0; position: sticky; top: 0; height: 100vh; z-index: 100; border-right: 1px solid var(--border-color); }
        .sidebar { display: flex; flex-direction: column; height: 100%; padding: 40px 0 24px 0; justify-content: space-between; }
        .sidebar-brand { padding: 0 32px 24px 32px; }
        .sidebar-brand-name { font-size: 24px; font-weight: 700; color: #000000; line-height: 1.25; display: block; }
        .sidebar-subtitle { font-size: 14px; color: var(--text-muted); line-height: 1.5; margin-top: 4px; display: block; text-transform: none; letter-spacing: normal; font-weight: 400; }
        .sidebar-nav { display: flex; flex-direction: column; gap: 0; flex: 1; }
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

        /* Main Content */
        .main-content { flex: 1; padding: 64px; display: flex; flex-direction: column; gap: 32px; overflow-x: hidden; background: var(--bg-primary); }

        .topbar { display: flex; align-items: center; justify-content: flex-end; padding-bottom: 8px; }
        .admin-profile-badge { display: flex; align-items: center; gap: 8px; background: transparent; border: none; cursor: pointer; }
        .admin-role-text { text-align: right; }
        .admin-role-text .role { font-size: 13px; font-weight: 600; color: var(--text-primary); display: block; }

        /* Breadcrumbs */
        .breadcrumbs { font-size: 12px; color: var(--text-secondary); display: flex; align-items: center; gap: 6px; }
        .breadcrumbs a { color: var(--text-muted); }
        .breadcrumbs a:hover { color: var(--text-primary); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0px; }
        .header-title-container { display: flex; align-items: center; gap: 12px; }
        .header-title-container h1 { font-size: 40px; font-weight: 700; color: #000000; letter-spacing: -0.02em; line-height: 1.15; }
        .header-badge { border: 1px solid #000000; background-color: #ffffff; color: #000000; font-size: 11px; font-weight: 700; padding: 4px 8px; text-transform: uppercase; border-radius: 0;}

        /* Split Section Row */
        .details-row { display: grid; grid-template-columns: 2fr 1fr; gap: 32px; }

        /* Form Card */
        .form-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; display: flex; flex-direction: column; gap: 24px; border-radius: 0; box-shadow: none; }
        .form-card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 12px; }
        .form-card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); }
        .form-group { display: flex; flex-direction: column; gap: 8px; }
        .form-label { font-size: 13px; font-weight: 600; color: #000000; text-transform: uppercase; letter-spacing: 0.05em; }
        
        .input-with-suffix { display: flex; align-items: center; border: 1px solid var(--border-color); background-color: #ffffff; padding-right: 12px; width: 100%; max-width: 320px; transition: border-color 0.2s; border-radius: 0;}
        .input-with-suffix:focus-within { border-color: #000000; }
        .input-with-suffix input { border: none; outline: none; padding: 14px 14px; font-size: 14px; font-weight: 600; width: 100%; background: transparent; border-radius: 0; }
        .input-suffix { font-size: 14px; font-weight: 600; color: var(--text-secondary); }
        
        .market-rate-hint { font-size: 12px; color: var(--text-secondary); align-self: center; margin-left: 12px; font-style: italic; }
        
        .date-input-wrapper { display: flex; align-items: center; border: 1px solid var(--border-color); background-color: #ffffff; padding-right: 12px; width: 100%; max-width: 320px; transition: border-color 0.2s; border-radius: 0;}
        .date-input-wrapper:focus-within { border-color: #000000; }
        .date-input-wrapper input { border: none; outline: none; padding: 14px 14px; font-size: 14px; width: 100%; background: transparent; cursor: pointer; color: var(--text-primary); font-weight: 500; border-radius: 0;}

        /* Alert Box */
        .alert-box { background-color: #f1f5f9; border-left: 4px solid #000000; padding: 24px; display: flex; flex-direction: column; gap: 8px; border-radius: 0;}
        .alert-title { font-size: 13px; font-weight: 700; color: #000000; text-transform: uppercase; letter-spacing: 0.05em; }
        .alert-desc { font-size: 13px; color: var(--text-secondary); line-height: 1.6; }

        /* Button group */
        .btn-group { display: flex; gap: 12px; margin-top: 10px; }
        .btn-primary { background-color: #000000; color: #ffffff; padding: 14px 24px; border: 1px solid #000000; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; border-radius: 0;}
        .btn-primary:hover { background-color: #333333; }
        .btn-outline { background-color: #ffffff; color: #000000; padding: 14px 24px; border: 1px solid var(--border-color); font-size: 14px; font-weight: 600; cursor: pointer; transition: border-color 0.2s; border-radius: 0;}
        .btn-outline:hover { border-color: #000000; }

        /* Right Panel Cards */
        .panel-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; margin-bottom: 24px; border-radius: 0; box-shadow: none;}
        .panel-card:last-child { margin-bottom: 0; }
        .panel-card-title { font-size: 13px; font-weight: 700; color: #000000; border-bottom: 2px solid var(--border-color); padding-bottom: 12px; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 0.05em; }

        .sim-item { display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 16px; }
        .sim-item:last-child { margin-bottom: 0; border-top: 1px solid var(--border-color); padding-top: 16px; margin-top: 4px; }
        .sim-label { color: var(--text-secondary); }
        .sim-value { font-weight: 700; color: var(--text-primary); }
        .sim-value.highlight { color: #000000; }

        /* Graphic Card */
        .graphic-card { background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=400&auto=format&fit=crop&q=60') no-repeat center center; background-size: cover; color: #ffffff; display: flex; flex-direction: column; justify-content: flex-end; min-height: 180px; padding: 24px; border: 1px solid var(--border-color); margin-bottom: 24px; border-radius: 0;}
        .graphic-title { font-size: 16px; font-weight: 700; margin-bottom: 8px; letter-spacing: 0.02em; }
        .graphic-desc { font-size: 13px; color: #e5e7eb; line-height: 1.5; }

        /* Security Card Specifics */
        .security-list { display: flex; flex-direction: column; gap: 16px; font-size: 13px; }
        .security-item { display: flex; gap: 12px; align-items: flex-start; line-height: 1.5; color: var(--text-secondary); }
        .security-icon { color: #000000; flex-shrink: 0; margin-top: 2px; }

        /* Bottom History Table */
        .history-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; border-radius: 0; box-shadow: none;}
        .history-card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid var(--border-color); padding-bottom: 16px; margin-bottom: 24px; }
        .history-card-title { font-size: 16px; font-weight: 700; color: #000000; text-transform: uppercase; letter-spacing: 0.05em;}
        .history-link { font-size: 13px; font-weight: 600; color: #000000; text-decoration: underline;}
        .history-link:hover { opacity: 0.7; }

        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #ffffff; color: #000000; font-weight: 700; padding: 20px 24px; border-bottom: 2px solid var(--border-color); text-transform: uppercase; font-size: 12px; letter-spacing: 0.1em; }
        .custom-table td { padding: 24px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        .custom-table tr:hover td { background-color: #f9f9f9; }
        
        .tag-success { background-color: var(--success-bg); color: var(--success-text); font-size: 11px; font-weight: 700; padding: 4px 8px; text-transform: uppercase; display: inline-block; border-radius: 0;}
    </style>
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <div>
                <div class="sidebar-brand">
                    <span class="sidebar-brand-name">MODA Admin</span>
                    <span class="sidebar-subtitle">Bảng điều khiển siêu cấp</span>
                </div>
                <ul class="sidebar-nav">
                    <li class="menu-item">
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
                        <a href="${pageContext.request.contextPath}/admin/shop-management">
                            <span class="menu-text">Danh sách Shop</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications">
                            <span class="menu-text">Duyệt đăng ký</span>
                        </a>
                    </li>
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                            <span class="menu-text">Tài chính</span>
                        </a>
                    </li>
                </ul>
            </div>
            <div class="sidebar-logout">
                <ul class="sidebar-nav">
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
            <div class="header-title-container">
                <h1>Cấu hình tỷ lệ chiết khấu</h1>
                <span class="header-badge">LIVE ENVIRONMENT</span>
            </div>
        </section>
        <p style="font-size:14px; color: var(--text-secondary); margin-top:-14px; margin-bottom:10px;">Thiết lập mức hoa hồng áp dụng cho các nhà bán hàng trên nền tảng MODA.</p>

        <section class="details-row">
            <!-- Left panel: Form configuration -->
            <form action="${pageContext.request.contextPath}/admin/finance/view-finance" method="post" class="form-card">


                <div class="form-group">
                    <label class="form-label">Tỷ lệ hoa hồng mới</label>
                    <div style="display: flex; align-items: center;">
                        <div class="input-with-suffix">
                            <input type="number" step="0.1" name="commissionRate" value="${currentRatePct}" required>
                            <span class="input-suffix">%</span>
                        </div>
                        <span class="market-rate-hint">* Mức trung bình thị trường: 8% - 12%</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Áp dụng từ ngày</label>
                    <div class="date-input-wrapper">
                        <input type="date" name="effectiveDate" value="${currentDate}" required>
                    </div>
                </div>

                <div class="alert-box">
                    <span class="alert-title">Tác động hệ thống:</span>
                    <span class="alert-desc">Việc thay đổi tỷ lệ chiết khấu sẽ ảnh hưởng trực tiếp đến thu nhập ròng của người bán và doanh thu của sàn. Mức phí mới sẽ được tự động tính toán cho tất cả các đơn hàng được tạo <strong>**sau thời điểm cập nhật**</strong>.</span>
                </div>

                <div class="btn-group">
                    <button type="submit" class="btn-primary">Cập nhật cấu hình</button>
                    <button type="button" class="btn-outline">Hủy bỏ</button>
                </div>
            </form>

            <!-- Right panel: Simulation & Info -->
            <div>
                <!-- Mô phỏng doanh thu -->
                <div class="panel-card">
                    <div class="panel-card-title">Mô phỏng doanh thu</div>
                    <div class="sim-item">
                        <span class="sim-label">Giá trị đơn hàng mẫu:</span>
                        <span class="sim-value">1.000.000 đ</span>
                    </div>
                    <div class="sim-item">
                        <span class="sim-label">Hoa hồng sàn (${currentRatePct}%):</span>
                        <span class="sim-value highlight">+ <fmt:formatNumber value="${1000000 * currentRatePct / 100}" pattern="#,###"/> đ</span>
                    </div>
                    <div class="sim-item">
                        <span class="sim-label">Người bán thực nhận:</span>
                        <span class="sim-value"><fmt:formatNumber value="${1000000 * (100 - currentRatePct) / 100}" pattern="#,###"/> đ</span>
                    </div>
                </div>

                <!-- Graphic Card -->
                <div class="graphic-card">
                    <span class="graphic-title">Hiệu suất tài chính Q1</span>
                    <span class="graphic-desc">Tỷ lệ chiết khấu ổn định giúp tăng trưởng 12% số lượng sellers mới.</span>
                </div>

                <!-- Lưu ý bảo mật -->
                <div class="panel-card">
                    <div class="panel-card-title">Lưu ý bảo mật</div>
                    <div class="security-list">
                        <div class="security-item">
                            <i data-lucide="shield" class="security-icon" style="width:16px; height:16px;"></i>
                            <span>Mọi thay đổi đều được ghi lại trong nhật ký hệ thống (Audit Log).</span>
                        </div>
                        <div class="security-item">
                            <i data-lucide="mail" class="security-icon" style="width:16px; height:16px;"></i>
                            <span>Thông báo sẽ được gửi tự động đến tất cả đối tác Sellers.</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Lịch sử thay đổi gần đây -->
        <section class="history-card">
            <div class="history-card-header">
                <span class="history-card-title">Lịch sử thay đổi gần đây</span>
                <a href="#" class="history-link">Xem tất cả</a>
            </div>
            <table class="custom-table">
                <thead>
                <tr>
                    <th>Thời gian</th>
                    <th>Thay đổi</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="history" items="${historyList}">
                    <tr>
                        <td>${history.time}</td>
                        <td style="font-weight: 600;">${history.change}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
        }
    });
</script>
</body>
</html>
