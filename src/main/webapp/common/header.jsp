<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!-- Header -->
<header class="header">
    <div class="container header__inner">
        <a href="view-home-page.jsp" class="header__logo">MODA</a>

        <nav class="header__nav d-none d-md-block">
            <ul>
                <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/product-list?type=NU">Nữ</a></li>
                <li><a href="${pageContext.request.contextPath}/product-list?type=NAM">Nam</a></li>
                <li><a href="${pageContext.request.contextPath}/product-list?type=UNISEX">Unisex</a></li>
<%--                <li><a href="${pageContext.request.contextPath}/product-list?type=accessory">Phụ kiện</a></li>--%>
<%--                <li><a href="${pageContext.request.contextPath}/product-list?gender=sale_off" class="sale-link">Khuyến mãi</a></li>--%>
            </ul>
        </nav>

        <div class="header__icons">
            <div class="header__search">
                <form action="${pageContext.request.contextPath}/product-list" method="get">
                    <c:if test="${filter.type != null}">
                        <input type="hidden" value="${filter.type}" name="type">
                    </c:if>
                <input type="text" placeholder="Tìm kiếm..." class="header__search-input" name="text_search" value="${filter.textSearch}">
                    <button type="submit" style="border: 0px;background: transparent;"><i class="fa-solid fa-magnifying-glass header__icon"></i></button>
                </form>
            </div>
<%--            <a href="${pageContext.request.contextPath}/customer/wishlist" class="header__icon header__icon-cart" style="color: inherit; text-decoration: none;">--%>
<%--                <i class="fa-regular fa-heart header__icon"></i>--%>
<%--                <span class="cart-count" id="wishlist-count">${numberProductWishlist}</span>--%>
<%--            </a>--%>
            <a href="${pageContext.request.contextPath}/customer/cart" class="header__icon header__icon-cart" style="color: inherit; text-decoration: none;">
                <i class="fa-solid fa-bag-shopping"></i>
                <span class="cart-count" id="cart-count">${numberProductCart}</span>
            </a>
            <c:if test="${sessionScope.user != null}">
                <a href="${pageContext.request.contextPath}/customer/profile" class="avatar-link">
                <img src="${sessionScope.user.avatarUrl}" alt="User Avatar" class="header__avatar">
                </a>
            </c:if>
            <c:if test="${sessionScope.user == null}">
            <a href="${pageContext.request.contextPath}/customer/profile" class="avatar-link">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="User Avatar" class="header__avatar">
            </a>
                </c:if>
        </div>
    </div>
</header>
