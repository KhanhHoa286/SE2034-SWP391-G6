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
                        <h1 class="shop-page-title">Hồ sơ cửa hàng</h1>
                        <div class="subtitle-container">
                            <span class="subtitle-line"></span>
                            <span class="shop-page-subtitle">TỔNG QUAN HỒ SƠ CỬA HÀNG</span>
                        </div>
                    </div>
                    <div class="action-area">
                        <a href="${pageContext.request.contextPath}/edit-shop" class="btn-edit-profile">
                            CHỈNH SỬA HỒ SƠ
                        </a>
                    </div>
                </div>

                <!-- SHOP BRAND SECTION (AVATAR ON THE LEFT, NAME & DETAILS ON THE RIGHT) -->
                <div class="shop-brand-section">
                    <div class="shop-avatar-wrapper">
                        <img src="${not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Shop Logo" class="shop-avatar-img">
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

                <!-- GIỚI THIỆU CỬA HÀNG -->
                <div class="about-card">
                    <div>
                        <h3 class="card-section-title">GIỚI THIỆU CỬA HÀNG</h3>
                        <p class="about-description">
                            ${not empty shop.description ? shop.description : 'Atelier Luxe là biểu tượng của sự tinh tế và tối giản trong thời trang cao cấp. Chúng tôi tập trung vào những thiết kế có cấu trúc rõ ràng, chất liệu thượng hạng và bảng màu monochrome bất hủ. Mỗi sản phẩm tại Atelier Luxe không chỉ là trang phục, mà là một tác phẩm kiến trúc dành cho cơ thể, tôn vinh vẻ đẹp và sự sang trọng thầm lặng của người mặc hiện đại.'}
                        </p>
                    </div>
                    <div class="about-contact-row">
                        <div class="contact-item">
                            <span class="contact-label">SỐ ĐIỆN THOẠI</span>
                            <span class="contact-value">${not empty shop.owner.phone ? shop.owner.phone : '+33 1 23 45 67 89'}</span>
                        </div>
                        <div class="contact-item">
                            <span class="contact-label">EMAIL LIÊN HỆ</span>
                            <span class="contact-value">${not empty shop.owner.email ? shop.owner.email : 'concierge@atelierluxe.com'}</span>
                        </div>
                    </div>
                </div>

                <!-- SẢN PHẨM -->
                <div class="stats-row">
                    <div class="stat-card">
                        <span class="stat-label">SẢN PHẨM</span>
                        <div class="stat-value-container">
                            <span class="stat-value">${activeProductsCount}</span>
                            <span class="stat-subtext">sản phẩm</span>
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
