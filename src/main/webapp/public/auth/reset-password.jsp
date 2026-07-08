<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Đặt lại mật khẩu</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/reset-password.css?v=1">
</head>

<body class="reset-body">

<main class="reset-main">
    <section class="reset-card">

        <div class="reset-header">
            <h1>Đặt lại mật khẩu</h1>
            <p>Vui lòng nhập mật khẩu mới của bạn bên dưới.</p>
        </div>

        <c:if test="${not empty error}">
            <p class="reset-error">
                <c:out value="${error}" />
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/reset-password"
              method="post"
              class="reset-form"
              id="resetPasswordForm">

            <div class="reset-field">
                <label for="password">Mật khẩu mới</label>

                <div class="reset-input-wrap">
                    <input type="password"
                           id="password"
                           name="password"
                           required>

                    <button type="button"
                            class="reset-toggle-password"
                            data-target="password">
                        <span class="material-symbols-outlined">visibility</span>
                    </button>
                </div>
            </div>

            <div class="reset-field">
                <label for="confirmPassword">Xác nhận mật khẩu mới</label>

                <div class="reset-input-wrap">
                    <input type="password"
                           id="confirmPassword"
                           name="confirmPassword"
                           required>

                    <button type="button"
                            class="reset-toggle-password"
                            data-target="confirmPassword">
                        <span class="material-symbols-outlined">visibility</span>
                    </button>
                </div>
            </div>

            <button type="submit" class="reset-submit">
                Lưu mật khẩu
            </button>

            <div class="reset-back">
                <a href="${pageContext.request.contextPath}/login">
                    <span class="material-symbols-outlined">arrow_back</span>
                    Quay lại Đăng nhập
                </a>
            </div>
        </form>

    </section>
</main>

<script>
    document.querySelectorAll(".reset-toggle-password").forEach(function (button) {
        button.addEventListener("click", function () {
            const targetId = this.getAttribute("data-target");
            const input = document.getElementById(targetId);
            const icon = this.querySelector(".material-symbols-outlined");

            if (input.type === "password") {
                input.type = "text";
                icon.textContent = "visibility_off";
            } else {
                input.type = "password";
                icon.textContent = "visibility";
            }
        });
    });
</script>

</body>
</html>