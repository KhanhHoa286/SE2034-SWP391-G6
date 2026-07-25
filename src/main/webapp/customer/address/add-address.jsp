<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Thêm địa chỉ mới</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <!-- Font Awesome nếu header chung đang dùng -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Bootstrap nếu header chung đang dùng -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">

    <!-- CSS layout sidebar/profile dùng chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=4">

    <!-- CSS riêng màn add address -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/add-address.css?v=1">
</head>

<body class="profile-body add-address-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout add-address-layout">

    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="addresses" />
    </jsp:include>

    <main class="profile-main add-address-main">

        <section class="add-address-content">

            <header class="add-address-header">
                <h1>Thêm địa chỉ mới</h1>
                <div class="add-address-title-line"></div>
            </header>

            <c:if test="${not empty errors.general}">
                <div class="add-address-alert">
                    <c:out value="${errors.general}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/customer/addresses/add"
                  method="post"
                  class="add-address-form">

                <!-- Họ tên + số điện thoại -->
                <div class="add-address-grid add-address-grid--two">

                    <div class="add-address-field">
                        <label for="receiverName">Họ và tên</label>
                        <input
                                type="text"
                                id="receiverName"
                                name="receiverName"
                                value="<c:out value='${inputReceiverName}' />"
                                placeholder="Nhập họ và tên đầy đủ">

                        <c:if test="${not empty errors.receiverName}">
                            <p class="add-address-error">
                                <c:out value="${errors.receiverName}" />
                            </p>
                        </c:if>
                    </div>

                    <div class="add-address-field">
                        <label for="receiverPhone">Số điện thoại</label>
                       <input
                               type="tel"
                               id="receiverPhone"
                               name="receiverPhone"
                               value="<c:out value='${inputReceiverPhone}' />"
                               placeholder="0912345678"
                               inputmode="numeric"
                               maxlength="10"
                               pattern="0[35789][0-9]{8}"

                               oninput="this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10)">

                        <c:if test="${not empty errors.receiverPhone}">
                            <p class="add-address-error">
                                <c:out value="${errors.receiverPhone}" />
                            </p>
                        </c:if>
                    </div>

                </div>

                <!-- Tỉnh / Phường -->
                <div class="add-address-grid add-address-grid--two">

                    <div class="add-address-field">
                        <label for="provinceSelect">Tỉnh/Thành phố</label>

                        <div class="add-address-select-wrap">
                            <select id="provinceSelect" name="provinceId">
                                <option value="">Chọn Tỉnh/Thành phố</option>

                                <c:forEach items="${provinces}" var="province">
                                    <option value="${province.id}"
                                            ${selectedProvinceId == province.id ? 'selected' : ''}>
                                        <c:out value="${province.fullName}" />
                                    </option>
                                </c:forEach>
                            </select>

                            <span class="material-symbols-outlined">expand_more</span>
                        </div>

                        <c:if test="${not empty errors.provinceId}">
                            <p class="add-address-error">
                                <c:out value="${errors.provinceId}" />
                            </p>
                        </c:if>
                    </div>

                    <div class="add-address-field">
                        <label for="wardSelect">Phường/Xã</label>

                        <div class="add-address-select-wrap">
                            <select id="wardSelect" name="wardId" disabled>
                                <option value="">Chọn Phường/Xã</option>
                            </select>

                            <span class="material-symbols-outlined">expand_more</span>
                        </div>

                        <c:if test="${not empty errors.wardId}">
                            <p class="add-address-error">
                                <c:out value="${errors.wardId}" />
                            </p>
                        </c:if>
                    </div>

                </div>

                <!-- Địa chỉ chi tiết -->
                <div class="add-address-field add-address-field--full">
                    <label for="streetAddress">Địa chỉ chi tiết</label>

                    <textarea
                            id="streetAddress"
                            name="streetAddress"
                            rows="3"
                            placeholder="Số nhà, tên đường, tòa nhà..."><c:out value="${inputStreetAddress}" /></textarea>

                    <c:if test="${not empty errors.streetAddress}">
                        <p class="add-address-error">
                            <c:out value="${errors.streetAddress}" />
                        </p>
                    </c:if>
                </div>

                <!-- Mặc định -->
                <div class="add-address-default-row">
                    <label class="add-address-checkbox">
                        <input
                                type="checkbox"
                                name="isDefault"
                                value="true"
                                ${(addressCount == 0 || inputIsDefault) ? 'checked' : ''}
                                ${addressCount == 0 ? 'disabled' : ''}>

                        <span>Đặt làm địa chỉ mặc định</span>
                    </label>

                    <c:if test="${addressCount == 0}">
                        <p class="add-address-note">
                            Đây là địa chỉ đầu tiên nên hệ thống sẽ tự đặt làm mặc định.
                        </p>
                    </c:if>
                </div>

                <!-- Actions -->
                <div class="add-address-actions">
                    <button type="submit" class="add-address-btn add-address-btn--save">
                        Lưu địa chỉ
                    </button>

                    <a href="${pageContext.request.contextPath}/customer/addresses"
                       class="add-address-btn add-address-btn--cancel">
                        Hủy
                    </a>
                </div>

            </form>

        </section>

    </main>

</div>

<jsp:include page="/common/footer.jsp" />

<script>
    const provinceSelect = document.getElementById("provinceSelect");
    const wardSelect = document.getElementById("wardSelect");

    const contextPath = "${pageContext.request.contextPath}";
    const oldSelectedWardId = "<c:out value='${selectedWardId}' />";

    function resetWardSelect(text, disabled) {
        wardSelect.innerHTML = "";

        const option = document.createElement("option");
        option.value = "";
        option.textContent = text;

        wardSelect.appendChild(option);
        wardSelect.disabled = disabled;
    }

    function loadWardsByProvince(provinceId, selectedWardId) {
        if (!provinceId) {
            resetWardSelect("Chọn Phường/Xã", true);
            return;
        }

        resetWardSelect("Đang tải...", true);

        fetch(contextPath + "/load-wards?provinceId=" + encodeURIComponent(provinceId))
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Không tải được danh sách phường/xã");
                }

                return response.json();
            })
            .then(function (wards) {
                resetWardSelect("Chọn Phường/Xã", false);

                if (!wards || wards.length === 0) {
                    resetWardSelect("Không có phường/xã", true);
                    return;
                }

                wards.forEach(function (ward) {
                    const option = document.createElement("option");

                    option.value = ward.id;
                    option.textContent = ward.name;

                    if (selectedWardId && String(ward.id) === String(selectedWardId)) {
                        option.selected = true;
                    }

                    wardSelect.appendChild(option);
                });

                wardSelect.disabled = false;
            })
            .catch(function (error) {
                console.error(error);
                resetWardSelect("Lỗi tải dữ liệu", true);
            });
    }

    provinceSelect.addEventListener("change", function () {
        loadWardsByProvince(this.value, "");
    });

    if (provinceSelect.value) {
        loadWardsByProvince(provinceSelect.value, oldSelectedWardId);
    } else {
        resetWardSelect("Chọn Phường/Xã", true);
    }
</script>
</body>
</html>