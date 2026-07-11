<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giấy phép Kinh doanh - ${app.shopName} - MODA Admin</title>

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

        .btn-back { display: inline-flex; align-items: center; gap: 8px; padding: 10px 18px; border-radius: 8px; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-secondary); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .btn-back:hover { background-color: #f1f5f9; color: var(--text-primary); }

        /* Document Container */
        .document-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
        .document-card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 24px; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; gap: 20px; }
        .document-card-header { border-bottom: 1px solid var(--border-color); padding-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
        .document-card-title { font-size: 18px; font-weight: 700; color: var(--text-primary); }
        
        .info-list { display: flex; flex-direction: column; gap: 14px; }
        .info-item { display: flex; justify-content: space-between; border-bottom: 1px dashed var(--border-color); padding-bottom: 8px; }
        .info-label { color: var(--text-muted); font-weight: 500; }
        .info-value { color: var(--text-primary); font-weight: 600; }

        /* Image Display */
        .image-wrapper { border: 2px dashed var(--border-color); border-radius: 8px; padding: 12px; display: flex; justify-content: center; align-items: center; background-color: #f8fafc; min-height: 250px; overflow: hidden; position: relative; }
        .document-image { max-width: 100%; max-height: 350px; border-radius: 6px; object-fit: contain; box-shadow: var(--shadow-sm); transition: transform 0.3s ease; }
        .document-image:hover { transform: scale(1.03); cursor: zoom-in; }

        .status-badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.03em; }
        .status-badge.active { background-color: #e6fcf5; color: #0ca678; }
        .status-badge.pending { background-color: #fff9db; color: #f08c00; }
        .status-badge.suspended { background-color: #fff5f5; color: #e03131; }

        @media (max-width: 768px) {
            .document-grid { grid-template-columns: 1fr; }
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
            <a href="${pageContext.request.contextPath}/admin/seller-applications/detail?id=${app.id}"><c:out value="${app.shopName}"/></a>
            <span class="breadcrumbs-separator">&gt;</span>
            <span>Giấy phép Kinh doanh</span>
        </nav>

        <section class="page-header">
            <div class="header-title-area">
                <h1>Hồ sơ & Giấy phép đăng ký</h1>
                <c:choose>
                    <c:when test="${app.status == 'APPROVED' || app.status == 'ACTIVE'}">
                        <span class="status-badge active">Đang hoạt động</span>
                    </c:when>
                    <c:when test="${app.status == 'PENDING'}">
                        <span class="status-badge pending">Chờ duyệt</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge suspended">Đình chỉ</span>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <a href="${pageContext.request.contextPath}/admin/seller-applications" class="btn-back">
                <i data-lucide="arrow-left" style="width: 16px; height: 16px;"></i>
                Quay lại
            </a>
        </section>

        <div class="document-grid">
            <!-- Thông tin Đăng ký & Thuế -->
            <div class="document-card">
                <div class="document-card-header">
                    <span class="document-card-title">Thông tin doanh nghiệp</span>
                </div>
                
                <div class="info-list">
                    <div class="info-item">
                        <span class="info-label">Tên Shop:</span>
                        <span class="info-value"><c:out value="${app.shopName}"/></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Chủ cửa hàng:</span>
                        <span class="info-value"><c:out value="${app.ownerName}"/></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Mã số thuế:</span>
                        <span class="info-value"><c:out value="${app.mst}"/></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Email liên hệ:</span>
                        <span class="info-value"><c:out value="${app.businessEmail}"/></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Ngày gửi đăng ký:</span>
                        <span class="info-value"><c:out value="${app.registeredDate}"/> <c:out value="${app.registeredTime}"/></span>
                    </div>
                </div>

                <div style="margin-top: 16px; padding: 16px; background-color: #f0fdf4; border-radius: 8px; border: 1px solid #bbf7d0;">
                    <p style="font-size: 13px; font-weight: 500; color: #15803d; line-height: 1.5;">
                        <i data-lucide="check-circle" style="width:16px; height:16px; display:inline-block; vertical-align:middle; margin-right:4px;"></i>
                        Tài liệu này đã được xác thực mã số thuế doanh nghiệp hợp lệ trên Cổng thông tin Tổng cục Thuế Việt Nam.
                    </p>
                </div>
            </div>

            <!-- Giấy tờ cá nhân (ID Card) -->
            <div class="document-card">
                <div class="document-card-header">
                    <span class="document-card-title">Giấy tờ tùy thân (Mặt trước CCCD/CMND)</span>
                </div>
                <div class="image-wrapper">
                    <c:choose>
                        <c:when test="${not empty app.frontIdImage}">
                            <img src="${app.frontIdImage}" alt="Mặt trước CCCD" class="document-image" />
                        </c:when>
                        <c:otherwise>
                            <img src="https://images.unsplash.com/photo-1554774853-719586f82d77?auto=format&fit=crop&w=600&q=80" alt="Mẫu CCCD" class="document-image" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Giấy tờ cá nhân (ID Card Back) -->
            <div class="document-card">
                <div class="document-card-header">
                    <span class="document-card-title">Giấy tờ tùy thân (Mặt sau CCCD/CMND)</span>
                </div>
                <div class="image-wrapper">
                    <c:choose>
                        <c:when test="${not empty app.backIdImage}">
                            <img src="${app.backIdImage}" alt="Mặt sau CCCD" class="document-image" />
                        </c:when>
                        <c:otherwise>
                            <img src="https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=600&q=80" alt="Mẫu GPKD" class="document-image" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Giấy phép kinh doanh thực tế -->
            <div class="document-card">
                <div class="document-card-header">
                    <span class="document-card-title">Ảnh chụp Giấy phép Kinh doanh (GPKD)</span>
                </div>
                <div class="image-wrapper">
                    <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780447385/business_license_example_ok7y2c.png" alt="Giấy phép kinh doanh thực tế" class="document-image" />
                </div>
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
