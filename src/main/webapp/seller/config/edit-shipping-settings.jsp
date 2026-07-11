<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cài đặt Giao hàng - MODA</title>
    <!-- Nhúng CSS dùng chung để đồng bộ font và layout -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css">
    <!-- Nhúng CSS riêng của trang edit-shipping-settings -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/edit-shipping-settings.css">
    <!-- Tải Lucide Icons qua CDN để sử dụng các icon hiện đại -->
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.js"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <%-- NHÚNG SIDEBAR TỪ FILE TÁCH BIỆT (taskbar-seller.jsp) --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <!-- HEADER -->
            <header class="top-header">
                <div class="header-left">
                    <span class="seller-center-brand">SELLER CENTER</span>
                </div>
                <div class="header-right">
                    <div class="profile-section">
                        <img src="${not empty shop && not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Admin Profile" class="profile-avatar">
                        <span class="profile-name">${not empty shop ? shop.shopName : 'ADMIN'}</span>
                    </div>
                </div>
            </header>

            <!-- MAIN CONTENT AREA -->
            <main class="content-wrapper">
                <!-- Page Title Section -->
                <div class="page-header-section">
                    <h1 class="page-title">Cài đặt Giao hàng</h1>
                    <p class="page-subtitle">Quản lý các đơn vị vận chuyển và phương thức giao hàng cho cửa hàng của bạn.</p>
                </div>

                <!-- Shipping Carrier Form Card -->
                <form action="${pageContext.request.contextPath}/edit-shipping-settings" method="POST">
                    <div class="shipping-card">
                        <div class="shipping-card-header">
                            <h2>ĐƠN VỊ VẬN CHUYỂN (SHIPPING CARRIERS)</h2>
                        </div>
                        
                        <div class="shipping-carriers-grid">
                            <!-- Carrier 1: GHN -->
                            <div class="carrier-item-card">
                                <div class="carrier-top-row">
                                    <div class="carrier-logo-container ghn-logo">
                                        <!-- GHN Minimalist Diamond Logo SVG -->
                                        <svg viewBox="0 0 100 100" class="carrier-svg-logo">
                                            <rect width="100" height="100" fill="#000000"/>
                                            <polygon points="50,25 75,50 50,75 25,50" fill="none" stroke="#ffffff" stroke-width="5"/>
                                            <polygon points="50,35 65,50 50,65 35,50" fill="#ffffff"/>
                                        </svg>
                                    </div>
                                    <div class="carrier-status-toggle">
                                        <span class="status-label">TRẠNG THÁI</span>
                                        <label class="square-switch">
                                            <input type="checkbox" name="ghn_active" value="true" checked>
                                            <span class="square-slider"></span>
                                        </label>
                                    </div>
                                </div>
                                <div class="carrier-details">
                                    <h3 class="carrier-title">GIAO HÀNG NHANH (GHN)</h3>
                                    <p class="carrier-description">Dịch vụ vận chuyển toàn quốc với tốc độ nhanh và mạng lưới bưu cục rộng khắp.</p>
                                </div>
                                <div class="carrier-meta-row">
                                    <span class="meta-label">THỜI GIAN TRUNG BÌNH.</span>
                                    <span class="meta-value">2-3 Ngày</span>
                                </div>
                            </div>

                            <!-- Carrier 2: Viettel Post -->
                            <div class="carrier-item-card">
                                <div class="carrier-top-row">
                                    <div class="carrier-logo-container viettel-logo">
                                        <!-- Viettel Post Globe SVG -->
                                        <svg viewBox="0 0 100 100" class="carrier-svg-logo">
                                            <rect width="100" height="100" fill="#f4f4f5"/>
                                            <circle cx="50" cy="50" r="30" fill="none" stroke="#52525b" stroke-width="4"/>
                                            <ellipse cx="50" cy="50" rx="30" ry="12" fill="none" stroke="#52525b" stroke-width="3"/>
                                            <ellipse cx="50" cy="50" rx="12" ry="30" fill="none" stroke="#52525b" stroke-width="3"/>
                                            <line x1="20" y1="50" x2="80" y2="50" stroke="#52525b" stroke-width="3"/>
                                        </svg>
                                    </div>
                                    <div class="carrier-status-toggle">
                                        <span class="status-label">TRẠNG THÁI</span>
                                        <label class="square-switch">
                                            <input type="checkbox" name="viettel_active" value="true">
                                            <span class="square-slider"></span>
                                        </label>
                                    </div>
                                </div>
                                <div class="carrier-details">
                                    <h3 class="carrier-title">VIETTEL POST</h3>
                                    <p class="carrier-description">Đơn vị vận chuyển uy tín từ tập đoàn Viettel, hỗ trợ giao hàng tận vùng sâu vùng xa.</p>
                                </div>
                                <div class="carrier-meta-row">
                                    <span class="meta-label">THỜI GIAN TRUNG BÌNH.</span>
                                    <span class="meta-value">3-5 Ngày</span>
                                </div>
                            </div>

                            <!-- Carrier 3: Ninja Van -->
                            <div class="carrier-item-card">
                                <div class="carrier-top-row">
                                    <div class="carrier-logo-container ninjavan-logo">
                                        <!-- Ninja Van Mask SVG -->
                                        <svg viewBox="0 0 100 100" class="carrier-svg-logo">
                                            <rect width="100" height="100" fill="#e4e4e7"/>
                                            <rect x="25" y="35" width="50" height="30" rx="8" fill="#18181b"/>
                                            <ellipse cx="40" cy="50" rx="6" ry="3" fill="#ffffff"/>
                                            <circle cx="40" cy="50" r="2" fill="#18181b"/>
                                            <ellipse cx="60" cy="50" rx="6" ry="3" fill="#ffffff"/>
                                            <circle cx="60" cy="50" r="2" fill="#18181b"/>
                                        </svg>
                                    </div>
                                    <div class="carrier-status-toggle">
                                        <span class="status-label">TRẠNG THÁI</span>
                                        <label class="square-switch">
                                            <input type="checkbox" name="ninjavan_active" value="true" checked>
                                            <span class="square-slider"></span>
                                        </label>
                                    </div>
                                </div>
                                <div class="carrier-details">
                                    <h3 class="carrier-title">NINJA VAN</h3>
                                    <p class="carrier-description">Công nghệ vận chuyển tối ưu, tích hợp theo dõi đơn hàng thời gian thực.</p>
                                </div>
                                <div class="carrier-meta-row">
                                    <span class="meta-label">THỜI GIAN TRUNG BÌNH.</span>
                                    <span class="meta-value">1-3 Ngày</span>
                                </div>
                            </div>

                            <!-- Carrier 4: J&T Express -->
                            <div class="carrier-item-card">
                                <div class="carrier-top-row">
                                    <div class="carrier-logo-container jtexpress-logo">
                                        <!-- J&T Express SVG -->
                                        <svg viewBox="0 0 100 100" class="carrier-svg-logo">
                                            <rect width="100" height="100" fill="#ffffff" stroke="#e4e4e7" stroke-width="2"/>
                                            <path d="M 30,35 H 70 V 45 H 55 V 65 H 45 V 45 H 30 Z" fill="#ef4444"/>
                                            <circle cx="70" cy="60" r="6" fill="#ef4444"/>
                                        </svg>
                                    </div>
                                    <div class="carrier-status-toggle">
                                        <span class="status-label">TRẠNG THÁI</span>
                                        <label class="square-switch">
                                            <input type="checkbox" name="jt_active" value="true" checked>
                                            <span class="square-slider"></span>
                                        </label>
                                    </div>
                                </div>
                                <div class="carrier-details">
                                    <h3 class="carrier-title">J&T EXPRESS</h3>
                                    <p class="carrier-description">Dịch vụ chuyển phát nhanh phủ sóng 63 tỉnh thành, làm việc cả ngày lễ.</p>
                                </div>
                                <div class="carrier-meta-row">
                                    <span class="meta-label">THỜI GIAN TRUNG BÌNH.</span>
                                    <span class="meta-value">2-4 Ngày</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons Section -->
                    <div class="actions-section">
                        <a href="${pageContext.request.contextPath}/sellerDashboard" class="back-link">
                            <i data-lucide="arrow-left"></i>
                            <span>QUAY LẠI</span>
                        </a>
                        <div class="btn-group">
                            <button type="button" onclick="window.location.reload();" class="btn-cancel">HỦY BỎ</button>
                            <button type="submit" class="btn-save">LƯU THAY ĐỔI</button>
                        </div>
                    </div>
                </form>
            </main>

            <!-- FOOTER -->
            <footer class="profile-footer">
                <div class="footer-left">
                    <span>© 2024 SELLER PORTAL ADMIN</span>
                </div>
            </footer>
        </div>
    </div>
</div>

<script>
    // Khởi tạo Lucide Icons để hiển thị icon
    lucide.createIcons();
</script>
</body>
</html>
