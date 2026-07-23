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
    <title>MODA - Viết đánh giá</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/add-review.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=20260722-shop-toast">
</head>
<body>

<!-- Include Header -->
<jsp:include page="/common/header.jsp" />

<div class="profile-layout">

<jsp:include page="/common/customer-sidebar.jsp">
    <jsp:param name="active" value="orders" />
</jsp:include>

<!-- Main Content -->
<main class="review-container">
    <!-- Back button -->
    <a href="javascript:history.back()" class="review-back">
        <i class="fa-solid fa-chevron-left"></i> QUAY LẠI
    </a>

    <!-- Titles -->
    <h1 class="review-title">Viết đánh giá</h1>
    <p class="review-subtitle">Chia sẻ cảm nhận chân thực của bạn để giúp cộng đồng MODA mua sắm tốt hơn.</p>

    <!-- Product info -->
    <div class="review-product-card">
        <div class="review-product-img-wrapper">
            <img src="${reviewResponse.thumbnail}" alt="${reviewResponse.productName}" class="review-product-img">
        </div>
        <div class="review-product-details">
            <div class="review-product-label">Sản phẩm của bạn</div>
            <div class="review-product-name">${reviewResponse.productName}</div>
            <div class="review-product-price"><fmt:formatNumber value="${reviewResponse.discountedPrice}" type="currency" maxFractionDigits="0"></fmt:formatNumber></div>
        </div>
    </div>

    <hr class="review-divider">

    <!-- Review Form -->
    <form action="${pageContext.request.contextPath}/customer/add-product-review" method="POST" class="review-form">

        <input type="hidden" value="${reviewResponse.productId}" name="product_id">
        <input type="hidden" value="${reviewResponse.orderItemId}" name="order_item_id">
        <input type="hidden" value="${reviewResponse.subOrderId}" name="sub_order_id">
        <!-- Rating -->
        <div class="review-form-group">
            <label class="review-label">Mức độ hài lòng</label>
            <div class="review-rating-group">
                <c:forEach begin="1" end="5" var="star">
                <label class="review-rating-item">
                    <input type="radio" name="rating" value="${star}" ${star == oldRating ? 'checked' : ''}>
                    <span>${star}<i class="fa-solid fa-star"></i></span>
                </label>
                </c:forEach>
            </div>
            <c:if test="${not empty error.rating}">
                <span style="color:red">${error.rating}</span>
            </c:if>
        </div>

        <!-- Review Title -->
        <div class="review-form-group">
            <label class="review-label">Tiêu đề đánh giá</label>
            <input type="text" class="review-input" placeholder="Ví dụ: Chất lượng tuyệt vời" name="title_review" value="${oldTitle}">
            <c:if test="${not empty error.titleReview}">
                <span style="color:red">${error.titleReview}</span>
            </c:if>
        </div>

        <!-- Review Content -->
        <div class="review-form-group">
            <label class="review-label">Chia sẻ trải nghiệm</label>
            <textarea class="review-textarea" name="comment" placeholder="Hãy chia sẻ thêm về chất liệu, kích cỡ hoặc cảm giác khi mặc sản phẩm này..." >${oldComment}</textarea>
            <c:if test="${not empty error.comment}">
                <span style="color:red">${error.comment}</span>
            </c:if>
        </div>

        <!-- Media Upload -->
<%--        <div class="review-form-group">--%>
<%--            <label class="review-label">Hình ảnh/Video thực tế</label>--%>
<%--            <div class="review-media-group">--%>
<%--                <label class="review-upload-btn">--%>
<%--                    <i class="fa-solid fa-camera"></i>--%>
<%--                    <span>Thêm ảnh</span>--%>
<%--                    <input type="file" class="review-upload-input" accept="image/*,video/mp4" multiple>--%>
<%--                </label>--%>
<%--                <img src="https://images.unsplash.com/photo-1551028719-00167b16eac5?q=80&w=200&auto=format&fit=crop" class="review-media-preview" alt="Preview Image">--%>
<%--            </div>--%>
<%--            <p class="review-media-helper">Tối đa 5 ảnh hoặc video. Định dạng: JPG, PNG, MP4.</p>--%>
<%--        </div>--%>

        <!-- Submit Button -->
        <div class="review-submit-wrapper">
            <button type="submit" class="review-submit-btn ${sessionScope.addSuccess != null ? 'disabled active' : ''}">Gửi đánh giá</button>
        </div>
        <c:if test="${sessionScope.addFail != null}">
        <div class="text-end">
            <span style="color:red;font-weight: bold" class="text-end">${addFail}</span>
        </div>
            <c:remove var="addFail" scope="session"></c:remove>
        </c:if>
        <c:if test="${sessionScope.addSuccess != null}">
        <div class="text-end">
            <span style="color:green;font-weight: bold" class="text-end">${addSuccess}</span>
        </div>
            <c:remove var="addSuccess" scope="session"></c:remove>
        </c:if>
    </form>
</main>
</div>
<!-- Include Footer -->
<%--<jsp:include page="footer.jsp" />--%>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    window.addEventListener("pageshow", function (event) {
        if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
            window.location.reload();
        }
    });
</script>
</body>
</html>
