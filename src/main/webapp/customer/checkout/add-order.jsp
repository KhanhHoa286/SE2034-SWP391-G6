<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                        <input
                                type="text"
                                class="form-control-custom"
                                placeholder="Nhập tên đầy đủ của bạn"
                                id="fullName"
                        >
                    </div>
                    <div class="form-group">
                        <label class="form-label">Số điện thoại</label>
                        <input
                                type="tel"
                                class="form-control-custom"
                                placeholder="090 000 0000"
                                id="phone"
                        >
                    </div>
                </div>

                <div class="form-row-3">
                    <div class="form-group">
                        <label class="form-label">Tỉnh / Thành phố</label>
                        <select class="form-control-custom" id="province">
                            <option value="">Hồ Chí Minh</option>
                            <option value="hn">Hà Nội</option>
                            <option value="dn">Đà Nẵng</option>
                            <option value="ct">Cần Thơ</option>
                            <option value="bd">Bình Dương</option>
                            <option value="br">Bà Rịa - Vũng Tàu</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Quận / Huyện</label>
                        <select class="form-control-custom" id="district">
                            <option value="">Quận 1</option>
                            <option value="q2">Quận 2</option>
                            <option value="q3">Quận 3</option>
                            <option value="q4">Quận 4</option>
                            <option value="tb">Tân Bình</option>
                            <option value="pn">Phú Nhuận</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Chi tiết địa chỉ</label>
                        <input
                                type="tel"
                                class="form-control-custom"
                                placeholder="Số nhà..."
                                id="phone"
                        >
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Địa chỉ chi tiết</label>
                    <input
                            type="text"
                            class="form-control-custom"
                            placeholder="Số nhà, tên đường..."
                            id="addressDetail"
                    >
                </div>

                <%-- Nút thêm địa chỉ mới — căn phải --%>
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
                        <input type="radio" name="paymentMethod" id="radio-cod" value="cod" checked>
                        <i class="fa-solid fa-box-open"></i>
                        <span class="payment-method-card__name">COD</span>
                        <span class="payment-method-card__desc">Thanh toán khi nhận hàng</span>
                    </label>

                    <%-- CHUYỂN KHOẢN --%>
                    <label class="payment-method-card" id="pm-bank" for="radio-bank" onclick="selectPayment('bank_transfer')">
                        <input type="radio" name="paymentMethod" id="radio-bank" value="bank_transfer">
                        <i class="fa-solid fa-building-columns"></i>
                        <span class="payment-method-card__name">Chuyển khoản</span>
                        <span class="payment-method-card__desc">Xác nhận thủ công</span>
                    </label>

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
                                <span class="bank-info-box__value">NGUYEN VAN AN</span>
                            </div>

                            <div class="bank-info-box__row">
                                <span class="bank-info-box__label">Số tài khoản</span>
                                <div class="bank-info-box__stk-row">
                                    <span class="bank-info-box__value" id="stkValue">9876 5432 10</span>
                                    <button type="button" class="bank-info-box__copy-btn" id="btnCopyStk" title="Sao chép STK">
                                        <i class="fa-regular fa-copy"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="bank-info-box__row">
                                <span class="bank-info-box__label">Nội dung CK</span>
                                <span class="bank-info-box__value bank-info-box__value--accent">MODA [Tên bạn]</span>
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
                    <div class="summary-shop-group">
                        <div class="summary-shop-header">
                            <div class="summary-shop-name">
                                <i class="fa-solid fa-store"></i>
                                MODA HÀ NỘI
                            </div>
                            <a href="#" class="summary-shop-link">Xem Shop</a>
                        </div>

                        <%-- SP 1 --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=200&auto=format&fit=crop"
                                    alt="Áo khoác Wool Overcoat"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Áo khoác Wool Overcoat</div>
                                <div class="summary-product-variant">SIZE: L | COLOR: BLACK</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">5.200.000đ</div>
                        </div>

                        <%-- SP 2 --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1600185365778-d9e3b1dcd23d?q=80&w=200&auto=format&fit=crop"
                                    alt="Quần âu Slim Fit"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Quần âu Slim Fit</div>
                                <div class="summary-product-variant">SIZE: 31 | COLOR: NAVY</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">1.350.000đ</div>
                        </div>
                    </div>

                    <%-- Shop 2 — 2 sản phẩm --%>
                    <div class="summary-shop-group">
                        <div class="summary-shop-header">
                            <div class="summary-shop-name">
                                <i class="fa-solid fa-store"></i>
                                MODA SÀI GÒN
                            </div>
                            <a href="#" class="summary-shop-link">Xem Shop</a>
                        </div>

                        <%-- SP 3 --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=200&auto=format&fit=crop"
                                    alt="Sơ mi LpA Premium"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Sơ mi LpA Premium</div>
                                <div class="summary-product-variant">SIZE: M | COLOR: WHITE</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">850.000đ</div>
                        </div>

                        <%-- SP 4 (mới) --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=200&auto=format&fit=crop"
                                    alt="Thắt lưng da Minimalist"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Thắt lưng da Minimalist</div>
                                <div class="summary-product-variant">SIZE: OS | COLOR: TAN</div>
                                <div class="summary-product-qty">x2</div>
                            </div>
                            <div class="summary-product-price">1.700.000đ</div>
                        </div>
                    </div>

                    <%-- Shop 3 (mới) — 1 sản phẩm --%>
                    <div class="summary-shop-group">
                        <div class="summary-shop-header">
                            <div class="summary-shop-name">
                                <i class="fa-solid fa-store"></i>
                                Gian hàng: MODA ARCHIVE
                            </div>
                            <a href="#" class="summary-shop-link">Xem Shop</a>
                        </div>

                        <%-- SP 5 --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=200&auto=format&fit=crop"
                                    alt="Sneaker Archive Low"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Sneaker Archive Low</div>
                                <div class="summary-product-variant">SIZE: 42 | COLOR: WHITE</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">2.900.000đ</div>
                        </div>

                        <%-- SP 6 (mới) --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1434389677669-e08b4cac3105?q=80&w=200&auto=format&fit=crop"
                                    alt="Mũ Bucket Canvas"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Mũ Bucket Canvas</div>
                                <div class="summary-product-variant">SIZE: FREE | COLOR: BEIGE</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">450.000đ</div>
                        </div>

                    </div><%-- /summary-shop-group Shop 3 --%>

                    <%-- Shop 4 (mới) — 2 sản phẩm --%>
                    <div class="summary-shop-group">
                        <div class="summary-shop-header">
                            <div class="summary-shop-name">
                                <i class="fa-solid fa-store"></i>
                                Gian hàng: MODA STUDIO
                            </div>
                            <a href="#" class="summary-shop-link">Xem Shop</a>
                        </div>

                        <%-- SP 7 (mới) --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1618354691373-d851c5c3a990?q=80&w=200&auto=format&fit=crop"
                                    alt="Áo Hoodie Oversized"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Áo Hoodie Oversized</div>
                                <div class="summary-product-variant">SIZE: XL | COLOR: GREY</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">980.000đ</div>
                        </div>

                        <%-- SP 8 (mới) --%>
                        <div class="summary-product-item">
                            <img
                                    src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?q=80&w=200&auto=format&fit=crop"
                                    alt="Túi Tote Leather"
                                    class="summary-product-img"
                            >
                            <div class="summary-product-info">
                                <div class="summary-product-name">Túi Tote Leather</div>
                                <div class="summary-product-variant">SIZE: OS | COLOR: BLACK</div>
                                <div class="summary-product-qty">x1</div>
                            </div>
                            <div class="summary-product-price">1.620.000đ</div>
                        </div>

                    </div><%-- /summary-shop-group Shop 4 --%>

                </div><%-- /summary-products-scroll --%>


                <%-- ===== FOOTER CỐ ĐỊNH: Tổng tiền + Nút thanh toán ===== --%>
                <div class="summary-footer">
                    <div class="summary-totals">
                        <div class="summary-row">
                            <span>Tạm tính</span>
                            <span>12.000.000đ</span>
                        </div>
                        <div class="summary-row summary-row--total">
                            <span>Tổng thanh toán</span>
                            <span>12.000.000đ</span>
                        </div>
                    </div>

                    <button class="add-order__checkout-btn" id="btn-checkout">
                        Thanh toán ngay
                    </button>

                    <p class="summary-terms">
                        Bằng cách đặt hàng, bạn đồng ý với các
                        <a href="#">Điều khoản &amp; Chính sách</a> của MODA.
                    </p>
                </div><%-- /summary-footer --%>

            </div><%-- /order-summary-panel --%>
        </aside>

    </div>
</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function selectPayment(method) {
        document.getElementById('pm-cod').classList.toggle('selected', method === 'cod');
        document.getElementById('pm-bank').classList.toggle('selected', method === 'bank_transfer');
        document.getElementById('bankInfoBox').classList.toggle('active', method === 'bank_transfer');
    }
</script>

</body>
</html>

