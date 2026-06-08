<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallet Balance - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-wallet.css">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<jsp:include page="/public/header.jsp"/>

<div class="wallet-shell">
    <aside class="wallet-sidebar">
        <div class="sidebar-block">
            <h2>Seller Center</h2>
            <nav class="seller-nav">
                <a href="${pageContext.request.contextPath}/seller/dashboard/view-seller-dashboard.jsp">
                    <i data-lucide="layout-grid"></i>
                    <span>Tổng quan</span>
                </a>
                <a class="active" href="${pageContext.request.contextPath}/seller/finance/view-wallet">
                    <i data-lucide="wallet-cards"></i>
                    <span>Ví tiền</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/order/list-seller-orders.jsp">
                    <i data-lucide="archive"></i>
                    <span>Đơn hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/customer_mgt/list-customers.jsp">
                    <i data-lucide="users"></i>
                    <span>Khách hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/product/list-seller-products.jsp">
                    <i data-lucide="clipboard-check"></i>
                    <span>Sản phẩm</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/voucher/list-seller-voucher.jsp">
                    <i data-lucide="badge-percent"></i>
                    <span>Khuyến mãi</span>
                </a>
                <a href="${pageContext.request.contextPath}/public/shop/view-shop.jsp">
                    <i data-lucide="store"></i>
                    <span>Hồ sơ shop</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/config/edit-shipping-settings.jsp">
                    <i data-lucide="truck"></i>
                    <span>Cấu hình giao hàng</span>
                </a>
            </nav>
        </div>

        <div class="sidebar-block account-block">
            <p>Tài khoản của tôi</p>
            <nav class="seller-nav">
                <a href="${pageContext.request.contextPath}/home">
                    <i data-lucide="shopping-bag"></i>
                    <span>Về trang mua sắm</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer/order/list-orders.jsp">
                    <i data-lucide="receipt-text"></i>
                    <span>Đơn mua của tôi</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer/account/view-profile.jsp">
                    <i data-lucide="circle-user-round"></i>
                    <span>Hồ sơ cá nhân</span>
                </a>
            </nav>
        </div>

        <div class="sidebar-footer">
            <a href="#">
                <i data-lucide="circle-help"></i>
                <span>Hỗ trợ</span>
            </a>
            <a href="#">
                <i data-lucide="log-out"></i>
                <span>Đăng xuất</span>
            </a>
        </div>
    </aside>

    <main class="wallet-main container-fluid">
        <nav class="wallet-breadcrumb d-flex align-items-center">
            <span>Seller Finance</span>
            <span>/</span>
            <strong>Wallet Balance</strong>
        </nav>

        <header class="wallet-heading">
            <h1>Wallet Balance</h1>
            <p>The shop sees available funds and funds awaiting reconciliation.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="wallet-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="metric-grid">
            <article class="metric-card card shadow-sm">
                <span>Revenue</span>
                <strong><fmt:formatNumber value="${revenue}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>delivered after commission</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>Orders</span>
                <strong>${totalOrders}</strong>
                <small>${urgentOrders} urgent</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>Wallet</span>
                <strong><fmt:formatNumber value="${availableBalance}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>ready for payout</small>
            </article>
            <article class="metric-card card shadow-sm">
                <span>Rating</span>
                <strong><fmt:formatNumber value="${averageRating}" minFractionDigits="1" maxFractionDigits="1"/>/5</strong>
                <small>from ${reviewCount} reviews</small>
            </article>
        </section>

        <form class="filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/finance/view-wallet" method="GET">
            <input type="text"
                   name="search"
                   class="filter-input form-control"
                   placeholder="Search"
                   value="${search}">

            <select name="status" class="filter-input form-select">
                <option value="" ${empty status ? 'selected' : ''}>Status</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Pending</option>
                <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                <option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
            </select>

            <select name="dateRange" class="filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Date</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>Today</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>Last 7 days</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>Last 30 days</option>
            </select>

            <select name="sort" class="filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Sort</option>
                <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Oldest</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>Amount high to low</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>Amount low to high</option>
            </select>

            <button type="submit" class="btn btn-dark">Filter</button>
        </form>

        <section class="table-card card shadow-sm">
            <table class="wallet-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Owner</th>
                    <th>Amount</th>
                    <th>Bank</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty payoutRequests}">
                        <c:forEach var="item" items="${payoutRequests}">
                            <tr>
                                <td>${item.code}</td>
                                <td>${item.owner}</td>
                                <td class="amount"><fmt:formatNumber value="${item.amount}" type="number" maxFractionDigits="0"/>đ</td>
                                <td>${item.bank}</td>
                                <td>
                                    <span class="status-badge status-${item.status}">${item.status}</span>
                                </td>
                                <td><a class="open-link" href="#">Open</a></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="empty-state">No payout requests found.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </section>

        <div class="wallet-actions">
            <a class="primary-action btn btn-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-request">Request Payout</a>
            <a class="secondary-action btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-account">Add Bank Account</a>
        </div>
    </main>
</div>

<jsp:include page="/public/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
</script>
</body>
</html>
