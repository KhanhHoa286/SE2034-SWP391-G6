<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Lịch sử đơn hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Fonts and Icons -->
    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- CSS and Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/list-orders.css">
</head>
<body class="profile-body">
<jsp:include page="/common/header.jsp" />

<div class="profile-layout">

    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>

    <!-- Main Content -->
    <main class="profile-main">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="javascript:history.back()" class="text-dark text-decoration-none"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI</a>
        </div>

        <div class="profile-container">

            <div class="orders-header-container">
                <div class="orders-header-text">
                    <h1>Lịch sử đơn hàng</h1>
<%--                    <p>Theo dõi và quản lý các giao dịch của bạn tại MODA Archive.</p>--%>
                </div>
                <form class="orders-filter-bar" action="order-list" method="GET">
                    <div class="filter-group">
                        <label for="statusFilter">Trạng thái:</label>
                        <select id="statusFilter" name="status" class="filter-select" onchange="this.form.submit()">
                            <option value="">Tất cả</option>
                            <c:forEach items="${subOrderStatus}" var="status">
                                <option value="${status}" ${orderRequest.status eq status ? 'selected' : ''}>
                                    ${status.displayName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label for="startDate">Từ ngày:</label>
                        <input type="date" id="startDate" name="from_date" class="filter-input" value="${orderRequest.fromDate}">
                    </div>
                    <div class="filter-group">
                        <label for="endDate">Đến ngày:</label>
                        <input type="date" id="endDate" name="to_date" class="filter-input" value="${orderRequest.toDate}">
                    </div>
                    <button type="submit" class="btn-filter" id="filterBtn"><i class="fa-solid fa-filter"></i> Lọc</button>
                </form>
                <div><span id="errorDate" class="text-danger text-end" style="display:none; margin-left:300px">* Ngày bắt đầu nên nhỏ hơn ngày kết thúc!</span></div>
            </div>

            <div class="orders-table-wrapper">
                <table class="orders-table">
                    <thead>
                    <tr>
                        <th>Mã đơn hàng</th>
                        <th>Cửa hàng</th>
                        <th>Ngày đặt</th>
                        <th>Trạng thái</th>
                        <th>Tổng cộng</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- Order 1 -->
                    <c:if test="${empty orderResponse.orderHistoryResponseList}">
                        <tr>
                            <td colspan="6" class="text-center">
                                <p style="color: #888888; margin: 0; padding: 15px 0;">
                                    Lịch sử đơn hàng trống!
                                </p>
                            </td>
                        </tr>
                    </c:if>
                    <c:forEach items="${orderResponse.orderHistoryResponseList}" var="order">
                    <tr>
                        <td class="order-id">#MODA${order.subOrderId}</td>
                        <th class="order-date">${order.shopName}</th>
                        <td class="order-date">${order.createdAtFormat}</td>
                        <td><span class="status-badge
                            ${order.status == 'PENDING' ? 'status-pending' : ''}
                            ${order.status == 'CONFIRMED' ? 'status-preparing' : ''}
                            ${order.status == 'PREPARING' ? 'status-preparing' : ''}
                            ${order.status == 'SHIPPING' ? 'status-shipping' : ''}
                            ${order.status == 'DELIVERED' ? 'status-completed' : ''}
                            ${order.status == 'CANCELLED' ? 'status-cancelled' : ''}
                        ">${order.status.displayName}</span></td>
                        <td class="order-total"><fmt:formatNumber value="${order.totalAmount}" type="currency" maxFractionDigits="0"></fmt:formatNumber> </td>
                        <td>
                            <div class="action-links">
                                <c:if test="${order.status == 'SHIPPING'}">
                                <button class="btn-received" onclick="updateStatusOrder('${pageContext.request.contextPath}',${order.subOrderId},'${order.paymentMethod}',${order.masterOrderId}, this)">Đã nhận được hàng</button>
                                </c:if>
                                <c:if test="${order.status == 'DELIVERED'}">
                                    <button class="btn-received update-status-order">Đã nhận hàng</button>
                                </c:if>
                        <a href="${pageContext.request.contextPath}/customer/view-order?sub_order_id=${order.subOrderId}" class="btn-view-details">Xem chi tiết</a>
                            </div>
                        </td>
                    </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:set var="filterPayload" value="&from_date=${orderRequest.fromDate}&to_date=${orderRequest.toDate}&status=${orderRequest.status}"></c:set>
            <c:if test="${orderResponse.totalPage > 1}">
            <div class="moda-pagination">
                <c:if test="${orderResponse.currentPage > 1}">
                <a href="${pageContext.request.contextPath}/customer/order-list?page=${orderResponse.currentPage - 1}${filterPayload}" class="moda-page-link"><i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC</a>
                </c:if>
                    <c:forEach begin="1" end="${orderResponse.totalPage}" var="i">
                <a href="${pageContext.request.contextPath}/customer/order-list?page=${i}${filterPayload}" class="moda-page-num ${i == orderResponse.currentPage ? 'active' : ''}">${i}</a>
                </c:forEach>
                <c:if test="${orderResponse.currentPage < orderResponse.totalPage}">
                <a href="${pageContext.request.contextPath}/customer/order-list?page=${orderResponse.currentPage + 1}${filterPayload}" class="moda-page-link">SAU &nbsp; <i class="fa-solid fa-chevron-right"></i></a>
                </c:if>
                    </div>
            </c:if>
        </div>
    </main>

</div>
<%--<jsp:include page="/common/footer.jsp" />--%>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/list-order.js"></script>
<script>
    const startDate = document.getElementById("startDate");
    const endDate = document.getElementById("endDate");
    const errorDate = document.getElementById("errorDate");
    const filterBtn = document.getElementById("filterBtn");

    function validateDate() {

        if (startDate.value && endDate.value) {
            if (startDate.value > endDate.value) {
                errorDate.style.display = "block";
                filterBtn.disabled = true;
                filterBtn.style.opacity = "0.2";
            } else {
                errorDate.style.display = "none";
                filterBtn.disabled = false;
                filterBtn.style.opacity = "1";
            }
        }
    }
    startDate.addEventListener('change', validateDate);
    endDate.addEventListener('change',validateDate);

    window.addEventListener("pageshow", function (event) {
        if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
            window.location.reload();
        }
    });
</script>
</body>
</html>
