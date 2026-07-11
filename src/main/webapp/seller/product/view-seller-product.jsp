<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm - MODA Seller Center</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260707b">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/view-seller-product.css?v=20260708b">
    <script src="https://unpkg.com/lucide@latest" defer></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <% request.setAttribute("activePage", "products"); %>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <header class="top-header">
                <div class="header-right">
                    <div class="profile-section">
                        <span class="profile-name">
                            <c:out value="${not empty shop ? shop.shopName : 'Seller Center'}"/>
                        </span>
                        <img src="${not empty shop && not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Seller avatar" class="profile-avatar">
                    </div>
                </div>
            </header>

            <main class="content-wrapper">
                <div class="view-breadcrumbs">
                    <a href="${pageContext.request.contextPath}/list-seller-products">Quản lý sản phẩm</a>
                    <span class="bc-separator">/</span>
                    <span class="bc-current">Chi tiết sản phẩm</span>
                </div>

                <div class="view-product-header">
                    <h1 class="view-product-title"><c:out value="${product.productName}"/></h1>
                    <a href="${pageContext.request.contextPath}/edit-product?id=${product.productId}" class="btn-edit-product">
                        <i data-lucide="edit-2"></i>
                        Chỉnh sửa
                    </a>
                </div>

                <div class="view-product-grid">
                    <div class="view-left-column">
                        <div class="view-image-card">
                            <div class="view-main-image-wrapper">
                                <span class="view-stock-badge ${stockStatusClass}">
                                    <c:out value="${stockStatusText}"/>
                                </span>

                                <c:choose>
                                    <c:when test="${hasMainProductImage}">
                                        <img id="mainProductImage"
                                             src="${mainProductImageUrl}"
                                             alt="${product.productName}"
                                             class="view-main-image">
                                    </c:when>
                                    <c:otherwise>
                                        <div id="mainProductImage" class="view-empty-image">
                                            <i data-lucide="image"></i>
                                            <span>Chưa có ảnh sản phẩm</span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <c:if test="${not empty productImagesList && productImageCount > 1}">
                                <div class="view-thumbnails-row">
                                    <c:forEach var="img" items="${productImagesList}" varStatus="idx">
                                        <c:if test="${idx.index < 4 && not empty img.imageUrl}">
                                            <button type="button"
                                                    class="view-thumb-item ${idx.index == 0 ? 'active' : ''}"
                                                    data-image-url="<c:out value='${img.imageUrl}'/>"
                                                    aria-label="Xem ảnh sản phẩm ${idx.index + 1}">
                                                <img src="${img.imageUrl}" alt="Ảnh sản phẩm ${idx.index + 1}">
                                            </button>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${extraImageCount > 0}">
                                        <div class="view-thumb-more">+${extraImageCount}</div>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>

                        <div class="view-meta-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Thông tin bổ sung</h3>
                            </div>
                            <div class="view-meta-list">
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Mã sản phẩm</span>
                                    <span class="view-meta-value"><c:out value="${formattedProductCode}"/></span>
                                </div>
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Giới tính</span>
                                    <span class="view-meta-value"><c:out value="${genderText}"/></span>
                                </div>
                                <div class="view-meta-row">
                                    <span class="view-meta-label">Ngày tạo</span>
                                    <span class="view-meta-value"><c:out value="${formattedCreatedAt}"/></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="view-right-column">
                        <div class="view-info-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Thông tin chung</h3>
                            </div>
                            <div class="view-info-metrics">
                                <div class="view-metric-item">
                                    <span class="view-metric-label">Giá bán</span>
                                    <c:choose>
                                        <c:when test="${product.discountPercentage != null && product.discountPercentage > 0}">
                                            <span class="original-price">
                                                <fmt:formatNumber value="${product.basePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                            </span>
                                            <span class="discount-badge">-${product.discountPercentage}%</span>
                                            <span class="view-metric-value price-value sale-price">
                                                <fmt:formatNumber value="${discountedPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="view-metric-value price-value">
                                                <fmt:formatNumber value="${product.basePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="view-metric-item">
                                    <span class="view-metric-label">Kho hàng</span>
                                    <span class="view-metric-value">${totalStock}</span>
                                </div>

                                <div class="view-metric-item">
                                    <span class="view-metric-label">Danh mục</span>
                                    <span class="view-metric-value view-metric-small">
                                        <c:out value="${categoryName}"/>
                                    </span>
                                </div>

                                <div class="view-metric-item">
                                    <span class="view-metric-label">Trạng thái</span>
                                    <span class="view-metric-value view-metric-small">
                                        <span class="view-status-pill view-status-plain ${activeClass}">
                                            <span class="s-dot"></span>
                                            <c:out value="${activeText}"/>
                                        </span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div class="view-description-card">
                            <div class="view-card-header">
                                <h3 class="view-card-title">Mô tả sản phẩm</h3>
                            </div>
                            <div class="view-description-body">
                                <div class="view-description-text">
                                    <c:choose>
                                        <c:when test="${not empty product.description}">
                                            <c:out value="${product.description}"/>
                                        </c:when>
                                        <c:otherwise>Chưa có mô tả sản phẩm.</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty productVariants}">
                                <div class="view-variant-table-card">
                                    <div class="view-card-header">
                                        <h3 class="view-card-title">Chi tiết biến thể (${productVariantCount} biến thể)</h3>
                                    </div>
                                    <div class="view-variant-table-wrap">
                                        <table class="view-variant-table">
                                            <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Màu sắc</th>
                                                <th>Kích thước</th>
                                                <th>Tồn kho</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="v" items="${productVariants}" varStatus="vIdx">
                                                <tr>
                                                    <td class="view-row-index">${vIdx.index + 1}</td>
                                                    <td>
                                                        <div class="variant-color-cell">
                                                            <span class="color-dot"
                                                                  style="background-color: ${not empty v.color && not empty v.color.colorCode ? v.color.colorCode : '#cccccc'};"></span>
                                                            <span><c:out value="${not empty v.color ? v.color.colorName : 'N/A'}"/></span>
                                                        </div>
                                                    </td>
                                                    <td class="view-variant-size">
                                                        <c:out value="${not empty v.size ? v.size.sizeName : 'N/A'}"/>
                                                    </td>
                                                    <td>
                                                        <span class="stock-text ${v.stockQuantity == 0 ? 'stock-zero' : ''}">
                                                            <c:out value="${v.stockQuantity != null ? v.stockQuantity : 0}"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="view-description-card">
                                    <div class="view-card-header">
                                        <h3 class="view-card-title">Chi tiết biến thể</h3>
                                    </div>
                                    <div class="view-description-body">
                                        <div class="view-description-text">Sản phẩm chưa có biến thể.</div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        if (window.lucide && typeof window.lucide.createIcons === 'function') {
            window.lucide.createIcons();
        }

        document.querySelectorAll('.view-thumb-item').forEach(function (button) {
            button.addEventListener('click', function () {
                var imageUrl = button.getAttribute('data-image-url');
                var mainImage = document.getElementById('mainProductImage');
                if (!imageUrl || !mainImage || mainImage.tagName !== 'IMG') return;

                mainImage.src = imageUrl;
                document.querySelectorAll('.view-thumb-item').forEach(function (item) {
                    item.classList.remove('active');
                });
                button.classList.add('active');
            });
        });

        renderVariantOptions();
    });

    function renderVariantOptions() {
        var colorContainer = document.getElementById('colorOptions');
        var sizeContainer = document.getElementById('sizeOptions');
        if (!colorContainer || !sizeContainer) return;

        var colorMap = {};
        var sizeMap = {};
        var variantRows = document.querySelectorAll('.view-variant-table tbody tr');

        variantRows.forEach(function (row) {
            var colorNameNode = row.querySelector('.variant-color-cell span:last-child');
            var colorDot = row.querySelector('.variant-color-cell .color-dot');
            var sizeCell = row.querySelector('.view-variant-size');

            if (colorNameNode) {
                var colorName = colorNameNode.textContent.trim();
                if (colorName && colorName !== 'N/A') {
                    colorMap[colorName] = colorDot ? colorDot.style.backgroundColor : '#cccccc';
                }
            }

            if (sizeCell) {
                var sizeName = sizeCell.textContent.trim();
                if (sizeName && sizeName !== 'N/A') {
                    sizeMap[sizeName] = true;
                }
            }
        });

        Object.keys(colorMap).forEach(function (name) {
            var swatch = document.createElement('div');
            swatch.className = 'variant-color-swatch';

            var dot = document.createElement('span');
            dot.className = 'color-dot';
            dot.style.backgroundColor = colorMap[name];

            var label = document.createElement('span');
            label.textContent = name;

            swatch.appendChild(dot);
            swatch.appendChild(label);
            colorContainer.appendChild(swatch);
        });

        Object.keys(sizeMap).forEach(function (name) {
            var chip = document.createElement('div');
            chip.className = 'variant-size-chip';
            chip.textContent = name;
            sizeContainer.appendChild(chip);
        });

        if (Object.keys(colorMap).length === 0) {
            colorContainer.innerHTML = '<span class="view-empty-inline">Không có thông tin màu sắc</span>';
        }
        if (Object.keys(sizeMap).length === 0) {
            sizeContainer.innerHTML = '<span class="view-empty-inline">Không có thông tin kích thước</span>';
        }
    }
</script>
</body>
</html>
