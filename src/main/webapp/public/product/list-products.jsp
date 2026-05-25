<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html><html lang="vi"><head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<title>MODA ARCHIVE - Danh sách sản phẩm</title>
<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Google Fonts: Inter -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet">
<!-- Material Symbols -->
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<link href="/assets/css/customer/list-products.css" rel="stylesheet">
</head>
<body>
<!-- Announcement Bar -->
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI
</div>
<!-- Navigation Bar -->
<header class="navbar-custom">
    <!-- Left: Logo -->
    <a class="brand-logo" href="#">MODA</a>
    <!-- Center: Links -->
    <nav class="nav-links-center d-none d-lg-flex">
        <a class="nav-link-custom" href="#">TRANG CHỦ</a>
        <a class="nav-link-custom" href="#">NỮ</a>
        <a class="nav-link-custom" href="#">NAM</a>
        <a class="nav-link-custom" href="#">PHỤ KIỆN</a>
        <a class="nav-link-custom sale-off" href="#">SALE OFF</a>
    </nav>
    <!-- Right: Utilities -->
    <div class="nav-utilities">
        <div class="utility-search d-none d-md-flex">
            <span class="material-symbols-outlined">search</span>
            <input placeholder="TÌM KIẾM..." type="text">
        </div>
        <button class="icon-btn"><span class="material-symbols-outlined">favorite</span></button>
        <button class="icon-btn">
            <span class="material-symbols-outlined">shopping_bag</span>
            <span class="cart-badge">3</span>
        </button>
        <button class="icon-btn"><span class="material-symbols-outlined">account_circle</span></button>
    </div>
</header>
<!-- Main Content -->
<main class="container-fluid px-5 py-5 mt-4">
    <div class="row">
        <!-- Sidebar Filters -->
        <aside class="col-lg-3 sidebar">
            <!-- Category Collapsible -->
            <div class="filter-section">
                <div class="accordion category-accordion" id="categoryFilter">
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button aria-expanded="true" class="accordion-button" data-bs-target="#collapseCategory" data-bs-toggle="collapse" type="button">
                                Category
                            </button>
                        </h2>
                        <div class="accordion-collapse collapse show" id="collapseCategory">
                            <div class="accordion-body">
                                <div class="d-flex flex-column">
                                    <label class="filter-item">
                                        <input class="filter-checkbox" type="checkbox">
                                        <span class="">Áo thun</span>
                                    </label>
                                    <label class="filter-item">
                                        <input class="filter-checkbox" type="checkbox">
                                        <span class="">Quần Jean</span>
                                    </label>
                                    <label class="filter-item">
                                        <input class="filter-checkbox" type="checkbox">
                                        <span class="">Áo Khoác</span>
                                    </label>
                                    <label class="filter-item">
                                        <input class="filter-checkbox" type="checkbox">
                                        <span class="">Phụ kiện</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Price Range -->
            <div class="filter-section">
                <h3 class="filter-title">Price Range</h3>
                <div class="row g-2 mb-3">
                    <div class="col-5">
                        <input class="price-input" placeholder="Từ" type="number">
                    </div>
                    <div class="col-2 d-flex align-items-center justify-content-center">
                        <span class="text-muted">—</span>
                    </div>
                    <div class="col-5">
                        <input class="price-input" placeholder="Đến" type="number">
                    </div>
                </div>
                <button class="btn-apply">ÁP DỤNG</button>
            </div>
            <!-- Rating Checkboxes -->
            <div class="filter-section">
                <h3 class="filter-title">Rating</h3>
                <div class="d-flex flex-column">
                    <label class="filter-item">
                        <input class="filter-checkbox" type="checkbox">
                        <div class="stars-container">
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                        </div>
                        <span class="">5 sao</span>
                    </label>
                    <label class="filter-item">
                        <input class="filter-checkbox" type="checkbox">
                        <div class="stars-container">
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined">star</span>
                        </div>
                        <span class="">4 sao trở lên</span>
                    </label>
                    <label class="filter-item">
                        <input class="filter-checkbox" type="checkbox">
                        <div class="stars-container">
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined fill-1">star</span>
                            <span class="material-symbols-outlined">star</span>
                            <span class="material-symbols-outlined">star</span>
                        </div>
                        <span class="">3 sao trở lên</span>
                    </label>
                </div>
            </div>
        </aside>
        <!-- Product Grid Content -->
        <div class="col-lg-9">
            <div class="grid-header">
                <p class="m-0 text-muted">Hiển thị 12 trên 48 sản phẩm</p>
                <div class="sort-select-container">
                    <span class="me-1">Sắp xếp theo:</span>
                    <select class="sort-select">
                        <option>Mới nhất</option>
                        <option>Giá tăng dần</option>
                        <option>Giá giảm dần</option>
                    </select>
                    <span class="material-symbols-outlined" style="font-size: 18px;">expand_more</span>
                </div>
            </div>
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-2 row-cols-xl-4 g-4">
                <!-- Product Card 1 -->
                <div class="col">
                    <div class="product-card">
                        <div class="product-image-wrapper">
                            <img alt="Product" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDVKbrTBn-DX1LWxEQtQWWFefsFLMWkRKXdftxcUFOJ3ISeuYwq8_uosNNUuXFXfbZPM8DOwG3bko9OBBiXLMdbugnyS0kwWgx4jMPv8q3X7z0LeNgZQfFP5NArnXAllAACZAaQige1sfMhCwsUn0AfCh-kMp0NGiSM_62QJdJztWTIyw06AM66xQ8COlr3BaSjkHRTT--IT4HfPwNWqWzjY6OGaA92iv07hx7XhukWTejUFPRoxoOEH29pmAw_VYiJ8CMLdeg3-_A">
                            <button class="quick-add-btn">QUICK ADD</button>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Áo Sơ Mi Oversize Minimalist Cotton Trắng</h4>
                            <p class="product-price">1.250.000đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 12px;">store</span>
                                MODA ARCHIVE
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Product Card 2 -->
                <div class="col">
                    <div class="product-card">
                        <div class="product-image-wrapper">
                            <img alt="Product" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDKX3i2_wqE4yP3cEvyrxq-ywaRENREIIC_xc2GX-EhdKzL5bdqqCjF2OmAoTZwQ3M3MY_DeenlBZJmvFJ5s5iotOavr_CWie522hdeOSKPwSUXe2fxoD17HU3msdoYi1mB1gO68aghtVFB3QbOgviD6Vq3LIMFnVCuQsug5SB-oFwt1P2smBUatCjvhbTvNrO2L-jYxLKyOanLcVzKLjatit4RpsCfG8TUW8OJHMiEStO74hdteNHRkc1Zw6o5b_G8zKv3hz4KCO0">
                            <button class="quick-add-btn">QUICK ADD</button>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Quần Tây Slim-fit Kaki Charcoal Edition</h4>
                            <p class="product-price">890.000đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 12px;">store</span>
                                RAW ESSENTIALS
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Product Card 3 -->
                <div class="col">
                    <div class="product-card">
                        <div class="product-image-wrapper">
                            <img alt="Product" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAZhZ7bNF6as3C6O9p5KXu471IRslMKf1xGfU8__pGJtwx6HIX_SJ6PdmUYLKXkCJmw09WrSnkk3evS9Ph5G_OJVfDpS7xYdQFPV4Y-NJt-LubCUm4l1VDVYDD97h3vCJgIP1-U8pT8QXm40UUUbmija3PxchH8Eb522fYfQhDq1gmbJAYkW4x7pdVNJ1MpSn1j9DybIxZWkx-o8nooyKofmzjbyjFvZyy29Hx52dn4rmcyu-niqcWAhd9rRssjqWhbc-1TGIKMyfs">
                            <button class="quick-add-btn">QUICK ADD</button>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Túi Tote Canvas Signature Black Monogram</h4>
                            <p class="product-price">550.000đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 12px;">store</span>
                                MODA ARCHIVE
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Product Card 4 -->
                <div class="col">
                    <div class="product-card">
                        <div class="product-image-wrapper">
                            <img alt="Product" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCoO6wSQCe4H7RLmh7prOQloicQr7JmPeNrcNmAGWaMlH_x2qBIgk51lvtvRbI6_8TQx-V0ly9zM1Lc2OnYXw_Vjsf9VlVtZ8XRqDJwYecMJGPKgjG8EyLm-GQTcKKPcI87H3RjXDsCm0HBVWpzQtk6eZ8ejPm6MsqsTsSEuQsDEhMkbbjjV6fns3C7c19Nm2EcbuiSmKw77YW3Wgf4rLknacCr1eIUnrUiM5R1SnhT-04X3bqsi4VHeMf-1hYR03BEMzN2hZ0hMEs">
                            <button class="quick-add-btn">QUICK ADD</button>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Sneaker Minimalist White Premium Leather</h4>
                            <p class="product-price">2.400.000đ</p>
                            <div class="product-vendor">
                                <span class="material-symbols-outlined" style="font-size: 12px;">store</span>
                                STUDIO VELOCE
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Placeholders -->
                <div class="col">
                    <div class="product-card opacity-75">
                        <div class="product-image-wrapper d-flex align-items-center justify-content-center">
                            <span class="material-symbols-outlined text-muted" style="font-size: 48px;">image</span>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Product Title Placeholder</h4>
                            <p class="product-price">XXX.XXXđ</p>
                            <div class="product-vendor">Vendor Name</div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="product-card opacity-75">
                        <div class="product-image-wrapper d-flex align-items-center justify-content-center">
                            <span class="material-symbols-outlined text-muted" style="font-size: 48px;">image</span>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Product Title Placeholder</h4>
                            <p class="product-price">XXX.XXXđ</p>
                            <div class="product-vendor">Vendor Name</div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="product-card opacity-75">
                        <div class="product-image-wrapper d-flex align-items-center justify-content-center">
                            <span class="material-symbols-outlined text-muted" style="font-size: 48px;">image</span>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Product Title Placeholder</h4>
                            <p class="product-price">XXX.XXXđ</p>
                            <div class="product-vendor">Vendor Name</div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="product-card opacity-75">
                        <div class="product-image-wrapper d-flex align-items-center justify-content-center">
                            <span class="material-symbols-outlined text-muted" style="font-size: 48px;">image</span>
                        </div>
                        <div class="product-info">
                            <h4 class="product-title">Product Title Placeholder</h4>
                            <p class="product-price">XXX.XXXđ</p>
                            <div class="product-vendor">Vendor Name</div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Pagination -->
            <div class="pagination-container justify-content-center">
                <a class="pagination-nav" href="#">
                    <span class="material-symbols-outlined">chevron_left</span> Previous
                </a>
                <div class="d-flex gap-2">
                    <a class="page-btn active" href="#">1</a>
                    <a class="page-btn" href="#">2</a>
                    <a class="page-btn" href="#">3</a>
                </div>
                <a class="pagination-nav" href="#">
                    Next <span class="material-symbols-outlined">chevron_right</span>
                </a>
            </div>
        </div>
    </div>
</main>
<!-- Footer -->
<footer class="footer container-fluid">
    <div class="row align-items-center">
        <div class="col-md-6 mb-4 mb-md-0">
            <div class="footer-brand">MODA</div>
            <p class="footer-copy mb-0">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
        </div>
        <div class="col-md-6 d-flex justify-content-md-end">
            <div class="footer-links">
                <a class="footer-link" href="#">Privacy Policy</a>
                <a class="footer-link" href="#">Terms of Service</a>
                <a class="footer-link" href="#">Shipping</a>
                <a class="footer-link" href="#">Returns</a>
                <a class="footer-link" href="#">Contact</a>
            </div>
        </div>
    </div>
</footer>
<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>


</body></html>