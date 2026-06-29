<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách sản phẩm - MODA</title>
    <!-- Nhúng CSS dùng chung để đồng bộ font Outfit và layout chính -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260625">
    <!-- Nhúng CSS riêng của trang list-seller-products -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/list-seller-products.css?v=20260625">
    <!-- Tải Lucide Icons qua CDN -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <%-- NHÚNG SIDEBAR TỪ FILE TÁCH BIỆT (taskbar-seller.jsp) --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <!-- HEADER -->
            <header class="top-header">
                <div class="header-right">
                    <div class="profile-section">
                        <span class="profile-name">${not empty shop ? shop.shopName : 'ADMIN'}</span>
                        <img src="${not empty shop && not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Profile Avatar" class="profile-avatar">
                    </div>
                </div>
            </header>

            <!-- MAIN CONTENT AREA -->
            <main class="content-wrapper">
                <!-- Page Title Area -->
                <div class="dashboard-header">
                    <div class="dashboard-title-area">
                        <h1 class="page-title">Danh sách sản phẩm</h1>
                        <p class="page-subtitle">Quản lý và cập nhật danh mục hàng hóa của bạn.</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/add-product" class="btn-header-action">
                        THÊM SẢN PHẨM MỚI
                    </a>
                </div>

                <!-- Filters & Search Controls -->
                <form id="filterForm" method="get" action="${pageContext.request.contextPath}/list-seller-products">
                    <div class="filter-card">
                        <div class="filter-top-row">
                            <!-- Search Box -->
                            <div class="search-box">
                                <i data-lucide="search"></i>
                                <input type="text" name="search" id="productSearch"
                                       placeholder="TÌM THEO TÊN SẢN PHẨM HOẶC SKU..."
                                       value="${searchValue}">
                            </div>

                            <!-- Category Select -->
                            <div class="category-select-wrapper">
                                <select name="cid" id="categoryFilter" onchange="document.getElementById('filterForm').submit();">
                                    <option value="">TẤT CẢ DANH MỤC</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.categoryId}" ${cidValue != null && cidValue == cat.categoryId ? 'selected' : ''}>
                                            ${cat.categoryName}
                                        </option>
                                        <c:forEach var="child" items="${cat.listChildCategory}">
                                            <option value="${child.categoryId}" ${cidValue != null && cidValue == child.categoryId ? 'selected' : ''}>
                                                &nbsp;&nbsp;└ ${child.categoryName}
                                            </option>
                                        </c:forEach>
                                    </c:forEach>
                                </select>
                                <i data-lucide="chevron-down" class="select-arrow"></i>
                            </div>
                        </div>

                        <!-- Filter Tabs (dùng hidden input để gửi giá trị status) -->
                        <input type="hidden" name="status" id="statusInput" value="${statusValue}">
                        <div class="filter-tabs">
                            <button type="submit" class="tab-item ${statusValue == 'all' || empty statusValue ? 'active' : ''}" onclick="document.getElementById('statusInput').value='all'">TẤT CẢ</button>
                            <button type="submit" class="tab-item ${statusValue == 'instock' ? 'active' : ''}" onclick="document.getElementById('statusInput').value='instock'">CÒN HÀNG</button>
                            <button type="submit" class="tab-item ${statusValue == 'outofstock' ? 'active' : ''}" onclick="document.getElementById('statusInput').value='outofstock'">HẾT HÀNG</button>
                            <button type="submit" class="tab-item ${statusValue == 'lowstock' ? 'active' : ''}" onclick="document.getElementById('statusInput').value='lowstock'">SẮP HẾT HÀNG</button>
                        </div>
                    </div>
                </form>

                <!-- Products Table Card -->
                <div class="table-card">
                    <div class="table-responsive">
                        <table class="products-table">
                            <thead>
                                <tr>
                                    <th>HÌNH ẢNH</th>
                                    <th>TÊN SẢN PHẨM & SKU</th>
                                    <th>PHÂN LOẠI</th>
                                    <th>GIÁ</th>
                                    <th>KHO HÀNG</th>
                                    <th>TRẠNG THÁI</th>
                                    <th>THAO TÁC</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty products}">
                                        <c:forEach var="p" items="${products}">
                                            <tr class="product-row">
                                                <td>
                                                    <img src="${not empty p.thumbnailUrl ? p.thumbnailUrl : 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=150&auto=format&fit=crop&q=80'}"
                                                         alt="${p.productName}" class="product-thumb">
                                                </td>
                                                <td>
                                                    <div class="product-info-cell">
                                                        <span class="product-name-text">${p.productName}</span>
                                                        <span class="product-sku-text">SKU: PRD-${String.format("%05d", p.productId)}</span>
                                                    </div>
                                                </td>
                                                <td class="product-meta-cell">${p.provinceName}</td>
                                                <td class="product-price-cell">
                                                    <fmt:formatNumber value="${p.basePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                                </td>
                                                <td class="product-stock-cell">${p.totalStock}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${p.totalStock > 15}">
                                                            <span class="status-pill status-instock">
                                                                <span class="status-dot"></span>CÒN HÀNG
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${p.totalStock > 0}">
                                                            <span class="status-pill status-lowstock">
                                                                <span class="status-dot"></span>SẮP HẾT HÀNG
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-pill status-outofstock">
                                                                <span class="status-dot"></span>HẾT HÀNG
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <a href="${pageContext.request.contextPath}/seller/product/edit-product.jsp?id=${p.productId}" class="action-btn edit-btn" title="Chỉnh sửa">
                                                            <i data-lucide="edit-2"></i>
                                                        </a>
                                                        <a href="#" class="action-btn delete-btn" title="Xóa sản phẩm" onclick="return deleteProduct(${p.productId})">
                                                            <i data-lucide="trash-2"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="empty-state">
                                                <div class="empty-state-content">
                                                    <i data-lucide="package-open" class="empty-icon"></i>
                                                    <p class="empty-title">Chưa có sản phẩm nào</p>
                                                    <p class="empty-desc">Hãy thêm sản phẩm đầu tiên cho cửa hàng của bạn.</p>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Footer & Pagination -->
                <c:if test="${totalProducts > 0}">
                    <div class="table-footer">
                        <div class="pagination-info">
                            <%-- Tính startItem và endItem --%>
                            <c:set var="startItem" value="${(currentPage - 1) * pageSize + 1}" />
                            <c:set var="endItem" value="${currentPage * pageSize}" />
                            <c:if test="${endItem > totalProducts}">
                                <c:set var="endItem" value="${totalProducts}" />
                            </c:if>
                            HIỂN THỊ ${startItem}-${endItem} TRÊN TỔNG SỐ ${totalProducts} SẢN PHẨM
                        </div>
                        <div class="pagination-controls">
                            <%-- Nút Trang trước --%>
                            <c:choose>
                                <c:when test="${currentPage > 1}">
                                    <a href="${pageContext.request.contextPath}/list-seller-products?page=${currentPage - 1}&search=${searchValue}&status=${statusValue}&cid=${cidValue}" class="page-btn page-arrow" title="Trang trước">
                                        <i data-lucide="chevron-left"></i>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-btn page-arrow disabled"><i data-lucide="chevron-left"></i></span>
                                </c:otherwise>
                            </c:choose>

                            <%-- Các số trang --%>
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:choose>
                                    <%-- Hiển thị 3 trang đầu, trang cuối, và xung quanh trang hiện tại --%>
                                    <c:when test="${i <= 3 || i == totalPages || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                        <a href="${pageContext.request.contextPath}/list-seller-products?page=${i}&search=${searchValue}&status=${statusValue}&cid=${cidValue}"
                                           class="page-btn page-num ${i == currentPage ? 'active' : ''}">${i}</a>
                                    </c:when>
                                    <c:when test="${i == 4 && currentPage > 5}">
                                        <span class="page-dots">...</span>
                                    </c:when>
                                    <c:when test="${i == totalPages - 1 && currentPage < totalPages - 3}">
                                        <span class="page-dots">...</span>
                                    </c:when>
                                </c:choose>
                            </c:forEach>

                            <%-- Nút Trang sau --%>
                            <c:choose>
                                <c:when test="${currentPage < totalPages}">
                                    <a href="${pageContext.request.contextPath}/list-seller-products?page=${currentPage + 1}&search=${searchValue}&status=${statusValue}&cid=${cidValue}" class="page-btn page-arrow" title="Trang sau">
                                        <i data-lucide="chevron-right"></i>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-btn page-arrow disabled"><i data-lucide="chevron-right"></i></span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>
            </main>
        </div>
    </div>
</div>

<script>
    // Khởi tạo các icons của Lucide
    lucide.createIcons();

    // Tìm kiếm bằng Enter key
    document.getElementById('productSearch').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            document.getElementById('filterForm').submit();
        }
    });

    function deleteProduct(productId) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm #" + productId + " không?")) {
            // TODO: Gọi servlet xóa sản phẩm
            alert("Đã yêu cầu xóa sản phẩm: " + productId);
        }
        return false;
    }
</script>
</body>
</html>
