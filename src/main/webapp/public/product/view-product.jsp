<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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

    <!-- Breadcrumb -->
    <div class="breadcrumb">
        <a href="list-products.jsp" class="text-dark text-decoration-none"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI</a>
    </div>

    <!-- Product Detail Area -->
    <div class="row mt-4">

        <!-- Left: Image Gallery -->
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

        <!-- Right: Product Info -->
        <div class="product-info col-lg-6">
<%--            <div class="product-info__brand">ARCHIVE COLLECTION</div>--%>
            <h1 class="product-info__title">${productDetail.productName}</h1>

            <!-- Product Stats -->
            <div class="product-info__stats" style="display: flex; align-items: center; margin-bottom: 15px; font-size: 14px;">
                <a href="view-reviews.jsp" class="product-info__reviews-link" style="text-decoration: none; color: inherit; display: flex; align-items: center;">
                    <span style="font-weight: 600; margin-right: 5px; border-bottom: 1px solid #000; padding-bottom: 1px;">${productDetail.averageRating}</span>
                    <span style="color: #f5c518; margin-right: 8px; font-size: 12px;">
              <i class="fa-solid fa-star"></i>
            </span>
                    <span style="color: #666;">(${productDetail.totalReview})</span>
                </a>
                <span style="color: #ccc; margin: 0 10px;">|</span>
                <span style="color: #666;">${productDetail.totalSold}</span>
            </div>

            <div class="product-info__price-wrap">
                <span class="product-info__price"><fmt:formatNumber value="${productDetail.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                <span class="product-info__price-old"><fmt:formatNumber value="${productDetail.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/></span>
                <span class="product-info__badge">${productDetail.discountPercentage}</span>
            </div>

            <div class="product-info__supplier">
                <span class="supplier-label">Cung cấp bởi:</span>
                <a href="view-shop.jsp" class="supplier-link text-dark text-decoration-none"><img src="" alt="Shop Avatar" class="supplier-avatar"><strong>MODA ARCHIVE</strong></a>
            </div>

            <!-- Color -->
            <div class="product-attr">
                <div class="product-attr__title">MÀU SẮC: <span style="font-weight:400; color:var(--text-muted); margin-left:5px;">ĐEN</span></div>
                <div class="size-options">
                    <button class="size-btn active">Đen</button>
                    <button class="size-btn">Đỏ</button>
                    <button class="size-btn">Xám</button>
                </div>
            </div>

            <!-- Size -->
            <div class="product-attr">
                <div class="product-attr__title">
                    KÍCH THƯỚC
                    <a href="#">BẢNG SIZE</a>
                </div>
                <div class="size-options">
                    <button class="size-btn">XS</button>
                    <button class="size-btn active">S</button>
                    <button class="size-btn">M</button>
                    <button class="size-btn">L</button>
                    <button class="size-btn" style="color:#ccc; border-color:#eee; cursor:not-allowed;">XL</button>
                </div>
            </div>

            <!-- Actions -->
            <div class="product-actions">
                <button class="moda-btn moda-btn-primary">THÊM VÀO GIỎ HÀNG</button>
                <button class="moda-btn moda-btn-outline">MUA NGAY</button>
            </div>

            <!-- Description -->
            <div class="product-desc">
                <h3 class="product-desc__title">MÔ TẢ SẢN PHẨM</h3>
                <div class="product-desc__content">
                    <p>Thiết kế áo khoác dạ Wool cao cấp từ bộ sưu tập Archive. Sản phẩm được chế tác với phom dáng kiến trúc, đường cắt tinh xảo mang lại vẻ ngoài lịch lãm và tối giản. Chất liệu 100% Wool Merino đảm bảo giữ ấm tuyệt đối trong khi vẫn giữ được sự nhẹ nhàng, thanh thoát.</p>
                    <ul>
                        <li>100% Merino Wool cao cấp</li>
                        <li>Lót lụa satin mềm mại</li>
                        <li>Khuy cài ẩn tinh tế</li>
                        <li>Sản xuất tại Việt Nam</li>
                    </ul>
                </div>
            </div>

        </div>
    </div>

    <!-- Related Products -->
    <section class="related-section">
        <div class="section-header">
            <h2>CÓ THỂ BẠN CŨNG THÍCH</h2>
            <a href="list-products.jsp">XEM TẤT CẢ</a>
        </div>

        <div class="row g-4">
            <!-- Product 1 -->
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">-10%</span>
                    <img src="https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?q=80&w=600&auto=format&fit=crop" alt="Quần Tây" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>MODA STUDIO</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">QUẦN TÂY PHOM SUÔNG ĐEN</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current">3.200.000 đ</span>
                        <span class="product-card__price-old">3.550.000 đ</span>
                        <span class="product-card__quantity">Số lượng: 20</span>
                    </div>
                </div>
            </article>

            <!-- Product 2 -->
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">-15%</span>
                    <img src="https://images.unsplash.com/photo-1617137968427-85924c800a22?q=80&w=600&auto=format&fit=crop" alt="Sơ Mi" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>MODA STUDIO</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">SƠ MI TRẮNG POPLIN CAO CẤP</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current">2.100.000 đ</span>
                        <span class="product-card__price-old">2.470.000 đ</span>
                        <span class="product-card__quantity">Số lượng: 45</span>
                    </div>
                </div>
            </article>

            <!-- Product 3 -->
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">-20%</span>
                    <img src="https://images.unsplash.com/photo-1550614000-4b95d466f272?q=80&w=600&auto=format&fit=crop" alt="Áo Len" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>MODA ARCHIVE</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hồ Chí Minh</span></div>
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">ÁO LEN CASHMERE CỔ LỌ</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current">5.800.000 đ</span>
                        <span class="product-card__price-old">7.250.000 đ</span>
                        <span class="product-card__quantity">Số lượng: 12</span>
                    </div>
                </div>
            </article>

            <!-- Product 4 -->
            <article class="product-card col-6 col-md-4 col-lg-3">
                <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><div class="product-card__img-wrapper">
                    <span class="product-card__badge">-5%</span>
                    <img src="https://images.unsplash.com/photo-1584917865442-de89df76afd3?q=80&w=600&auto=format&fit=crop" alt="Túi Da" class="product-card__img">
                </div></a>
                <div class="product-card__info">
                    <div class="product-card__brand"><span>MODA ACCESSORIES</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Đà Nẵng</span></div>
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">TÚI DA CẦM TAY STRUCTURE</h3></a>
                    <div class="product-card__price">
                        <span class="product-card__price-current">8.400.000 đ</span>
                        <span class="product-card__price-old">8.840.000 đ</span>
                        <span class="product-card__quantity">Số lượng: 8</span>
                    </div>
                </div>
            </article>
        </div>
    </section>

</main>

<jsp:include page="/public/footer.jsp" />


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



