<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh Sách Sản Phẩm Hệ Thống - MODA Admin</title>

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
            --font-main: 'Inter', sans-serif;
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

        /* Main Content */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }

        .topbar { display: flex; align-items: center; justify-content: flex-end; gap: 16px; padding-bottom: 8px; }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); box-shadow: var(--shadow-sm); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; }
        .header-info h1 { font-size: 28px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
        .header-info p { font-size: 14px; color: var(--text-muted); }

        /* Filter Box */
        .filter-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; box-shadow: var(--shadow-sm); }
        .filter-form { display: flex; flex-wrap: wrap; gap: 16px; align-items: flex-end; }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-group label { font-size: 12px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; }
        .form-control { padding: 10px 14px; border: 1px solid var(--border-color); border-radius: 8px; font-size: 14px; font-family: inherit; color: var(--text-primary); transition: all 0.2s; min-width: 240px; background-color: #fff; }
        .form-control:focus { outline: none; border-color: var(--sidebar-item-active); box-shadow: 0 0 0 3px rgba(88, 80, 236, 0.1); }
        .btn { padding: 10px 18px; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; gap: 8px; justify-content: center; border: 1px solid transparent; }
        .btn-primary { background-color: var(--sidebar-item-active); color: #fff; box-shadow: var(--shadow-sm); }
        .btn-primary:hover { background-color: #4f46e5; }
        .btn-outline { background-color: #fff; color: var(--text-secondary); border-color: var(--border-color); }
        .btn-outline:hover { background-color: #f1f5f9; color: var(--text-primary); }

        /* Table Card */
        .table-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); overflow: hidden; }
        .table-responsive { width: 100%; overflow-x: auto; }
        .custom-table { width: 100%; border-collapse: separate; border-spacing: 0; }
        .custom-table th { background-color: #f8fafc; padding: 16px 20px; text-align: left; font-size: 12px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--border-color); white-space: nowrap; }
        .custom-table td { padding: 16px 20px; font-size: 14px; color: var(--text-primary); border-bottom: 1px solid var(--border-color); vertical-align: middle; }
        .custom-table tbody tr:hover td { background-color: #f8fafc; }
        .custom-table tbody tr:last-child td { border-bottom: none; }

        .product-cell { display: flex; align-items: center; gap: 12px; }
        .product-img { width: 48px; height: 48px; border-radius: 8px; object-fit: cover; border: 1px solid var(--border-color); }
        .product-info { display: flex; flex-direction: column; }
        .product-name { font-weight: 600; color: var(--text-primary); max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .product-category { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

        .badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 9999px; font-size: 12px; font-weight: 600; letter-spacing: 0.02em; }
        .badge-active { background-color: var(--success-bg); color: var(--success-text); }
        .badge-pending { background-color: var(--warning-bg); color: var(--warning-text); }
        .badge-banned { background-color: var(--danger-bg); color: var(--danger-text); }
        .badge-hidden { background-color: #f1f5f9; color: #475569; }

        .action-btns { display: flex; gap: 8px; }
        .btn-icon { width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; border-radius: 6px; border: 1px solid var(--border-color); background-color: #fff; color: var(--text-secondary); cursor: pointer; transition: all 0.2s; }
        .btn-icon:hover { background-color: #f1f5f9; color: var(--sidebar-item-active); border-color: #cbd5e1; }

        /* Pagination */
        .pagination-wrapper { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-top: 1px solid var(--border-color); background-color: #fff; }
        .pagination-info { font-size: 14px; color: var(--text-secondary); }
        .pagination-controls { display: flex; gap: 4px; }
        .page-btn { min-width: 36px; height: 36px; display: inline-flex; align-items: center; justify-content: center; padding: 0 12px; border: 1px solid var(--border-color); border-radius: 6px; font-size: 14px; font-weight: 500; color: var(--text-secondary); background-color: #fff; transition: all 0.2s; cursor: pointer; }
        .page-btn:hover:not(:disabled) { background-color: #f1f5f9; color: var(--text-primary); }
        .page-btn.active { background-color: var(--sidebar-item-active); color: #fff; border-color: var(--sidebar-item-active); }
        .page-btn:disabled { opacity: 0.5; cursor: not-allowed; }

        /* Modal Styles */
        .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; opacity: 0; visibility: hidden; transition: all 0.3s; }
        .modal-overlay.active { opacity: 1; visibility: visible; }
        .modal-card { background: #fff; width: 500px; max-width: 90%; border-radius: 16px; box-shadow: var(--shadow-lg); transform: translateY(20px) scale(0.95); transition: all 0.3s; display: flex; flex-direction: column; overflow: hidden; }
        .modal-overlay.active .modal-card { transform: translateY(0) scale(1); }
        .modal-header { padding: 20px 24px; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; background: #f8fafc; }
        .modal-title { font-size: 18px; font-weight: 700; color: var(--text-primary); }
        .btn-close { background: none; border: none; color: var(--text-muted); cursor: pointer; border-radius: 4px; padding: 4px; transition: all 0.2s; }
        .btn-close:hover { background: #e2e8f0; color: var(--danger); }
        .modal-body { padding: 24px; display: flex; flex-direction: column; gap: 20px; }
        .modal-footer { padding: 16px 24px; border-top: 1px solid var(--border-color); display: flex; justify-content: flex-end; gap: 12px; background: #f8fafc; }
        textarea.form-control { min-height: 100px; resize: vertical; }

        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; display: flex; align-items: center; gap: 8px; }
        .alert-success { background-color: var(--success-bg); color: var(--success-text); border: 1px solid #a7f3d0; }
        .alert-error { background-color: var(--danger-bg); color: var(--danger-text); border: 1px solid #fecaca; }
    </style>
</head>
<body>

<div class="app-container">
    <!-- Sidebar -->
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
                    <li class="menu-item">
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
                    <li class="menu-item active">
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

    <!-- Main Content -->
    <main class="main-content">
        <div class="topbar">
            <div class="topbar-actions">
                <img src="https://ui-avatars.com/api/?name=Admin&background=f472b6&color=fff" alt="Admin" class="topbar-avatar">
            </div>
        </div>

        <!-- Page Header -->
        <div class="page-header">
            <div class="header-info">
                <h1>Danh Sách Sản Phẩm Hệ Thống</h1>
                <p>Quản lý và kiểm duyệt tất cả các sản phẩm của các đối tác Sellers trên sàn MODA.</p>
            </div>
        </div>

        <c:if test="${not empty sessionScope.msgSuccess}">
            <div class="alert alert-success" id="alert-success">
                <i data-lucide="check-circle" style="width: 18px; height: 18px;"></i>
                ${sessionScope.msgSuccess}
                <c:remove var="msgSuccess" scope="session" />
            </div>
        </c:if>
        <c:if test="${not empty sessionScope.msgError}">
            <div class="alert alert-error" id="alert-error">
                <i data-lucide="alert-circle" style="width: 18px; height: 18px;"></i>
                ${sessionScope.msgError}
                <c:remove var="msgError" scope="session" />
            </div>
        </c:if>

        <!-- Filter Form -->
        <div class="filter-card">
            <form action="${pageContext.request.contextPath}/admin/products" method="GET" class="filter-form">
                <div class="form-group" style="flex: 1;">
                    <label>Tìm kiếm sản phẩm</label>
                    <input type="text" name="search" class="form-control" placeholder="Tên sản phẩm, tên cửa hàng..." value="${param.search}">
                </div>

                <button type="submit" class="btn btn-primary">
                    <i data-lucide="search" style="width: 16px; height: 16px;"></i> Lọc kết quả
                </button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline">
                    Xóa bộ lọc
                </a>
            </form>
        </div>

        <!-- Data Table -->
        <div class="table-card">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Sản phẩm</th>
                            <th>Cửa hàng</th>
                            <th>Giá bán</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th style="text-align: right;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty products}">
                                <c:forEach var="p" items="${products}">
                                    <tr>
                                        <td style="font-weight: 500; color: var(--text-muted);">#${p.productId}</td>
                                        <td>
                                            <div class="product-cell">
                                                <img src="${p.thumbnailUrl != null ? p.thumbnailUrl : 'https://placehold.co/100x100?text=No+Image'}" alt="Thumb" class="product-img">
                                                <div class="product-info">
                                                    <div class="product-name" title="${p.productName}">${p.productName}</div>
                                                    <div class="product-category">${p.categoryName}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td style="font-weight: 500;">${p.shopName}</td>
                                        <td><fmt:formatNumber value="${p.basePrice}" type="number" groupingUsed="true"/> VND</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.status == 'ACTIVE'}">
                                                    <span class="badge badge-active">Hoạt động</span>
                                                </c:when>
                                                <c:when test="${p.status == 'PENDING'}">
                                                    <span class="badge badge-pending">Chờ duyệt</span>
                                                </c:when>
                                                <c:when test="${p.status == 'BANNED'}">
                                                    <span class="badge badge-banned">Cấm bán</span>
                                                </c:when>
                                                <c:when test="${p.status == 'HIDDEN'}">
                                                    <span class="badge badge-hidden">Bị ẩn</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-hidden">${p.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="color: var(--text-muted);"><fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td style="text-align: right;">
                                            <div class="action-btns" style="justify-content: flex-end;">
                                                <c:if test="${p.status == 'PENDING'}">
                                                    <a href="${pageContext.request.contextPath}/admin/product/edit-status?productId=${p.productId}" class="btn-icon" title="Kiểm duyệt / Chi tiết">
                                                        <i data-lucide="eye" style="width: 16px; height: 16px;"></i>
                                                    </a>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                        <i data-lucide="package-x" style="width: 48px; height: 48px; opacity: 0.5; margin-bottom: 12px; display: block; margin-left: auto; margin-right: auto;"></i>
                                        Không tìm thấy sản phẩm nào trong hệ thống.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="pagination-wrapper">
                    <div class="pagination-info">
                        Hiển thị <strong>${products.size()}</strong> trên tổng <strong>${totalProducts}</strong> sản phẩm
                    </div>
                    <div class="pagination-controls">
                        <c:if test="${currentPage > 1}">
                            <a href="?page=${currentPage - 1}&search=${param.search}&status=${param.status}" class="page-btn"><i data-lucide="chevron-left" style="width: 16px; height: 16px;"></i></a>
                        </c:if>
                        
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="?page=${i}&search=${param.search}&status=${param.status}" class="page-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>
                        
                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}&search=${param.search}&status=${param.status}" class="page-btn"><i data-lucide="chevron-right" style="width: 16px; height: 16px;"></i></a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </main>
</div>

<script src="https://unpkg.com/lucide@latest"></script>
<script>
    lucide.createIcons();
    
    // Tự động ẩn thông báo sau 5 giây
    setTimeout(function() {
        var alertSuccess = document.getElementById('alert-success');
        var alertError = document.getElementById('alert-error');
        if(alertSuccess) alertSuccess.style.display = 'none';
        if(alertError) alertError.style.display = 'none';
    }, 5000);
</script>

</body>
</html>
