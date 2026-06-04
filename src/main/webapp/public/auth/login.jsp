<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demo Login - MODA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background: #f5f6fb;
            color: #07111f;
            font-family: Arial, Helvetica, sans-serif;
        }

        .login-shell {
            display: grid;
            place-items: center;
            min-height: 100vh;
            padding: 32px 18px;
        }

        .login-card {
            width: min(940px, 100%);
            display: grid;
            grid-template-columns: 1fr 1.1fr;
            background: #ffffff;
            border: 1px solid #d1c7c7;
        }

        .login-panel {
            padding: 42px;
            border-right: 1px solid #d1c7c7;
        }

        .login-panel h1 {
            margin: 0 0 10px;
            font-size: 30px;
            font-weight: 800;
        }

        .login-panel p {
            margin: 0 0 28px;
            color: #4b5563;
            line-height: 1.6;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 9px;
            margin-bottom: 18px;
        }

        .form-group label {
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .form-control {
            min-height: 48px;
            padding: 0 14px;
            border: 1px solid #cfc7c7;
            background: #f6f7fb;
            color: #07111f;
            font-size: 14px;
            outline: none;
        }

        .form-control:focus {
            border-color: #07111f;
            background: #ffffff;
        }

        .login-button {
            width: 100%;
            min-height: 50px;
            margin-top: 8px;
            border: 0;
            background: #000000;
            color: #ffffff;
            cursor: pointer;
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 1px;
            text-transform: uppercase;
        }

        .alert-error {
            margin-bottom: 18px;
            padding: 13px 15px;
            border: 1px solid #fecaca;
            background: #fef2f2;
            color: #b91c1c;
            font-size: 13px;
            font-weight: 700;
        }

        .demo-panel {
            padding: 42px;
            background: #fafafa;
        }

        .demo-panel h2 {
            margin: 0 0 16px;
            font-size: 14px;
            font-weight: 800;
            letter-spacing: 2.5px;
            text-transform: uppercase;
        }

        .demo-list {
            display: grid;
            gap: 12px;
        }

        .demo-account {
            width: 100%;
            padding: 16px;
            border: 1px solid #d1c7c7;
            background: #ffffff;
            cursor: pointer;
            text-align: left;
        }

        .demo-account strong {
            display: block;
            margin-bottom: 4px;
            color: #07111f;
            font-size: 14px;
        }

        .demo-account span {
            display: block;
            color: #4b5563;
            font-size: 13px;
            line-height: 1.45;
        }

        .demo-note {
            margin-top: 18px;
            color: #4b5563;
            font-size: 13px;
            line-height: 1.6;
        }

        @media (max-width: 760px) {
            .login-card {
                grid-template-columns: 1fr;
            }

            .login-panel {
                border-right: 0;
                border-bottom: 1px solid #d1c7c7;
            }
        }
    </style>
</head>
<body>
<main class="login-shell">
    <section class="login-card">
        <div class="login-panel">
            <h1>Demo Login</h1>
            <p>Đăng nhập bằng tài khoản seller đã có shop để test màn Add Payout Account.</p>

            <c:if test="${not empty errorMessage}">
                <div class="alert-error">${errorMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST">
                <input type="hidden" name="redirect" value="${empty redirect ? '/seller/finance/view-wallet' : redirect}">

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email"
                           id="email"
                           name="email"
                           class="form-control"
                           placeholder="seller1@gmail.com"
                           value="${empty email ? 'seller1@gmail.com' : email}"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">Mật khẩu demo</label>
                    <input type="password"
                           id="password"
                           name="password"
                           class="form-control"
                           value="123456"
                           required>
                </div>

                <button type="submit" class="login-button">Đăng nhập để test</button>
            </form>
        </div>

        <aside class="demo-panel">
            <h2>Tài khoản đủ điều kiện</h2>
            <div class="demo-list">
                <button type="button" class="demo-account" data-email="seller1@gmail.com">
                    <strong>seller1@gmail.com</strong>
                    <span>Role SELLER, owner shop 1 - Men Shop, có ví seller.</span>
                </button>
                <button type="button" class="demo-account" data-email="hoahuy20@gmail.com">
                    <strong>hoahuy20@gmail.com</strong>
                    <span>Role SELLER, owner shop 2 - Cloth Store, có ví seller.</span>
                </button>
                <button type="button" class="demo-account" data-email="kimmita963@gmail.com">
                    <strong>kimmita963@gmail.com</strong>
                    <span>Role SELLER, owner shop 3 - Fashion Store, có ví seller.</span>
                </button>
            </div>
            <p class="demo-note">
                Password demo: <strong>123456</strong>. Servlet cũng chấp nhận password seed
                <strong>hashed_pass</strong> để tiện test nhanh với database hiện tại.
            </p>
        </aside>
    </section>
</main>

<script>
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    document.querySelectorAll('.demo-account').forEach(function (button) {
        button.addEventListener('click', function () {
            emailInput.value = this.dataset.email;
            passwordInput.value = '123456';
            emailInput.focus();
        });
    });
</script>
</body>
</html>
