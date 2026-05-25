
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
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap"
            rel="stylesheet"
    />
    <link
            href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
            rel="stylesheet"
    />
    <link rel="stylesheet" href="/assest/css/customer/list-cart-items.css" />
</head>
<body>
<!-- Announcement Bar -->
<div class="announcement-bar">
    FREESHIP ĐƠN TỪ 500K - GIẢM 10% CHO KHÁCH MỚI
</div>
<!-- Navigation Bar -->
<header class="navbar-custom">
    <a class="navbar-logo" href="#">MODA</a>
    <nav class="navbar-nav-links d-none d-lg-flex">
        <a href="#">TRANG CHỦ</a>
        <a href="#">NỮ</a>
        <a href="#">NAM</a>
        <a href="#">PHỤ KIỆN</a>
        <a class="sale-off" href="#">SALE OFF</a>
    </nav>
    <div class="navbar-utilities">
        <div class="utility-search-container d-none d-md-flex">
            <span class="material-symbols-outlined">search</span>
            <input placeholder="TÌM KIẾM..." type="text" />
        </div>
        <button class="utility-btn">
          <span class="material-symbols-outlined" style="font-size: 24px"
          >favorite</span
          >
        </button>
        <button class="utility-btn">
          <span class="material-symbols-outlined" style="font-size: 24px"
          >shopping_bag</span
          >
            <span class="cart-badge">3</span>
        </button>
        <button class="utility-btn">
          <span class="material-symbols-outlined" style="font-size: 24px"
          >account_circle</span
          >
        </button>
    </div>
</header>
<main>
    <div class="row g-5 align-items-start">
        <!-- Left Column: Cart Items -->
        <div class="col-lg-8">
            <header class="mb-5">
                <a class="back-btn" href="#">
                    <span class="material-symbols-outlined">chevron_left</span>
                    Quay lại
                </a>
                <h1 class="page-title">GIỎ HÀNG CỦA BẠN</h1>
                <p class="product-count">(3 Sản phẩm)</p>
            </header>
            <div class="cart-items-container">
                <!-- Cart Item 1 -->
                <div class="cart-item fade-in-up" style="animation-delay: 0.1s">
                    <div class="cart-item-image">
                        <img
                                alt="Tailored Wool Coat"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB_js9aZc3xVrvraXi43bOHKi7jKR58_ycx6_vlqO4B0DTmTsETTiTWRY_dxg7owOrsWMZEscPueEVzxkVbCTQXiswOORBaF7v2uDhJmlIoo4eTsss32e8Zm1K_D-QnlcVQnbJX4UDUCfotEALxzU1opFZ2nIaqYKVioKtYw5NTgt-L_XjsoFSGJNEX4qnYx6pqFojRBJCU4D8x_SMfwu3mvIxaVqI2xnbxelrTFJeQBmpsyI5gla20YmkB0vzq_V0ss3FkJWb9INs"
                        />
                    </div>
                    <div class="cart-item-info">
                        <div class="cart-item-header">
                            <div>
                                <h3 class="cart-item-title">Tailored Wool Coat</h3>
                                <p class="cart-item-meta">Màu sắc: Charcoal</p>
                                <p class="cart-item-meta">Kích cỡ: M</p>
                            </div>
                            <div class="cart-item-price">12,500,000₫</div>
                        </div>
                        <div class="cart-item-footer">
                            <div class="quantity-selector">
                                <button
                                        class="quantity-btn"
                                        onclick="this.nextElementSibling.stepDown()"
                                >
                      <span
                              class="material-symbols-outlined"
                              style="font-size: 14px"
                      >remove</span
                      >
                                </button>
                                <input
                                        class="quantity-input"
                                        min="1"
                                        readonly=""
                                        type="number"
                                        value="1"
                                />
                                <button
                                        class="quantity-btn"
                                        onclick="this.previousElementSibling.stepUp()"
                                >
                      <span
                              class="material-symbols-outlined"
                              style="font-size: 14px"
                      >add</span
                      >
                                </button>
                            </div>
                            <button class="remove-btn">
                                <span class="material-symbols-outlined">delete</span>
                                <span class="remove-label">Gỡ bỏ</span>
                            </button>
                        </div>
                    </div>
                </div>
                <!-- Cart Item 2 -->
                <div class="cart-item fade-in-up" style="animation-delay: 0.2s">
                    <div class="cart-item-image">
                        <img
                                alt="Signature T-Shirt"
                                src="https://lh3.googleusercontent.com/aida-public/AB6AXuD1rccatGnwV4ZSToHtUj-U77mPrUiSb79jA6Tx3OqEp2wnVBFYQ33J8lcTDnsBrtCQ01kBdhWxqhOhY6IcreYL62buJTJwlBTu_6WDQDR3Ppp-JJcyRsQgpd5daZ3_7oLoDmtfG9DtpketTCWbChGMhefVGbPH7m8RmGr4aZF_TY7O5glKY_iUolY9QhQOd9UL2jFcUYP7y2W1Me-RUEq8EPkqzz1FAo6nb4pzjLYMMhrkssPOeyenx6F6rvclo2pVj0csfggSkB0"
                        />
                    </div>
                    <div class="cart-item-info">
                        <div class="cart-item-header">
                            <div>
                                <h3 class="cart-item-title">Signature T-Shirt</h3>
                                <p class="cart-item-meta">Màu sắc: Optic White</p>
                                <p class="cart-item-meta">Kích cỡ: L</p>
                            </div>
                            <div class="cart-item-price">1,200,000₫</div>
                        </div>
                        <div class="cart-item-footer">
                            <div class="quantity-selector">
                                <button
                                        class="quantity-btn"
                                        onclick="this.nextElementSibling.stepDown()"
                                >
                      <span
                              class="material-symbols-outlined"
                              style="font-size: 14px"
                      >remove</span
                      >
                                </button>
                                <input
                                        class="quantity-input"
                                        min="1"
                                        readonly=""
                                        type="number"
                                        value="2"
                                />
                                <button
                                        class="quantity-btn"
                                        onclick="this.previousElementSibling.stepUp()"
                                >
                      <span
                              class="material-symbols-outlined"
                              style="font-size: 14px"
                      >add</span
                      >
                                </button>
                            </div>
                            <button class="remove-btn">
                                <span class="material-symbols-outlined">delete</span>
                                <span class="remove-label">Gỡ bỏ</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Right Column: Order Summary -->
        <div class="col-lg-4">
            <div class="sticky-top" style="top: 100px">
                <div class="summary-card">
                    <h2 class="summary-title">Tóm tắt đơn hàng</h2>
                    <div class="summary-row">
                        <span class="summary-label">Tạm tính</span>
                        <span class="summary-value">14,900,000₫</span>
                    </div>
                    <div class="summary-row">
                        <span class="summary-label">Phí vận chuyển</span>
                        <span
                                class="summary-value"
                                style="
                    color: var(--primary);
                    font-weight: 500;
                    text-transform: uppercase;
                  "
                        >Miễn phí</span
                        >
                    </div>
                    <div class="promo-container">
                        <label class="promo-label">Mã giảm giá</label>
                        <div class="promo-input-group">
                            <input
                                    class="promo-input"
                                    placeholder="VOUCHER10"
                                    type="text"
                            />
                            <button class="promo-apply-btn">Áp dụng</button>
                        </div>
                    </div>
                    <div class="total-row">
                        <span class="total-label">Tổng cộng</span>
                        <span class="total-value">14,900,000₫</span>
                    </div>
                    <button class="checkout-btn">TIẾN HÀNH THANH TOÁN</button>
                    <div class="payment-info">
                        <p class="payment-label">Phương thức thanh toán chấp nhận</p>
                        <div class="payment-icons">
                            <span class="material-symbols-outlined">credit_card</span>
                            <span class="material-symbols-outlined">payments</span>
                            <span class="material-symbols-outlined">account_balance</span>
                        </div>
                        <a class="continue-shopping" href="#">Tiếp tục mua sắm</a>
                    </div>
                </div>
                <div class="info-box">
              <span
                      class="material-symbols-outlined"
                      style="font-size: 20px; color: var(--primary)"
              >info</span
              >
                    <p>
                        Sản phẩm trong giỏ hàng không được giữ chỗ. Hãy hoàn thành thanh
                        toán để đảm bảo sở hữu sản phẩm.
                    </p>
                </div>
            </div>
        </div>
    </div>
</main>
<!-- Footer -->
<footer>
    <div class="footer-container">
        <div class="footer-brand">
            <div class="footer-logo">MODA</div>
            <p class="footer-copyright">
                © 2024 MODA ARCHIVE. ALL RIGHTS RESERVED.
            </p>
        </div>
        <div class="footer-links">
            <a href="#">Privacy Policy</a>
            <a href="#">Terms of Service</a>
            <a href="#">Shipping</a>
            <a href="#">Returns</a>
            <a href="#">Contact</a>
        </div>
    </div>
</footer>
<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Quantity adjustment micro-interaction
    document.querySelectorAll(".quantity-btn").forEach((btn) => {
        btn.addEventListener("click", function () {
            this.style.transform = "scale(0.95)";
            setTimeout(() => (this.style.transform = "scale(1)"), 100);
        });
    });
</script>
</body>
</html>
