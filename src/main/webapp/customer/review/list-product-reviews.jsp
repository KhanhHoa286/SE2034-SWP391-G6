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
    <title>MODA - Đánh giá từ khách hàng</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/product-detail.css"> <!-- for breadcrumb and moda-btn -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/reviews.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/products.css"> <!-- for pagination -->

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp" />

<main class="container">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
        <a href="javascript:history.back()" class="text-dark text-decoration-none"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI</a>
    </div>

    <!-- Page Header -->
    <div class="reviews-header">
        <div class="reviews-header__info">
            <h1 class="reviews-title">ĐÁNH GIÁ TỪ KHÁCH HÀNG</h1>
            <h2 class="reviews-product-name">${productReview.productName}</h2>
            <div class="reviews-product-price"><fmt:formatNumber value="${productReview.price}" type="currency" maxFractionDigits="0"></fmt:formatNumber> </div>
        </div>
        <div class="reviews-header__rating" style="display: none;">
        </div>
    </div>

    <hr class="reviews-divider">

    <div class="row mt-5">
        <!-- Left: Rating Stats & Button -->
        <div class="col-lg-3 mb-5 text-center">
            <div class="reviews-header__rating mb-4 d-flex flex-column align-items-center" style="gap: 10px;">
                <div class="rating-score" style="font-size: 4rem; font-weight: 700; line-height: 1;">${productReview.averageRating} <i class="fa-solid fa-star"></i>
                </div>
<%--                <div class="rating-stars" style="color: #000; font-size: 1.5rem;">--%>
<%--                    <i class="fa-solid fa-star"></i>--%>
<%--&lt;%&ndash;                    <i class="fa-solid fa-star"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <i class="fa-solid fa-star"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <i class="fa-solid fa-star"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <i class="fa-solid fa-star"></i>&ndash;%&gt;--%>
<%--                </div>--%>
                <div class="rating-count" style="color: #666; font-size: 1rem;">(${productReview.totalReview} đánh giá)</div>
            </div>

<%--            <button class="moda-btn moda-btn-primary w-100 mt-2">VIẾT ĐÁNH GIÁ</button>--%>
        </div>

        <!-- Right: Reviews List -->
        <div class="col-lg-9 pl-lg-5">

            <!-- Filters -->
            <div class="reviews-filters d-flex justify-content-between align-items-center flex-wrap gap-3 mb-5">
                <div class="filter-buttons d-flex gap-2 flex-wrap">
                    <a href="${pageContext.request.contextPath}/product-review?pid=${productReview.productId}&page=1"
                       class="filter-btn text-decoration-none ${empty param.star ? 'active' : ''}">TẤT CẢ</a>

                    <c:forEach begin="1" end="5" var="starNum">
                        <c:set var="currentStar" value="${6 - starNum}"/>
                        <a href="${pageContext.request.contextPath}/product-review?pid=${productReview.productId}&page=1&star=${currentStar}"
                           class="filter-btn text-decoration-none ${param.star == currentStar ? 'active' : ''}">
                                ${currentStar} <i class="fa-solid fa-star"></i>
                        </a>
                    </c:forEach>
                </div>
<%--                <div class="filter-toggle form-check form-switch d-flex align-items-center gap-2">--%>
<%--                    <label class="form-check-label text-uppercase" for="imageToggle" style="font-size: 0.8rem; font-weight: 600; color: var(--text-muted);">CÓ HÌNH ẢNH/VID</label>--%>
<%--                    <input class="form-check-input" type="checkbox" role="switch" id="imageToggle" style="margin-top:0;">--%>
<%--                </div>--%>
            </div>

            <!-- Review Items -->
            <div class="review-list">
                <c:forEach items="${productReview.reviewResponse}" var="review">
                <!-- Item 1 -->
                <div class="review-item">
                    <div class="row">
                        <div class="col-md-3 review-meta">
                            <div class="d-flex align-items-center mb-2">
                                <img src="${review.avatarUrl}" class="rounded-circle" style="width: 40px; height: 40px; margin-right: 10px;" alt="Avatar">
                                <div class="reviewer-name mb-0">${review.userName}</div>
                            </div>
                            <div class="review-date"><fmt:formatDate value="${review.createdAt}" pattern="dd-MM-yyyy"></fmt:formatDate></div>
                        </div>
                        <div class="col-md-9 review-content">
                            <div class="review-stars" style="font-weight: 600; font-size: 1.1rem; color: #000; margin-bottom: 10px;">
                                ${review.rating} <i class="fa-solid fa-star"></i>
                            </div>
                            <h4 class="review-title">${review.reviewTitle}</h4>
                            <p class="review-text">${review.comment}</p>
                        </div>
                    </div>
                </div>
           </c:forEach>
                <c:if test="${empty productReview.reviewResponse}">
                    <p class="text-muted text-center my-5">Chưa có đánh giá nào!</p>
                </c:if>
            </div>

            <!-- Pagination -->
            <c:if test="${productReview.totalPage > 1}">
                <div class="moda-pagination mt-5 mb-5">
                    <c:if test="${productReview.pageNumber > 1}">
                        <a href="${pageContext.request.contextPath}/product-review?pid=${productReview.productId}&page=${productReview.pageNumber - 1}${not empty param.star ? '&star='.concat(param.star) : ''}" class="moda-page-link">
                            <i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC
                        </a>
                    </c:if>

                    <c:forEach begin="1" end="${productReview.totalPage}" var="i">
                        <a href="${pageContext.request.contextPath}/product-review?pid=${productReview.productId}&page=${i}${not empty param.star ? '&star='.concat(param.star) : ''}"
                           class="moda-page-num ${i == productReview.pageNumber ? 'active' : ''}">
                                ${i}
                        </a>
                    </c:forEach>

                    <c:if test="${productReview.pageNumber < productReview.totalPage}">
                        <a href="${pageContext.request.contextPath}/product-review?pid=${productReview.productId}&page=${productReview.pageNumber + 1}${not empty param.star ? '&star='.concat(param.star) : ''}" class="moda-page-link">
                            SAU &nbsp; <i class="fa-solid fa-chevron-right"></i>
                        </a>
                    </c:if>
                </div>
            </c:if>

        </div>
    </div>

</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
