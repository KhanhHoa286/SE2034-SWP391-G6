<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn vận chuyển của tôi - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/list-shipper-orders.css?v=20260707a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="shipper-order-shell">
    <%
        request.setAttribute("activePage", "delivery-my-orders");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="shipper-order-main container-fluid">
        <nav class="shipper-order-breadcrumb d-flex align-items-center">
            <span>Trung tâm giao hàng</span>
            <span>/</span>
            <strong>Đơn vận chuyển của tôi</strong>
        </nav>

        <header class="shipper-order-heading">
            <h1>Đơn vận chuyển của tôi</h1>
            <p>Danh sách các đơn bạn đã nhận. Đơn được nhận sẽ không còn xuất hiện trong danh sách chung để tránh shipper khác nhận trùng.</p>
        </header>

        <c:if test="${not empty successMessage}">
            <div class="shipper-order-alert shipper-order-alert-success alert d-flex align-items-center" role="alert">
                <i data-lucide="check-circle-2"></i>
                <span>${successMessage}</span>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="shipper-order-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <section class="shipper-order-metric-grid">
            <article class="shipper-order-metric-card card shadow-sm">
                <span>Tổng đơn đã nhận</span>
                <strong>${totalOrders}</strong>
                <small>Tất cả đơn đang thuộc shipper hiện tại</small>
            </article>

            <article class="shipper-order-metric-card card shadow-sm">
                <span>Chờ người bán giao</span>
                <strong>${assignedOrders}</strong>
                <small>Đã nhận đơn, chờ cửa hàng bàn giao</small>
            </article>

            <article class="shipper-order-metric-card card shadow-sm">
                <span>Đang vận chuyển</span>
                <strong>${shippingOrders}</strong>
                <small>Người bán đã chuyển đơn sang đang giao</small>
            </article>

            <article class="shipper-order-metric-card card shadow-sm">
                <span>Tiền cần thu</span>
                <strong><fmt:formatNumber value="${collectAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Tổng tiền với đơn chưa thanh toán</small>
            </article>
        </section>

        <form class="shipper-order-filter-card card shadow-sm"
              action="${pageContext.request.contextPath}/logistics/delivery/my-orders"
              method="GET">
            <input type="text"
                   name="search"
                   class="shipper-order-filter-input form-control"
                   placeholder="Tìm mã vận đơn, mã đơn, cửa hàng, người nhận"
                   value="${search}">

            <select name="status" class="shipper-order-filter-input form-select">
                <option value="" ${empty status ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="assigned" ${status == 'assigned' ? 'selected' : ''}>Đã nhận đơn</option>
                <option value="shipping" ${status == 'shipping' ? 'selected' : ''}>Đang vận chuyển</option>
                <option value="delivered" ${status == 'delivered' ? 'selected' : ''}>Đã giao</option>
            </select>

            <select name="payment" class="shipper-order-filter-input form-select">
                <option value="" ${empty payment ? 'selected' : ''}>Tất cả thanh toán</option>
                <option value="paid" ${payment == 'paid' ? 'selected' : ''}>Đã thanh toán</option>
                <option value="cod" ${payment == 'cod' ? 'selected' : ''}>Cần thu tiền</option>
            </select>

            <select name="sort" class="shipper-order-filter-input form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Mới nhận gần đây</option>
                <option value="oldest" ${sort == 'oldest' ? 'selected' : ''}>Nhận cũ nhất</option>
                <option value="amount_desc" ${sort == 'amount_desc' ? 'selected' : ''}>Giá trị cao đến thấp</option>
                <option value="amount_asc" ${sort == 'amount_asc' ? 'selected' : ''}>Giá trị thấp đến cao</option>
            </select>

            <button type="submit" class="btn btn-dark">Lọc</button>
        </form>

        <section class="shipper-order-table-card card shadow-sm">
            <table class="shipper-order-table table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Mã vận đơn</th>
                    <th>Cửa hàng lấy hàng</th>
                    <th>Người nhận</th>
                    <th>Sản phẩm</th>
                    <th>Ngày nhận đơn</th>
                    <th>Thanh toán</th>
                    <th>Tiền thu</th>
                    <th>Trạng thái đơn</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${not empty orders}">
                        <c:forEach var="order" items="${orders}">
                            <tr class="shipper-order-clickable-row"
                                data-href="${pageContext.request.contextPath}/logistics/delivery/status?deliveryId=${order.deliveryId}"
                                tabindex="0"
                                role="link"
                                aria-label="Xem vận đơn ${order.trackingNumber}">
                                <td>
                                    <strong class="shipper-order-code">${order.trackingNumber}</strong>
                                    <small>#SUB-${order.subOrderId} / #MO-${order.masterOrderId}</small>
                                </td>

                                <td class="shipper-order-address">
                                    <strong>${order.shopName}</strong>
                                    <small>${order.sellerPhone}</small>
                                    <small>${order.pickupAddress}</small>
                                </td>

                                <td class="shipper-order-address">
                                    <strong>${order.receiverName}</strong>
                                    <small>${order.receiverPhone}</small>
                                    <small>${order.shippingAddress}</small>
                                </td>

                                <td class="shipper-order-products">
                                    <span>${empty order.productsSummary ? 'Chưa có sản phẩm' : order.productsSummary}</span>
                                    <small>${order.itemCount} dòng hàng - ${order.totalQuantity} sản phẩm</small>
                                </td>

                                <td>
                                    <fmt:formatDate value="${order.assignedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    <small>Khách đặt: <fmt:formatDate value="${order.orderedAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                                </td>

                                <td>
                                    <span class="shipper-order-payment">${order.paymentMethod}</span>
                                    <small>
                                        <c:choose>
                                            <c:when test="${order.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                            <c:otherwise>Cần thu tiền</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>

                                <td class="shipper-order-amount">
                                    <fmt:formatNumber value="${order.collectAmount}" type="number" maxFractionDigits="0"/>đ
                                </td>

                                <td>
                                    <span class="shipper-order-status-badge status-${order.orderStatus}">
                                        <c:choose>
                                            <c:when test="${order.orderStatus == 'PREPARING'}">Chờ bàn giao</c:when>
                                            <c:when test="${order.orderStatus == 'SHIPPING'}">Đang vận chuyển</c:when>
                                            <c:when test="${order.orderStatus == 'DELIVERED'}">Đã giao</c:when>
                                            <c:when test="${order.orderStatus == 'CANCELLED'}">Đã hủy</c:when>
                                            <c:otherwise>${order.orderStatus}</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <small>
                                        <c:choose>
                                            <c:when test="${order.deliveryStatus == 'ASSIGNED'}">Đã nhận đơn</c:when>
                                            <c:when test="${order.deliveryStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
                                            <c:when test="${order.deliveryStatus == 'IN_TRANSIT'}">Đang giao</c:when>
                                            <c:when test="${order.deliveryStatus == 'DELIVERED'}">Hoàn tất giao hàng</c:when>
                                            <c:when test="${order.deliveryStatus == 'FAILED'}">Giao thất bại</c:when>
                                            <c:otherwise>${order.deliveryStatus}</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>

                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="8" class="shipper-order-empty-state">
                                Bạn chưa nhận đơn vận chuyển nào.
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

    document.querySelectorAll('.shipper-order-clickable-row').forEach(function (row) {
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
