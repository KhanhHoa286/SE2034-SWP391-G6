<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chuyển trạng thái đơn hàng - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/edit-seller-status.css?v=20260707a">
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
            <a href="${pageContext.request.contextPath}/seller/orders">Quản lý đơn hàng</a>
            <span>/</span>
            <c:choose>
                <c:when test="${not empty orderStatus}">
                    <a href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">#SUB-${orderStatus.subOrderId}</a>
                </c:when>
                <c:otherwise>
                    <span>Chi tiết đơn</span>
                </c:otherwise>
            </c:choose>
            <span>/</span>
            <strong>Chuyển trạng thái</strong>
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
                        <h1>Chuyển trạng thái đơn hàng</h1>
                        <p>Người bán xác nhận đơn, in phiếu vận đơn khi chuẩn bị hàng và bàn giao khi bên vận chuyển đến lấy.</p>
                    </div>
                    <span class="seller-status-badge status-${orderStatus.status}">
                        <c:choose>
                            <c:when test="${orderStatus.status == 'PENDING'}">Chờ xác nhận</c:when>
                            <c:when test="${orderStatus.status == 'CONFIRMED'}">Đang chuẩn bị</c:when>
                            <c:when test="${orderStatus.status == 'PREPARING'}">Đang chuẩn bị</c:when>
                            <c:when test="${orderStatus.status == 'SHIPPING'}">Đã giao vận chuyển</c:when>
                            <c:when test="${orderStatus.status == 'DELIVERED'}">Đã giao hàng</c:when>
                            <c:when test="${orderStatus.status == 'CANCELLED'}">Đã hủy</c:when>
                            <c:otherwise>${orderStatus.status}</c:otherwise>
                        </c:choose>
                    </span>
                </header>

                <section class="seller-status-grid">
                    <div class="seller-status-left">
                        <section class="seller-status-card card shadow-sm">
                            <div class="seller-status-card-header">
                                <h2>Tiến trình đơn hàng</h2>
                                <span>3 bước xử lý</span>
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
                                                    <c:when test="${step.value == 'PENDING'}">Chờ xác nhận</c:when>
                                                    <c:when test="${step.value == 'PREPARING'}">Đang chuẩn bị hàng</c:when>
                                                    <c:when test="${step.value == 'SHIPPING'}">Đã giao vận chuyển</c:when>
                                                </c:choose>
                                            </strong>
                                            <small>
                                                <c:choose>
                                                    <c:when test="${step.value == 'PENDING'}">Khách đã xác nhận mua, chờ người bán kiểm tra.</c:when>
                                                    <c:when test="${step.value == 'PREPARING'}">Cửa hàng đóng gói, in phiếu vận đơn và chờ lấy hàng.</c:when>
                                                    <c:when test="${step.value == 'SHIPPING'}">Đơn đã bàn giao cho bên vận chuyển.</c:when>
                                                </c:choose>
                                            </small>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>

                        <c:if test="${orderStatus.status == 'CONFIRMED' || orderStatus.status == 'PREPARING' || orderStatus.status == 'SHIPPING'}">
                            <section class="seller-status-card seller-status-label-card card shadow-sm">
                                <div class="seller-status-card-header">
                                    <h2>Phiếu vận đơn</h2>
                                    <span>In và dán lên đơn hàng</span>
                                </div>

                                <div id="shippingLabel" class="seller-shipping-label">
                                    <div class="label-header">
                                        <div>
                                            <strong>MODA EXPRESS</strong>
                                            <span>#SUB-${orderStatus.subOrderId} / #MO-${orderStatus.masterOrderId}</span>
                                        </div>
                                        <div class="label-code">
                                            <span>Số vận đơn</span>
                                            <strong>${orderStatus.trackingNumber}</strong>
                                        </div>
                                    </div>

                                    <div class="label-route">
                                        <div class="label-address">
                                            <h3>Từ</h3>
                                            <strong>${orderStatus.shopName}</strong>
                                            <p>${orderStatus.pickupAddress}</p>
                                            <p>${orderStatus.sellerPhone}</p>
                                        </div>
                                        <div class="label-address">
                                            <h3>Đến</h3>
                                            <strong>${orderStatus.receiverName}</strong>
                                            <p>${orderStatus.shippingAddress}</p>
                                            <p>${orderStatus.receiverPhone}</p>
                                        </div>
                                    </div>

                                    <div class="label-products">
                                        <h3>Nội dung hàng (Tổng SL sản phẩm: ${orderStatus.totalQuantity})</h3>
                                        <p>1. ${empty orderStatus.productsSummary ? 'Chua co san pham' : orderStatus.productsSummary}</p>
                                        <em>Người gửi phải cam kết hàng hóa có đầy đủ các hóa đơn, chứng từ, giấy phép cần thiết theo quy định của pháp luật và đính kèm theo bên trong đơn hàng này.</em>
                                    </div>

                                    <div class="label-footer">
                                        <div class="label-payment">
                                            <span>Tiền thu Người nhận:</span>
                                            <strong><fmt:formatNumber value="${orderStatus.collectAmount}" type="number" maxFractionDigits="0"/> VND</strong>
                                            <div class="label-instruction">
                                                <h3>Chỉ dẫn giao hàng:</h3>
                                                <p>- Không đồng kiểm;</p>
                                                <p>- Chuyển hoàn sau 3 lần phát;</p>
                                                <p>- Lưu kho tối đa 5 ngày.</p>
                                            </div>
                                        </div>
                                        <div class="label-signature">
                                            <span>Khối lượng tối đa: 300g</span>
                                            <div>
                                                <strong>Chữ ký người nhận</strong>
                                                <p>Xác nhận hàng nguyên vẹn, không móp/méo, bể/vỡ</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="seller-status-actions">
                                    <button type="button" class="seller-status-secondary btn btn-outline-dark" onclick="printShippingLabel()">
                                        <i data-lucide="printer"></i>
                                        <span>In phiếu</span>
                                    </button>
                                </div>
                            </section>
                        </c:if>

                        <section class="seller-status-card card shadow-sm">
                            <div class="seller-status-card-header">
                                <h2>Cập nhật trạng thái</h2>
                                <span>Chọn bước kế tiếp</span>
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
                                                                <c:when test="${option.value == 'PREPARING'}">Xác nhận và chuẩn bị hàng</c:when>
                                                                <c:when test="${option.value == 'SHIPPING'}">Đã giao cho bên vận chuyển</c:when>
                                                                <c:otherwise>${option.label}</c:otherwise>
                                                            </c:choose>
                                                        </strong>
                                                        <small>
                                                            <c:choose>
                                                                <c:when test="${option.value == 'PREPARING'}">Đơn chuyển sang chuẩn bị hàng và phiếu vận đơn sẽ được tạo để in.</c:when>
                                                                <c:when test="${option.value == 'SHIPPING'}">Chỉ chọn khi bên vận chuyển đã đến lấy đơn.</c:when>
                                                                <c:otherwise>${option.description}</c:otherwise>
                                                            </c:choose>
                                                        </small>
                                                    </span>
                                                </label>
                                            </c:forEach>
                                        </div>

                                        <div class="seller-status-note">
                                            <i data-lucide="info"></i>
                                            <span>Chỉ chuyển sang đã giao vận chuyển khi bên vận chuyển đến lấy.</span>
                                        </div>

                                        <div class="seller-status-actions">
                                            <a class="seller-status-secondary btn btn-outline-dark"
                                               href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">
                                                Hủy
                                            </a>
                                            <button type="submit" class="seller-status-primary btn btn-dark">
                                                <i data-lucide="refresh-cw"></i>
                                                <span>Cập nhật</span>
                                            </button>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="seller-status-locked">
                                        <i data-lucide="lock"></i>
                                        <h3>Người bán không thể cập nhật tiếp</h3>
                                        <p>${lockedMessage}</p>
                                        <a class="seller-status-primary btn btn-dark"
                                           href="${pageContext.request.contextPath}/seller/order/view?subOrderId=${orderStatus.subOrderId}">
                                            Quay lại chi tiết
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>
                    </div>

                    <aside class="seller-status-side">
                        <section class="seller-status-info-card card shadow-sm">
                            <h2>Thông tin đơn</h2>
                            <dl>
                                <dt>Khách hàng</dt>
                                <dd>${orderStatus.customerName}</dd>
                                <dt>Email</dt>
                                <dd>${orderStatus.customerEmail}</dd>
                                <dt>Ngày đặt</dt>
                                <dd><fmt:formatDate value="${orderStatus.buyerOrderedAt}" pattern="dd/MM/yyyy HH:mm"/></dd>
                                <dt>Tổng tiền</dt>
                                <dd><fmt:formatNumber value="${orderStatus.totalAmount}" type="number" maxFractionDigits="0"/>đ</dd>
                            </dl>
                        </section>

                        <section class="seller-status-info-card card shadow-sm">
                            <h2>Sản phẩm</h2>
                            <p>${empty orderStatus.productsSummary ? 'Chua co san pham' : orderStatus.productsSummary}</p>
                            <small>${orderStatus.itemCount} dòng hàng • ${orderStatus.totalQuantity} sản phẩm</small>
                        </section>

                        <section class="seller-status-info-card card shadow-sm">
                            <h2>Giao hàng</h2>
                            <dl>
                                <dt>Người nhận</dt>
                                <dd>${orderStatus.receiverName}</dd>
                                <dt>Số điện thoại</dt>
                                <dd>${orderStatus.receiverPhone}</dd>
                                <dt>Địa chỉ</dt>
                                <dd>${orderStatus.shippingAddress}</dd>
                            </dl>
                        </section>
                    </aside>
                </section>
            </c:when>
            <c:otherwise>
                <section class="seller-status-error-card card shadow-sm">
                    <i data-lucide="package-x"></i>
                    <h1>Không tải được đơn hàng</h1>
                    <p>Vui lòng quay lại danh sách đơn hàng và chọn một đơn hợp lệ.</p>
                    <a class="seller-status-primary btn btn-dark" href="${pageContext.request.contextPath}/seller/orders">Quay lại danh sách</a>
                </section>
            </c:otherwise>
        </c:choose>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function printShippingLabel() {
        const label = document.getElementById('shippingLabel');
        if (!label) {
            return;
        }

        const printWindow = window.open('', '_blank', 'width=720,height=820');
        printWindow.document.write(`
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <title>Phieu van don</title>
                <style>
                    body { margin: 0; padding: 16px; font-family: Arial, sans-serif; color: #000; }
                    .seller-shipping-label { width: 560px; min-height: 640px; border: 2px solid #000; line-height: 1.25; }
                    .label-header, .label-route, .label-footer { display: flex; }
                    .label-header { justify-content: space-between; gap: 16px; padding: 10px 12px; border-bottom: 2px dashed #000; }
                    .label-header strong { display: block; font-size: 20px; font-weight: 900; }
                    .label-header span, .label-code span, .label-payment span, .label-signature > span { display: block; font-size: 12px; font-weight: 800; }
                    .label-code { text-align: right; }
                    .label-code strong { display: block; margin-top: 4px; font-size: 13px; font-weight: 900; }
                    .label-route { min-height: 170px; border-bottom: 2px dashed #000; }
                    .label-address { flex: 1; padding: 12px; }
                    .label-address + .label-address { border-left: 1px dashed #000; }
                    h3 { margin: 0 0 8px; font-size: 14px; font-weight: 900; }
                    .label-address strong { display: block; font-size: 14px; font-weight: 900; }
                    .label-address p { margin: 3px 0 0; font-size: 14px; font-weight: 700; }
                    .label-products { min-height: 230px; padding: 12px; border-bottom: 2px dashed #000; }
                    .label-products p { margin: 0; font-size: 14px; font-weight: 700; }
                    .label-products em { display: block; margin-top: 132px; font-size: 13px; font-weight: 700; line-height: 1.35; }
                    .label-footer { min-height: 190px; }
                    .label-payment { flex: 1; padding: 12px; }
                    .label-payment strong { display: block; margin: 16px 0 18px; text-align: center; font-size: 26px; font-weight: 900; }
                    .label-instruction p { margin: 2px 0; font-size: 13px; font-weight: 700; }
                    .label-signature { flex: 1; padding: 12px; text-align: center; }
                    .label-signature > span { margin-bottom: 16px; }
                    .label-signature > div { min-height: 130px; padding: 12px; border: 2px solid #9ca3af; }
                    .label-signature strong { display: block; margin-bottom: 8px; font-size: 15px; font-weight: 900; }
                    .label-signature p { margin: 0; font-size: 13px; font-weight: 700; }
                </style>
            </head>
            <body>` + label.outerHTML + `</body>
            </html>
        `);
        printWindow.document.close();
        printWindow.focus();
        printWindow.print();
    }

    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
</script>
</body>
</html>
