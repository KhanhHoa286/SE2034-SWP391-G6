<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập nhật trạng thái giao hàng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/edit-delivery-status.css?v=20260708a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="delivery-status-shell">
    <%
        request.setAttribute("activePage", "delivery-my-orders");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="delivery-status-main container-fluid">
        <nav class="delivery-status-breadcrumb">
            <a href="${pageContext.request.contextPath}/logistics/delivery/my-orders">Trung tâm giao hàng</a>
            <span>/</span>
            <strong>Cập nhật trạng thái</strong>
        </nav>

        <c:if test="${not empty successMessage}">
            <div class="delivery-status-alert delivery-status-alert-success alert d-flex align-items-center" role="alert">
                <i data-lucide="check-circle-2"></i>
                <span>${successMessage}</span>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="delivery-status-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty deliveryStatus}">
                <header class="delivery-status-heading">
                    <div>
                        <span class="delivery-status-eyebrow">
                            #SUB-${deliveryStatus.subOrderId} / #MO-${deliveryStatus.masterOrderId}
                        </span>
                        <h1>Cập nhật trạng thái giao hàng</h1>
                        <p>Chỉ xác nhận hoàn tất khi người mua đã nhận được hàng.</p>
                    </div>
                    <span class="delivery-status-badge status-${deliveryStatus.deliveryStatus}">
                        <c:choose>
                            <c:when test="${deliveryStatus.deliveryStatus == 'ASSIGNED'}">Đã nhận đơn</c:when>
                            <c:when test="${deliveryStatus.deliveryStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
                            <c:when test="${deliveryStatus.deliveryStatus == 'IN_TRANSIT'}">Đang giao</c:when>
                            <c:when test="${deliveryStatus.deliveryStatus == 'DELIVERED'}">Đã giao</c:when>
                            <c:when test="${deliveryStatus.deliveryStatus == 'FAILED'}">Giao thất bại</c:when>
                            <c:otherwise>${deliveryStatus.deliveryStatus}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="delivery-status-metrics">
                    <article class="delivery-status-metric card shadow-sm">
                        <span>Mã vận đơn</span>
                        <strong>${deliveryStatus.trackingNumber}</strong>
                        <small>Đơn thuộc danh sách vận chuyển của bạn</small>
                    </article>
                    <article class="delivery-status-metric card shadow-sm">
                        <span>Thanh toán</span>
                        <strong>${deliveryStatus.paymentMethod}</strong>
                        <small>
                            <c:choose>
                                <c:when test="${deliveryStatus.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                <c:otherwise>Cần thu tiền khi giao</c:otherwise>
                            </c:choose>
                        </small>
                    </article>
                    <article class="delivery-status-metric card shadow-sm">
                        <span>Tiền cần thu</span>
                        <strong><fmt:formatNumber value="${deliveryStatus.collectAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                        <small>Đơn đã thanh toán thì tiền thu là 0đ</small>
                    </article>
                    <article class="delivery-status-metric card shadow-sm">
                        <span>Tổng tiền đơn</span>
                        <strong><fmt:formatNumber value="${deliveryStatus.totalAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                        <small>Giá trị đơn cần giao</small>
                    </article>
                </section>

                <div class="delivery-status-grid">
                    <div class="delivery-status-left">
                        <section class="delivery-status-card card shadow-sm">
                            <div class="delivery-status-card-header">
                                <h2>Tiến trình giao hàng</h2>
                                <span>3 bước xử lý</span>
                            </div>
                            <div class="delivery-status-steps">
                                <c:forEach var="step" items="${statusSteps}">
                                    <article class="delivery-status-step ${step.completed ? 'completed' : ''}">
                                        <div class="delivery-status-step-icon">
                                            <c:choose>
                                                <c:when test="${step.completed}">
                                                    <i data-lucide="check"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="circle"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <h3>${step.title}</h3>
                                        <p>${step.description}</p>
                                    </article>
                                </c:forEach>
                            </div>
                        </section>

                        <section class="delivery-status-card card shadow-sm">
                            <div class="delivery-status-card-header">
                                <h2>Cập nhật trạng thái</h2>
                                <span>Người mua đã nhận hàng</span>
                            </div>

                            <c:choose>
                                <c:when test="${canMarkDelivered}">
                                    <form class="delivery-status-form"
                                          action="${pageContext.request.contextPath}/logistics/delivery/status"
                                          method="POST">
                                        <input type="hidden" name="deliveryId" value="${deliveryStatus.deliveryId}">
                                        <input type="hidden" name="newStatus" value="DELIVERED">

                                        <label class="delivery-status-option">
                                            <input type="radio" checked>
                                            <span>
                                                <strong>Đã giao cho người mua</strong>
                                                <small>Xác nhận người mua đã nhận hàng, hệ thống sẽ hoàn tất đơn giao.</small>
                                            </span>
                                        </label>

                                        <div class="delivery-status-actions">
                                            <a class="btn btn-outline-dark"
                                               href="${pageContext.request.contextPath}/logistics/delivery/my-orders">
                                                Hủy
                                            </a>
                                            <button type="submit" class="btn btn-dark">
                                                <i data-lucide="check-circle-2"></i>
                                                <span>Cập nhật</span>
                                            </button>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="delivery-status-locked">
                                        <i data-lucide="info"></i>
                                        <span>${lockedMessage}</span>
                                    </div>
                                    <div class="delivery-status-actions">
                                        <a class="btn btn-dark delivery-status-back-link"
                                           href="${pageContext.request.contextPath}/logistics/delivery/my-orders">
                                            Quay lại danh sách
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>

                    </div>

                    <aside class="delivery-status-side">
                        <section class="delivery-status-info card shadow-sm">
                            <h2>Thông tin đơn</h2>
                            <dl>
                                <dt>Ngày khách đặt</dt>
                                <dd><fmt:formatDate value="${deliveryStatus.orderedAt}" pattern="dd/MM/yyyy HH:mm"/></dd>
                                <dt>Trạng thái đơn</dt>
                                <dd>
                                    <c:choose>
                                        <c:when test="${deliveryStatus.orderStatus == 'PREPARING'}">Đang chuẩn bị</c:when>
                                        <c:when test="${deliveryStatus.orderStatus == 'SHIPPING'}">Đang giao</c:when>
                                        <c:when test="${deliveryStatus.orderStatus == 'DELIVERED'}">Đã giao hàng</c:when>
                                        <c:when test="${deliveryStatus.orderStatus == 'COMPLETED'}">Hoàn tất</c:when>
                                        <c:otherwise>${deliveryStatus.orderStatus}</c:otherwise>
                                    </c:choose>
                                </dd>
                            </dl>
                        </section>

                        <section class="delivery-status-info card shadow-sm">
                            <h2>Cửa hàng lấy hàng</h2>
                            <dl>
                                <dt>Cửa hàng</dt>
                                <dd>${deliveryStatus.shopName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${deliveryStatus.sellerPhone}</dd>
                                <dt>Địa chỉ lấy hàng</dt>
                                <dd>${deliveryStatus.pickupAddress}</dd>
                            </dl>
                        </section>

                        <section class="delivery-status-info card shadow-sm">
                            <h2>Người nhận</h2>
                            <dl>
                                <dt>Họ tên</dt>
                                <dd>${deliveryStatus.receiverName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${deliveryStatus.receiverPhone}</dd>
                                <dt>Địa chỉ giao hàng</dt>
                                <dd>${deliveryStatus.shippingAddress}</dd>
                            </dl>
                        </section>

                        <section class="delivery-status-info card shadow-sm">
                            <h2>Sản phẩm</h2>
                            <p>${empty deliveryStatus.productsSummary ? 'Chưa có sản phẩm' : deliveryStatus.productsSummary}</p>
                            <small>Tổng số lượng: ${deliveryStatus.totalQuantity}</small>
                        </section>
                    </aside>
                </div>
            </c:when>

            <c:otherwise>
                <section class="delivery-status-empty-page card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Không có dữ liệu đơn vận chuyển</h1>
                    <p>Vui lòng quay lại danh sách đơn vận chuyển của tôi và chọn một đơn để cập nhật.</p>
                    <a class="btn btn-dark delivery-status-back-link" href="${pageContext.request.contextPath}/logistics/delivery/my-orders">
                        Quay lại danh sách
                    </a>
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
