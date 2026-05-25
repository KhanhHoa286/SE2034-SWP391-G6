
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html><html lang="vi"><head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<title>Checkout - MODA</title>
<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Google Fonts: Inter -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet">
<!-- Material Symbols -->
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet">
<link rel="stylesheet" href="/assets/css/customer/list-orders.css">
</head>
<body>
<div class="promo-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI
</div>
<nav class="navbar-moda">
    <div class="container d-flex align-items-center justify-content-between"><div class="row w-100 align-items-center m-0">
        <!-- Left: Brand Logo -->
        <div class="col-3 d-flex align-items-center p-0">
            <a class="navbar-brand-moda" href="#">MODA</a>
        </div>

        <!-- Center: Navigation Links -->
        <div class="col-6 d-none d-md-flex justify-content-center align-items-center p-0">
            <a class="nav-link-moda" href="#">TRANG CHỦ</a>
            <a class="nav-link-moda" href="#">NỮ</a>
            <a class="nav-link-moda" href="#">NAM</a>
            <a class="nav-link-moda" href="#">PHỤ KIỆN</a>
            <a class="nav-link-moda nav-link-sale" href="#">SALE OFF</a>
        </div>

        <!-- Right: Search & Utilities -->
        <div class="col-3 d-flex justify-content-end align-items-center gap-3 p-0">
            <div class="search-container d-none d-sm-block">
                <span class="material-symbols-outlined search-icon">search</span>
                <input class="search-input" placeholder="TÌM KIẾM..." type="text">
            </div>
            <button class="btn p-0 border-0"><span class="material-symbols-outlined">favorite_border</span></button>
            <button class="btn p-0 border-0 position-relative">
                <span class="material-symbols-outlined">shopping_bag</span>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 8px; padding: 3px 5px;">2</span>
            </button>
            <button class="btn p-0 border-0"><span class="material-symbols-outlined">account_circle</span></button>
        </div>
    </div></div>
</nav>
<main class="container py-5 mb-5">
    <a class="back-link" href="#">
        <span class="material-symbols-outlined" style="font-size: 20px;">chevron_left</span> QUAY LẠI
    </a>
    <div class="row g-5">
        <!-- Left Column: Checkout Steps -->
        <div class="col-lg-7">
            <!-- 1. Thông tin giao hàng -->
            <section>
                <h2 class="step-title">1. Thông tin giao hàng</h2>
                <div class="row">
                    <div class="col-12 form-group-moda">
                        <label class="form-label-moda">HỌ VÀ TÊN</label>
                        <input class="form-input-moda" placeholder="Nhập tên đầy đủ của bạn" type="text">
                    </div>
                    <div class="col-md-6 form-group-moda">
                        <label class="form-label-moda">SỐ ĐIỆN THOẠI</label>
                        <input class="form-input-moda" placeholder="090 000 0000" type="tel">
                    </div>
                    <div class="col-md-6 form-group-moda">
                        <label class="form-label-moda">EMAIL</label>
                        <input class="form-input-moda" placeholder="example@email.com" type="email">
                    </div>
                    <div class="col-md-4 form-group-moda">
                        <label class="form-label-moda">TỈNH / THÀNH PHỐ</label>
                        <select class="form-input-moda">
                            <option>Hồ Chí Minh</option>
                            <option>Hà Nội</option>
                            <option>Đà Nẵng</option>
                        </select>
                    </div>
                    <div class="col-md-4 form-group-moda">
                        <label class="form-label-moda">QUẬN / HUYỆN</label>
                        <select class="form-input-moda">
                            <option>Quận 1</option>
                            <option>Quận 3</option>
                            <option>Quận 7</option>
                        </select>
                    </div>
                    <div class="col-md-4 form-group-moda">
                        <label class="form-label-moda">PHƯỜNG / XÃ</label>
                        <select class="form-input-moda">
                            <option>Phường Bến Nghé</option>
                            <option>Phường Đa Kao</option>
                        </select>
                    </div>
                    <div class="col-12 form-group-moda">
                        <label class="form-label-moda">ĐỊA CHỈ CHI TIẾT</label>
                        <input class="form-input-moda" placeholder="Số nhà, tên đường..." type="text">
                    </div>
                </div>
            </section>
            <hr>
            <!-- 2. Phương thức vận chuyển -->
            <section>
                <h2 class="step-title">2. Phương thức vận chuyển</h2>
                <div class="shipping-method-item active">
                    <div class="d-flex align-items-center gap-3">
                        <input checked="" class="form-check-input mt-0" name="shipping" style="width: 16px; height: 16px; border-radius: 0;" type="radio">
                        <div>
                            <div class="fw-bold">Giao hàng tiêu chuẩn</div>
                            <div class="text-muted" style="font-size: 12px;">3 - 5 ngày làm việc</div>
                        </div>
                    </div>
                    <div class="fw-bold">Miễn phí</div>
                </div>
                <div class="shipping-method-item">
                    <div class="d-flex align-items-center gap-3">
                        <input class="form-check-input mt-0" name="shipping" style="width: 16px; height: 16px; border-radius: 0;" type="radio">
                        <div>
                            <div class="fw-bold">Giao hàng nhanh</div>
                            <div class="text-muted" style="font-size: 12px;">1 - 2 ngày làm việc</div>
                        </div>
                    </div>
                    <div class="fw-bold">50.000đ</div>
                </div>
            </section>
            <hr>
            <!-- 3. Phương thức thanh toán -->
            <section>
                <h2 class="step-title">3. Phương thức thanh toán</h2>
                <div class="payment-method-grid">
                    <div class="payment-method-item">
                        <span class="material-symbols-outlined">account_balance_wallet</span>
                        <div class="fw-bold" style="font-size: 12px; letter-spacing: 0.05em;">MOMO / VNPAY</div>
                        <div class="text-muted mt-1" style="font-size: 10px;">Ví điện tử nội địa</div>
                    </div>
                    <div class="payment-method-item">
                        <span class="material-symbols-outlined">payments</span>
                        <div class="fw-bold" style="font-size: 12px; letter-spacing: 0.05em;">COD</div>
                        <div class="text-muted mt-1" style="font-size: 10px;">Thanh toán khi nhận hàng</div>
                    </div>
                    <div class="payment-method-item">
                        <span class="material-symbols-outlined">account_balance</span>
                        <div class="fw-bold" style="font-size: 12px; letter-spacing: 0.05em;">CHUYỂN KHOẢN</div>
                        <div class="text-muted mt-1" style="font-size: 10px;">Xác nhận thủ công</div>
                    </div>
                </div>
            </section>
        </div>
        <!-- Right Column: Order Summary -->
        <div class="col-lg-5">
            <div class="order-summary-card">
                <h2 class="step-title" style="font-size: 20px; margin-bottom: 24px;">Tóm tắt đơn hàng</h2>
                <div class="order-items no-scrollbar" style="max-height: 300px; overflow-y: auto;">
                    <div class="product-item">
                        <img alt="Tailored Wool Coat" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCTzI3-Q836vN_HQtEbV0MhQoLwajYHRV9ZqBi2HIj6vh6NsgtYbih09c2iFG3JY-VomxtxIZHpTiGQU9fisl93WLgQk5gYr5wX_CAxzXelYrFJR9AjPQyinkB26FVmBXGC-12XWqLl95QFGYCsDUIP2GMm7Mv5U2pDr9kKHH2A5AvWW8T6P6aOrcZeV6msc2jc9E0MORONkNbbe9m8wE398_2ECBW-KV0t8c8dpH51UHzzN3oeBnTArBjyB2EnLgmI1LVYeJgBOlE">
                        <div class="product-info">
                            <h3 class="">Tailored Wool Coat</h3>
                            <p class="">SIZE: L | QTY: 1</p>
                            <div class="product-price">5.200.000đ</div>
                        </div>
                    </div>
                    <div class="product-item">
                        <img alt="Signature T-Shirt" class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAuPZF0RoE_nDqx2Y_jvWP2cleeZo0QMFMiHQmZ6eW7IwSx8sZ4G2fSCFZTRfEf4_p4hhQmSHTLFQdu1G3CCna9G8TRCRuhUgTaMVPGza8acyiu-5OkYl5i9yKj1oE962NJy5wJuRFfG_W2Wa99Qx_ijU-SJez3e4Dy0wp35kSVFwSDQ9JJohoYzJAmzyvcCnNx7ErQl4_G0Wdy-wH0gHWWEKnt2ANBeBN55GYisTqlNxFJ9LPFlYdJH9Abo-aKL9Y7nS0LzKE9Bug">
                        <div class="product-info">
                            <h3 class="">Signature T-Shirt</h3>
                            <p class="">SIZE: M | QTY: 1</p>
                            <div class="product-price">850.000đ</div>
                        </div>
                    </div>
                </div>
                <hr class="my-3">
                <div class="summary-details">
                    <div class="summary-row">
                        <span class="">Tạm tính</span>
                        <span class="">6.050.000đ</span>
                    </div>
                    <div class="summary-row">
                        <span class="">Phí vận chuyển</span>
                        <span class="">0đ</span>
                    </div>
                    <div class="summary-row text-error">
                        <span class="">Giảm giá</span>
                        <span class="">-250.000đ</span>
                    </div>
                </div>
                <div style="border-top: 2px solid var(--moda-primary); margin: 16px 0;"></div>
                <div class="summary-total">
                    <span class="total-label">Tổng thanh toán</span>
                    <span class="total-amount">5.800.000đ</span>
                </div>
                <button class="btn btn-moda">THANH TOÁN NGAY</button>
                <p class="text-center text-muted mt-3 mb-0" style="font-size: 10px;">
                    Bằng cách đặt hàng, bạn đồng ý với các <a class="text-dark text-decoration-underline" href="#">Điều khoản &amp; Chính sách</a> của MODA.
                </p>
            </div>
        </div>
    </div>
</main>
<!-- Footer -->
<footer class="mt-5 py-5 border-top border-dark">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-4">
                <div class="navbar-brand-moda" style="font-size: 28px;">MODA</div>
            </div>
            <div class="col-md-4 mt-4 mt-md-0">
                <div style="font-size: 12px; font-weight: 700; text-transform: uppercase; margin-bottom: 12px;" class="">Hỗ trợ</div>
                <ul class="list-unstyled mb-0" style="font-size: 12px; color: var(--moda-secondary);">
                    <li class="mb-1"><a class="text-decoration-none text-muted" href="#">Privacy Policy</a></li>
                    <li class="mb-1"><a class="text-decoration-none text-muted" href="#">Terms of Service</a></li>
                    <li class="mb-1"><a class="text-decoration-none text-muted" href="#">Shipping &amp; Returns</a></li>
                    <li class=""><a class="text-decoration-none text-muted" href="#">Contact</a></li>
                </ul>
            </div>
            <div class="col-md-4 text-md-end mt-4 mt-md-0">
                <p class="mb-0 text-muted" style="font-size: 12px;">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
            </div>
        </div>
    </div>
</footer>
<!-- Mobile Bottom Nav -->
<nav class="mobile-bottom-nav d-md-none">
    <a class="mobile-nav-item" href="#">
        <span class="material-symbols-outlined">storefront</span>
        Shop
    </a>
    <a class="mobile-nav-item" href="#">
        <span class="material-symbols-outlined">search</span>
        Search
    </a>
    <a class="mobile-nav-item active" href="#">
        <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">shopping_bag</span>
        Bag
    </a>
    <a class="mobile-nav-item" href="#">
        <span class="material-symbols-outlined">person</span>
        Account
    </a>
</nav>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>


</body></html>