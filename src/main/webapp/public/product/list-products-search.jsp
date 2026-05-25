<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>MODA - Kết Quả Tìm Kiếm</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&amp;display=swap" rel="stylesheet"/>
    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="/assets/css/customer/list-products-search.css"/>
</head>
<body>
<!-- Announcement Bar -->
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI
</div>
<!-- Navbar -->
<nav class="navbar-custom">
    <div class="navbar-container">
        <!-- Brand -->
        <a class="brand-logo" href="#">MODA</a>
        <!-- Nav Links -->
        <div class="nav-links d-none d-md-flex">
            <a class="nav-link-item" href="#">TRANG CHỦ</a>
            <a class="nav-link-item" href="#">NỮ</a>
            <a class="nav-link-item" href="#">NAM</a>
            <a class="nav-link-item" href="#">PHỤ KIỆN</a>
            <a class="nav-link-item sale-off" href="#">SALE OFF</a>
        </div>
        <!-- Utilities -->
        <div class="nav-utilities">
            <div class="search-box">
                <span class="material-symbols-outlined search-icon">search</span>
                <input class="search-input" placeholder="TÌM KIẾM..." type="text" value="ÁO"/>
            </div>
            <button class="btn p-0 border-0">
                <span class="material-symbols-outlined">favorite_border</span>
            </button>
            <button class="btn p-0 border-0 position-relative">
                <span class="material-symbols-outlined">shopping_bag</span>
                <span class="cart-badge">2</span>
            </button>
            <button class="btn p-0 border-0">
                <span class="material-symbols-outlined">account_circle</span>
            </button>
        </div>
    </div>
</nav>
<main class="main-container">
    <!-- Page Title -->
    <header class="page-header">
        <h1 class="page-title">KẾT QUẢ TÌM KIẾM CHO: 'ÁO'</h1>
        <p class="results-count">8 sản phẩm được tìm thấy</p>
    </header>
    <div class="row gx-4">
        <!-- Sidebar Filters -->
        <aside class="col-12 col-md-3 sidebar">
            <!-- Gender -->
            <div class="filter-section">
                <h3 class="filter-title">GENDER</h3>
                <div class="filter-options">
                    <label class="filter-option">
                        <input type="checkbox"/>
                        <span>Nam</span>
                    </label>
                    <label class="filter-option">
                        <input type="checkbox"/>
                        <span>Nữ</span>
                    </label>
                </div>
            </div>
            <!-- Category -->
            <div class="filter-section">
                <details open="">
                    <summary class="d-flex justify-content-between align-items-center cursor-pointer mb-4">
                        <h3 class="filter-title mb-0">CATEGORY</h3>
                        <span class="material-symbols-outlined">expand_more</span>
                    </summary>
                    <div class="filter-options pt-2">
                        <label class="filter-option">
                            <input type="checkbox"/>
                            <span>Nữ</span>
                        </label>
                        <label class="filter-option">
                            <input checked="" type="checkbox"/>
                            <span>Sơ mi</span>
                        </label>
                        <label class="filter-option">
                            <input type="checkbox"/>
                            <span>Áo khoác</span>
                        </label>
                    </div>
                </details>
            </div>
            <!-- Price Range -->
            <div class="filter-section">
                <h3 class="filter-title">PRICE RANGE</h3>
                <div class="price-inputs">
                    <input class="price-input" placeholder="Từ" type="number"/>
                    <span class="text-secondary">—</span>
                    <input class="price-input" placeholder="Đến" type="number"/>
                </div>
                <button class="btn-apply">ÁP DỤNG</button>
            </div>
            <!-- Size -->
            <div class="filter-section">
                <h3 class="filter-title">SIZE</h3>
                <div class="size-grid">
                    <button class="size-btn">XS</button>
                    <button class="size-btn active">S</button>
                    <button class="size-btn">M</button>
                    <button class="size-btn">L</button>
                    <button class="size-btn">XL</button>
                </div>
            </div>
            <!-- Color -->
            <div class="filter-section border-0">
                <h3 class="filter-title">COLOR</h3>
                <div class="color-swatches">
                    <div class="color-swatch active" style="background-color: #000;"></div>
                    <div class="color-swatch" style="background-color: #9ca3af;"></div>
                    <div class="color-swatch" style="background-color: #fff;"></div>
                    <div class="color-swatch" style="background-color: #1e3a8a;"></div>
                </div>
            </div>
        </aside>
        <!-- Product Grid -->
        <section class="col-12 col-md-9 product-grid">
            <div class="row g-4">
                <!-- Product Item 1 -->
                <div class="col-12 col-sm-6 col-lg-4">
                    <a class="product-card" href="#">
                        <div class="product-image-container">
                            <img alt="Áo Khoác Wool Heritage" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBAmmfizEFqzsGBDtHRgNn9sMF0yxR8QoYfiOk3OmhcwC6hG6Nv-SZt-qLbmorOYoVsIZPQMxxMq9klw5DHBt2YO923wQBq5JDkiAwJh8aLfwfZ23-6mR2fhgV20LbnvVMq-wadveGbvPtEOIDzU8FtINC7FmtG2JqbkWI_oZ45zrtDOBMGJCZuCI9SPTfZvdWQR2SPjooHnJdzAq1mer4fmnNHbXUGvQuhRhGUiaNlXa_Oo3ArZSbMkT90uv2NCPoqnJN5UE4NPik"/>
                            <div class="quick-add">
                                <span class="text-uppercase fw-semibold" style="font-size: 12px; letter-spacing: 0.15em;">QUICK ADD</span>
                            </div>
                        </div>
                        <div class="product-info">
                            <h2 class="product-name">Áo Khoác Wool Heritage</h2>
                            <p class="product-price">3.550.000 đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 14px;">store</span>
                                MODA ARCHIVE
                            </div>
                        </div>
                    </a>
                </div>
                <!-- Product Item 2 -->
                <div class="col-12 col-sm-6 col-lg-4">
                    <a class="product-card" href="#">
                        <div class="product-image-container">
                            <img alt="Áo Sơ Mi Poplin Cotton" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDtn1TvHk09iND3gtFR6SxseUMUAmbRwauQJ81OK3vR2t6RDgaLujiLn6CFjg4WD8x1DIeOvo78d9MED6TtXRcUyAIamsBlm1KlJTkLOEZHoVpVqsZbJLhIbtsTmFIk3e-rY57bBgFWGkoMaTKndUQe1vVeMzPzIdACsWbsU6Y7LStwFFUVn2wRRI6tu21t18ukZ9T3oJ1ABOcagdirRj7x_FN07iflEa9pRO4l4KApGGFfzYZ5S8g1HX9Z8dQ1tcO_DjBRTeU2Jss"/>
                            <div class="quick-add">
                                <span class="text-uppercase fw-semibold" style="font-size: 12px; letter-spacing: 0.15em;">QUICK ADD</span>
                            </div>
                        </div>
                        <div class="product-info">
                            <h2 class="product-name">Áo Sơ Mi Poplin Cotton</h2>
                            <p class="product-price">1.250.000 đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 14px;">store</span>
                                MODA ARCHIVE
                            </div>
                        </div>
                    </a>
                </div>
                <!-- Product Item 3 -->
                <div class="col-12 col-sm-6 col-lg-4">
                    <a class="product-card" href="#">
                        <div class="product-image-container">
                            <img alt="Áo Thun Oversize Minimalist" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuC5R4ibjSdDfqHt0wp_ZlhFZnkIJv4PDw9hL5yl-_r2U4Rbac9dhQvuYKT-C_NKhzbH4XqfW-5D_pIBXHZFJmOn2lpJQBmEupplz-QKJK5YCgK50Mb1mbANrDTxn5dprKEJxxBgHZF0Kl1j0OY_RX2xIP3P7yMQDDpWH9NF-_c_ppTOKq6uxHdFJvLa-xtBAysZYbdC90e16fl0jAAH1emc27OauL39v7sPrZfgAWTnsEiHK8ZGaq19zb0aNkoEcEIThv3xQ6DTlT0"/>
                            <div class="quick-add">
                                <span class="text-uppercase fw-semibold" style="font-size: 12px; letter-spacing: 0.15em;">QUICK ADD</span>
                            </div>
                        </div>
                        <div class="product-info">
                            <h2 class="product-name">Áo Thun Oversize Minimalist</h2>
                            <p class="product-price">850.000 đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 14px;">store</span>
                                MODA ARCHIVE
                            </div>
                        </div>
                    </a>
                </div>
            </div>
            <!-- Pagination -->
            <nav class="pagination-container">
                <a class="pagination-link" href="#">
                    <span class="material-symbols-outlined">chevron_left</span> PREVIOUS
                </a>
                <div class="pagination-numbers">
                    <a class="page-num active" href="#">1</a>
                    <a class="page-num" href="#">2</a>
                    <a class="page-num" href="#">3</a>
                </div>
                <a class="pagination-link" href="#">
                    NEXT <span class="material-symbols-outlined">chevron_right</span>
                </a>
            </nav>
        </section>
    </div>
</main>
<!-- Footer -->
<footer class="site-footer">
    <div class="main-container">
        <div class="footer-content">
            <div class="footer-brand-section">
                <span class="footer-logo">MODA</span>
                <p class="footer-tagline">Định nghĩa lại sự sang trọng qua ngôn ngữ tối giản và chất lượng vượt trội.</p>
            </div>
            <div class="footer-links">
                <a class="footer-link" href="#">SHIPPING &amp; RETURNS</a>
                <a class="footer-link" href="#">PRIVACY POLICY</a>
                <a class="footer-link" href="#">TERMS OF SERVICE</a>
                <a class="footer-link" href="#">CONTACT</a>
            </div>
            <div class="footer-copyright">
                <p class="copyright">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
            </div>
        </div>
    </div>
</footer>
<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>