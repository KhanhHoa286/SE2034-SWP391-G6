<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260630a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-seller-orders.css?v=20260630a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-orders-shell">
    <%
        request.setAttribute("activePage", "orders");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="seller-orders-main container-fluid">
        <nav class="seller-orders-breadcrumb d-flex align-items-center">
            <span>Seller Center</span>
            <span>/</span>
            <strong>Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng</strong>
        </nav>

        <header class="seller-orders-heading">
            <h1>Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng</h1>
            <p>Theo d&#245;i c&#225;c &#273;&#417;n h&#224;ng kh&#225;ch &#273;&#227; x&#225;c nh&#7853;n mua, tr&#7841;ng th&#225;i x&#7917; l&#253;, thanh to&#225;n v&#224; th&#244;ng tin giao h&#224;ng.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="seller-orders-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="seller-orders-metric-grid">
            <article class="seller-orders-metric-card card shadow-sm">
                <span>T&#7893;ng &#273;&#417;n</span>
                <strong>${totalOrders}</strong>
                <small>&#272;&#417;n &#273;&#227; &#273;&#432;&#7907;c kh&#225;ch &#273;&#7863;t mua</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Ch&#7901; x&#225;c nh&#7853;n</span>
                <strong>${pendingOrders}</strong>
                <small>C&#7847;n seller ki&#7875;m tra v&#224; x&#7917; l&#253;</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>&#272;ang x&#7917; l&#253;</span>
                <strong>${processingOrders}</strong>
                <small>&#272;&#227; x&#225;c nh&#7853;n, chu&#7849;n b&#7883; ho&#7863;c &#273;ang giao</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Doanh s&#7889; &#273;&#417;n</span>
                <strong><fmt:formatNumber value="${grossAmount}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>Kh&#244;ng t&#237;nh &#273;&#417;n &#273;&#227; h&#7911;y</small>
            </article>
        </section>

        <form class="seller-orders-filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/orders" method="GET">
            <input type="text"
                   name="search"
                   class="seller-orders-filter-input form-control"
                   placeholder="T&#236;m m&#227; &#273;&#417;n, kh&#225;ch h&#224;ng, s&#7843;n ph&#7849;m"
                   value="${search}">

            <select name="status" class="seller-orders-filter-input form-select">
                <option value="" ${empty status ? 'selected' : ''}>T&#7845;t c&#7843; tr&#7841;ng th&#225;i</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Ch&#7901; x&#225;c nh&#7853;n</option>
                <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>&#272;&#227; x&#225;c nh&#7853;n</option>
                <option value="PREPARING" ${status == 'PREPARING' ? 'selected' : ''}>&#272;ang chu&#7849;n b&#7883;</option>
                <option value="SHIPPING" ${status == 'SHIPPING' ? 'selected' : ''}>&#272;ang giao h&#224;ng</option>
                <option value="DELIVERED" ${status == 'DELIVERED' ? 'selected' : ''}>&#272;&#227; ho&#224;n th&#224;nh</option>
                <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>&#272;&#227; h&#7911;y</option>
            </select>

            <select name="dateRange" class="seller-orders-filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Th&#7901;i gian &#273;&#7863;t</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>H&#244;m nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ng&#224;y g&#7847;n nh&#7845;t</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ng&#224;y g&#7847;n nh&#7845;t</option>
            </select>

            <select name="sort" class="seller-orders-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>M&#7899;i nh&#7845;t</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>C&#361; nh&#7845;t</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>Gi&#225; tr&#7883; cao &#273;&#7871;n th&#7845;p</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>Gi&#225; tr&#7883; th&#7845;p &#273;&#7871;n cao</option>
            </select>

            <button type="submit" class="btn btn-dark">L&#7885;c</button>
        </form>

        <section class="seller-orders-table-card card shadow-sm">
            <table class="seller-orders-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>M&#227; &#273;&#417;n</th>
                    <th>Kh&#225;ch h&#224;ng</th>
                    <th>S&#7843;n ph&#7849;m</th>
                    <th>Ng&#224;y &#273;&#7863;t</th>
                    <th>Thanh to&#225;n</th>
                    <th>T&#7893;ng ti&#7873;n</th>
                    <th>Tr&#7841;ng th&#225;i</th>
                    <th>Giao h&#224;ng</th>
                    <th>Thao t&#225;c</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty sellerOrders}">
                        <c:forEach var="order" items="${sellerOrders}">
                            <tr>
                                <td>
                                    <strong class="seller-orders-code">#SUB-${order.subOrderId}</strong>
                                    <small>#MO-${order.masterOrderId}</small>
                                </td>
                                <td>
                                    <strong>${order.customerName}</strong>
                                    <small>${order.customerEmail}</small>
                                    <small>${order.receiverPhone}</small>
                                </td>
                                <td class="seller-orders-products">
                                    <span>${empty order.productsSummary ? 'Chua co san pham' : order.productsSummary}</span>
                                    <small>${order.itemCount} d&#242;ng h&#224;ng &bull; ${order.totalQuantity} s&#7843;n ph&#7849;m</small>
                                </td>
                                <td>
                                    <fmt:formatDate value="${order.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <span class="seller-orders-payment">${order.paymentMethod}</span>
                                    <small>
                                        <c:choose>
                                            <c:when test="${order.paymentStatus == 'PAID'}">&#272;&#227; thanh to&#225;n</c:when>
                                            <c:when test="${order.paymentStatus == 'REFUNDED'}">&#272;&#227; ho&#224;n ti&#7873;n</c:when>
                                            <c:otherwise>Ch&#7901; thanh to&#225;n</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>
                                <td class="seller-orders-amount">
                                    <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/>&#273;
                                </td>
                                <td>
                                    <span class="seller-orders-status-badge status-${order.status}">
                                        <c:choose>
                                            <c:when test="${order.status == 'PENDING'}">Ch&#7901; x&#225;c nh&#7853;n</c:when>
                                            <c:when test="${order.status == 'CONFIRMED'}">&#272;&#227; x&#225;c nh&#7853;n</c:when>
                                            <c:when test="${order.status == 'PREPARING'}">&#272;ang chu&#7849;n b&#7883;</c:when>
                                            <c:when test="${order.status == 'SHIPPING'}">&#272;ang giao</c:when>
                                            <c:when test="${order.status == 'DELIVERED'}">Ho&#224;n th&#224;nh</c:when>
                                            <c:when test="${order.status == 'CANCELLED'}">&#272;&#227; h&#7911;y</c:when>
                                            <c:otherwise>${order.status}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td class="seller-orders-address">
                                    <span>${order.receiverName}</span>
                                    <small>${order.shippingAddress}</small>
                                </td>
                                <td>
                                    <a class="seller-orders-open-link"
                                       href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${order.subOrderId}">
                                        Xem
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="9" class="seller-orders-empty-state">
                                Ch&#432;a c&#243; &#273;&#417;n h&#224;ng n&#224;o ph&#249; h&#7907;p v&#7899;i b&#7897; l&#7885;c.
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
