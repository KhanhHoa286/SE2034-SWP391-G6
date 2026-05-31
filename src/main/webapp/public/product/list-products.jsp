<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Danh sách sản phẩm</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/products.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/public/header.jsp" />

<main class="container">

    <!-- Header của trang danh sách -->
    <div class="page-header">
        <h1 class="page-title" style="margin-bottom: 0;">TẤT CẢ SẢN PHẨM</h1>

        <div style="display:flex; justify-content: flex-end;">
            <div class="sort-wrapper">
                <span>Sắp xếp theo:</span>
                <select class="sort-select">
                    <option value="newest">Mới nhất</option>
                    <option value="price-asc">Giá: Thấp đến Cao</option>
                    <option value="price-desc">Giá: Cao đến Thấp</option>
                </select>
            </div>
        </div>
    </div>

    <!-- Cấu trúc Layout -->
    <div class="row mt-4">

        <!-- Sidebar / Filters -->
        <aside class="sidebar col-lg-3 mb-4">
            <!-- Danh mục -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Danh mục</h3>
                <select class="form-select mb-3">
                    <option value="">Tất cả danh mục</option>
                    <option value="ao-thun">Áo thun</option>
                    <option value="quan-jean">Quần Jean</option>
                    <option value="ao-khoac">Áo Khoác</option>
                    <option value="phu-kien">Phụ kiện</option>
                </select>
            </div>

            <!-- Mức giá -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Mức giá</h3>
                <div class="price-range-inputs">
                    <input type="text" placeholder="Từ" class="price-input">
                    <span>-</span>
                    <input type="text" placeholder="Đến" class="price-input">
                </div>
                <button class="apply-btn">Áp dụng</button>
            </div>

            <!-- Đánh giá -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Đánh giá</h3>
                <select class="form-select mb-3">
                    <option value="">Tất cả đánh giá</option>
                    <option value="5">5 sao</option>
                    <option value="4">Từ 4 sao</option>
                    <option value="3">Từ 3 sao</option>
                </select>
            </div>

            <!-- Tỉnh thành -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Tỉnh thành</h3>
                <select class="form-select mb-3">
                    <option value="">Tất cả tỉnh thành</option>
                    <option value="hanoi">Hà Nội</option>
                    <option value="hcm">TP. Hồ Chí Minh</option>
                    <option value="danang">Đà Nẵng</option>
                </select>
            </div>

            <!-- Khuyến mãi -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Khuyến mãi</h3>
                <select class="form-select mb-3">
                    <option value="">Tất cả khuyến mãi</option>
                    <option value="under-10">Dưới 10%</option>
                    <option value="10-25">10 - 25%</option>
                    <option value="25-35">25 - 35%</option>
                    <option value="under-45">Dưới 45%</option>
                </select>
            </div>
        </aside>

        <!-- Danh sách sản phẩm -->
        <div class="products-list col-lg-9">
            <div class="row g-4">

                <!-- Product Item 1 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-10%</span>
                            <img src="https://images.unsplash.com/photo-1596755094514-f87e32f6b717?q=80&w=600&auto=format&fit=crop" alt="Sơ mi" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA ARCHIVE</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Sơ Mi Oversize Minimalist Cotton Trắng</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">1.250.000đ</span>
                            <span class="product-card__price-old">1.380.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 2 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-15%</span>
                            <img src="https://images.unsplash.com/photo-1512436991641-6745cdb1723f?q=80&w=600&auto=format&fit=crop" alt="Quần Tây" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>RAW ESSENTIALS</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Quần Tây Slim-fit Kaki Charcoal Edition</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">890.000đ</span>
                            <span class="product-card__price-old">1.050.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 3 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-5%</span>
                            <img src="https://images.unsplash.com/photo-1584917865442-de89df76afd3?q=80&w=600&auto=format&fit=crop" alt="Túi Xách" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA ARCHIVE</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Túi Tote Canvas Signature Black Monogram</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">550.000đ</span>
                            <span class="product-card__price-old">580.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 4 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-20%</span>
                            <img src="https://images.unsplash.com/photo-1549298916-b41d501d3772?q=80&w=600&auto=format&fit=crop" alt="Sneaker" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>STUDIO VELOCE</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Sneaker Minimalist White Premium Leather</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">2.400.000đ</span>
                            <span class="product-card__price-old">3.000.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 5 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-10%</span>
                            <img src="https://images.unsplash.com/photo-1551028719-00167b16eac5?q=80&w=600&auto=format&fit=crop" alt="Áo Khoác" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA LIMITED</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Áo Khoác Metallic Runway</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">4.200.000đ</span>
                            <span class="product-card__price-old">4.660.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 6 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-15%</span>
                            <img src="https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=600&auto=format&fit=crop" alt="Kính Mắt" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA ACCESSORIES</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Kính Mắt Geometric Frame</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">1.200.000đ</span>
                            <span class="product-card__price-old">1.410.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 7 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-12%</span>
                            <img src="https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?q=80&w=600&auto=format&fit=crop" alt="Váy" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA STUDIO</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Váy Silk Slip Dress</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">1.450.000đ</span>
                            <span class="product-card__price-old">1.640.000đ</span>
                        </div>
                    </div>
                </article>

                <!-- Product Item 8 -->
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="view-product.jsp" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <span class="product-card__badge">-20%</span>
                            <img src="https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=600&auto=format&fit=crop" alt="Áo khoác Wool" class="product-card__img">
                        </div>
                    </a>
                    <div class="product-card__info">
                        <div class="product-card__brand"><span>MODA STUDIO</span> <span class="location"><i class="fa-solid fa-location-dot"></i> Hà Nội</span></div>
                        <a href="view-product.jsp" style="color:inherit; text-decoration:none;"><h3 class="product-card__title">Áo Khoác Wool Structured</h3></a>
                        <div class="product-card__price">
                            <span class="product-card__price-current">2.450.000đ</span>
                            <span class="product-card__price-old">3.060.000đ</span>
                        </div>
                    </div>
                </article>

            </div>

            <!-- Pagination -->
            <div class="moda-pagination">
                <a href="#" class="moda-page-link"><i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC</a>
                <a href="#" class="moda-page-num active">1</a>
                <a href="#" class="moda-page-num">2</a>
                <a href="#" class="moda-page-num">3</a>
                <a href="#" class="moda-page-link">SAU &nbsp; <i class="fa-solid fa-chevron-right"></i></a>
            </div>

        </div>
    </div>
</main>

<jsp:include page="/public/footer.jsp" />


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



