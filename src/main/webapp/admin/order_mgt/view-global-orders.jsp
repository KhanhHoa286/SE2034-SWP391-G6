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
    <title>Quản lý Đơn Hàng Quốc Tế - MODA Admin</title>

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
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --sidebar-w: 260px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar Styles */
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
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 16px;
            padding-bottom: 8px;
        }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); box-shadow: var(--shadow-sm); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; }
        .header-info h1 { font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.02em; margin-bottom: 4px; }
        .header-info p { font-size: 14px; color: var(--text-muted); }

        /* Filter Box */
        .filter-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; box-shadow: var(--shadow-sm); }
        .filter-form { display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end; }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-group label { font-size: 12px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.03em; }
        .form-input, .form-select { min-width: 200px; padding: 10px 14px; border: 1px solid var(--border-color); border-radius: 8px; font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: #ffffff; outline: none; transition: border-color 0.2s ease; }
        .btn-secondary { background-color: #f1f5f9; color: var(--text-primary); padding: 10px 16px; border-radius: 8px; border: 1px solid var(--border-color); font-size: 14px; font-weight: 500; cursor: pointer; display: inline-flex; align-items: center; gap: 6px; transition: all 0.2s ease; }
        .btn-secondary:hover { background-color: #e2e8f0; }

        /* Table Design */
        .table-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); overflow: hidden; display: flex; flex-direction: column; }
        .table-responsive { overflow-x: auto; width: 100%; }
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #f8fafc; color: var(--text-secondary); font-weight: 600; padding: 14px 20px; border-bottom: 1px solid var(--border-color); text-transform: uppercase; font-size: 12px; letter-spacing: 0.05em; }
        .custom-table td { padding: 16px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        .custom-table tr:hover td { background-color: #f8fafc; }

        .order-id-link { font-weight: 600; color: var(--sidebar-item-active); }
        .order-id-link:hover { opacity: 0.8; }

        /* Badges */
        .badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 9999px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.02em; }
        .badge.status-success { background-color: var(--success-bg); color: var(--success-text); }
        .badge.status-delivering { background-color: var(--delivering-bg); color: var(--delivering-text); }
        .badge.status-canceled { background-color: var(--danger-bg); color: var(--danger-text); }
        .badge.status-pending { background-color: var(--warning-bg); color: var(--warning-text); }

        /* Actions */
        .actions-cell { display: flex; gap: 8px; }
        .btn-icon { width: 32px; height: 32px; border-radius: 6px; display: flex; align-items: center; justify-content: center; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-secondary); cursor: pointer; transition: all 0.2s ease; }
        .btn-icon:hover { background-color: #f8fafc; color: var(--sidebar-item-active); border-color: #cbd5e1; }
        .action-icon { width: 16px; height: 16px; }

        /* Table Footer */
        .table-footer { padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; border-top: 1px solid var(--border-color); background-color: #ffffff; }
        .footer-text { font-size: 14px; color: var(--text-muted); }
        .pagination-list { display: flex; gap: 6px; }
        .page-link { display: flex; align-items: center; justify-content: center; min-width: 32px; height: 32px; padding: 0 6px; border-radius: 6px; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-primary); font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s; }
        .page-link.active { background-color: var(--sidebar-item-active); color: #ffffff; border-color: var(--sidebar-item-active); }

        @media (max-width: 768px) { .app-container { flex-direction: column; } .sidebar-wrapper { width: 100%; height: auto; } .main-content { padding: 16px; } }
    </style>

    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <div class="sidebar-brand">
                <span class="sidebar-brand-name">MODA Admin</span>
                <span class="sidebar-subtitle">Bảng điều khiển Super Admin</span>
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
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/admin/order_mgt/view-global-orders.jsp">
                        <i data-lucide="globe" class="menu-icon"></i>
                        <span>Đơn hàng quốc tế</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                        <i data-lucide="credit-card" class="menu-icon"></i>
                        <span>Tài chính</span>
                    </a>
                </li>
            </ul>
        </div>
    </aside>

    <main class="main-content">
        <div class="topbar">
            <div class="topbar-actions">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="Avatar" class="topbar-avatar" />
            </div>
        </div>

        <section class="page-header">
            <div class="header-info">
                <h1>Đơn hàng quốc tế</h1>
                <p>Danh sách và quản lý các giao dịch mua sắm quốc tế trên hệ thống.</p>
            </div>
        </section>

        <section class="filter-card">
            <form action="" method="GET" class="filter-form">
                <div class="form-group">
                    <label for="searchTxt">Mã đơn hàng hoặc Khách hàng</label>
                    <input type="text" id="searchTxt" name="search" class="form-input" placeholder="Nhập từ khóa..." value="${param.search}">
                </div>
                <div class="form-group">
                    <label for="statusFilter">Trạng thái</label>
                    <select id="statusFilter" name="status" class="form-select">
                        <option value="all">Tất cả trạng thái</option>
                        <option value="success">Thành công</option>
                        <option value="delivering">Đang giao</option>
                        <option value="canceled">Đã hủy</option>
                        <option value="pending">Chờ xác nhận</option>
                    </select>
                </div>
                <button type="button" class="btn-secondary">
                    <i data-lucide="filter" style="width:16px;height:16px;"></i>
                    <span>Lọc dữ liệu</span>
                </button>
            </form>
        </section>

        <section class="table-card">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Mã đơn hàng</th>
                        <th>Khách hàng</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền (VND)</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%-- Nếu có tham số customerId = 5 (Trần Thị Buyer) --%>
                    <c:choose>
                        <c:when test="${param.customerId == '5'}">
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-001</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>29/06/2026</td>
                                <td>435,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td>
                                    <div class="actions-cell">
                                        <button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-007</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>29/06/2026</td>
                                <td>850,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td>
                                    <div class="actions-cell">
                                        <button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-014</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>29/06/2026</td>
                                <td>610,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td>
                                    <div class="actions-cell">
                                        <button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <%-- Mock Data Tổng quan các khách hàng --%>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-001</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>29/06/2026</td>
                                <td>435,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td><div class="actions-cell"><button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button></div></td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-002</a></td>
                                <td>Nguyễn Văn An (#3)</td>
                                <td>28/06/2026</td>
                                <td>1,250,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td><div class="actions-cell"><button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button></div></td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-003</a></td>
                                <td>Lê Hoàng Nam (#4)</td>
                                <td>28/06/2026</td>
                                <td>850,000</td>
                                <td><span class="badge status-delivering">Đang giao</span></td>
                                <td><div class="actions-cell"><button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button></div></td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-007</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>27/06/2026</td>
                                <td>850,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td><div class="actions-cell"><button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button></div></td>
                            </tr>
                            <tr>
                                <td><a href="#" class="order-id-link">#ORD-014</a></td>
                                <td>Trần Thị Buyer (#5)</td>
                                <td>25/06/2026</td>
                                <td>610,000</td>
                                <td><span class="badge status-success">Thành công</span></td>
                                <td><div class="actions-cell"><button class="btn-icon" title="Xem chi tiết"><i data-lucide="eye" class="action-icon"></i></button></div></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="table-footer">
                <span class="footer-text">Hiển thị dữ liệu thực tế hệ thống (Tổng số bản ghi: <b>${param.customerId == '5' ? 3 : 5}</b>)</span>
                <div class="pagination-list">
                    <a href="#" class="page-link active">1</a>
                </div>
            </div>
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
