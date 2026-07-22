<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:if test="${shop == null}">
    <c:redirect url="/admin/shop-management"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Chi tiết Shop - MODA Super Admin</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
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
                        <a href="${pageContext.request.contextPath}/admin/shop-management">
                            <span class="menu-text">Danh sách Shop</span>
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
        <section class="page-header">
            <div class="header-info">
                <h1>Chi tiết Shop</h1>
                <p>Thông tin cửa hàng và danh sách sản phẩm.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/shop-management" style="background: #000; color: #fff; padding: 12px 24px; border-radius: 4px; font-weight: 600; display: inline-flex; align-items: center; gap: 8px;">
                    <i data-lucide="arrow-left" style="width: 18px; height: 18px;"></i> Quay lại
                </a>
            </div>
        </section>

        <section class="profile-card">
            <div>
                <c:choose>
                    <c:when test="${not empty shop.logoUrl}">
                        <img src="${shop.logoUrl}" alt="logo" class="profile-avatar">
                    </c:when>
                    <c:otherwise>
                        <div class="profile-avatar" style="background: #eee; display:flex; align-items:center; justify-content:center; font-size: 32px; font-weight: 700;">
                            ${fn:substring(shop.shopName, 0, 1)}
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="profile-info">
                <h2 class="profile-name">${shop.shopName}</h2>
                <p class="profile-desc">${shop.description}</p>
                <div class="profile-grid">
                    <div class="info-item">
                        <span class="info-label">ID Shop</span>
                        <span class="info-value">#${shop.shopId}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Chủ Shop</span>
                        <span class="info-value">${shop.owner.lastName} ${shop.owner.firstName} (${shop.owner.email})</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Địa chỉ</span>
                        <span class="info-value">${shop.streetAddress}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Trạng thái</span>
                        <span class="info-value">${shop.status} - DUYỆT: ${shop.approvalStatus}</span>
                    </div>
                </div>
            </div>
        </section>

        <section class="table-card">
            <div style="padding: 24px; border-bottom: 1px solid var(--border-color);">
                <h3 style="font-size: 18px; font-weight: 700;">Sản phẩm của Shop (${fn:length(products)})</h3>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Giá (VND)</th>
                        <th>Kho</th>
                        <th>Đã bán</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty products}">
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <div class="product-cell">
                                            <img src="${p.thumbnailUrl}" alt="product" class="product-img">
                                            <div>
                                                <span class="product-name">${p.productName}</span>
                                                <span class="product-cat">Mã: #${p.productId}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${p.basePrice}" type="number" maxFractionDigits="0"/> đ
                                    </td>
                                    <td>${p.totalStock}</td>
                                    <td>${p.totalSold}</td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="4" style="text-align: center;">Chưa có sản phẩm nào.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
            <c:if test="${endP > 1}">
                <div class="table-footer">
                    <div class="pagination-list">
                        <c:forEach begin="1" end="${endP}" var="i">
                            <a href="${pageContext.request.contextPath}/admin/shop-management/detail?shopId=${shop.shopId}&page=${i}" 
                               class="page-link ${tag == i ? 'active' : ''}">${i}</a>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
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
