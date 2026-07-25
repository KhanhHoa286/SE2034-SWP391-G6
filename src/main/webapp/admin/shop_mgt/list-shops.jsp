<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:if test="${shopList == null}">
    <c:redirect url="/admin/shop-management"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Danh Sách Shop - MODA Super Admin</title>

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
                <h1>Danh sách Shop</h1>
                <p>Quản lý và giám sát các shop đang hoạt động trong hệ thống.</p>
            </div>
        </section>

        <section class="filter-card">
            <form action="${pageContext.request.contextPath}/admin/shop-management" method="GET" class="filter-form">
                <div class="form-group">
                    <label for="searchTxt">Tìm kiếm</label>
                    <input type="text" id="searchTxt" name="search" class="form-input" placeholder="Tên shop hoặc email chủ shop..." value="${saveSearch}">
                </div>
                <div class="form-group">
                    <label for="statusFilter">Trạng thái</label>
                    <select id="statusFilter" name="status" class="form-select">
                        <option value="all">Tất cả trạng thái</option>
                        <option value="ACTIVE" ${saveStatus == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                        <option value="INACTIVE" ${saveStatus == 'INACTIVE' ? 'selected' : ''}>Không hoạt động</option>
                        <option value="SUSPENDED" ${saveStatus == 'SUSPENDED' ? 'selected' : ''}>Bị khóa</option>
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
                        <th>Tên Shop</th>
                        <th>Chủ Shop</th>
                        <th>Trạng thái duyệt</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:set var="currentPage" value="${not empty tag ? tag : 1}" />
                    <c:choose>
                        <c:when test="${not empty shopList}">
                            <c:forEach var="s" items="${shopList}" varStatus="loopStatus">
                                <tr>
                                    <td>
                                        <div class="user-cell">
                                            <div class="avatar-circle">
                                                <c:choose>
                                                    <c:when test="${not empty s.logoUrl}">
                                                        <img src="${s.logoUrl}" alt="logo">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${fn:substring(s.shopName, 0, 1)}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div>
                                                <span class="user-name"><c:out value="${s.shopName}"/></span>
                                                <span class="user-id">#ID: ${s.shopId}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div>
                                            <span class="user-name"><c:out value="${s.owner.lastName} ${s.owner.firstName}"/></span>
                                            <span class="user-id"><c:out value="${s.owner.email}"/></span>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge status-active">DUYỆT: ${s.approvalStatus}</span>
                                    </td>
                                    <td>
                                        <span class="badge status-${fn:toLowerCase(s.status)}">
                                            <c:choose>
                                                <c:when test="${s.status == 'ACTIVE'}">Hoạt động</c:when>
                                                <c:when test="${s.status == 'INACTIVE'}">Tạm ngưng</c:when>
                                                <c:otherwise>Bị khóa</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td>
                                        <div class="actions-cell">
                                            <a href="${pageContext.request.contextPath}/admin/shop-management/detail?shopId=${s.shopId}" class="btn-icon" title="Xem chi tiết shop">
                                                <i data-lucide="eye" class="action-icon"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/shop-management" method="POST" style="display:inline;">
                                                <input type="hidden" name="id" value="${s.shopId}">
                                                <input type="hidden" name="action" value="${s.status == 'SUSPENDED' ? 'unban' : 'ban'}">
                                                <button type="submit" class="btn-icon delete" title="${s.status == 'SUSPENDED' ? 'Mở khóa' : 'Khóa'}">
                                                    <i data-lucide="${s.status == 'SUSPENDED' ? 'shield-check' : 'shield-alert'}" class="action-icon"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" style="text-align: center;">Không tìm thấy shop nào.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="table-footer">

                <div class="pagination-list">
                    <c:forEach begin="1" end="${endP != null ? endP : 1}" var="i">
                        <a href="${pageContext.request.contextPath}/admin/shop-management?page=${i}&search=${saveSearch}&status=${saveStatus}"
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
