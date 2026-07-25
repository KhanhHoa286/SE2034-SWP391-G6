<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Chỉnh sửa địa chỉ</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <!-- Font Awesome nếu header chung đang dùng -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Bootstrap nếu header chung đang dùng -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=20260722-shop-toast">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/edit-address.css?v=1">
</head>

<body class="profile-body edit-address-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout edit-address-layout">

    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="addresses" />
    </jsp:include>

    <main class="profile-main edit-address-main">

        <section class="edit-address-content">

            <header class="edit-address-header">
                <h1>Chỉnh sửa địa chỉ</h1>
            </header>

            <c:if test="${not empty errors.general}">
                <div class="edit-address-alert">
                    <c:out value="${errors.general}" />
                </div>
            </c:if>

            <c:set var="receiverNameValue" value="${not empty inputReceiverName ? inputReceiverName : address.receiverName}" />
            <c:set var="receiverPhoneValue" value="${not empty inputReceiverPhone ? inputReceiverPhone : address.receiverPhone}" />
            <c:set var="streetAddressValue" value="${not empty inputStreetAddress ? inputStreetAddress : address.streetAddress}" />
            <c:set var="provinceValue" value="${not empty selectedProvinceId ? selectedProvinceId : address.ward.province.id}" />
            <c:set var="wardValue" value="${not empty selectedWardId ? selectedWardId : address.wardId}" />
            <c:set var="defaultValue" value="${not empty inputIsDefault ? inputIsDefault : address.isDefault}" />

            <div class="edit-address-card">
                <form action="${pageContext.request.contextPath}/customer/addresses/edit"
                      method="post"
                      class="edit-address-form">

                    <input type="hidden" name="addressId" value="${address.addressId}">

                    <div class="edit-address-grid edit-address-grid--two">
                        <div class="edit-address-field">
                            <label for="receiverName">Họ và tên</label>
                            <input type="text"
                                   id="receiverName"
                                   name="receiverName"
                                   value="<c:out value='${receiverNameValue}' />"
                                   placeholder="Nhập họ và tên đầy đủ">

                            <c:if test="${not empty errors.receiverName}">
                                <p class="edit-address-error">
                                    <c:out value="${errors.receiverName}" />
                                </p>
                            </c:if>
                        </div>

                        <div class="edit-address-field">
                            <label for="receiverPhone">Số điện thoại</label>
                            <input type="tel"
                                   id="receiverPhone"
                                   name="receiverPhone"
                                   value="<c:out value='${receiverPhoneValue}' />"
                                   placeholder="0912345678"
                                   inputmode="numeric"
                                   maxlength="10"
                                   pattern="0[35789][0-9]{8}"
                                   oninput="this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10)">

                            <c:if test="${not empty errors.receiverPhone}">
                                <p class="edit-address-error">
                                    <c:out value="${errors.receiverPhone}" />
                                </p>
                            </c:if>
                        </div>
                    </div>

                    <div class="edit-address-field edit-address-field--full">
                        <label for="streetAddress">Địa chỉ cụ thể</label>
                        <input type="text"
                               id="streetAddress"
                               name="streetAddress"
                               value="<c:out value='${streetAddressValue}' />"
                               placeholder="Số nhà, tên đường, tòa nhà...">

                        <c:if test="${not empty errors.streetAddress}">
                            <p class="edit-address-error">
                                <c:out value="${errors.streetAddress}" />
                            </p>
                        </c:if>
                    </div>

                    <div class="edit-address-grid edit-address-grid--two">
                        <div class="edit-address-field">
                            <label for="provinceSelect">Tỉnh / Thành phố</label>

                            <div class="edit-address-select-wrap">
                                <select id="provinceSelect" name="provinceId">
                                    <option value="">Chọn Tỉnh/Thành phố</option>

                                    <c:forEach items="${provinces}" var="province">
                                        <option value="${province.id}"
                                                ${provinceValue == province.id ? 'selected' : ''}>
                                            <c:out value="${province.fullName}" />
                                        </option>
                                    </c:forEach>
                                </select>

                                <span class="material-symbols-outlined">expand_more</span>
                            </div>

                            <c:if test="${not empty errors.provinceId}">
                                <p class="edit-address-error">
                                    <c:out value="${errors.provinceId}" />
                                </p>
                            </c:if>
                        </div>

                        <div class="edit-address-field">
                            <label for="wardSelect">Phường / Xã</label>

                            <div class="edit-address-select-wrap">
                                <select id="wardSelect" name="wardId" disabled>
                                    <option value="">Chọn Phường/Xã</option>
                                </select>

                                <span class="material-symbols-outlined">expand_more</span>
                            </div>

                            <c:if test="${not empty errors.wardId}">
                                <p class="edit-address-error">
                                    <c:out value="${errors.wardId}" />
                                </p>
                            </c:if>
                        </div>
                    </div>

                    <div class="edit-address-default-row">
                        <label class="edit-address-switch">
                            <input type="checkbox"
                                   name="isDefault"
                                   value="true"
                                   ${defaultValue ? 'checked' : ''}>
                            <span class="edit-address-slider"></span>
                            <span class="edit-address-switch-text">Đặt làm địa chỉ mặc định</span>
                        </label>
                    </div>

                    <div class="edit-address-actions">
                        <button type="submit" class="edit-address-btn edit-address-btn--save">
                            Cập nhật
                        </button>

                        <a href="${pageContext.request.contextPath}/customer/addresses"
                           class="edit-address-btn edit-address-btn--cancel">
                            Hủy
                        </a>
                    </div>
                </form>
            </div>

            <div class="edit-address-helper-grid">
                <div class="edit-address-helper-card">
                    <div class="edit-address-helper-title">
                        <span class="material-symbols-outlined">local_shipping</span>
                        <h3>Vận chuyển</h3>
                    </div>
                    <p>Địa chỉ này sẽ được sử dụng để tính toán phí vận chuyển và thời gian giao hàng dự kiến.</p>
                </div>

                <div class="edit-address-helper-card">
                    <div class="edit-address-helper-title">
                        <span class="material-symbols-outlined">security</span>
                        <h3>Bảo mật</h3>
                    </div>
                    <p>Thông tin của bạn được bảo mật trong quá trình xử lý đơn hàng.</p>
                </div>
            </div>

        </section>

    </main>

</div>

<jsp:include page="/common/footer.jsp" />

<script>
    const provinceSelect = document.getElementById("provinceSelect");
    const wardSelect = document.getElementById("wardSelect");

    const contextPath = "${pageContext.request.contextPath}";
    const oldSelectedWardId = "<c:out value='${wardValue}' />";

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
