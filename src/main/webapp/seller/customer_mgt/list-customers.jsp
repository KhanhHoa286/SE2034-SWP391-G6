<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kh&#225;ch h&#224;ng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260630a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-customers.css?v=20260630a">
    <script src="https://unpkg.com/lucide@latest"></script>
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
            <strong>Kh&#225;ch h&#224;ng</strong>
        </nav>

        <header class="seller-customers-heading">
            <h1>Kh&#225;ch h&#224;ng</h1>
            <p>Danh s&#225;ch kh&#225;ch &#273;&#227; ph&#225;t sinh &#273;&#417;n h&#224;ng v&#7899;i shop, k&#232;m t&#7893;ng chi ti&#234;u, s&#7889; &#273;&#417;n v&#224; l&#7847;n mua g&#7847;n nh&#7845;t.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="seller-customers-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="seller-customers-metric-grid">
            <article class="seller-customers-metric-card card shadow-sm">
                <span>T&#7893;ng kh&#225;ch</span>
                <strong>${totalCustomers}</strong>
                <small>Kh&#225;ch &#273;&#227; mua h&#224;ng t&#7841;i shop</small>
            </article>
            <article class="seller-customers-metric-card card shadow-sm">
                <span>Kh&#225;ch quay l&#7841;i</span>
                <strong>${returningCustomers}</strong>
                <small>C&#243; t&#7915; 2 &#273;&#417;n tr&#7903; l&#234;n</small>
            </article>
            <article class="seller-customers-metric-card card shadow-sm">
                <span>Doanh thu</span>
                <strong><fmt:formatNumber value="${totalCustomerRevenue}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>Kh&#244;ng t&#237;nh &#273;&#417;n &#273;&#227; h&#7911;y</small>
            </article>
        </section>

        <form class="seller-customers-filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/customers" method="GET">
            <input type="text"
                   name="search"
                   class="seller-customers-filter-input form-control"
                   placeholder="T&#236;m t&#234;n, email, s&#7889; &#273;i&#7879;n tho&#7841;i"
                   value="${search}">

            <select name="segment" class="seller-customers-filter-input form-select">
                <option value="" ${empty segment ? 'selected' : ''}>T&#7845;t c&#7843; kh&#225;ch</option>
                <option value="returning" ${segment == 'returning' ? 'selected' : ''}>Kh&#225;ch quay l&#7841;i</option>
            </select>

            <select name="dateRange" class="seller-customers-filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>L&#7847;n mua g&#7847;n nh&#7845;t</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>H&#244;m nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ng&#224;y g&#7847;n nh&#7845;t</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ng&#224;y g&#7847;n nh&#7845;t</option>
            </select>

            <select name="sort" class="seller-customers-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>M&#7899;i mua g&#7847;n &#273;&#226;y</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>L&#226;u nh&#7845;t ch&#432;a mua</option>
                <option value="spend_desc" ${sort == 'spend_desc' ? 'selected' : ''}>Chi ti&#234;u cao nh&#7845;t</option>
                <option value="orders_desc" ${sort == 'orders_desc' ? 'selected' : ''}>Nhi&#7873;u &#273;&#417;n nh&#7845;t</option>
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>T&#234;n A-Z</option>
            </select>

            <button type="submit" class="btn btn-dark">L&#7885;c</button>
        </form>

        <section class="seller-customers-table-card card shadow-sm">
            <table class="seller-customers-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Kh&#225;ch h&#224;ng</th>
                    <th>Li&#234;n h&#7879;</th>
                    <th>S&#7889; &#273;&#417;n</th>
                    <th>T&#7893;ng chi ti&#234;u</th>
                    <th>L&#7847;n mua g&#7847;n nh&#7845;t</th>
                    <th>&#272;&#417;n g&#7847;n nh&#7845;t</th>
                    <th>S&#7843;n ph&#7849;m</th>
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
                                    <small>${customer.deliveredOrders} &#273;&#417;n ho&#224;n t&#7845;t</small>
                                </td>
                                <td class="seller-customers-amount">
                                    <fmt:formatNumber value="${customer.totalSpent}" type="number" maxFractionDigits="0"/>&#273;
                                    <small>TB <fmt:formatNumber value="${customer.averageOrderValue}" type="number" maxFractionDigits="0"/>&#273;/&#273;&#417;n</small>
                                </td>
                                <td>
                                    <fmt:formatDate value="${customer.lastOrderAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    <small>Mua l&#7847;n &#273;&#7847;u: <fmt:formatDate value="${customer.firstOrderAt}" pattern="dd/MM/yyyy"/></small>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty customer.lastSubOrderId}">
                                            <a class="seller-customers-open-link"
                                               href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${customer.lastSubOrderId}">
                                                #SUB-${customer.lastSubOrderId}
                                            </a>
                                            <small><fmt:formatNumber value="${customer.lastOrderAmount}" type="number" maxFractionDigits="0"/>&#273;</small>
                                        </c:when>
                                        <c:otherwise>
                                            <span>Ch&#432;a c&#243;</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <strong>${customer.purchasedProducts}</strong>
                                    <small>${customer.totalQuantity} s&#7843;n ph&#7849;m &#273;&#227; mua</small>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="seller-customers-empty-state">
                                Ch&#432;a c&#243; kh&#225;ch h&#224;ng n&#224;o ph&#249; h&#7907;p v&#7899;i b&#7897; l&#7885;c.
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
