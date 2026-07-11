<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />

<article class="product-card col-6 col-md-4 col-lg-3">
    <a href="${pageContext.request.contextPath}/product-detail?pid=${product.productId}" style="color:inherit; text-decoration:none;">
        <div class="product-card__img-wrapper">
            <c:if test="${product.discountPercentage > 0}">
                <span class="product-card__badge">-${product.discountPercentage}%</span>
            </c:if>
            <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
        </div>
    </a>
    <div class="product-card__info">
        <div class="product-card__brand">
            <span>${product.shopName}</span>
            <span class="location"><i class="fa-solid fa-location-dot"></i> ${product.provinceName}</span>
        </div>
        <a href="${pageContext.request.contextPath}/product-detail?pid=${product.productId}" style="color:inherit; text-decoration:none;">
            <h3 class="product-card__title">${product.productName}</h3>
        </a>
        <div class="product-card__price">
            <c:if test="${product.discountPercentage > 0}">
                <span class="product-card__price-current">
                    <fmt:formatNumber value="${product.finalPrice}" type="currency" maxFractionDigits="0"/>
                </span>
            </c:if>
            <span class="${product.discountPercentage > 0 ? 'product-card__price-old' : 'product-card__price-current'}">
                <fmt:formatNumber value="${product.basePrice}" type="currency" maxFractionDigits="0"/>
            </span>
        </div>
        <div style="font-size: 0.85rem; color: #777; margin-top: 4px;">Số lượng: ${product.totalStock}</div>
    </div>
</article>