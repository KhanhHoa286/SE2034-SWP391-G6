<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="post" class="space-y-4" id="registerForm">

                <!-- Họ và Tên -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Họ</label>
                        <input type="text"
                               id="firstName"
                               name="firstName"
                               required
                               maxlength="50"
                               autocomplete="family-name"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="Nguyễn">
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Tên</label>
                        <input type="text"
                               id="lastName"
                               name="lastName"
                               required
                               maxlength="50"
                               autocomplete="given-name"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                               placeholder="Văn A">
                    </div>
                </div>

                <!-- Số điện thoại -->
                <div class="relative input-focus-border">
                    <label class="block mb-1 text-sm font-medium">Số điện thoại</label>
                    <input type="tel"
                           name="phone"
                           required
                           pattern="[0-9]{10,12}"
                           class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none"
                           placeholder="0123456789">
                </div>

                <!-- Ngày sinh & Giới tính -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Ngày sinh (không bắt buộc)</label>
                        <input type="date"
                               id="dob"
                               name="dob"
                               class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none">
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Giới tính (không bắt buộc)</label>
                        <select name="gender"
                                class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none appearance-none">
                            <option value="" selected>Chọn giới tính</option>
                            <option value="nam">Nam</option>
                            <option value="nu">Nữ</option>
                            <option value="">Khác</option>
                        </select>
                    </div>
                </div>

                <!-- Email -->
                <div class="relative input-focus-border">
                    <label class="block mb-1 text-sm font-medium">Email / Tên đăng nhập</label>
                    <input type="email"
                           name="email"
                           required
                           class="w-full border-b border-gray-300 py-2 bg-transparent focus:outline-none">
                </div>

                <!-- Mật khẩu -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Mật khẩu</label>
                        <input type="password"
                               id="password"
                               name="password"
                               required
                               class="w-full border-b border-gray-300 py-2 pr-10 bg-transparent focus:outline-none">
                        <button type="button"
                                class="absolute right-2 top-9 text-gray-500 text-sm"
                                onclick="togglePassword('password', this)">Xem</button>
                    </div>

                    <div class="relative input-focus-border">
                        <label class="block mb-1 text-sm font-medium">Xác nhận mật khẩu</label>
                        <input type="password"
                               id="confirm_password"
                               name="confirm_password"
                               required
                               class="w-full border-b border-gray-300 py-2 pr-10 bg-transparent focus:outline-none">
                        <button type="button"
                                class="absolute right-2 top-9 text-gray-500 text-sm"
                                onclick="togglePassword('confirm_password', this)">Xem</button>
                    </div>
                </div>

                <!-- Nút đăng ký -->
                <button type="submit"
                        class="w-full bg-black text-white py-3 font-semibold uppercase hover:bg-gray-800 transition">
                    Đăng ký
                </button>
            </form>

            <p class="mt-4 text-center text-sm text-gray-600">
                Đã có tài khoản?
                <a href="login.jsp" class="text-black font-bold hover:underline">Đăng nhập</a>
            </p>
        </div>
    </section>
</main>

<script>
    // Datepicker max today
    const dobInput = document.getElementById("dob");
    if(dobInput) dobInput.max = new Date().toISOString().split("T")[0];

    const registerForm = document.getElementById("registerForm");
    const firstNameInput = document.getElementById("firstName");
    const lastNameInput = document.getElementById("lastName");

    function validateNameInput(input, fieldLabel){
        const value = input.value.trim();
        if(value.length===0){ input.setCustomValidity(fieldLabel+" không được để trống."); return false; }
        if(value.length>50){ input.setCustomValidity(fieldLabel+" không được vượt quá 50 ký tự."); return false; }
        const regex=/^[A-Za-zÀ-ỹ\s'-]+$/u;
        if(!regex.test(value)){ input.setCustomValidity(fieldLabel+" chỉ được chứa chữ cái, khoảng trắng, dấu nháy đơn hoặc dấu gạch ngang."); return false; }
        input.setCustomValidity(""); return true;
    }

    if(firstNameInput) firstNameInput.addEventListener("input", ()=>validateNameInput(firstNameInput,"Họ"));
    if(lastNameInput) lastNameInput.addEventListener("input", ()=>validateNameInput(lastNameInput,"Tên"));

    if(registerForm){
        registerForm.addEventListener("submit", function(event){
            const isFirst=validateNameInput(firstNameInput,"Họ");
            const isLast=validateNameInput(lastNameInput,"Tên");
            if(!isFirst || !isLast){ event.preventDefault(); registerForm.reportValidity(); }
        });
    }

    function togglePassword(inputId, btn){
        const input=document.getElementById(inputId);
        if(input.type==="password"){ input.type="text"; btn.innerText="Ẩn"; }
        else{ input.type="password"; btn.innerText="Xem"; }
    }
</script>

</body>
</html>