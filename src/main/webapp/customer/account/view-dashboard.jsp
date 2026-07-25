<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Bảng điều khiển</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <!-- Font Awesome cho header chung nếu cần -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Bootstrap nếu header chung đang dùng -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">

    <!-- CSS profile để dùng lại layout + sidebar hiện có -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=20260722-shop-toast">

    <!-- CSS riêng dashboard -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/customer-dashboard.css?v=1">
</head>

<body class="profile-body dashboard-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout dashboard-layout">

    <!-- Sidebar dùng chung -->
    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="dashboard" />
    </jsp:include>

    <!-- Main Content -->
    <main class="profile-main dashboard-main">
        <div class="dashboard-container">

            <c:if test="${not empty successMessage}">
                <div class="dashboard-alert dashboard-alert--success">
                    <c:out value="${successMessage}" />
                </div>
            </c:if>

            <!-- Greeting -->
            <section class="dashboard-hero">
                <h1>
                    Xin chào,
                    <span>
                        <c:out value="${empty fullNameText ? 'Khách hàng MODA' : fullNameText}" />
                    </span>
                </h1>
                <p>Chào mừng bạn trở lại. Đây là tóm tắt hoạt động đơn hàng của bạn.</p>
            </section>

            <!-- Stats -->
            <section class="dashboard-stats" aria-label="Thống kê đơn hàng">
                <article class="dashboard-stat-card">
                    <div>
                        <span class="dashboard-stat-label">Tổng đơn hàng</span>
                        <strong>
                            <c:out value="${empty totalOrders ? 0 : totalOrders}" />
                        </strong>
                    </div>
                    <span class="material-symbols-outlined dashboard-stat-icon">shopping_bag</span>
                </article>

                <article class="dashboard-stat-card">
                    <div>
                        <span class="dashboard-stat-label">Đang giao</span>
                        <strong>
                            <c:out value="${empty shippingOrders ? 0 : shippingOrders}" />
                        </strong>
                    </div>
                    <span class="material-symbols-outlined dashboard-stat-icon">local_shipping</span>
                </article>
            </section>

            <!-- Recent Orders -->
            <section class="dashboard-orders-card">
                <div class="dashboard-orders-head">
                    <div>
                        <h2>5 Đơn hàng gần nhất</h2>
                        <p>Theo dõi nhanh các đơn hàng mới nhất của bạn.</p>
                    </div>

                    <a class="dashboard-view-all"
                       href="${pageContext.request.contextPath}/customer/order-list">
                        Xem tất cả
                    </a>
                </div>

                <div class="dashboard-table-wrap">
                    <table class="dashboard-orders-table">
                        <thead>
                        <tr>
                            <th>Mã ĐH</th>
                            <th>Cửa hàng</th>
                            <th>Ngày</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th class="dashboard-action-col">Hành động</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:choose>
                            <c:when test="${empty recentOrders}">
                                <tr>
                                    <td colspan="6" class="dashboard-empty">
                                        Bạn chưa có đơn hàng nào.
                                    </td>
                                </tr>
                            </c:when>

                            <c:otherwise>
                                <c:forEach items="${recentOrders}" var="order">
                                    <tr>
                                        <td class="dashboard-order-code">
                                            #MODA<c:out value="${order.subOrderId}" />
                                        </td>

                                        <td>
                                            <c:out value="${order.shopName}" />
                                        </td>

                                        <td class="dashboard-muted">
                                            <c:out value="${order.createdAtFormat}" />
                                        </td>

                                        <td class="dashboard-order-total">
                                            <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0'đ'" />
                                        </td>

                                        <td>
                                            <span class="dashboard-status
                                                ${order.status.name() == 'PENDING' ? 'dashboard-status--pending' : ''}
                                                ${order.status.name() == 'CONFIRMED' ? 'dashboard-status--confirmed' : ''}
                                                ${order.status.name() == 'PREPARING' ? 'dashboard-status--preparing' : ''}
                                                ${order.status.name() == 'SHIPPING' ? 'dashboard-status--shipping' : ''}
                                                ${order.status.name() == 'DELIVERED' ? 'dashboard-status--completed' : ''}
                                                ${order.status.name() == 'CANCELLED' ? 'dashboard-status--cancelled' : ''}">
                                                <c:out value="${order.status.displayName}" />
                                            </span>
                                        </td>

                                        <td class="dashboard-action-col">
                                            <a class="dashboard-detail-link"
                                               href="${pageContext.request.contextPath}/customer/view-order?sub_order_id=${order.subOrderId}">
                                                Xem chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>

        </div>
    </main>

</div>


</body>
</html>
