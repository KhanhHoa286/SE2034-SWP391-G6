<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String verified = request.getParameter("verified");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập | MODA</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/public/login.css">
</head>

<body>

<header class="top-nav">
    <a class="brand" href="<%= request.getContextPath() %>/public/home/view-home.jsp">MODA</a>
</header>

<main class="login-page">

    <section class="login-visual">
        <img
                src="https://images2.thanhnien.vn/thumb_w/640/528068263637045248/2024/7/18/1-den-trang-decode-house-1721291431976503346008.jpg"
                alt="MODA Fashion"
        >

        <div class="visual-overlay"></div>

        <div class="visual-content">
            <h2>ĐỊNH NGHĨA SỰ SÁNG TẠO HIỆN ĐẠI</h2>
            <p>
                Khám phá MODA, nơi phong cách, cá tính và thời trang hiện đại được kết nối trong từng lựa chọn.
            </p>
        </div>
    </section>

    <section class="login-section">
        <div class="login-card">

            <div class="login-heading">
                <h1>Đăng nhập</h1>
                <p>Nhập thông tin chi tiết để truy cập tài khoản của bạn.</p>
            </div>

            <% if ("true".equals(verified)) { %>
            <div class="message message-success">
                Xác thực tài khoản thành công. Vui lòng tự nhập email và mật khẩu để đăng nhập.
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <div class="message message-error">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login"
                  method="post"
                  id="loginForm"
                  autocomplete="off">

                <div class="form-group">
                    <label for="email">EMAIL</label>
                    <input
                            type="email"
                            id="email"
                            name="email"
                            required
                            autocomplete="off"
                            placeholder="hello@example.com"
                    >
                </div>

                <div class="form-group">
                    <label for="password">MẬT KHẨU</label>
                    <input
                            type="password"
                            id="password"
                            name="password"
                            required
                            autocomplete="current-password"
                            placeholder="••••••••"
                    >
                </div>

                <div class="form-actions">
                    <label class="remember">
                        <input type="checkbox" name="remember">
                        <span>Ghi nhớ đăng nhập</span>
                    </label>

                    <a href="#" class="forgot-link">Quên mật khẩu?</a>
                </div>

                <button type="submit" class="submit-btn">
                    ĐĂNG NHẬP
                </button>
            </form>

            <p class="register-text">
                Chưa có tài khoản?
                <a href="<%= request.getContextPath() %>/register">Tạo tài khoản</a>
            </p>

        </div>
    </section>

</main>

<footer class="footer">
    <div class="footer-brand">MODA</div>

    <div class="footer-links">
        <a href="#">ĐIỀU KHOẢN</a>
        <a href="#">BẢO MẬT</a>
        <a href="#">VẬN CHUYỂN</a>
        <a href="#">LIÊN HỆ</a>
    </div>

    <p>© 2024 MODA. TẤT CẢ QUYỀN ĐƯỢC BẢO LƯU.</p>
</footer>

<script>
    const loginForm = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    function validateEmailInput() {
        const value = emailInput.value.trim();
        const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (value.length === 0) {
            emailInput.setCustomValidity("Email không được để trống.");
            return false;
        }

        if (!regex.test(value)) {
            emailInput.setCustomValidity("Email không hợp lệ. Ví dụ: example@gmail.com");
            return false;
        }

        emailInput.setCustomValidity("");
        return true;
    }

    function validatePasswordInput() {
        if (passwordInput.value.trim().length === 0) {
            passwordInput.setCustomValidity("Mật khẩu không được để trống.");
            return false;
        }

        passwordInput.setCustomValidity("");
        return true;
    }

    emailInput.addEventListener("input", validateEmailInput);
    passwordInput.addEventListener("input", validatePasswordInput);

    loginForm.addEventListener("submit", function (event) {
        const emailValid = validateEmailInput();
        const passwordValid = validatePasswordInput();

        if (!emailValid || !passwordValid) {
            event.preventDefault();
            loginForm.reportValidity();
            return;
        }

        const submitBtn = loginForm.querySelector(".submit-btn");
        submitBtn.textContent = "ĐANG XỬ LÝ...";
        submitBtn.classList.add("is-loading");
    });
</script>

</body>
</html>