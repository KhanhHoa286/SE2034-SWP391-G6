<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    private String h(Object value) {
        if (value == null) return "";
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
%>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String ctx = request.getContextPath();
    String selectedGender = h(request.getAttribute("gender"));
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản | MODA</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Font chính của giao diện -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
          rel="stylesheet">

    <!-- CSS thuần, không dùng Tailwind -->
    <link rel="stylesheet" href="<%= ctx %>/assets/css/public/register.css">
</head>

<body>

<!-- Header -->
<header class="register-header">
    <a href="<%= ctx %>/home" class="register-logo">MODA</a>
</header>

<!-- Main Content -->
<main class="register-layout">

    <!-- Left Visual Section -->
    <section class="register-visual">
        <div class="visual-overlay"></div>

        <img class="visual-image"
             src="https://i.pinimg.com/736x/82/9e/c1/829ec14750233402aed8b58a799ce830.jpg"
             alt="MODA Fashion">

        <div class="visual-content">
            <p class="visual-label">MODA STYLE</p>
            <h2>Thời trang hiện đại cho phong cách riêng của bạn</h2>
            <p>
                Tạo tài khoản để khám phá sản phẩm, lưu yêu thích và theo dõi đơn hàng dễ dàng hơn.
            </p>
        </div>
    </section>

    <!-- Register Form Section -->
    <section class="register-content">
        <div class="register-card">

            <div class="register-heading">
                <p class="eyebrow">BẮT ĐẦU VỚI MODA</p>
                <h1>Đăng ký tài khoản</h1>
                <p>Nhập thông tin của bạn để tạo tài khoản mua sắm.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="server-error">
                <%= h(request.getAttribute("error")) %>
            </div>
            <% } %>

            <form action="<%= ctx %>/register"
                  method="post"
                  id="registerForm"
                  class="register-form"
                  novalidate>

                <!-- Backend vẫn nhận accountType CUSTOMER như luồng cũ -->
                <input type="hidden" name="accountType" value="CUSTOMER">

                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">Họ</label>

                        <input type="text"
                               id="firstName"
                               name="firstName"
                               required
                               maxlength="50"
                               autocomplete="family-name"
                               value="<%= h(request.getAttribute("firstName")) %>"
                               placeholder="Nguyễn">

                        <p id="firstNameError" class="field-error"></p>
                    </div>

                    <div class="form-group">
                        <label for="lastName">Tên</label>

                        <input type="text"
                               id="lastName"
                               name="lastName"
                               required
                               maxlength="50"
                               autocomplete="given-name"
                               value="<%= h(request.getAttribute("lastName")) %>"
                               placeholder="Văn A">

                        <p id="lastNameError" class="field-error"></p>
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone">Số điện thoại</label>

                    <p class="field-note">
                        Số điện thoại Việt Nam gồm 10 số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.
                    </p>

                    <input type="tel"
                           id="phone"
                           name="phone"
                           required
                           maxlength="10"
                           inputmode="numeric"
                           autocomplete="tel"
                           value="<%= h(request.getAttribute("phone")) %>"
                           placeholder="0923456789">

                    <p id="phoneError" class="field-error"></p>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="dob">Ngày sinh <span>không bắt buộc</span></label>

                        <input type="date"
                               id="dob"
                               name="dob"
                               value="<%= h(request.getAttribute("dob")) %>">

                        <p id="dobError" class="field-error"></p>
                    </div>

                    <div class="form-group">
                        <label for="gender">Giới tính <span>không bắt buộc</span></label>

                        <select name="gender" id="gender">
                            <option value="" <%= "".equals(selectedGender) ? "selected" : "" %>>
                                Chọn giới tính
                            </option>

                            <option value="nam" <%= "nam".equalsIgnoreCase(selectedGender) ? "selected" : "" %>>
                                Nam
                            </option>

                            <option value="nu" <%= "nu".equalsIgnoreCase(selectedGender) ? "selected" : "" %>>
                                Nữ
                            </option>
                        </select>

                        <p id="genderError" class="field-error"></p>
                    </div>
                </div>

                <div class="form-group">
                    <label for="email">Email / Tên đăng nhập</label>

                    <p class="field-note">
                        Email cần đúng định dạng, ví dụ: example@gmail.com.
                    </p>

                    <input type="email"
                           id="email"
                           name="email"
                           required
                           autocomplete="email"
                           value="<%= h(request.getAttribute("email")) %>"
                           placeholder="example@gmail.com">

                    <p id="emailError" class="field-error"></p>
                </div>

                <div class="form-row password-row">
                    <div class="form-group">
                        <label for="password">Mật khẩu</label>

                        <p class="field-note">
                            Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.
                        </p>

                        <div class="password-field">
                            <input type="password"
                                   id="password"
                                   name="password"
                                   required
                                   minlength="6"
                                   maxlength="32"
                                   inputmode="numeric"
                                   autocomplete="new-password"
                                   value="<%= h(request.getAttribute("password")) %>"
                                   title="Mật khẩu chỉ được gồm các chữ số 0-9, dài từ 6 đến 32 số.">

                            <button type="button"
                                    class="password-toggle"
                                    onclick="togglePasswordVisibility('password')"
                                    aria-label="Hiện hoặc ẩn mật khẩu">
                                <svg xmlns="http://www.w3.org/2000/svg"
                                     width="20"
                                     height="20"
                                     viewBox="0 0 24 24"
                                     fill="none"
                                     stroke="currentColor"
                                     stroke-width="1.8"
                                     stroke-linecap="round"
                                     stroke-linejoin="round">
                                    <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
                        </div>

                        <p id="passwordError" class="field-error"></p>
                    </div>

                    <div class="form-group">
                        <label for="confirm_password">Xác nhận mật khẩu</label>

                        <p class="field-note">
                            Nhập lại mật khẩu đã chọn.
                        </p>

                        <div class="password-field">
                            <input type="password"
                                   id="confirm_password"
                                   name="confirm_password"
                                   required
                                   minlength="6"
                                   maxlength="32"
                                   inputmode="numeric"
                                   autocomplete="new-password"
                                   value="<%= h(request.getAttribute("confirmPassword")) %>">

                            <button type="button"
                                    class="password-toggle"
                                    onclick="togglePasswordVisibility('confirm_password')"
                                    aria-label="Hiện hoặc ẩn mật khẩu">
                                <svg xmlns="http://www.w3.org/2000/svg"
                                     width="20"
                                     height="20"
                                     viewBox="0 0 24 24"
                                     fill="none"
                                     stroke="currentColor"
                                     stroke-width="1.8"
                                     stroke-linecap="round"
                                     stroke-linejoin="round">
                                    <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
                        </div>

                        <p id="confirmPasswordError" class="field-error"></p>
                    </div>
                </div>

                <button type="submit" class="register-button">
                    Đăng ký
                </button>
            </form>

            <p class="login-text">
                Đã có tài khoản?
                <a href="<%= ctx %>/public/auth/login.jsp">Đăng nhập</a>
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
    const genderInput = document.getElementById("gender");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirm_password");

    const firstNameError = document.getElementById("firstNameError");
    const lastNameError = document.getElementById("lastNameError");
    const phoneError = document.getElementById("phoneError");
    const dobError = document.getElementById("dobError");
    const genderError = document.getElementById("genderError");
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

        if (value.length > 50 || !/^[\p{L}\s'-]+$/u.test(value)) {
            showError(firstNameInput, firstNameError, "Họ không hợp lệ.");
            return false;
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

        if (value.length > 50 || !/^[\p{L}\s'-]+$/u.test(value)) {
            showError(lastNameInput, lastNameError, "Tên không hợp lệ.");
            return false;
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

    firstNameInput.addEventListener("input", function () {
        firstNameTouched = true;
        validateFirstName(false);
    });

    lastNameInput.addEventListener("input", function () {
        lastNameTouched = true;
        validateLastName(false);
    });

    phoneInput.addEventListener("input", function () {
        phoneTouched = true;
        phoneInput.value = phoneInput.value.replace(/[^0-9]/g, "");
        validatePhone(false);
    });

    dobInput.addEventListener("change", function () {
        validateDob();
    });

    genderInput.addEventListener("change", function () {
        validateGender();
    });

    emailInput.addEventListener("input", function () {
        emailTouched = true;
        validateEmail(false);
    });

    passwordInput.addEventListener("input", function () {
        passwordTouched = true;
        passwordInput.value = passwordInput.value.replace(/[^0-9]/g, "");
        validatePassword(false);

        if (confirmPasswordTouched) {
            validateConfirmPassword(false);
        }
    });

    confirmPasswordInput.addEventListener("input", function () {
        confirmPasswordTouched = true;
        confirmPasswordInput.value = confirmPasswordInput.value.replace(/[^0-9]/g, "");
        validateConfirmPassword(false);
    });

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
        const isDobValid = validateDob();
        const isGenderValid = validateGender();
        const isEmailValid = validateEmail(true);
        const isPasswordValid = validatePassword(true);
        const isConfirmPasswordValid = validateConfirmPassword(true);

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