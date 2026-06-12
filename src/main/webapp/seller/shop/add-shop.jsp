<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="vn.edu.fpt.model.Province" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ Sơ Cửa Hàng - MODA</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/seller/seller.css">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<%
    // Lấy dữ liệu cũ (khi form bị lỗi và trả về)
    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    @SuppressWarnings("unchecked")
    Map<String, String> oldInput = (Map<String, String>) request.getAttribute("oldInput");
    @SuppressWarnings("unchecked")
    List<Object[]> wards = (List<Object[]>) request.getAttribute("wards");

    String errorMessage = (String) request.getAttribute("errorMessage");

    // Helper: lấy giá trị cũ, trả "" nếu null
    java.util.function.Function<String, String> old = key ->
            (oldInput != null && oldInput.get(key) != null) ? oldInput.get(key) : "";

    // Helper: lấy thông báo lỗi của một field
    java.util.function.Function<String, String> err = key ->
            (errors != null && errors.get(key) != null) ? errors.get(key) : "";
%>

<div class="app-container">
    <div class="main-layout">

        <%-- NHÚNG SIDEBAR --%>
        <%@ include file="/seller/taskbar-seller.jsp" %>

        <div class="content-container">


            <main class="content-wrapper">

                <!-- BREADCRUMBS -->
                <nav class="breadcrumbs">
                    <a href="#">Đăng ký người bán</a>
                    <span class="separator">›</span>
                    <span class="current-page">Thêm hồ sơ cửa hàng</span>
                </nav>

                <h1 class="page-title">Hồ Sơ Cửa Hàng</h1>
                <p class="page-subtitle" style="margin-bottom: 28px;">
                    Cung cấp thông tin chi tiết về doanh nghiệp của bạn để bắt đầu tiếp cận hàng triệu khách hàng tiềm
                    năng.
                </p>

                <%-- Thông báo lỗi hệ thống (SQL, session hết hạn...) --%>
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert alert-error" id="systemAlert" role="alert">
                    <i data-lucide="alert-circle"></i>
                    <%= errorMessage %>
                </div>
                <% } %>

                <!-- 2 CỘT: FORM TRÁI | HƯỚNG DẪN PHẢI -->
                <div class="dashboard-grid">

                    <!-- CỘT TRÁI: Form đăng ký -->
                    <section class="card">
                        <%--
                            action="/add-shop" → gọi AddShopServlet.doPost()
                            enctype="multipart/form-data" → bắt buộc để upload file
                        --%>
                        <form id="shopForm"
                              action="<%= request.getContextPath() %>/add-shop"
                              method="POST"
                              enctype="multipart/form-data"
                              novalidate>

                            <!-- LOGO CỬA HÀNG -->
                            <div class="form-group-upload">
                                <label class="form-label-upload">Logo cửa hàng</label>
                                <div class="upload-inner-flex">
                                    <div class="logo-upload-box <%= !err.apply("logo").isEmpty() ? "input-error-border" : "" %>"
                                         id="logoUploadBox">
                                        <input type="file" id="logoInput" name="logo"
                                               accept="image/png, image/jpeg" style="display: none;">
                                        <div id="uploadPlaceholder"
                                             style="display: flex; flex-direction: column; align-items: center; justify-content: center;">
                                            <i data-lucide="camera"></i>
                                            <span>Tải lên logo<br>(1:1)</span>
                                        </div>
                                        <img id="logoPreview" src="" alt="Xem trước Logo"
                                             style="display: none; width: 100%; height: 100%; object-fit: cover; border-radius: 4px;">
                                    </div>
                                    <div>
                                        <p class="upload-info-text">
                                            Định dạng hỗ trợ: JPG, PNG. Kích thước tối đa 2MB.
                                            Logo nên có tỉ lệ vuông để hiển thị tốt nhất.
                                        </p>
                                        <% if (!err.apply("logo").isEmpty()) { %>
                                        <span class="field-error"><%= err.apply("logo") %></span>
                                        <% } %>
                                    </div>
                                </div>
                            </div>

                            <!-- TÊN CỬA HÀNG -->
                            <div class="form-group">
                                <label for="shopName" class="form-label">Tên cửa hàng <span
                                        class="required">*</span></label>
                                <div class="input-wrapper">
                                    <input type="text" id="shopName" name="shopName"
                                           class="input-control <%= !err.apply("shopName").isEmpty() ? "input-error" : "" %>"
                                           placeholder="VD: Minimalist Home Decor"
                                           value="<%= old.apply("shopName") %>"
                                           maxlength="100" required>
                                </div>
                                <% if (!err.apply("shopName").isEmpty()) { %>
                                <span class="field-error"><%= err.apply("shopName") %></span>
                                <% } %>
                            </div>
                            <!--- Email cửa hàng --->
<%--                            <div class="form-group">--%>
<%--                                <label for="shopEmail" class="form-label">Email cửa hàng <span class="required">*</span></label>--%>
<%--                                <div class="input-wrapper">--%>
<%--                                    <input type="email" id="shopEmail" name="shopEmail"--%>
<%--                                           class="input-control <%= !err.apply("shopEmail").isEmpty() ? "input-error" : "" %>"--%>
<%--                                           placeholder="VD: contact@cuahang.com"--%>
<%--                                           value="<%= old.apply("shopEmail") %>"--%>
<%--                                           required>--%>
<%--                                </div>--%>
<%--                                <% if (!err.apply("shopEmail").isEmpty()) { %>--%>
<%--                                <span class="field-error"><%= err.apply("shopEmail") %></span>--%>
<%--                                <% } %>--%>
<%--                            </div>--%>

                            <div class="form-row">
                            </div>
                            <div class="form-row" style="flex-direction: column;">
                                <div style="display: flex; gap: 16px;">

                                    <!-- Province -->
                                    <div class="form-group" style="flex: 1;">
                                        <label for="provinceId" class="form-label">
                                            Tỉnh / Thành phố
                                            <span class="required">*</span>
                                        </label>

                                        <select id="provinceId"
                                                name="provinceId"
                                                class="input-control"
                                                required>

                                            <option value="">
                                                 Tỉnh/Thành
                                            </option>

                                            <%
                                                List<Province> provinces =
                                                        (List<Province>) request.getAttribute("provinces");

                                                if (provinces != null) {
                                                    for (Province p : provinces) {
                                            %>

                                            <option value="<%= p.getId() %>">
                                                <%= p.getName() %>
                                            </option>

                                            <%
                                                    }
                                                }
                                            %>

                                        </select>
                                    </div>

                                    <!-- Ward -->
                                    <div class="form-group" style="flex: 1;">
                                        <label for="wardId" class="form-label">
                                            Phường / Xã
                                            <span class="required">*</span>
                                        </label>

                                        <select id="wardId"
                                                name="wardId"
                                                class="input-control"
                                                required>

                                            <option value="">
                                                Phường/Xã
                                            </option>

                                        </select>
                                    </div>

                                </div>

                                <div class="form-group">
                                    <label for="streetAddress" class="form-label">
                                        Số nhà, tên đường
                                        <span class="required">*</span>
                                    </label>

                                    <input type="text"
                                           id="streetAddress"
                                           name="streetAddress"
                                           class="input-control"
                                           placeholder="VD: 123 Nguyễn Huệ"
                                           required>
                                </div>
                            </div>


                            <!-- MÔ TẢ NGẮN -->
                            <div class="form-group">
                                <label for="description" class="form-label">Mô tả ngắn</label>
                                <textarea id="description" name="description"
                                          class="input-control textarea-control <%= !err.apply("description").isEmpty() ? "input-error" : "" %>"
                                          placeholder="Giới thiệu đôi nét về phong cách và sản phẩm của cửa hàng..."
                                          maxlength="250"><%= old.apply("description") %></textarea>
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 4px;">
                                    <% if (!err.apply("description").isEmpty()) { %>
                                    <span class="field-error"><%= err.apply("description") %></span>
                                    <% } else { %>
                                    <span></span>
                                    <% } %>
                                    <span class="char-counter" id="charCounter">
                                        <%= old.apply("description").length() %> / 250 Ký tự
                                    </span>
                                </div>
                            </div>

                            <div class="form-divider"></div>

                            <!-- BUTTONS -->
                            <div class="form-actions">
                                <button type="button" class="btn btn-link" id="btnCancel">Hủy</button>
                                <button type="submit" class="btn btn-primary" id="btnSubmit">
                                    Công bố hồ sơ
                                </button>
                            </div>

                        </form>
                    </section>

                    <!-- CỘT PHẢI: Hướng dẫn & Mẹo -->
                    <aside class="guide-container">
                        <div class="guide-card">
                            <h3 class="guide-title">Hướng dẫn</h3>
                            <div class="guide-steps">
                                <div class="guide-step">
                                    <div class="step-num">01</div>
                                    <div class="step-body">
                                        <h4 class="step-title">Tên cửa hàng</h4>
                                        <p class="step-desc">Nên phản ánh đúng thương hiệu và ngành hàng. Tránh sử dụng
                                            ký tự đặc biệt.</p>
                                    </div>
                                </div>
                                <div class="guide-step">
                                    <div class="step-num">02</div>
                                    <div class="step-body">
                                        <h4 class="step-title">Hình ảnh & Logo</h4>
                                        <p class="step-desc">Logo chất lượng cao giúp tăng độ tin cậy lên 80% đối với
                                            khách hàng.</p>
                                    </div>
                                </div>
                                <div class="guide-step">
                                    <div class="step-num">03</div>
                                    <div class="step-body">
                                        <h4 class="step-title">Địa chỉ</h4>
                                        <p class="step-desc">Chọn đúng phường/xã và điền số nhà, tên đường để khách hàng
                                            dễ dàng tìm đến.</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="tips-card">
                            <span class="tips-tag">Mẹo tối ưu</span>
                            <blockquote class="tips-quote">
                                "Mô tả cửa hàng nên ngắn gọn nhưng đầy đủ giá trị cốt lõi để khách hàng dễ dàng ghi
                                nhớ."
                            </blockquote>
                            <a href="#" class="tips-link">
                                Dịch vụ hỗ trợ người bán
                                <i data-lucide="chevron-right"></i>
                            </a>
                        </div>
                    </aside>

                </div>
            </main>

        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<!-- JavaScript nâng cao trải nghiệm người dùng -->
<script>
    // Khởi tạo Lucide icons
    lucide.createIcons();

    // ── Helper functions for validation errors ──────────────────────────────
    function clearAllErrors() {
        document.querySelectorAll('.field-error').forEach(el => el.remove());
        document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
        document.querySelectorAll('.input-error-border').forEach(el => el.classList.remove('input-error-border'));
    }

    function showFieldError(fieldId, message) {
        let inputEl = document.getElementById(fieldId);
        if (fieldId === 'logo') {
            inputEl = document.getElementById('logoInput');
        }
        if (!inputEl) return;

        if (fieldId === 'logo') {
            const logoBox = document.getElementById('logoUploadBox');
            if (logoBox) logoBox.classList.add('input-error-border');
        } else {
            inputEl.classList.add('input-error');
        }

        const errorSpan = document.createElement('span');
        errorSpan.className = 'field-error';
        errorSpan.textContent = message;

        if (fieldId === 'logo') {
            const uploadInner = inputEl.closest('.upload-inner-flex');
            const targetDiv = uploadInner ? uploadInner.querySelector('div:last-child') : null;
            if (targetDiv) {
                targetDiv.appendChild(errorSpan);
                return;
            }
        }

        const formGroup = inputEl.closest('.form-group') || inputEl.closest('.form-group-upload');
        if (formGroup) {
            formGroup.appendChild(errorSpan);
        }
    }

    // ── 1. Xem trước logo khi chọn file ──────────────────────────────────────
    const logoUploadBox = document.getElementById('logoUploadBox');
    const logoInput = document.getElementById('logoInput');
    const uploadPlaceholder = document.getElementById('uploadPlaceholder');
    const logoPreview = document.getElementById('logoPreview');

    logoUploadBox.addEventListener('click', () => logoInput.click());

    logoInput.addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;

        clearAllErrors();

        if (file.size > 2 * 1024 * 1024) {
            showFieldError('logo', 'Kích thích file không được vượt quá 2MB');
            this.value = '';
            return;
        }
        if (!['image/jpeg', 'image/png'].includes(file.type)) {
            showFieldError('logo', 'Chỉ hỗ trợ định dạng JPG, PNG');
            this.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            logoPreview.src = e.target.result;
            logoPreview.style.display = 'block';
            uploadPlaceholder.style.display = 'none';
            logoUploadBox.style.borderStyle = 'solid';
        };
        reader.readAsDataURL(file);
    });

    // ── 2. Bộ đếm ký tự textarea ─────────────────────────────────────────────
    const descTextarea = document.getElementById('description');
    const charCounter = document.getElementById('charCounter');

    descTextarea.addEventListener('input', function () {
        const len = this.value.length;
        charCounter.textContent = len + ' / 250 Ký tự';
        charCounter.style.color = len >= 230 ? '#ef4444' : 'var(--color-text-tertiary)';
    });

    // ── 3. Validate client-side trước khi submit ──────────────────────────────
    document.getElementById('shopForm').addEventListener('submit', function (e) {
        let isValid = true;
        clearAllErrors();

        const shopName = document.getElementById('shopName').value.trim();
        if (!shopName) {
            showFieldError('shopName', 'Tên cửa hàng không được để trống.');
            isValid = false;
        } else if (shopName.length > 100) {
            showFieldError('shopName', 'Tên cửa hàng không được vượt quá 100 ký tự.');
            isValid = false;
        }

        // shopEmail field is currently disabled/hidden — validation skipped

        const provinceId = document.getElementById('provinceId').value;
        if (!provinceId) {
            showFieldError('provinceId', 'Vui lòng chọn Tỉnh/Thành Phố');
            isValid = false;
        }

        const wardId = document.getElementById('wardId').value;
        if (!wardId) {
            showFieldError('wardId', 'Vui lòng chọn Phường/Xã.');
            isValid = false;
        }

        const streetAddress = document.getElementById('streetAddress').value.trim();
        if (!streetAddress) {
            showFieldError('streetAddress', 'Địa chỉ không được để trống.');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
            // Cuộn lên đầu để người dùng thấy lỗi
            window.scrollTo({top: 0, behavior: 'smooth'});
        } else {
            // Disable nút submit để tránh double click
            document.getElementById('btnSubmit').disabled = true;
            document.getElementById('btnSubmit').textContent = 'Đang xử lý...';
        }
    });

    // ── 4. Nút Hủy ───────────────────────────────────────────────────────────
    document.getElementById('btnCancel').addEventListener('click', function () {
        if (confirm('Bạn có chắc chắn muốn hủy? Dữ liệu chưa lưu sẽ bị mất.')) {
            document.getElementById('shopForm').reset();
            logoPreview.style.display = 'none';
            uploadPlaceholder.style.display = 'flex';
            logoUploadBox.style.borderStyle = 'dashed';
            charCounter.textContent = '0 / 250 Ký tự';
            charCounter.style.color = 'var(--color-text-tertiary)';
            clearAllErrors();
        }
    });

    // ── 6. Tự động ẩn thông báo hệ thống sau 5 giây ─────────────────────────
    const systemAlert = document.getElementById('systemAlert');
    if (systemAlert) {
        setTimeout(() => {
            systemAlert.style.transition = 'opacity 0.5s';
            systemAlert.style.opacity = '0';
            setTimeout(() => systemAlert.remove(), 500);
        }, 5000);
    }
</script>
<script>

    document.getElementById("provinceId")
        .addEventListener("change", function () {

            let provinceId = this.value;

            let wardSelect =
                document.getElementById("wardId");

            wardSelect.innerHTML =
                '<option value="">Đang tải...</option>';

            fetch(
                '<%= request.getContextPath() %>/load-wards?provinceId='
                + provinceId
            )

                .then(response => response.json())

                .then(data => {

                    wardSelect.innerHTML =
                        '<option value="">Phường/Xã</option>';

                    data.forEach(function(ward){

                        let option =
                            document.createElement("option");

                        option.value = ward.id;
                        option.textContent = ward.name;

                        wardSelect.appendChild(option);
                    });
                })

                .catch(error => {

                    console.error(error);

                    wardSelect.innerHTML =
                        '<option value="">Lỗi tải dữ liệu</option>';
                });
        });

</script>
<%
    String popupMessage =
            (String) request.getAttribute("popupMessage");

    String popupType =
            (String) request.getAttribute("popupType");
%>

<% if (popupMessage != null) { %>

<script>
    Swal.fire({
        title: '<%= "success".equals(popupType) ? "Thành công!" : "Lỗi!" %>',
        text: '<%= popupMessage %>',
        icon: '<%= "success".equals(popupType) ? "success" : "error" %>',
        confirmButtonText: 'OK',
        confirmButtonColor: '#ff4d4f'
    });
</script>

<% } %>
</body>
</html>