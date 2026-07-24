<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn giao - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-delivery.css?v=20260707a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="delivery-detail-shell">
    <%
        request.setAttribute("activePage", request.getParameter("deliveryId") == null ? "delivery-list" : "delivery-my-orders");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="delivery-detail-main container-fluid">
        <nav class="delivery-detail-breadcrumb d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/logistics/delivery/list">Trung tâm giao hàng</a>
            <span>/</span>
            <strong>Chi tiết đơn</strong>
        </nav>

        <c:if test="${not empty errorMessage}">
            <div class="delivery-detail-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty deliveryDetail}">
                <header class="delivery-detail-heading">
                    <div>
                        <span class="delivery-detail-eyebrow">
                            #SUB-${deliveryDetail.subOrderId} / #MO-${deliveryDetail.masterOrderId}
                        </span>
                        <h1>Chi tiết đơn giao</h1>
                        <p>Đơn được chuẩn bị lúc <fmt:formatDate value="${deliveryDetail.preparedAt}" pattern="dd/MM/yyyy HH:mm"/> tại ${deliveryDetail.shopName}.</p>
                    </div>
                    <span class="delivery-detail-status status-${deliveryDetail.deliveryStatus}">
                        <c:choose>
                            <c:when test="${deliveryDetail.deliveryStatus == 'WAITING'}">Chờ nhận đơn</c:when>
                            <c:when test="${deliveryDetail.deliveryStatus == 'ASSIGNED'}">Đã nhận đơn</c:when>
                            <c:when test="${deliveryDetail.deliveryStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
                            <c:when test="${deliveryDetail.deliveryStatus == 'IN_TRANSIT'}">Đang giao</c:when>
                            <c:when test="${deliveryDetail.deliveryStatus == 'DELIVERED'}">Đã giao</c:when>
                            <c:when test="${deliveryDetail.deliveryStatus == 'FAILED'}">Giao thất bại</c:when>
                            <c:otherwise>${deliveryDetail.deliveryStatus}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="delivery-detail-metrics">
                    <article class="delivery-detail-metric card shadow-sm">
                        <span>Mã vận đơn</span>
                        <strong>${deliveryDetail.trackingNumber}</strong>
                    </article>

                    <article class="delivery-detail-metric card shadow-sm">
                        <span>Phương thức thanh toán</span>
                        <strong>${deliveryDetail.paymentMethod}</strong>
                        <small>
                            <c:choose>
                                <c:when test="${deliveryDetail.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                <c:otherwise>Cần thu tiền khi giao</c:otherwise>
                            </c:choose>
                        </small>
                    </article>

                    <article class="delivery-detail-metric card shadow-sm">
                        <span>Tiền cần thu</span>
                        <strong><fmt:formatNumber value="${deliveryDetail.collectAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                    </article>

                    <article class="delivery-detail-metric card shadow-sm">
                        <span>Tổng tiền đơn</span>
                        <strong><fmt:formatNumber value="${deliveryDetail.totalAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                    </article>
                </section>

                <div class="delivery-detail-grid">
                    <section class="delivery-detail-products card shadow-sm">
                        <div class="delivery-detail-card-header">
                            <h2>Sản phẩm cần giao</h2>
                            <span>${fn:length(deliveryDetail.items)} dòng hàng</span>
                        </div>

                        <c:choose>
                            <c:when test="${not empty deliveryDetail.items}">
                                <div class="delivery-detail-item-list">
                                    <c:forEach var="item" items="${deliveryDetail.items}">
                                        <article class="delivery-detail-item">
                                            <div class="delivery-detail-image">
                                                <c:choose>
                                                    <c:when test="${not empty item.thumbnailUrl}">
                                                        <img src="${item.thumbnailUrl}" alt="${item.productName}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i data-lucide="package"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="delivery-detail-item-info">
                                                <h3>${item.productName}</h3>
                                                <div class="delivery-detail-meta">
                                                    <span>Màu: ${empty item.colorName ? 'Không có' : item.colorName}</span>
                                                    <span>Size: ${empty item.sizeName ? 'Không có' : item.sizeName}</span>
                                                    <span>SKU: ${empty item.variantName ? 'Không có' : item.variantName}</span>
                                                </div>
                                            </div>
                                            <div class="delivery-detail-item-price">
                                                <span><fmt:formatNumber value="${item.priceAtPurchase}" type="number" maxFractionDigits="0"/>đ</span>
                                                <small>x${item.quantity}</small>
                                                <strong><fmt:formatNumber value="${item.lineTotal}" type="number" maxFractionDigits="0"/>đ</strong>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="delivery-detail-empty">Chưa có sản phẩm trong đơn này.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <aside class="delivery-detail-side">
                        <section class="delivery-detail-info-card card shadow-sm">
                            <h2>Cửa hàng lấy hàng</h2>
                            <dl>
                                <dt>Cửa hàng</dt>
                                <dd>${deliveryDetail.shopName}</dd>
                                <dt>Người bán</dt>
                                <dd>${deliveryDetail.sellerName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${deliveryDetail.sellerPhone}</dd>
                                <dt>Email</dt>
                                <dd>${deliveryDetail.sellerEmail}</dd>
                                <dt>Địa chỉ lấy hàng</dt>
                                <dd>${deliveryDetail.pickupAddress}</dd>
                            </dl>
                        </section>

                        <section class="delivery-detail-info-card card shadow-sm">
                            <h2>Người nhận</h2>
                            <dl>
                                <dt>Họ tên</dt>
                                <dd>${deliveryDetail.receiverName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${deliveryDetail.receiverPhone}</dd>
                                <dt>Địa chỉ giao hàng</dt>
                                <dd>${deliveryDetail.shippingAddress}</dd>
                            </dl>
                        </section>

                        <section class="delivery-detail-summary card shadow-sm">
                            <h2>Thanh toán</h2>
                            <div class="delivery-detail-summary-row">
                                <span>Tạm tính</span>
                                <strong><fmt:formatNumber value="${deliveryDetail.subTotal}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                            <div class="delivery-detail-summary-row">
                                <span>Tổng đơn</span>
                                <strong><fmt:formatNumber value="${deliveryDetail.totalAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                            <div class="delivery-detail-summary-total">
                                <span>Tiền cần thu</span>
                                <strong><fmt:formatNumber value="${deliveryDetail.collectAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                        </section>
                    </aside>
                </div>

                <div class="delivery-detail-actions card shadow-sm">
                    <a class="delivery-detail-secondary btn btn-outline-dark"
                       href="${pageContext.request.contextPath}/logistics/delivery/list">
                        <i data-lucide="arrow-left"></i>
                        <span>Quay lại</span>
                    </a>

                    <c:choose>
                        <c:when test="${deliveryDetail.deliveryId == 0}">
                            <button type="button"
                                    class="delivery-detail-primary btn btn-dark"
                                    data-bs-toggle="modal"
                                    data-bs-target="#receiveDeliveryModal">
                                <i data-lucide="check-circle-2"></i>
                                <span>Nhận đơn hàng</span>
                            </button>
                        </c:when>
                        <c:otherwise>
                            <a class="delivery-detail-primary btn btn-dark"
                               href="${pageContext.request.contextPath}/logistics/delivery/my-orders">
                                <i data-lucide="list-checks"></i>
                                <span>Đơn của tôi</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${deliveryDetail.deliveryId == 0}">
                    <div class="modal fade" id="receiveDeliveryModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content delivery-detail-modal">
                                <div class="modal-header">
                                    <h2 class="modal-title">Nhận đơn hàng</h2>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                                </div>
                                <div class="modal-body">
                                    <p>Sau khi nhận, đơn #SUB-${deliveryDetail.subOrderId} sẽ chuyển sang danh sách đơn vận chuyển của bạn và không còn xuất hiện ở danh sách chung.</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Đóng</button>
                                    <form action="${pageContext.request.contextPath}/logistics/delivery/view" method="POST">
                                        <input type="hidden" name="subOrderId" value="${deliveryDetail.subOrderId}">
                                        <button type="submit" class="btn btn-dark">Nhận đơn hàng</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
            </c:when>

            <c:otherwise>
                <section class="delivery-detail-empty-page card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Không có dữ liệu đơn giao</h1>
                    <p>Vui lòng quay lại danh sách và chọn một đơn đang chờ lấy hàng.</p>
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/logistics/delivery/list">Quay lại danh sách</a>
                </section>
            </c:otherwise>
        </c:choose>
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
