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
    <title>MODA - Chi tiết sản phẩm</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/home.css"> <!-- Tái sử dụng grid card -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/product-detail.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/public/header.jsp" />

<main class="container">
    <div class="breadcrumb">
        <c:choose>
            <c:when test="${not empty param.returnUrl}">
                <%-- Nếu tồn tại param returnUrl từ trang trước truyền sang -> Dùng luôn để load mới trang --%>
                <a href="${param.returnUrl}" class="text-dark text-decoration-none">
                    <i class="fa-solid fa-chevron-left"></i> QUAY LẠI
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/product-list" class="text-dark text-decoration-none">
                    <i class="fa-solid fa-chevron-left"></i> QUAY LẠI
                </a>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Product Detail Area -->
    <div class="row mt-4">

        <!-- Ảnh chính phụ -->
        <div class="product-gallery col-lg-6 mb-4">
            <div class="product-gallery__main">
                <c:forEach items="${productDetail.urlImageDetails}" var="image">
                    <c:if test="${image.isPrimary}">
                        <img id="zoom-image" src="${image.imageUrl}" alt="Image main">
                    </c:if>
                </c:forEach>
            </div>

            <div class="product-gallery__thumbs">
                <c:forEach items="${productDetail.urlImageDetails}" var="image">
                    <div class="product-gallery__thumb ${image.isPrimary ? 'active' : ''}">
                        <img src="${image.imageUrl}" alt="Image detail" onclick="changeImage(this)">
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- THÔNG tin product -->
        <div class="product-info col-lg-6">
            <h1 class="product-info__title">${productDetail.productName}</h1>

            <!-- Product Stats -->
            <div class="product-info__stats" style="display: flex; align-items: center; margin-bottom: 15px; font-size: 14px;">
                <a href="view-reviews.jsp" class="product-info__reviews-link" style="text-decoration: none; color: inherit; display: flex; align-items: center;">
                    <span style="font-weight: 600; margin-right: 5px; border-bottom: 1px solid #000; padding-bottom: 1px;">${productDetail.averageRating}</span>
                    <span style="color: #f5c518; margin-right: 8px; font-size: 12px;">
              <i class="fa-solid fa-star"></i>
            </span>
                    <span style="color: #666;">(${productDetail.totalReview} Đánh giá)</span>
                </a>
                <span style="color: #ccc; margin: 0 10px;">|</span>
                <span style="color: #666;">${productDetail.totalSold} Đã bán</span>
            </div>

            <div class="product-info__price-wrap">
                <span class="product-info__price"><fmt:formatNumber value="${productDetail.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                <span class="product-info__price-old"><fmt:formatNumber value="${productDetail.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                <span class="product-info__badge">${productDetail.discountPercentage}%</span>
            </div>

<%--            <div class="product-info__supplier">--%>
<%--                <span class="supplier-label">Cung cấp bởi:</span>--%>
<%--                <a href="${pageContext.request.contextPath}/shop?shop_id=${productDetail.shopId}" class="supplier-link text-dark text-decoration-none"><img src="${productDetail.logoUrl}" alt="Logo shop ${productDetail.shopName}" class="supplier-avatar"><strong>${productDetail.shopName}</strong></a>--%>
<%--            </div>--%>
            <%-- Bước 1: Đóng gói URL hiện tại của sản phẩm đang xem --%>
            <c:url var="currentProductUrl" value="product-detail">
                <c:param name="pid" value="${productDetail.productId}" />
                <c:param name="returnUrl" value="${param.returnUrl}" />
            </c:url>

            <%-- Bước 2: Ném cái URL an toàn ở trên sang cho trang Shop giữ hộ --%>
            <c:url var="goToShopUrl" value="shop">
                <c:param name="shop_id" value="${productDetail.shopId}" />
                <c:param name="returnUrl" value="${currentProductUrl}" />
            </c:url>

            <%-- Bước 3: Hiển thị giao diện và đổi href thành biến goToShopUrl --%>
            <div class="product-info__supplier">
                <span class="supplier-label">Cung cấp bởi:</span>
                <a href="${goToShopUrl}" class="supplier-link text-dark text-decoration-none">
                    <img src="${productDetail.logoUrl}" alt="Logo shop ${productDetail.shopName}" class="supplier-avatar">
                    <strong>${productDetail.shopName}</strong>
                </a>
            </div>

            <%--Hiển thị số lượng--%>
            <div class="product-stock-status mt-2">
                <span id="stock-display" class="text-muted"></span>
            </div>
            <!-- Hien thi danh sach mau sac cua san pham do -->
            <div class="product-attr color">
                <div class="product-attr__title">MÀU SẮC:</div>
                <div class="size-options">
                    <c:forEach items="${productDetail.colors}" var="color" varStatus="loop">
                        <button type="button"
                                class="attr-btn size-btn color-list ${loop.first ? 'active' : ''}"
                                data-color-id="${color.colorId}"
                                onclick="selectColor(this)">
                                ${color.colorName}
                        </button>
                    </c:forEach>
                </div>
            </div>
            <div id="data-helper" hidden
                 data-context-path="${pageContext.request.contextPath}"
                 data-check-seller="${checkProductSeller}"
            >
            </div>
            <!-- Hien thi kich co cua san pham -->
            <div class="product-attr">
                <div class="product-attr__title">
                    KÍCH THƯỚC
                    <a href="#"></a>
                </div>
                <div class="size-options">
                    <c:forEach items="${productDetail.sizes}" var="size" varStatus="loop">
                        <button type="button"
                                class="attr-btn size-btn size-list ${loop.first ? 'active' : ''}"
                                data-size-id="${size.sizeId}"
                                onclick="selectSize(this)">
                                ${size.sizeName}
                        </button>
                    </c:forEach>
                </div>
            </div>

            <%--Chọn số lượng, màu, cỡ, id sản phẩm--%>
    <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" class="add-to-cart-form">
        <input type="hidden" id="hidden-color-id" name="colorId" value="${productDetail.colors[0].colorId}">
        <input type="hidden" id="hidden-size-id" name="sizeId" value="${productDetail.sizes[0].sizeId}">
        <input type="hidden" id="hidden-product-id" name="productId" value="${productDetail.productId}">

        <div class="quantity-input mb-3">
            <label>Số lượng:</label>
            <input type="number" name="quantity" id="hidden-quantity" value="1" min="1"  oninput="if(this.value < 1) this.value = 1;" class="form-control" style="width: 80px;">
        </div>

            <!-- Actions -->
            <div class="product-actions">
                <button class="moda-btn moda-btn-primary" type="button" onclick="cart()" id="add-to-cart">THÊM VÀO GIỎ HÀNG</button>
                <button class="moda-btn moda-btn-outline" type="submit" id="add-order">MUA NGAY</button>
            </div>
    </form>
            <span id="product-seller" class="text-danger fw-bold"></span>
            <span id="cart-over-quantity" class="text-danger fw-bold"></span>
            <span id="add-to-cart-success" class="text-success fw-bold"></span>
            <!-- Description -->
            <div class="product-desc">
                <h3 class="product-desc__title">MÔ TẢ SẢN PHẨM</h3>
                <div class="product-desc__content">
                    <p>${productDetail.description}</p>
                </div>
            </div>

        </div>
    </div>

    <!-- Related Products -->
    <section class="related-section">
        <div class="section-header">
            <h2>CÓ THỂ BẠN CŨNG THÍCH</h2>
<%--            <a href="list-products.jsp">XEM TẤT CẢ</a>--%>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <c:forEach items="${productResponseList}" var="product">

                <c:url var="goToRelatedUrl" value="product-detail">
                    <c:param name="pid" value="${product.productId}" />
                    <%-- Điều hướng nút quay lại chỉ thẳng vào servlet product-detail kèm data chuẩn --%>
                    <c:param name="returnUrl" value="product-detail?pid=${productDetail.productId}&returnUrl=${param.returnUrl}" />
                </c:url>

                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="${goToRelatedUrl}" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">${product.discountPercentage}%</span>
                            <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                        </div>
                    </a>

                    <button class="product-card__favorite ${product.liked == true ? 'active' : ''}" id="wishlist-heart-${product.productId}" onclick="toggleWishlist(${product.productId}, '${pageContext.request.contextPath}')">
                        <i class="fa-regular fa-heart"></i>
                    </button>

                    <div class="product-card__info">
                        <div class="product-card__brand">
                            <span>${product.shopName}</span>
                            <span class="location"><i class="fa-solid fa-location-dot"></i>${product.provinceName}</span>
                        </div>

                        <a href="${goToRelatedUrl}" style="color:inherit; text-decoration:none;">
                            <h3 class="product-card__title">${product.productName}</h3>
                        </a>

                        <div class="product-card__price">
                            <span class="product-card__price-current"><fmt:formatNumber value="${product.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                            <span class="product-card__price-old"><fmt:formatNumber value="${product.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                            <span class="product-card__quantity">Số lượng: ${product.totalStock}</span>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </section>
</main>

<jsp:include page="/public/footer.jsp" />


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/wishlist.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/product-detail.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/cart.js"></script>
</body>
</html>



