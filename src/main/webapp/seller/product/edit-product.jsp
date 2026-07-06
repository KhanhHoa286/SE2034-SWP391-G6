<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    // Lấy thông tin sản phẩm cần chỉnh sửa
    vn.edu.fpt.model.Product product = (vn.edu.fpt.model.Product) request.getAttribute("product");

    // Nếu chưa có product (do vào trực tiếp JSP thay vì Servlet), thực hiện tự load
    if (product == null && request.getParameter("id") != null) {
        try {
            int pid = Integer.parseInt(request.getParameter("id"));
            vn.edu.fpt.dao.ProductDAO pDAO = new vn.edu.fpt.dao.ProductDAO();
            product = pDAO.getProductById(pid);
            request.setAttribute("product", product);
            request.setAttribute("productVariants", pDAO.getVariantsByProductId(pid));
            request.setAttribute("productImagesList", pDAO.getProductImagesByProductId(pid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Trích xuất danh sách ảnh phụ
    java.util.List<vn.edu.fpt.model.ProductImage> imagesList = (java.util.List<vn.edu.fpt.model.ProductImage>) request.getAttribute("productImagesList");
    String subImg1 = "";
    String subImg2 = "";
    String subImg3 = "";
    if (imagesList != null) {
        int subCount = 1;
        for (vn.edu.fpt.model.ProductImage img : imagesList) {
            if (img.getIsPrimary() == null || !img.getIsPrimary()) {
                if (subCount == 1) subImg1 = img.getImageUrl();
                else if (subCount == 2) subImg2 = img.getImageUrl();
                else if (subCount == 3) subImg3 = img.getImageUrl();
                subCount++;
            }
        }
    }
    request.setAttribute("subImg1", subImg1);
    request.setAttribute("subImg2", subImg2);
    request.setAttribute("subImg3", subImg3);

    // Dự phòng tải danh mục
    if (request.getAttribute("categories") == null) {
        vn.edu.fpt.dao.CategoryDAO categoryDAO = new vn.edu.fpt.dao.CategoryDAO();
        request.setAttribute("categories", categoryDAO.getAllCategory());
    }
    // Dự phòng tải thông tin shop
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
    // Dự phòng tải danh sách màu sắc
    if (request.getAttribute("colors") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("colors", productDAO.getAllColors());
    }
    // Dự phòng tải danh sách kích thước
    if (request.getAttribute("sizes") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("sizes", productDAO.getAllSizes());
    }
    // Dự phòng tải danh sách phần trăm giảm giá
    if (request.getAttribute("discounts") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("discounts", productDAO.getDiscountPercentages());
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa sản phẩm - MODA</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260706">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/edit-product.css?v=20260706">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="app-container">
    <div class="main-layout">
        <% request.setAttribute("activePage", "products"); %>
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
                <div class="dashboard-header-simple">
                    <h1 class="page-title">Chỉnh sửa sản phẩm</h1>
                </div>

                <!-- Form Chỉnh Sửa Sản Phẩm -->
                <form id="editProductForm" action="${pageContext.request.contextPath}/edit-product" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="productId" value="${product.productId}">
                    <div class="add-product-grid">

                        <!-- Cột Trái -->
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
                                               class="form-control"
                                               placeholder="Ví dụ: Áo sơ mi lụa cao cấp"
                                               value="${not empty product ? product.productName : ''}">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="description">MÔ TẢ SẢN PHẨM</label>
                                        <textarea id="description" name="description" required rows="6"
                                                  class="form-control text-area-control"
                                                  placeholder="Mô tả chi tiết về sản phẩm...">${not empty product ? product.description : ''}</textarea>
                                    </div>

                                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                                        <div class="form-group">
                                            <label class="form-label" for="basePrice">GIÁ BÁN (đ)</label>
                                            <input type="text" id="basePrice" name="basePrice" required
                                                   class="form-control price-input"
                                                   placeholder="Giá bán"
                                                   value="${not empty product ? product.basePrice : ''}">
                                            <input type="hidden" id="basePriceRaw" name="basePriceRaw"
                                                   value="${not empty product ? product.basePrice : ''}">
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label" for="discountPercentage">PHẦN TRĂM GIẢM GIÁ (%)</label>
                                            <div class="category-select-container">
                                                <select id="discountPercentage" name="discountPercentage" class="form-control select-control">
                                                    <c:forEach var="d" items="${discounts}">
                                                        <option value="${d}" ${product.discountPercentage == d ? 'selected' : ''}>${d}%</option>
                                                    </c:forEach>
                                                </select>
                                                <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                            </div>
                                        </div>
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
                                                    <th>SỐ LƯỢNG KHO</th>
                                                    <th></th>
                                                </tr>
                                            </thead>
                                            <tbody id="variantsBody">
                                                <c:choose>
                                                    <c:when test="${not empty productVariants}">
                                                        <c:forEach var="v" items="${productVariants}">
                                                            <tr class="variant-row">
                                                                <td>
                                                                    <select name="variantColor" class="form-control form-control-sm" required>
                                                                        <option value="">Chọn màu</option>
                                                                        <c:forEach var="c" items="${colors}">
                                                                            <option value="${c.colorName}" ${v.color.colorName == c.colorName ? 'selected' : ''}>${c.colorName}</option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </td>
                                                                <td>
                                                                    <select name="variantSize" class="form-control form-control-sm" required>
                                                                        <option value="">Chọn kích thước</option>
                                                                        <c:forEach var="s" items="${sizes}">
                                                                            <option value="${s.sizeName}" ${v.size.sizeName == s.sizeName ? 'selected' : ''}>${s.sizeName}</option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </td>
                                                                <td>
                                                                    <input type="number" name="variantStock" class="form-control form-control-sm"
                                                                           placeholder="Số lượng" min="0" required value="${v.stockQuantity}">
                                                                </td>
                                                                <td>
                                                                    <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                                                                        <i data-lucide="trash-2"></i>
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <!-- Row mặc định nếu không có biến thể -->
                                                        <tr class="variant-row">
                                                            <td>
                                                                <select name="variantColor" class="form-control form-control-sm" required>
                                                                    <option value="" selected>Chọn màu</option>
                                                                    <c:forEach var="c" items="${colors}">
                                                                        <option value="${c.colorName}">${c.colorName}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </td>
                                                            <td>
                                                                <select name="variantSize" class="form-control form-control-sm" required>
                                                                    <option value="" selected>Chọn kích thước</option>
                                                                    <c:forEach var="s" items="${sizes}">
                                                                        <option value="${s.sizeName}">${s.sizeName}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </td>
                                                            <td>
                                                                <input type="number" name="variantStock" class="form-control form-control-sm" placeholder="Số lượng" min="0" required>
                                                            </td>
                                                            <td>
                                                                <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">
                                                                    <i data-lucide="trash-2"></i>
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                    <button type="button" class="btn-add-variant" onclick="addVariantRow()">
                                        + THÊM BIẾN THỂ
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Cột Phải -->
                        <div class="grid-right-col">

                            <!-- Ảnh sản phẩm -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">03. ẢNH SẢN PHẨM</h2>
                                </div>
                                <div class="card-body">
                                    <div class="images-grid">
                                        <!-- Ô 1: Main Image -->
                                        <div class="image-slot upload-slot ${not empty product && not empty product.thumbnailUrl ? 'has-image' : ''}"
                                             onclick="document.getElementById('imgInput0').click()" id="slot0">
                                            <c:choose>
                                                <c:when test="${not empty product && not empty product.thumbnailUrl}">
                                                    <img src="${product.thumbnailUrl}" class="preview-img" alt="Ảnh chính">
                                                    <span class="img-label img-label-main">ẢNH CHÍNH</span>
                                                    <button type="button" class="btn-remove-img"
                                                            onclick="event.stopPropagation(); removeSlotImage(0)" title="Xóa ảnh">&times;</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="camera"></i>
                                                    <span>ẢNH CHÍNH</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <input type="hidden" id="keepImage0" name="keepImage0" value="${product.thumbnailUrl}">
                                        <input type="file" id="imgInput0" name="productImages0" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 0)">

                                        <!-- Ô 2: Sub Image 1 -->
                                        <div class="image-slot upload-slot ${not empty subImg1 ? 'has-image' : ''}"
                                             onclick="document.getElementById('imgInput1').click()" id="slot1">
                                            <c:choose>
                                                <c:when test="${not empty subImg1}">
                                                    <img src="${subImg1}" class="preview-img" alt="Ảnh phụ 1">
                                                    <span class="img-label img-label-sub">ẢNH PHỤ</span>
                                                    <button type="button" class="btn-remove-img"
                                                            onclick="event.stopPropagation(); removeSlotImage(1)" title="Xóa ảnh">&times;</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="image-plus"></i>
                                                    <span>ẢNH PHỤ</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <input type="hidden" id="keepImage1" name="keepImage1" value="${subImg1}">
                                        <input type="file" id="imgInput1" name="productImages1" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 1)">

                                        <!-- Ô 3: Sub Image 2 -->
                                        <div class="image-slot upload-slot ${not empty subImg2 ? 'has-image' : ''}"
                                             onclick="document.getElementById('imgInput2').click()" id="slot2">
                                            <c:choose>
                                                <c:when test="${not empty subImg2}">
                                                    <img src="${subImg2}" class="preview-img" alt="Ảnh phụ 2">
                                                    <span class="img-label img-label-sub">ẢNH PHỤ</span>
                                                    <button type="button" class="btn-remove-img"
                                                            onclick="event.stopPropagation(); removeSlotImage(2)" title="Xóa ảnh">&times;</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="image-plus"></i>
                                                    <span>ẢNH PHỤ</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <input type="hidden" id="keepImage2" name="keepImage2" value="${subImg2}">
                                        <input type="file" id="imgInput2" name="productImages2" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 2)">

                                        <!-- Ô 4: Sub Image 3 -->
                                        <div class="image-slot upload-slot ${not empty subImg3 ? 'has-image' : ''}"
                                             onclick="document.getElementById('imgInput3').click()" id="slot3">
                                            <c:choose>
                                                <c:when test="${not empty subImg3}">
                                                    <img src="${subImg3}" class="preview-img" alt="Ảnh phụ 3">
                                                    <span class="img-label img-label-sub">ẢNH PHỤ</span>
                                                    <button type="button" class="btn-remove-img"
                                                            onclick="event.stopPropagation(); removeSlotImage(3)" title="Xóa ảnh">&times;</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-lucide="image-plus"></i>
                                                    <span>ẢNH PHỤ</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <input type="hidden" id="keepImage3" name="keepImage3" value="${subImg3}">
                                        <input type="file" id="imgInput3" name="productImages3" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 3)">
                                    </div>
                                    <p class="image-help-text">
                                        Định dạng .JPG, .PNG. Tối đa 5MB mỗi ảnh. Nên sử dụng ảnh tỉ lệ 1:1.
                                    </p>
                                </div>
                            </div>

                            <!-- Danh mục & Giới tính -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">04. DANH MỤC &amp; GIỚI TÍNH</h2>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label class="form-label" for="categorySelect">DANH MỤC SẢN PHẨM</label>
                                        <div class="category-select-container">
                                            <select id="categorySelect" name="categoryId" required class="form-control select-control">
                                                <option value="" disabled>Chọn danh mục</option>
                                                <c:forEach var="cat" items="${categories}">
                                                    <option value="${cat.categoryId}" ${product.categoryId == cat.categoryId ? 'selected' : ''}>${cat.categoryName}</option>
                                                    <c:forEach var="child" items="${cat.listChildCategory}">
                                                        <option value="${child.categoryId}" ${product.categoryId == child.categoryId ? 'selected' : ''}>&nbsp;&nbsp;└ ${child.categoryName}</option>
                                                    </c:forEach>
                                                </c:forEach>
                                            </select>
                                            <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                        </div>
                                    </div>

                                    <div class="form-group margin-bottom-none">
                                        <label class="form-label" for="genderSelect">GIỚI TÍNH</label>
                                        <div class="category-select-container">
                                            <select id="genderSelect" name="gender" required class="form-control select-control">
                                                <option value="" disabled>Chọn giới tính</option>
                                                <option value="NAM" ${product.gender == 'NAM' ? 'selected' : ''}>Nam</option>
                                                <option value="NU" ${product.gender == 'NU' ? 'selected' : ''}>Nữ</option>
                                                <option value="UNISEX" ${product.gender == 'UNISEX' ? 'selected' : ''}>Unisex</option>
                                            </select>
                                            <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Footer Action Bar -->
                    <div class="form-actions-bar">
                        <div class="actions-wrapper">
                            <a href="${pageContext.request.contextPath}/list-seller-products" class="btn-action btn-cancel">
                                HỦY
                            </a>
                            <button type="submit" class="btn-action btn-save">
                                LƯU THAY ĐỔI
                            </button>
                        </div>
                    </div>
                </form>
            </main>
        </div>
    </div>
</div>

<script>
    lucide.createIcons();

    const dbColors = [
        <c:forEach var="c" items="${colors}">
            "${c.colorName}",
        </c:forEach>
    ];
    const dbSizes = [
        <c:forEach var="s" items="${sizes}">
            "${s.sizeName}",
        </c:forEach>
    ];

    /* ===== XỬ LÝ TẢI ẢNH TỪNG Ô ===== */
    function handleSlotImage(input, slotIndex) {
        const file = input.files[0];
        if (!file) return;
        if (!file.type.match('image/jpeg') && !file.type.match('image/png') && !file.type.match('image/jpg')) {
            alert('Chỉ hỗ trợ định dạng JPG, PNG.');
            input.value = '';
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            alert('Dung lượng ảnh tối đa 5MB.');
            input.value = '';
            return;
        }
        const reader = new FileReader();
        reader.onload = function(e) {
            const slot = document.getElementById('slot' + slotIndex);
            const label = (slotIndex === 0) ? 'ẢNH CHÍNH' : 'ẢNH PHỤ';
            const labelClass = (slotIndex === 0) ? 'img-label-main' : 'img-label-sub';
            slot.innerHTML =
                '<img src="' + e.target.result + '" class="preview-img" alt="' + label + '">' +
                '<span class="img-label ' + labelClass + '">' + label + '</span>' +
                '<button type="button" class="btn-remove-img" onclick="event.stopPropagation(); removeSlotImage(' + slotIndex + ')" title="Xóa ảnh">&times;</button>';
            slot.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    }

    function removeSlotImage(slotIndex) {
        const slot = document.getElementById('slot' + slotIndex);
        const input = document.getElementById('imgInput' + slotIndex);
        input.value = '';
        const keepInput = document.getElementById('keepImage' + slotIndex);
        if (keepInput) keepInput.value = '';
        if (slotIndex === 0) {
            slot.innerHTML = '<i data-lucide="camera"></i><span>ẢNH CHÍNH</span>';
        } else {
            slot.innerHTML = '<i data-lucide="image-plus"></i><span>ẢNH PHỤ</span>';
        }
        slot.classList.remove('has-image');
        lucide.createIcons();
    }

    /* ===== ĐỊNH DẠNG GIÁ BÁN ===== */
    function formatPriceDisplay(value) {
        let raw = value.replace(/[^0-9]/g, '');
        if (raw === '') return '';
        return raw.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }

    function parsePriceRaw(formattedValue) {
        return formattedValue.replace(/\./g, '');
    }

    function initPriceInput(input) {
        if (input.value) {
            input.value = formatPriceDisplay(input.value);
        }
        input.addEventListener('input', function() {
            const cursorPos = this.selectionStart;
            const oldLen = this.value.length;
            this.value = formatPriceDisplay(this.value);
            const newLen = this.value.length;
            this.setSelectionRange(cursorPos + (newLen - oldLen), cursorPos + (newLen - oldLen));
            const hiddenInput = this.parentElement.querySelector('input[name="basePriceRaw"]');
            if (hiddenInput) hiddenInput.value = parsePriceRaw(this.value);
        });
        input.addEventListener('blur', function() {
            let raw = parsePriceRaw(this.value);
            if (raw === '') return;
            let num = parseInt(raw);
            if (num > 0 && num < 1000) num = num * 1000;
            this.value = formatPriceDisplay(num.toString());
            const hiddenInput = this.parentElement.querySelector('input[name="basePriceRaw"]');
            if (hiddenInput) hiddenInput.value = num.toString();
        });
    }

    document.querySelectorAll('.price-input').forEach(initPriceInput);

    /* ===== BIẾN THỂ ===== */
    function deleteVariantRow(button) {
        const row = button.closest('.variant-row');
        const tbody = document.getElementById('variantsBody');
        if (tbody.querySelectorAll('.variant-row').length > 1) {
            row.remove();
        } else {
            alert('Sản phẩm phải có ít nhất 1 biến thể.');
        }
    }

    function addVariantRow() {
        const tbody = document.getElementById('variantsBody');
        const newRow = document.createElement('tr');
        newRow.className = 'variant-row';

        let colorOptions = '<option value="" selected>Chọn màu</option>';
        dbColors.forEach(color => { colorOptions += '<option value="' + color + '">' + color + '</option>'; });

        let sizeOptions = '<option value="" selected>Chọn kích thước</option>';
        dbSizes.forEach(size => { sizeOptions += '<option value="' + size + '">' + size + '</option>'; });

        newRow.innerHTML =
            '<td><select name="variantColor" class="form-control form-control-sm" required>' + colorOptions + '</select></td>' +
            '<td><select name="variantSize" class="form-control form-control-sm" required>' + sizeOptions + '</select></td>' +
            '<td><input type="number" name="variantStock" class="form-control form-control-sm" placeholder="Số lượng" min="0" required></td>' +
            '<td><button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa"><i data-lucide="trash-2"></i></button></td>';

        tbody.appendChild(newRow);
        lucide.createIcons();
    }

    /* ===== VALIDATE & SUBMIT ===== */
    function clearAllErrors() {
        document.querySelectorAll('.field-error').forEach(el => el.remove());
        document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
        document.querySelectorAll('.input-error-border').forEach(el => el.classList.remove('input-error-border'));
    }

    function showFieldError(element, message) {
        if (!element) return;
        element.classList.add('input-error');
        const errorSpan = document.createElement('span');
        errorSpan.className = 'field-error';
        errorSpan.textContent = message;
        const formGroup = element.closest('.form-group');
        if (formGroup) formGroup.appendChild(errorSpan);
    }

    document.getElementById('editProductForm').addEventListener('submit', function(e) {
        let isValid = true;
        clearAllErrors();

        const productName = document.getElementById('productName');
        if (!productName.value.trim()) {
            showFieldError(productName, 'Tên sản phẩm không được để trống.');
            isValid = false;
        }

        const description = document.getElementById('description');
        if (!description.value.trim()) {
            showFieldError(description, 'Mô tả sản phẩm không được để trống.');
            isValid = false;
        }

        const basePrice = document.getElementById('basePrice');
        if (!basePrice.value.trim()) {
            showFieldError(basePrice, 'Giá bán sản phẩm không được để trống.');
            isValid = false;
        }

        const categorySelect = document.getElementById('categorySelect');
        if (!categorySelect.value) {
            categorySelect.classList.add('input-error');
            isValid = false;
        }

        const genderSelect = document.getElementById('genderSelect');
        if (!genderSelect.value) {
            genderSelect.classList.add('input-error');
            isValid = false;
        }

        const rows = document.querySelectorAll('.variant-row');
        if (rows.length === 0) {
            alert('Sản phẩm phải có ít nhất 1 biến thể.');
            isValid = false;
        } else {
            rows.forEach(row => {
                const colorSelect = row.querySelector('select[name="variantColor"]');
                const sizeSelect = row.querySelector('select[name="variantSize"]');
                const stockInput = row.querySelector('input[name="variantStock"]');
                if (!colorSelect.value) { colorSelect.classList.add('input-error'); isValid = false; }
                if (!sizeSelect.value) { sizeSelect.classList.add('input-error'); isValid = false; }
                if (!stockInput.value.trim() || parseInt(stockInput.value) < 0) { stockInput.classList.add('input-error'); isValid = false; }
            });
        }

        if (!isValid) {
            e.preventDefault();
            const firstError = document.querySelector('.input-error, .input-error-border');
            if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else {
            document.querySelectorAll('.price-input').forEach(function(input) {
                const hiddenInput = input.parentElement.querySelector('input[name="basePriceRaw"]');
                if (hiddenInput) hiddenInput.value = parsePriceRaw(input.value);
            });
        }
    });
</script>
</body>
</html>
