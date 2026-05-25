
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>

<html lang="vi">
<head>
    <meta charset="utf-8" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <!-- Bootstrap 5 CSS -->
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
            rel="stylesheet"
    />
    <!-- Fonts and Icons -->
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&amp;display=swap"
            rel="stylesheet"
    />
    <link
            href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
            rel="stylesheet"
    />
    <link rel="stylesheet" href="/assest/css/customer/list-products.css" />
</head>
<body>
<div class="promo-bar">FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI</div>
<!-- Navigation -->
<nav
        class="navbar-custom"
        style="
        background-color: var(--surface);
        border-bottom: 1px solid var(--primary);
        padding: 16px 0;
        width: 100%;
        z-index: 1050;
        position: relative;
      "
>
    <div class="container-fluid px-3 px-md-5 max-w-container-max mx-auto">
        <div class="row w-100 align-items-center g-0">
            <div class="col-3 d-flex align-items-center">
                <a class="navbar-brand-custom" href="#">MODA</a>
            </div>
            <div class="col-6 d-flex justify-content-center align-items-center">
                <div class="d-flex">
                    <a class="nav-link-custom mx-2" href="#">TRANG CHỦ</a>
                    <a class="nav-link-custom mx-2" href="#">NỮ</a>
                    <a class="nav-link-custom mx-2" href="#">NAM</a>
                    <a class="nav-link-custom mx-2" href="#">PHỤ KIỆN</a>
                    <a class="nav-link-custom nav-link-sale mx-2" href="#"
                    >SALE OFF</a
                    >
                </div>
            </div>
            <div class="col-3 d-flex justify-content-end align-items-center">
                <div class="search-container d-none d-lg-block">
                    <span class="material-symbols-outlined search-icon">search</span>
                    <input
                            class="search-input"
                            placeholder="TÌM KIẾM..."
                            type="text"
                    />
                </div>
                <button class="nav-icon-btn ms-3">
              <span class="material-symbols-outlined" style="font-size: 24px"
              >favorite_border</span
              >
                </button>
                <button class="nav-icon-btn ms-3">
              <span class="material-symbols-outlined" style="font-size: 24px"
              >shopping_bag</span
              >
                    <span class="cart-badge">3</span>
                </button>
                <button class="nav-icon-btn ms-3">
              <span class="material-symbols-outlined" style="font-size: 24px"
              >account_circle</span
              >
                </button>
            </div>
        </div>
    </div>
</nav>
<main
        class="main-container"
        style="
        max-width: var(--container-max);
        margin: 0 auto;
        padding: 64px var(--margin-mobile) 0;
      "
>
    <a class="back-link" href="#">
        <span class="material-symbols-outlined" style="font-size: 18px"
        >chevron_left</span
        >
        QUAY LẠI
    </a>
    <div class="row g-4 py-4">
        <!-- Left Column: Gallery -->
        <div class="col-12 col-md-7">
            <div class="main-image-wrapper">
                <img
                        alt="Product Main View"
                        id="main-product-img"
                        src="https://lh3.googleusercontent.com/aida-public/AB6AXuBAmmfizEFqzsGBDtHRgNn9sMF0yxR8QoYfiOk3OmhcwC6hG6Nv-SZt-qLbmorOYoVsIZPQMxxMq9klw5DHBt2YO923wQBq5JDkiAwJh8aLfwfZ23-6mR2fhgV20LbnvVMq-wadveGbvPtEOIDzU8FtINC7FmtG2JqbkWI_oZ45zrtDOBMGJCZuCI9SPTfZvdWQR2SPjooHnJdzAq1mer4fmnNHbXUGvQuhRhGUiaNlXa_Oo3ArZSbMkT90uv2NCPoqnJN5UE4NPik"
                />
            </div>
            <div class="thumbnail-grid">
                <button class="thumb-btn active">
                    <img
                            alt="Thumbnail 1"
                            src="https://lh3.googleusercontent.com/aida-public/AB6AXuDtn1TvHk09iND3gtFR6SxseUMUAmbRwauQJ81OK3vR2t6RDgaLujiLn6CFjg4WD8x1DIeOvo78d9MED6TtXRcUyAIamsBlm1KlJTkLOEZHoVpVqsZbJLhIbtsTmFIk3e-rY57bBgFWGkoMaTKndUQe1vVeMzPzIdACsWbsU6Y7LStwFFUVn2wRRI6tu21t18ukZ9T3oJ1ABOcagdirRj7x_FN07iflEa9pRO4l4KApGGFfzYZ5S8g1HX9Z8dQ1tcO_DjBRTeU2Jss"
                    />
                </button>
                <button class="thumb-btn">
                    <img
                            alt="Thumbnail 2"
                            src="https://lh3.googleusercontent.com/aida-public/AB6AXuBPnhnvylbIKMTgtwLcQhU8ntBjUU8PVWJXRirrpz7Rvx2eSJTpMnyytDI9OD04isA3HH_IlcVs0eN5oTpIAgd1cPPt0tG6b1hInxEzpkpT12hDrFmId5eDWYTeEO-x2KYXDmTE57wBQl-0ORVAz6vlvy2RtpYuyXySDAWRo2mmjAUh7SBV1R8Y_eHE2Eb2cQRX2s8gtYKnpTfjTefIWVhmLcsNENjpexjeGZqBvY5TvFjgHffGf8gB3Jl0oP1TGJXmE4Z3Z0SwN3Q"
                    />
                </button>
                <button class="thumb-btn">
                    <img
                            alt="Thumbnail 3"
                            src="https://lh3.googleusercontent.com/aida-public/AB6AXuBjvIYNiHzZ2SKKoNYAWHkk9KbgmEy23WkaAf6YYsueTf-kmbIbXOpVi5KHkBrb623Z1LmMwnkbfvZIzNqofeoVntJ4TAOWpXrDCaciT2EIKycF8wELtCKeKvTZ_h4G2ND7u2633JkgatOvx9NLm5l6z4bHe9KAYKs6WB41MyiQsBUNLhInyzh9X0EFbRJxpdsSbnnYG8Lu18V8uAVUwvUBoXCqgJi1DFcHBnBh5r5MDG9LKv018EZ_FAwB4717p_J4s5uHRsGPz-Q"
                    />
                </button>
                <button class="thumb-btn">
                    <img
                            alt="Thumbnail 4"
                            src="https://lh3.googleusercontent.com/aida-public/AB6AXuC-B38j4PONto26jWrD0Ik2vNO8E4GRYHAf7XMxQYYS09l2ImJkgGqclJ7t521yYhc9PFNKvlMr0-cfjWEd8xJnbuH6-3rGSuvRtyimGepestbPAWwsvr1MX4onyK0M9WSy91ehgV4nFaZ83c_unsc1dsV3ZCgRT57dN9FkWFoyLZKV7k9e9bAhWnrpAkV41lO_Ejat_4CVb1pSJjRoohPrFYnLCTsUsi_MY5sLpqUVms0LNfFdkITuEPSOrS-6QEDdIIWC9JpEpSw"
                    />
                </button>
            </div>
        </div>
        <!-- Right Column: Info -->
        <div class="col-12 col-md-5">
            <div class="product-sticky">
                <p class="collection-label">ARCHIVE COLLECTION</p>
                <h1 class="product-title">ÁO KHOÁC DẠ WOOL TỐI GIẢN</h1>
                <p class="product-price">12.500.000 ₫</p>
                <div>
                    <p class="selector-label">
                        MÀU SẮC: <span id="color-name">Midnight Black</span>
                    </p>
                    <div class="color-selector">
                        <button class="color-btn active" data-color="Midnight Black">
                            <span style="background-color: black"></span>
                        </button>
                        <button class="color-btn" data-color="Slate Grey">
                            <span style="background-color: #3d3d3d"></span>
                        </button>
                        <button class="color-btn" data-color="Soft Silver">
                            <span style="background-color: #e5e5e5"></span>
                        </button>
                    </div>
                </div>
                <div>
                    <div class="size-header">
                        <p class="selector-label mb-0">KÍCH THƯỚC</p>
                        <button class="size-guide">BẢNG SIZE</button>
                    </div>
                    <div class="size-grid">
                        <button class="size-btn">XS</button>
                        <button class="size-btn active">S</button>
                        <button class="size-btn">M</button>
                        <button class="size-btn">L</button>
                        <button class="size-btn" disabled="">XL</button>
                    </div>
                </div>
                <div class="action-btns">
                    <button class="btn-primary-custom">THÊM VÀO GIỎ HÀNG</button>
                    <button class="btn-outline-custom">MUA NGAY</button>
                </div>
                <div class="description-section">
                    <p class="desc-title">MÔ TẢ SẢN PHẨM</p>
                    <p class="desc-text">
                        Thiết kế áo khoác dạ Wool cao cấp từ bộ sưu tập Archive. Sản
                        phẩm được chế tác với phom dáng kiến trúc, đường cắt tinh xảo
                        mang lại vẻ ngoài lịch lãm và tối giản. Chất liệu 100% Wool
                        Merino đảm bảo giữ ấm tuyệt đối trong khi vẫn giữ được sự nhẹ
                        nhàng, thanh thoát.
                    </p>
                    <ul class="desc-list">
                        <li>100% Merino Wool cao cấp</li>
                        <li>Lót lụa satin mềm mại</li>
                        <li>Khuy cài ẩn tinh tế</li>
                        <li>Sản xuất tại Việt Nam</li>
                    </ul>
                </div>
                <div class="policy-accordion">
                    <div class="accordion-item-custom" id="acc-delivery">
                        <button class="accordion-button-custom">
                            GIAO HÀNG &amp; ĐỔI TRẢ
                            <span class="material-symbols-outlined">expand_more</span>
                        </button>
                        <div class="accordion-content">
                            <p class="policy-label">Giao hàng miễn phí</p>
                            <p class="policy-text">
                                Miễn phí giao hàng tiêu chuẩn cho mọi đơn hàng trên
                                2.000.000 ₫. Thời gian nhận hàng từ 2-5 ngày làm việc.
                            </p>
                            <p class="policy-label">Chính sách đổi trả</p>
                            <p class="policy-text">
                                Đổi trả sản phẩm trong vòng 7 ngày kể từ ngày nhận hàng. Sản
                                phẩm phải còn nguyên tem mác và chưa qua sử dụng.
                            </p>
                        </div>
                    </div>
                    <div class="accordion-item-custom" id="acc-care">
                        <button class="accordion-button-custom">
                            HƯỚNG DẪN BẢO QUẢN
                            <span class="material-symbols-outlined">expand_more</span>
                        </button>
                        <div class="accordion-content">
                            <p class="policy-text" style="margin-top: 16px">
                                Chỉ giặt khô chuyên nghiệp. Không sử dụng chất tẩy. Là ở
                                nhiệt độ thấp nếu cần thiết. Bảo quản trong túi vải chuyên
                                dụng để giữ phom dáng áo.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Related Products -->
    <section class="related-section">
        <div class="section-header">
            <h2 class="section-title">CÓ THỂ BẠN CŨNG THÍCH</h2>
            <a class="view-all" href="#">XEM TẤT CẢ</a>
        </div>
        <div class="row g-4">
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="card-img-wrapper">
                        <img
                                alt="Related 1"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuC5R4ibjSdDfqHt0wp_ZlhFZnkIJv4PDw9hL5yl-_r2U4Rbac9dhQvuYKT-C_NKhzbH4XqfW-5D_pIBXHZFJmOn2lpJQBmEupplz-QKJK5YCgK50Mb1mbANrDTxn5dprKEJxxBgHZF0Kl1j0OY_RX2xIP3P7yMQDDpWH9NF-_c_ppTOKq6uxHdFJvLa-xtBAysZYbdC90e16fl0jAAH1emc27OauL39v7sPrZfgAWTnsEiHK8ZGaq19zb0aNkoEcEIThv3xQ6DTlT0"
                        />
                        <div class="quick-add">QUICK ADD</div>
                    </div>
                    <p class="card-category">Quần Tây</p>
                    <p class="card-title">QUẦN TÂY PHOM SUÔNG ĐEN</p>
                    <p class="card-price">3.200.000 ₫</p>
                </a>
            </div>
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="card-img-wrapper">
                        <img
                                alt="Related 2"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCTTaVrhsxbXqTslkvu-xaN24XRqjxi08zZqG6sVjA_SGRtF__vRwmzk2KOc7yUZlpIvME5K-4QyXhuZ3wlHsXScH_DIAzmq0xw0uoNrw6aJINSUtaJdgRiMqxvkv7BVsxWqag6mIAscG0yt4PLQ6pRAw2dxeeuPheXYgtH3Vd-rCMj2L4bJagm8UIUZpg-5k9qTCBbRQDRB30-_hIPxu_V9oU6mv2iTVQwlxiqvdwrKFlRm7cnI-XaloGa0eqpkfwAMsvQ3APaiEg"
                        />
                        <div class="quick-add">QUICK ADD</div>
                    </div>
                    <p class="card-category">Áo Sơ Mi</p>
                    <p class="card-title">SƠ MI TRẮNG POPLIN CAO CẤP</p>
                    <p class="card-price">2.100.000 ₫</p>
                </a>
            </div>
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="card-img-wrapper">
                        <img
                                alt="Related 3"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuA7GcWOfoGttz6WTdQqikRnPRgbd_ckLVkDOhA-5JJHKG9OCZQkAoU7fgi7WRDrh-zyuKfS5E5O1khveYx_XCgJ9cC2vKbMolOsWB1tnxFjFiqIfDcsd-x7SdAAk5z2gpICFrkwvtotuuh-W5cOsHtNBPlUZqZBecEXlktCz0FmqYwwJqICkcUNaiQwBGy8--P5pzeT49Z3aaLTZ5b8i5YIhIQ7op73Q0FqEjOwJQQ3hLJk9y0fS8KDmBSxFA7XA2NQtpA1RKNd83U"
                        />
                        <div class="quick-add">QUICK ADD</div>
                    </div>
                    <p class="card-category">Áo Len</p>
                    <p class="card-title">ÁO LEN CASHMERE CỔ LỌ</p>
                    <p class="card-price">5.800.000 ₫</p>
                </a>
            </div>
            <div class="col-6 col-md-3">
                <a class="product-card" href="#">
                    <div class="card-img-wrapper">
                        <img
                                alt="Related 4"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuA_lgU0cqBbhY_rjU5gI1N7Jvys2oeiCPbqHWeB4Wp0qG7-hThWhZQTzyrLjeVKh3HJH9qmPylwiJXveb1ZTtKsU_cH3L22Ziwn_urveW0ImBAUrlQuoQl9gSxCPDWnFwYvv1Avmvqp812hfZn8cMfpSu5Bj_ZRd3gA4U3Lum4HKzTD5eDRPQwX8p2p4eYjyfLu3Ptif6iAU8MO0LqPZlMQ1rL6ucL57kk8C2ynRzFbB_lWyfkly6XhG50Eu15R4GbrEig2jq6GlIY"
                        />
                        <div class="quick-add">QUICK ADD</div>
                    </div>
                    <p class="card-category">Phụ Kiện</p>
                    <p class="card-title">TÚI DA CẦM TAY STRUCTURE</p>
                    <p class="card-price">8.400.000 ₫</p>
                </a>
            </div>
        </div>
    </section>
</main>
<footer class="footer-custom">
    <div class="container-fluid px-3 px-md-5 max-w-container-max mx-auto">
        <div class="row align-items-center">
            <div class="col-12 col-md-4">
                <span class="footer-brand">MODA</span>
                <p class="footer-text">
                    Định nghĩa lại sự sang trọng qua ngôn ngữ tối giản và chất lượng
                    vượt trội.
                </p>
            </div>
            <div class="col-12 col-md-4">
                <div class="footer-links">
                    <a class="footer-link" href="#">SHIPPING &amp; RETURNS</a>
                    <a class="footer-link" href="#">PRIVACY POLICY</a>
                    <a class="footer-link" href="#">TERMS OF SERVICE</a>
                    <a class="footer-link" href="#">CONTACT</a>
                </div>
            </div>
            <div class="col-12 col-md-4 text-md-end">
                <p class="copyright">© 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.</p>
            </div>
        </div>
    </div>
</footer>
<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Gallery Interaction
    const mainImg = document.getElementById("main-product-img");
    const thumbs = document.querySelectorAll(".thumb-btn");

    thumbs.forEach((btn) => {
        btn.addEventListener("click", () => {
            thumbs.forEach((b) => b.classList.remove("active"));
            btn.classList.add("active");
            mainImg.src = btn.querySelector("img").src;
        });
    });

    // Color Interaction
    const colorBtns = document.querySelectorAll(".color-btn");
    const colorNameDisplay = document.getElementById("color-name");

    colorBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            colorBtns.forEach((b) => b.classList.remove("active"));
            btn.classList.add("active");
            colorNameDisplay.textContent = btn.getAttribute("data-color");
        });
    });

    // Size Interaction
    const sizeBtns = document.querySelectorAll(".size-btn:not([disabled])");

    sizeBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            sizeBtns.forEach((b) => b.classList.remove("active"));
            btn.classList.add("active");
        });
    });

    // Accordion Interaction
    const accordionBtns = document.querySelectorAll(
        ".accordion-button-custom",
    );

    accordionBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            const parent = btn.closest(".accordion-item-custom");
            const isOpen = parent.classList.contains("open");

            // Close others
            // document.querySelectorAll('.accordion-item-custom').forEach(item => item.classList.remove('open'));

            if (isOpen) {
                parent.classList.remove("open");
            } else {
                parent.classList.add("open");
            }
        });
    });
</script>
</body>
</html>

