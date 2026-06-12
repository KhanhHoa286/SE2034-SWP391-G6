<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String email = request.getParameter("email");

  if (email == null || email.trim().isEmpty()) {
    email = (String) request.getAttribute("email");
  }

  if (email == null) {
    email = "";
  }

  Object errorObj = request.getAttribute("error");
  Object messageObj = request.getAttribute("message");
  Object successObj = request.getAttribute("success");
%>
<%
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Xác thực OTP | MODA</title>
  <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>

  <style>
    body {
      font-family: 'Inter', sans-serif;
    }

    .otp-input:focus {
      outline: none;
      border-bottom-color: black;
    }
  </style>
</head>

<body class="bg-gray-50 text-black">

<header class="w-full bg-white border-b border-gray-300 px-8 py-4 flex justify-between items-center">
  <div class="text-xl font-bold">MODA</div>
</header>

<main class="min-h-screen flex flex-col md:flex-row">
  <section class="hidden md:block md:w-1/2">
    <img class="w-full h-screen object-cover"
         src="https://lh3.googleusercontent.com/aida-public/AB6AXuB8R4J4y0xhU_uMOtNxaIlkCrRNBAyAFrP8qotJJgFgRKwwkpi2N_iwfi4VA5wRKkkbKTkLOABi0LG9zKIuUEuEF2mnBAeS1QtHrCMJ56rilCCLWoh3JFG5JNA6J6Tx3q4Q-GdPRSeQctnZQzLWiwukBdiDJXqVuNJSyiaXxDzO02XGAEmMXR6AiMEUb7Qb-FPiWgq95tCszaa99axFFCvk62M2ID8wNnozUGNZWnwcxH4H4oGzE_0BwTy3ZJGyiFiYgMqk3pTRObs"
         alt="Banner">
  </section>

  <section class="w-full md:w-1/2 flex items-center justify-center p-6 md:p-16">
    <div class="w-full max-w-md">
      <h2 class="text-3xl font-bold mb-4">Xác thực OTP</h2>

      <p class="text-gray-600 mb-4">
        Nhập mã OTP 6 chữ số đã gửi đến email đăng ký của bạn:
        <span class="font-semibold"><%= email %></span>
      </p>

      <% if (errorObj != null) { %>
      <div class="mb-4 rounded border border-red-200 bg-red-50 px-4 py-3 text-red-600 text-sm font-medium">
        <%= errorObj %>
      </div>
      <% } %>

      <% if (messageObj != null) { %>
      <div class="mb-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-600 text-sm font-medium">
        <%= messageObj %>
      </div>
      <% } %>

      <% if (successObj != null) { %>
      <div class="mb-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-600 text-sm font-medium">
        <%= successObj %>
      </div>
      <% } %>

      <form id="otp-form"
            class="space-y-4"
            action="<%= request.getContextPath() %>/verify-otp"
            method="post">

        <input type="hidden" name="email" value="<%= email %>"/>
        <input type="hidden" name="otp" id="otp"/>

        <div class="flex justify-between gap-2">
          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">

          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">

          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">

          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">

          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">

          <input type="text" maxlength="1" inputmode="numeric" required
                 class="otp-input w-full border-b border-gray-300 bg-transparent text-center py-2 text-lg">
        </div>

        <div class="flex justify-between items-center mt-2">
          <span id="timer" class="text-gray-600 text-sm">01:00</span>

          <button type="submit"
                  form="resend-form"
                  id="resend-btn"
                  class="text-sm text-black hover:underline">
            Gửi lại mã
          </button>
        </div>

        <button type="submit"
                class="w-full bg-black text-white py-3 font-semibold uppercase hover:bg-gray-800 transition">
          Xác nhận
        </button>
      </form>

      <form id="resend-form"
            action="<%= request.getContextPath() %>/resend-otp"
            method="post">
        <input type="hidden" name="email" value="<%= email %>"/>
      </form>

      <form id="cancel-pending-form"
            action="<%= request.getContextPath() %>/cancel-pending-registration"
            method="post"
            class="mt-5 text-center">

        <input type="hidden" name="email" value="<%= email %>"/>

        <button type="submit"
                class="text-sm text-gray-600 hover:text-black hover:underline"
                onclick="return confirm('Bạn muốn hủy yêu cầu đăng ký hiện tại để nhập lại thông tin từ đầu?');">
          Sửa thông tin đăng ký
        </button>
      </form>

      <div class="mt-4 text-center">
        <a href="<%= request.getContextPath() %>/login?exitOtp=true"
           class="text-sm text-gray-500 hover:text-black hover:underline">
          Quay lại đăng nhập
        </a>
      </div>
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

  /*
   * Timer chỉ để hiển thị 1 phút của OTP hiện tại.
   * Không khóa nút gửi lại mã.
   */
  let timeLeft = 60;
  const timerEl = document.getElementById('timer');

  const countdown = setInterval(() => {
    const minute = Math.floor(timeLeft / 60);
    const second = timeLeft % 60;

    timerEl.innerText =
            String(minute).padStart(2, '0') + ':' + String(second).padStart(2, '0');

    if (timeLeft <= 0) {
      clearInterval(countdown);
      timerEl.innerText = "00:00";
      return;
    }

    timeLeft--;
  }, 1000);

  document.getElementById('otp-form').addEventListener('submit', e => {
    const otpCode = Array.from(inputs).map(i => i.value).join('');

    if (otpCode.length !== 6) {
      e.preventDefault();
      alert("Vui lòng nhập đầy đủ 6 chữ số OTP.");
      return;
    }

    document.getElementById('otp').value = otpCode;
  });
</script>

</body>
</html>