
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>MODA - Voucher Detail</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Google Fonts: Inter -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet"/>
    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>

    <link rel="stylesheet" href="/assets/css/customer/view-voucher.css">
</head>
<body>
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KH MỚI
</div>
<header class="navbar-custom">
    <a class="brand-logo" href="#">MODA</a>
    <nav class="nav-links">
        <a class="nav-link-custom" href="#">TRANG CHỦ</a>
        <a class="nav-link-custom" href="#">NỮ</a>
        <a class="nav-link-custom" href="#">NAM</a>
        <a class="nav-link-custom" href="#">PHỤ KIỆN</a>
        <a class="nav-link-custom sale-off" href="#">SALE OFF</a>
    </nav>
    <div class="navbar-utilities">
        <div class="search-container d-none d-md-block">
            <span class="material-symbols-outlined search-icon">search</span>
            <input class="search-input" placeholder="TÌM KIẾM..." type="text"/>
        </div>
        <button class="utility-btn">
            <span class="material-symbols-outlined">favorite</span>
        </button>
        <button class="utility-btn">
            <span class="material-symbols-outlined">shopping_bag</span>
            <span class="cart-badge">3</span>
        </button>
        <button class="utility-btn">
            <span class="material-symbols-outlined">account_circle</span>
        </button>
    </div>
</header>
<main class="main-container">
    <div>
        <button class="btn-back" onclick="history.back()">
            <span class="material-symbols-outlined" style="font-size: 20px;">arrow_back</span>
            QUAY LẠI
        </button>
    </div>
    <!-- Voucher Detail Card -->
    <section class="voucher-section">
        <div class="voucher-card">
            <div class="status-badge">Còn hiệu lực</div>
            <h2 class="font-label mb-3" style="color: var(--on-secondary-container);">Ưu đãi độc quyền</h2>
            <h1 class="font-headline-xl mb-2">50.000đ</h1>
            <p class="font-headline-md mb-4">Giảm 50k cho đơn từ 500k</p>
            <div class="voucher-code-wrapper">
                <div class="voucher-code-box" id="voucherCode">MODA50K</div>
                <button class="btn-copy" onclick="copyCode(this)">SAO CHÉP MÃ</button>
            </div>
            <div class="expiry-text">
                <span class="material-symbols-outlined" style="font-size: 16px;">calendar_today</span>
                Hạn dùng: 31/12/2024
            </div>
        </div>
        <div class="row g-5">
            <!-- Conditions -->
            <div class="col-md-6">
                <h3 class="info-title">Điều kiện áp dụng</h3>
                <ul class="info-list">
                    <li>
                        <span class="material-symbols-outlined" style="font-size: 18px; color: var(--primary);">check</span>
                        <span>Áp dụng cho đơn hàng có tổng giá trị từ 500.000đ trở lên.</span>
                    </li>
                    <li>
                        <span class="material-symbols-outlined" style="font-size: 18px; color: var(--primary);">check</span>
                        <span>Không áp dụng đồng thời với các chương trình khuyến mãi khác.</span>
                    </li>
                    <li>
                        <span class="material-symbols-outlined" style="font-size: 18px; color: var(--primary);">check</span>
                        <span>Chỉ áp dụng cho các sản phẩm nguyên giá tại hệ thống MODA.</span>
                    </li>
                    <li>
                        <span class="material-symbols-outlined" style="font-size: 18px; color: var(--primary);">check</span>
                        <span>Mỗi khách hàng chỉ được sử dụng mã 01 lần duy nhất.</span>
                    </li>
                </ul>
            </div>
            <!-- Instructions -->
            <div class="col-md-6">
                <h3 class="info-title">Hướng dẫn sử dụng</h3>
                <div class="info-list">
                    <div class="step-item">
                        <span class="step-number">1</span>
                        <p class="mb-0" style="color: var(--on-secondary-container);">Chọn các sản phẩm yêu thích và thêm vào giỏ hàng.</p>
                    </div>
                    <div class="step-item">
                        <span class="step-number">2</span>
                        <p class="mb-0" style="color: var(--on-secondary-container);">Tại trang Thanh toán, tìm ô "Mã giảm giá".</p>
                    </div>
                    <div class="step-item">
                        <span class="step-number">3</span>
                        <p class="mb-0" style="color: var(--on-secondary-container);">Dán mã <strong>MODA50K</strong> và nhấn "Áp dụng".</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- Recommended Products -->
    <section class="product-section">
        <h2 class="section-title">Sản phẩm áp dụng</h2>
        <div class="row g-4">
            <!-- Product 1 -->
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="product-image-container">
                        <img alt="Tailored Coat" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCQs4yp1rI-0wCn0CJ9VCChQ7O4DWU3axIKlI57dFdQDlVj6FKUR5IiEvEjpAW6apZuE-RgCIi3ILvt__HSf3arRmYBkMlejobRTMrKt3OrxTMf7BJu20PQ0Ubg90u5nIUWV43YSwvq_Y3Jh4im7WZHzyVzInLwEsplKAEPqzG4ZtdZl3ULWAoR_QfeYBviUfq6H-ofXpvS2jK4eIRWZtk9MILoT087at-DHWrkc_EOOIMeTAkqvOXwC2kdfNR1ui6szo_wzfowtsc"/>
                    </div>
                    <p class="product-name">Tailored Coat</p>
                    <p class="product-price">1.250.000đ</p>
                </a>
            </div>
            <!-- Product 2 -->
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="product-image-container">
                        <img alt="Classic White Shirt" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBXyTYB4Om-FBZCwyv1xUDOuvk-KU1hstQMiijefMeVikaXOrqyfrAjwj9HIJeg7Y0h39wcQDOeiiKNgPdshJt2RsZcEKMNkC-YsBxwrYsPtlN-p2B-yQSdNhgjPnAhuq3PfuQ3RBzihsUrsILzWH5kOROTTH34-rroyp6gEfiMU5DhnfnnWPTwifCrmUMe14oE7shjgdcWVY9udjdjzd0k1Lg-shZi42i1URE2hnjjZ1c8yEYCFeNoB5Tqu_aCwVpQKkCUj6UaMBg"/>
                    </div>
                    <p class="product-name">Classic White Shirt</p>
                    <p class="product-price">680.000đ</p>
                </a>
            </div>
            <!-- Product 3 -->
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="product-image-container">
                        <img alt="Raw Denim" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAJpBnxy1FGjAOPZkrrkgvdlv2VNprt_cy5ApzIESh_2aXUOsK-BYcoRgGFCYEoQu0ysZTmjwSk7XEuGGkw0dnH-o9EfMBEWsgsiMwIgk54OQnqI_VtkPCPBUTgbY3RoM3oJsCJ2G0-s8dgmjTk3JaVHw-1z1MWz0XdRSBjzgbuvQoN316N1r8g59-YA02W2cb8L8eGQEVnCVdaWIS4_r-SGK2sFojd7Sg-G3lzp93oDS8Pss6Viqyt-ob2tyeed79yOePjtn2KHOY"/>
                    </div>
                    <p class="product-name">Raw Denim</p>
                    <p class="product-price">950.000đ</p>
                </a>
            </div>
            <!-- Product 4 -->
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="product-image-container">
                        <img alt="Wool Turtleneck" class="product-image" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBvLSkdgz6M0ky7Cl9HIj6vqK27GsNxry64kK-MMbneVp3mJ0P2duT0MRYCFaNs7350RDmmhy7Vfnq1WVnR9sOwevhp-_gjANos7i7yu9pzMzKSU16N8dwSjGIeRBIDUDo-Uyvb4Fp7BhrB5hkDv1SHhXgXbYebNHcli2zV1ex8RGqzc3OGR91tYsa4scCclQhe2cUV36TKhUqgLKmzvX9EYF5u71EIB_eCsB7Q8TKlIZvtCCiyDopcsSeBCezKCRSd51RHmNZUsJs"/>
                    </div>
                    <p class="product-name">Wool Turtleneck</p>
                    <p class="product-price">720.000đ</p>
                </a>
            </div>
        </div>
        <button class="btn-outline-custom">Xem tất cả sản phẩm</button>
    </section>
</main>
<footer>
    <div class="footer-content">
        <div class="footer-logo">MODA</div>
        <div class="footer-links">
            <a class="footer-link" href="#">Privacy Policy</a>
            <a class="footer-link" href="#">Terms of Service</a>
            <a class="footer-link" href="#">Shipping &amp; Returns</a>
            <a class="footer-link" href="#">Contact</a>
        </div>
        <div class="copyright">
            © 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.
        </div>
    </div>
</footer>
<!-- Bootstrap Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function copyCode(btn) {
        const code = document.getElementById('voucherCode').innerText;
        navigator.clipboard.writeText(code).then(() => {
            const originalText = btn.innerText;
            btn.innerText = 'ĐÃ SAO CHÉP';
            btn.style.backgroundColor = '#ffffff';
            btn.style.color = '#000000';

            setTimeout(() => {
                btn.innerText = originalText;
                btn.style.backgroundColor = '';
                btn.style.color = '';
            }, 2000);
        });
    }
</script>
</body></html>