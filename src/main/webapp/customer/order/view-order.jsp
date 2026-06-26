<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Chi tiết đơn hàng</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/view-order.css">
</head>
<body class="profile-body">

<!-- Include Header -->
<jsp:include page="/common/header.jsp" />

<div class="profile-layout">
    <!-- Sidebar -->
    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>

    <!-- Main Content -->
    <main class="profile-main">
        <div class="profile-container">
            <!-- Back to orders -->
            <a href="list-orders.jsp" class="back-link"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI LỊCH SỬ ĐƠN HÀNG</a>

            <!-- Order Header -->
            <div class="order-detail-header">
                <div class="order-title-group">
                    <h1 class="order-id">ĐƠN HÀNG #MD-882910</h1>
                    <p class="order-date">Đặt ngày 14 tháng 05, 2024</p>
                </div>
                <div class="order-status-group">
                    <span class="status-badge status-shipping">ĐANG VẬN CHUYỂN</span>
                </div>
            </div>

            <hr class="header-divider">

            <div class="order-content-row">
                <!-- Left Column: Products -->
                <div class="order-products-col">
                    <h2 class="section-title">SẢN PHẨM TRONG ĐƠN</h2>

                    <!-- Product 1 -->
                    <div class="product-card">
                        <div class="product-brand"><i class="fa-solid fa-store"></i> MODA ARCHIVE</div>
                        <div class="product-details-wrap">
                            <div class="product-img-box">
                                <div class="img-placeholder"><i class="fa-regular fa-image"></i></div>
                            </div>
                            <div class="product-info">
                                <div class="product-name-price">
                                    <h3 class="product-name">ÁO KHOÁC WOOL OVERCOAT</h3>
                                    <span class="product-price">12,500,000đ</span>
                                </div>
                                <div class="product-meta">
                                    <p>MÀU SẮC: CHARCOAL GREY</p>
                                    <p>KÍCH THƯỚC: 48 (M)</p>
                                    <p>SỐ LƯỢNG: 1</p>
                                </div>
                                <div class="product-actions">
                                    <a href="#" class="action-link">MUA LẠI</a>
                                    <a href="add-review.jsp" class="action-link">VIẾT ĐÁNH GIÁ</a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Product 2 -->
                    <div class="product-card">
                        <div class="product-brand"><i class="fa-solid fa-wand-magic-sparkles"></i> MODA STUDIO</div>
                        <div class="product-details-wrap">
                            <div class="product-img-box">
                                <div class="img-placeholder"><i class="fa-regular fa-image"></i></div>
                            </div>
                            <div class="product-info">
                                <div class="product-name-price">
                                    <h3 class="product-name">SƠ MI LỤA PREMIUM</h3>
                                    <span class="product-price">4,200,000đ</span>
                                </div>
                                <div class="product-meta">
                                    <p>MÀU SẮC: IVORY WHITE</p>
                                    <p>KÍCH THƯỚC: 39 (S)</p>
                                    <p>SỐ LƯỢNG: 1</p>
                                </div>
                                <div class="product-actions">
                                    <a href="#" class="action-link">MUA LẠI</a>
                                    <a href="add-review.jsp" class="action-link">VIẾT ĐÁNH GIÁ</a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- Right Column: Info & Summary -->
                <div class="order-info-col">

                    <!-- Delivery Info -->
                    <div class="info-card">
                        <h3 class="card-title">THÔNG TIN NHẬN HÀNG</h3>
                        <div class="info-group">
                            <label>NGƯỜI NHẬN</label>
                            <p><strong>NGUYEN VAN A</strong><br>(+84) 901 234 567</p>
                        </div>
                        <div class="info-group">
                            <label>ĐỊA CHỈ</label>
                            <p>285 Cách Mạng Tháng 8, Phường 12,<br>Quận 10, TP. Hồ Chí Minh, Việt Nam</p>
                        </div>
                    </div>

                    <!-- Payment Info -->
                    <div class="info-card">
                        <h3 class="card-title">THANH TOÁN</h3>
                        <div class="info-group">
                            <label>PHƯƠNG THỨC</label>
                            <p><i class="fa-brands fa-cc-visa"></i> Visa kết thúc bằng &bull;&bull;&bull;&bull; 4242</p>
                        </div>
                        <div class="info-group">
                            <label>TRẠNG THÁI</label>
                            <p>Đã thanh toán</p>
                        </div>
                    </div>

                    <!-- Order Summary -->
                    <div class="summary-card">
                        <h3 class="card-title">TỔNG KẾT ĐƠN HÀNG</h3>
                        <div class="summary-row">
                            <span>Tạm tính</span>
                            <span>16,700,000đ</span>
                        </div>
                        <div class="summary-row discount">
                            <span>Giảm giá (MODA10)</span>
                            <span>-1,670,000đ</span>
                        </div>
                        <hr class="summary-divider">
                        <div class="summary-row total">
                            <span>TỔNG CỘNG</span>
                            <span>15,030,000đ</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Include Footer -->
<jsp:include page="/common/footer.jsp" />

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
