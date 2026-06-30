<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chuy&#7875;n tr&#7841;ng th&#225;i &#273;&#417;n h&#224;ng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260630a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/edit-seller-status.css?v=20260630a">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-status-shell">
    <%
        request.setAttribute("activePage", "orders");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="seller-status-main container-fluid">
        <nav class="seller-status-breadcrumb d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/seller/orders">Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng</a>
            <span>/</span>
            <c:choose>
                <c:when test="${not empty orderStatus}">
                    <a href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">#SUB-${orderStatus.subOrderId}</a>
                </c:when>
                <c:otherwise>
                    <span>Chi ti&#7871;t &#273;&#417;n</span>
                </c:otherwise>
            </c:choose>
            <span>/</span>
            <strong>Chuy&#7875;n tr&#7841;ng th&#225;i</strong>
        </nav>

        <c:if test="${not empty errorMessage}">
            <div class="seller-status-alert alert alert-danger d-flex align-items-center" role="alert">
                <i data-lucide="alert-triangle"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="seller-status-alert seller-status-alert-success alert d-flex align-items-center" role="alert">
                <i data-lucide="check-circle-2"></i>
                <span>${successMessage}</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orderStatus}">
                <header class="seller-status-heading">
                    <div>
                        <span class="seller-status-eyebrow">#SUB-${orderStatus.subOrderId} / #MO-${orderStatus.masterOrderId}</span>
                        <h1>Chuy&#7875;n tr&#7841;ng th&#225;i &#273;&#417;n h&#224;ng</h1>
                        <p>Seller ch&#7881; x&#7917; l&#253; c&#225;c b&#432;&#7899;c t&#7915; ch&#7901; x&#225;c nh&#7853;n &#273;&#7871;n b&#224;n giao cho v&#7853;n chuy&#7875;n.</p>
                    </div>
                    <span class="seller-status-badge status-${orderStatus.status}">
                        <c:choose>
                            <c:when test="${orderStatus.status == 'PENDING'}">Ch&#7901; x&#225;c nh&#7853;n</c:when>
                            <c:when test="${orderStatus.status == 'CONFIRMED'}">&#272;&#227; x&#225;c nh&#7853;n</c:when>
                            <c:when test="${orderStatus.status == 'PREPARING'}">&#272;ang chu&#7849;n b&#7883;</c:when>
                            <c:when test="${orderStatus.status == 'SHIPPING'}">&#272;&#227; giao v&#7853;n chuy&#7875;n</c:when>
                            <c:when test="${orderStatus.status == 'DELIVERED'}">&#272;&#227; giao h&#224;ng</c:when>
                            <c:when test="${orderStatus.status == 'CANCELLED'}">&#272;&#227; h&#7911;y</c:when>
                            <c:otherwise>${orderStatus.status}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="seller-status-grid">
                    <div class="seller-status-left">
                        <section class="seller-status-card card shadow-sm">
                            <div class="seller-status-card-header">
                                <h2>Ti&#7871;n tr&#236;nh seller</h2>
                                <span>4 b&#432;&#7899;c x&#7917; l&#253;</span>
                            </div>

                            <div class="seller-status-steps">
                                <c:forEach var="step" items="${statusSteps}">
                                    <div class="seller-status-step ${step.completed ? 'completed' : ''}">
                                        <span class="seller-status-step-icon">
                                            <c:choose>
                                                <c:when test="${step.completed}">
                                                    <i data-lucide="check"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="circle"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <div>
                                            <strong>
                                                <c:choose>
                                                    <c:when test="${step.value == 'PENDING'}">Ch&#7901; x&#225;c nh&#7853;n</c:when>
                                                    <c:when test="${step.value == 'CONFIRMED'}">&#272;&#227; x&#225;c nh&#7853;n</c:when>
                                                    <c:when test="${step.value == 'PREPARING'}">&#272;ang chu&#7849;n b&#7883;</c:when>
                                                    <c:when test="${step.value == 'SHIPPING'}">&#272;&#227; giao v&#7853;n chuy&#7875;n</c:when>
                                                </c:choose>
                                            </strong>
                                            <small>
                                                <c:choose>
                                                    <c:when test="${step.value == 'PENDING'}">Kh&#225;ch &#273;&#227; x&#225;c nh&#7853;n mua, ch&#7901; seller ki&#7875;m tra.</c:when>
                                                    <c:when test="${step.value == 'CONFIRMED'}">Seller ch&#7845;p nh&#7853;n x&#7917; l&#253; &#273;&#417;n.</c:when>
                                                    <c:when test="${step.value == 'PREPARING'}">Shop &#273;ang chu&#7849;n b&#7883; v&#224; &#273;&#243;ng g&#243;i h&#224;ng.</c:when>
                                                    <c:when test="${step.value == 'SHIPPING'}">&#272;&#417;n &#273;&#227; b&#224;n giao cho b&#234;n v&#7853;n chuy&#7875;n.</c:when>
                                                </c:choose>
                                            </small>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>

                        <section class="seller-status-card card shadow-sm">
                            <div class="seller-status-card-header">
                                <h2>C&#7853;p nh&#7853;t tr&#7841;ng th&#225;i</h2>
                                <span>Ch&#7885;n b&#432;&#7899;c k&#7871; ti&#7871;p</span>
                            </div>

                            <c:choose>
                                <c:when test="${not empty nextStatuses}">
                                    <form class="seller-status-form" action="${pageContext.request.contextPath}/seller/order/status" method="POST">
                                        <input type="hidden" name="subOrderId" value="${orderStatus.subOrderId}">

                                        <div class="seller-status-option-list">
                                            <c:forEach var="option" items="${nextStatuses}" varStatus="loop">
                                                <label class="seller-status-option">
                                                    <input type="radio"
                                                           name="newStatus"
                                                           value="${option.value}"
                                                           ${loop.first || selectedStatus == option.value ? 'checked' : ''}>
                                                    <span>
                                                        <strong>
                                                            <c:choose>
                                                                <c:when test="${option.value == 'CONFIRMED'}">X&#225;c nh&#7853;n &#273;&#417;n h&#224;ng</c:when>
                                                                <c:when test="${option.value == 'PREPARING'}">&#272;ang chu&#7849;n b&#7883; h&#224;ng</c:when>
                                                                <c:when test="${option.value == 'SHIPPING'}">&#272;&#227; giao cho b&#234;n v&#7853;n chuy&#7875;n</c:when>
                                                                <c:otherwise>${option.label}</c:otherwise>
                                                            </c:choose>
                                                        </strong>
                                                        <small>
                                                            <c:choose>
                                                                <c:when test="${option.value == 'CONFIRMED'}">Seller &#273;&#227; ki&#7875;m tra v&#224; ch&#7845;p nh&#7853;n x&#7917; l&#253; &#273;&#417;n n&#224;y.</c:when>
                                                                <c:when test="${option.value == 'PREPARING'}">Shop b&#7855;t &#273;&#7847;u &#273;&#243;ng g&#243;i v&#224; chu&#7849;n b&#7883; b&#224;n giao &#273;&#417;n.</c:when>
                                                                <c:when test="${option.value == 'SHIPPING'}">&#272;&#417;n &#273;&#227; &#273;&#432;&#7907;c b&#224;n giao cho b&#7897; ph&#7853;n v&#7853;n chuy&#7875;n.</c:when>
                                                                <c:otherwise>${option.description}</c:otherwise>
                                                            </c:choose>
                                                        </small>
                                                    </span>
                                                </label>
                                            </c:forEach>
                                        </div>

                                        <div class="seller-status-note">
                                            <i data-lucide="info"></i>
                                            <span>H&#7879; th&#7889;ng ch&#7881; cho ph&#233;p chuy&#7875;n sang b&#432;&#7899;c k&#7871; ti&#7871;p, kh&#244;ng nh&#7843;y tr&#7841;ng th&#225;i.</span>
                                        </div>

                                        <div class="seller-status-actions">
                                            <a class="seller-status-secondary btn btn-outline-dark"
                                               href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">
                                                H&#7911;y
                                            </a>
                                            <button type="submit" class="seller-status-primary btn btn-dark">
                                                <i data-lucide="refresh-cw"></i>
                                                <span>C&#7853;p nh&#7853;t</span>
                                            </button>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="seller-status-locked">
                                        <i data-lucide="lock"></i>
                                        <h3>Seller kh&#244;ng th&#7875; c&#7853;p nh&#7853;t ti&#7871;p</h3>
                                        <p>${lockedMessage}</p>
                                        <a class="seller-status-primary btn btn-dark"
                                           href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">
                                            Quay l&#7841;i chi ti&#7871;t
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>
                    </div>

                    <aside class="seller-status-side">
                        <section class="seller-status-info-card card shadow-sm">
                            <h2>Th&#244;ng tin &#273;&#417;n</h2>
                            <dl>
                                <dt>Kh&#225;ch h&#224;ng</dt>
                                <dd>${orderStatus.customerName}</dd>
                                <dt>Email</dt>
                                <dd>${orderStatus.customerEmail}</dd>
                                <dt>Ng&#224;y &#273;&#7863;t</dt>
                                <dd><fmt:formatDate value="${orderStatus.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/></dd>
                                <dt>T&#7893;ng ti&#7873;n</dt>
                                <dd><fmt:formatNumber value="${orderStatus.totalAmount}" type="number" maxFractionDigits="0"/>&#273;</dd>
                            </dl>
                        </section>

                        <section class="seller-status-info-card card shadow-sm">
                            <h2>S&#7843;n ph&#7849;m</h2>
                            <p>${empty orderStatus.productsSummary ? 'Chua co san pham' : orderStatus.productsSummary}</p>
                            <small>${orderStatus.itemCount} d&#242;ng h&#224;ng &bull; ${orderStatus.totalQuantity} s&#7843;n ph&#7849;m</small>
                        </section>

                        <section class="seller-status-info-card card shadow-sm">
                            <h2>Giao h&#224;ng</h2>
                            <dl>
                                <dt>Ng&#432;&#7901;i nh&#7853;n</dt>
                                <dd>${orderStatus.receiverName}</dd>
                                <dt>S&#7889; &#273;i&#7879;n tho&#7841;i</dt>
                                <dd>${orderStatus.receiverPhone}</dd>
                                <dt>&#272;&#7883;a ch&#7881;</dt>
                                <dd>${orderStatus.shippingAddress}</dd>
                            </dl>
                        </section>
                    </aside>
                </section>
            </c:when>
            <c:otherwise>
                <section class="seller-status-error-card card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Kh&#244;ng t&#7843;i &#273;&#432;&#7907;c &#273;&#417;n h&#224;ng</h1>
                    <p>Vui l&#242;ng quay l&#7841;i danh s&#225;ch &#273;&#417;n h&#224;ng v&#224; ch&#7885;n m&#7897;t &#273;&#417;n h&#7907;p l&#7879;.</p>
                    <a class="seller-status-primary btn btn-dark" href="${pageContext.request.contextPath}/seller/orders">Quay l&#7841;i danh s&#225;ch</a>
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
