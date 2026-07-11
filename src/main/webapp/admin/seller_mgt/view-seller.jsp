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
    <title>Chi tiết người bán - MODA Admin</title>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        :root {
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --sidebar-bg: #111827;
            --sidebar-text: #9ca3af;
            --sidebar-text-hover: #ffffff;
            --sidebar-item-active: #1f2937;
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
            --accent-purple: #5850ec;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
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
        .menu-item.active a { color: #ffffff; background-color: var(--sidebar-item-active); box-shadow: 0 4px 12px rgba(88, 80, 236, 0.15); border-left: 3px solid var(--accent-purple); border-radius: 0 8px 8px 0; }
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

        /* Breadcrumbs */
        .breadcrumbs { font-size: 13px; color: var(--text-muted); display: flex; align-items: center; gap: 6px; }
        .breadcrumbs a:hover { color: var(--text-primary); }
        .breadcrumbs-separator { color: var(--text-muted); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; margin-top: -8px; }
        .header-title-area { display: flex; align-items: center; gap: 12px; }
        .header-title-area h1 { font-size: 26px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.02em; }
        
        /* Badges */
        .status-badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.03em; }
        .status-badge.active { background-color: #e6fcf5; color: #0ca678; }
        .status-badge.pending { background-color: #fff9db; color: #f08c00; }
        .status-badge.suspended { background-color: #fff5f5; color: #e03131; }

        .header-actions-area { display: flex; gap: 12px; }
        .btn-suspend { display: inline-flex; align-items: center; gap: 8px; padding: 10px 18px; border-radius: 8px; border: 1px solid var(--danger); background-color: #ffffff; color: var(--danger); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .btn-suspend:hover { background-color: var(--danger-bg); }
        .btn-approve { display: inline-flex; align-items: center; gap: 8px; padding: 10px 18px; border-radius: 8px; border: 1px solid #111827; background-color: #111827; color: #ffffff; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .btn-approve:hover { background-color: #1f2937; }
        .btn-back { background-color: #ffffff; border: 1px solid var(--border-color); color: var(--text-primary); padding: 10px 16px; border-radius: 8px; font-weight: 600; font-size: 14px; display: inline-flex; align-items: center; gap: 8px; cursor: pointer; transition: all 0.2s; }
        .btn-back:hover { background-color: #f8fafc; }

        /* Grid Content Layout */
        .detail-grid { display: grid; grid-template-columns: 360px 1fr; gap: 24px; }
        
        /* Seller Profile Card */
        .profile-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 24px; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; gap: 20px; }
        .profile-header { display: flex; align-items: center; gap: 16px; padding-bottom: 20px; border-bottom: 1px solid var(--border-color); }
        .profile-avatar { width: 64px; height: 64px; border-radius: 12px; object-fit: cover; border: 1px solid var(--border-color); background-color: #f1f5f9; display: flex; align-items: center; justify-content: center; font-weight: 700; color: var(--accent-purple); font-size: 24px; }
        .profile-title-group h2 { font-size: 18px; font-weight: 700; color: var(--text-primary); }
        .profile-title-group p { font-size: 13px; color: var(--text-muted); margin-top: 2px; }

        .profile-details-list { display: flex; flex-direction: column; gap: 16px; }
        .detail-item { display: flex; justify-content: space-between; align-items: flex-start; font-size: 14px; }
        .detail-label { color: var(--text-muted); font-weight: 500; min-width: 110px; }
        .detail-value { color: var(--text-primary); font-weight: 600; text-align: right; word-break: break-word; }

        /* Stats Dashboard Grid */
        .stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
        .stat-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 20px; box-shadow: var(--shadow-sm); display: flex; justify-content: space-between; align-items: flex-start; }
        .stat-info { display: flex; flex-direction: column; gap: 8px; }
        .stat-label { font-size: 13px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
        .stat-value { font-size: 24px; font-weight: 700; color: var(--text-primary); }
        .stat-change { font-size: 12px; font-weight: 600; display: inline-flex; align-items: center; gap: 4px; }
        .stat-change.up { color: var(--success); }
        .stat-change.down { color: var(--danger); }
        .stat-icon-wrapper { width: 40px; height: 40px; border-radius: 8px; background-color: #f1f5f9; display: flex; align-items: center; justify-content: center; color: var(--text-secondary); }

        /* Table Card Section */
        .section-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); overflow: hidden; display: flex; flex-direction: column; }
        .section-header { padding: 18px 24px; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; }
        .section-title { font-size: 16px; font-weight: 700; color: var(--text-primary); }
        .btn-view-all { font-size: 13px; font-weight: 600; color: var(--accent-purple); display: inline-flex; align-items: center; gap: 4px; }
        .btn-view-all:hover { opacity: 0.8; }

        /* Table Styling */
        .table-responsive { overflow-x: auto; width: 100%; }
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #f8fafc; color: var(--text-secondary); font-weight: 600; padding: 12px 24px; border-bottom: 1px solid var(--border-color); text-transform: uppercase; font-size: 11px; letter-spacing: 0.05em; }
        .custom-table td { padding: 14px 24px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        .custom-table tr:hover td { background-color: #f8fafc; }

        .product-cell { display: flex; align-items: center; gap: 12px; }
        .product-img { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; border: 1px solid var(--border-color); background-color: #f1f5f9; display: flex; align-items: center; justify-content: center; color: var(--text-muted); font-size: 12px; }
        .product-name { font-weight: 600; color: var(--text-primary); }
        .product-code { font-weight: 600; color: var(--text-secondary); }

        .badge-status { display: inline-flex; align-items: center; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
        .badge-status.active { background-color: #e6fcf5; color: #0ca678; }
        .badge-status.out-of-stock { background-color: #fff0f6; color: #d6336c; }
        .badge-status.inactive { background-color: #f1f3f5; color: #495057; }

        @media (max-width: 1024px) {
            .detail-grid { grid-template-columns: 1fr; }
        }
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
                <li class="menu-item active">
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
                <li class="menu-item">
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
            <div class="topbar-actions">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="Avatar" class="topbar-avatar" />
            </div>
        </div>

        <nav class="breadcrumbs">
            <a href="${pageContext.request.contextPath}/admin/seller-applications">Người bán</a>
            <span class="breadcrumbs-separator">&gt;</span>
            <span><c:out value="${seller.shopName}"/></span>
        </nav>

        <section class="page-header">
            <div class="header-title-area">
                <h1><c:out value="${seller.shopName}"/></h1>
                <c:choose>
                    <c:when test="${seller.status == 'APPROVED' || seller.status == 'ACTIVE'}">
                        <span class="status-badge active">Đang hoạt động</span>
                    </c:when>
                    <c:when test="${seller.status == 'PENDING'}">
                        <span class="status-badge pending">Chờ duyệt</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge suspended">Đình chỉ</span>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="header-actions-area">
                <button onclick="history.back()" class="btn-back" style="margin-right: 8px;">
                    <i data-lucide="arrow-left" style="width:16px;height:16px;"></i>
                    Quay lại
                </button>
                <c:if test="${seller.status == 'PENDING'}">
                    <form action="${pageContext.request.contextPath}/admin/seller-applications/detail" method="POST" style="display: inline-block;">
                        <input type="hidden" name="id" value="${seller.applicationId}">
                        <input type="hidden" name="action" value="suspend">
                        <button type="submit" class="btn-suspend">Từ chối đơn</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/seller-applications/approve" method="POST" style="display: inline-block;">
                        <input type="hidden" name="id" value="${seller.applicationId}">
                        <button type="submit" class="btn-approve">Phê duyệt đối tác</button>
                    </form>
                </c:if>
                <c:if test="${seller.status == 'APPROVED' || seller.status == 'ACTIVE'}">
                    <form action="${pageContext.request.contextPath}/admin/seller-applications/detail" method="POST" style="display: inline-block;">
                        <input type="hidden" name="id" value="${seller.applicationId}">
                        <input type="hidden" name="action" value="suspend">
                        <button type="submit" class="btn-suspend">Đình chỉ hoạt động</button>
                    </form>
                </c:if>
            </div>
        </section>

        <div class="detail-grid">
            <!-- Cột trái: Thông tin Shop -->
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">
                        <c:choose>
                            <c:when test="${not empty seller.shopName}">
                                <c:out value="${fn:substring(seller.shopName, 0, 1)}"/>
                            </c:when>
                            <c:otherwise>?</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="profile-title-group">
                        <h2><c:out value="${seller.shopName}"/></h2>
                        <p>Đối tác cấp 1 MODA</p>
                    </div>
                </div>

                <div class="profile-details-list">
                    <div class="detail-item">
                        <span class="detail-label">Chủ cửa hàng:</span>
                        <span class="detail-value"><c:out value="${seller.fullName}"/></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Email liên hệ:</span>
                        <span class="detail-value"><c:out value="${seller.email}"/></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Số điện thoại:</span>
                        <span class="detail-value"><c:out value="${seller.phone}"/></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Địa chỉ Shop:</span>
                        <span class="detail-value"><c:out value="${seller.streetAddress}"/></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Mô tả Shop:</span>
                        <span class="detail-value"><c:out value="${seller.shopDescription}"/></span>
                    </div>
                </div>
            </div>

            <!-- Cột phải: Thống kê & Sản phẩm -->
            <div style="display: flex; flex-direction: column; gap: 24px;">
                <section class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-info">
                            <span class="stat-label">Tổng sản phẩm</span>
                            <span class="stat-value"><c:out value="${seller.totalProducts}"/></span>
                            <span class="stat-change up">
                                <i data-lucide="arrow-up-right" style="width:12px;height:12px;"></i>
                                +10%
                            </span>
                        </div>
                        <div class="stat-icon-wrapper">
                            <i data-lucide="package" style="width:20px;height:20px;"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-info">
                            <span class="stat-label">Đánh giá Shop</span>
                            <span class="stat-value">
                                <c:out value="${seller.rating != null ? seller.rating : '4.8'}"/> / 5.0
                            </span>
                        </div>
                        <div class="stat-icon-wrapper">
                            <i data-lucide="star" style="width:20px;height:20px;"></i>
                        </div>
                    </div>
                </section>

                <section class="section-card">
                    <div class="section-header">
                        <span class="section-title">Sản phẩm nổi bật</span>
                        <a href="${pageContext.request.contextPath}/shop?shop_id=${seller.shopId != null && seller.shopId != 0 ? seller.shopId : 1}" class="btn-view-all">
                            Xem tất cả
                            <i data-lucide="arrow-right" style="width:14px;height:14px;"></i>
                        </a>
                    </div>
                    <div class="table-responsive">
                        <table class="custom-table">
                            <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Mã SP</th>
                                <th>Giá bán</th>
                                <th>Đã bán</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <div class="product-cell">
                                            <div class="product-img" style="overflow: hidden; display: flex; align-items: center; justify-content: center; background-color: #f1f5f9;">
                                                <c:choose>
                                                    <c:when test="${not empty p.thumbnailUrl}">
                                                        <img src="${p.thumbnailUrl}" alt="${p.productName}" style="width: 100%; height: 100%; object-fit: cover;" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i data-lucide="image" style="width:16px;height:16px; color: #94a3b8;"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <span class="product-name"><c:out value="${p.productName}"/></span>
                                        </div>
                                    </td>
                                    <td><span class="product-code"><c:out value="${p.productCode}"/></span></td>
                                    <td>
                                        <fmt:formatNumber value="${p.basePrice}" type="number" /> đ
                                    </td>
                                    <td><c:out value="${p.soldCount}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'ACTIVE' || p.status == 'APPROVED'}">
                                                <span class="badge-status active">Đang bán</span>
                                            </c:when>
                                            <c:when test="${p.status == 'OUT_OF_STOCK'}">
                                                <span class="badge-status out-of-stock">Hết hàng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-status inactive">Không bán</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
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
