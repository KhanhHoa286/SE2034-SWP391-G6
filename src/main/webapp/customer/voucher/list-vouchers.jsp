
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>MODA ARCHIVE - Voucher của tôi</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="/assets/css/customer/list-vouchers.css">
</head>
<body>
<!-- Announcement Bar -->
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KH MỚI
</div>
<!-- Navigation Bar -->
<nav class="navbar-custom d-flex align-items-center justify-content-between">
    <div class="d-flex align-items-center flex-grow-1">
        <a class="brand-logo" href="#">MODA</a>
    </div>
    <div class="nav-links-center d-none d-lg-flex justify-content-center flex-grow-1">
        <a class="nav-link-custom" href="#">TRANG CHỦ</a>
        <a class="nav-link-custom" href="#">NỮ</a>
        <a class="nav-link-custom" href="#">NAM</a>
        <a class="nav-link-custom" href="#">PHỤ KIỆN</a>
        <a class="nav-link-custom sale-off" href="#">SALE OFF</a>
    </div>
    <div class="d-flex align-items-center justify-content-end gap-4 flex-grow-1">
        <div class="search-input-wrapper d-none d-md-block">
            <span class="material-symbols-outlined">search</span>
            <input class="search-input" placeholder="TÌM KIẾM..." type="text"/>
        </div>
        <button class="icon-btn">
            <span class="material-symbols-outlined">favorite</span>
        </button>
        <button class="icon-btn">
            <span class="material-symbols-outlined">shopping_bag</span>
            <span class="cart-badge">3</span>
        </button>
        <button class="icon-btn">
            <span class="material-symbols-outlined">account_circle</span>
        </button>
    </div>
</nav>
<main>
    <!-- Back Button -->
    <button class="back-btn">
        <span class="material-symbols-outlined" style="font-size: 18px;">chevron_left</span>
        QUAY LẠI
    </button>
    <!-- Title Section -->
    <div class="page-header">
        <h1 class="page-title">Voucher của tôi</h1>
        <div class="filter-wrapper">
            <select class="filter-select">
                <option value="all">TẤT CẢ</option>
                <option value="available">Sẵn sàng sử dụng</option>
                <option value="expired">Đã sử dụng/Hết hạn</option>
            </select>
            <span class="material-symbols-outlined">expand_more</span>
        </div>
    </div>
    <!-- Voucher Grid -->
    <div class="voucher-grid">
        <!-- Voucher 1 -->
        <div class="voucher-card">
            <div class="voucher-left">
                <span class="voucher-logo">MODA</span>
            </div>
            <div class="voucher-right">
                <div>
                    <div class="voucher-title">Giảm 50k cho đơn từ 500k</div>
                    <div class="voucher-expiry">Hết hạn: 31/12/2024</div>
                    <a class="voucher-link" href="#">Điều kiện</a>
                </div>
                <button class="voucher-btn">DÙNG NGAY</button>
            </div>
        </div>
        <!-- Voucher 2 -->
        <div class="voucher-card">
            <div class="voucher-left">
                <div class="badge-new">NEW</div>
                <span class="voucher-logo">SALE</span>
            </div>
            <div class="voucher-right">
                <div>
                    <div class="voucher-title">Miễn phí vận chuyển</div>
                    <div class="voucher-expiry">Hết hạn: 15/12/2024</div>
                    <a class="voucher-link" href="#">Điều kiện</a>
                </div>
                <button class="voucher-btn">DÙNG NGAY</button>
            </div>
        </div>
        <!-- Voucher 3 (Expired) -->
        <div class="voucher-card expired">
            <div class="voucher-left">
                <span class="voucher-logo">MODA</span>
            </div>
            <div class="voucher-right">
                <div>
                    <div class="voucher-title" style="color: var(--secondary);">Ưu đãi 10% Membership</div>
                    <div class="voucher-expiry">Đã hết hiệu lực</div>
                    <a class="voucher-link" href="#">Điều kiện</a>
                </div>
                <button class="voucher-btn" disabled="">HẾT HẠN</button>
            </div>
        </div>
        <!-- Voucher 4 -->
        <div class="voucher-card">
            <div class="voucher-left">
                <span class="voucher-logo">GOLD</span>
            </div>
            <div class="voucher-right">
                <div>
                    <div class="voucher-title">Giảm 200k đơn 2 triệu</div>
                    <div class="voucher-expiry">Hết hạn: 01/01/2025</div>
                    <a class="voucher-link" href="#">Điều kiện</a>
                </div>
                <button class="voucher-btn">DÙNG NGAY</button>
            </div>
        </div>
        <!-- Voucher 5 (Used) -->
        <div class="voucher-card expired">
            <div class="voucher-left">
                <span class="voucher-logo">MODA</span>
            </div>
            <div class="voucher-right">
                <div>
                    <div class="voucher-title" style="color: var(--secondary);">Chào bạn mới 30k</div>
                    <div class="voucher-expiry">Đã dùng vào 20/11/2024</div>
                    <a class="voucher-link" href="#">Điều kiện</a>
                </div>
                <button class="voucher-btn" disabled="">ĐÃ DÙNG</button>
            </div>
        </div>
    </div>
</main>
<!-- Footer -->
<footer>
    <div class="footer-content">
        <div class="footer-logo">MODA</div>
        <div class="footer-links">
            <a class="footer-link" href="#">Privacy Policy</a>
            <a class="footer-link" href="#">Terms of Service</a>
            <a class="footer-link" href="#">Shipping &amp; Returns</a>
            <a class="footer-link" href="#">Contact</a>
        </div>
        <div class="footer-copy">
            © 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.
        </div>
    </div>
</footer>
<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>