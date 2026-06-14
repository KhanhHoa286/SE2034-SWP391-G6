<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private String h(Object value) {
        if (value == null) return "";
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Đăng ký tài khoản | MODA</title>

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link crossorigin href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
          rel="stylesheet">

    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: {
                        sans: ['Inter', 'sans-serif'],
                    },
                    colors: {
                        'moda-black': '#000000',
                        'moda-gray': '#71717a',
                        'moda-bg': '#f8f9ff',
                        'moda-border': '#e4e4e7',
                    }
                }
            }
        }
    </script>

    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/public/register.css">
</head>

<body class="bg-moda-bg">

<header class="w-full bg-white border-b border-moda-border px-6 lg:px-12 py-4 z-50 relative h-[72px] flex items-center">
    <div class="max-w-[1440px]">
        <a href="<%= request.getContextPath() %>/home"
           class="text-3xl font-bold tracking-tighter text-moda-black uppercase">
            MODA
        </a>
    </div>
</header>

<main class="split-container w-full">

    <section class="hidden lg:block relative overflow-hidden" data-purpose="editorial-image">
        <img
                alt="MODA Editorial Fashion"
                class="absolute inset-0 w-full h-full object-cover"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAKfh4C_YuubRKatEGr2GNRQmFVFGZfu1ife8wBuqQ10IPHjRj782a3auERnhXHsz73Vx3j9oOMZoOTZ16KmBcWpUlTvetOHzsV0LhQZrEYkipLHZ4qt1WqFVHAfnozMnfswdoeF9b7fuJpyq_1-SixZmKts9CqQmtTXyM5p2LGAMGpdtrZdhP2LFeOTpffFupuaRzoyHoB660VvYCJS1NGnNUFlComXIvO6AcUZzTnsV7WwiF24cuQjXzdLQiC2c9NBgizae67Iy4">
    </section>

    <section class="flex items-center justify-center p-6 md:p-12 lg:p-14"
             data-purpose="registration-form-container">

        <div class="w-full max-w-[560px] space-y-8">

            <header class="space-y-2">
                <h1 class="text-3xl font-bold tracking-tight text-moda-black">
                    Đăng ký tài khoản
                </h1>

                <p class="text-moda-gray font-medium">
                    Tham gia để trải nghiệm dịch vụ MODA
                </p>
            </header>

            <% if (request.getAttribute("error") != null) { %>
            <div class="text-sm font-semibold text-red-600">
                <%= h(request.getAttribute("error")) %>
            </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register"
                  class="space-y-6"
                  method="post"
                  id="registerForm"
                  novalidate>

                <%-- UI gọi là User, nhưng DB hiện tại vẫn dùng role CUSTOMER --%>
                <input type="hidden" name="accountType" value="CUSTOMER">

                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <label class="block text-sm font-semibold text-moda-black" for="firstName">
                            Họ
                        </label>

                        <input
                                class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                                id="firstName"
                                name="firstName"
                                placeholder="Nguyễn"
                                required
                                maxlength="50"
                                autocomplete="family-name"
                                value="<%= h(request.getAttribute("firstName")) %>"
                                type="text">

                        <p id="firstNameError" class="field-error"></p>
                    </div>

                    <div class="space-y-1">
                        <label class="block text-sm font-semibold text-moda-black" for="lastName">
                            Tên
                        </label>

                        <input
                                class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                                id="lastName"
                                name="lastName"
                                placeholder="Văn A"
                                required
                                maxlength="50"
                                autocomplete="given-name"
                                value="<%= h(request.getAttribute("lastName")) %>"
                                type="text">

                        <p id="lastNameError" class="field-error"></p>
                    </div>
                </div>

                <div class="space-y-1">
                    <label class="block text-sm font-semibold text-moda-black" for="phone">
                        Số điện thoại
                    </label>

                    <p class="text-[11px] text-moda-gray mb-1">
                        Số điện thoại Việt Nam gồm 10 số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.
                    </p>

                    <input
                            class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                            id="phone"
                            name="phone"
                            placeholder="0923456789"
                            required
                            maxlength="10"
                            inputmode="numeric"
                            autocomplete="tel"
                            value="<%= h(request.getAttribute("phone")) %>"
                            type="tel">

                    <p id="phoneError" class="field-error"></p>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <label class="block text-sm font-semibold text-moda-black" for="dob">
                            Ngày sinh (không bắt buộc)
                        </label>

                        <input
                                class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all uppercase text-sm h-12"
                                id="dob"
                                name="dob"
                                value="<%= h(request.getAttribute("dob")) %>"
                                type="date">

                        <p id="dobError" class="field-error"></p>
                    </div>

                    <div class="space-y-1">
                        <label class="block text-sm font-semibold text-moda-black" for="gender">
                            Giới tính (không bắt buộc)
                        </label>

                        <select
                                class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all bg-white text-sm h-12"
                                id="gender"
                                name="gender">

                            <option value="" <%= "".equals(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Chọn giới tính
                            </option>

                            <option value="nam" <%= "nam".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Nam
                            </option>

                            <option value="nu" <%= "nu".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Nữ
                            </option>

                            <%-- Giữ giống giao diện Stitch, value rỗng để backend không lỗi nếu chưa hỗ trợ giới tính khác --%>
                            <option value="">
                                Khác
                            </option>
                        </select>

                        <p id="genderError" class="field-error"></p>
                    </div>
                </div>

                <div class="space-y-1">
                    <label class="block text-sm font-semibold text-moda-black" for="email">
                        Email / Tên đăng nhập
                    </label>

                    <p class="text-[11px] text-moda-gray mb-1">
                        Email cần đúng định dạng. ví dụ: example@gmail.com.
                    </p>

                    <input
                            class="w-full px-4 py-3 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                            id="email"
                            name="email"
                            placeholder="example@gmail.com"
                            required
                            autocomplete="email"
                            value="<%= h(request.getAttribute("email")) %>"
                            type="email">

                    <p id="emailError" class="field-error"></p>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <label class="block text-sm font-semibold text-moda-black" for="password">
                            Mật khẩu
                        </label>

                        <p class="text-[11px] text-moda-gray mb-1 leading-tight">
                            Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.
                        </p>

                        <div class="relative">
                            <input
                                    class="w-full px-4 py-3 pr-11 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                                    id="password"
                                    name="password"
                                    required
                                    minlength="6"
                                    maxlength="32"
                                    inputmode="numeric"
                                    autocomplete="new-password"
                                    value="<%= h(request.getAttribute("password")) %>"
                                    type="password">

                            <button aria-label="Toggle password visibility"
                                    class="password-toggle-btn"
                                    type="button"
                                    onclick="togglePasswordVisibility('password')">
                                <svg class="w-5 h-5"
                                     fill="none"
                                     stroke="currentColor"
                                     stroke-width="1.5"
                                     viewBox="0 0 24 24"
                                     xmlns="http://www.w3.org/2000/svg">
                                    <path d="M2.036 12.322a1.012 1.012 0 0 1 0-.644C3.301 8.216 7.066 5.25 12 5.25c4.935 0 8.701 2.966 9.964 6.428.068.185.068.388 0 .573-1.263 3.462-5.03 6.428-9.964 6.428-4.935 0-8.701-2.966-9.964-6.428ZM12 15.75a3.75 3.75 0 1 0 0-7.5 3.75 3.75 0 0 0 0 7.5Z"
                                          stroke-linecap="round"
                                          stroke-linejoin="round"></path>
                                    <path d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
                                          stroke-linecap="round"
                                          stroke-linejoin="round"></path>
                                </svg>
                            </button>
                        </div>

                        <p id="passwordError" class="field-error"></p>
                    </div>

                    <div class="space-y-1 password-confirm-block">
                        <label class="block text-sm font-semibold text-moda-black" for="confirm_password">
                            Xác nhận mật khẩu
                        </label>

                        <p class="text-[11px] text-moda-gray mb-1 leading-tight">
                            Nhập lại mật khẩu đã chọn ở trên để xác nhận.
                        </p>

                        <div class="relative">
                            <input
                                    class="w-full px-4 py-3 pr-11 border border-moda-border rounded-none focus:ring-1 focus:ring-moda-black focus:border-moda-black transition-all h-12"
                                    id="confirm_password"
                                    name="confirm_password"
                                    required
                                    minlength="6"
                                    maxlength="32"
                                    inputmode="numeric"
                                    autocomplete="new-password"
                                    value="<%= h(request.getAttribute("confirmPassword")) %>"
                                    type="password">

                            <button aria-label="Toggle password visibility"
                                    class="password-toggle-btn"
                                    type="button"
                                    onclick="togglePasswordVisibility('confirm_password')">
                                <svg class="w-5 h-5"
                                     fill="none"
                                     stroke="currentColor"
                                     stroke-width="1.5"
                                     viewBox="0 0 24 24"
                                     xmlns="http://www.w3.org/2000/svg">
                                    <path d="M2.036 12.322a1.012 1.012 0 0 1 0-.644C3.301 8.216 7.066 5.25 12 5.25c4.935 0 8.701 2.966 9.964 6.428.068.185.068.388 0 .573-1.263 3.462-5.03 6.428-9.964 6.428-4.935 0-8.701-2.966-9.964-6.428ZM12 15.75a3.75 3.75 0 1 0 0-7.5 3.75 3.75 0 0 0 0 7.5Z"
                                          stroke-linecap="round"
                                          stroke-linejoin="round"></path>
                                    <path d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
                                          stroke-linecap="round"
                                          stroke-linejoin="round"></path>
                                </svg>
                            </button>
                        </div>

                        <p id="confirmPasswordError" class="field-error"></p>
                    </div>
                </div>

                <button
                        class="w-full bg-moda-black text-white font-bold py-4 text-sm tracking-widest hover:opacity-90 transition-opacity"
                        type="submit">
                    ĐĂNG KÝ
                </button>
            </form>

            <footer class="text-center pt-4">
                <p class="text-sm text-moda-gray">
                    Đã có tài khoản?
                    <a class="text-moda-black font-bold hover:underline"
                       href="<%= request.getContextPath() %>/public/auth/login.jsp">
                        Đăng nhập
                    </a>
                </p>
            </footer>
        </div>
    </section>
</main>

<script>
    const dobInput = document.getElementById("dob");
    if (dobInput) {
        dobInput.max = new Date().toISOString().split("T")[0];
    }

    const registerForm = document.getElementById("registerForm");

    const firstNameInput = document.getElementById("firstName");
    const lastNameInput = document.getElementById("lastName");
    const phoneInput = document.getElementById("phone");
    const dobError = document.getElementById("dobError");
    const genderInput = document.getElementById("gender");
    const genderError = document.getElementById("genderError");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirm_password");

    const firstNameError = document.getElementById("firstNameError");
    const lastNameError = document.getElementById("lastNameError");
    const phoneError = document.getElementById("phoneError");
    const emailError = document.getElementById("emailError");
    const passwordError = document.getElementById("passwordError");
    const confirmPasswordError = document.getElementById("confirmPasswordError");

    function showError(input, errorElement, message) {
        if (input) {
            input.setCustomValidity(message);
        }

        if (errorElement) {
            errorElement.innerText = message;
        }
    }

    function clearError(input, errorElement) {
        if (input) {
            input.setCustomValidity("");
        }

        if (errorElement) {
            errorElement.innerText = "";
        }
    }

    function validateName(input, errorElement, label) {
        const value = input.value.trim();

        if (value.length === 0) {
            showError(input, errorElement, label + " không được để trống.");
            return false;
        }

        if (value.length > 50 || !/^[\p{L}\s'-]+$/u.test(value)) {
            showError(input, errorElement, label + " không hợp lệ.");
            return false;
        }

        clearError(input, errorElement);
        return true;
    }

    function validatePhone() {
        const value = phoneInput.value.trim();
        const regex = /^(0)(3[2-9]|5[689]|7[06-9]|8[0-9]|9[0-9])[0-9]{7}$/;

        if (value.length === 0) {
            showError(phoneInput, phoneError, "Số điện thoại không được để trống.");
            return false;
        }

        if (!regex.test(value)) {
            showError(phoneInput, phoneError, "Số điện thoại không hợp lệ.");
            return false;
        }

        clearError(phoneInput, phoneError);
        return true;
    }

    function validateDob() {
        const value = dobInput.value;

        if (!value) {
            clearError(dobInput, dobError);
            return true;
        }

        const selectedDate = new Date(value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate > today) {
            showError(dobInput, dobError, "Ngày sinh không được lớn hơn ngày hiện tại.");
            return false;
        }

        clearError(dobInput, dobError);
        return true;
    }

    function validateGender() {
        clearError(genderInput, genderError);
        return true;
    }

    function validateEmail() {
        const value = emailInput.value.trim();
        const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (value.length === 0) {
            showError(emailInput, emailError, "Email không được để trống.");
            return false;
        }

        if (!regex.test(value)) {
            showError(emailInput, emailError, "Email không hợp lệ.");
            return false;
        }

        clearError(emailInput, emailError);
        return true;
    }

    function validatePassword() {
        const value = passwordInput.value.trim();
        const regex = /^[0-9]{6,32}$/;

        if (value.length === 0) {
            showError(passwordInput, passwordError, "Mật khẩu không được để trống.");
            return false;
        }

        if (!regex.test(value)) {
            showError(passwordInput, passwordError, "Mật khẩu không hợp lệ.");
            return false;
        }

        clearError(passwordInput, passwordError);
        return true;
    }

    function validateConfirmPassword() {
        const value = confirmPasswordInput.value.trim();
        const passwordValue = passwordInput.value.trim();

        if (value.length === 0) {
            showError(confirmPasswordInput, confirmPasswordError, "Vui lòng xác nhận mật khẩu.");
            return false;
        }

        if (value !== passwordValue) {
            showError(confirmPasswordInput, confirmPasswordError, "Mật khẩu xác nhận không khớp.");
            return false;
        }

        clearError(confirmPasswordInput, confirmPasswordError);
        return true;
    }

    firstNameInput.addEventListener("input", function () {
        clearError(firstNameInput, firstNameError);
    });

    lastNameInput.addEventListener("input", function () {
        clearError(lastNameInput, lastNameError);
    });

    phoneInput.addEventListener("input", function () {
        phoneInput.value = phoneInput.value.replace(/[^0-9]/g, "");
        clearError(phoneInput, phoneError);
    });

    dobInput.addEventListener("change", function () {
        clearError(dobInput, dobError);
    });

    genderInput.addEventListener("change", function () {
        clearError(genderInput, genderError);
    });

    emailInput.addEventListener("input", function () {
        clearError(emailInput, emailError);
    });

    passwordInput.addEventListener("input", function () {
        passwordInput.value = passwordInput.value.replace(/[^0-9]/g, "");
        clearError(passwordInput, passwordError);
    });

    confirmPasswordInput.addEventListener("input", function () {
        confirmPasswordInput.value = confirmPasswordInput.value.replace(/[^0-9]/g, "");
        clearError(confirmPasswordInput, confirmPasswordError);
    });

    registerForm.addEventListener("submit", function (event) {
        const isFirstNameValid = validateName(firstNameInput, firstNameError, "Họ");
        const isLastNameValid = validateName(lastNameInput, lastNameError, "Tên");
        const isPhoneValid = validatePhone();
        const isDobValid = validateDob();
        const isGenderValid = validateGender();
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();
        const isConfirmPasswordValid = validateConfirmPassword();

        if (!isFirstNameValid
            || !isLastNameValid
            || !isPhoneValid
            || !isDobValid
            || !isGenderValid
            || !isEmailValid
            || !isPasswordValid
            || !isConfirmPasswordValid) {

            event.preventDefault();
            registerForm.reportValidity();
        }
    });

    function togglePasswordVisibility(inputId) {
        const input = document.getElementById(inputId);

        if (!input) {
            return;
        }

        input.type = input.type === "password" ? "text" : "password";
    }
</script>

</body>
</html>