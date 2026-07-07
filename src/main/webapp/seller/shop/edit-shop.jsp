<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="vn.edu.fpt.model.Province" %>
<%@ page import="vn.edu.fpt.model.Ward" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa hồ sơ cửa hàng - MODA</title>

    <%-- CSS dùng chung cho toàn bộ seller center --%>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">
    <%-- CSS riêng cho trang edit-shop --%>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/assets/css/seller/edit-shop.css?v=20260611c">

    <!-- Tránh cache trình duyệt cho tiêu đề trang -->
    <style>
        .page-title {
            font-family: 'Outfit', Arial, sans-serif !important;
            font-size: 36px !important;
            font-weight: 700 !important;
            color: #000000 !important;
            line-height: 1.1 !important;
            margin: 0 0 6px 0 !important;
        }
    </style>

    <%-- Lucide Icons --%>
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<%
    @SuppressWarnings("unchecked")
    Map<String, String> errors  = (Map<String, String>) request.getAttribute("errors");
    @SuppressWarnings("unchecked")
    Map<String, String> oldInput = (Map<String, String>) request.getAttribute("oldInput");

    java.util.function.Function<String, String> old = key ->
            (oldInput != null && oldInput.get(key) != null) ? oldInput.get(key) : "";
    java.util.function.Function<String, String> err = key ->
            (errors  != null && errors.get(key)  != null) ? errors.get(key)  : "";

    // Pre-populate from Shop attribute set by servlet
    vn.edu.fpt.model.Shop shop =
            (vn.edu.fpt.model.Shop) request.getAttribute("shop");

    String currentShopName   = (shop != null && shop.getShopName()     != null) ? shop.getShopName()     : old.apply("shopName");
    String currentDesc       = (shop != null && shop.getDescription()   != null) ? shop.getDescription()  : old.apply("description");
    String currentStreet     = (shop != null && shop.getStreetAddress() != null) ? shop.getStreetAddress(): old.apply("streetAddress");
    String currentLogoUrl    = (shop != null && shop.getLogoUrl()       != null) ? shop.getLogoUrl()      : "";
    String currentPhone      = (shop != null && shop.getOwner() != null && shop.getOwner().getPhone() != null)
                               ? shop.getOwner().getPhone() : old.apply("phone");
    String currentEmail      = (shop != null && shop.getOwner() != null && shop.getOwner().getEmail() != null)
                               ? shop.getOwner().getEmail() : old.apply("email");

    // Shop name initials for the logo placeholder
    String initials = "M";
    if (currentShopName != null && !currentShopName.trim().isEmpty()) {
        String[] words = currentShopName.trim().split("\\s+");
        initials = (words.length >= 2)
                ? (words[0].substring(0,1) + words[1].substring(0,1)).toUpperCase()
                : currentShopName.substring(0, Math.min(2, currentShopName.length())).toUpperCase();
    }

    String popupType    = (String) request.getAttribute("popupType");
    String popupMessage = (String) request.getAttribute("popupMessage");
%>

<div class="app-container">
    <div class="main-layout">

        <%-- ══ SIDEBAR (tách thành file riêng) ══ --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <%-- ══ MAIN CONTENT ══ --%>
        <div class="content-container">

            <%-- ── CONTENT ── --%>
            <main class="content-wrapper es-content-wrapper">

                <%-- Page heading --%>
                <div class="page-header">
                    <h1 class="page-title">Chỉnh Sửa Hồ Sơ</h1>
                    <p class="page-subtitle">Cập nhật thông tin thương hiệu và chi tiết liên lạc của cửa hàng bạn.</p>
                </div>

                <%-- Success / Error alert --%>
                <% if (popupMessage != null && !popupMessage.isEmpty()) { %>
                <div class="es-alert <%= "success".equals(popupType) ? "es-alert-success" : "es-alert-error" %>"
                     id="pageAlert" role="alert">
                    <i data-lucide="<%= "success".equals(popupType) ? "check-circle" : "alert-circle" %>"></i>
                    <%= popupMessage %>
                </div>                <% } %>

                <%-- ── EDIT CARD ── --%>
                <div class="edit-card">
                    <form id="editShopForm"
                          action="${pageContext.request.contextPath}/edit-shop"
                          method="POST"
                          enctype="multipart/form-data"
                          novalidate>

                        <%-- ── LOGO ── --%>
                        <div class="logo-section">
                            <p class="section-label">Logo</p>
                            <div class="logo-upload-wrapper">

                                <%-- Circle preview --%>
                                <div class="logo-circle" id="logoCircle" onclick="document.getElementById('logoFileInput').click()">
                                    <% if (!currentLogoUrl.isEmpty()) { %>
                                    <img id="logoPreviewImg" src="<%= currentLogoUrl %>" alt="Logo">
                                    <% } else { %>
                                    <span class="logo-initials" id="logoInitials"><%= initials %></span>
                                    <img id="logoPreviewImg" src="" alt="Logo" style="display:none;">
                                    <% } %>
                                    <div class="logo-camera-overlay">
                                        <i data-lucide="camera"></i>
                                    </div>
                                </div>

                                <input type="file" id="logoFileInput" name="logo"
                                       accept="image/png,image/jpeg" style="display:none;">

                                <%-- Upload meta --%>
                                <div class="logo-upload-meta">
                                    <button type="button" class="logo-upload-btn"
                                            onclick="document.getElementById('logoFileInput').click()">
                                        <i data-lucide="upload"></i>
                                        Tải ảnh lên
                                    </button>
                                    <span class="logo-hint">
                                        Định dạng hỗ trợ: JPG, PNG. Kích thước tối đa 2&nbsp;MB.<br>
                                        Tỉ lệ ảnh vuông (1:1) cho kết quả hiển thị tốt nhất.
                                    </span>
                                    <% if (!err.apply("logo").isEmpty()) { %>
                                    <span class="es-field-error"><%= err.apply("logo") %></span>
                                    <% } %>
                                </div>
                            </div>
                        </div>

                        <hr class="section-divider">

                        <%-- ── THÔNG TIN CƠ BẢN ── --%>
                        <p class="section-label" style="margin-bottom:20px;">Thông tin cơ bản</p>

                        <div class="basic-info-section">

                            <%-- Tên cửa hàng --%>
                            <div class="es-form-group">
                                <label for="shopName" class="es-label">Tên cửa hàng</label>
                                <input type="text" id="shopName" name="shopName"
                                       class="es-input <%= !err.apply("shopName").isEmpty() ? "error" : "" %>"
                                       value="<%= currentShopName %>"
                                       placeholder="VD: Maison Luxury Official Store"
                                       maxlength="100" required>
                                <% if (!err.apply("shopName").isEmpty()) { %>
                                <span class="es-field-error"><%= err.apply("shopName") %></span>
                                <% } %>
                            </div>

                            <%-- Giới thiệu cửa hàng (with simple rich-text toolbar) --%>
                            <div class="es-form-group">
                                <label for="description" class="es-label">Giới thiệu cửa hàng</label>
                                <div class="rich-toolbar" role="toolbar" aria-label="Định dạng văn bản">
                                    <button type="button" class="rich-btn" title="In đậm"
                                            onclick="execCmd('bold')"><b>B</b></button>
                                    <button type="button" class="rich-btn" title="In nghiêng"
                                            onclick="execCmd('italic')"><i>I</i></button>
                                    <button type="button" class="rich-btn" title="Danh sách"
                                            onclick="execCmd('insertUnorderedList')">
                                        <i data-lucide="list"></i>
                                    </button>
                                    <button type="button" class="rich-btn" title="Chèn link"
                                            onclick="insertLink()">
                                        <i data-lucide="link"></i>
                                    </button>
                                </div>
                                <textarea id="descriptionDisplay"
                                          class="es-textarea <%= !err.apply("description").isEmpty() ? "error" : "" %>"
                                          placeholder="Giới thiệu đôi nét về phong cách và sản phẩm của cửa hàng..."
                                          maxlength="500"
                                          oninput="syncDescription(this)"><%= currentDesc %></textarea>
                                <input type="hidden" id="description" name="description" value="<%= currentDesc %>">
                                <% if (!err.apply("description").isEmpty()) { %>
                                <span class="es-field-error"><%= err.apply("description") %></span>
                                <% } %>
                            </div>

                            <%-- Phone + Email (2 cols) --%>
                            <div class="es-row">
                                <div class="es-form-group">
                                    <label for="phone" class="es-label">Số điện thoại liên hệ</label>
                                    <input type="tel" id="phone" name="phone"
                                           class="es-input <%= !err.apply("phone").isEmpty() ? "error" : "" %>"
                                           value="<%= currentPhone %>"
                                           placeholder="+84 90 123 4567">
                                    <% if (!err.apply("phone").isEmpty()) { %>
                                    <span class="es-field-error"><%= err.apply("phone") %></span>
                                    <% } %>
                                </div>

                                <div class="es-form-group">
                                    <label for="email" class="es-label">Email liên hệ</label>
                                    <input type="email" id="email" name="email"
                                           class="es-input <%= !err.apply("email").isEmpty() ? "error" : "" %>"
                                           value="<%= currentEmail %>"
                                           placeholder="contact@cuahang.vn">
                                    <% if (!err.apply("email").isEmpty()) { %>
                                    <span class="es-field-error"><%= err.apply("email") %></span>
                                    <% } %>
                                </div>
                            </div>

                            <%-- Tỉnh / Thành phố & Phường / Xã (2 cột) --%>
                            <div class="es-row">
                                <div class="es-form-group">
                                    <label for="provinceId" class="es-label">Tỉnh / Thành phố <span class="required">*</span></label>
                                    <select id="provinceId" name="provinceId"
                                            class="es-input <%= !err.apply("provinceId").isEmpty() ? "error" : "" %>" required>
                                        <option value="">Chọn Tỉnh/Thành</option>
                                        <%
                                            List<Province> provinces = (List<Province>) request.getAttribute("provinces");
                                            Integer currentProvinceId = (shop != null && shop.getWard() != null && shop.getWard().getProvince() != null)
                                                    ? shop.getWard().getProvince().getId() : null;
                                            if (provinces != null) {
                                                for (Province p : provinces) {
                                                    boolean isSelected = currentProvinceId != null && currentProvinceId.equals(p.getId());
                                        %>
                                        <option value="<%= p.getId() %>" <%= isSelected ? "selected" : "" %>>
                                            <%= p.getName() %>
                                        </option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                    <% if (!err.apply("provinceId").isEmpty()) { %>
                                    <span class="es-field-error"><%= err.apply("provinceId") %></span>
                                    <% } %>
                                </div>

                                <div class="es-form-group">
                                    <label for="wardId" class="es-label">Phường / Xã <span class="required">*</span></label>
                                    <select id="wardId" name="wardId"
                                            class="es-input <%= !err.apply("wardId").isEmpty() ? "error" : "" %>" required>
                                        <option value="">Chọn Phường/Xã</option>
                                        <%
                                            List<Ward> wards = (List<Ward>) request.getAttribute("wards");
                                            Integer currentWardId = (shop != null) ? shop.getWardId() : null;
                                            if (wards != null) {
                                                for (Ward w : wards) {
                                                    boolean isSelected = currentWardId != null && currentWardId.equals(w.getId());
                                        %>
                                        <option value="<%= w.getId() %>" <%= isSelected ? "selected" : "" %>>
                                            <%= w.getName() %>
                                        </option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                    <% if (!err.apply("wardId").isEmpty()) { %>
                                    <span class="es-field-error"><%= err.apply("wardId") %></span>
                                    <% } %>
                                </div>
                            </div>

                            <%-- Số nhà, tên đường --%>
                            <div class="es-form-group">
                                <label for="streetAddress" class="es-label">Số nhà, tên đường <span class="required">*</span></label>
                                <div class="es-input-icon-wrap">
                                    <input type="text" id="streetAddress" name="streetAddress"
                                           class="es-input <%= !err.apply("streetAddress").isEmpty() ? "error" : "" %>"
                                           value="<%= currentStreet %>"
                                           placeholder="VD: 123 Lê Lợi" required>
                                    <span class="es-input-icon"><i data-lucide="map-pin"></i></span>
                                </div>
                                <% if (!err.apply("streetAddress").isEmpty()) { %>
                                <span class="es-field-error"><%= err.apply("streetAddress") %></span>
                                <% } %>
                            </div>

                        </div><%-- /basic-info-section --%>

                        <%-- ── ACTIONS ── --%>
                        <div class="es-actions">
                            <button type="button" class="btn-cancel" id="btnCancel"
                                    onclick="window.location.href='${pageContext.request.contextPath}/view-shop'">HỦY</button>
                            <button type="submit" class="btn-save" id="btnSave">
                                LƯU THAY ĐỔI
                            </button>
                        </div>

                    </form><%-- /editShopForm --%>
                </div><%-- /edit-card --%>

            </main><%-- /es-content-wrapper --%>
        </div><%-- /content-container --%>
    </div><%-- /main-layout --%>
</div><%-- /app-container --%>

<%-- ══ SCRIPTS ══ --%>
<script>
    /* ---------- Lucide icons ---------- */
    lucide.createIcons();

    /* ---------- Logo preview ---------- */
    document.getElementById('logoFileInput').addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;

        if (file.size > 2 * 1024 * 1024) {
            alert('Dung lượng ảnh vượt quá 2 MB. Vui lòng chọn ảnh nhỏ hơn.');
            this.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            const img     = document.getElementById('logoPreviewImg');
            const initials = document.getElementById('logoInitials');
            img.src        = e.target.result;
            img.style.display = 'block';
            if (initials) initials.style.display = 'none';
        };
        reader.readAsDataURL(file);
    });

    /* ---------- Sync hidden description field ---------- */
    function syncDescription(textarea) {
        document.getElementById('description').value = textarea.value;
    }

    /* ---------- Basic rich-text helpers ---------- */
    function execCmd(cmd) {
        const ta = document.getElementById('descriptionDisplay');
        ta.focus();
        /* Textarea doesn't support execCommand – just focus for now.
           Replace textarea with contenteditable div if full rich-text needed. */
    }

    function insertLink() {
        const ta  = document.getElementById('descriptionDisplay');
        const url = prompt('Nhập URL liên kết:');
        if (!url) return;
        const start = ta.selectionStart;
        const end   = ta.selectionEnd;
        const sel   = ta.value.substring(start, end) || url;
        const link  = '[' + sel + '](' + url + ')';
        ta.value    = ta.value.substring(0, start) + link + ta.value.substring(end);
        document.getElementById('description').value = ta.value;
    }

    /* ---------- Dynamic Ward dropdown based on Province Selection ---------- */
    document.getElementById("provinceId").addEventListener("change", function () {
        let provinceId = this.value;
        let wardSelect = document.getElementById("wardId");
        
        if (!provinceId) {
            wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
            return;
        }

        wardSelect.innerHTML = '<option value="">Đang tải...</option>';

        fetch('${pageContext.request.contextPath}/load-wards?provinceId=' + provinceId)
            .then(response => response.json())
            .then(data => {
                wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
                data.forEach(function(ward){
                    let option = document.createElement("option");
                    option.value = ward.id;
                    option.textContent = ward.name;
                    wardSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error(error);
                wardSelect.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
            });
    });

    /* ---------- Submit state ---------- */
    document.getElementById('editShopForm').addEventListener('submit', function (e) {
        // Simple client-side validation
        let isValid = true;
        const shopName = document.getElementById('shopName').value.trim();
        const provinceId = document.getElementById('provinceId').value;
        const wardId = document.getElementById('wardId').value;
        const streetAddress = document.getElementById('streetAddress').value.trim();

        if (!shopName) isValid = false;
        if (!provinceId) isValid = false;
        if (!wardId) isValid = false;
        if (!streetAddress) isValid = false;

        if (!isValid) {
            e.preventDefault();
            alert('Vui lòng điền đầy đủ các thông tin bắt buộc.');
            return;
        }

        const btn = document.getElementById('btnSave');
        btn.disabled    = true;
        btn.textContent = 'ĐANG LƯU...';
    });

    /* ---------- Cancel button handler ---------- */
    // Capture initial values on page load
    const initialName = document.getElementById('shopName').value.trim();
    const initialDesc = document.getElementById('descriptionDisplay').value.trim();
    const initialPhone = document.getElementById('phone').value.trim();
    const initialEmail = document.getElementById('email').value.trim();
    const initialProvince = document.getElementById('provinceId').value;
    const initialWard = document.getElementById('wardId').value;
    const initialStreet = document.getElementById('streetAddress').value.trim();

    function hasChanges() {
        const name = document.getElementById('shopName').value.trim();
        const desc = document.getElementById('descriptionDisplay').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const email = document.getElementById('email').value.trim();
        const province = document.getElementById('provinceId').value;
        const ward = document.getElementById('wardId').value;
        const street = document.getElementById('streetAddress').value.trim();
        const logoFile = document.getElementById('logoFileInput').files.length > 0;

        return name !== initialName ||
               desc !== initialDesc ||
               phone !== initialPhone ||
               email !== initialEmail ||
               province !== initialProvince ||
               ward !== initialWard ||
               street !== initialStreet ||
               logoFile;
    }

    document.getElementById('btnCancel').addEventListener('click', function (e) {
        e.preventDefault();
        if (hasChanges()) {
            if (confirm('Bạn đã thay đổi thông tin nhưng chưa lưu. Bạn có chắc chắn muốn hủy bỏ các thay đổi này và quay lại trang hồ sơ không?')) {
                window.location.href = '${pageContext.request.contextPath}/view-shop';
            }
        } else {
            window.location.href = '${pageContext.request.contextPath}/view-shop';
        }
    });

    /* ---------- Auto-hide alert after 4 s ---------- */
    const alert = document.getElementById('pageAlert');
    if (alert) {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s';
            alert.style.opacity    = '0';
            setTimeout(() => alert.remove(), 400);
        }, 4000);
    }
</script>
</body>
</html>
