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

<jsp:include page="/common/header.jsp" />

<main>
    <!-- Hero Banner -->
    <section class="hero">
        <div class="container">
            <div class="hero__content">
                <h1 class="hero__title">Định Hình<br>Phong Cách.</h1>
            </div>
        </div>
    </section>

    <!-- Ưu đãi sâu nhất -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Ưu đãi sâu nhất</h2>
            <a href="${pageContext.request.contextPath}/product-list?sort_by=discount">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${topDiscountedProducts}">
                <%@ include file="/public/product/product-card.jsp" %>
            </c:forEach>
        </div>
    </section>

    <!-- Sản phẩm mới -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Sản phẩm mới</h2>
            <a href="${pageContext.request.contextPath}/product-list?sort_by=lastest">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${latestProducts}">
                <%@ include file="/public/product/product-card.jsp" %>
            </c:forEach>
        </div>
    </section>

    <!-- Sản phẩm bán chạy -->
    <section class="product-section container">
        <div class="section-header">
            <h2>Sản phẩm bán chạy</h2>
            <a href="${pageContext.request.contextPath}/product-list?sort_by=best_seller">Xem tất cả</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach var="product" begin="0" end="3" items="${bestSellingProducts}">
                <%@ include file="/public/product/product-card.jsp" %>
            </c:forEach>
        </div>
    </section>

</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
