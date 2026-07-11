<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Quên mật khẩu</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/forgot-password.css?v=1">
</head>

<body class="forgot-password-body">

<main class="forgot-password-main">
    <section class="forgot-password-card">

        <h1>Quên mật khẩu?</h1>

        <p class="forgot-password-desc">
            Vui lòng nhập địa chỉ email liên kết với tài khoản của bạn.
            Chúng tôi sẽ gửi một mã OTP để bạn đặt lại mật khẩu mới.
        </p>

        <c:if test="${not empty error}">
            <p class="forgot-password-error">
                <c:out value="${error}" />
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/forgot-password"
              method="post"
              class="forgot-password-form">

            <div class="forgot-password-field">
                <label for="email">Địa chỉ Email</label>

                <input type="email"
                       id="email"
                       name="email"
                       value="<c:out value='${email}' />"
                       placeholder="yourname@domain.com"
                       required>
            </div>

            <button type="submit" class="forgot-password-submit">
                Gửi liên kết
            </button>
        </form>

        <div class="forgot-password-back">
            <a href="${pageContext.request.contextPath}/login">
                <span class="material-symbols-outlined">arrow_back</span>
                Quay lại Đăng nhập
            </a>
        </div>

    </section>
</main>

</body>
</html>