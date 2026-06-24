<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách sản phẩm - SELLER PORTAL</title>
    <!-- Nhúng CSS dùng chung để đồng bộ font Outfit và layout chính -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">
    <!-- Nhúng CSS riêng của trang list-seller-products -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/list-seller-products.css?v=20260611c">
    <!-- Tải Lucide Icons qua CDN để sử dụng các icon tương tự các trang trước -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <%
            // Đặt activePage là "products" để sidebar highlight mục "Quản lý sản phẩm"
            request.setAttribute("activePage", "products");
        %>
        <%-- NHÚNG SIDEBAR TỪ FILE TÁCH BIỆT --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">
            <!-- HEADER -->
            <header class="top-header">
                <div class="header-left">
                    <span class="seller-center-brand">SELLER CENTER</span>
                </div>
                <div class="header-right">
                    <div class="header-icons">
                        <button class="icon-btn" title="Thông báo">
                            <i data-lucide="bell"></i>
                            <span class="icon-badge"></span>
                        </button>
                        <button class="icon-btn" title="Trợ giúp">
                            <i data-lucide="help-circle"></i>
                        </button>
                    </div>
                    <div class="profile-section">
                        <img src="${not empty shop && not empty shop.logoUrl ? shop.logoUrl : 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'}"
                             alt="Profile Avatar" class="profile-avatar">
                        <span class="profile-name">${not empty shop ? shop.shopName : 'ADMIN'}</span>
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
                    <a href="${pageContext.request.contextPath}/seller/product/add-product.jsp" class="btn-header-action">
                        THÊM SẢN PHẨM MỚI
                    </a>
                </div>

                <!-- Filters & Search Controls -->
                <div class="filter-card">
                    <div class="filter-top-row">
                        <!-- Search Box -->
                        <div class="search-box">
                            <i data-lucide="search"></i>
                            <input type="text" id="productSearch" placeholder="TÌM THEO TÊN SẢN PHẨM HOẶC SKU..." onkeyup="filterProducts()">
                        </div>
                        
                        <!-- Category Select -->
                        <div class="category-select-wrapper">
                            <select id="categoryFilter" onchange="filterProducts()">
                                <option value="">TẤT CẢ DANH MỤC</option>
                                <option value="Thời trang">Thời trang</option>
                                <option value="Đồ gia dụng">Đồ gia dụng</option>
                                <option value="Phụ kiện">Phụ kiện</option>
                            </select>
                            <i data-lucide="chevron-down" class="select-arrow"></i>
                        </div>
                    </div>

                    <!-- Filter Tabs -->
                    <div class="filter-tabs">
                        <button class="tab-item active" onclick="switchTab(this, 'all')">TẤT CẢ</button>
                        <button class="tab-item" onclick="switchTab(this, 'instock')">CÒN HÀNG</button>
                        <button class="tab-item" onclick="switchTab(this, 'outofstock')">HẾT HÀNG</button>
                        <button class="tab-item" onclick="switchTab(this, 'lowstock')">SẮP HẾT HÀNG</button>
                    </div>
                </div>

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
                            <tbody id="productsTableBody">
                                <c:choose>
                                    <c:when test="${not empty products}">
                                        <%-- Render danh sách sản phẩm động từ Controller --%>
                                        <c:forEach var="p" items="${products}">
                                            <tr class="product-row" 
                                                data-name="${p.productName}" 
                                                data-sku="PRD-00${p.productId}"
                                                data-stock="${p.totalStock}"
                                                data-status="${p.totalStock > 15 ? 'instock' : (p.totalStock > 0 ? 'lowstock' : 'outofstock')}">
                                                <td>
                                                    <img src="${not empty p.thumbnailUrl ? p.thumbnailUrl : 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=150&auto=format&fit=crop&q=80'}" 
                                                         alt="${p.productName}" class="product-thumb">
                                                </td>
                                                <td>
                                                    <div class="product-info-cell">
                                                        <span class="product-name-text">${p.productName}</span>
                                                        <span class="product-sku-text">SKU: PRD-00${p.productId}</span>
                                                    </div>
                                                </td>
                                                <td class="product-meta-cell">Mặc định</td>
                                                <td class="product-price-cell">
                                                    <fmt:formatNumber value="${p.finalPrice}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
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
                                                        <a href="#" class="action-btn delete-btn" title="Xóa sản phẩm" onclick="deleteProduct(${p.productId})">
                                                            <i data-lucide="trash-2"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- Dữ liệu giả định giống hệt trong ảnh chụp màn hình --%>
                                        <!-- Row 1: Túi Da Minimalist Noir -->
                                        <tr class="product-row" data-name="Túi Da Minimalist Noir" data-sku="LTHR-BG-001" data-stock="45" data-status="instock" data-category="Thời trang">
                                            <td>
                                                <img src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=150&auto=format&fit=crop&q=80" alt="Túi Da Minimalist Noir" class="product-thumb">
                                            </td>
                                            <td>
                                                <div class="product-info-cell">
                                                    <span class="product-name-text">Túi Da Minimalist Noir</span>
                                                    <span class="product-sku-text">SKU: LTHR-BG-001</span>
                                                </div>
                                            </td>
                                            <td class="product-meta-cell">Đen / Large</td>
                                            <td class="product-price-cell">2.450.000đ</td>
                                            <td class="product-stock-cell">45</td>
                                            <td>
                                                <span class="status-pill status-instock">
                                                    <span class="status-dot"></span>CÒN HÀNG
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/seller/product/edit-product.jsp?sku=LTHR-BG-001" class="action-btn edit-btn" title="Chỉnh sửa">
                                                        <i data-lucide="edit-2"></i>
                                                    </a>
                                                    <a href="#" class="action-btn delete-btn" title="Xóa" onclick="deleteProduct('LTHR-BG-001')">
                                                        <i data-lucide="trash-2"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                        
                                        <!-- Row 2: Bình Gốm Kiến Trúc White -->
                                        <tr class="product-row" data-name="Bình Gốm Kiến Trúc White" data-sku="HOME-VS-042" data-stock="0" data-status="outofstock" data-category="Đồ gia dụng">
                                            <td>
                                                <img src="https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?w=150&auto=format&fit=crop&q=80" alt="Bình Gốm Kiến Trúc White" class="product-thumb">
                                            </td>
                                            <td>
                                                <div class="product-info-cell">
                                                    <span class="product-name-text">Bình Gốm Kiến Trúc White</span>
                                                    <span class="product-sku-text">SKU: HOME-VS-042</span>
                                                </div>
                                            </td>
                                            <td class="product-meta-cell">Trắng / One Size</td>
                                            <td class="product-price-cell">890.000đ</td>
                                            <td class="product-stock-cell">0</td>
                                            <td>
                                                <span class="status-pill status-outofstock">
                                                    <span class="status-dot"></span>HẾT HÀNG
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/seller/product/edit-product.jsp?sku=HOME-VS-042" class="action-btn edit-btn" title="Chỉnh sửa">
                                                        <i data-lucide="edit-2"></i>
                                                    </a>
                                                    <a href="#" class="action-btn delete-btn" title="Xóa" onclick="deleteProduct('HOME-VS-042')">
                                                        <i data-lucide="trash-2"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                        
                                        <!-- Row 3: Đồng Hồ Thép Chrono S -->
                                        <tr class="product-row" data-name="Đồng Hồ Thép Chrono S" data-sku="ACC-WT-089" data-stock="12" data-status="lowstock" data-category="Phụ kiện">
                                            <td>
                                                <img src="https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=150&auto=format&fit=crop&q=80" alt="Đồng Hồ Thép Chrono S" class="product-thumb">
                                            </td>
                                            <td>
                                                <div class="product-info-cell">
                                                    <span class="product-name-text">Đồng Hồ Thép Chrono S</span>
                                                    <span class="product-sku-text">SKU: ACC-WT-089</span>
                                                </div>
                                            </td>
                                            <td class="product-meta-cell">Bạc / Standard</td>
                                            <td class="product-price-cell">5.200.000đ</td>
                                            <td class="product-stock-cell">12</td>
                                            <td>
                                                <span class="status-pill status-lowstock">
                                                    <span class="status-dot"></span>SẮP HẾT HÀNG
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/seller/product/edit-product.jsp?sku=ACC-WT-089" class="action-btn edit-btn" title="Chỉnh sửa">
                                                        <i data-lucide="edit-2"></i>
                                                    </a>
                                                    <a href="#" class="action-btn delete-btn" title="Xóa" onclick="deleteProduct('ACC-WT-089')">
                                                        <i data-lucide="trash-2"></i>
                                                    </a>
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
                <div class="table-footer">
                    <div class="pagination-info">
                        HIỂN THỊ 1-10 TRÊN TỔNG SỐ 124 SẢN PHẨM
                    </div>
                    <div class="pagination-controls">
                        <button class="page-btn page-arrow" title="Trang trước"><i data-lucide="chevron-left"></i></button>
                        <button class="page-btn page-num active">1</button>
                        <button class="page-btn page-num">2</button>
                        <button class="page-btn page-num">3</button>
                        <span class="page-dots">...</span>
                        <button class="page-btn page-num">12</button>
                        <button class="page-btn page-arrow" title="Trang sau"><i data-lucide="chevron-right"></i></button>
                    </div>
                </div>
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
    // Khởi tạo các icons của Lucide
    lucide.createIcons();

    // Biến lưu trạng thái tab và bộ lọc hiện tại
    let currentTab = 'all';

    function filterProducts() {
        const searchVal = document.getElementById('productSearch').value.toLowerCase().trim();
        const categoryVal = document.getElementById('categoryFilter').value;
        const rows = document.querySelectorAll('.product-row');

        rows.forEach(row => {
            const name = row.getAttribute('data-name').toLowerCase();
            const sku = row.getAttribute('data-sku').toLowerCase();
            const status = row.getAttribute('data-status');
            const category = row.getAttribute('data-category') || '';

            // Kiểm tra khớp từ khóa tìm kiếm (tên hoặc SKU)
            const matchesSearch = name.includes(searchVal) || sku.includes(searchVal);
            
            // Kiểm tra khớp bộ lọc danh mục
            const matchesCategory = categoryVal === "" || category === categoryVal;

            // Kiểm tra khớp tab bộ lọc trạng thái
            const matchesTab = currentTab === 'all' || status === currentTab;

            if (matchesSearch && matchesCategory && matchesTab) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    function switchTab(button, tabName) {
        // Đổi trạng thái active cho tab button
        document.querySelectorAll('.tab-item').forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        // Gán tab hiện tại và thực hiện lọc
        currentTab = tabName;
        filterProducts();
    }

    function deleteProduct(idOrSku) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm " + idOrSku + " này không?")) {
            // Logic xử lý xóa sản phẩm (gửi request lên servlet hoặc ajax)
            alert("Đã yêu cầu xóa sản phẩm: " + idOrSku);
        }
    }
</script>
</body>
</html>
