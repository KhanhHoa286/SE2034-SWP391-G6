<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="profileUser" value="${requestScope.profileUser}" />
<c:set var="defaultAvatar" value="${requestScope.defaultAvatar}" />
<c:set var="avatarUrl" value="${requestScope.avatarUrl}" />

<!DOCTYPE html>

<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Hồ sơ cá nhân</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">


<!-- Google Font -->
<link href="https://fonts.googleapis.com" rel="preconnect">
<link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

<!-- Material Symbols cho sidebar/profile -->
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

<!-- Font Awesome cho public/header.jsp -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<!-- Bootstrap nếu public/header.jsp đang dùng class Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- CSS chung của project -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">

<!-- CSS riêng profile, đặt cuối để không bị đè -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=20260722-shop-toast">


</head>

<body class="profile-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout">


<!-- Sidebar -->
    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="profile" />
    </jsp:include>

<!-- Main Content -->
<main class="profile-main">
    <div class="profile-container">

<%--        <!-- Back Button -->--%>
<%--        <div class="profile-back">--%>
<%--            <a href="${pageContext.request.contextPath}/home">--%>
<%--                <span class="material-symbols-outlined">arrow_back</span>--%>
<%--                <span>Quay lại</span>--%>
<%--            </a>--%>
<%--        </div>--%>

        <c:if test="${not empty successMessage}">
            <div class="profile-alert profile-alert--success">
                <c:out value="${successMessage}" />
            </div>
        </c:if>

        <c:if test="${hasRejectedSellerRegistration}">
            <div class="profile-alert profile-alert--error profile-rejected-shop-alert">
                <span>Yêu cầu đăng kí shop đã bị từ chối, vui lòng điền đúng thông tin theo yêu cầu.</span>
                <form action="${pageContext.request.contextPath}/customer/profile" method="post">
                    <input type="hidden" name="action" value="confirmRejectedShop">
                    <button type="submit">Xác nhận</button>
                </form>
            </div>
        </c:if>

        <c:if test="${param.retryConfirmed == '1'}">
            <div class="profile-alert profile-alert--success">
                Đã xác nhận. Vui lòng chọn “Đăng ký người bán” để cập nhật và gửi lại thông tin shop.
            </div>
        </c:if>

        <c:if test="${param.retryError == '1'}">
            <div class="profile-alert profile-alert--error">
                Không thể xác nhận yêu cầu đăng ký lại. Vui lòng tải lại trang và thử lại.
            </div>
        </c:if>

        <!-- Header & Action -->
        <div class="profile-title-row">
            <div>
                <h1>Hồ sơ cá nhân</h1>
                <p>Xem thông tin chi tiết tài khoản và thông tin thành viên của bạn.</p>
            </div>

            <a class="profile-edit-btn" href="${pageContext.request.contextPath}/customer/profile/edit">
                <span class="material-symbols-outlined">edit</span>
                <span>Chỉnh sửa hồ sơ</span>
            </a>
        </div>

        <!-- Profile Card -->
        <section class="profile-card">

            <!-- Left Column -->
            <div class="profile-card__left">
                <div class="profile-avatar-wrap">
                    <img class="profile-avatar"
                         src="${avatarUrl}"
                         alt="Ảnh đại diện"
                         onerror="this.src='${defaultAvatar}'">
                </div>

                <h3>
                    <c:out value="${fullNameText}" />
                </h3>

                <p>Thành viên MODA</p>
            </div>

            <!-- Right Column -->
            <div class="profile-card__right">

                <div class="profile-info-row">
                    <label>Họ và tên</label>
                    <div>
                        <c:out value="${fullNameText}" />
                    </div>
                </div>

                <div class="profile-info-row">
                    <label>Địa chỉ Email</label>
                    <div>
                        <c:out value="${emailText}" />
                    </div>
                </div>

                <div class="profile-info-row">
                    <label>Số điện thoại</label>
                    <div>
                        <span class="${phoneMissing ? 'profile-empty' : ''}">
                            <c:out value="${phoneText}" />
                        </span>
                    </div>
                </div>

                <div class="profile-info-row">
                    <label>Giới tính</label>
                    <div>
                        <span class="${genderMissing ? 'profile-empty' : ''}">
                            <c:out value="${genderText}" />
                        </span>
                    </div>
                </div>

                <div class="profile-info-row">
                    <label>Ngày sinh</label>
                    <div>
                        <span class="${dateOfBirthMissing ? 'profile-empty' : ''}">
                            <c:out value="${dateOfBirthText}" />
                        </span>
                    </div>
                </div>

                <div class="profile-info-row">
                    <label>Thành viên từ</label>
                    <div>
                        <span class="${createdAtMissing ? 'profile-empty' : ''}">
                            <c:out value="${createdAtText}" />
                        </span>
                    </div>
                </div>

            </div>

        </section>

    </div>
</main>


</div>

</body>
</html>
