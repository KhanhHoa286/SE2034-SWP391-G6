<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    @SuppressWarnings("unchecked")
    java.util.Map<String, String> errors = (java.util.Map<String, String>) request.getAttribute("errors");
    @SuppressWarnings("unchecked")
    java.util.Map<String, String> oldInput = (java.util.Map<String, String>) request.getAttribute("oldInput");

    java.util.function.Function<String, String> old = key ->
            (oldInput != null && oldInput.get(key) != null) ? oldInput.get(key) : "";

    java.util.function.Function<String, String> err = key ->
            (errors != null && errors.get(key) != null) ? errors.get(key) : "";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm sản phẩm mới - MODA</title>
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
    // Dự phòng tải danh sách màu sắc nếu không đi qua Servlet
    if (request.getAttribute("colors") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("colors", productDAO.getAllColors());
    }
    // Dự phòng tải danh sách kích thước nếu không đi qua Servlet
    if (request.getAttribute("sizes") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("sizes", productDAO.getAllSizes());
    }
    // Dự phòng tải danh sách phần trăm giảm giá nếu không đi qua Servlet
    if (request.getAttribute("discounts") == null) {
        vn.edu.fpt.dao.ProductDAO productDAO = new vn.edu.fpt.dao.ProductDAO();
        request.setAttribute("discounts", productDAO.getDiscountPercentages());
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
                <!-- Page Title -->
                <div class="dashboard-header-simple">
                    <h1 class="page-title">Thêm sản phẩm mới</h1>
                </div>

                <!-- Form Thêm Sản Phẩm -->
                <form id="addProductForm" action="${pageContext.request.contextPath}/add-product" method="post" enctype="multipart/form-data">
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
                                               class="form-control <%= !err.apply("productName").isEmpty() ? "input-error" : "" %>"
                                               placeholder="Ví dụ: Áo sơ mi lụa cao cấp"
                                               value="<%= old.apply("productName") %>">
                                        <% if (!err.apply("productName").isEmpty()) { %>
                                            <span class="field-error"><%= err.apply("productName") %></span>
                                        <% } %>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="description">MÔ TẢ SẢN PHẨM</label>
                                        <textarea id="description" name="description" required rows="6"
                                                  class="form-control text-area-control <%= !err.apply("description").isEmpty() ? "input-error" : "" %>"
                                                  placeholder="Mô tả chi tiết về sản phẩm, chất liệu, kiểu dáng..."><%= old.apply("description") %></textarea>
                                        <% if (!err.apply("description").isEmpty()) { %>
                                            <span class="field-error"><%= err.apply("description") %></span>
                                        <% } %>
                                    </div>
                                    
                                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                                        <div class="form-group">
                                            <label class="form-label" for="basePrice">GIÁ BÁN (đ)</label>
                                            <input type="text" id="basePrice" name="basePrice" required
                                                   class="form-control price-input <%= !err.apply("basePrice").isEmpty() ? "input-error" : "" %>"
                                                   placeholder="Giá bán"
                                                   value="<%= old.apply("basePriceRaw") %>">
                                            <input type="hidden" id="basePriceRaw" name="basePriceRaw" value="<%= old.apply("basePriceRaw") %>">
                                            <% if (!err.apply("basePrice").isEmpty()) { %>
                                                <span class="field-error"><%= err.apply("basePrice") %></span>
                                            <% } %>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label" for="discountPercentage">PHẦN TRĂM GIẢM GIÁ (%)</label>
                                            <div class="category-select-container">
                                                <select id="discountPercentage" name="discountPercentage" class="form-control select-control">
                                                    <c:forEach var="d" items="${discounts}">
                                                        <option value="${d}" ${oldInput.discountPercentage == d ? 'selected' : ''}>${d}%</option>
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
                                                <!-- Row 1 mặc định -->
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
                                    <h2 class="card-title">03. ẢNH SẢN PHẨM</h2>
                                </div>
                                <div class="card-body">
                                    <div class="images-grid">
                                        <!-- Ô 1: Main Product Image -->
                                        <div class="image-slot upload-slot" onclick="document.getElementById('imgInput0').click()" id="slot0">
                                            <i data-lucide="camera"></i>
                                            <span>ẢNH CHÍNH</span>
                                        </div>
                                        <input type="file" id="imgInput0" name="productImages" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 0)">

                                        <!-- Ô 2: Sub Image 1 -->
                                        <div class="image-slot upload-slot" onclick="document.getElementById('imgInput1').click()" id="slot1">
                                            <i data-lucide="image-plus"></i>
                                            <span>ẢNH PHỤ</span>
                                        </div>
                                        <input type="file" id="imgInput1" name="productImages" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 1)">

                                        <!-- Ô 3: Sub Image 2 -->
                                        <div class="image-slot upload-slot" onclick="document.getElementById('imgInput2').click()" id="slot2">
                                            <i data-lucide="image-plus"></i>
                                            <span>ẢNH PHỤ</span>
                                        </div>
                                        <input type="file" id="imgInput2" name="productImages" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 2)">

                                        <!-- Ô 4: Sub Image 3 -->
                                        <div class="image-slot upload-slot" onclick="document.getElementById('imgInput3').click()" id="slot3">
                                            <i data-lucide="image-plus"></i>
                                            <span>ẢNH PHỤ</span>
                                        </div>
                                        <input type="file" id="imgInput3" name="productImages" accept="image/jpeg,image/png" style="display:none;" onchange="handleSlotImage(this, 3)">
                                    </div>
                                    <p class="image-help-text">
                                        Định dạng .JPG, .PNG. Tối đa 5MB mỗi ảnh. Nên sử dụng ảnh tỉ lệ 1:1.
                                    </p>
                                    <% if (!err.apply("images").isEmpty()) { %>
                                        <span class="field-error" style="margin-top: 8px;"><%= err.apply("images") %></span>
                                    <% } %>
                                </div>
                            </div>

                            <!-- Danh mục & Giới tính -->
                            <div class="form-card">
                                <div class="card-header">
                                    <h2 class="card-title">04. DANH MỤC & GIỚI TÍNH</h2>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label class="form-label" for="categorySelect">DANH MỤC SẢN PHẨM</label>
                                        <div class="category-select-container">
                                            <select id="categorySelect" name="categoryId" required class="form-control select-control <%= !err.apply("categoryId").isEmpty() ? "input-error" : "" %>">
                                                <option value="" disabled selected>Chọn danh mục</option>
                                                <c:forEach var="cat" items="${categories}">
                                                    <option value="${cat.categoryId}" ${oldInput.categoryId == cat.categoryId ? 'selected' : ''}>${cat.categoryName}</option>
                                                    <c:forEach var="child" items="${cat.listChildCategory}">
                                                        <option value="${child.categoryId}" ${oldInput.categoryId == child.categoryId ? 'selected' : ''}>&nbsp;&nbsp;└ ${child.categoryName}</option>
                                                    </c:forEach>
                                                </c:forEach>
                                            </select>
                                            <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                        </div>
                                        <% if (!err.apply("categoryId").isEmpty()) { %>
                                            <span class="field-error" style="margin-top: 8px;"><%= err.apply("categoryId") %></span>
                                        <% } %>
                                    </div>

                                    <div class="form-group margin-bottom-none">
                                        <label class="form-label" for="genderSelect">GIỚI TÍNH</label>
                                        <div class="category-select-container">
                                            <select id="genderSelect" name="gender" required class="form-control select-control <%= !err.apply("gender").isEmpty() ? "input-error" : "" %>">
                                                <option value="" disabled selected>Chọn giới tính</option>
                                                <option value="NAM" ${oldInput.gender == 'NAM' ? 'selected' : ''}>Nam</option>
                                                <option value="NU" ${oldInput.gender == 'NU' ? 'selected' : ''}>Nữ</option>
                                                <option value="UNISEX" ${oldInput.gender == 'UNISEX' ? 'selected' : ''}>Unisex</option>
                                            </select>
                                            <i data-lucide="chevron-down" class="select-arrow-icon"></i>
                                        </div>
                                        <% if (!err.apply("gender").isEmpty()) { %>
                                            <span class="field-error" style="margin-top: 8px;"><%= err.apply("gender") %></span>
                                        <% } %>
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

    // Lưu các danh sách màu và size từ JSTL sang JS arrays để dùng cho addVariantRow
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

    /* ===== XỬ LÝ TẢI ẢNH TỪNG Ô RIÊNG BIỆT ===== */
    function handleSlotImage(input, slotIndex) {
        const file = input.files[0];
        if (!file) return;

        // Kiểm tra định dạng
        if (!file.type.match('image/jpeg') && !file.type.match('image/png') && !file.type.match('image/jpg')) {
            alert('Chỉ hỗ trợ định dạng JPG, PNG.');
            input.value = '';
            return;
        }
        // Kiểm tra dung lượng
        if (file.size > 5 * 1024 * 1024) {
            alert('Dung lượng ảnh tối đa 5MB.');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            const slot = document.getElementById('slot' + slotIndex);
            const label = (slotIndex === 0) ? 'ẢNH CHÍNH' : 'ẢNH PHỤ ' + slotIndex;
            const labelClass = (slotIndex === 0) ? 'img-label-main' : 'img-label-sub';

            // Dung string concatenation de tranh JSP hieu lam template literal
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

        if (slotIndex === 0) {
            slot.innerHTML = '<i data-lucide="camera"></i><span>ẢNH CHÍNH</span>';
        } else {
            slot.innerHTML = '<i data-lucide="image-plus"></i><span>ẢNH PHỤ</span>';
        }
        slot.classList.remove('has-image');
        lucide.createIcons();
    }

    /* ===== XỬ LÝ ĐỊNH DẠNG GIÁ BÁN (.000) ===== */
    function formatPriceDisplay(value) {
        // Loại bỏ tất cả ký tự không phải số
        let raw = value.replace(/[^0-9]/g, '');
        if (raw === '') return '';
        // Thêm dấu chấm phân cách hàng nghìn
        return raw.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }

    function parsePriceRaw(formattedValue) {
        // Trả về giá trị số thuần (bỏ dấu chấm)
        return formattedValue.replace(/\./g, '');
    }

    function initPriceInput(input) {
        // format initial value if present
        if (input.value) {
            input.value = formatPriceDisplay(input.value);
        }

        input.addEventListener('input', function() {
            const cursorPos = this.selectionStart;
            const oldLen = this.value.length;
            this.value = formatPriceDisplay(this.value);
            const newLen = this.value.length;
            // Giữ con trỏ đúng vị trí
            this.setSelectionRange(cursorPos + (newLen - oldLen), cursorPos + (newLen - oldLen));

            // Cập nhật giá trị ẩn (raw) cho hidden input
            const hiddenInput = this.parentElement.querySelector('input[name="variantPriceRaw"], input[name="basePriceRaw"]');
            if (hiddenInput) {
                hiddenInput.value = parsePriceRaw(this.value);
            }
        });

        // Khi blur: nếu giá trị < 1000, tự động nhân 1000 (ví dụ: 200 -> 200.000)
        input.addEventListener('blur', function() {
            let raw = parsePriceRaw(this.value);
            if (raw === '') return;
            let num = parseInt(raw);
            if (num > 0 && num < 1000) {
                num = num * 1000;
            }
            this.value = formatPriceDisplay(num.toString());
            const hiddenInput = this.parentElement.querySelector('input[name="variantPriceRaw"], input[name="basePriceRaw"]');
            if (hiddenInput) {
                hiddenInput.value = num.toString();
            }
        });
    }

    // Khởi tạo price input cho các dòng có sẵn
    document.querySelectorAll('.price-input').forEach(initPriceInput);

    /* ===== XỬ LÝ BIẾN THỂ ===== */
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

        // Tạo chuỗi HTML cho option màu
        let colorOptions = '<option value="" selected>Chọn màu</option>';
        dbColors.forEach(color => {
            colorOptions += '<option value="' + color + '">' + color + '</option>';
        });

        // Tạo chuỗi HTML cho option size
        let sizeOptions = '<option value="" selected>Chọn kích thước</option>';
        dbSizes.forEach(size => {
            sizeOptions += '<option value="' + size + '">' + size + '</option>';
        });

        newRow.innerHTML =
            '<td>' +
            '    <select name="variantColor" class="form-control form-control-sm" required>' +
            colorOptions +
            '    </select>' +
            '</td>' +
            '<td>' +
            '    <select name="variantSize" class="form-control form-control-sm" required>' +
            sizeOptions +
            '    </select>' +
            '</td>' +
            '<td>' +
            '    <input type="number" name="variantStock" class="form-control form-control-sm" placeholder="Số lượng" min="0" required>' +
            '</td>' +
            '<td>' +
            '    <button type="button" class="btn-delete-row" onclick="deleteVariantRow(this)" title="Xóa">' +
            '        <i data-lucide="trash-2"></i>' +
            '    </button>' +
            '</td>';

        tbody.appendChild(newRow);
        lucide.createIcons();
    }

    /* ===== TRƯỚC KHI SUBMIT: đồng bộ giá trị thực vào hidden inputs & VALIDATE ===== */
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
        errorSpan.style.marginTop = '4px';

        const formGroup = element.closest('.form-group');
        if (formGroup) {
            formGroup.appendChild(errorSpan);
        }
    }

    function showImageError(message) {
        const grid = document.querySelector('.images-grid');
        grid.classList.add('input-error-border');

        const errorSpan = document.createElement('span');
        errorSpan.className = 'field-error';
        errorSpan.textContent = message;
        errorSpan.style.marginTop = '8px';

        const helpText = document.querySelector('.image-help-text');
        if (helpText) {
            helpText.parentNode.insertBefore(errorSpan, helpText.nextSibling);
        }
    }

    function showCategoryError(message) {
        const select = document.getElementById('categorySelect');
        select.classList.add('input-error');

        const errorSpan = document.createElement('span');
        errorSpan.className = 'field-error';
        errorSpan.textContent = message;
        errorSpan.style.marginTop = '8px';

        const formGroup = select.closest('.form-group');
        if (formGroup) {
            formGroup.appendChild(errorSpan);
        }
    }

    function showGenderError(message) {
        const select = document.getElementById('genderSelect');
        select.classList.add('input-error');

        const errorSpan = document.createElement('span');
        errorSpan.className = 'field-error';
        errorSpan.textContent = message;
        errorSpan.style.marginTop = '8px';

        const formGroup = select.closest('.form-group');
        if (formGroup) {
            formGroup.appendChild(errorSpan);
        }
    }

    document.getElementById('addProductForm').addEventListener('submit', function(e) {
        let isValid = true;
        clearAllErrors();

        // 1. Validate Product Name
        const productName = document.getElementById('productName');
        if (!productName.value.trim()) {
            showFieldError(productName, 'Tên sản phẩm không được để trống.');
            isValid = false;
        }

        // 2. Validate Description
        const description = document.getElementById('description');
        if (!description.value.trim()) {
            showFieldError(description, 'Mô tả sản phẩm không được để trống.');
            isValid = false;
        }

        // 2.5 Validate Base Price
        const basePrice = document.getElementById('basePrice');
        if (!basePrice.value.trim()) {
            showFieldError(basePrice, 'Giá bán sản phẩm không được để trống.');
            isValid = false;
        }

        // 3. Validate Main Image (slot0)
        const slot0 = document.getElementById('slot0');
        if (!slot0.classList.contains('has-image')) {
            showImageError('Vui lòng tải lên ít nhất ảnh chính của sản phẩm.');
            isValid = false;
        }

        // 4. Validate Category
        const categorySelect = document.getElementById('categorySelect');
        if (!categorySelect.value) {
            showCategoryError('Vui lòng chọn danh mục sản phẩm.');
            isValid = false;
        }

        // 4.5 Validate Gender
        const genderSelect = document.getElementById('genderSelect');
        if (!genderSelect.value) {
            showGenderError('Vui lòng chọn giới tính.');
            isValid = false;
        }

        // 5. Validate Variants
        const rows = document.querySelectorAll('.variant-row');
        if (rows.length === 0) {
            alert('Sản phẩm phải có ít nhất 1 biến thể.');
            isValid = false;
        } else {
            rows.forEach((row, index) => {
                const colorSelect = row.querySelector('select[name="variantColor"]');
                const sizeSelect = row.querySelector('select[name="variantSize"]');
                const stockInput = row.querySelector('input[name="variantStock"]');

                if (!colorSelect.value) {
                    colorSelect.classList.add('input-error');
                    isValid = false;
                }
                if (!sizeSelect.value) {
                    sizeSelect.classList.add('input-error');
                    isValid = false;
                }
                if (!stockInput.value.trim() || parseInt(stockInput.value) < 0) {
                    stockInput.classList.add('input-error');
                    isValid = false;
                }
            });
        }

        if (!isValid) {
            e.preventDefault();
            // Scroll to the first error
            const firstError = document.querySelector('.input-error, .input-error-border');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        } else {
            // Đồng bộ giá trị price raw
            document.querySelectorAll('.price-input').forEach(function(input) {
                const hiddenInput = input.parentElement.querySelector('input[name="variantPriceRaw"], input[name="basePriceRaw"]');
                if (hiddenInput) {
                    hiddenInput.value = parsePriceRaw(input.value);
                }
            });
        }
    });
</script>
</body>
</html>
