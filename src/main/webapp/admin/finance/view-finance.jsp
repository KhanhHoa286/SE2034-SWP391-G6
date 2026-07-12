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
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --sidebar-bg: #111827;
            --sidebar-text: #9ca3af;
            --sidebar-text-hover: #ffffff;
            --sidebar-item-active: #5850ec;
            --sidebar-item-hover: #1f2937;
            --text-primary: #1f2937;
            --text-secondary: #4b5563;
            --text-muted: #9ca3af;
            --border-color: #e5e7eb;
            --success: #10b981;
            --success-bg: #d1fae5;
            --success-text: #065f46;
            --danger: #ef4444;
            --danger-bg: #fee2e2;
            --danger-text: #991b1b;
            --warning: #f59e0b;
            --warning-bg: #fef3c7;
            --warning-text: #92400e;
            --info: #3b82f6;
            --info-bg: #dbeafe;
            --info-text: #1e40af;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --sidebar-w: 260px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar */
        .sidebar-wrapper { width: var(--sidebar-w); background-color: var(--sidebar-bg); flex-shrink: 0; position: sticky; top: 0; height: 100vh; z-index: 100; }
        .sidebar { display: flex; flex-direction: column; height: 100%; padding: 24px 16px; }
        .sidebar-brand { padding: 12px 8px 32px 8px; }
        .sidebar-brand-name { font-size: 17px; font-weight: 700; color: #ffffff; letter-spacing: -0.01em; display: block; }
        .sidebar-subtitle { font-size: 11px; color: #4b5563; font-weight: 500; margin-top: 2px; display: block; text-transform: uppercase; letter-spacing: 0.05em; }
        .sidebar-nav { display: flex; flex-direction: column; gap: 6px; flex: 1; }
        .menu-item a { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; color: var(--sidebar-text); font-size: 14px; font-weight: 500; transition: all 0.2s; }
        .menu-item a:hover { color: var(--sidebar-text-hover); background-color: var(--sidebar-item-hover); }
        .menu-item.active a { color: #ffffff; background-color: var(--sidebar-item-active); box-shadow: 0 4px 12px rgba(88, 80, 236, 0.25); }
        .menu-icon { width: 20px; height: 20px; stroke-width: 2px; flex-shrink: 0; }

        /* Main Content */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 20px; overflow-x: hidden; }

        .topbar { display: flex; align-items: center; justify-content: flex-end; padding-bottom: 8px; }
        .admin-profile-badge { display: flex; align-items: center; gap: 8px; background: transparent; border: none; cursor: pointer; }
        .admin-role-text { text-align: right; }
        .admin-role-text .role { font-size: 13px; font-weight: 600; color: var(--text-primary); display: block; }

        /* Breadcrumbs */
        .breadcrumbs { font-size: 12px; color: var(--text-secondary); display: flex; align-items: center; gap: 6px; }
        .breadcrumbs a { color: var(--text-muted); }
        .breadcrumbs a:hover { color: var(--text-primary); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
        .header-title-container { display: flex; align-items: center; gap: 12px; }
        .header-title-container h1 { font-size: 24px; font-weight: 700; color: var(--text-primary); }
        .header-badge { background-color: #d1fae5; color: #065f46; font-size: 11px; font-weight: 700; padding: 4px 8px; border-radius: 4px; text-transform: uppercase; }

        /* Split Section Row */
        .details-row { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }

        /* Form Card */
        .form-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 8px; padding: 24px; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; gap: 20px; }
        .form-card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 12px; }
        .form-card-title { font-size: 16px; font-weight: 700; color: var(--text-primary); }
        .form-group { display: flex; flex-direction: column; gap: 8px; }
        .form-label { font-size: 13px; font-weight: 600; color: var(--text-primary); }
        .input-with-suffix { display: flex; align-items: center; border: 1px solid var(--border-color); border-radius: 6px; background-color: #ffffff; padding-right: 12px; width: 100%; max-width: 320px;}
        .input-with-suffix input { border: none; outline: none; padding: 10px 14px; font-size: 14px; font-weight: 600; width: 100%; border-radius: 6px 0 0 6px; }
        .input-suffix { font-size: 14px; font-weight: 600; color: var(--text-secondary); }
        
        .market-rate-hint { font-size: 12px; color: var(--text-secondary); align-self: center; margin-left: 12px; font-style: italic; }
        
        .date-input-wrapper { display: flex; align-items: center; border: 1px solid var(--border-color); border-radius: 6px; background-color: #f9fafb; padding-right: 12px; width: 100%; max-width: 320px; }
        .date-input-wrapper input { border: none; outline: none; padding: 10px 14px; font-size: 14px; width: 100%; background: transparent; cursor: pointer; color: var(--text-primary); font-weight: 500;}

        /* Alert Box */
        .alert-box { background-color: #f0f4f8; border-left: 4px solid var(--sidebar-item-active); padding: 16px; border-radius: 4px; display: flex; flex-direction: column; gap: 6px; }
        .alert-title { font-size: 13px; font-weight: 700; color: var(--text-primary); }
        .alert-desc { font-size: 12px; color: var(--text-secondary); line-height: 1.6; }

        /* Button group */
        .btn-group { display: flex; gap: 12px; margin-top: 10px; }
        .btn-primary { background-color: #000000; color: #ffffff; padding: 10px 20px; border: none; border-radius: 6px; font-size: 13px; font-weight: 700; cursor: pointer; transition: opacity 0.2s; }
        .btn-primary:hover { opacity: 0.9; }
        .btn-outline { background-color: #ffffff; color: var(--text-primary); padding: 10px 20px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-outline:hover { background-color: #f9fafb; }

        /* Right Panel Cards */
        .panel-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 8px; padding: 20px; box-shadow: var(--shadow-sm); margin-bottom: 20px; }
        .panel-card:last-child { margin-bottom: 0; }
        .panel-card-title { font-size: 12px; font-weight: 700; color: var(--text-secondary); border-bottom: 1px solid var(--border-color); padding-bottom: 10px; margin-bottom: 14px; text-transform: uppercase; letter-spacing: 0.05em; }

        .sim-item { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 12px; }
        .sim-item:last-child { margin-bottom: 0; border-top: 1px solid var(--border-color); padding-top: 12px; margin-top: 4px; }
        .sim-label { color: var(--text-secondary); }
        .sim-value { font-weight: 600; color: var(--text-primary); }
        .sim-value.highlight { color: var(--sidebar-item-active); }

        /* Graphic Card */
        .graphic-card { background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=400&auto=format&fit=crop&q=60') no-repeat center center; background-size: cover; color: #ffffff; display: flex; flex-direction: column; justify-content: flex-end; min-height: 140px; padding: 20px; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 20px; }
        .graphic-title { font-size: 14px; font-weight: 700; margin-bottom: 4px; }
        .graphic-desc { font-size: 11px; color: #e5e7eb; line-height: 1.4; }

        /* Security Card Specifics */
        .security-list { display: flex; flex-direction: column; gap: 12px; font-size: 12px; }
        .security-item { display: flex; gap: 10px; align-items: flex-start; line-height: 1.5; color: var(--text-secondary); }
        .security-icon { color: var(--sidebar-item-active); flex-shrink: 0; margin-top: 2px; }

        /* Bottom History Table */
        .history-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 8px; padding: 20px; box-shadow: var(--shadow-sm); }
        .history-card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-color); padding-bottom: 12px; margin-bottom: 16px; }
        .history-card-title { font-size: 14px; font-weight: 700; color: var(--text-primary); }
        .history-link { font-size: 12px; font-weight: 600; color: var(--sidebar-item-active); }
        .history-link:hover { text-decoration: underline; }

        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13px; }
        .custom-table th { background-color: #f8fafc; color: var(--text-secondary); font-weight: 600; padding: 12px 20px; border-bottom: 1px solid var(--border-color); text-transform: uppercase; font-size: 11px; letter-spacing: 0.05em; }
        .custom-table td { padding: 16px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        
        .tag-success { background-color: var(--success-bg); color: var(--success-text); font-size: 10px; font-weight: 700; padding: 4px 8px; border-radius: 4px; text-transform: uppercase; display: inline-block;}
    </style>
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <div class="sidebar-brand" style="padding-bottom: 24px;">
                <span class="sidebar-brand-name" style="font-size: 1.25rem; font-weight: 700; color: #ffffff;">MODA Admin</span>
                <span class="sidebar-subtitle" style="display: block; font-size: 0.75rem; color: #9ca3af; margin-top: 4px;">Bảng điều khiển siêu cấp</span>
            </div>
            <ul class="sidebar-nav">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/dashboard/overview">
                        <i data-lucide="layout-dashboard" class="menu-icon"></i>
                        <span>Tổng quan</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/user-management">
                        <i data-lucide="users" class="menu-icon"></i>
                        <span>Người dùng</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/seller-applications">
                        <i data-lucide="store" class="menu-icon"></i>
                        <span>Người bán</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/orders">
                        <i data-lucide="shopping-cart" class="menu-icon"></i>
                        <span>Đơn hàng hệ thống</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/products">
                        <i data-lucide="package" class="menu-icon"></i>
                        <span>Danh sách sản phẩm</span>
                    </a>
                </li>
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                        <i data-lucide="credit-card" class="menu-icon"></i>
                        <span>Tài chính</span>
                    </a>
                </li>
            </ul>
            <div style="margin-top: auto;">
                <ul class="sidebar-nav">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/logout">
                            <i data-lucide="log-out" class="menu-icon"></i>
                            <span>Đăng xuất</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </aside>

    <main class="main-content">
        <div class="topbar">
            <button class="admin-profile-badge">
                <i data-lucide="user-circle" style="width:24px; height:24px; color: var(--text-secondary); margin-right:6px;"></i>
                <div class="admin-role-text">
                    <span class="role">Hồ sơ Admin</span>
                </div>
            </button>
        </div>

        <nav class="breadcrumbs">
            <a href="#">Tài chính</a>
            <i data-lucide="chevron-right" style="width: 12px; height: 12px; color: var(--text-muted);"></i>
            <span>Cấu hình tỷ lệ chiết khấu</span>
        </nav>

        <section class="page-header">
            <div class="header-title-container">
                <h1>Cấu hình tỷ lệ chiết khấu</h1>
                <span class="header-badge">LIVE ENVIRONMENT</span>
            </div>
        </section>
        <p style="font-size:14px; color: var(--text-secondary); margin-top:-14px; margin-bottom:10px;">Thiết lập mức hoa hồng áp dụng cho các nhà bán hàng trên nền tảng MODA.</p>

        <section class="details-row">
            <!-- Left panel: Form configuration -->
            <div class="form-card">
                <div class="form-card-header">
                    <span class="form-card-title">Biểu mẫu cập nhật</span>
                    <i data-lucide="info" style="width:18px; height:18px; color: var(--text-muted); cursor:pointer;"></i>
                </div>

                <div class="form-group">
                    <label class="form-label">Tỷ lệ hoa hồng hiện tại</label>
                    <div style="display: flex; align-items: center;">
                        <div class="input-with-suffix">
                            <input type="text" value="10">
                            <span class="input-suffix">%</span>
                        </div>
                        <span class="market-rate-hint">* Mức trung bình thị trường: 8% - 12%</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Áp dụng từ ngày</label>
                    <div class="date-input-wrapper">
                        <input type="text" value="05/20/2024">
                        <i data-lucide="calendar" style="width:16px; height:16px; color: var(--text-secondary); cursor:pointer;"></i>
                    </div>
                </div>

                <div class="alert-box">
                    <span class="alert-title">Tác động hệ thống:</span>
                    <span class="alert-desc">Việc thay đổi tỷ lệ chiết khấu sẽ ảnh hưởng trực tiếp đến thu nhập ròng của người bán và doanh thu của sàn. Mức phí mới sẽ được tự động tính toán cho tất cả các đơn hàng được tạo <strong>**sau thời điểm cập nhật**</strong>.</span>
                </div>

                <div class="btn-group">
                    <button class="btn-primary">Cập nhật cấu hình</button>
                    <button class="btn-outline">Hủy bỏ</button>
                </div>
            </div>

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
                        <span class="sim-label">Hoa hồng sàn (10%):</span>
                        <span class="sim-value highlight">+ 100.000 đ</span>
                    </div>
                    <div class="sim-item">
                        <span class="sim-label">Người bán thực nhận:</span>
                        <span class="sim-value">900.000 đ</span>
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
                    <th>Người thực hiện</th>
                    <th>Thay đổi</th>
                    <th>Trạng thái</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>20/04/2024 14:30</td>
                    <td style="font-weight: 500;">Admin_DungNT</td>
                    <td style="font-weight: 600;">8% → 10%</td>
                    <td><span class="tag-success">Hoàn tất</span></td>
                </tr>
                <tr>
                    <td>01/01/2024 09:00</td>
                    <td style="font-weight: 500;">System_Auto</td>
                    <td style="font-weight: 600;">10% → 8% (Khuyến mãi Tết)</td>
                    <td><span class="tag-success">Hoàn tất</span></td>
                </tr>
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
