<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đơn hàng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-seller-orders.css?v=20260721b">
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
            <strong>Quản lý đơn hàng</strong>
        </nav>

        <header class="seller-orders-heading">
            <h1>Quản lý đơn hàng</h1>
            <p>Theo dõi các đơn hàng khách đã xác nhận mua, trạng thái xử lý, thanh toán và thông tin giao hàng.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="seller-orders-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="seller-orders-metric-grid">
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Tổng đơn</span>
                <strong>${totalOrders}</strong>
                <small>Đơn đã được khách đặt mua</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Chờ xác nhận</span>
                <strong>${pendingOrders}</strong>
                <small>Cần seller kiểm tra và xử lý</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Đang xử lý</span>
                <strong>${processingOrders}</strong>
                <small>Đã xác nhận, chuẩn bị hoặc đang giao</small>
            </article>
            <article class="seller-orders-metric-card card shadow-sm">
                <span>Doanh số đơn</span>
                <strong><fmt:formatNumber value="${grossAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Không tính đơn đã hủy</small>
            </article>
        </section>

        <form class="seller-orders-filter-card card shadow-sm" action="${pageContext.request.contextPath}/seller/orders" method="GET">
            <input type="text"
                   name="search"
                   class="seller-orders-filter-input form-control"
                   placeholder="Tìm mã đơn, khách hàng, sản phẩm"
                   value="${search}">

            <select name="status" class="seller-orders-filter-input form-select">
                <option value="" ${empty status ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chờ xác nhận</option>
                <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                <option value="PREPARING" ${status == 'PREPARING' ? 'selected' : ''}>Đang chuẩn bị</option>
                <option value="SHIPPING" ${status == 'SHIPPING' ? 'selected' : ''}>Đang giao hàng</option>
                <option value="DELIVERED" ${status == 'DELIVERED' ? 'selected' : ''}>Đã hoàn thành</option>
                <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
            </select>

            <select name="dateRange" class="seller-orders-filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Thời gian đặt</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>Hôm nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ngày gần nhất</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ngày gần nhất</option>
            </select>

            <select name="sort" class="seller-orders-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Mới nhất</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>Giá trị cao đến thấp</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>Giá trị thấp đến cao</option>
            </select>

            <button type="submit" class="btn btn-dark">Lọc</button>
        </form>

        <section class="seller-orders-table-card card shadow-sm">
            <table class="seller-orders-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Khách hàng</th>
                    <th>Sản phẩm</th>
                    <th>Ngày đặt</th>
                    <th>Thanh toán</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th>Giao hàng</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty sellerOrders}">
                        <c:forEach var="order" items="${sellerOrders}">
                            <tr class="seller-orders-clickable-row"
                                data-href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${order.subOrderId}"
                                tabindex="0"
                                role="link"
                                aria-label="Xem chi tiet don hang #SUB-${order.subOrderId}">
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
                                    <small>${order.itemCount} dòng hàng • ${order.totalQuantity} sản phẩm</small>
                                </td>
                                <td>
                                    <fmt:formatDate value="${order.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <span class="seller-orders-payment">${order.paymentMethod}</span>
                                    <small>
                                        <c:choose>
                                            <c:when test="${order.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                            <c:when test="${order.paymentStatus == 'REFUNDED'}">Đã hoàn tiền</c:when>
                                            <c:otherwise>Chờ thanh toán</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>
                                <td class="seller-orders-amount">
                                    <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/>đ
                                </td>
                                <td>
                                    <span class="seller-orders-status-badge status-${order.status}">
                                        <c:choose>
                                            <c:when test="${order.status == 'PENDING'}">Chờ xác nhận</c:when>
                                            <c:when test="${order.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                                            <c:when test="${order.status == 'PREPARING'}">Đang chuẩn bị</c:when>
                                            <c:when test="${order.status == 'SHIPPING'}">Đang giao</c:when>
                                            <c:when test="${order.status == 'DELIVERED'}">Hoàn thành</c:when>
                                            <c:when test="${order.status == 'CANCELLED'}">Đã hủy</c:when>
                                            <c:otherwise>${order.status}</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <c:if test="${order.status == 'PREPARING' && order.shipperAssigned}">
                                        <small class="seller-orders-shipper-note">đã được shipper nhận giao</small>
                                    </c:if>
                                </td>
                                <td class="seller-orders-address">
                                    <span>${order.receiverName}</span>
                                    <small>${order.shippingAddress}</small>
                                </td>
                                <td>
                                    <a class="seller-orders-open-link"
                                       href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${order.subOrderId}">
                                        <i data-lucide="eye"></i>
                                        <span>Xem</span>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="9" class="seller-orders-empty-state">
                                Chưa có đơn hàng nào phù hợp với bộ lọc.
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

    document.querySelectorAll('.seller-orders-clickable-row').forEach(function (row) {
        row.addEventListener('click', function (event) {
            if (event.target.closest('a, button, input, select, textarea, label')) {
                return;
            }

            window.location.href = row.dataset.href;
        });

        row.addEventListener('keydown', function (event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                window.location.href = row.dataset.href;
            }
        });
    });
</script>
</body>
</html>
