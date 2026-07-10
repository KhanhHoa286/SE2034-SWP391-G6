<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-seller-order.css?v=20260707a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-order-detail-shell">
    <%
        request.setAttribute("activePage", "orders");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="seller-order-detail-main container-fluid">
        <nav class="seller-order-detail-breadcrumb d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/seller/orders">Quản lý đơn hàng</a>
            <span>/</span>
            <strong>Chi tiết đơn</strong>
        </nav>

        <c:if test="${not empty errorMessage}">
            <div class="seller-order-detail-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="seller-order-detail-alert seller-order-detail-alert-success alert d-flex align-items-center" role="alert">
                <i data-lucide="check-circle-2"></i>
                <span>${successMessage}</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orderDetail}">
                <header class="seller-order-detail-heading">
                    <div>
                        <span class="seller-order-detail-eyebrow">#SUB-${orderDetail.subOrderId} / #MO-${orderDetail.masterOrderId}</span>
                        <h1>Chi tiết đơn hàng</h1>
                        <p>Người mua đặt lúc <fmt:formatDate value="${orderDetail.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/> cho shop ${orderDetail.shopName}.</p>
                    </div>
                    <span class="seller-order-detail-status status-${orderDetail.status}">
                        <c:choose>
                            <c:when test="${orderDetail.status == 'PENDING'}">Chờ xác nhận</c:when>
                            <c:when test="${orderDetail.status == 'CONFIRMED'}">Đã xác nhận</c:when>
                            <c:when test="${orderDetail.status == 'PREPARING'}">Đang chuẩn bị</c:when>
                            <c:when test="${orderDetail.status == 'SHIPPING'}">Đang giao</c:when>
                            <c:when test="${orderDetail.status == 'DELIVERED'}">Hoàn thành</c:when>
                            <c:when test="${orderDetail.status == 'CANCELLED'}">Đã hủy</c:when>
                            <c:otherwise>${orderDetail.status}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="seller-order-detail-metrics">
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Tổng tiền</span>
                        <strong><fmt:formatNumber value="${orderDetail.totalAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Phí nền tảng</span>
                        <strong><fmt:formatNumber value="${orderDetail.commissionFee}" type="number" maxFractionDigits="0"/>đ</strong>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Tiền nhận được</span>
                        <strong><fmt:formatNumber value="${orderDetail.sellerReceivable}" type="number" maxFractionDigits="0"/>đ</strong>
                        <small>Số tiền sau khi trừ phí nền tảng</small>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Phương thức thanh toán</span>
                        <strong>${orderDetail.paymentMethod}</strong>
                        <small>
                            <c:choose>
                                <c:when test="${orderDetail.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                <c:when test="${orderDetail.paymentStatus == 'REFUNDED'}">Đã hoàn tiền</c:when>
                                <c:otherwise>Chờ thanh toán</c:otherwise>
                            </c:choose>
                        </small>
                    </article>
                </section>

                <div class="seller-order-detail-grid">
                    <section class="seller-order-detail-products card shadow-sm">
                        <div class="seller-order-detail-card-header">
                            <h2>Sản phẩm trong đơn</h2>
                            <span>${fn:length(orderDetail.items)} dòng hàng</span>
                        </div>

                        <c:choose>
                            <c:when test="${not empty orderDetail.items}">
                                <div class="seller-order-detail-item-list">
                                    <c:forEach var="item" items="${orderDetail.items}">
                                        <article class="seller-order-detail-item">
                                            <div class="seller-order-detail-image">
                                                <c:choose>
                                                    <c:when test="${not empty item.thumbnailUrl}">
                                                        <img src="${item.thumbnailUrl}" alt="${item.productName}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i data-lucide="image"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="seller-order-detail-item-info">
                                                <h3>${item.productName}</h3>
                                                <div class="seller-order-detail-meta">
                                                    <span>Màu: ${empty item.colorName ? 'Khong co' : item.colorName}</span>
                                                    <span>Size: ${empty item.sizeName ? 'Khong co' : item.sizeName}</span>
                                                    <span>SKU: ${empty item.variantName ? 'Khong co' : item.variantName}</span>
                                                </div>
                                            </div>
                                            <div class="seller-order-detail-item-price">
                                                <span><fmt:formatNumber value="${item.priceAtPurchase}" type="number" maxFractionDigits="0"/>đ</span>
                                                <small>x${item.quantity}</small>
                                                <strong><fmt:formatNumber value="${item.lineTotal}" type="number" maxFractionDigits="0"/>đ</strong>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="seller-order-detail-empty">Chưa có sản phẩm trong đơn này.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <aside class="seller-order-detail-side">
                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Khách hàng</h2>
                            <dl>
                                <dt>Người mua</dt>
                                <dd>${orderDetail.customerName}</dd>
                                <dt>Email</dt>
                                <dd>${orderDetail.customerEmail}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${orderDetail.customerPhone}</dd>
                            </dl>
                        </section>

                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Giao hàng</h2>
                            <dl>
                                <dt>Người nhận</dt>
                                <dd>${orderDetail.receiverName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${orderDetail.receiverPhone}</dd>
                                <dt>Địa chỉ</dt>
                                <dd>${orderDetail.shippingAddress}</dd>
                            </dl>
                        </section>

                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Thanh toán</h2>
                            <dl>
                                <dt>Phương thức</dt>
                                <dd>${orderDetail.paymentMethod}</dd>
                                <dt>Trạng thái</dt>
                                <dd>
                                    <c:choose>
                                        <c:when test="${orderDetail.paymentStatus == 'PAID'}">Đã thanh toán</c:when>
                                        <c:when test="${orderDetail.paymentStatus == 'REFUNDED'}">Đã hoàn tiền</c:when>
                                        <c:otherwise>Chờ thanh toán</c:otherwise>
                                    </c:choose>
                                </dd>
                                <c:if test="${not empty orderDetail.transactionCode}">
                                    <dt>Mã giao dịch</dt>
                                    <dd>${orderDetail.transactionCode}</dd>
                                </c:if>
                                <c:if test="${not empty orderDetail.bankName}">
                                    <dt>Ngân hàng</dt>
                                    <dd>${orderDetail.bankName}</dd>
                                </c:if>
                                <c:if test="${not empty orderDetail.paymentDate}">
                                    <dt>Ngày thanh toán</dt>
                                    <dd><fmt:formatDate value="${orderDetail.paymentDate}" pattern="dd/MM/yyyy HH:mm"/></dd>
                                </c:if>
                            </dl>
                        </section>

                        <section class="seller-order-detail-summary card shadow-sm">
                            <h2>Tổng kết</h2>
                            <div class="seller-order-detail-summary-row">
                                <span>Tạm tính</span>
                                <strong><fmt:formatNumber value="${orderDetail.subTotal}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                            <div class="seller-order-detail-summary-row">
                                <span>Giảm giá</span>
                                <strong>-<fmt:formatNumber value="${orderDetail.discountAmount}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                            <div class="seller-order-detail-summary-row">
                                <span>Commission</span>
                                <strong>-<fmt:formatNumber value="${orderDetail.commissionFee}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                            <div class="seller-order-detail-summary-total">
                                <span>Tiền nhận được sau phí</span>
                                <strong><fmt:formatNumber value="${orderDetail.sellerReceivable}" type="number" maxFractionDigits="0"/>đ</strong>
                            </div>
                        </section>
                    </aside>
                </div>

                <div class="seller-order-detail-actions">
                    <a class="seller-order-detail-secondary btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/orders">
                        Quay lại danh sách
                    </a>
                    <c:if test="${orderDetail.status == 'PENDING' || orderDetail.status == 'CONFIRMED' || orderDetail.status == 'PREPARING'}">
                        <a class="seller-order-detail-primary btn btn-dark"
                           href="${pageContext.request.contextPath}/seller/order/status?subOrderId=${orderDetail.subOrderId}">
                            <i data-lucide="refresh-cw"></i>
                            <span>Chuyển trạng thái</span>
                        </a>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <section class="seller-order-detail-error-card card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Không tải được đơn hàng</h1>
                    <p>Vui lòng quay lại danh sách đơn hàng và chọn một đơn hợp lệ.</p>
                    <a class="seller-order-detail-primary btn btn-dark" href="${pageContext.request.contextPath}/seller/orders">Quay lại danh sách</a>
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
