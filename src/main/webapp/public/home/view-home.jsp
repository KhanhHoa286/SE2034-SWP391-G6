<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Architectural Minimalism</title>

    <!-- Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/home.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/public/header.jsp" />

<main>
    <!-- Hero Banner -->
    <section class="hero">
        <div class="container">
            <div class="hero__content">
                <h1 class="hero__title">Architectural<br>Minimalism.</h1>
                <a href="list-products.jsp" class="hero__btn">Khám phá ngay</a>
            </div>
        </div>
    </section>

    <!-- Ưu đãi sâu nhất -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Ưu đãi sâu nhất</h2>
            <a href="list-products.jsp">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${topDiscountedProducts}">
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">${product.discountPercentage}</span>
                    <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>${product.shopName}</span> <span class="location"><i class="fa-solid fa-location-dot"></i> ${product.provinceName}</span></div>
                    <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">${product.productName}</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current"><fmt:formatNumber value="${product.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                        <span class="product-card__price-old"><fmt:formatNumber value="${product.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                    </div>
                </div>
            </article>
            </c:forEach>
        </div>
    </section>

    <!-- Sản phẩm mới -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Sản phẩm mới</h2>
            <a href="list-products.jsp">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${latestProducts}">
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">${product.discountPercentage}</span>
                    <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>${product.shopName}</span> <span class="location"><i class="fa-solid fa-location-dot"></i> ${product.provinceName}</span></div>
                    <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">${product.productName}</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current"><fmt:formatNumber value="${product.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                        <span class="product-card__price-old"><fmt:formatNumber value="${product.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                    </div>
                </div>
            </article>
            </c:forEach>
        </div>
    </section>

    <!-- Sản phẩm bán chạy -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Sản phẩm bán chạy</h2>
            <a href="list-products.jsp">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${bestSellingProducts}">
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">${product.discountPercentage}</span>
                    <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>${product.shopName}</span> <span class="location"><i class="fa-solid fa-location-dot"></i> ${product.provinceName}</span></div>
                    <a href="list-products.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">${product.productName}</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current"><fmt:formatNumber value="${product.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                        <span class="product-card__price-old"><fmt:formatNumber value="${product.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                    </div>
                </div>
            </article>
            </c:forEach>
        </div>
    </section>

</main>

<jsp:include page="/public/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
