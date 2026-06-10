<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    List<Map<String, Object>> provinces = (List<Map<String, Object>>) request.getAttribute("provinces");
    List<Map<String, Object>> wards = (List<Map<String, Object>>) request.getAttribute("wards");

    if (provinces == null || wards == null) {
        response.sendRedirect(request.getContextPath() + "/register");
        return;
    }

    String selectedAccountType = request.getAttribute("accountType") == null
            ? "CUSTOMER"
            : String.valueOf(request.getAttribute("accountType"));
%>
<%!
    private String h(Object value) {
        if (value == null) return "";
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String js(Object value) {
        if (value == null) return "";
        return String.valueOf(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản | MODA</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/public/register.css">


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

            <form action="<%= request.getContextPath() %>/register"
                  method="post"
                  class="space-y-4"
                  id="registerForm"
                  enctype="multipart/form-data">

                <div>
                    <label class="block mb-2 text-sm font-semibold">Chọn loại tài khoản</label>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                        <label class="account-option">
                            <input type="radio"
                                   name="accountType"
                                   value="CUSTOMER"
                                   class="mr-2"
                                <%= !"DELIVERY".equalsIgnoreCase(selectedAccountType) ? "checked" : "" %>>
                            <div class="inline-block align-top">
                                <div class="font-semibold">Đăng ký Khách Hàng</div>
                                <div class="text-xs text-gray-500 mt-1">Mua sắm trên MODA</div>
                            </div>
                        </label>

                        <label class="account-option">
                            <input type="radio"
                                   name="accountType"
                                   value="DELIVERY"
                                   class="mr-2"
                                <%= "DELIVERY".equalsIgnoreCase(selectedAccountType) ? "checked" : "" %>>
                            <div class="inline-block align-top">
                                <div class="font-semibold">Đối tác giao hàng</div>
                                <div class="text-xs text-gray-500 mt-1">Đăng ký hoạt động giao hàng</div>
                            </div>
                        </label>
                    </div>
                </div>

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
                        <label class="block mb-1 text-sm font-medium" id="dobLabel">Ngày sinh (không bắt buộc)</label>
                        <input type="date"
                               id="dob"
                               name="dob"
                               value="<%= h(request.getAttribute("dob")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none">
                        <p id="dobError" class="field-error"></p>
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium" id="genderLabel">Giới tính (không bắt buộc)</label>
                        <select name="gender"
                                id="gender"
                                class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none appearance-none">
                            <option value="" <%= "".equals(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Chọn giới tính
                            </option>
                            <option value="nam" <%= "nam".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Nam
                            </option>
                            <option value="nu" <%= "nu".equalsIgnoreCase(h(request.getAttribute("gender"))) ? "selected" : "" %>>
                                Nữ
                            </option>
                        </select>
                        <p id="genderError" class="field-error"></p>
                    </div>
                </div>

                <div id="shipperFields" class="shipper-box hidden space-y-4">
                    <div class="text-sm font-semibold text-gray-800">
                        Thông tin bắt buộc cho đối tác giao hàng
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Số CCCD</label>
                        <p class="text-xs text-gray-500 mb-1 leading-4">
                            CCCD Việt Nam gồm đúng 12 chữ số. Ví dụ: 001204005678.
                        </p>

                        <input type="text"
                               id="idCardNumber"
                               name="idCardNumber"
                               maxlength="12"
                               value="<%= h(request.getAttribute("idCardNumber")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="001204005678">
                        <p id="idCardNumberError" class="field-error"></p>
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Biển số xe</label>
                        <p class="text-xs text-gray-500 mb-1 leading-4">
                            Nhập biển số xe Việt Nam. Ví dụ: 29A12345, 29A-12345, 30K1-12345.
                        </p>

                        <input type="text"
                               id="licensePlate"
                               name="licensePlate"
                               maxlength="20"
                               value="<%= h(request.getAttribute("licensePlate")) %>"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="29A12345">
                        <p id="licensePlateError" class="field-error"></p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="relative input-focus-border">
                            <label class="block mb-1 text-sm font-medium">Tỉnh/Thành phố hoạt động</label>
                            <select id="shipperProvinceId"
                                    name="shipperProvinceId"
                                    class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none appearance-none">
                                <option value="">Chọn tỉnh/thành phố</option>

                                <% if (provinces != null) {
                                    for (Map<String, Object> province : provinces) {
                                        String provinceId = String.valueOf(province.get("id"));
                                        String provinceName = String.valueOf(province.get("name"));
                                        String selectedProvince = h(request.getAttribute("shipperProvinceId"));
                                %>
                                <option value="<%= h(provinceId) %>"
                                        <%= provinceId.equals(selectedProvince) ? "selected" : "" %>>
                                    <%= h(provinceName) %>
                                </option>
                                <%  }
                                } %>
                            </select>
                            <p id="shipperProvinceError" class="field-error"></p>
                        </div>

                        <div class="relative input-focus-border">
                            <label class="block mb-1 text-sm font-medium">Xã/Phường hoạt động</label>
                            <select id="shipperWardId"
                                    name="shipperWardId"
                                    class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none appearance-none">
                                <option value="">Chọn tỉnh trước</option>
                            </select>
                            <p id="shipperWardError" class="field-error"></p>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="border border-gray-300 rounded-xl p-4 bg-white min-h-[150px] flex flex-col justify-between">
                            <div>
                                <label class="block mb-1 text-sm font-semibold">
                                    Ảnh bằng lái xe mặt trước
                                </label>

                                <p class="text-xs text-gray-500 mb-3 leading-4">
                                    Chỉ chấp nhận file ảnh, tối đa 10MB.
                                </p>
                            </div>

                            <div>
                                <input type="file"
                                       id="driverLicenseFront"
                                       name="driverLicenseFront"
                                       accept="image/*"
                                       class="hidden">

                                <label for="driverLicenseFront"
                                       class="inline-flex items-center justify-center px-4 py-2 border border-gray-800 rounded-lg text-sm font-medium cursor-pointer hover:bg-gray-100 transition">
                                    Chọn ảnh
                                </label>

                                <span id="driverLicenseFrontFileName"
                                      class="block mt-2 text-xs text-gray-500 truncate">
                Chưa chọn ảnh
            </span>

                                <p id="driverLicenseFrontError" class="field-error"></p>
                            </div>
                        </div>

                        <div class="border border-gray-300 rounded-xl p-4 bg-white min-h-[150px] flex flex-col justify-between">
                            <div>
                                <label class="block mb-1 text-sm font-semibold">
                                    Ảnh bằng lái xe mặt sau
                                </label>

                                <p class="text-xs text-gray-500 mb-3 leading-4">
                                    Ảnh phải rõ thông tin, không bị mờ hoặc cắt góc.
                                </p>
                            </div>

                            <div>
                                <input type="file"
                                       id="driverLicenseBack"
                                       name="driverLicenseBack"
                                       accept="image/*"
                                       class="hidden">

                                <label for="driverLicenseBack"
                                       class="inline-flex items-center justify-center px-4 py-2 border border-gray-800 rounded-lg text-sm font-medium cursor-pointer hover:bg-gray-100 transition">
                                    Chọn ảnh
                                </label>

                                <span id="driverLicenseBackFileName"
                                      class="block mt-2 text-xs text-gray-500 truncate">
                Chưa chọn ảnh
            </span>

                                <p id="driverLicenseBackError" class="field-error"></p>
                            </div>
                        </div>
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

                        <div class="relative">
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
                                    class="password-toggle-btn"
                                    onclick="togglePasswordVisibility('password')"
                                    aria-label="Hiện hoặc ẩn mật khẩu">
                                <svg xmlns="http://www.w3.org/2000/svg"
                                     width="22"
                                     height="22"
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

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Xác nhận mật khẩu</label>

                        <p class="text-xs text-gray-500 mb-1 leading-4 invisible">
                            Mật khẩu chỉ gồm chữ số 0-9, dài từ 6 đến 32 số.
                        </p>

                        <div class="relative">
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
                                    class="password-toggle-btn"
                                    onclick="togglePasswordVisibility('confirm_password')"
                                    aria-label="Hiện hoặc ẩn mật khẩu">
                                <svg xmlns="http://www.w3.org/2000/svg"
                                     width="22"
                                     height="22"
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
                <button type="submit"
                        class="w-full bg-black text-white py-3 font-semibold uppercase hover:bg-gray-800 transition">
                    Đăng ký
                </button>
            </form>

            <p class="mt-4 text-center text-sm text-gray-600">
                Đã có tài khoản?
                <a href="<%= request.getContextPath() %>/public/auth/login.jsp"
                   class="text-black font-bold hover:underline">Đăng nhập</a>
            </p>
        </div>
    </section>
</main>

<script>
    const allWards = [
        <% if (wards != null) {
            for (int i = 0; i < wards.size(); i++) {
                Map<String, Object> ward = wards.get(i);
                String comma = i < wards.size() - 1 ? "," : "";
        %>
        {
            id: "<%= js(ward.get("id")) %>",
            provinceId: "<%= js(ward.get("provinceId")) %>",
            name: "<%= js(ward.get("name")) %>"
        }<%= comma %>
        <%  }
        } %>
    ];

    const initialWardId = "<%= js(request.getAttribute("shipperWardId")) %>";
    const maxImageSize = 10 * 1024 * 1024;


    const dobInput = document.getElementById("dob");
    if (dobInput) {
        dobInput.max = new Date().toISOString().split("T")[0];
    }

    const registerForm = document.getElementById("registerForm");

    const accountTypeInputs = document.querySelectorAll("input[name='accountType']");
    const shipperFields = document.getElementById("shipperFields");

    const firstNameInput = document.getElementById("firstName");
    const lastNameInput = document.getElementById("lastName");
    const phoneInput = document.getElementById("phone");
    const dobLabel = document.getElementById("dobLabel");
    const genderLabel = document.getElementById("genderLabel");
    const genderInput = document.getElementById("gender");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirm_password");

    const idCardNumberInput = document.getElementById("idCardNumber");
    const licensePlateInput = document.getElementById("licensePlate");
    const shipperProvinceInput = document.getElementById("shipperProvinceId");
    const shipperWardInput = document.getElementById("shipperWardId");
    const driverLicenseFrontInput = document.getElementById("driverLicenseFront");
    const driverLicenseBackInput = document.getElementById("driverLicenseBack");
    const driverLicenseFrontFileName = document.getElementById("driverLicenseFrontFileName");
    const driverLicenseBackFileName = document.getElementById("driverLicenseBackFileName");

    const firstNameError = document.getElementById("firstNameError");
    const lastNameError = document.getElementById("lastNameError");
    const phoneError = document.getElementById("phoneError");
    const dobError = document.getElementById("dobError");
    const genderError = document.getElementById("genderError");
    const emailError = document.getElementById("emailError");
    const passwordError = document.getElementById("passwordError");
    const confirmPasswordError = document.getElementById("confirmPasswordError");
    const idCardNumberError = document.getElementById("idCardNumberError");
    const licensePlateError = document.getElementById("licensePlateError");
    const shipperProvinceError = document.getElementById("shipperProvinceError");
    const shipperWardError = document.getElementById("shipperWardError");
    const driverLicenseFrontError = document.getElementById("driverLicenseFrontError");
    const driverLicenseBackError = document.getElementById("driverLicenseBackError");

    let firstNameTouched = false;
    let lastNameTouched = false;
    let phoneTouched = false;
    let emailTouched = false;
    let passwordTouched = false;
    let confirmPasswordTouched = false;
    let idCardTouched = false;
    let licensePlateTouched = false;
    let provinceTouched = false;
    let wardTouched = false;
    let frontImageTouched = false;
    let backImageTouched = false;

    function isShipperSelected() {
        const checked = document.querySelector("input[name='accountType']:checked");
        return checked && checked.value === "DELIVERY";
    }

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

    function toggleAccountType() {
        const shipper = isShipperSelected();

        if (shipper) {
            shipperFields.classList.remove("hidden");

            dobInput.required = true;
            genderInput.required = true;
            idCardNumberInput.required = true;
            licensePlateInput.required = true;
            shipperProvinceInput.required = true;
            shipperWardInput.required = true;
            driverLicenseFrontInput.required = true;
            driverLicenseBackInput.required = true;

            dobLabel.innerText = "Ngày sinh";
            genderLabel.innerText = "Giới tính";
        } else {
            shipperFields.classList.add("hidden");

            dobInput.required = false;
            genderInput.required = false;
            idCardNumberInput.required = false;
            licensePlateInput.required = false;
            shipperProvinceInput.required = false;
            shipperWardInput.required = false;
            driverLicenseFrontInput.required = false;
            driverLicenseBackInput.required = false;

            dobLabel.innerText = "Ngày sinh (không bắt buộc)";
            genderLabel.innerText = "Giới tính (không bắt buộc)";

            clearError(idCardNumberInput, idCardNumberError);
            clearError(licensePlateInput, licensePlateError);
            clearError(shipperProvinceInput, shipperProvinceError);
            clearError(shipperWardInput, shipperWardError);
            clearError(driverLicenseFrontInput, driverLicenseFrontError);
            clearError(driverLicenseBackInput, driverLicenseBackError);
            clearError(dobInput, dobError);
            clearError(genderInput, genderError);
        }
    }

    function loadWardsForProvince() {
        const provinceId = shipperProvinceInput.value;
        const currentSelectedWardId = shipperWardInput.value || initialWardId;

        shipperWardInput.innerHTML = "";

        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.textContent = provinceId ? "Chọn xã/phường" : "Chọn tỉnh trước";
        shipperWardInput.appendChild(defaultOption);

        if (!provinceId) {
            return;
        }

        allWards
            .filter(function (ward) {
                return ward.provinceId === provinceId;
            })
            .forEach(function (ward) {
                const option = document.createElement("option");
                option.value = ward.id;
                option.textContent = ward.name;

                if (ward.id === currentSelectedWardId) {
                    option.selected = true;
                }

                shipperWardInput.appendChild(option);
            });
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

    function validateDob(forceCheck) {
        if (!isShipperSelected()) {
            clearError(dobInput, dobError);
            return true;
        }

        const value = dobInput.value;

        if (!value) {
            if (forceCheck) {
                showError(dobInput, dobError, "Đối tác giao hàng bắt buộc nhập ngày sinh.");
                return false;
            }

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

    function validateGender(forceCheck) {
        if (!isShipperSelected()) {
            clearError(genderInput, genderError);
            return true;
        }

        if (!genderInput.value) {
            if (forceCheck) {
                showError(genderInput, genderError, "Đối tác giao hàng bắt buộc chọn giới tính.");
                return false;
            }

            clearError(genderInput, genderError);
            return true;
        }

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

    function validateIdCardNumber(forceCheck) {
        if (!isShipperSelected()) {
            clearError(idCardNumberInput, idCardNumberError);
            return true;
        }

        const value = idCardNumberInput.value.trim();

        if (value.length === 0) {
            if (forceCheck || idCardTouched) {
                showError(idCardNumberInput, idCardNumberError, "Số CCCD không hợp lệ.");
                return false;
            }

            clearError(idCardNumberInput, idCardNumberError);
            return true;
        }

        if (!/^[0-9]{12}$/.test(value)) {
            showError(idCardNumberInput, idCardNumberError, "Số CCCD không hợp lệ.");
            return false;
        }

        if (/^(\d)\1{11}$/.test(value)) {
            showError(idCardNumberInput, idCardNumberError, "Số CCCD không hợp lệ.");
            return false;
        }

        const provinceCode = parseInt(value.substring(0, 3), 10);

        if (provinceCode < 1 || provinceCode > 96) {
            showError(idCardNumberInput, idCardNumberError, "Số CCCD không hợp lệ.");
            return false;
        }

        clearError(idCardNumberInput, idCardNumberError);
        return true;
    }

    function normalizeLicensePlate(value) {
        return value.trim().toUpperCase().replace(/[\s\-.]/g, "");
    }

    function validateLicensePlate(forceCheck) {
        if (!isShipperSelected()) {
            clearError(licensePlateInput, licensePlateError);
            return true;
        }

        const rawValue = licensePlateInput.value.trim();
        const value = normalizeLicensePlate(rawValue);
        const regex = /^[1-9][0-9][A-Z][0-9A-Z]?[0-9]{4,5}$/;

        if (rawValue.length === 0) {
            if (forceCheck || licensePlateTouched) {
                showError(licensePlateInput, licensePlateError, "Biển số xe không được để trống.");
                return false;
            }

            clearError(licensePlateInput, licensePlateError);
            return true;
        }

        if (!regex.test(value)) {
            showError(licensePlateInput, licensePlateError, "Biển số xe không hợp lệ. Ví dụ: 29A12345 hoặc 30K1-12345.");
            return false;
        }

        licensePlateInput.value = value;
        clearError(licensePlateInput, licensePlateError);
        return true;
    }

    function validateProvince(forceCheck) {
        if (!isShipperSelected()) {
            clearError(shipperProvinceInput, shipperProvinceError);
            return true;
        }

        if (!shipperProvinceInput.value) {
            if (forceCheck || provinceTouched) {
                showError(shipperProvinceInput, shipperProvinceError, "Vui lòng chọn tỉnh/thành phố hoạt động.");
                return false;
            }

            clearError(shipperProvinceInput, shipperProvinceError);
            return true;
        }

        clearError(shipperProvinceInput, shipperProvinceError);
        return true;
    }

    function validateWard(forceCheck) {
        if (!isShipperSelected()) {
            clearError(shipperWardInput, shipperWardError);
            return true;
        }

        if (!shipperWardInput.value) {
            if (forceCheck || wardTouched) {
                showError(shipperWardInput, shipperWardError, "Vui lòng chọn xã/phường hoạt động.");
                return false;
            }

            clearError(shipperWardInput, shipperWardError);
            return true;
        }

        clearError(shipperWardInput, shipperWardError);
        return true;
    }

    function validateImageFile(input, errorElement, forceCheck, touched, emptyMessage) {
        if (!isShipperSelected()) {
            clearError(input, errorElement);
            return true;
        }

        const file = input.files && input.files.length > 0 ? input.files[0] : null;

        if (!file) {
            if (forceCheck || touched) {
                showError(input, errorElement, emptyMessage);
                return false;
            }

            clearError(input, errorElement);
            return true;
        }

        if (!file.type || !file.type.startsWith("image/")) {
            showError(input, errorElement, "File tải lên phải là ảnh.");
            return false;
        }

        if (file.size > maxImageSize) {
            showError(input, errorElement, "Ảnh không được vượt quá 10MB.");
            return false;
        }

        clearError(input, errorElement);
        return true;
    }

    function validateDriverLicenseFront(forceCheck) {
        return validateImageFile(
            driverLicenseFrontInput,
            driverLicenseFrontError,
            forceCheck,
            frontImageTouched,
            "Vui lòng tải ảnh bằng lái xe mặt trước."
        );
    }

    function validateDriverLicenseBack(forceCheck) {
        return validateImageFile(
            driverLicenseBackInput,
            driverLicenseBackError,
            forceCheck,
            backImageTouched,
            "Vui lòng tải ảnh bằng lái xe mặt sau."
        );
    }

    accountTypeInputs.forEach(function (input) {
        input.addEventListener("change", function () {
            toggleAccountType();
            validateDob(false);
            validateGender(false);
            validateIdCardNumber(false);
            validateLicensePlate(false);
            validateProvince(false);
            validateWard(false);
            validateDriverLicenseFront(false);
            validateDriverLicenseBack(false);
        });
    });

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
        validateDob(false);
    });

    genderInput.addEventListener("change", function () {
        validateGender(false);
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

    idCardNumberInput.addEventListener("input", function () {
        idCardTouched = true;
        validateIdCardNumber(false);
    });

    licensePlateInput.addEventListener("input", function () {
        licensePlateTouched = true;
        licensePlateInput.value = licensePlateInput.value.toUpperCase();
        validateLicensePlate(false);
    });

    shipperProvinceInput.addEventListener("change", function () {
        provinceTouched = true;
        shipperWardInput.value = "";
        loadWardsForProvince();
        validateProvince(false);
        validateWard(false);
    });

    shipperWardInput.addEventListener("change", function () {
        wardTouched = true;
        validateWard(false);
    });

    driverLicenseFrontInput.addEventListener("change", function () {
        frontImageTouched = true;

        if (driverLicenseFrontInput.files && driverLicenseFrontInput.files.length > 0) {
            driverLicenseFrontFileName.innerText = driverLicenseFrontInput.files[0].name;
        } else {
            driverLicenseFrontFileName.innerText = "Chưa chọn ảnh";
        }

        validateDriverLicenseFront(false);
    });

    driverLicenseBackInput.addEventListener("change", function () {
        backImageTouched = true;

        if (driverLicenseBackInput.files && driverLicenseBackInput.files.length > 0) {
            driverLicenseBackFileName.innerText = driverLicenseBackInput.files[0].name;
        } else {
            driverLicenseBackFileName.innerText = "Chưa chọn ảnh";
        }

        validateDriverLicenseBack(false);
    });

    registerForm.addEventListener("submit", function (event) {
        firstNameTouched = true;
        lastNameTouched = true;
        phoneTouched = true;
        emailTouched = true;
        passwordTouched = true;
        confirmPasswordTouched = true;
        idCardTouched = true;
        licensePlateTouched = true;
        provinceTouched = true;
        wardTouched = true;
        frontImageTouched = true;
        backImageTouched = true;

        const isFirstNameValid = validateFirstName(true);
        const isLastNameValid = validateLastName(true);
        const isPhoneValid = validatePhone(true);
        const isDobValid = validateDob(true);
        const isGenderValid = validateGender(true);
        const isEmailValid = validateEmail(true);
        const isPasswordValid = validatePassword(true);
        const isConfirmPasswordValid = validateConfirmPassword(true);
        const isIdCardValid = validateIdCardNumber(true);
        const isLicensePlateValid = validateLicensePlate(true);
        const isProvinceValid = validateProvince(true);
        const isWardValid = validateWard(true);
        const isFrontImageValid = validateDriverLicenseFront(true);
        const isBackImageValid = validateDriverLicenseBack(true);

        if (!isFirstNameValid
            || !isLastNameValid
            || !isPhoneValid
            || !isDobValid
            || !isGenderValid
            || !isEmailValid
            || !isPasswordValid
            || !isConfirmPasswordValid
            || !isIdCardValid
            || !isLicensePlateValid
            || !isProvinceValid
            || !isWardValid
            || !isFrontImageValid
            || !isBackImageValid) {

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