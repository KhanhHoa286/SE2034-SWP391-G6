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
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản | MODA</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
    <style>
        .input-focus-border::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 0;
            height: 1px;
            background-color: black;
            transition: width 0.3s ease;
        }

        .input-focus-border:focus-within::after {
            width: 100%;
        }

        .field-error {
            min-height: 16px;
            margin-top: 4px;
            font-size: 12px;
            color: #dc2626;
        }
    </style>
</head>
<body class="bg-surface-container-lowest text-on-surface font-body-md">

<header class="w-full bg-white border-b border-gray-300 px-8 py-4 flex justify-between items-center">
    <div class="text-xl font-bold">MODA</div>
</header>

<main class="min-h-screen flex flex-col md:flex-row">
    <section class="hidden md:block md:w-1/2 h-screen">
        <img class="w-full h-full object-cover"
             src="https://lh3.googleusercontent.com/aida-public/AB6AXuBJ1BpWcDXwucIaQdw30KgAXXA-GDYF7rJtTZBbm6P4zjaRrnpiSuFGemd6sqz30WOoAnFqQ1YsIPudx2w9eAIkcp0KcSAtwhzhbet4swl4-vBzW2j8tNy6QW0EDjyw6rwl4QLEvKw_TSm9v18uRTlBUwD7iyJEXUVQfg8ZLjjDTq17x_xfrQo8EBRXyULtj3sXsn87wcBUxprAwrvTJMO_9pLRATvMVePAMjK2u_sjWf2pmsZdnQr81cnfn_VbS-6KFXQ2MhFQS34"
             alt="Fashion">
    </section>

    <section class="w-full md:w-1/2 flex items-center justify-center p-6 md:p-16">
        <div class="w-full max-w-md">
            <h2 class="text-3xl font-bold mb-4">Đăng ký tài khoản</h2>
            <p class="mb-6 text-gray-600">Tham gia để trải nghiệm dịch vụ MODA</p>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mb-4 text-red-600 text-sm font-medium">
                <%= h(request.getAttribute("error")) %>
            </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="post" class="space-y-4" id="registerForm">

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Họ</label>
                        <input type="text"
                               id="firstName"
                               name="firstName"
                               required
                               maxlength="50"
                               autocomplete="family-name"
                               value="<%= h(request.getAttribute("firstName")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="Nguyễn">
                        <p id="firstNameError" class="field-error"></p>
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Tên</label>
                        <input type="text"
                               id="lastName"
                               name="lastName"
                               required
                               maxlength="50"
                               autocomplete="given-name"
                               value="<%= h(request.getAttribute("lastName")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="Văn A">
                        <p id="lastNameError" class="field-error"></p>
                    </div>
                </div>

                <div class="relative input-focus-border">
                    <label class="block mb-1 text-sm font-medium">Số điện thoại</label>

                    <p class="text-xs text-gray-500 mb-1 leading-4">
                        Số điện thoại Việt Nam gồm 10 số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.
                    </p>

                    <input type="tel"
                           id="phone"
                           name="phone"
                           required
                           maxlength="10"
                           inputmode="numeric"
                           value="<%= h(request.getAttribute("phone")) %>"
                           class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                           placeholder="0923456789">
                    <p id="phoneError" class="field-error"></p>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Ngày sinh (không bắt buộc)</label>
                        <input type="date"
                               id="dob"
                               name="dob"
                               value="<%= h(request.getAttribute("dob")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none">
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Giới tính (không bắt buộc)</label>
                        <select name="gender"
                                class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none appearance-none">
                            <option value="" <%= "".equals(h(request.getAttribute("gender"))) ? "selected" : "" %>>Chọn giới tính</option>
                            <option value="nam" <%= "nam".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>Nam</option>
                            <option value="nu" <%= "nu".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>Nữ</option>
                            <option value="khac" <%= "khac".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>Khác</option>
                        </select>
                    </div>
                </div>

                <div class="relative input-focus-border">
                    <label class="block mb-1 text-sm font-medium">Email / Tên đăng nhập</label>

                    <p class="text-xs text-gray-500 mb-1 leading-4">
                        Email cần đúng định dạng, ví dụ: example@gmail.com.
                    </p>

                    <input type="email"
                           id="email"
                           name="email"
                           required
                           value="<%= h(request.getAttribute("email")) %>"
                           class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                           placeholder="example@gmail.com">
                    <p id="emailError" class="field-error"></p>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Mật khẩu</label>

                        <p class="text-xs text-gray-500 mb-1 leading-4">
                            Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.
                        </p>

                        <input type="password"
                               id="password"
                               name="password"
                               required
                               minlength="6"
                               maxlength="32"
                               inputmode="numeric"
                               value="<%= h(request.getAttribute("password")) %>"
                               title="Mật khẩu chỉ được gồm các chữ số 0-9, dài từ 6 đến 32 số."
                               class="w-full border-b border-gray-300 py-2 pr-10 bg-transparent focus:outline-none">

                        <button type="button"
                                class="absolute right-2 top-[58px] text-gray-500 text-sm"
                                onclick="togglePassword('password', this)">Xem</button>

                        <p id="passwordError" class="field-error"></p>
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Xác nhận mật khẩu</label>

                        <p class="text-xs text-gray-500 mb-1 leading-4 invisible">
                            Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.
                        </p>

                        <input type="password"
                               id="confirm_password"
                               name="confirm_password"
                               required
                               minlength="6"
                               maxlength="32"
                               inputmode="numeric"
                               value="<%= h(request.getAttribute("confirmPassword")) %>"
                               class="w-full border-b border-gray-300 py-2 pr-10 bg-transparent focus:outline-none">

                        <button type="button"
                                class="absolute right-2 top-[58px] text-gray-500 text-sm"
                                onclick="togglePassword('confirm_password', this)">Xem</button>

                        <p id="confirmPasswordError" class="field-error"></p>
                    </div>
                </div>

                <button type="submit"
                        class="w-full bg-black text-white py-3 font-semibold uppercase hover:bg-gray-800 transition">
                    Đăng ký
                </button>
            </form>

            <p class="mt-4 text-center text-sm text-gray-600">
                Đã có tài khoản?
                <a href="<%= request.getContextPath() %>/public/auth/login.jsp" class="text-black font-bold hover:underline">Đăng nhập</a>
            </p>
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
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirm_password");

    const firstNameError = document.getElementById("firstNameError");
    const lastNameError = document.getElementById("lastNameError");
    const phoneError = document.getElementById("phoneError");
    const emailError = document.getElementById("emailError");
    const passwordError = document.getElementById("passwordError");
    const confirmPasswordError = document.getElementById("confirmPasswordError");

    let firstNameTouched = false;
    let lastNameTouched = false;
    let phoneTouched = false;
    let emailTouched = false;
    let passwordTouched = false;
    let confirmPasswordTouched = false;

    function showError(input, errorElement, message) {
        input.setCustomValidity(message);
        errorElement.innerText = message;
    }

    function clearError(input, errorElement) {
        input.setCustomValidity("");
        errorElement.innerText = "";
    }

    function validateFirstName(forceCheck) {
        const value = firstNameInput.value.trim();

        if (value.length === 0) {
            if (forceCheck || firstNameTouched) {
                showError(firstNameInput, firstNameError, "Họ không được để trống.");
                return false;
            }

            clearError(firstNameInput, firstNameError);
            return true;
        }

        clearError(firstNameInput, firstNameError);
        return true;
    }

    function validateLastName(forceCheck) {
        const value = lastNameInput.value.trim();

        if (value.length === 0) {
            if (forceCheck || lastNameTouched) {
                showError(lastNameInput, lastNameError, "Tên không được để trống.");
                return false;
            }

            clearError(lastNameInput, lastNameError);
            return true;
        }

        clearError(lastNameInput, lastNameError);
        return true;
    }

    function validatePhone(forceCheck) {
        const value = phoneInput.value.trim();
        const regex = /^(0)(3[2-9]|5[689]|7[06-9]|8[0-9]|9[0-9])[0-9]{7}$/;

        if (value.length === 0) {
            if (forceCheck || phoneTouched) {
                showError(phoneInput, phoneError, "Số điện thoại không được để trống.");
                return false;
            }

            clearError(phoneInput, phoneError);
            return true;
        }

        if (!regex.test(value)) {
            showError(phoneInput, phoneError, "Số điện thoại không hợp lệ.");
            return false;
        }

        clearError(phoneInput, phoneError);
        return true;
    }

    function validateEmail(forceCheck) {
        const value = emailInput.value.trim();
        const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (value.length === 0) {
            if (forceCheck || emailTouched) {
                showError(emailInput, emailError, "Email không được để trống.");
                return false;
            }

            clearError(emailInput, emailError);
            return true;
        }

        if (!regex.test(value)) {
            showError(emailInput, emailError, "Email không hợp lệ.");
            return false;
        }

        clearError(emailInput, emailError);
        return true;
    }

    function validatePassword(forceCheck) {
        const value = passwordInput.value.trim();
        const regex = /^[0-9]{6,32}$/;

        if (value.length === 0) {
            if (forceCheck || passwordTouched) {
                showError(passwordInput, passwordError, "Mật khẩu không được để trống.");
                return false;
            }

            clearError(passwordInput, passwordError);
            return true;
        }

        if (!regex.test(value)) {
            showError(passwordInput, passwordError, "Mật khẩu không hợp lệ.");
            return false;
        }

        clearError(passwordInput, passwordError);
        return true;
    }

    function validateConfirmPassword(forceCheck) {
        const value = confirmPasswordInput.value.trim();
        const passwordValue = passwordInput.value.trim();

        if (value.length === 0) {
            if (forceCheck || confirmPasswordTouched) {
                showError(confirmPasswordInput, confirmPasswordError, "Vui lòng xác nhận mật khẩu.");
                return false;
            }

            clearError(confirmPasswordInput, confirmPasswordError);
            return true;
        }

        if (value !== passwordValue) {
            showError(confirmPasswordInput, confirmPasswordError, "Mật khẩu xác nhận không khớp.");
            return false;
        }

        clearError(confirmPasswordInput, confirmPasswordError);
        return true;
    }

    if (firstNameInput) {
        firstNameInput.addEventListener("input", function () {
            firstNameTouched = true;
            validateFirstName(false);
        });

        firstNameInput.addEventListener("blur", function () {
            if (firstNameTouched) {
                validateFirstName(false);
            }
        });
    }

    if (lastNameInput) {
        lastNameInput.addEventListener("input", function () {
            lastNameTouched = true;
            validateLastName(false);
        });

        lastNameInput.addEventListener("blur", function () {
            if (lastNameTouched) {
                validateLastName(false);
            }
        });
    }

    if (phoneInput) {
        phoneInput.addEventListener("input", function () {
            phoneTouched = true;
            phoneInput.value = phoneInput.value.replace(/[^0-9]/g, "");
            validatePhone(false);
        });

        phoneInput.addEventListener("blur", function () {
            if (phoneTouched) {
                validatePhone(false);
            }
        });
    }

    if (emailInput) {
        emailInput.addEventListener("input", function () {
            emailTouched = true;
            validateEmail(false);
        });

        emailInput.addEventListener("blur", function () {
            if (emailTouched) {
                validateEmail(false);
            }
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener("input", function () {
            passwordTouched = true;
            passwordInput.value = passwordInput.value.replace(/[^0-9]/g, "");
            validatePassword(false);

            if (confirmPasswordTouched) {
                validateConfirmPassword(false);
            }
        });

        passwordInput.addEventListener("blur", function () {
            if (passwordTouched) {
                validatePassword(false);
            }
        });
    }

    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener("input", function () {
            confirmPasswordTouched = true;
            confirmPasswordInput.value = confirmPasswordInput.value.replace(/[^0-9]/g, "");
            validateConfirmPassword(false);
        });

        confirmPasswordInput.addEventListener("blur", function () {
            if (confirmPasswordTouched) {
                validateConfirmPassword(false);
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", function (event) {
            firstNameTouched = true;
            lastNameTouched = true;
            phoneTouched = true;
            emailTouched = true;
            passwordTouched = true;
            confirmPasswordTouched = true;

            const isFirstNameValid = validateFirstName(true);
            const isLastNameValid = validateLastName(true);
            const isPhoneValid = validatePhone(true);
            const isEmailValid = validateEmail(true);
            const isPasswordValid = validatePassword(true);
            const isConfirmPasswordValid = validateConfirmPassword(true);

            if (!isFirstNameValid
                || !isLastNameValid
                || !isPhoneValid
                || !isEmailValid
                || !isPasswordValid
                || !isConfirmPasswordValid) {
                event.preventDefault();
                registerForm.reportValidity();
            }
        });
    }

    function togglePassword(inputId, btn) {
        const input = document.getElementById(inputId);

        if (input.type === "password") {
            input.type = "text";
            btn.innerText = "Ẩn";
        } else {
            input.type = "password";
            btn.innerText = "Xem";
        }
    }
</script>

</body>
</html>