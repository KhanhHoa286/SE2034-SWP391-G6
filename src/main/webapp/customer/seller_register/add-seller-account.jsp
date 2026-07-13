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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/add-seller-account.css?v=20260710-id-required">
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
                    Cung cấp thông tin định danh của chủ tài khoản. Sau bước này bạn sẽ tiếp tục tạo hồ sơ cửa hàng.
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
                  enctype="multipart/form-data"
                  novalidate>

                <div class="seller-register-card">
                    <div class="seller-register-card__head">
                        <h2>Thông tin định danh</h2>
                        <p>Các thông tin này dùng để xác nhận chủ tài khoản người bán, không trùng với hồ sơ cửa hàng.</p>
                    </div>

                    <div class="seller-register-field">
                        <label for="legalFullName">Họ tên theo căn cước công dân</label>
                        <input id="legalFullName"
                               name="legalFullName"
                               type="text"
                               maxlength="120"
                               value="<c:out value='${oldInput.legalFullName}' />"
                               placeholder="Ví dụ: Nguyễn Văn A">
                        <c:if test="${not empty errors.legalFullName}">
                            <small class="seller-register-error"><c:out value="${errors.legalFullName}" /></small>
                        </c:if>
                    </div>

                    <div class="seller-register-grid">
                        <div class="seller-register-field">
                            <label for="citizenId">Số căn cước công dân</label>
                            <input id="citizenId"
                                   name="citizenId"
                                   type="text"
                                   maxlength="12"
                                   inputmode="numeric"
                                   value="<c:out value='${oldInput.citizenId}' />"
                                   placeholder="12 chữ số">
                            <c:if test="${not empty errors.citizenId}">
                                <small class="seller-register-error"><c:out value="${errors.citizenId}" /></small>
                            </c:if>
                        </div>

                        <div class="seller-register-field">
                            <label for="citizenIdIssueDate">Ngày cấp <span>không bắt buộc</span></label>
                            <input id="citizenIdIssueDate"
                                   name="citizenIdIssueDate"
                                   type="date"
                                   value="<c:out value='${oldInput.citizenIdIssueDate}' />">
                            <c:if test="${not empty errors.citizenIdIssueDate}">
                                <small class="seller-register-error"><c:out value="${errors.citizenIdIssueDate}" /></small>
                            </c:if>
                        </div>
                    </div>

                    <div class="seller-register-field">
                        <label for="citizenIdIssuePlace">Nơi cấp</label>
                        <input id="citizenIdIssuePlace"
                               name="citizenIdIssuePlace"
                               type="text"
                               maxlength="255"
                               value="<c:out value='${oldInput.citizenIdIssuePlace}' />"
                               placeholder="Ví dụ: Cục Cảnh sát quản lý hành chính về trật tự xã hội">
                        <c:if test="${not empty errors.citizenIdIssuePlace}">
                            <small class="seller-register-error"><c:out value="${errors.citizenIdIssuePlace}" /></small>
                        </c:if>
                    </div>

                    <div class="seller-register-field">
                        <label for="permanentAddress">Địa chỉ thường trú</label>
                        <textarea id="permanentAddress"
                                  name="permanentAddress"
                                  rows="3"
                                  maxlength="500"
                                  placeholder="Nhập địa chỉ thường trú theo giấy tờ định danh"><c:out value="${oldInput.permanentAddress}" /></textarea>
                        <c:if test="${not empty errors.permanentAddress}">
                            <small class="seller-register-error"><c:out value="${errors.permanentAddress}" /></small>
                        </c:if>
                    </div>
                </div>

                <div class="seller-register-card">
                    <div class="seller-register-card__head">
                        <h2>Ảnh căn cước công dân</h2>
                        <p>Ảnh JPG hoặc PNG, tối đa 5MB mỗi ảnh. Cần tải đủ ảnh mặt trước và mặt sau căn cước công dân.</p>
                    </div>

                    <div class="seller-register-grid seller-register-image-grid">
                        <div class="seller-register-field">
                            <label for="frontIdImage">Ảnh mặt trước <span>bắt buộc</span></label>
                            <input id="frontIdImage" name="frontIdImage" type="file" accept="image/png,image/jpeg" required>
                            <div class="seller-register-preview" data-preview-for="frontIdImage" hidden>
                                <img src="" alt="Ảnh mặt trước căn cước công dân">
                            </div>
                            <c:if test="${not empty errors.frontIdImage}">
                                <small class="seller-register-error"><c:out value="${errors.frontIdImage}" /></small>
                            </c:if>
                        </div>

                        <div class="seller-register-field">
                            <label for="backIdImage">Ảnh mặt sau <span>bắt buộc</span></label>
                            <input id="backIdImage" name="backIdImage" type="file" accept="image/png,image/jpeg" required>
                            <div class="seller-register-preview" data-preview-for="backIdImage" hidden>
                                <img src="" alt="Ảnh mặt sau căn cước công dân">
                            </div>
                            <c:if test="${not empty errors.backIdImage}">
                                <small class="seller-register-error"><c:out value="${errors.backIdImage}" /></small>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="seller-register-actions">
                    <a class="seller-register-btn seller-register-btn--secondary"
                       href="${pageContext.request.contextPath}/customer/dashboard">
                        Hủy
                    </a>
                    <button class="seller-register-btn seller-register-btn--primary" type="submit">
                        Tiếp tục tạo cửa hàng
                    </button>
                </div>
            </form>
        </section>
    </main>
</div>

<jsp:include page="/common/footer.jsp" />

<script>
    document.querySelectorAll('input[type="file"][accept*="image"]').forEach(function (input) {
        const preview = document.querySelector('[data-preview-for="' + input.id + '"]');
        const previewImage = preview ? preview.querySelector('img') : null;
        let objectUrl = null;

        input.addEventListener('change', function () {
            if (!preview || !previewImage) {
                return;
            }

            if (objectUrl) {
                URL.revokeObjectURL(objectUrl);
                objectUrl = null;
            }

            const file = input.files && input.files[0];
            if (!file || !file.type || !file.type.startsWith('image/')) {
                preview.hidden = true;
                previewImage.removeAttribute('src');
                return;
            }

            objectUrl = URL.createObjectURL(file);
            previewImage.src = objectUrl;
            preview.hidden = false;
        });
    });
</script>

</body>
</html>
