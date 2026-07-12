<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- BẮT BUỘC: Thêm thư viện functions để xử lý chuỗi (viết thường chữ) an toàn --%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- BẪY TỰ ĐỘNG CHẠY SAI: Nếu mở trực tiếp file JSP này, hệ thống tự đẩy về Servlet --%>
<c:if test="${userListLoaded == null}">
    <c:redirect url="/admin/user-management"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Danh Sách Người Dùng - MODA Super Admin</title>

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
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02);
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

        /* Main Content & Topbar Styles */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 16px;
            padding-bottom: 8px;
        }

        .topbar-actions {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-shrink: 0;
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
        .form-input:focus, .form-select:focus { border-color: var(--sidebar-item-active); }
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

        /* User Profile Cell */
        .user-cell { display: flex; align-items: center; gap: 12px; }
        .avatar-circle { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 14px; color: #ffffff; text-transform: uppercase; background-color: #3b82f6; box-shadow: var(--shadow-sm); }
        .user-name { font-weight: 600; color: var(--text-primary); display: block; }
        .user-id { font-size: 12px; color: var(--text-muted); }

        /* Badges */
        .badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 9999px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.02em; }
        .badge.role-admin { background-color: #eff6ff; color: #1d4ed8; }
        .badge.role-seller { background-color: #f5f3ff; color: #6d28d9; }
        .badge.role-customer { background-color: #f0fdf4; color: #166534; }
        .badge.status-active { background-color: var(--success-bg); color: var(--success-text); }
        .badge.status-inactive { background-color: #f1f5f9; color: #475569; }
        .badge.status-banned { background-color: var(--danger-bg); color: var(--danger-text); }

        /* Actions Menu */
        .actions-cell { display: flex; gap: 8px; }
        .btn-icon { width: 32px; height: 32px; border-radius: 6px; display: flex; align-items: center; justify-content: center; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-secondary); cursor: pointer; transition: all 0.2s ease; }
        .btn-icon:hover { background-color: #f8fafc; color: var(--sidebar-item-active); border-color: #cbd5e1; }
        .btn-icon.delete:hover { color: var(--danger); border-color: #fca5a5; background-color: var(--danger-bg); }
        .action-icon { width: 16px; height: 16px; }

        /* Pagination & Footer */
        .table-footer { padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; border-top: 1px solid var(--border-color); background-color: #ffffff; flex-wrap: wrap; gap: 12px; }
        .footer-text { font-size: 14px; color: var(--text-muted); }
        .pagination-list { display: flex; gap: 6px; }
        .page-link { display: flex; align-items: center; justify-content: center; min-width: 32px; height: 32px; padding: 0 6px; border-radius: 6px; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-primary); font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.2s ease; }
        .page-link:hover { background-color: #f8fafc; border-color: #cbd5e1; }
        .page-link.active { background-color: var(--sidebar-item-active); color: #ffffff; border-color: var(--sidebar-item-active); box-shadow: 0 2px 6px rgba(88, 80, 236, 0.2); }

        @media (max-width: 992px) { .filter-form { flex-direction: column; align-items: stretch; } .form-input, .form-select { min-width: 100%; } .btn-secondary { justify-content: center; } }
        @media (max-width: 768px) { .app-container { flex-direction: column; } .sidebar-wrapper { width: 100%; height: auto; } .main-content { padding: 16px; } .page-header { flex-direction: column; align-items: flex-start; gap: 12px; } .topbar { justify-content: flex-end; } }
    </style>

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
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/dashboard/overview">
                            <i data-lucide="layout-dashboard" class="menu-icon"></i>
                            <span class="menu-text">Tổng quan</span>
                        </a>
                    </li>
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/admin/user-management">
                            <i data-lucide="users" class="menu-icon"></i>
                            <span class="menu-text">Người dùng</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications">
                            <i data-lucide="store" class="menu-icon"></i>
                            <span class="menu-text">Người bán</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/orders">
                            <i data-lucide="shopping-cart" class="menu-icon"></i>
                            <span class="menu-text">Đơn hàng hệ thống</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/products">
                            <i data-lucide="package" class="menu-icon"></i>
                            <span class="menu-text">Danh sách sản phẩm</span>
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
            <div style="margin-top: auto;">
                <ul class="sidebar-menu">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/logout">
                            <i data-lucide="log-out" class="menu-icon"></i>
                            <span class="menu-text">Đăng xuất</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </aside>

    <main class="main-content">
        <div class="topbar">
            <div class="topbar-actions">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="Avatar" class="topbar-avatar" />
            </div>
        </div>

        <!-- Khối tiêu đề trang (Đã xóa button Thêm người dùng mới) -->
        <section class="page-header">
            <div class="header-info">
                <h1>Danh sách người dùng</h1>
                <p>Quản lý và giám sát toàn bộ người dùng trong hệ thống.</p>
            </div>
        </section>

        <section class="filter-card">
            <form action="${pageContext.request.contextPath}/admin/user-management" method="GET" class="filter-form">
                <div class="form-group">
                    <label for="searchTxt">Tìm kiếm</label>
                    <input type="text" id="searchTxt" name="search" class="form-input" placeholder="Tên hoặc email..." value="${saveSearch}">
                </div>
                <div class="form-group">
                    <label for="statusFilter">Trạng thái</label>
                    <select id="statusFilter" name="status" class="form-select">
                        <option value="all">Tất cả trạng thái</option>
                        <option value="ACTIVE" ${saveStatus == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                        <option value="INACTIVE" ${saveStatus == 'INACTIVE' ? 'selected' : ''}>Không hoạt động</option>
                        <option value="BANNED" ${saveStatus == 'BANNED' ? 'selected' : ''}>Bị khóa</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="roleFilter">Vai trò</label>
                    <select id="roleFilter" name="role" class="form-select">
                        <option value="all">Tất cả vai trò</option>
                        <option value="ADMIN" ${saveRole == 'ADMIN' ? 'selected' : ''}>Admin</option>
                        <option value="SELLER" ${saveRole == 'SELLER' ? 'selected' : ''}>Seller</option>
                        <option value="CUSTOMER" ${saveRole == 'CUSTOMER' ? 'selected' : ''}>Customer</option>
                    </select>
                </div>
                <button type="submit" class="btn-secondary">
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
                        <th>Người dùng</th>
                        <th>Email</th>
                        <th>Vai trò</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:set var="currentPage" value="${not empty tag ? tag : 1}" />
                    <c:choose>
                        <c:when test="${not empty userList}">
                            <c:forEach var="u" items="${userList}" varStatus="loopStatus">
                                <tr>
                                    <td>
                                        <div class="user-cell">
                                            <div class="avatar-circle" style="background-color: ${fn:contains(u.roleNames, 'ADMIN') ? '#5850ec' : (fn:contains(u.roleNames, 'SELLER') ? '#8b5cf6' : '#10b981')}">
                                                <c:choose>
                                                    <c:when test="${not empty u.fullName}">
                                                        <c:out value="${fn:substring(u.fullName, 0, 1)}"/>
                                                    </c:when>
                                                    <c:otherwise>?</c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div>
                                                <span class="user-name"><c:out value="${u.fullName}"/></span>
                                                <span class="user-id">#${(currentPage - 1) * 5 + loopStatus.count}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td><c:out value="${u.email}"/></td>
                                    <td>
                                        <span class="badge role-${fn:contains(u.roleNames, 'ADMIN') ? 'admin' : (fn:contains(u.roleNames, 'SELLER') ? 'seller' : 'customer')}"><c:out value="${u.roleNames}"/></span>
                                    </td>
                                    <td>
                                        <span class="badge status-${fn:toLowerCase(u.status)}">
                                            <c:choose>
                                                <c:when test="${u.status == 'ACTIVE'}">Hoạt động</c:when>
                                                <c:when test="${u.status == 'INACTIVE'}">Tạm ngưng</c:when>
                                                <c:otherwise>Bị khóa</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td>
                                        <div class="actions-cell">
                                            <c:choose>
                                                <c:when test="${fn:contains(u.roleNames, 'SELLER')}">
                                                    <a href="${pageContext.request.contextPath}/admin/seller-applications/detail?userId=${u.userId}" class="btn-icon" title="Xem chi tiết người bán">
                                                        <i data-lucide="eye" class="action-icon"></i>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/admin/user_mgt/view-customer?id=${u.userId}" class="btn-icon" title="Xem chi tiết khách hàng">
                                                        <i data-lucide="eye" class="action-icon"></i>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                            <form action="${pageContext.request.contextPath}/admin/user-management" method="POST" style="display:inline;">
                                                <input type="hidden" name="id" value="${u.userId}">
                                                <input type="hidden" name="action" value="${u.status == 'BANNED' ? 'unban' : 'ban'}">
                                                <button type="submit" class="btn-icon delete" title="${u.status == 'BANNED' ? 'Mở khóa' : 'Khóa'}">
                                                    <i data-lucide="${u.status == 'BANNED' ? 'shield-check' : 'shield-alert'}" class="action-icon"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td><div class="user-cell"><div class="avatar-circle" style="background-color:#5850ec">A</div><div><span class="user-name">Admin System</span><span class="user-id">#1</span></div></div></td>
                                <td>admin@moda.com</td>
                                <td><span class="badge role-admin">ADMIN</span></td>
                                <td><span class="badge status-active">Hoạt động</span></td>
                                <td><div class="actions-cell"><a href="${pageContext.request.contextPath}/admin/user_mgt/view-customer?id=1" class="btn-icon" title="Xem chi tiết khách hàng"><i data-lucide="eye" class="action-icon"></i></a><button class="btn-icon delete" title="Khóa"><i data-lucide="shield-alert" class="action-icon"></i></button></div></td>
                            </tr>
                            <tr>
                                <td><div class="user-cell"><div class="avatar-circle" style="background-color:#8b5cf6">N</div><div><span class="user-name">Nguyễn Văn Seller</span><span class="user-id">#2</span></div></div></td>
                                <td>seller1@gmail.com</td>
                                <td><span class="badge role-seller">SELLER</span></td>
                                <td><span class="badge status-active">Hoạt động</span></td>
                                <td><div class="actions-cell"><a href="${pageContext.request.contextPath}/admin/seller-applications/detail?userId=2" class="btn-icon" title="Xem chi tiết người bán"><i data-lucide="eye" class="action-icon"></i></a><button class="btn-icon delete" title="Khóa"><i data-lucide="shield-alert" class="action-icon"></i></button></div></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="table-footer">
                <span class="footer-text">Hiển thị dữ liệu hệ thống (Tổng số bản ghi: <b>${totalUsers != null ? totalUsers : 2}</b>)</span>
                <div class="pagination-list">
                    <c:forEach begin="1" end="${endP != null ? endP : 1}" var="i">
                        <a href="${pageContext.request.contextPath}/admin/user-management?page=${i}&search=${saveSearch}&role=${saveRole}&status=${saveStatus}"
                           class="page-link ${tag == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
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
