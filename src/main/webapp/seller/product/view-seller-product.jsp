<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm - MODA Seller Center</title>
    <!-- Nhúng CSS dùng chung (seller.css) -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707a">
    <!-- Nhúng CSS riêng trang view-seller-product -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/view-seller-product.css?v=20260707a">
    <!-- Lucide Icons CDN -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <%-- NHÚNG SIDEBAR TỪ FILE TÁCH BIỆT (taskbar-seller.jsp) --%>
        <%
            request.setAttribute("activePage", "products");
        %>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <!-- HEADER -->
            <header class="top-header">
                <div class="header-right">
                    <div class="profile-section">
                        <span class="profile-name">${not empty shop ? shop.shopName : 'Seller Center'}</span>
                        <img src="${not empty shop && not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Profile Avatar" class="profile-avatar">
                    </div>
                </div>
            </header>

            <!-- MAIN CONTENT AREA -->
            <main class="content-wrapper">

                <!-- Breadcrumbs -->
                <div class="view-breadcrumbs">
                    <a href="${pageContext.request.contextPath}/list-seller-products">Quản lý sản phẩm</a>
                    <span class="bc-separator">/</span>
                    <span class="bc-current">Chi tiết sản phẩm</span>
                </div>

                <!-- Page Header: Tên sản phẩm + Nút chỉnh sửa -->
                <div class="view-product-header">
                    <h1 class="view-product-title">${product.productName}</h1>
                    <a href="${pageContext.request.contextPath}/edit-product?id=${product.productId}" class="btn-edit-product">
                        <i data-lucide="edit-2"></i>
                        Chỉnh sửa
                    </a>
                </div>

                <!-- ===== GRID 2 CỘT: Ảnh + Thông tin ===== -->
                <div class="view-product-grid">

                    <!-- ========== CỘT TRÁI: Ảnh + Phân loại ========== -->
                    <div class="view-left-column">

                        <!-- Card Ảnh sản phẩm -->
                        <div class="view-image-card">
                            <!-- Ảnh chính lớn -->
                            <div class="view-main-image-wrapper">
                                <%-- Badge trạng thái tồn kho --%>
                                <c:set var="computedTotalStock" value="0" />
                                <c:if test="${not empty productVariants}">
                                    <c:forEach var="v" items="${productVariants}">
                                        <c:set var="computedTotalStock" value="${computedTotalStock + v.stockQuantity}" />
                                    </c:forEach>
                                </c:if>

                                <c:choose>
                                    <c:when test="${computedTotalStock > 15}">
                                        <span class="view-stock-badge stock-badge-instock">Còn hàng</span>
                                    </c:when>
                                    <c:when test="${computedTotalStock > 0}">
                                        <span class="view-stock-badge stock-badge-lowstock">Sắp hết hàng</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="view-stock-badge stock-badge-outofstock">Hết hàng</span>
                                    </c:otherwise>
                                </c:choose>

                                <img id="mainProductImage"
                                     src="${not empty productImagesList && not empty productImagesList[0].imageUrl ? productImagesList[0].imageUrl : product.thumbnailUrl}"
                                     alt="${product.productName}"
                                     class="view-main-image">
                            </div>

                            <!-- Hàng thumbnails nhỏ -->
                            <c:if test="${not empty productImagesList && productImagesList.size() > 1}">
                                <div class="view-thumbnails-row">
                                    <c:forEach var="img" items="${productImagesList}" varStatus="idx">
                                        <c:if test="${idx.index < 4}">
                                            <div class="view-thumb-item ${idx.index == 0 ? 'active' : ''}"
                                                 onclick="changeMainImage('${img.imageUrl}', this)">
                                                <img src="${img.imageUrl}" alt="Ảnh ${idx.index + 1}">
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${productImagesList.size() > 4}">
                                        <div class="view-thumb-more">
                                            +${productImagesList.size() - 4}
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>

                        <!-- Card Phân loại hàng (Màu sắc + Kích thước) -->
                        <c:if test="${not empty productVariants}">
                            <div class="view-variants-card">
                                <div class="view-card-header">
                                    <h3 class="view-card-title">Phân loại hàng</h3>
                                </div>
                                <div class="view-card-body">
                                    <%-- Nhóm MÀU SẮC (lấy unique colors) --%>
                                    <div class="variant-group">
                                        <span class="variant-group-label">Màu sắc</span>
                                        <div class="variant-options" id="colorOptions">
                                            <%-- Sẽ render bằng JS để lọc unique --%>
                                        </div>
                                    </div>

                                    <%-- Nhóm KÍCH THƯỚC (lấy unique sizes) --%>
                                    <div class="variant-group">
                                        <span class="variant-group-label">Kích thước</span>
                                        <div class="variant-options" id="sizeOptions">
                                            <%-- Sẽ render bằng JS để lọc unique --%>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- ========== CỘT PHẢI: Thông tin + Mô tả ========== -->
                    <div class="view-right-column">

                        <!-- Card Thông tin chung (4 metrics ngang) -->
                        <div class="view-info-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Thông tin chung</h3>
                            </div>
                            <div class="view-info-metrics">
                                <!-- Giá bán -->
                                <div class="view-metric-item">
                                    <span class="view-metric-label">Giá bán</span>
                                    <span class="view-metric-value price-value">
                                        <fmt:formatNumber value="${product.basePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                        <c:if test="${product.discountPercentage != null && product.discountPercentage > 0}">
                                            <span class="discount-badge">-${product.discountPercentage}%</span>
                                        </c:if>
                                    </span>
                                    <c:if test="${product.discountPercentage != null && product.discountPercentage > 0}">
                                        <span class="original-price">
                                            Giá sau giảm: <fmt:formatNumber value="${product.discountedPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                        </span>
                                    </c:if>
                                </div>

                                <!-- Kho hàng -->
                                <div class="view-metric-item">
                                    <span class="view-metric-label">Kho hàng</span>
                                    <span class="view-metric-value">${computedTotalStock}</span>
                                </div>

                                <!-- Danh mục -->
                                <div class="view-metric-item">
                                    <span class="view-metric-label">Danh mục</span>
                                    <span class="view-metric-value" style="font-size: 14px; font-weight: 700;">
                                        ${not empty product.category ? product.category.categoryName : 'Chưa phân loại'}
                                    </span>
                                </div>

                                <!-- Lượt xem (nếu có) -->
                                <div class="view-metric-item">
                                    <span class="view-metric-label">Trạng thái</span>
                                    <span class="view-metric-value" style="font-size: 14px;">
                                        <c:choose>
                                            <c:when test="${product.isActive == true}">
                                                <span class="view-status-pill view-status-active">
                                                    <span class="s-dot"></span>Đang hoạt động
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="view-status-pill view-status-inactive">
                                                    <span class="s-dot"></span>Ngừng bán
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Card Mô tả sản phẩm -->
                        <div class="view-description-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Mô tả sản phẩm</h3>
                            </div>
                            <div class="view-description-body">
                                <div class="view-description-text">
                                    ${product.description}
                                </div>
                            </div>
                        </div>

                        <!-- Card Bảng biến thể chi tiết -->
                        <c:if test="${not empty productVariants}">
                            <div class="view-variant-table-card">
                                <div class="view-card-header">
                                    <h3 class="view-card-title">Chi tiết biến thể (${productVariants.size()} biến thể)</h3>
                                </div>
                                <div class="view-variant-table-wrap">
                                    <table class="view-variant-table">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Màu sắc</th>
                                                <th>Kích thước</th>
                                                <th>Tồn kho</th>
                                                <th>Giá</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="v" items="${productVariants}" varStatus="vIdx">
                                                <tr>
                                                    <td style="font-weight: 700; color: var(--color-text-tertiary);">${vIdx.index + 1}</td>
                                                    <td>
                                                        <div class="variant-color-cell">
                                                            <span class="color-dot" style="background-color: ${not empty v.color && not empty v.color.colorCode ? v.color.colorCode : '#cccccc'};"></span>
                                                            ${not empty v.color ? v.color.colorName : 'N/A'}
                                                        </div>
                                                    </td>
                                                    <td style="font-weight: 700;">${not empty v.size ? v.size.sizeName : 'N/A'}</td>
                                                    <td>
                                                        <span class="stock-text ${v.stockQuantity == 0 ? 'stock-zero' : ''}">
                                                            ${v.stockQuantity}
                                                        </span>
                                                    </td>
                                                    <td style="font-weight: 700;">
                                                        <fmt:formatNumber value="${not empty v.price ? v.price : product.basePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:if>

                        <!-- Card Thông tin bổ sung -->
                        <div class="view-meta-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Thông tin bổ sung</h3>
                            </div>
                            <div class="view-meta-list">
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Mã sản phẩm</span>
                                    <span class="view-meta-value">PRD-${String.format("%05d", product.productId)}</span>
                                </div>
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Giới tính</span>
                                    <span class="view-meta-value">
                                        <c:choose>
                                            <c:when test="${product.gender == 'MALE'}">Nam</c:when>
                                            <c:when test="${product.gender == 'FEMALE'}">Nữ</c:when>
                                            <c:otherwise>Unisex</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <c:if test="${product.discountPercentage != null && product.discountPercentage > 0}">
                                    <div class="view-meta-row">
                                        <span class="view-meta-label">Giảm giá</span>
                                        <span class="view-meta-value">${product.discountPercentage}%</span>
                                    </div>
                                </c:if>
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Ngày tạo</span>
                                    <span class="view-meta-value">${not empty formattedCreatedAt ? formattedCreatedAt : 'Chưa có thông tin'}</span>
                                </div>
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Trạng thái</span>
                                    <span class="view-meta-value">
                                        <c:choose>
                                            <c:when test="${product.status == 'APPROVED'}">
                                                <span class="view-status-pill view-status-active">
                                                    <span class="s-dot"></span>Đã duyệt
                                                </span>
                                            </c:when>
                                            <c:when test="${product.status == 'PENDING'}">
                                                <span class="view-status-pill view-status-pending">
                                                    <span class="s-dot"></span>Chờ duyệt
                                                </span>
                                            </c:when>
                                            <c:when test="${product.status == 'REJECTED'}">
                                                <span class="view-status-pill view-status-inactive">
                                                    <span class="s-dot"></span>Bị từ chối
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="view-status-pill view-status-active">
                                                    <span class="s-dot"></span>${product.status}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <div class="view-product-footer">
                    <p class="footer-text">&copy; 2024 MODA Marketplace Seller Center. All Rights Reserved.</p>
                </div>

            </main>
        </div>
    </div>
</div>

<script>
    // Khởi tạo Lucide Icons
    lucide.createIcons();

    // === Chuyển ảnh chính khi click thumbnail ===
    function changeMainImage(imageUrl, thumbElement) {
        // Đổi ảnh chính
        document.getElementById('mainProductImage').src = imageUrl;

        // Cập nhật trạng thái active cho thumbnail
        document.querySelectorAll('.view-thumb-item').forEach(function(el) {
            el.classList.remove('active');
        });
        if (thumbElement) {
            thumbElement.classList.add('active');
        }
    }

    // === Render Unique Colors & Sizes từ variants ===
    (function() {
        var colorContainer = document.getElementById('colorOptions');
        var sizeContainer = document.getElementById('sizeOptions');
        if (!colorContainer || !sizeContainer) return;

        // Tập hợp unique colors và sizes từ variant table
        var colorMap = {};
        var sizeMap = {};

        // Đọc dữ liệu từ bảng variant
        var variantRows = document.querySelectorAll('.view-variant-table tbody tr');
        variantRows.forEach(function(row) {
            var cells = row.querySelectorAll('td');
            if (cells.length >= 3) {
                // Color: cell[1] chứa .variant-color-cell
                var colorCell = cells[1];
                var colorDot = colorCell.querySelector('.color-dot');
                var colorHex = colorDot ? colorDot.style.backgroundColor : '#cccccc';
                var colorName = colorCell.textContent.trim().replace(/\s+/g, ' ');
                // Remove the color dot text artifact
                if (colorDot) {
                    colorName = colorCell.textContent.replace(colorDot.textContent, '').trim();
                }
                if (colorName && colorName !== 'N/A') {
                    colorMap[colorName] = colorHex;
                }

                // Size: cell[2]
                var sizeName = cells[2].textContent.trim();
                if (sizeName && sizeName !== 'N/A') {
                    sizeMap[sizeName] = true;
                }
            }
        });

        // Render colors
        Object.keys(colorMap).forEach(function(name) {
            var swatch = document.createElement('div');
            swatch.className = 'variant-color-swatch';
            swatch.innerHTML = '<span class="color-dot" style="background-color:' + colorMap[name] + ';"></span>' + name;
            colorContainer.appendChild(swatch);
        });

        // Render sizes
        Object.keys(sizeMap).forEach(function(name) {
            var chip = document.createElement('div');
            chip.className = 'variant-size-chip';
            chip.textContent = name;
            sizeContainer.appendChild(chip);
        });

        // Fallback nếu không có dữ liệu
        if (Object.keys(colorMap).length === 0) {
            colorContainer.innerHTML = '<span style="font-size:12px;color:var(--color-text-tertiary);">Không có thông tin màu sắc</span>';
        }
        if (Object.keys(sizeMap).length === 0) {
            sizeContainer.innerHTML = '<span style="font-size:12px;color:var(--color-text-tertiary);">Không có thông tin kích thước</span>';
        }
    })();
</script>
</body>
</html>
