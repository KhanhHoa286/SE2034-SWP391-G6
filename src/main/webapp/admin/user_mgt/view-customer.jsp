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
    <title>Chi tiết khách hàng: ${not empty customer.fullName ? customer.fullName : 'N/A'} - MODA Admin</title>
    <meta name="description" content="Xem chi tiết thông tin và lịch sử mua hàng của khách hàng trong hệ thống MODA Admin.">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin/view-customer.css?v=1.2">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">

    <%-- ═══════════════════════════════════════
         SIDEBAR
         ═══════════════════════════════════════ --%>
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <!-- Brand -->
            <div class="sidebar-brand">
                <span class="sidebar-brand-name">MODA Admin</span>
                <span class="sidebar-subtitle">Bảng điều khiển Super Admin</span>
            </div>

            <!-- Navigation -->
            <nav class="sidebar-nav">
                <ul>
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/admin/dashboard/overview">
                            <i data-lucide="layout-dashboard" class="menu-icon"></i>
                            <span>Tổng quan</span>
                        </a>
                    </li>
                    <li class="menu-item active">
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
                    <li class="menu-item">
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
            </nav>

        </div>
    </aside>

    <%-- ═══════════════════════════════════════
         MAIN CONTENT
         ═══════════════════════════════════════ --%>
    <main class="main-content">

        <%-- Topbar --%>
        <div class="topbar" style="justify-content: flex-end;">
            <div class="topbar-actions">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg"
                     alt="Avatar" class="topbar-avatar" />
            </div>
        </div>

        <%-- Page Header --%>
        <section class="page-header">
            <div class="header-left">
                <a href="${pageContext.request.contextPath}/admin/user-management" class="btn-back" title="Quay lại">
                    <i data-lucide="arrow-left" class="btn-back-icon"></i>
                </a>
                <div class="header-info">
                    <h1>Chi tiết khách hàng:
                        <c:out value="${not empty customer.fullName ? customer.fullName : 'Không xác định'}"/>
                    </h1>
                    <span class="header-customer-id">
                        ID Khách hàng: #<c:out value="${not empty customer.userId ? customer.userId : 'CUS-00000'}"/>
                    </span>
                </div>
            </div>

            <%-- Ban / Unban button --%>
            <form action="${pageContext.request.contextPath}/admin/user_mgt/view-customer" method="POST" style="display:inline;">
                <input type="hidden" name="userId" value="${customer.userId}">
                <c:choose>
                    <c:when test="${customer.status == 'BANNED'}">
                        <input type="hidden" name="action" value="unlock">
                        <button type="submit" class="btn-ban" id="btn-unban-account"
                                style="color:#047857;border-color:#6ee7b7;background:#ecfdf5;">
                            <i data-lucide="shield-check" class="btn-ban-icon"></i>
                            Mở khóa tài khoản
                        </button>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="lock">
                        <button type="submit" class="btn-ban" id="btn-ban-account">
                            <i data-lucide="shield-alert" class="btn-ban-icon"></i>
                            Khóa tài khoản
                        </button>
                    </c:otherwise>
                </c:choose>
            </form>
        </section>

        <%-- ═══════════════════════════════════
             DETAIL GRID
             ═══════════════════════════════════ --%>
        <div class="detail-grid">

            <%-- LEFT COLUMN --%>
            <div class="profile-col">

                <%-- Profile Card --%>
                <div class="profile-card">
                    <div class="profile-avatar-wrapper">
                        <c:choose>
                            <c:when test="${not empty customer.avatarUrl}">
                                <img src="${customer.avatarUrl}" alt="Avatar" class="profile-avatar" id="customer-avatar"/>
                            </c:when>
                            <c:otherwise>
                                <div class="profile-avatar-text" id="customer-avatar-text">
                                    <c:choose>
                                        <c:when test="${not empty customer.fullName}">
                                            <c:out value="${fn:substring(customer.fullName, 0, 1)}"/>
                                        </c:when>
                                        <c:otherwise>?</c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="profile-info">
                        <p class="profile-name">
                            <c:out value="${not empty customer.fullName ? customer.fullName : 'Chưa cập nhật'}"/>
                        </p>
                        <c:choose>
                            <c:when test="${customer.status == 'ACTIVE'}">
                                <span class="status-badge status-active">Đang hoạt động</span>
                            </c:when>
                            <c:when test="${customer.status == 'BANNED'}">
                                <span class="status-badge status-banned">Đã bị khóa</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge status-inactive">Tạm ngưng</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- Contact Info --%>
                    <ul class="contact-list">
                        <li class="contact-item">
                            <i data-lucide="mail" class="contact-icon"></i>
                            <a href="mailto:${customer.email}" class="contact-link">
                                <c:out value="${not empty customer.email ? customer.email : 'Chưa cập nhật'}"/>
                            </a>
                        </li>
                        <li class="contact-item">
                            <i data-lucide="phone" class="contact-icon"></i>
                            <span><c:out value="${not empty customer.phone ? customer.phone : 'Chưa cập nhật'}"/></span>
                        </li>
                        <li class="contact-item">
                            <i data-lucide="map-pin" class="contact-icon"></i>
                            <span>Chưa cập nhật</span>
                        </li>
                        <li class="contact-item">
                            <i data-lucide="calendar" class="contact-icon"></i>
                            <span>Ngày tham gia:&nbsp;
                                <c:choose>
                                    <c:when test="${not empty customer.createdAt}">
                                        <fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy"/>
                                    </c:when>
                                    <c:otherwise>Chưa rõ</c:otherwise>
                                </c:choose>
                            </span>
                        </li>
                    </ul>
                </div>

                <%-- Stats Cards --%>
                <div class="stats-grid" style="display: flex; flex-direction: column; gap: 12px;">
                    <div class="stat-card">
                        <p class="stat-label">Tổng đơn hàng</p>
                        <p class="stat-value" id="total-orders">
                            <c:out value="${not empty totalOrders ? totalOrders : 0}"/>
                        </p>
                    </div>
                    <div class="stat-card full-width">
                        <p class="stat-label">Tổng chi tiêu (VNĐ)</p>
                        <p class="stat-value" id="total-spent">
                            <c:choose>
                                <c:when test="${not empty customer.totalSpent}">
                                    <fmt:formatNumber value="${customer.totalSpent}" type="number" groupingUsed="true"/>
                                </c:when>
                                <c:otherwise>0</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>

            </div><%-- /profile-col --%>

            <%-- RIGHT COLUMN: ORDER HISTORY --%>
            <div class="orders-col">
                <div class="orders-card">
                    <div class="orders-card-header">
                        <h2 class="orders-card-title">Lịch sử mua hàng</h2>
                        <a href="${pageContext.request.contextPath}/admin/order_mgt/view-global-orders.jsp?customerId=${customer.userId}"
                           class="btn-view-all" id="btn-view-all-orders">
                            Xem tất cả
                            <i data-lucide="arrow-right" style="width:13px;height:13px;"></i>
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty orderHistory}">
                            <div style="overflow-x:auto;">
                                <table class="orders-table">
                                    <thead>
                                    <tr>
                                        <th>Mã đơn hàng</th>
                                        <th>Ngày đặt</th>
                                        <th>Tổng tiền (VND)</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="order" items="${orderHistory}">
                                        <tr>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/order-detail?id=${order.masterOrderId}"
                                                   class="order-id-link">
                                                    <c:out value="${order.orderCode}"/>
                                                </a>
                                            </td>
                                            <td class="col-date">
                                                <c:choose>
                                                    <c:when test="${not empty order.createdAt}">
                                                        <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="col-amount">
                                                <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.status == 'SUCCESS' or order.status == 'COMPLETED' or order.status == 'THANH_CONG' or order.status == 'DELIVERED'}">
                                                        <span class="order-badge badge-success">Thành công</span>
                                                    </c:when>
                                                    <c:when test="${order.status == 'DELIVERING' or order.status == 'SHIPPING' or order.status == 'DANG_GIAO' or order.status == 'PREPARING' or order.status == 'CONFIRMED'}">
                                                        <span class="order-badge badge-delivering">Đang giao</span>
                                                    </c:when>
                                                    <c:when test="${order.status == 'CANCELED' or order.status == 'CANCELLED' or order.status == 'DA_HUY'}">
                                                        <span class="order-badge badge-canceled">Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="order-badge badge-pending">
                                                            <c:out value="${order.status == 'PENDING' ? 'Chờ xác nhận' : order.status}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <%-- Empty / Demo State --%>
                            <div style="overflow-x:auto;">
                                <table class="orders-table">
                                    <thead>
                                    <tr>
                                        <th>Mã đơn hàng</th>
                                        <th>Ngày đặt</th>
                                        <th>Tổng tiền (VND)</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-001</a></td>
                                        <td class="col-date">20/10/2023</td>
                                        <td class="col-amount">1,250,000</td>
                                        <td><span class="order-badge badge-success">Thành công</span></td>
                                    </tr>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-002</a></td>
                                        <td class="col-date">15/09/2023</td>
                                        <td class="col-amount">850,000</td>
                                        <td><span class="order-badge badge-delivering">Đang giao</span></td>
                                    </tr>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-003</a></td>
                                        <td class="col-date">01/09/2023</td>
                                        <td class="col-amount">3,400,000</td>
                                        <td><span class="order-badge badge-success">Thành công</span></td>
                                    </tr>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-004</a></td>
                                        <td class="col-date">12/08/2023</td>
                                        <td class="col-amount">450,000</td>
                                        <td><span class="order-badge badge-canceled">Đã hủy</span></td>
                                    </tr>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-005</a></td>
                                        <td class="col-date">25/07/2023</td>
                                        <td class="col-amount">2,100,000</td>
                                        <td><span class="order-badge badge-success">Thành công</span></td>
                                    </tr>
                                    <tr>
                                        <td><a href="#" class="order-id-link">#ORD-006</a></td>
                                        <td class="col-date">10/06/2023</td>
                                        <td class="col-amount">950,000</td>
                                        <td><span class="order-badge badge-success">Thành công</span></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div><%-- /orders-col --%>

        </div><%-- /detail-grid --%>

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
