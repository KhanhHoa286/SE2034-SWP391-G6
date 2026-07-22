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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin/view-customer.css?v=1.4">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">

    <%-- ═══════════════════════════════════════
         SIDEBAR
         ═══════════════════════════════════════ --%>
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
                    <li class="menu-item active">
                        <a href="${pageContext.request.contextPath}/admin/user-management">
                            <span class="menu-text">Người dùng</span>
                        </a>
                    </li>
                    <li class="menu-item">
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

    <%-- ═══════════════════════════════════════
         MAIN CONTENT
         ═══════════════════════════════════════ --%>
    <main class="main-content">


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
                            <%
                                int currentUserId = ((vn.edu.fpt.controller.admin.CustomerDTO) request.getAttribute("customer")).getUserId();
                                String addrStr = "Chưa cập nhật";
                                String dbgErr = "";
                                java.sql.Connection conn = null;
                                try {
                                    conn = new vn.edu.fpt.common.DBContext().getConnection();
                                    if (conn != null) {
                                        boolean found = false;
                                        
                                        // 1. Check addresses table
                                        java.sql.PreparedStatement ps = conn.prepareStatement(
                                            "SELECT a.street_address, w.name as ward_name, p.name as prov_name " +
                                            "FROM addresses a LEFT JOIN wards w ON a.ward_id = w.id " +
                                            "LEFT JOIN provinces p ON w.province_id = p.id WHERE a.user_id = ? ORDER BY a.is_default DESC, a.created_at DESC"
                                        );
                                        ps.setInt(1, currentUserId);
                                        java.sql.ResultSet rs = ps.executeQuery();
                                        if (rs.next()) {
                                            String street = rs.getString("street_address");
                                            String ward = rs.getString("ward_name");
                                            String prov = rs.getString("prov_name");
                                            String temp = "";
                                            if (street != null && !street.trim().isEmpty()) temp += street;
                                            if (ward != null && !ward.trim().isEmpty()) temp += (temp.isEmpty() ? "" : ", ") + ward;
                                            if (prov != null && !prov.trim().isEmpty()) temp += (temp.isEmpty() ? "" : ", ") + prov;
                                            if (!temp.isEmpty()) {
                                                addrStr = temp;
                                                found = true;
                                            }
                                        }
                                        rs.close(); ps.close();

                                        // 2. Check shops table
                                        if (!found) {
                                            ps = conn.prepareStatement(
                                                "SELECT s.street_address, w.name as ward_name, p.name as prov_name " +
                                                "FROM shops s LEFT JOIN wards w ON s.ward_id = w.id " +
                                                "LEFT JOIN provinces p ON w.province_id = p.id WHERE s.owner_id = ?"
                                            );
                                            ps.setInt(1, currentUserId);
                                            rs = ps.executeQuery();
                                            if (rs.next()) {
                                                String street = rs.getString("street_address");
                                                String ward = rs.getString("ward_name");
                                                String prov = rs.getString("prov_name");
                                                String temp = "";
                                                if (street != null && !street.trim().isEmpty()) temp += street;
                                                if (ward != null && !ward.trim().isEmpty()) temp += (temp.isEmpty() ? "" : ", ") + ward;
                                                if (prov != null && !prov.trim().isEmpty()) temp += (temp.isEmpty() ? "" : ", ") + prov;
                                                if (!temp.isEmpty()) {
                                                    addrStr = temp;
                                                    found = true;
                                                }
                                            }
                                            rs.close(); ps.close();
                                        }

                                    } else {
                                        dbgErr = " (Conn Null)";
                                    }
                                } catch (Exception e) {
                                    // Ignore errors instead of showing them on UI, or log them
                                } finally {
                                    if (conn != null) try { conn.close(); } catch(Exception e){}
                                }
                            %>
                            <span><%= addrStr %><%= dbgErr %></span>
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
                        <a href="${pageContext.request.contextPath}/admin/orders?customerId=${customer.userId}"
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
                                                    #ORD-<c:out value="${order.masterOrderId}"/>
                                                </a>
                                            </td>
                                            <td class="col-date">
                                                <c:choose>
                                                    <c:when test="${not empty order.createdAt}">
                                                        <c:out value="${order.createdAtFormat}"/>
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="col-amount">
                                                <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.status.name() == 'DELIVERED'}">
                                                        <span class="order-badge badge-success">Thành công</span>
                                                    </c:when>
                                                    <c:when test="${order.status.name() == 'SHIPPING' or order.status.name() == 'PREPARING' or order.status.name() == 'CONFIRMED'}">
                                                        <span class="order-badge badge-delivering">Đang giao</span>
                                                    </c:when>
                                                    <c:when test="${order.status.name() == 'CANCELLED'}">
                                                        <span class="order-badge badge-canceled">Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="order-badge badge-pending">
                                                            <c:out value="${order.status.displayName}"/>
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
                            <%-- Empty State --%>
                            <div style="padding: 24px; text-align: center; color: #6b7280; font-size: 14px; background: var(--bg-secondary); border-radius: 8px;">
                                Khách hàng này chưa có đơn hàng nào.
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


