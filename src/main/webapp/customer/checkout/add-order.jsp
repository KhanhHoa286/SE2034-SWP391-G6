
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Lịch sử đơn hàng | MODA</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;900&amp;display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="/assets/css/customer/add-order.css">
</head>
<body>
<!-- Navigation Bar: Symmetrical 3-part layout -->
<header class="navbar-custom">
    <a class="brand-logo" href="#">MODA</a>
    <nav class="nav-links-center d-none d-lg-flex">
        <a class="nav-link-custom" href="#">TRANG CHỦ</a>
        <a class="nav-link-custom" href="#">NỮ</a>
        <a class="nav-link-custom" href="#">NAM</a>
        <a class="nav-link-custom" href="#">PHỤ KIỆN</a>
        <a class="nav-link-custom sale-off" href="#">SALE OFF</a>
    </nav>
    <div class="nav-utilities">
        <div class="utility-search d-none d-md-flex">
            <span class="material-symbols-outlined">search</span>
            <input placeholder="TÌM KIẾM..." type="text"/>
        </div>
        <button class="icon-btn"><span class="material-symbols-outlined">favorite</span></button>
        <button class="icon-btn">
            <span class="material-symbols-outlined">shopping_bag</span>
            <span class="cart-badge">3</span>
        </button>
        <button class="icon-btn"><span class="material-symbols-outlined">account_circle</span></button>
    </div>
</header>
<main style="padding-top: 120px;">
    <div class="container-xl">
        <!-- Page Header -->
        <div class="row align-items-end pb-4 mb-4 border-bottom border-dark">
            <div class="col-md-8">
                <h1 class="headline-xl mb-2">Lịch sử đơn hàng</h1>
                <p class="body-md text-muted mb-0">Theo dõi và quản lý các giao dịch của bạn tại MODA Archive.</p>
            </div>
            <div class="col-md-4 mt-4 mt-md-0 position-relative">
                <div class="d-flex align-items-center position-relative">
                    <input class="form-control search-input-border body-sm" placeholder="Tìm kiếm mã đơn hàng..." type="text"/>
                    <span class="material-symbols-outlined position-absolute end-0 bottom-0 mb-1 text-muted" style="font-size: 20px;">search</span>
                </div>
            </div>
        </div>
        <!-- Orders Table -->
        <div class="row d-none d-md-flex table-header label-md">
            <div class="col-2">Mã đơn hàng</div>
            <div class="col-2">Ngày đặt</div>
            <div class="col-2">Trạng thái</div>
            <div class="col-2 text-end">Tổng cộng</div>
            <div class="col-4 text-end">Thao tác</div>
        </div>
        <!-- Order Items -->
        <div class="order-list">
            <!-- Order 1 -->
            <div class="row order-row align-items-center">
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="label-md fw-bold">#MODA98210</span>
                </div>
                <div class="col-md-2 mb-2 mb-md-0 body-sm text-muted">12/10/2024</div>
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="status-badge status-delivering">Đang giao hàng</span>
                </div>
                <div class="col-md-2 mb-3 mb-md-0 text-md-end body-md fw-semibold">4.200.000₫</div>
                <div class="col-md-4 text-md-end d-flex justify-content-md-end align-items-center gap-4">
                    <a class="nav-link-custom text-dark text-decoration-underline" href="#" style="text-underline-offset: 4px;">Mua lại</a>
                    <button class="btn-primary-custom">Xem chi tiết</button>
                </div>
            </div>
            <!-- Order 2 -->
            <div class="row order-row align-items-center">
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="label-md fw-bold">#MODA97544</span>
                </div>
                <div class="col-md-2 mb-2 mb-md-0 body-sm text-muted">05/10/2024</div>
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="status-badge status-completed">Đã hoàn thành</span>
                </div>
                <div class="col-md-2 mb-3 mb-md-0 text-md-end body-md fw-semibold">1.850.000₫</div>
                <div class="col-md-4 text-md-end d-flex justify-content-md-end align-items-center gap-4">
                    <a class="nav-link-custom text-dark text-decoration-underline" href="#" style="text-underline-offset: 4px;">Mua lại</a>
                    <button class="btn-primary-custom">Xem chi tiết</button>
                </div>
            </div>
            <!-- Order 3 -->
            <div class="row order-row align-items-center">
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="label-md fw-bold">#MODA96123</span>
                </div>
                <div class="col-md-2 mb-2 mb-md-0 body-sm text-muted">28/09/2024</div>
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="status-badge status-completed">Đã hoàn thành</span>
                </div>
                <div class="col-md-2 mb-3 mb-md-0 text-md-end body-md fw-semibold">12.400.000₫</div>
                <div class="col-md-4 text-md-end d-flex justify-content-md-end align-items-center gap-4">
                    <a class="nav-link-custom text-dark text-decoration-underline" href="#" style="text-underline-offset: 4px;">Mua lại</a>
                    <button class="btn-primary-custom">Xem chi tiết</button>
                </div>
            </div>
            <!-- Order 4 -->
            <div class="row order-row align-items-center">
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="label-md fw-bold">#MODA95001</span>
                </div>
                <div class="col-md-2 mb-2 mb-md-0 body-sm text-muted">15/09/2024</div>
                <div class="col-md-2 mb-2 mb-md-0">
                    <span class="status-badge status-cancelled">Đã hủy</span>
                </div>
                <div class="col-md-2 mb-3 mb-md-0 text-md-end body-md fw-semibold">3.200.000₫</div>
                <div class="col-md-4 text-md-end d-flex justify-content-md-end align-items-center gap-4">
                    <a class="nav-link-custom text-dark text-decoration-underline" href="#" style="text-underline-offset: 4px;">Mua lại</a>
                    <button class="btn-primary-custom">Xem chi tiết</button>
                </div>
            </div>
        </div>
        <!-- Pagination -->
        <div class="pagination-container justify-content-center">
            <a class="pagination-nav" href="#">
                <span class="material-symbols-outlined">chevron_left</span> PREVIOUS
            </a>
            <div class="d-flex gap-2 mx-3">
                <a class="page-btn active" href="#">1</a>
                <a class="page-btn" href="#">2</a>
                <a class="page-btn" href="#">3</a>
            </div>
            <a class="pagination-nav" href="#">
                NEXT <span class="material-symbols-outlined">chevron_right</span>
            </a>
        </div>
        <!-- Banners Section -->
        <section class="mt-5 pt-4">
            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="position-relative h-100 overflow-hidden" style="min-height: 400px;">
                        <img alt="Fashion Editorial" class="w-100 h-100 object-fit-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuA8GoF63bqwWpgd8V1VykAibgqh942cZXSvcJTz8V6q0bnPUDKujPJ2HGWdCHqFHGteODD078xAU9YlFdg_h3faWkJA7MvbKhoJgdzt0fnkrS3YgZU0qXYQ_fsq_3eL04pVcCUF2t9W906t2xnBTmoLlmwouN93ThAS5IIUxLydzS01BQJoymUB8ooMH1FseYBL9pQFV1NAzoXQknL3uZsML3H6HHyC8ZpD5jkPB7F840xoxZxCFhiJkfqMitazMuSYAAb0tq9FC1A"/>
                        <div class="featured-banner-light-box shadow-sm">
                            <h3 class="headline-md mb-2">Bộ sưu tập Mùa Thu</h3>
                            <p class="body-sm text-muted mb-4">Khám phá những thiết kế mới nhất vừa được cập nhật trong kho lưu trữ của chúng tôi.</p>
                            <a class="label-md text-dark text-decoration-underline fw-bold" href="#" style="text-underline-offset: 8px;">KHÁM PHÁ NGAY</a>
                        </div>
                    </div>
                </div>
                <div class="col-lg-5">
                    <div class="featured-banner-dark h-100 d-flex flex-column justify-content-center">
                        <span class="label-sm text-uppercase opacity-75 mb-3">Ưu đãi độc quyền</span>
                        <h2 class="headline-lg mb-4">Chương trình Khách hàng Thân thiết MODA</h2>
                        <p class="body-md mb-5 opacity-75">Nhận thông báo sớm nhất về các đợt Archive Sale và tích lũy điểm thưởng cho mỗi đơn hàng thành công.</p>
                        <button class="btn-outline-custom border-white text-white align-self-start py-3 px-5">THAM GIA NGAY</button>
                    </div>
                </div>
            </div>
        </section>
    </div>
</main>
<!-- Footer -->
<footer class="mt-5 py-5 bg-white footer-border">
    <div class="container-xl">
        <div class="row g-4 mb-5">
            <div class="col-lg-3 col-md-6">
                <h2 class="headline-md mb-3">MODA</h2>
                <p class="body-sm text-muted">Nền tảng lưu trữ thời trang cao cấp với phong cách tối giản và kiến trúc hiện đại.</p>
            </div>
            <div class="col-lg-3 col-md-6">
                <h4 class="label-md mb-3">Hỗ trợ</h4>
                <ul class="list-unstyled d-flex flex-column gap-2">
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Privacy Policy</a></li>
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Terms of Service</a></li>
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Contact</a></li>
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Returns</a></li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h4 class="label-md mb-3">Theo dõi</h4>
                <ul class="list-unstyled d-flex flex-column gap-2">
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Instagram</a></li>
                    <li><a class="body-sm text-muted text-decoration-none" href="#">Pinterest</a></li>
                    <li><a class="body-sm text-muted text-decoration-none" href="#">TikTok</a></li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h4 class="label-md mb-3">Bản tin</h4>
                <div class="d-flex border-bottom border-dark pb-2">
                    <input class="form-control border-0 bg-transparent p-0 body-sm" placeholder="Email của bạn" type="email"/>
                    <button class="btn p-0 ms-2"><span class="material-symbols-outlined">arrow_forward</span></button>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <p class="body-sm text-muted mb-0">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
            </div>
        </div>
    </div>
</footer>
<!-- Bootstrap Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>