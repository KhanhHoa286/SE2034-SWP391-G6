<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm sản phẩm mới - SELLER PORTAL</title>
    <!-- Nhúng CSS dùng chung để đồng bộ font Outfit và layout chính -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260628">
    <!-- Nhúng CSS riêng của trang add-product -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/add-product.css?v=20260628">
    <!-- Tải Lucide Icons qua CDN -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<%
    // Dự phòng tải danh mục nếu không đi qua Servlet
    if (request.getAttribute("categories") == null) {
        vn.edu.fpt.dao.CategoryDAO categoryDAO = new vn.edu.fpt.dao.CategoryDAO();
        request.setAttribute("categories", categoryDAO.getAllCategory());
    }
    // Dự phòng tải thông tin shop nếu không đi qua Servlet
    if (request.getAttribute("shop") == null) {
        jakarta.servlet.http.HttpSession sess = request.getSession();
        vn.edu.fpt.model.User account = (vn.edu.fpt.model.User) sess.getAttribute("account");
        vn.edu.fpt.dao.ShopDAO shopDAO = new vn.edu.fpt.dao.ShopDAO();
        vn.edu.fpt.model.Shop shop = null;
        if (account != null) {
            shop = shopDAO.getShopByOwnerId(account.getUserId());
        }
        if (shop == null) {
            java.util.List<vn.edu.fpt.model.Shop> allShops = shopDAO.getAllShops();
            if (allShops != null && !allShops.isEmpty()) {
                shop = allShops.get(0);
            }
        }
        request.setAttribute("shop", shop);
    }
%>

<div class="app-container">
    <div class="main-layout">
        <%-- Đặt activePage là "products" để sidebar highlight mục "Quản lý sản phẩm" --%>
        <% request.setAttribute("activePage", "products"); %>
        
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
                <!-- Page Title -->
                <div class="dashboard-header-simple">
                    <h1 class="page-title">Thêm sản phẩm mới</h1>
                </div>

                <!-- Form Thêm Sản Phẩm -->
                <form id="addProductForm" action="${pageContext.request.contextPath}/seller/product/add" method="post" enctype="multipart/form-data">
                    <div class="add-product-grid">
                        
                        <!-- Cột Trái (Thông tin sản phẩm & Biến thể) -->
                        <div class="grid-left-col">
                            
                            <!-- 01. Thông tin cơ bản -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">01. THÔNG TIN CƠ BẢN</h2>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label class="form-label" for="productName">TÊN SẢN PHẨM</label>
                                        <input type="text" id="productName" name="productName" required
                                               class="form-control" placeholder="Ví dụ: Áo sơ mi lụa cao cấp">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="description">MÔ TẢ SẢN PHẨM</label>
                                        <textarea id="description" name="description" required rows="6"
                                                  class="form-control text-area-control" 
                                                  placeholder="Mô tả chi tiết về sản phẩm, chất liệu, kiểu dáng..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- 02. Thông tin biến thể -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">02. THÔNG TIN BIẾN THỂ</h2>
                                </div>
                                <div class="card-body">
                                    <div class="variants-table-wrapper">
                                        <table class="variants-table">
                                            <thead>
                                                <tr>
                                                    <th>MÀU SẮC</th>
                                                    <th>KÍCH THƯỚC</th>
                                                    <th>GIÁ BÁN (đ)</th>
                                                    <th>SỐ LƯỢNG KHO</th>
                                                    <th></th>
                                                </tr>
                                            </thead>
                                            <tbody id="variantsBody">
                                                <!-- Row 1 mặc định -->
                                                <tr class="variant-row">
                                                    <td>
                                                        <input type="text" name="variantColor" class="form-control form-control-sm" placeholder="Đen" required>
                                                    </td>
                                                    <td>
                                                        <input type="text" name="variantSize" class="form-control form-control-sm" placeholder="S" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantPrice" class="form-control form-control-sm" placeholder="250000" min="0" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantStock" class="form-control form-control-sm" placeholder="10" min="0" required>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                                                            <i data-lucide="trash-2"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <!-- Row 2 mặc định -->
                                                <tr class="variant-row">
                                                    <td>
                                                        <input type="text" name="variantColor" class="form-control form-control-sm" value="Đen" required>
                                                    </td>
                                                    <td>
                                                        <input type="text" name="variantSize" class="form-control form-control-sm" value="M" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantPrice" class="form-control form-control-sm" value="250000" min="0" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantStock" class="form-control form-control-sm" value="15" min="0" required>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                                                            <i data-lucide="trash-2"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <!-- Row 3 mặc định -->
                                                <tr class="variant-row">
                                                    <td>
                                                        <input type="text" name="variantColor" class="form-control form-control-sm" value="Trắng" required>
                                                    </td>
                                                    <td>
                                                        <input type="text" name="variantSize" class="form-control form-control-sm" value="S" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantPrice" class="form-control form-control-sm" value="250000" min="0" required>
                                                    </td>
                                                    <td>
                                                        <input type="number" name="variantStock" class="form-control form-control-sm" value="8" min="0" required>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                                                            <i data-lucide="trash-2"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <button type="button" class="btn-add-variant" onclick="addVariantRow()">
                                        + THÊM BIẾN THỂ
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Cột Phải (Ảnh sản phẩm & Danh mục) -->
                        <div class="grid-right-col">
                            
                            <!-- Ảnh sản phẩm -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">ẢNH SẢN PHẨM</h2>
                                </div>
                                <div class="card-body">
                                    <div class="images-grid">
                                        <!-- Ô Tải Ảnh -->
                                        <div class="image-slot upload-slot active" onclick="triggerFileInput()" id="uploadSlot">
                                            <i data-lucide="camera"></i>
                                            <span>TẢI ẢNH</span>
                                        </div>
                                        <!-- 3 Ô Preview rỗng tiếp theo -->
                                        <div class="image-slot preview-slot" id="preview1">
                                            <i data-lucide="image" class="placeholder-icon"></i>
                                        </div>
                                        <div class="image-slot preview-slot" id="preview2">
                                            <i data-lucide="image" class="placeholder-icon"></i>
                                        </div>
                                        <div class="image-slot preview-slot" id="preview3">
                                            <i data-lucide="image" class="placeholder-icon"></i>
                                        </div>
                                    </div>
                                    <p class="image-help-text">
                                        Định dạng .JPG, .PNG. Tối đa 5MB mỗi ảnh. Nên sử dụng ảnh tỉ lệ 1:1.
                                    </p>
                                    <!-- Input File ẩn để người dùng chọn ảnh -->
                                    <input type="file" id="productImages" name="productImages" accept="image/*" multiple
                                           style="display: none;" onchange="handleImageSelection(this)">
                                </div>
                            </div>

                            <!-- Danh mục -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">DANH MỤC</h2>
                                </div>
                                <div class="card-body">
                                    <div class="form-group margin-bottom-none">
                                        <label class="form-label" for="categorySelect">DANH MỤC SẢN PHẨM</label>
                                        <div class="category-select-container">
                                            <select id="categorySelect" name="categoryId" required class="form-control select-control">
                                                <option value="" disabled selected>Chọn danh mục</option>
                                                <c:forEach var="cat" items="${categories}">
                                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                                    <c:forEach var="child" items="${cat.listChildCategory}">
                                                        <option value="${child.categoryId}">&nbsp;&nbsp;└ ${child.categoryName}</option>
                                                    </c:forEach>
                                                </c:forEach>
                                            </select>
                                            <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- Footer Action Bar cố định dưới màn hình -->
                    <div class="form-actions-bar">
                        <div class="actions-wrapper">
                            <a href="${pageContext.request.contextPath}/list-seller-products" class="btn-action btn-cancel">
                                HỦY
                            </a>
                            <button type="submit" class="btn-action btn-save">
                                LƯU SẢN PHẨM
                            </button>
                        </div>
                    </div>
                </form>
            </main>
        </div>
    </div>
</div>

<script>
    // Khởi tạo Lucide Icons
    lucide.createIcons();

    // Trigger input file click
    function triggerFileInput() {
        document.getElementById('productImages').click();
    }

    // Xử lý khi tải nhiều ảnh lên và hiển thị preview
    function handleImageSelection(input) {
        const files = input.files;
        if (!files) return;

        // Cho phép tối đa hiển thị 4 ảnh bao gồm cả ảnh chính ở ô đầu tiên
        let limit = Math.min(files.length, 4);

        for (let i = 0; i < limit; i++) {
            const file = files[i];
            const reader = new FileReader();

            reader.onload = function(e) {
                if (i === 0) {
                    // Đặt ảnh chính vào ô Upload Slot
                    const uploadSlot = document.getElementById('uploadSlot');
                    uploadSlot.innerHTML = `<img src="${e.target.result}" class="preview-img" alt="Main Product Image">`;
                    uploadSlot.classList.remove('active');
                } else {
                    // Đặt các ảnh phụ vào 3 ô preview kế tiếp
                    const previewSlot = document.getElementById('preview' + i);
                    if (previewSlot) {
                        previewSlot.innerHTML = `<img src="${e.target.result}" class="preview-img" alt="Sub Image ${i}">`;
                        previewSlot.classList.add('has-image');
                    }
                }
            };
            reader.readAsDataURL(file);
        }
    }

    // Xóa dòng biến thể trong bảng
    function deleteVariantRow(button) {
        const row = button.closest('.variant-row');
        const tbody = document.getElementById('variantsBody');
        
        // Đảm bảo giữ lại ít nhất 1 dòng
        if (tbody.querySelectorAll('.variant-row').length > 1) {
            row.remove();
        } else {
            alert("Sản phẩm phải có ít nhất 1 biến thể.");
        }
    }

    // Thêm dòng biến thể mới
    function addVariantRow() {
        const tbody = document.getElementById('variantsBody');
        const newRow = document.createElement('tr');
        newRow.className = 'variant-row';
        newRow.innerHTML = `
            <td>
                <input type="text" name="variantColor" class="form-control form-control-sm" placeholder="Màu sắc" required>
            </td>
            <td>
                <input type="text" name="variantSize" class="form-control form-control-sm" placeholder="Kích thước" required>
            </td>
            <td>
                <input type="number" name="variantPrice" class="form-control form-control-sm" placeholder="Giá bán" min="0" required>
            </td>
            <td>
                <input type="number" name="variantStock" class="form-control form-control-sm" placeholder="Số lượng" min="0" required>
            </td>
            <td>
                <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                    <i data-lucide="trash-2"></i>
                </button>
            </td>
        `;
        tbody.appendChild(newRow);
        
        // Cập nhật lại Lucide icons cho hàng vừa sinh ra
        lucide.createIcons();
    }
</script>
</body>
</html>
