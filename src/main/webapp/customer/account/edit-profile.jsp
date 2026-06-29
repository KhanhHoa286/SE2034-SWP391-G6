<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="profileUser" value="${not empty requestScope.profileUser ? requestScope.profileUser : sessionScope.user}" />
<c:set var="defaultAvatar" value="${not empty requestScope.defaultAvatar ? requestScope.defaultAvatar : 'https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg'}" />
<c:set var="avatarDisplay" value="${not empty requestScope.avatarUrl ? requestScope.avatarUrl : (not empty profileUser.avatarUrl ? profileUser.avatarUrl : defaultAvatar)}" />
<c:set var="fallbackFullName" value="${profileUser.firstName} ${profileUser.lastName}" />
<c:set var="fullNameInput" value="${not empty requestScope.fullNameValue ? requestScope.fullNameValue : fallbackFullName}" />
<c:set var="dateOfBirthInput" value="${not empty requestScope.dateOfBirthValue ? requestScope.dateOfBirthValue : profileUser.dateOfBirth}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa Hồ sơ | MODA</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Font chính của giao diện mẫu -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <!-- Font Awesome cho common/header.jsp -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Bootstrap vì common/header.jsp có thể đang dùng class Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS chung của project -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">

    <!-- CSS riêng màn edit profile -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/edit-profile.css?v=6">
</head>

<body class="edit-profile-body">

<jsp:include page="/common/header.jsp" />

<div class="edit-profile-layout">

    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="profile" />
    </jsp:include>

    <main class="edit-profile-main">
        <section class="edit-profile-container">

            <header class="edit-profile-header">
                <h1>Chỉnh sửa Hồ sơ</h1>
                <p>Cập nhật thông tin cá nhân và ảnh đại diện của bạn.</p>
            </header>

            <c:if test="${not empty errorMessage}">
                <div class="edit-profile-alert edit-profile-alert--error">
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="edit-profile-alert edit-profile-alert--success">
                    <c:out value="${successMessage}" />
                </div>
            </c:if>

            <form class="edit-profile-form"
                  action="${pageContext.request.contextPath}/customer/profile/edit"
                  method="post"
                  enctype="multipart/form-data">

                <div class="edit-profile-avatar-section">
                    <img id="avatarPreview"
                         class="edit-profile-avatar-img"
                         src="${avatarDisplay}"
                         alt="Ảnh đại diện"
                         onerror="this.src='${defaultAvatar}'">

                    <div class="edit-profile-avatar-controls">
                        <h4>Ảnh đại diện</h4>
                        <p>JPG, GIF hoặc PNG. Tối đa 2MB.</p>

                        <div class="edit-profile-avatar-actions">
                            <label class="edit-profile-btn-outline" for="avatarInput">Thay đổi</label>

                            <input id="avatarInput"
                                   class="edit-profile-file-input"
                                   type="file"
                                   name="avatar"
                                   accept="image/png,image/jpeg,image/gif">

                            <button id="removeAvatarButton"
                                    class="edit-profile-btn-text-danger"
                                    type="button">
                                Gỡ bỏ
                            </button>

                            <input id="removeAvatarInput"
                                   type="hidden"
                                   name="removeAvatar"
                                   value="false">
                        </div>
                    </div>
                </div>

                <div class="edit-profile-grid">

                    <div class="edit-profile-field edit-profile-field--full">
                        <label class="edit-profile-label" for="fullName">Họ và tên</label>

                        <input id="fullName"
                               class="edit-profile-control"
                               type="text"
                               name="fullName"
                               value="${fullNameInput}"
                               placeholder="Nhập họ và tên"
                               autocomplete="name"
                               required>
                    </div>

                    <div class="edit-profile-field edit-profile-field--full">
                        <label class="edit-profile-label" for="email">Email (Không thể thay đổi)</label>

                        <div class="edit-profile-input-lock">
                            <input id="email"
                                   class="edit-profile-control"
                                   type="email"
                                   value="${profileUser.email}"
                                   readonly>

                            <span class="material-symbols-outlined edit-profile-lock-icon">lock</span>
                        </div>
                    </div>

                    <div class="edit-profile-field edit-profile-field--full">
                        <label class="edit-profile-label" for="phone">Số điện thoại</label>

                        <input id="phone"
                               class="edit-profile-control"
                               type="tel"
                               name="phone"
                               value="${profileUser.phone}"
                               placeholder="Nhập số điện thoại"
                               autocomplete="tel"
                               required>
                    </div>

                    <div class="edit-profile-field">
                        <span class="edit-profile-label">Giới tính</span>

                        <div class="edit-profile-gender-options">
                            <label class="edit-profile-gender-option">
                                <input name="gender"
                                       type="radio"
                                       value="NAM"
                                       ${profileUser.gender == 'NAM' ? 'checked' : ''}>
                                <span>Nam</span>
                            </label>

                            <label class="edit-profile-gender-option">
                                <input name="gender"
                                       type="radio"
                                       value="NU"
                                       ${profileUser.gender == 'NU' ? 'checked' : ''}>
                                <span>Nữ</span>
                            </label>

                            <label class="edit-profile-gender-option">
                                <input name="gender"
                                       type="radio"
                                       value="UNISEX"
                                       ${profileUser.gender == 'UNISEX' ? 'checked' : ''}>
                                <span>Khác</span>
                            </label>
                        </div>
                    </div>

                    <div class="edit-profile-field">
                        <label class="edit-profile-label" for="dateOfBirth">Ngày sinh</label>

                        <input id="dateOfBirth"
                               class="edit-profile-control"
                               type="date"
                               name="dateOfBirth"
                               value="${dateOfBirthInput}">
                    </div>

                </div>

                <div class="edit-profile-actions">
                    <button class="edit-profile-btn-save" type="submit">
                        Lưu thay đổi
                    </button>

                    <a class="edit-profile-btn-cancel"
                       href="${pageContext.request.contextPath}/customer/profile">
                        Hủy
                    </a>
                </div>

            </form>

        </section>
    </main>

</div>

<jsp:include page="/common/footer.jsp" />

<script>
    const avatarInput = document.getElementById('avatarInput');
    const avatarPreview = document.getElementById('avatarPreview');
    const removeAvatarButton = document.getElementById('removeAvatarButton');
    const removeAvatarInput = document.getElementById('removeAvatarInput');
    const defaultAvatar = '${defaultAvatar}';

    avatarInput.addEventListener('change', function () {
        const file = this.files && this.files[0];

        if (!file) {
            return;
        }

        avatarPreview.src = URL.createObjectURL(file);
        removeAvatarInput.value = 'false';
    });

    removeAvatarButton.addEventListener('click', function () {
        avatarInput.value = '';
        avatarPreview.src = defaultAvatar;
        removeAvatarInput.value = 'true';
    });
</script>

</body>
</html>