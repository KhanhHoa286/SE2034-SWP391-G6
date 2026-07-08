<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Đăng ký người bán</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/add-seller-account.css?v=20260708">
</head>

<body class="profile-body seller-register-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout seller-register-layout">
    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="seller" />
    </jsp:include>

    <main class="profile-main seller-register-main">
        <section class="seller-register-container">
            <header class="seller-register-header">
                <p>MODA Seller Center</p>
                <h1>Đăng ký người bán</h1>
                <span>
                    Tạo hồ sơ cửa hàng để bắt đầu bán sản phẩm trên MODA.
                    Sau khi đăng ký thành công, mục này sẽ chuyển thành Trang người bán.
                </span>
            </header>

            <c:if test="${not empty errors.general}">
                <div class="seller-register-alert seller-register-alert--error">
                    <c:out value="${errors.general}" />
                </div>
            </c:if>

            <form class="seller-register-form"
                  action="${pageContext.request.contextPath}/seller-register"
                  method="post"
                  novalidate>

                <div class="seller-register-card">
                    <div class="seller-register-card__head">
                        <h2>Thông tin cửa hàng</h2>
                        <p>Tên cửa hàng và mô tả sẽ được dùng trong khu vực người bán.</p>
                    </div>

                    <div class="seller-register-field">
                        <label for="shopName">Tên cửa hàng</label>
                        <input id="shopName"
                               name="shopName"
                               type="text"
                               maxlength="100"
                               value="<c:out value='${oldInput.shopName}' />"
                               placeholder="Ví dụ: Minh Fashion">
                        <c:if test="${not empty errors.shopName}">
                            <small class="seller-register-error">
                                <c:out value="${errors.shopName}" />
                            </small>
                        </c:if>
                    </div>

                    <div class="seller-register-field">
                        <label for="description">Mô tả cửa hàng <span>không bắt buộc</span></label>
                        <textarea id="description"
                                  name="description"
                                  rows="4"
                                  maxlength="500"
                                  placeholder="Giới thiệu ngắn về cửa hàng của bạn"><c:out value="${oldInput.description}" /></textarea>
                        <c:if test="${not empty errors.description}">
                            <small class="seller-register-error">
                                <c:out value="${errors.description}" />
                            </small>
                        </c:if>
                    </div>
                </div>

                <div class="seller-register-card">
                    <div class="seller-register-card__head">
                        <h2>Địa chỉ lấy hàng</h2>
                        <p>Địa chỉ này sẽ được dùng để tạo phiếu vận đơn cho bên giao hàng.</p>
                    </div>

                    <div class="seller-register-grid">
                        <div class="seller-register-field">
                            <label for="provinceSelect">Tỉnh/Thành phố</label>
                            <select id="provinceSelect" name="provinceId">
                                <option value="">Chọn Tỉnh/Thành phố</option>
                                <c:forEach items="${provinces}" var="province">
                                    <option value="${province.id}"
                                            ${oldInput.provinceId == province.id ? 'selected' : ''}>
                                        <c:out value="${province.fullName}" />
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty errors.provinceId}">
                                <small class="seller-register-error">
                                    <c:out value="${errors.provinceId}" />
                                </small>
                            </c:if>
                        </div>

                        <div class="seller-register-field">
                            <label for="wardSelect">Phường/Xã</label>
                            <select id="wardSelect" name="wardId" disabled>
                                <option value="">Chọn Phường/Xã</option>
                            </select>
                            <c:if test="${not empty errors.wardId}">
                                <small class="seller-register-error">
                                    <c:out value="${errors.wardId}" />
                                </small>
                            </c:if>
                        </div>
                    </div>

                    <div class="seller-register-field">
                        <label for="streetAddress">Địa chỉ chi tiết</label>
                        <textarea id="streetAddress"
                                  name="streetAddress"
                                  rows="3"
                                  maxlength="255"
                                  placeholder="Số nhà, tên đường, tòa nhà..."><c:out value="${oldInput.streetAddress}" /></textarea>
                        <c:if test="${not empty errors.streetAddress}">
                            <small class="seller-register-error">
                                <c:out value="${errors.streetAddress}" />
                            </small>
                        </c:if>
                    </div>
                </div>

                <div class="seller-register-actions">
                    <a class="seller-register-btn seller-register-btn--secondary"
                       href="${pageContext.request.contextPath}/customer/dashboard">
                        Hủy
                    </a>
                    <button class="seller-register-btn seller-register-btn--primary" type="submit">
                        Đăng ký người bán
                    </button>
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
    const selectedProvinceId = "<c:out value='${oldInput.provinceId}' />";
    const selectedWardId = "<c:out value='${oldInput.wardId}' />";

    function resetWardSelect(text, disabled) {
        wardSelect.innerHTML = "";
        const option = document.createElement("option");
        option.value = "";
        option.textContent = text;
        wardSelect.appendChild(option);
        wardSelect.disabled = disabled;
    }

    function loadWardsByProvince(provinceId, wardId) {
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
                    if (wardId && String(ward.id) === String(wardId)) {
                        option.selected = true;
                    }
                    wardSelect.appendChild(option);
                });
            })
            .catch(function () {
                resetWardSelect("Không tải được phường/xã", true);
            });
    }

    provinceSelect.addEventListener("change", function () {
        loadWardsByProvince(this.value, "");
    });

    if (selectedProvinceId) {
        loadWardsByProvince(selectedProvinceId, selectedWardId);
    }
</script>

</body>
</html>
