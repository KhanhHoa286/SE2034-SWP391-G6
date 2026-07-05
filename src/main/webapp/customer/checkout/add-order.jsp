<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Đặt hàng</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
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

        <%-- ============================
             CỘT TRÁI: FORM ĐẶT HÀNG
             ============================ --%>
        <div class="add-order__form-section">

            <%-- 1. THÔNG TIN GIAO HÀNG --%>
            <div class="add-order__block">
                <h2 class="add-order__section-title">1. Thông tin giao hàng</h2>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Họ và tên</label>
                        <input type="text" class="form-control-custom" placeholder="Nhập tên đầy đủ của bạn" id="fullName" value="${checkoutResponse.addressResponse.fullName}" readonly>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Số điện thoại</label>
                        <input type="tel" class="form-control-custom" placeholder="090 000 0000" id="phone" value="${checkoutResponse.addressResponse.phone}" readonly>
                    </div>
                </div>

                <div class="form-row-3">
                    <div class="form-group">
                        <label class="form-label">Tỉnh / Thành phố</label>
                        <input type="tel" class="form-control-custom" placeholder="Tỉnh / Thành phố" id="phone" value="${checkoutResponse.addressResponse.provinceName}" readonly>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Quận / Huyện</label>
                        <input type="tel" class="form-control-custom" placeholder="Quận / Huyện" id="phone" value="${checkoutResponse.addressResponse.wardName}" readonly>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Chi tiết địa chỉ</label>
                        <input type="tel" class="form-control-custom" placeholder="Số nhà..." id="phone" value="${checkoutResponse.addressResponse.localDetail}" readonly>
                    </div>
                </div>

                <%-- Nút thêm địa chỉ mới — căn phải --%>
                <c:if test="${empty checkoutResponse.addressResponse}">
                <div class="text-end">
                    <p class="text-danger">* Vui lòng thêm địa chỉ để đặt hàng</p>
                </div>
                </c:if>
                <div class="add-address-row">
                    <a href="${pageContext.request.contextPath}/customer/addresses/add" class="add-address-btn" id="btn-add-address">
                        <i class="fa-solid fa-plus"></i>
                        Thêm địa chỉ mới
                    </a>
                </div>
            </div>

            <%-- ĐƯỜNG KẺ PHÂN CÁCH --%>
            <hr class="add-order__divider">

            <%-- 2. PHƯƠNG THỨC THANH TOÁN (chỉ COD & Chuyển khoản) --%>
            <div class="add-order__block">
                <h2 class="add-order__section-title">2. Phương thức thanh toán</h2>

                <div class="payment-method-grid">

                    <%-- COD --%>
                    <label class="payment-method-card selected" id="pm-cod" for="radio-cod" onclick="selectPayment('cod')">
                        <input type="radio" name="payment_method" id="radio-cod" value="COD" checked>
                        <i class="fa-solid fa-box-open"></i>
                        <span class="payment-method-card__name">COD</span>
                        <span class="payment-method-card__desc">Thanh toán khi nhận hàng</span>
                    </label>

                    <%-- CHUYỂN KHOẢN --%>
                    <label class="payment-method-card"  data-user-id = "${checkoutResponse.userId}" id="pm-bank" for="radio-bank" onclick="selectPayment('bank_transfer')">
                        <input type="radio" name="payment_method" id="radio-bank" value="BANK">
                        <i class="fa-solid fa-building-columns"></i>
                        <span class="payment-method-card__name">Chuyển khoản</span>
                        <span class="payment-method-card__desc">Xác nhận thủ công</span>
                    </label>
                        <input type="hidden" id="hidden-transaction-code" name="transaction_code" value="">
                </div>

                <%-- === KHỐI THÔNG TIN CHUYỂN KHOẢN (hiện khi chọn bank_transfer) === --%>
                <div class="bank-info-box" id="bankInfoBox">
                    <div class="bank-info-box__inner">

                        <%-- QR code --%>
                        <div class="bank-info-box__qr-wrapper">
                            <img
                                    src="https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=MODA-STK-9876543210-NGUYEN+VAN+AN&color=000000&bgcolor=ffffff&margin=10"
                                    alt="QR Chuyển khoản MODA"
                                    class="bank-info-box__qr"
                            >
                            <span class="bank-info-box__qr-label">Quét mã để chuyển tiền</span>
                        </div>

                        <%-- Thông tin tài khoản --%>
                        <div class="bank-info-box__details">
                            <div class="bank-info-box__bank-name">
                                <i class="fa-solid fa-landmark"></i>
                                Ngân hàng Vietcombank
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

                            <div class="bank-info-box__note">
                                <i class="fa-solid fa-circle-info"></i>
                                Đơn hàng sẽ được xác nhận sau khi chúng tôi kiểm tra thanh toán.
                            </div>
                        </div>

                    </div>
                </div><%-- /bankInfoBox --%>

            </div>

        </div>

        <%-- ============================
             CỘT PHẢI: TÓM TẮT ĐƠN HÀNG
             ============================ --%>
        <aside>
            <div class="order-summary-panel">
                <h2 class="order-summary-panel__title">Tóm tắt đơn hàng</h2>

                <%-- ===== KHU VỰC SCROLL SẢN PHẨM ===== --%>
                <div class="summary-products-scroll" id="summaryScroll">

                    <%-- Shop 1 — 2 sản phẩm --%>
                    <c:if test="${type == 'CART'}">
                    <div class="summary-shop-group">
                        <c:forEach items="${checkoutResponse.shopCartResponses}" var="shop">
                        <div class="summary-shop-header">
                            <div class="summary-shop-name">
                                <i class="fa-solid fa-store"></i>
                                ${shop.shopName}
                            </div>
                            <a href="${pageContext.request.contextPath}/shop?shop_id=${shop.shopId}" class="summary-shop-link">Xem Shop</a>
                        </div>

                        <%-- SP 1 --%>
                            <c:forEach items="${shop.items}" var="shopItem">
                        <div class="summary-product-item">
                            <img
                                    src="${shopItem.thumbnailUrl}"
                                    alt="${shopItem.productName}"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">${shopItem.productName}</div>
                                <div class="summary-product-variant">Kích cỡ: ${shopItem.sizeName} | Màu sắc: ${shopItem.colorName}</div>
                                <div class="summary-product-qty">Số lượng: ${shopItem.quantity}</div>
                            </div>
                            <div class="summary-product-price"><c:if test="${not empty shopItem.discountPrice}"><fmt:formatNumber type="currency" maxFractionDigits="0" value="${shopItem.discountPrice}"/></c:if></div>
                        </div>
                            </c:forEach>
                        </c:forEach>
                    </div>
                    </c:if>
                <%--Nếu đi từ trang chi tiết sang--%>
                        <c:if test="${type == 'DETAILS_PRODUCT'}">
                            <div class="summary-shop-group">
                                    <div class="summary-shop-header">
                                        <div class="summary-shop-name">
                                            <i class="fa-solid fa-store"></i>
                                                ${checkoutResponse.summary.shopName}
                                        </div>
                                        <a href="${pageContext.request.contextPath}/shop?shop_id= ${checkoutResponse.summary.shopId}" class="summary-shop-link">Xem Shop</a>
                                    </div>

                                    <%-- SP 1 --%>
                                        <div class="summary-product-item">
                                            <img src="${checkoutResponse.summary.thumbnail}" alt="${checkoutResponse.summary.productName}" class="summary-product-img">
                                            <div class="summary-product-info">
                                                <div class="summary-product-name">${checkoutResponse.summary.productName}</div>
                                                <div class="summary-product-variant">Kích cỡ: ${checkoutResponse.summary.sizeName} | Màu sắc: ${checkoutResponse.summary.colorName}</div>
                                                <div class="summary-product-qty">Số lượng: ${checkoutResponse.summary.quantity}</div>
                                            </div>
                                            <div class="summary-product-price"><c:if test="${not empty checkoutResponse.summary.price}"><fmt:formatNumber type="currency" maxFractionDigits="0" value="${checkoutResponse.summary.price}"/></c:if></div>
                                        </div>
                            </div>
                        </c:if>
                </div>
                <%-- ===== FOOTER CỐ ĐỊNH: Tổng tiền + Nút thanh toán ===== --%>
                <div class="summary-footer">
                    <div class="summary-totals">
                        <div class="summary-row summary-row--total">
                            <span>Tổng thanh toán</span>
                            <span><c:if test="${not empty checkoutResponse.allShopTotal}"><fmt:formatNumber type="currency" maxFractionDigits="0" value="${checkoutResponse.allShopTotal}"/></c:if></span>
                        </div>
                    </div>

                    <button type="submit"
                            class="add-order__checkout-btn ${empty checkoutResponse.addressResponse.fullName ? 'disabled' : ''}"
                            id="btn-checkout"
                    ${empty checkoutResponse.addressResponse.fullName ? 'disabled' : ''}>
                        Thanh toán ngay
                    </button>
                    <%--Kiểm tra nút thanh toán và gửi đi--%>
                    <input type="hidden" value="${type}" name="type">
                    <input type="hidden" value="${checkoutResponse.addressResponse.fullName}" name="receiver_name">
                    <input type="hidden" value="${checkoutResponse.addressResponse.phone}" name="receiver_phone">
                    <input type="hidden" value="${checkoutResponse.addressResponse.localDetail}, ${checkoutResponse.addressResponse.wardName}, ${checkoutResponse.addressResponse.provinceName}" name="shipping_address">
                    <input type="hidden" value="${checkoutResponse.allShopTotal}" name="total_amount">

                    <c:if test="${type == 'CART'}">
                        <input type="hidden" value="${checkoutResponse.listCartItemIds}" name="cartItemIds">
                    </c:if>
                    <c:if test="${type == 'DETAILS_PRODUCT'}">
                        <input type="hidden" value="${checkoutResponse.variantId}" name="variant_id">
                        <input type="hidden" value="${checkoutResponse.summary.quantity}" name="quantity_details_product">
                    </c:if>
                    <%----------------------%>
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

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/add-order.js"></script>
</body>
</html>

