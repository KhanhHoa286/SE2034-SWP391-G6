<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();

    String email = request.getParameter("email");

    if (email == null || email.trim().isEmpty()) {
        email = (String) request.getAttribute("email");
    }

    if (email == null) {
        email = "";
    }

    email = email.trim();

    String safeEmail = email
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");

    Object errorObj = request.getAttribute("error");
    Object messageObj = request.getAttribute("message");
    Object successObj = request.getAttribute("success");

    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực tài khoản | MODA</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap"
          rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
          rel="stylesheet">

    <link rel="stylesheet" href="<%= ctx %>/assets/css/public/verify-otp.css">
</head>

<body>
<div class="otp-page">

    <header class="otp-header"></header>

    <main class="otp-main">
        <section class="otp-card">

            <!-- Nút QUAY LẠI mới: xóa user PENDING rồi quay về /register -->
            <form id="cancel-pending-form"
                  action="<%= ctx %>/cancel-pending-registration"
                  method="post"
                  class="back-form">

                <input type="hidden" name="email" value="<%= safeEmail %>">

                <button type="submit" class="back-button">
                    <span class="material-symbols-outlined back-icon">arrow_back</span>
                    <span>QUAY LẠI</span>
                </button>
            </form>

            <h1 class="otp-title">Xác thực tài khoản</h1>

            <p class="otp-description">
                Vui lòng nhập mã OTP gồm 6 chữ số đã được gửi đến email
                <strong><%= safeEmail %></strong>.
            </p>

            <% if (errorObj != null) { %>
            <div class="alert alert-error">
                <%= errorObj %>
            </div>
            <% } %>

            <% if (messageObj != null) { %>
            <div class="alert alert-info">
                <%= messageObj %>
            </div>
            <% } %>

            <% if (successObj != null) { %>
            <div class="alert alert-success">
                <%= successObj %>
            </div>
            <% } %>

            <!-- Form xác nhận OTP -->
            <form id="otp-form"
                  action="<%= ctx %>/verify-otp"
                  method="post"
                  class="otp-form">

                <input type="hidden" name="email" value="<%= safeEmail %>">
                <input type="hidden" name="otp" id="otp">

                <div class="otp-input-group">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric" autocomplete="one-time-code">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric">
                    <input class="otp-input" maxlength="1" required type="text" inputmode="numeric">
                </div>

                <div class="otp-action-row">
                    <span id="timer" class="otp-timer">01:00</span>

                    <button id="resend-btn"
                            type="button"
                            class="resend-button disabled"
                            disabled>
                        Gửi lại mã
                    </button>
                </div>

                <button id="submit-btn" type="submit" class="confirm-button">
                    XÁC NHẬN
                </button>
            </form>

            <!-- Form gửi lại OTP -->
            <form id="resend-form"
                  action="<%= ctx %>/resend-otp"
                  method="post"
                  class="hidden-form">

                <input type="hidden" name="email" value="<%= safeEmail %>">
            </form>

        </section>
    </main>

    <footer class="otp-footer"></footer>
</div>

<script>
    const inputs = document.querySelectorAll('.otp-input');
    const otpForm = document.getElementById('otp-form');
    const otpHiddenInput = document.getElementById('otp');
    const timerElement = document.getElementById('timer');
    const resendBtn = document.getElementById('resend-btn');
    const submitBtn = document.getElementById('submit-btn');
    const resendForm = document.getElementById('resend-form');

    inputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');

            if (e.target.value.length > 1) {
                e.target.value = e.target.value.slice(0, 1);
            }

            if (e.target.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
            }
        });

        input.addEventListener('paste', (e) => {
            e.preventDefault();

            const pastedText = (e.clipboardData || window.clipboardData)
                .getData('text')
                .replace(/[^0-9]/g, '');

            if (!pastedText) {
                return;
            }

            inputs.forEach((otpInput, i) => {
                otpInput.value = pastedText[i] || '';
            });

            const focusIndex = Math.min(pastedText.length, inputs.length) - 1;
            inputs[focusIndex].focus();
        });
    });

    let timeLeft = 60;

    const countdown = setInterval(() => {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;

        timerElement.innerText =
            String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');

        if (timeLeft <= 0) {
            clearInterval(countdown);

            timerElement.innerText = 'Mã OTP đã hết hạn';
            timerElement.classList.add('expired');

            resendBtn.disabled = false;
            resendBtn.classList.remove('disabled');

            submitBtn.disabled = true;
            submitBtn.classList.add('disabled');

            inputs.forEach(input => {
                input.disabled = true;
                input.classList.add('disabled');
            });

            return;
        }

        timeLeft--;
    }, 1000);

    resendBtn.addEventListener('click', () => {
        resendBtn.disabled = true;
        resendBtn.innerText = 'ĐANG GỬI...';
        resendForm.submit();
    });

    otpForm.addEventListener('submit', (e) => {
        const otpCode = Array.from(inputs).map(input => input.value).join('');

        if (otpCode.length !== 6) {
            e.preventDefault();
            alert('Vui lòng nhập đầy đủ 6 chữ số OTP.');
            return;
        }

        otpHiddenInput.value = otpCode;

        submitBtn.innerText = 'ĐANG XỬ LÝ...';
        submitBtn.classList.add('disabled');
    });
</script>
</body>
</html>