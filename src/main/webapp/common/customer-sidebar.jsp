<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="hasSellerAccount" value="${requestScope.hasSellerAccount or sessionScope.hasSellerAccount}" />

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
            <c:when test="${hasSellerAccount}">
                <a class="profile-side-link ${param.active == 'seller' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/seller/orders">
                    <span>Trang người bán</span>
                </a>
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
