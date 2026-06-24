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
    <title>MODA - Giỏ hàng của bạn</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/cart.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp" />

<main class="container cart-page">

    <div class="cart-header">
        <a href="javascript:history.back()" class="cart-breadcrumb text-dark text-decoration-none"><i class="fa-solid fa-arrow-left"></i> TIẾP TỤC MUA SẮM</a>
        <h1 class="cart-title">GIỎ HÀNG CỦA BẠN</h1>
    </div>

    <div class="row mt-4">
        <!-- Left: Cart Items -->
        <div class="cart-items-wrapper">
            <c:if test="${empty cartDetail}">
                <p class="text-muted fs-5 text-center">Giỏ hàng hiện chưa có sản phẩm nào!</p>
            </c:if>
            <!-- Vendor 1 -->
            <c:forEach items="${cartDetail}" var="shop">
            <div class="vendor-group">
                <div class="vendor-header">
                    <div class="vendor-header__left">
<%--                        <input type="checkbox" checked data-shop-id="${shop.key}">--%>
                        <i class="fa-solid fa-store"></i> ${shop.value.shopName}
                    </div>
<%--                    <div class="vendor-header__right">--%>
<%--                        <input type="text" placeholder="Mã giảm giá" class="discount-input">--%>
<%--                        <button class="apply-discount-btn">ÁP DỤNG</button>--%>
<%--                    </div>--%>
                </div>

                <!-- Item 1 -->
                <c:forEach items="${shop.value.items}" var="shopProduct">
                <div class="cart-item">
<%--                    <input type="checkbox" ${shopProduct.selected ? 'checked' : ''} value="${shopProduct.cartItemId}" class="cart-item__checkbox">--%>
                    <img src="${shopProduct.thumbnailUrl}" alt="${shopProduct.productName}" class="cart-item__img">

                    <div class="cart-item__info">
                        <h3 class="cart-item__title">${shopProduct.productName}</h3>
                        <div class="cart-item__variant">Màu: ${shopProduct.colorName} | Size: ${shopProduct.sizeName}</div>
                        <div class="cart-item__price" data-price="${shopProduct.discountPrice}"><fmt:formatNumber value="${shopProduct.discountPrice}" type="currency" maxFractionDigits="0"/></div>
                    </div>

                    <div class="cart-item__actions">
                        <div class="quantity-control">
                            <button class="qty-btn" onclick="updateItemQuantity('${pageContext.request.contextPath}', ${shopProduct.cartItemId},${shopProduct.variantId},${shopProduct.shopId},'decrease', this)">-</button>
                            <input type="number" value="${shopProduct.quantity}" class="qty-input" min="1" readonly>
                            <button class="qty-btn"  onclick="updateItemQuantity('${pageContext.request.contextPath}', ${shopProduct.cartItemId},${shopProduct.variantId},${shopProduct.shopId}, 'increase', this)">+</button>
                        </div>
                        <button class="cart-item__remove" onclick="removeAnItem('${pageContext.request.contextPath}',${shopProduct.cartItemId},${shopProduct.shopId},this)"><i class="fa-regular fa-trash-can"></i></button>
                        <span class="stock-error text-danger" style=" font-size: 12px; margin-top: 5px;"></span>
                    </div>
                </div>
                </c:forEach>
                <div class="vendor-footer">
<%--                    <div class="vendor-footer__shipping">Phí vận chuyển dự kiến (Shop này): Miễn phí</div>--%>
                    <div class="vendor-footer__subtotal shopTotal" id="subtotal_shop_cart">Tạm tính đơn hàng: <fmt:formatNumber value="${shop.value.shopTotal}" type="currency" maxFractionDigits="0"/></div>
                </div>
            </div>
                </c:forEach>
            </div>
        <!-- Right: Order Summary -->
        <div class="order-summary">
<%--                <h2 class="order-summary__title">TÓM TẮT ĐƠN HÀNG</h2>--%>

<%--                <div class="summary-row">--%>
<%--                    <span>Tạm tính</span>--%>
<%--                    <span>14,900,000đ</span>--%>
<%--                </div>--%>
<%--                <div class="summary-row">--%>
<%--                    <span>Phí vận chuyển (Tổng)</span>--%>
<%--                    <span>0đ</span>--%>
<%--                </div>--%>
<%--                <div class="summary-row">--%>
<%--                    <span>Giảm giá</span>--%>
<%--                    <span>0đ</span>--%>
<%--                </div>--%>

            <div class="summary-row total">
                <span>TỔNG CỘNG</span>
                <span id="new-all-shop-total"><fmt:formatNumber value="${shopAllTotal}" type="currency" maxFractionDigits="0"/></span>
            </div>

            <button class="checkout-btn">TIẾN HÀNH THANH TOÁN</button>
<%--                <div class="payment-methods">--%>
<%--                    <p>CHẤP NHẬN THANH TOÁN</p>--%>
<%--                    <div class="payment-icons">--%>
<%--                        <i class="fa-brands fa-cc-visa"></i>--%>
<%--                        <i class="fa-brands fa-cc-mastercard"></i>--%>
<%--                        <i class="fa-solid fa-money-bill-wave"></i>--%>
<%--                    </div>--%>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/common/footer.jsp" />


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/cart.js"></script>
</body>
</html>


