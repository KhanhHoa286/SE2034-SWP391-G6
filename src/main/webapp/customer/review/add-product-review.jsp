
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="vi"><head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
    <title>Viết Đánh Giá - MODA ARCHIVE</title>
    <!-- Google Fonts: Inter -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet"/>
    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="/assets/css/customer/add-product-review.css">
</head>
<body>
<div class="announcement-bar">FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI</div>
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
<main class="container py-5" style="max-width: 800px;">
    <div class="mb-5">
        <a class="text-decoration-none text-dark d-flex align-items-center mb-3 fw-bold" href="#" style="font-size: 12px; letter-spacing: 0.2em;">
            <span class="material-symbols-outlined fs-6">chevron_left</span> QUAY LẠI
        </a>
        <h1 class="fw-bold mb-2" style="font-size: 32px;">Viết đánh giá</h1>
        <p class="text-secondary">Chia sẻ cảm nhận chân thực của bạn để giúp cộng đồng MODA mua sắm tốt hơn.</p>
    </div>
    <section class="d-flex gap-4 pb-4 mb-4 border-bottom">
        <img class="product-img" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAelUg_VMd2Uwn395qUe7EPSNbQqmzflZGUZVMt5ru8o39foNQKzPZGMDEzZznLGbqzd0IfKanhXEZw6EK_B_gXSwBtuSWjXViiZ2y13RNNb76l6lq_6q3tgTzVFpDjmv-1vWwmDrmR5AUthjK7o0wz9Rdm2HyC8Je5C1A6XvaNrL1pZIbOoa-pwxfFddgQxdjHYAhZphLIhoJeGVH1Z0VBx2b_MVJfBcA4mzz1KqvVquIAvGJIh7m52L74FbKvCBkcmGFldHWbPkA"/>
        <div class="d-flex flex-column justify-content-center">
            <span class="text-secondary text-uppercase fw-bold" style="font-size: 10px; letter-spacing: 0.1em;">Sản phẩm của bạn</span>
            <h2 class="h5 fw-bold mb-1">ÁO KHOÁC DA WOOL TỐI GIẢN</h2>
            <p class="fw-bold mb-0">4.250.000 VND</p>
        </div>
    </section>
    <form onsubmit="event.preventDefault(); alert('Cảm ơn bạn đã gửi đánh giá!');">
        <div class="mb-4">
            <label class="form-label fw-bold text-uppercase" style="font-size: 12px; letter-spacing: 0.1em;">Mức độ hài lòng</label>
            <div class="d-flex gap-2" id="starRating">
                <button class="btn btn-star" data-index="1" type="button"><span class="fw-bold small">1</span><span class="material-symbols-outlined fs-6">star</span></button>
                <button class="btn btn-star" data-index="2" type="button"><span class="fw-bold small">2</span><span class="material-symbols-outlined fs-6">star</span></button>
                <button class="btn btn-star" data-index="3" type="button"><span class="fw-bold small">3</span><span class="material-symbols-outlined fs-6">star</span></button>
                <button class="btn btn-star" data-index="4" type="button"><span class="fw-bold small">4</span><span class="material-symbols-outlined fs-6">star</span></button>
                <button class="btn btn-star" data-index="5" type="button"><span class="fw-bold small">5</span><span class="material-symbols-outlined fs-6">star</span></button>
            </div>
        </div>
        <div class="mb-4">
            <label class="form-label fw-bold text-uppercase" style="font-size: 12px; letter-spacing: 0.1em;">Tiêu đề đánh giá</label>
            <input class="form-control border-secondary-subtle p-3 rounded-0" placeholder="Ví dụ: Chất lượng tuyệt vờmi" type="text"/>
        </div>
        <div class="mb-4">
            <label class="form-label fw-bold text-uppercase" style="font-size: 12px; letter-spacing: 0.1em;">Chia sẻ trải nghiệm</label>
            <textarea class="form-control border-secondary-subtle p-3 rounded-0" placeholder="Hãy chia sẻ thém về chất liệu, kích cỡ hoặc cảm giác khi mặc sản phẩm này..." rows="6"></textarea>
        </div>
        <div class="mb-4">
            <label class="form-label fw-bold text-uppercase" style="font-size: 12px; letter-spacing: 0.1em;">Hình ảnh/Video thực tế</label>
            <div class="d-flex gap-3 flex-wrap">
                <div class="upload-box">
                    <span class="material-symbols-outlined text-secondary">photo_camera</span>
                    <span class="fw-bold text-secondary" style="font-size: 9px; margin-top: 5px;">THÊM ẢNH</span>
                </div>
                <div class="position-relative" style="width: 96px; height: 96px;">
                    <img class="w-100 h-100 object-fit-cover border" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBdKwPDib-UEq0IP70ohbVduuh87BM_zeYBPSSHfjKMYXXO_O67LUJ0ulYMPXYXqa-LqJT0uyTPYvQXSnMRQWrRjlXe2JrKkSm3lbEkU5ONchJNBAyF7R5Z-R96BAtErnPJbVw89o_8acptUpnL2Jeq1OcP4QhupfoWh4PLm7_Bw7P6ZXfrN2b5wFpdEV_Ao9BU_-cvzsFvdeYkJCXuS8-YylLjYvOH_sMzQD4D543nMIPI7ihAMw-fZ_kbCqwoFC6La-83giP_YkY"/>
                </div>
            </div>
            <p class="text-secondary small mt-2">Tối đa 5 ảnh hoặc video. Định dạng: JPG, PNG, MP4.</p>
        </div>
        <div class="d-flex justify-content-end pt-4">
            <button class="btn-submit" type="submit">Gửi đánh giá</button>
        </div>
    </form>
</main>
<footer>
    <div class="container d-flex flex-column flex-md-row justify-content-between align-items-center gap-4">
        <div>
            <span class="h4 fw-bold mb-1 d-block">MODA</span>
            <span class="text-secondary small text-uppercase" style="letter-spacing: -0.02em;">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</span>
        </div>
        <div class="d-flex gap-4">
            <a class="text-secondary text-decoration-none small" href="#">Privacy Policy</a>
            <a class="text-secondary text-decoration-none small" href="#">Terms of Service</a>
            <a class="text-secondary text-decoration-none small" href="#">Shipping</a>
            <a class="text-secondary text-decoration-none small" href="#">Returns</a>
            <a class="text-secondary text-decoration-none small" href="#">Contact</a>
        </div>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const stars = document.querySelectorAll('.btn-star');
    let currentRating = 0;
    stars.forEach(star => {
        star.addEventListener('click', () => {
            currentRating = parseInt(star.getAttribute('data-index'));
            stars.forEach((s, idx) => {
                if (idx < currentRating) s.classList.add('active');
                else s.classList.remove('active');
            });
        });
    });
</script>
</body></html>
