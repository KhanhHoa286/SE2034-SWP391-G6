<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ Sơ Cửa Hàng - MODA</title>
    <!-- Nhúng CSS dùng chung -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css">
    <!-- Nhúng CSS riêng của trang view-shop -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/view-shop.css">

    <!-- CSS trực tiếp để tránh cache trình duyệt -->
    <style>
        .content-wrapper {
            max-width: 100% !important;
        }
        .shop-brand-section {
            display: flex !important;
            flex-direction: row !important;
            align-items: center !important;
            gap: 24px !important;
            padding: 24px 0 !important;
            margin-bottom: 28px !important;
            border-bottom: 1px solid #e4e4e7 !important;
        }
        .shop-avatar-wrapper {
            width: 100px !important;
            height: 100px !important;
            min-width: 100px !important;
            min-height: 100px !important;
            border-radius: 50% !important;
            border: 3px solid #e4e4e7 !important;
            overflow: hidden !important;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06) !important;
            flex-shrink: 0 !important;
            background-color: #ffffff !important;
            display: block !important;
        }
        .shop-avatar-img {
            width: 100% !important;
            height: 100% !important;
            object-fit: cover !important;
            object-position: center !important;
            display: block !important;
            border-radius: 50% !important;
        }
        .shop-meta-info {
            margin-bottom: 0 !important;
            margin-left: 0 !important;
            display: block !important;
        }
        .shop-title-name {
            margin: 0 0 8px 0 !important;
            font-family: 'Playfair Display', Georgia, serif !important;
            font-size: 28px !important;
            font-weight: 700 !important;
            color: #000000 !important;
        }
        .shop-meta-details {
            display: flex !important;
            flex-direction: row !important;
            gap: 16px !important;
        }
        @media (max-width: 768px) {
            .shop-brand-section {
                flex-direction: column !important;
                align-items: center !important;
                text-align: center !important;
            }
            .shop-meta-details {
                flex-direction: column !important;
                align-items: center !important;
            }
        }
    </style>

    <!-- Tải Lucide Icons qua CDN để sử dụng icon hiện đại và sắc nét -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <%-- NHÚNG SIDEBAR TỪ FILE TÁCH BIÊT (taskbar-seller.jsp) --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <!-- HEADER -->
            <header class="top-header">
                <div class="header-left">
                    <span class="seller-center-title"></span>
                </div>
                <div class="header-right">
                    <div class="profile-section">
                        <span class="profile-name">${shop.shopName}</span>
                        <img src="${not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Seller Profile" class="profile-avatar">
                    </div>
                </div>
            </header>

            <main class="content-wrapper">
                <!-- SHOP PROFILE TITLE & ACTIONS -->
                <div class="profile-header-row">
                    <div class="title-area">
                        <h1 class="shop-page-title">Hồ Sơ Cửa Hàng</h1>
                        <div class="subtitle-container">
                            <span class="subtitle-line"></span>
                            <span class="shop-page-subtitle">STORE PROFILE OVERVIEW</span>
                        </div>
                    </div>
                    <div class="action-area">
                        <a href="${pageContext.request.contextPath}/edit-shop" class="btn-edit-profile">
                            CHỈNH SỬA HỒ SƠ
                        </a>
                    </div>
                </div>

                <!-- SHOP BRAND INFO -->
                <div class="shop-brand-section">
                    <div class="shop-avatar-wrapper">
                        <img src="${not empty shop.logoUrl ? shop.logoUrl : 'https://via.placeholder.com/130'}" alt="${shop.shopName}" class="shop-avatar-img">
                    </div>
                    <div class="shop-meta-info">
                        <h2 class="shop-title-name">${shop.shopName}</h2>
                        <div class="shop-meta-details">
                            <span class="meta-item">
                                <i data-lucide="calendar"></i>
                                Tham gia: ${joinedDate}
                            </span>
                            <span class="meta-item">
                                <i data-lucide="map-pin"></i>
                                ${shop.streetAddress}, ${shop.ward.name}, ${shop.ward.province.name}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- ABOUT SECTION - FULL WIDTH -->
                <div class="about-card">
                    <div>
                        <h3 class="card-section-title">GIỚI THIỆU CỬA HÀNG</h3>
                        <p class="about-description">
                            ${not empty shop.description ? shop.description : 'Chưa có mô tả cửa hàng.'}
                        </p>
                    </div>
                    <div class="about-contact-row">
                        <div class="contact-item">
                            <span class="contact-label">SỐ ĐIỆN THOẠI</span>
                            <span class="contact-value">${not empty shop.owner.phone ? shop.owner.phone : 'Chưa cập nhật'}</span>
                        </div>
                        <div class="contact-item">
                            <span class="contact-label">EMAIL LIÊN HỆ</span>
                            <span class="contact-value">${not empty shop.owner.email ? shop.owner.email : 'Chưa cập nhật'}</span>
                        </div>
                    </div>
                </div>

                <!-- STATS ROW -->
                <div class="stats-row">
                    <!-- Products Count -->
                    <div class="stat-card">
                        <span class="stat-label">SẢN PHẨM</span>
                        <div class="stat-value-container">
                            <span class="stat-value">${activeProductsCount}</span>
                        </div>
                        <span class="stat-desc">ĐANG HOẠT ĐỘNG</span>
                    </div>
                </div>
            </main>

            <!-- FOOTER -->
            <footer class="profile-footer">
                <div class="footer-left">
                    <span>© MODA — ${shop.shopName} PROFILE</span>
                </div>
            </footer>
        </div>
    </div>
</div>

<script>
    // Initialize Lucide icons
    lucide.createIcons();
</script>
</body>
</html>
