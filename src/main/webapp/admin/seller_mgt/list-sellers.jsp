<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- BẮT BUỘC: Thêm thư viện functions để xử lý chuỗi (viết thường chữ) an toàn --%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- BẪY TỰ ĐỘNG CHẠY SAI: Nếu mở trực tiếp file JSP này, hệ thống tự đẩy về Servlet --%>
<c:if test="${userListLoaded == null}">
    <c:redirect url="/admin/seller-management"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Danh Sách Người Bán - MODA Super Admin</title>

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
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            --shadow-sm: none;
            --shadow-md: none;
            --shadow-lg: none;
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

        /* Main Content & Topbar Styles */
        .main-content { flex: 1; padding: 64px; display: flex; flex-direction: column; gap: 48px; overflow-x: hidden; background: var(--bg-primary); }

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

        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); cursor: pointer; transition: all 0.2s ease; }
        .topbar-avatar:hover { opacity: 0.8; }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
        .header-info h1 { font-size: 40px; font-weight: 700; color: #000000; letter-spacing: -0.02em; margin-bottom: 8px; line-height: 1.15; }
        .header-info p { font-size: 16px; color: var(--text-muted); line-height: 1.5; margin: 0; }

        /* Filter Box */
        .filter-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; }
        .filter-form { display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end; }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-group label { font-size: 11px; font-weight: 700; color: #000000; text-transform: uppercase; letter-spacing: 0.1em; }
        .form-input, .form-select { min-width: 240px; padding: 14px 16px; border: 1px solid var(--border-color); font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: #ffffff; outline: none; transition: border-color 0.2s ease; min-height: 52px; }
        .form-input:focus, .form-select:focus { border-color: #000000; }
        .btn-secondary { background-color: #000000; color: #ffffff; padding: 16px 32px; border: 1px solid #000000; font-size: 12px; font-weight: 700; cursor: pointer; display: inline-flex; align-items: center; gap: 10px; transition: opacity 0.2s ease; text-transform: uppercase; letter-spacing: 0.1em; min-height: 52px; }
        .btn-secondary:hover { opacity: 0.9; }

        /* Table Design */
        .table-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); overflow: hidden; display: flex; flex-direction: column; }
        .table-responsive { overflow-x: auto; width: 100%; }
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #ffffff; color: #000000; font-weight: 700; padding: 20px 24px; border-bottom: 2px solid var(--border-color); text-transform: uppercase; font-size: 12px; letter-spacing: 0.1em; white-space: nowrap; }
        .custom-table td { padding: 24px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        .custom-table tr:hover td { background-color: #f9f9f9; }

        /* User Profile Cell */
        .user-cell { display: flex; align-items: center; gap: 16px; }
        .avatar-circle { width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 16px; color: #ffffff; text-transform: uppercase; background-color: #000000; }
        .user-name { font-weight: 600; color: var(--text-primary); display: block; font-size: 15px; }
        .user-id { font-size: 12px; color: var(--text-muted); margin-top: 4px; display: block; }

        /* Badges */
        .badge { display: inline-flex; align-items: center; padding: 6px 12px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.1em; }
        .badge.role-admin { background-color: #e2e8f0; color: #000000; }
        .badge.role-seller { background-color: #f1f5f9; color: #000000; }
        .badge.role-customer { background-color: #f8fafc; color: #475569; }
        .badge.status-active { background-color: var(--success-bg); color: var(--success-text); }
        .badge.status-inactive { background-color: #f1f5f9; color: #475569; }
        .badge.status-banned { background-color: var(--danger-bg); color: var(--danger-text); }

        /* Actions Menu */
        .actions-cell { display: flex; gap: 8px; }
        .btn-icon { width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-secondary); cursor: pointer; transition: all 0.2s ease; }
        .btn-icon:hover { background-color: #000000; color: #ffffff; border-color: #000000; }
        .btn-icon.delete:hover { background-color: var(--danger); color: #ffffff; border-color: var(--danger); }
        .action-icon { width: 18px; height: 18px; }

        /* Pagination & Footer */
        .table-footer { padding: 24px; display: flex; align-items: center; justify-content: center !important; border-top: 1px solid var(--border-color); background-color: #ffffff; flex-wrap: wrap; gap: 12px; width: 100%; }
        .footer-text { font-size: 14px; color: var(--text-muted); }
        .pagination-list { display: flex; gap: 8px; }
        .page-link { display: flex; align-items: center; justify-content: center; min-width: 40px; height: 40px; padding: 0 8px; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-primary); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
        .page-link:hover { background-color: #f5f5f5; border-color: var(--border-color); }
        .page-link.active { background-color: #000000; color: #ffffff; border-color: #000000; box-shadow: none; }

        @media (max-width: 992px) { .filter-form { flex-direction: column; align-items: stretch; } .form-input, .form-select { min-width: 100%; } .btn-secondary { justify-content: center; } }
        @media (max-width: 768px) {
            .app-container { flex-direction: column; }
            .sidebar-wrapper { width: 100%; height: auto; position: relative; }
            .sidebar { padding: 24px 0; }
            .main-content { padding: 32px 20px; gap: 32px; }
            .page-header { flex-direction: column; align-items: flex-start; gap: 12px; }
        }
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
                            <span class="menu-text">Tổng quan</span>
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/user-management">
                            <span class="menu-text">Người dùng</span>
                        </a>
                    </li>
                    <li class="menu-item active">
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


        <!-- Khối tiêu đề trang (Đã xóa button Thêm người dùng mới) -->
        <section class="page-header">
            <div class="header-info">
                <h1>Danh sách người bán</h1>
                <p>Quản lý và giám sát danh sách người bán trong hệ thống.</p>
            </div>
        </section>

        <section class="filter-card">
            <form action="${pageContext.request.contextPath}/admin/seller-management" method="GET" class="filter-form">
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
                                            <form action="${pageContext.request.contextPath}/admin/seller-management" method="POST" style="display:inline;">
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

                <div class="pagination-list">
                    <c:forEach begin="1" end="${endP != null ? endP : 1}" var="i">
                        <a href="${pageContext.request.contextPath}/admin/seller-management?page=${i}&search=${saveSearch}&status=${saveStatus}"
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
