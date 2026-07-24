<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Đặt hàng</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/popup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/add-order.css">
</head>

<body>
<%-- Header nhúng vào --%>
<jsp:include page="/common/header.jsp" />

<main class="container add-order-page">
    <%-- Nút quay lại --%>
    <a href="javascript:history.back()" class="add-order__back">
        <i class="fa-solid fa-arrow-left"></i> Quay lại
    </a>

    <form action="${pageContext.request.contextPath}/customer/add-order" method="post">
        <div class="add-order__layout">

            <%--============================ CỘT TRÁI: FORM ĐẶT HÀNG ============================--%>
            <div class="add-order__form-section">

                <%-- 1. THÔNG TIN GIAO HÀNG --%>
                <div class="add-order__block">
                    <c:if test="${not empty checkoutResponse.addressResponse}">
                        <div>
                            <h2 class="add-order__section-title">1. Thông tin giao hàng</h2>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Họ và tên</label>
                                    <input type="text" class="form-control-custom" placeholder="Nhập tên đầy đủ của bạn"
                                           id="fullName" value="${checkoutResponse.addressResponse.fullName}" readonly>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control-custom" placeholder="090 000 0000"
                                           id="phone" value="${checkoutResponse.addressResponse.phone}" readonly>
                                </div>
                            </div>

                            <div class="form-row-3">
                                <div class="form-group">
                                    <label class="form-label">Tỉnh / Thành phố</label>
                                    <input type="text" class="form-control-custom" placeholder="Tỉnh / Thành phố"
                                           value="${checkoutResponse.addressResponse.provinceName}" readonly>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Quận / Huyện</label>
                                    <input type="text" class="form-control-custom" placeholder="Quận / Huyện"
                                           value="${checkoutResponse.addressResponse.wardName}" readonly>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Chi tiết địa chỉ</label>
                                    <input type="text" class="form-control-custom" placeholder="Số nhà..."
                                           value="${checkoutResponse.addressResponse.localDetail}" readonly>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <%-- Nút thêm địa chỉ mới — căn phải --%>
                    <c:if test="${empty checkoutResponse.addressResponse}">
                        <div class="${empty checkoutResponse.addressResponse ? 'text-center' : 'text-end'}">
                            <p class="text-danger">* Vui lòng chọn thêm địa chỉ để đặt hàng</p>
                        </div>
                    </c:if>

                    <div class="add-address-row ${empty checkoutResponse.addressResponse ? 'justify-content-center' : ''}">
                        <a href="${pageContext.request.contextPath}/customer/addresses?type=checkout" class="add-address-btn" id="btn-add-address">
                            <i class="fa-solid fa-plus"></i> Thêm địa chỉ mới
                        </a>
                    </div>
                </div>

                <%--đường kẻ--%>
                <hr class="add-order__divider">

                <%--phương thức thanh toán --%>
                <div class="add-order__block">
                    <h2 class="add-order__section-title">2. Phương thức thanh toán</h2>

                    <div class="payment-method-grid ${empty checkoutResponse.addressResponse ? 'disabled-section' : ''}">
                        <%-- COD --%>
                        <label class="payment-method-card selected" id="pm-cod" for="radio-cod" onclick="selectPayment('cod')">
                            <input type="radio" name="payment_method" id="radio-cod" value="COD" checked>
                            <i class="fa-solid fa-box-open"></i>
                            <span class="payment-method-card__name">COD</span>
                            <span class="payment-method-card__desc">Thanh toán khi nhận hàng</span>
                        </label>

                        <%-- CHUYỂN KHOẢN --%>
                        <label class="payment-method-card" data-user-id="${checkoutResponse.userId}" id="pm-bank" for="radio-bank" onclick="selectPayment('bank_transfer')">
                            <input type="radio" name="payment_method" id="radio-bank" value="BANK">
                            <i class="fa-solid fa-building-columns"></i>
                            <span class="payment-method-card__name">Chuyển khoản</span>
                            <span class="payment-method-card__desc">Xác nhận thủ công</span>
                        </label>
                    </div>

                    <%--===chọn thông tin chuyển khoản===--%>
                    <div class="bank-info-box" id="bankInfoBox">
                        <div class="bank-info-box__inner">
                            <%-- QR code --%>
                            <div class="bank-info-box__qr-wrapper">
                                <img src="https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=MODA-STK-9876543210-NGUYEN+VAN+AN&color=000000&bgcolor=ffffff&margin=10"
                                     alt="QR Chuyển khoản MODA" class="bank-info-box__qr">
                                <span class="bank-info-box__qr-label">Quét mã để chuyển tiền</span>
                            </div>

                            <%-- Thông tin tài khoản --%>
                            <div class="bank-info-box__details">
                                <div class="bank-info-box__bank-name">
                                    <i class="fa-solid fa-landmark"></i> Ngân hàng Vietcombank
                                </div>

                                <div class="bank-info-box__row">
                                    <span class="bank-info-box__label">Tên người nhận</span>
                                    <span class="bank-info-box__value">Cong Ty MODA</span>
                                </div>

                                <div class="bank-info-box__row">
                                    <span class="bank-info-box__label">Số tài khoản</span>
                                    <div class="bank-info-box__stk-row">
                                        <span class="bank-info-box__value" id="stkValue">13686789</span>
                                        <button type="button" class="bank-info-box__copy-btn" id="btnCopyStk" title="Sao chép STK">
                                            <i class="fa-regular fa-copy"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="bank-info-box__row">
                                    <span class="bank-info-box__label">Nội dung CK</span>
                                    <span id="paymentContent" class="bank-info-box__value bank-info-box__value--accent">MODA</span>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <%--Tóm tắt đơn hàng--%>
            <aside>
                <div class="order-summary-panel">
                    <h2 class="order-summary-panel__title">Tóm tắt đơn hàng</h2>

                    <div class="summary-products-scroll" id="summaryScroll">

                        <%-- Đi từ trang Giỏ hàng --%>
                        <c:if test="${type == 'CART'}">
                            <div class="summary-shop-group">
                                <c:forEach items="${checkoutResponse.shopCartResponses}" var="shop">
                                    <div class="summary-shop-header">
                                        <div class="summary-shop-name">
                                            <i class="fa-solid fa-store"></i> ${shop.shopName}
                                        </div>
                                        <a href="${pageContext.request.contextPath}/shop?shop_id=${shop.shopId}" class="summary-shop-link">Xem Shop</a>
                                    </div>

                                    <c:forEach items="${shop.items}" var="shopItem">
                                        <div class="summary-product-item">
                                            <img src="${shopItem.thumbnailUrl}" alt="${shopItem.productName}" class="summary-product-img">
                                            <div class="summary-product-info">
                                                <div class="summary-product-name">
                                                        ${shopItem.productName}
                                                </div>
                                                <div class="summary-product-variant">
                                                    Kích cỡ: ${shopItem.sizeName} | Màu sắc: ${shopItem.colorName}
                                                </div>
                                                <div class="summary-product-qty">
                                                    Số lượng: ${shopItem.quantity}
                                                </div>
                                            </div>
                                            <div class="summary-product-price">
                                                <c:if test="${not empty shopItem.discountPrice}">
                                                    <fmt:formatNumber type="currency" maxFractionDigits="0" value="${shopItem.discountPrice}" />
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:forEach>
                            </div>
                        </c:if>

                        <%-- Đi từ trang Chi tiết sản phẩm --%>
                        <c:if test="${type == 'DETAILS_PRODUCT'}">
                            <div class="summary-shop-group">
                                <div class="summary-shop-header">
                                    <div class="summary-shop-name">
                                        <i class="fa-solid fa-store"></i> ${checkoutResponse.summary.shopName}
                                    </div>
                                    <a href="${pageContext.request.contextPath}/shop?shop_id=${checkoutResponse.summary.shopId}" class="summary-shop-link">Xem Shop</a>
                                </div>

                                <div class="summary-product-item">
                                    <img src="${checkoutResponse.summary.thumbnail}" alt="${checkoutResponse.summary.productName}" class="summary-product-img">
                                    <div class="summary-product-info">
                                        <div class="summary-product-name">
                                                ${checkoutResponse.summary.productName}
                                        </div>
                                        <div class="summary-product-variant">
                                            Kích cỡ: ${checkoutResponse.summary.sizeName} | Màu sắc: ${checkoutResponse.summary.colorName}
                                        </div>
                                        <div class="summary-product-qty">
                                            Số lượng: ${checkoutResponse.summary.quantity}
                                        </div>
                                    </div>
                                    <div class="summary-product-price">
                                        <c:if test="${not empty checkoutResponse.summary.price}">
                                            <fmt:formatNumber type="currency" maxFractionDigits="0" value="${checkoutResponse.summary.price}" />
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <%-- Tổng tiền + Nút thanh toán --%>
                    <div class="summary-footer">
                        <div class="summary-totals">
                            <div class="summary-row summary-row--total">
                                <span>Tổng thanh toán</span>
                                <span>
                                        <c:if test="${not empty checkoutResponse.allShopTotal}">
                                            <fmt:formatNumber type="currency" maxFractionDigits="0" value="${checkoutResponse.allShopTotal}" />
                                        </c:if>
                                    </span>
                            </div>
                        </div>

                        <button type="button"
                                class="add-order__checkout-btn ${empty checkoutResponse.addressResponse.fullName ? 'disabled' : ''}"
                                id="btn-checkout"
                                onclick="confirmCheckout(this)"
                        ${empty checkoutResponse.addressResponse.fullName ? 'disabled' : ''}>
                            Thanh toán ngay
                        </button>

                        <%-- Kiểm tra nút thanh toán và gửi đi --%>
                        <input type="hidden" name="type" value="${type}">
                        <input type="hidden" name="receiver_name" value="${checkoutResponse.addressResponse.fullName}">
                        <input type="hidden" name="receiver_phone" value="${checkoutResponse.addressResponse.phone}">
                        <input type="hidden" name="shipping_address" value="${checkoutResponse.addressResponse.localDetail}, ${checkoutResponse.addressResponse.wardName}, ${checkoutResponse.addressResponse.provinceName}">
                        <input type="hidden" name="total_amount" value="${checkoutResponse.allShopTotal}">

                        <c:if test="${type == 'CART'}">
                            <input type="hidden" name="cartItemIds" value="${checkoutResponse.listCartItemIds}">
                        </c:if>
                        <c:if test="${type == 'DETAILS_PRODUCT'}">
                            <input type="hidden" name="variant_id" value="${checkoutResponse.variantId}">
                            <input type="hidden" name="quantity_details_product" value="${checkoutResponse.summary.quantity}">
                        </c:if>

                        <p class="summary-terms">
                            Bằng cách đặt hàng, bạn đồng ý với các
                            <a href="#">Điều khoản &amp; Chính sách</a> của MODA.
                        </p>
                    </div>
                </div>
            </aside>

        </div>
    </form>
</main>


<!-- Popup Xác nhận thanh toán -->
<div id="simpleConfirmModal" class="simple-confirm-modal">
    <div class="simple-confirm-content">
        <h5 id="simpleConfirmMessage" class="simple-confirm-title">Bạn có chắc chắn?</h5>
        <div class="simple-confirm-actions">
            <button type="button" class="simple-confirm-btn-cancel" onclick="closeSimpleConfirm()">Hủy</button>
            <button type="button" id="simpleConfirmBtn" class="simple-confirm-btn-ok">Đồng ý</button>
        </div>
    </div>
</div>

<!-- Popup Thông báo lỗi thanh toán -->
<div id="errorAlertModal" class="simple-confirm-modal">
    <div class="simple-confirm-content">
        <div class="mb-3 text-danger">
            <i class="fa-solid fa-circle-exclamation fa-3x"></i>
        </div>
        <h5 id="errorAlertTitle" class="simple-confirm-title text-danger mb-2">Thanh toán không thành công</h5>
        <p id="errorAlertMessage" class="text-secondary mb-4" style="font-size: 0.95rem; line-height: 1.5;"></p>
        <div class="simple-confirm-actions">
            <button type="button" class="simple-confirm-btn-ok bg-danger border-0 px-4" onclick="closeErrorAlert()">Đã hiểu</button>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/add-order.js"></script>

<script>
    let confirmCallback = null;

    function openSimpleConfirm(message, callback) {
        document.getElementById('simpleConfirmMessage').innerText = message;
        confirmCallback = callback;
        document.getElementById('simpleConfirmModal').style.display = 'block';
    }

    function closeSimpleConfirm() {
        document.getElementById('simpleConfirmModal').style.display = 'none';
        confirmCallback = null;
    }

    document.getElementById('simpleConfirmBtn').addEventListener('click', function () {
        if (confirmCallback) confirmCallback();
        closeSimpleConfirm();
    });

    function confirmCheckout(btn) {
        if (btn.classList.contains('disabled')) return;
        openSimpleConfirm("Bạn có chắc chắn muốn tiến hành thanh toán đơn hàng này không?", function () {
            btn.closest('form').submit();
        });
    }

    // Xử lý Popup hiển thị lỗi thanh toán dựa trên param 'error'
    const ERROR_MESSAGES = {
        'order_failed': 'Đã xảy ra lỗi trong quá trình xử lý tạo đơn hàng. Vui lòng thử lại sau!',
        'empty_address': 'Vui lòng cập nhật đầy đủ thông tin người nhận và địa chỉ giao hàng!',
        'self_buy': 'Bạn không thể đặt mua sản phẩm từ cửa hàng của chính mình!',
        'out_of_stock': 'Sản phẩm đã hết hàng hoặc số lượng trong kho không đủ để đáp ứng!',
        'invalid_product': 'Thông tin sản phẩm hoặc biến thể không hợp lệ!'
    };

    function showErrorAlert(errorCode) {
        const message = ERROR_MESSAGES[errorCode] || 'Đã xảy ra lỗi trong quá trình thanh toán. Vui lòng thử lại!';
        document.getElementById('errorAlertMessage').innerText = message;
        document.getElementById('errorAlertModal').style.display = 'block';
    }

    function closeErrorAlert() {
        document.getElementById('errorAlertModal').style.display = 'none';
        // Xóa param error khỏi URL sau khi đóng popup để tránh bật lại khi F5
        const url = new URL(window.location.href);
        if (url.searchParams.has('error')) {
            url.searchParams.delete('error');
            window.history.replaceState({}, document.title, url.toString());
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const errorCode = urlParams.get('error');
        if (errorCode) {
            showErrorAlert(errorCode);
        }
    });
</script>
</body>
</html>