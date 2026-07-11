<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khách hàng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260630a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-customers.css?v=20260630a">
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<div class="seller-customers-shell">
    <%
        request.setAttribute("activePage", "customers");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="seller-customers-main container-fluid">
        <nav class="seller-customers-breadcrumb d-flex align-items-center">
            <span>Seller Center</span>
            <span>/</span>
            <strong>Khách hàng</strong>
        </nav>

        <header class="seller-customers-heading">
            <h1>Khách hàng</h1>
            <p>Danh sách khách đã phát sinh đơn hàng với shop, kèm tổng chi tiêu, số đơn và lần mua gần nhất.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="seller-customers-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="seller-customers-metric-grid">
            <article class="seller-customers-metric-card card shadow-sm">
                <span>Tổng khách</span>
                <strong>${totalCustomers}</strong>
                <small>Khách đã mua hàng tại shop</small>
            </article>
            <article class="seller-customers-metric-card card shadow-sm">
                <span>Khách quay lại</span>
                <strong>${returningCustomers}</strong>
                <small>Có từ 2 đơn trở lên</small>
            </article>
            <article class="seller-customers-metric-card card shadow-sm">
                <span>Doanh thu</span>
                <strong><fmt:formatNumber value="${totalCustomerRevenue}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Không tính đơn đã hủy</small>
            </article>
        </section>

        <form class="seller-customers-filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/customers" method="GET">
            <input type="text"
                   name="search"
                   class="seller-customers-filter-input form-control"
                   placeholder="Tìm tên, email, số điện thoại"
                   value="${search}">

            <select name="segment" class="seller-customers-filter-input form-select">
                <option value="" ${empty segment ? 'selected' : ''}>Tất cả khách</option>
                <option value="returning" ${segment == 'returning' ? 'selected' : ''}>Khách quay lại</option>
            </select>

            <select name="dateRange" class="seller-customers-filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Lần mua gần nhất</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>Hôm nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ngày gần nhất</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ngày gần nhất</option>
            </select>

            <select name="sort" class="seller-customers-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Mới mua gần đây</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Lâu nhất chưa mua</option>
                <option value="spend_desc" ${sort == 'spend_desc' ? 'selected' : ''}>Chi tiêu cao nhất</option>
                <option value="orders_desc" ${sort == 'orders_desc' ? 'selected' : ''}>Nhiều đơn nhất</option>
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Tên A-Z</option>
            </select>

            <button type="submit" class="btn btn-dark">Lọc</button>
        </form>

        <section class="seller-customers-table-card card shadow-sm">
            <table class="seller-customers-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Khách hàng</th>
                    <th>Liên hệ</th>
                    <th>Số đơn</th>
                    <th>Tổng chi tiêu</th>
                    <th>Lần mua gần nhất</th>
                    <th>Đơn gần nhất</th>
                    <th>Sản phẩm</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty customers}">
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td>
                                    <div class="seller-customers-person">
                                        <div class="seller-customers-avatar">
                                            <c:choose>
                                                <c:when test="${not empty customer.avatarUrl}">
                                                    <img src="${customer.avatarUrl}" alt="${customer.customerName}">
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="user"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <strong>${customer.customerName}</strong>
                                            <small>#CUS-${customer.customerId}</small>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <strong>${customer.email}</strong>
                                    <small>${customer.phone}</small>
                                </td>
                                <td>
                                    <strong>${customer.totalOrders}</strong>
                                    <small>${customer.deliveredOrders} đơn hoàn tất</small>
                                </td>
                                <td class="seller-customers-amount">
                                    <fmt:formatNumber value="${customer.totalSpent}" type="number" maxFractionDigits="0"/>đ
                                    <small>TB <fmt:formatNumber value="${customer.averageOrderValue}" type="number" maxFractionDigits="0"/>đ/đơn</small>
                                </td>
                                <td>
                                    <fmt:formatDate value="${customer.lastOrderAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    <small>Mua lần đầu: <fmt:formatDate value="${customer.firstOrderAt}" pattern="dd/MM/yyyy"/></small>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty customer.lastSubOrderId}">
                                            <a class="seller-customers-open-link"
                                               href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${customer.lastSubOrderId}">
                                                #SUB-${customer.lastSubOrderId}
                                            </a>
                                            <small><fmt:formatNumber value="${customer.lastOrderAmount}" type="number" maxFractionDigits="0"/>đ</small>
                                        </c:when>
                                        <c:otherwise>
                                            <span>Chưa có</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <strong>${customer.purchasedProducts}</strong>
                                    <small>${customer.totalQuantity} sản phẩm đã mua</small>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="seller-customers-empty-state">
                                Chưa có khách hàng nào phù hợp với bộ lọc.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </section>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
</script>
</body>
</html>
