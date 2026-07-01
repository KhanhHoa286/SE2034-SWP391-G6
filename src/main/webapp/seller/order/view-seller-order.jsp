<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi ti&#7871;t &#273;&#417;n h&#224;ng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260630a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-seller-order.css?v=20260630a">
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
            <a href="${pageContext.request.contextPath}/seller/orders">Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng</a>
            <span>/</span>
            <strong>Chi ti&#7871;t &#273;&#417;n</strong>
        </nav>

        <c:if test="${not empty errorMessage}">
            <div class="seller-order-detail-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orderDetail}">
                <header class="seller-order-detail-heading">
                    <div>
                        <span class="seller-order-detail-eyebrow">#SUB-${orderDetail.subOrderId} / #MO-${orderDetail.masterOrderId}</span>
                        <h1>Chi ti&#7871;t &#273;&#417;n h&#224;ng</h1>
                        <p>Ng&#432;&#7901;i mua &#273;&#7863;t l&#250;c <fmt:formatDate value="${orderDetail.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/> cho shop ${orderDetail.shopName}.</p>
                    </div>
                    <span class="seller-order-detail-status status-${orderDetail.status}">
                        <c:choose>
                            <c:when test="${orderDetail.status == 'PENDING'}">Ch&#7901; x&#225;c nh&#7853;n</c:when>
                            <c:when test="${orderDetail.status == 'CONFIRMED'}">&#272;&#227; x&#225;c nh&#7853;n</c:when>
                            <c:when test="${orderDetail.status == 'PREPARING'}">&#272;ang chu&#7849;n b&#7883;</c:when>
                            <c:when test="${orderDetail.status == 'SHIPPING'}">&#272;ang giao</c:when>
                            <c:when test="${orderDetail.status == 'DELIVERED'}">Ho&#224;n th&#224;nh</c:when>
                            <c:when test="${orderDetail.status == 'CANCELLED'}">&#272;&#227; h&#7911;y</c:when>
                            <c:otherwise>${orderDetail.status}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="seller-order-detail-metrics">
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>T&#7893;ng ti&#7873;n</span>
                        <strong><fmt:formatNumber value="${orderDetail.totalAmount}" type="number" maxFractionDigits="0"/>&#273;</strong>
                        <small>Gi&#225; tr&#7883; sub-order c&#7911;a shop</small>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Ph&#237; n&#7873;n t&#7843;ng</span>
                        <strong><fmt:formatNumber value="${orderDetail.commissionFee}" type="number" maxFractionDigits="0"/>&#273;</strong>
                        <small>Commission theo database</small>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Doanh thu sau ph&#237;</span>
                        <strong><fmt:formatNumber value="${orderDetail.sellerReceivable}" type="number" maxFractionDigits="0"/>&#273;</strong>
                        <small>S&#7889; ti&#7873;n sau khi tr&#7915; ph&#237; n&#7873;n t&#7843;ng</small>
                    </article>
                    <article class="seller-order-detail-metric card shadow-sm">
                        <span>Thanh to&#225;n</span>
                        <strong>${orderDetail.paymentMethod}</strong>
                        <small>
                            <c:choose>
                                <c:when test="${orderDetail.paymentStatus == 'PAID'}">&#272;&#227; thanh to&#225;n</c:when>
                                <c:when test="${orderDetail.paymentStatus == 'REFUNDED'}">&#272;&#227; ho&#224;n ti&#7873;n</c:when>
                                <c:otherwise>Ch&#7901; thanh to&#225;n</c:otherwise>
                            </c:choose>
                        </small>
                    </article>
                </section>

                <div class="seller-order-detail-grid">
                    <section class="seller-order-detail-products card shadow-sm">
                        <div class="seller-order-detail-card-header">
                            <h2>S&#7843;n ph&#7849;m trong &#273;&#417;n</h2>
                            <span>${fn:length(orderDetail.items)} d&#242;ng h&#224;ng</span>
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
                                                    <span>M&#224;u: ${empty item.colorName ? 'Khong co' : item.colorName}</span>
                                                    <span>Size: ${empty item.sizeName ? 'Khong co' : item.sizeName}</span>
                                                    <span>SKU: ${empty item.variantName ? 'Khong co' : item.variantName}</span>
                                                </div>
                                            </div>
                                            <div class="seller-order-detail-item-price">
                                                <span><fmt:formatNumber value="${item.priceAtPurchase}" type="number" maxFractionDigits="0"/>&#273;</span>
                                                <small>x${item.quantity}</small>
                                                <strong><fmt:formatNumber value="${item.lineTotal}" type="number" maxFractionDigits="0"/>&#273;</strong>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="seller-order-detail-empty">Ch&#432;a c&#243; s&#7843;n ph&#7849;m trong &#273;&#417;n n&#224;y.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <aside class="seller-order-detail-side">
                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Kh&#225;ch h&#224;ng</h2>
                            <dl>
                                <dt>Ng&#432;&#7901;i mua</dt>
                                <dd>${orderDetail.customerName}</dd>
                                <dt>Email</dt>
                                <dd>${orderDetail.customerEmail}</dd>
                                <dt>S&#7889; &#273;i&#7879;n tho&#7841;i</dt>
                                <dd>${orderDetail.customerPhone}</dd>
                            </dl>
                        </section>

                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Giao h&#224;ng</h2>
                            <dl>
                                <dt>Ng&#432;&#7901;i nh&#7853;n</dt>
                                <dd>${orderDetail.receiverName}</dd>
                                <dt>S&#7889; &#273;i&#7879;n tho&#7841;i</dt>
                                <dd>${orderDetail.receiverPhone}</dd>
                                <dt>&#272;&#7883;a ch&#7881;</dt>
                                <dd>${orderDetail.shippingAddress}</dd>
                            </dl>
                        </section>

                        <section class="seller-order-detail-info-card card shadow-sm">
                            <h2>Thanh to&#225;n</h2>
                            <dl>
                                <dt>Ph&#432;&#417;ng th&#7913;c</dt>
                                <dd>${orderDetail.paymentMethod}</dd>
                                <dt>Tr&#7841;ng th&#225;i</dt>
                                <dd>
                                    <c:choose>
                                        <c:when test="${orderDetail.paymentStatus == 'PAID'}">&#272;&#227; thanh to&#225;n</c:when>
                                        <c:when test="${orderDetail.paymentStatus == 'REFUNDED'}">&#272;&#227; ho&#224;n ti&#7873;n</c:when>
                                        <c:otherwise>Ch&#7901; thanh to&#225;n</c:otherwise>
                                    </c:choose>
                                </dd>
                                <c:if test="${not empty orderDetail.transactionCode}">
                                    <dt>M&#227; giao d&#7883;ch</dt>
                                    <dd>${orderDetail.transactionCode}</dd>
                                </c:if>
                                <c:if test="${not empty orderDetail.bankName}">
                                    <dt>Ng&#226;n h&#224;ng</dt>
                                    <dd>${orderDetail.bankName}</dd>
                                </c:if>
                                <c:if test="${not empty orderDetail.paymentDate}">
                                    <dt>Ng&#224;y thanh to&#225;n</dt>
                                    <dd><fmt:formatDate value="${orderDetail.paymentDate}" pattern="dd/MM/yyyy HH:mm"/></dd>
                                </c:if>
                            </dl>
                        </section>

                        <section class="seller-order-detail-summary card shadow-sm">
                            <h2>T&#7893;ng k&#7871;t</h2>
                            <div class="seller-order-detail-summary-row">
                                <span>T&#7841;m t&#237;nh</span>
                                <strong><fmt:formatNumber value="${orderDetail.subTotal}" type="number" maxFractionDigits="0"/>&#273;</strong>
                            </div>
                            <div class="seller-order-detail-summary-row">
                                <span>Gi&#7843;m gi&#225;</span>
                                <strong>-<fmt:formatNumber value="${orderDetail.discountAmount}" type="number" maxFractionDigits="0"/>&#273;</strong>
                            </div>
                            <div class="seller-order-detail-summary-row">
                                <span>Commission</span>
                                <strong>-<fmt:formatNumber value="${orderDetail.commissionFee}" type="number" maxFractionDigits="0"/>&#273;</strong>
                            </div>
                            <div class="seller-order-detail-summary-total">
                                <span>Doanh thu sau ph&#237;</span>
                                <strong><fmt:formatNumber value="${orderDetail.sellerReceivable}" type="number" maxFractionDigits="0"/>&#273;</strong>
                            </div>
                        </section>
                    </aside>
                </div>

                <div class="seller-order-detail-actions">
                    <a class="seller-order-detail-secondary btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/orders">
                        Quay l&#7841;i danh s&#225;ch
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <section class="seller-order-detail-error-card card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Kh&#244;ng t&#7843;i &#273;&#432;&#7907;c &#273;&#417;n h&#224;ng</h1>
                    <p>Vui l&#242;ng quay l&#7841;i danh s&#225;ch &#273;&#417;n h&#224;ng v&#224; ch&#7885;n m&#7897;t &#273;&#417;n h&#7907;p l&#7879;.</p>
                    <a class="seller-order-detail-primary btn btn-dark" href="${pageContext.request.contextPath}/seller/orders">Quay l&#7841;i danh s&#225;ch</a>
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
