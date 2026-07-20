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
            --accent-purple: #000000;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
            --shadow-sm: none;
            --shadow-md: none;
            --sidebar-w: 280px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }
        button { font-family: inherit; cursor: pointer; outline: none; }

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
        .menu-item.active a { color: #ffffff !important; background-color: var(--sidebar-item-active); box-shadow: none; border-left: none; border-radius: 0; }
        .menu-text { white-space: nowrap; font-size: 12px; font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; color: inherit; }
        .menu-item.active .menu-text { color: #ffffff !important; }

        .sidebar-logout { border-top: 1px solid var(--border-color); margin-top: auto; }
        .sidebar-logout .menu-item a { height: 72px; }
        .sidebar-logout .menu-item a:hover { color: var(--danger) !important; }
        .sidebar-logout .menu-item a:hover .menu-text { color: var(--danger) !important; }

        /* Main Content */
        .main-content { flex: 1; padding: 64px; display: flex; flex-direction: column; gap: 32px; overflow-x: hidden; background: var(--bg-primary); }

        .topbar { display: flex; align-items: center; justify-content: flex-end; gap: 16px; padding-bottom: 8px; }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 1px solid var(--border-color); }



        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; margin-top: -8px; }
        .header-title-area { display: flex; align-items: center; gap: 16px; }
        .header-title-area h1 { font-size: 40px; font-weight: 700; color: #000000; letter-spacing: -0.02em; line-height: 1.15; }
        
        /* Badges */
        .status-badge { display: inline-flex; align-items: center; padding: 4px 8px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; border-radius: 0; }
        .status-badge.active { background-color: var(--success-bg); color: var(--success-text); }
        .status-badge.pending { background-color: var(--warning-bg); color: var(--warning-text); }
        .status-badge.suspended { background-color: var(--danger-bg); color: var(--danger-text); }

        .header-actions-area { display: flex; gap: 16px; }
        .btn-suspend { display: inline-flex; align-items: center; gap: 8px; padding: 14px 24px; border: 1px solid var(--danger); background-color: #ffffff; color: var(--danger-text); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; border-radius: 0; }
        .btn-suspend:hover { background-color: var(--danger-text); color: #ffffff; }
        .btn-approve { display: inline-flex; align-items: center; gap: 8px; padding: 14px 24px; border: 1px solid #000000; background-color: #000000; color: #ffffff; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; border-radius: 0; }
        .btn-approve:hover { background-color: #333333; }
        .btn-back { background-color: #ffffff; border: 1px solid #000000; color: #000000; padding: 14px 24px; font-weight: 600; font-size: 14px; display: inline-flex; align-items: center; gap: 8px; cursor: pointer; transition: all 0.2s; border-radius: 0; }
        .btn-back:hover { background-color: #000000; color: #ffffff; }

        /* Grid Content Layout */
        .detail-grid { display: grid; grid-template-columns: 320px 1fr; gap: 32px; }
        
        /* Seller Profile Card */
        .profile-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; display: flex; flex-direction: column; gap: 24px; border-radius: 0; box-shadow: none; }
        .profile-header { display: flex; align-items: center; gap: 16px; padding-bottom: 24px; border-bottom: 2px solid var(--border-color); }
        .profile-avatar { width: 80px; height: 80px; object-fit: cover; border: 1px solid var(--border-color); background-color: #000000; display: flex; align-items: center; justify-content: center; font-weight: 700; color: #ffffff; font-size: 32px; border-radius: 0; }
        .profile-title-group h2 { font-size: 20px; font-weight: 700; color: #000000; letter-spacing: -0.01em; }
        .profile-title-group p { font-size: 14px; color: var(--text-muted); margin-top: 4px; }

        .profile-details-list { display: flex; flex-direction: column; gap: 16px; }
        .detail-item { display: flex; justify-content: space-between; align-items: flex-start; font-size: 14px; }
        .detail-label { color: var(--text-muted); font-weight: 600; min-width: 110px; }
        .detail-value { color: #000000; font-weight: 700; text-align: right; word-break: break-word; }

        /* Stats Dashboard Grid */
        .stats-grid { display: flex; flex-direction: column; gap: 24px; }
        .stat-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); padding: 32px; display: flex; justify-content: space-between; align-items: flex-start; border-radius: 0; box-shadow: none; }
        .stat-info { display: flex; flex-direction: column; gap: 12px; }
        .stat-label { font-size: 13px; font-weight: 600; color: #000000; text-transform: uppercase; letter-spacing: 0.05em; }
        .stat-value { font-size: 32px; font-weight: 700; color: #000000; letter-spacing: -.02em; line-height: 1.1; }
        .stat-change { font-size: 12px; font-weight: 600; display: inline-flex; align-items: center; gap: 4px; }
        .stat-change.up { color: var(--success); }
        .stat-change.down { color: var(--danger); }
        .stat-icon-wrapper { width: 48px; height: 48px; background-color: #ffffff; border: 1px solid #000000; display: flex; align-items: center; justify-content: center; color: #000000; border-radius: 0; }

        /* Table Card Section */
        .section-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); overflow: hidden; display: flex; flex-direction: column; border-radius: 0; box-shadow: none; padding: 32px; }
        .section-header { padding-bottom: 24px; border-bottom: 2px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .section-title { font-size: 16px; font-weight: 700; color: #000000; text-transform: uppercase; letter-spacing: 0.05em; }
        .btn-view-all { font-size: 13px; font-weight: 600; color: #000000; display: inline-flex; align-items: center; gap: 8px; text-decoration: underline; }
        .btn-view-all:hover { opacity: 0.7; }

        /* Table Styling */
        .table-responsive { overflow-x: auto; width: 100%; }
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #ffffff; color: #000000; font-weight: 700; padding: 20px 24px; border-bottom: 2px solid var(--border-color); text-transform: uppercase; font-size: 12px; letter-spacing: 0.1em; white-space: nowrap; }
        .custom-table td { padding: 24px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        .custom-table tr:hover td { background-color: #f9f9f9; }

        .product-cell { display: flex; align-items: center; gap: 16px; }
        .product-img { width: 48px; height: 48px; object-fit: cover; border: 1px solid var(--border-color); background-color: #ffffff; display: flex; align-items: center; justify-content: center; color: var(--text-muted); font-size: 12px; border-radius: 0; }
        .product-name { font-weight: 700; color: #000000; }
        .product-code { font-weight: 700; color: var(--text-secondary); }

        .badge-status { display: inline-flex; align-items: center; padding: 4px 8px; font-size: 11px; font-weight: 700; text-transform: uppercase; border-radius: 0; letter-spacing: 0.05em; }
        .badge-status.active { background-color: var(--success-bg); color: var(--success-text); }
        .badge-status.out-of-stock { background-color: var(--warning-bg); color: var(--warning-text); }
        .badge-status.inactive { background-color: var(--danger-bg); color: var(--danger-text); }

        @media (max-width: 1024px) {
            .detail-grid { grid-template-columns: 280px 1fr; }
        }
        @media (max-width: 900px) {
            .detail-grid { grid-template-columns: 1fr; }
        }
        @media (max-width: 768px) {
            .sidebar-wrapper { width: 100%; min-width: unset; max-width: unset; height: auto; position: static; border-right: none; border-bottom: 1px solid var(--border-color); }
            .sidebar { padding: 16px; }
            .main-content { padding: 32px; gap: 24px; }
            .app-container { flex-direction: column; }
        }
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
                    <li class="menu-item ${activeMenu == 'seller-management' ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/admin/seller-management">
                            <span class="menu-text">Người bán</span>
                        </a>
                    </li>
                    <li class="menu-item ${activeMenu == 'seller-applications' || activeMenu == null ? 'active' : ''}">
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
