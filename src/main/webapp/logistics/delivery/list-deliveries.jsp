<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách đơn giao - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-deliveries.css?v=20260707a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="delivery-list-shell">
    <%
        request.setAttribute("activePage", "delivery-list");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="delivery-list-main container-fluid">
        <nav class="delivery-list-breadcrumb d-flex align-items-center">
            <span>Trung tâm giao hàng</span>
            <span>/</span>
            <strong>Danh sách đơn</strong>
        </nav>

        <header class="delivery-list-heading">
            <h1>Danh sách đơn giao</h1>
            <p>Hiển thị các đơn đang chuẩn bị hàng để nhân viên giao hàng đến cửa hàng lấy hàng.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="delivery-list-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="delivery-list-metric-grid">
            <article class="delivery-list-metric-card card shadow-sm">
                <span>Chờ lấy hàng</span>
                <strong>${totalWaiting}</strong>
                <small>Đơn đang chuẩn bị tại cửa hàng</small>
            </article>

            <article class="delivery-list-metric-card card shadow-sm">
                <span>Đơn hôm nay</span>
                <strong>${todayWaiting}</strong>
                <small>Đơn mới được chuyển sang chuẩn bị</small>
            </article>

            <article class="delivery-list-metric-card card shadow-sm">
                <span>Đã thanh toán</span>
                <strong>${paidOrders}</strong>
                <small>Không cần thu tiền người nhận</small>
            </article>

            <article class="delivery-list-metric-card card shadow-sm">
                <span>Tiền cần thu</span>
                <strong><fmt:formatNumber value="${collectAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Tổng tiền với đơn chưa thanh toán</small>
            </article>
        </section>

        <form class="delivery-list-filter-card card shadow-sm"
              action="${pageContext.request.contextPath}/logistics/delivery/list"
              method="GET">
            <input type="text"
                   name="search"
                   class="delivery-list-filter-input form-control"
                   placeholder="Tìm mã vận đơn, mã đơn, cửa hàng, người nhận"
                   value="${search}">

            <select name="payment" class="delivery-list-filter-input form-select">
                <option value="" ${empty payment ? 'selected' : ''}>Tất cả thanh toán</option>
                <option value="paid" ${payment == 'paid' ? 'selected' : ''}>Đã thanh toán</option>
                <option value="cod" ${payment == 'cod' ? 'selected' : ''}>Cần thu tiền</option>
            </select>

            <select name="dateRange" class="delivery-list-filter-input form-select">
                <option value="" ${empty dateRange ? 'selected' : ''}>Thời gian chuẩn bị</option>
                <option value="today" ${dateRange == 'today' ? 'selected' : ''}>Hôm nay</option>
                <option value="7days" ${dateRange == '7days' ? 'selected' : ''}>7 ngày gần nhất</option>
                <option value="30days" ${dateRange == '30days' ? 'selected' : ''}>30 ngày gần nhất</option>
            </select>

            <select name="sort" class="delivery-list-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Mới nhất</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>Giá trị cao đến thấp</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>Giá trị thấp đến cao</option>
            </select>

            <button type="submit" class="btn btn-dark">Lọc</button>
        </form>

        <section class="delivery-list-table-card card shadow-sm">
            <table class="delivery-list-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Mã vận đơn</th>
                    <th>Cửa hàng lấy hàng</th>
                    <th>Người nhận</th>
                    <th>Sản phẩm</th>
                    <th>Ngày chuẩn bị</th>
                    <th>Thanh toán</th>
                    <th>Tiền thu</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${not empty deliveries}">
                        <c:forEach var="delivery" items="${deliveries}">
                            <tr class="delivery-list-clickable-row"
                                data-href="${pageContext.request.contextPath}/logistics/delivery/view-delivery.jsp?deliveryId=${delivery.deliveryId}"
                                tabindex="0"
                                role="link"
                                aria-label="Xem vận đơn ${delivery.trackingNumber}">
                                <td>
                                    <strong class="delivery-list-code">${delivery.trackingNumber}</strong>
                                    <small>#SUB-${delivery.subOrderId} / #MO-${delivery.masterOrderId}</small>
                                </td>

                                <td class="delivery-list-address">
                                    <strong>${delivery.shopName}</strong>
                                    <small>${delivery.sellerPhone}</small>
                                    <small>${delivery.pickupAddress}</small>
                                </td>

                                <td class="delivery-list-address">
                                    <strong>${delivery.receiverName}</strong>
                                    <small>${delivery.receiverPhone}</small>
                                    <small>${delivery.shippingAddress}</small>
                                </td>

                                <td class="delivery-list-products">
                                    <span>${empty delivery.productsSummary ? 'Chưa có sản phẩm' : delivery.productsSummary}</span>
                                    <small>${delivery.itemCount} dòng hàng - ${delivery.totalQuantity} sản phẩm</small>
                                </td>

                                <td>
                                    <fmt:formatDate value="${delivery.sellerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    <small>Khách đặt: <fmt:formatDate value="${delivery.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                                </td>

                                <td>
                                    <span class="delivery-list-payment">${delivery.paymentMethod}</span>
                                    <small>
                                        <c:choose>
                                            <c:when test="${delivery.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                            <c:otherwise>Cần thu tiền</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>

                                <td class="delivery-list-amount">
                                    <fmt:formatNumber value="${delivery.collectAmount}" type="number" maxFractionDigits="0"/>đ
                                </td>

                                <td>
                                    <span class="delivery-list-status-badge status-${delivery.deliveryStatus}">
                                        <c:choose>
                                            <c:when test="${delivery.deliveryStatus == 'ASSIGNED'}">Chờ lấy hàng</c:when>
                                            <c:when test="${delivery.deliveryStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
                                            <c:when test="${delivery.deliveryStatus == 'IN_TRANSIT'}">Đang giao</c:when>
                                            <c:when test="${delivery.deliveryStatus == 'DELIVERED'}">Đã giao</c:when>
                                            <c:when test="${delivery.deliveryStatus == 'FAILED'}">Giao thất bại</c:when>
                                            <c:otherwise>${delivery.deliveryStatus}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>

                                <td>
                                    <a class="delivery-list-open-link"
                                       href="${pageContext.request.contextPath}/logistics/delivery/view-delivery.jsp?deliveryId=${delivery.deliveryId}">
                                        <i data-lucide="eye"></i>
                                        <span>Xem</span>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="9" class="delivery-list-empty-state">
                                Chưa có đơn nào đang chờ lấy hàng.
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

    document.querySelectorAll('.delivery-list-clickable-row').forEach(function (row) {
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
