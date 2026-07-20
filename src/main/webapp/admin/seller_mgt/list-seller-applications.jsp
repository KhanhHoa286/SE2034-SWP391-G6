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
    <title>MODA Admin - Danh sách yêu cầu mở Shop</title>
    
    <!-- Link CSS đã tách riêng biệt (Thêm cache buster để tránh lưu cache trình duyệt) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin/list-seller-applications.css?v=1.1">
    
    <!-- Tải bộ icon Lucide -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <!-- Sidebar bên trái -->
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
                        <a href="${pageContext.request.contextPath}/admin/seller-management">
                            <i data-lucide="shopping-bag" class="menu-icon"></i>
                            <span class="menu-text">Người bán</span>
                        </a>
                    </li>
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications">
                            <i data-lucide="store" class="menu-icon"></i>
                            <span class="menu-text">Duyệt đăng ký</span>
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

    <!-- Nội dung chính bên phải -->
    <main class="main-content">


        <!-- Khối tiêu đề trang -->
        <section class="page-header">
            <div class="header-info">
                <h1>Yêu cầu đăng ký gian hàng</h1>
                <p>Quản lý và xét duyệt các đơn đăng ký trở thành nhà bán hàng trên hệ thống.</p>
            </div>
        </section>

        <!-- Khối lọc dữ liệu -->
        <section class="filter-card">
            <form action="${pageContext.request.contextPath}/admin/seller-applications" method="GET" class="filter-form">
                <!-- Ô tìm kiếm -->
                <div class="search-input-wrapper">
                    <i data-lucide="search" class="search-icon"></i>
                    <input type="text" name="search" class="search-input" placeholder="Tìm kiếm theo ID, tên shop, MST..." value="${param.search}">
                </div>
                
                <!-- Bộ lọc Trạng thái -->
                <div class="search-input-wrapper" style="flex: unset; min-width: 210px;">
                    <i data-lucide="sliders-horizontal" class="search-icon" style="width: 16px; height: 16px;"></i>
                    <select name="status" class="search-input" onchange="this.form.submit()" style="padding-left: 42px; cursor: pointer;">
                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Trạng thái: Chờ duyệt</option>
                        <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>Trạng thái: Đã duyệt</option>
                        <option value="ALL" ${status == 'ALL' ? 'selected' : ''}>Trạng thái: Tất cả</option>
                    </select>
                </div>
                
                <!-- Bộ lọc Ngày đăng ký -->
                <div class="search-input-wrapper" style="flex: unset; min-width: 180px;">
                    <i data-lucide="calendar" class="search-icon" style="width: 16px; height: 16px;"></i>
                    <input type="date" name="date" class="search-input" value="${date}" onchange="this.form.submit()" style="padding-left: 42px; cursor: pointer; color: var(--text-secondary);">
                </div>
            </form>
        </section>

        <!-- Bảng hiển thị danh sách yêu cầu -->
        <section class="table-card">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Chủ sở hữu</th>
                        <th>Tên shop đề xuất</th>
                        <th>Thông tin kinh doanh</th>
                        <th>Ngày đăng ký</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty applicationList}">
                            <!-- Lặp dữ liệu động khi được load từ Controller/Servlet -->
                            <c:forEach var="app" items="${applicationList}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/seller-applications/detail?id=${app.id}" class="app-id-link">
                                            #<c:out value="${app.id}"/>
                                        </a>
                                    </td>
                                    <td>
                                        <span class="owner-name"><c:out value="${app.ownerName}"/></span>
                                    </td>
                                    <td>
                                        <span class="shop-name"><c:out value="${app.shopName}"/></span>
                                    </td>
                                    <td>
                                        <div class="business-info">
                                            <span class="mst-text">CCCD: <c:out value="${app.mst}"/></span>
                                            <a href="${pageContext.request.contextPath}/admin/seller-applications/license?id=${app.id}" class="license-link">
                                                <i data-lucide="file-text" class="license-icon"></i>
                                                Xem TTCN
                                            </a>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="date-container">
                                            <span class="date-text"><c:out value="${app.registeredDate}"/></span>
                                            <span class="time-text"><c:out value="${app.registeredTime}"/></span>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge status-${fn:toLowerCase(app.status)}">
                                            <c:choose>
                                                <c:when test="${app.status == 'PENDING'}">Chờ duyệt</c:when>
                                                <c:when test="${app.status == 'APPROVED'}">Đã duyệt</c:when>
                                                <c:otherwise><c:out value="${app.status}"/></c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td>
                                        <div class="actions-cell">
                                            <form action="${pageContext.request.contextPath}/admin/seller-applications/approve" method="POST" style="display:inline;">
                                                <input type="hidden" name="id" value="${app.id}">
                                                <button type="submit" class="action-btn-circle approve-btn" title="Duyệt yêu cầu">
                                                    <i data-lucide="check-circle" class="action-btn-icon"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" style="text-align: center; padding: 40px 20px; color: var(--text-muted);">
                                    <i data-lucide="inbox" style="width: 40px; height: 40px; margin-bottom: 12px; opacity: 0.4;"></i>
                                    <p style="font-size: 15px; font-weight: 600;">Không có yêu cầu đăng ký nào</p>
                                    <p style="font-size: 13px; margin-top: 4px;">Hiện tại chưa có đơn đăng ký nào phù hợp với bộ lọc.</p>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Thanh phân trang động -->
            <div class="table-footer">
                <div class="pagination-list">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications?page=${currentPage - 1}&status=${status}&search=${search}&date=${date}" class="page-link nav-text">&lt; Trước</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications?page=${i}&status=${status}&search=${search}&date=${date}"
                           class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/admin/seller-applications?page=${currentPage + 1}&status=${status}&search=${search}&date=${date}" class="page-link nav-text">Tiếp &gt;</a>
                    </c:if>
                </div>
            </div>
        </section>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof lucide !== 'undefined') {
            // Khởi tạo các icon từ thư viện Lucide
            lucide.createIcons();
        }
    });
</script>
</body>
</html>
