<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>S&#7889; d&#432; v&#237; - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611d">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-wallet.css?v=20260611d">
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>

<div class="wallet-shell">
    <%
        request.setAttribute("activePage", "finance");
        request.setAttribute("sellerSidebarClass", "sidebar");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="wallet-main container-fluid">
        <nav class="wallet-breadcrumb d-flex align-items-center">
            <span>T&#224;i ch&#237;nh ng&#432;&#7901;i b&#225;n</span>
            <span>/</span>
            <strong>S&#7889; d&#432; v&#237;</strong>
        </nav>

        <header class="wallet-heading">
            <h1>S&#7889; d&#432; v&#237;</h1>
            <p>Theo d&#245;i s&#7889; ti&#7873;n c&#243; th&#7875; r&#250;t, s&#7889; ti&#7873;n &#273;ang ch&#7901; x&#7917; l&#253; v&#224; l&#7883;ch s&#7917; y&#234;u c&#7847;u thanh to&#225;n.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="wallet-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="metric-grid">
            <article class="metric-card card shadow-sm">
                <span>Doanh thu</span>
                <strong><fmt:formatNumber value="${revenue}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>T&#7915; c&#225;c &#273;&#417;n &#273;&#227; giao th&#224;nh c&#244;ng</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>&#272;&#417;n h&#224;ng</span>
                <strong>${totalOrders}</strong>
                <small>${urgentOrders} &#273;&#417;n c&#7847;n x&#7917; l&#253;</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>V&#237;</span>
                <strong><fmt:formatNumber value="${availableBalance}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>s&#7861;n s&#224;ng y&#234;u c&#7847;u r&#250;t</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>&#272;&#225;nh gi&#225;</span>
                <strong><fmt:formatNumber value="${averageRating}" minFractionDigits="1" maxFractionDigits="1"/>/5</strong>
                <small>t&#7915; ${reviewCount} l&#432;&#7907;t &#273;&#225;nh gi&#225;</small>
            </article>
        </section>

        <form class="filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/finance/view-wallet" method="GET">
            <input type="text"
                   name="search"
                   class="filter-input form-control"
                   placeholder="T&#236;m ki&#7871;m"
                   value="${search}">

            <select name="status" class="filter-input form-select">
                <option value="" ${empty status ? 'selected' : ''}>Tr&#7841;ng th&#225;i</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Ch&#7901; x&#7917; l&#253;</option>
                <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>&#272;&#227; duy&#7879;t</option>
                <option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>T&#7915; ch&#7889;i</option>
            </select>

            <select name="dateRange" class="filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Th&#7901;i gian</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>H&#244;m nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ng&#224;y g&#7847;n nh&#7845;t</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ng&#224;y g&#7847;n nh&#7845;t</option>
            </select>

            <select name="sort" class="filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>S&#7855;p x&#7871;p</option>
                <option value="newest" ${sort == 'newest' ? 'selected' : ''}>M&#7899;i nh&#7845;t</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>C&#361; nh&#7845;t</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>S&#7889; ti&#7873;n cao &#273;&#7871;n th&#7845;p</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>S&#7889; ti&#7873;n th&#7845;p &#273;&#7871;n cao</option>
            </select>

            <button type="submit" class="btn btn-dark">L&#7885;c</button>
        </form>

        <section class="table-card card shadow-sm">
            <table class="wallet-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>M&#227;</th>
                    <th>Ch&#7911; t&#224;i kho&#7843;n</th>
                    <th>S&#7889; ti&#7873;n</th>
                    <th>Ng&#226;n h&#224;ng</th>
                    <th>Tr&#7841;ng th&#225;i</th>
                    <th>Thao t&#225;c</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty payoutRequests}">
                        <c:forEach var="item" items="${payoutRequests}">
                            <tr>
                                <td>${item.code}</td>
                                <td>${item.owner}</td>
                                <td class="amount"><fmt:formatNumber value="${item.amount}" type="number" maxFractionDigits="0"/>&#273;</td>
                                <td>${item.bank}</td>
                                <td>
                                    <span class="status-badge status-${item.status}">
                                        <c:choose>
                                            <c:when test="${item.status == 'PENDING'}">Ch&#7901; x&#7917; l&#253;</c:when>
                                            <c:when test="${item.status == 'APPROVED'}">&#272;&#227; duy&#7879;t</c:when>
                                            <c:when test="${item.status == 'REJECTED'}">T&#7915; ch&#7889;i</c:when>
                                            <c:otherwise>${item.status}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td><a class="open-link" href="#">Xem</a></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="empty-state">Ch&#432;a c&#243; y&#234;u c&#7847;u r&#250;t ti&#7873;n.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </section>

        <div class="wallet-actions">
            <a class="primary-action btn btn-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-request">Y&#234;u c&#7847;u r&#250;t ti&#7873;n</a>
            <a class="secondary-action btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-account">Th&#234;m t&#224;i kho&#7843;n ng&#226;n h&#224;ng</a>
        </div>
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
