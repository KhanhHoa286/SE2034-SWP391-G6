<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="hasSellerAccount" value="${requestScope.hasSellerAccount or sessionScope.hasSellerAccount}" />
<c:set var="hasPendingSellerRegistration" value="${requestScope.hasPendingSellerRegistration or sessionScope.hasPendingSellerRegistration}" />
<c:set var="sellerRegistrationRetry" value="${requestScope.sellerRegistrationRetry or sessionScope.sellerRegistrationRetry}" />
<c:set var="shopSuspended" value="${requestScope.shopSuspended or sessionScope.shopSuspended}" />

<aside class="profile-sidebar">
    <div class="profile-sidebar__head">
        <h2>Tài khoản của tôi</h2>
        <p>Quản lý sở thích của bạn</p>
    </div>

    <nav class="profile-sidebar__nav">
        <a class="profile-side-link ${param.active == 'dashboard' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/customer/dashboard">
            <span>Bảng điều khiển</span>
        </a>

        <a class="profile-side-link ${param.active == 'profile' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/customer/profile">
            <span>Hồ sơ</span>
        </a>

        <a class="profile-side-link ${param.active == 'addresses' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/customer/addresses">
            <span>Địa chỉ</span>
        </a>

        <a class="profile-side-link ${param.active == 'orders' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/customer/order-list">
            <span>Đơn hàng</span>
        </a>

        <c:choose>
            <c:when test="${hasSellerAccount and shopSuspended}">
                <button class="profile-side-link profile-side-link--suspended"
                        data-suspended-shop-trigger
                        type="button">
                    <span>Trang người bán</span>
                </button>
            </c:when>
            <c:when test="${hasSellerAccount}">
                <a class="profile-side-link ${param.active == 'seller' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/seller/orders">
                    <span>Trang người bán</span>
                </a>
            </c:when>
            <c:when test="${hasPendingSellerRegistration and not sellerRegistrationRetry}">
            </c:when>
            <c:otherwise>
                <a class="profile-side-link ${param.active == 'seller' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/seller-register">
                    <span>Đăng ký người bán</span>
                </a>
            </c:otherwise>
        </c:choose>

        <div class="profile-sidebar__logout">
            <a class="profile-side-link" href="${pageContext.request.contextPath}/logout">
                <span>Đăng xuất</span>
            </a>
        </div>
    </nav>
</aside>

<c:if test="${hasSellerAccount and shopSuspended}">
    <div id="shopSuspendedToast" class="shop-suspended-toast" role="status" aria-live="polite" hidden>
        <span class="material-symbols-outlined" aria-hidden="true">lock</span>
        <span>Shop của bạn đã bị khóa</span>
    </div>
    <script>
        (function () {
            var trigger = document.querySelector('[data-suspended-shop-trigger]');
            var toast = document.getElementById('shopSuspendedToast');
            var hideTimer;

            if (!trigger || !toast) return;

            trigger.addEventListener('click', function () {
                window.clearTimeout(hideTimer);
                toast.hidden = false;
                toast.classList.remove('is-hiding');
                toast.classList.add('is-visible');

                hideTimer = window.setTimeout(function () {
                    toast.classList.add('is-hiding');
                    window.setTimeout(function () {
                        toast.classList.remove('is-visible', 'is-hiding');
                        toast.hidden = true;
                    }, 200);
                }, 5000);
            });
        }());
    </script>
</c:if>
