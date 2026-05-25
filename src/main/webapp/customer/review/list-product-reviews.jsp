
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Đánh giá sản phẩm | MODA</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link href="/assets/css/customer/list-product-reviews.css" rel="stylesheet"/>
</head>
<body>
<!-- Announcement Bar -->
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH HÀNG MỚI
</div>
<!-- Navigation Bar -->
<header class="navbar-custom d-flex align-items-center sticky-top">
    <!-- Left: Logo -->
    <div class="flex-grow-1">
        <a class="brand-logo" href="#">MODA</a>
    </div>
    <!-- Center: Links -->
    <nav class="d-none d-lg-flex justify-content-center flex-grow-1 gap-4">
        <a class="nav-link-custom" href="#">TRANG CHỦ</a>
        <a class="nav-link-custom" href="#">NỮ</a>
        <a class="nav-link-custom" href="#">NAM</a>
        <a class="nav-link-custom" href="#">PHỤ KIỆN</a>
        <a class="nav-link-custom sale-off" href="#">SALE OFF</a>
    </nav>
    <!-- Right: Utilities -->
    <div class="d-flex align-items-center justify-content-end flex-grow-1 gap-4">
        <div class="search-container d-none d-md-block">
            <span class="material-symbols-outlined search-icon">search</span>
            <input class="search-input" placeholder="TÌM KIẾM..." type="text"/>
        </div>
        <button class="utility-btn"><span class="material-symbols-outlined">favorite</span></button>
        <button class="utility-btn">
            <span class="material-symbols-outlined">shopping_bag</span>
            <span class="cart-badge">3</span>
        </button>
        <button class="utility-btn"><span class="material-symbols-outlined" style="font-size: 24px;">account_circle</span></button>
    </div>
</header>
<main class="main-container">
    <!-- Page Header -->
    <a class="back-link" href="#">
        <span class="material-symbols-outlined" style="font-size: 18px; margin-right: 4px;">chevron_left</span>
        Quay lại
    </a>
    <h1 class="page-title">Đánh giá từ khách hàng</h1>
    <div class="row product-info-row align-items-end g-3">
        <div class="col-md-8">
            <h2 class="product-name">ÁO KHOÁC DA WOOL TỐI GIẢN</h2>
            <p class="product-price">12.500.000 đ</p>
        </div>
        <div class="col-md-4 text-md-end">
            <div class="d-flex align-items-center justify-content-md-end gap-2">
                <span class="rating-summary">4.8</span>
                <div class="d-flex" style="color: var(--primary-color);">
                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0.5;">star_half</span>
                </div>
                <span style="font-size: 12px; color: var(--secondary-color); font-weight: 600;">(128 đánh giá)</span>
            </div>
        </div>
    </div>
    <div class="row g-5">
        <!-- Sidebar: Review Summary -->
        <aside class="col-lg-3 col-md-4">
            <div class="sticky-top" style="top: 100px;">
                <h3 class="distribution-label">Phân bổ đánh giá</h3>
                <div class="rating-row">
                    <span style="width: 15px;">5</span>
                    <div class="bar-container"><div class="bar-fill" style="width: 85%;"></div></div>
                    <span style="width: 30px; text-align: right; color: var(--secondary-color);">109</span>
                </div>
                <div class="rating-row">
                    <span style="width: 15px;">4</span>
                    <div class="bar-container"><div class="bar-fill" style="width: 10%;"></div></div>
                    <span style="width: 30px; text-align: right; color: var(--secondary-color);">12</span>
                </div>
                <div class="rating-row">
                    <span style="width: 15px;">3</span>
                    <div class="bar-container"><div class="bar-fill" style="width: 3%;"></div></div>
                    <span style="width: 30px; text-align: right; color: var(--secondary-color);">4</span>
                </div>
                <div class="rating-row">
                    <span style="width: 15px;">2</span>
                    <div class="bar-container"><div class="bar-fill" style="width: 1%;"></div></div>
                    <span style="width: 30px; text-align: right; color: var(--secondary-color);">2</span>
                </div>
                <div class="rating-row">
                    <span style="width: 15px;">1</span>
                    <div class="bar-container"><div class="bar-fill" style="width: 1%;"></div></div>
                    <span style="width: 30px; text-align: right; color: var(--secondary-color);">1</span>
                </div>
                <button class="write-review-btn">Viết đánh giá</button>
            </div>
        </aside>
        <!-- Main Content: Review List -->
        <div class="col-lg-9 col-md-8">
            <!-- Filter Bar -->
            <div class="filter-tabs">
                <div class="d-flex overflow-auto pb-1">
                    <button class="tab-btn active">Tất cả</button>
                    <button class="tab-btn">5 Sao</button>
                    <button class="tab-btn">4 Sao</button>
                    <button class="tab-btn">3 Sao</button>
                    <button class="tab-btn">2 Sao</button>
                    <button class="tab-btn">1 Sao</button>
                </div>
                <div class="switch-container">
                    <span>Có hình ảnh/video</span>
                    <div class="form-check form-switch p-0 m-0">
                        <input class="form-check-input shadow-none cursor-pointer" role="switch" style="width: 36px; height: 18px;" type="checkbox"/>
                    </div>
                </div>
            </div>
            <!-- Review List -->
            <div class="review-list">
                <!-- Review Card 1 -->
                <article class="review-item">
                    <div class="row">
                        <div class="col-lg-3 col-md-4 mb-3">
                            <p class="reviewer-meta">Minh Hoang</p>
                            <p class="review-date">12 THÁNG 10, 2024</p>
                            <div class="verified-badge">
                                <span class="material-symbols-outlined" style="font-size: 16px;">verified</span>
                                ĐÃ MUA HÀNG
                            </div>
                        </div>
                        <div class="col-lg-9 col-md-8">
                            <div class="d-flex mb-2" style="color: var(--primary-color);">
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                            </div>
                            <h4 class="review-title">Chất lượng da tuyệt vời, vượt mong đợi</h4>
                            <p class="review-content">
                                Áo khoác rất nặng tay, da thuộc mịn và không có mùi khó chịu. Form dáng tối giản nhưng cực kỳ sang trọng. Đường may tỉ mỉ, sắc sảo. Rất đáng đầu tư cho một món đồ vượt thời gian.
                            </p>
                            <div class="review-images">
                                <img alt="Close-up of leather" class="review-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDwTAJYhy8QQdVG_tla_b81qMPlvOcHQQFPcioHtyht17CqoTZ5qtRduBL0Hmyru7f_pS8EZpQOlcWmhToyh2EFF73rFb6Ii1WTaCSEr9HGMP21Mlq47MemzDujHGa478rw_wTXZDfLNdshV63jZpaIOYkay5tRo5oJyRyQcxz56NbxZdVrGwAUY011WnK5n-1FFglx_daCwlnayPTf7JPL_am_tExRJ4qG_tD0fHbQaEchjtbO2bOCX7J_q1wd9ZoV3U0OzRPxAHw"/>
                                <img alt="Jacket on rack" class="review-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAVVz4Yrqfa7AeEAl5NQ4tY7JTjW8RClH0kCeKOBME6Ckfio2_OvE3zP_zJhK06Uje8-t_2CqUXSmwFOA_EwOVC8kcZX-S2qe1VAaqT4dU_rEiYfIZ3wvs60_B6oYXWn5wRHMq8wc3odnqRdRHP_BdlT_se4FNIPCmhBAby4K8rEaETg-iR9ywUX2a22JLantr1_NwaF3PmBi3E7lvTHPuu5d_2O5JmMYVu0u2K21mQTKNER3dHJ9KEo4wFZsVPIzsCmQbjGlayBkc"/>
                            </div>
                        </div>
                    </div>
                </article>
                <!-- Review Card 2 -->
                <article class="review-item">
                    <div class="row">
                        <div class="col-lg-3 col-md-4 mb-3">
                            <p class="reviewer-meta">Thanh Van</p>
                            <p class="review-date">05 THÁNG 10, 2024</p>
                            <div class="verified-badge">
                                <span class="material-symbols-outlined" style="font-size: 16px;">verified</span>
                                ĐÃ MUA HÀNG
                            </div>
                        </div>
                        <div class="col-lg-9 col-md-8">
                            <div class="d-flex mb-2" style="color: var(--primary-color);">
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">star</span>
                            </div>
                            <h4 class="review-title">Phù hợp với phong cách Minimalist</h4>
                            <p class="review-content">
                                Màu sắc áo rất đẹp, đúng như hình ảnh. Tuy nhiên phần tay áo hơi dài so với mình một chút, nhưng vẫn có thể chấp nhận được. Giao hàng nhanh và đóng gói rất chuyên nghiệp.
                            </p>
                        </div>
                    </div>
                </article>
                <!-- Review Card 3 -->
                <article class="review-item">
                    <div class="row">
                        <div class="col-lg-3 col-md-4 mb-3">
                            <p class="reviewer-meta">Tuan Anh</p>
                            <p class="review-date">28 THÁNG 09, 2024</p>
                            <div class="verified-badge">
                                <span class="material-symbols-outlined" style="font-size: 16px;">verified</span>
                                ĐÃ MUA HÀNG
                            </div>
                        </div>
                        <div class="col-lg-9 col-md-8">
                            <div class="d-flex mb-2" style="color: var(--primary-color);">
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                                <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">star</span>
                            </div>
                            <h4 class="review-title">Đẳng cấp khác biệt</h4>
                            <p class="review-content">
                                Đã tìm kiếm một chiếc áo khoác da thật sự chất lượng từ lâu và cuối cùng đã thấy ở MODA. Lớp lót wool bên trong rất ấm áp và dễ chịu. 10/10 cho trải nghiệm dịch vụ.
                            </p>
                            <div class="review-images">
                                <img alt="Flat lay of jacket" class="review-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBOVs91B4Ru304_wE7uJGdr9ZKVw3vD_qVPDeruCOjLebzRtFnFn2Jec6vEyTZ-6IKDVbabLhRisQgXKTVhxEtu_d_Be3NMcT6adGMm6DFmKghdJVGL8bBv78YRoQzw-JOVJyPp-OexBRFPRQknPr6VTfks_CxPJIVPfWdwaZhelB3If9KhGSDpirB0SL_zl11kLIsFKIo535u9KxhY8N2ymSlflDYo6MReN6ydRYUz-N1OHtR0v5VSXFIkewQe_BfPYDVy2f9HZ-g"/>
                            </div>
                        </div>
                    </div>
                </article>
            </div>
            <!-- Pagination -->
            <div class="pagination-custom">
                <a class="page-num" href="#"><span class="material-symbols-outlined">chevron_left</span></a>
                <a class="page-num active" href="#">1</a>
                <a class="page-num" href="#">2</a>
                <a class="page-num" href="#">3</a>
                <span style="padding: 0 8px;">...</span>
                <a class="page-num" href="#">12</a>
                <a class="page-num" href="#"><span class="material-symbols-outlined">chevron_right</span></a>
            </div>
        </div>
    </div>
</main>
<!-- Footer -->
<footer class="footer-custom">
    <div class="container-fluid p-0">
        <div class="row align-items-start g-4">
            <div class="col-lg-4">
                <div class="footer-brand">MODA</div>
                <p class="footer-text">Hệ thống lưu trữ thời trang cao cấp &amp; bền vững.</p>
                <p class="footer-copy">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
            </div>
            <div class="col-lg-4">
                <div class="row row-cols-2 g-3">
                    <div class="col"><a class="footer-link" href="#">Privacy Policy</a></div>
                    <div class="col"><a class="footer-link" href="#">Terms of Service</a></div>
                    <div class="col"><a class="footer-link" href="#">Shipping &amp; Returns</a></div>
                    <div class="col"><a class="footer-link" href="#">Sustainability</a></div>
                </div>
            </div>
            <div class="col-lg-4 text-lg-end">
                <a class="social-btn" href="#"><span class="material-symbols-outlined">language</span></a>
                <a class="social-btn" href="#"><span class="material-symbols-outlined">mail</span></a>
            </div>
        </div>
    </div>
</footer>
<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
