<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String email = request.getParameter("email");

  if (email == null || email.trim().isEmpty()) {
    email = (String) request.getAttribute("email");
  }

  if (email == null) {
    email = "";
  }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Xác thực OTP | MODA</title>
  <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
  <style>
    .otp-input:focus { outline: none; border-bottom-color: black; }
  </style>
</head>
<body class="bg-gray-50 text-black font-body-md">

<header class="w-full bg-white border-b border-gray-300 px-8 py-4 flex justify-between items-center">
  <div class="text-xl font-bold">MODA</div>
</header>

<main class="min-h-screen flex flex-col md:flex-row">
  <section class="hidden md:block md:w-1/2">
    <img class="w-full h-screen object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuB8R4J4y0xhU_uMOtNxaIlkCrRNBAyAFrP8qotJJgFgRKwwkpi2N_iwfi4VA5wRKkkbKTkLOABi0LG9zKIuUEuEF2mnBAeS1QtHrCMJ56rilCCLWoh3JFG5JNA6J6Tx3q4Q-GdPRSeQctnZQzLWiwukBdiDJXqVuNJSyiaXxDzO02XGAEmMXR6AiMEUb7Qb-FPiWgq95tCszaa99axFFCvk62M2ID8wNnozUGNZWnwcxH4H4oGzE_0BwTy3ZJGyiFiYgMqk3pTRObs" alt="Banner">
  </section>

  <section class="w-full md:w-1/2 flex items-center justify-center p-6 md:p-16">
    <div class="w-full max-w-md">
      <h2 class="text-3xl font-bold mb-4">Xác thực OTP</h2>

      <p class="text-gray-600 mb-4">
        Nhập mã OTP 6 chữ số đã gửi đến Email đăng kí của bạn
        <span class="font-semibold"><%= email %></span>
      </p>

      <% if (request.getAttribute("error") != null) { %>
      <div class="mb-4 text-red-600 text-sm font-medium">
        <%= request.getAttribute("error") %>
      </div>
      <% } %>

      <% if (request.getAttribute("message") != null) { %>
      <div class="mb-4 text-green-600 text-sm font-medium">
        <%= request.getAttribute("message") %>
      </div>
      <% } %>

      <form id="otp-form" class="space-y-4" action="<%= request.getContextPath() %>/verify-otp" method="post">
        <input type="hidden" name="email" value="<%= email %>"/>
        <input type="hidden" name="otp" id="otp"/>

        <div class="flex justify-between gap-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
          <input type="text" maxlength="1" inputmode="numeric" required class="otp-input w-full border-b border-gray-300 text-center py-2">
        </div>

        <div class="flex justify-between items-center mt-2">
          <span id="timer" class="text-gray-600">01:00</span>
          <button type="submit"
                  form="resend-form"
                  id="resend-btn"
                  disabled
                  class="text-gray-400 cursor-not-allowed">
            Gửi lại mã
          </button>
        </div>

        <button type="submit" class="w-full bg-black text-white py-3 font-semibold uppercase hover:bg-gray-800 transition">
          Xác nhận
        </button>
      </form>

      <form id="resend-form" action="<%= request.getContextPath() %>/resend-otp" method="post">
        <input type="hidden" name="email" value="<%= email %>"/>
      </form>
    </div>
  </section>
</main>

<script>
  const inputs = document.querySelectorAll('.otp-input');

  inputs.forEach((input, index) => {
    input.addEventListener('input', e => {
      e.target.value = e.target.value.replace(/[^0-9]/g, '');

      if (e.target.value && index < inputs.length - 1) {
        inputs[index + 1].focus();
      }
    });

    input.addEventListener('keydown', e => {
      if (e.key === 'Backspace' && !e.target.value && index > 0) {
        inputs[index - 1].focus();
      }
    });

    input.addEventListener('paste', e => {
      e.preventDefault();

      const pasteData = (e.clipboardData || window.clipboardData)
              .getData('text')
              .replace(/[^0-9]/g, '');

      if (pasteData.length === 6) {
        inputs.forEach((otpInput, i) => {
          otpInput.value = pasteData[i] || '';
        });
        inputs[5].focus();
      }
    });
  });

  let timeLeft = 60;
  const timerEl = document.getElementById('timer');
  const resendBtn = document.getElementById('resend-btn');

  const countdown = setInterval(() => {
    const minute = Math.floor(timeLeft / 60);
    const second = timeLeft % 60;

    timerEl.innerText =
            String(minute).padStart(2, '0') + ':' + String(second).padStart(2, '0');

    if (timeLeft <= 0) {
      clearInterval(countdown);
      timerEl.innerText = "00:00";

      resendBtn.disabled = false;
      resendBtn.classList.remove('text-gray-400', 'cursor-not-allowed');
      resendBtn.classList.add('text-black', 'hover:underline');
      return;
    }

    timeLeft--;
  }, 1000);

  document.getElementById('otp-form').addEventListener('submit', e => {
    const otpCode = Array.from(inputs).map(i => i.value).join('');

    if (otpCode.length !== 6) {
      e.preventDefault();
      alert("Vui lòng nhập đầy đủ 6 chữ số.");
      return;
    }

    document.getElementById('otp').value = otpCode;
  });
</script>

</body>
</html>