<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Th&#234;m t&#224;i kho&#7843;n nh&#7853;n ti&#7873;n - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611d">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/add-payout-account.css?v=20260611d">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="payout-account-shell">
    <%
        request.setAttribute("activePage", "finance");
        request.setAttribute("sellerSidebarClass", "sidebar");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="payout-main container-fluid">
        <nav class="payout-breadcrumb d-flex align-items-center">
            <span>T&#224;i ch&#237;nh ng&#432;&#7901;i b&#225;n</span>
            <span>/</span>
            <strong>Th&#234;m t&#224;i kho&#7843;n nh&#7853;n ti&#7873;n</strong>
        </nav>

        <header class="payout-heading">
            <h1>Th&#234;m t&#224;i kho&#7843;n nh&#7853;n ti&#7873;n</h1>
            <p>Li&#234;n k&#7871;t t&#224;i kho&#7843;n ng&#226;n h&#224;ng &#273;&#7875; nh&#7853;n doanh thu t&#7915; shop.</p>
        </header>

        <c:if test="${not empty popupMessage}">
            <div class="page-alert alert ${popupType == 'success' ? 'alert-success' : 'alert-danger'} d-flex align-items-center" role="alert">
                <i data-lucide="${popupType == 'success' ? 'check-circle' : 'alert-triangle'}"></i>
                <span>${popupMessage}</span>
            </div>
        </c:if>

        <div class="payout-content-grid">
            <section class="payout-card card shadow-sm">
                <h2>Th&#244;ng tin t&#224;i kho&#7843;n</h2>

                <c:if test="${otpRequired}">
                    <div class="otp-verification-card">
                        <div>
                            <h3>X&#225;c th&#7921;c Gmail</h3>
                            <p>M&#227; x&#225;c th&#7921;c &#273;&#227; &#273;&#432;&#7907;c g&#7917;i t&#7899;i <strong>${verifiedEmail}</strong>. M&#227; c&#243; hi&#7879;u l&#7921;c trong 10 ph&#250;t.</p>
                        </div>
                        <form class="otp-form"
                              action="${pageContext.request.contextPath}/seller/finance/add-payout-account"
                              method="POST"
                              novalidate>
                            <input type="hidden" name="action" value="verifyOtp">
                            <div class="form-group">
                                <label for="otpCode">M&#227; x&#225;c th&#7921;c</label>
                                <input type="text"
                                       id="otpCode"
                                       name="otpCode"
                                       class="form-control otp-input ${not empty errors.otpCode ? 'input-error' : ''}"
                                       placeholder="Nh&#7853;p m&#227; 6 s&#7889;"
                                       maxlength="6"
                                       inputmode="numeric"
                                       autocomplete="one-time-code"
                                       required>
                                <c:if test="${not empty errors.otpCode}">
                                    <span class="field-error">${errors.otpCode}</span>
                                </c:if>
                            </div>
                            <button type="submit" class="verify-button btn btn-dark">
                                X&#225;c th&#7921;c v&#224; l&#432;u t&#224;i kho&#7843;n
                            </button>
                        </form>
                    </div>
                </c:if>

                <form id="payoutAccountForm"
                      action="${pageContext.request.contextPath}/seller/finance/add-payout-account"
                      method="POST"
                      novalidate>
                    <input type="hidden" name="action" value="requestOtp">
                    <div class="form-group">
                        <label for="bankName">T&#234;n ng&#226;n h&#224;ng</label>
                        <div class="bank-combobox ${not empty errors.bankName ? 'input-error' : ''}">
                            <input type="hidden"
                                   id="bankName"
                                   name="bankName"
                                   value="${oldInput.bankName}">
                            <div class="combo-control">
                                <i data-lucide="search"></i>
                                <input type="search"
                                       id="bankSearch"
                                       class="bank-search-input form-control"
                                       placeholder="Ch&#7885;n ho&#7863;c t&#236;m ng&#226;n h&#224;ng..."
                                       value="${oldInput.bankName}"
                                       autocomplete="off">
                                <button type="button" class="combo-toggle" id="bankToggle" aria-label="M&#7903; danh s&#225;ch ng&#226;n h&#224;ng">
                                    <i data-lucide="chevron-down"></i>
                                </button>
                            </div>
                            <div class="bank-dropdown" id="bankDropdown">
                                <button type="button" class="bank-option" data-value="Vietcombank">Vietcombank</button>
                                <button type="button" class="bank-option" data-value="VietinBank">VietinBank</button>
                                <button type="button" class="bank-option" data-value="BIDV">BIDV</button>
                                <button type="button" class="bank-option" data-value="Agribank">Agribank</button>
                                <button type="button" class="bank-option" data-value="Techcombank">Techcombank</button>
                                <button type="button" class="bank-option" data-value="MB Bank">MB Bank</button>
                                <button type="button" class="bank-option" data-value="ACB">ACB</button>
                                <button type="button" class="bank-option" data-value="Sacombank">Sacombank</button>
                                <button type="button" class="bank-option" data-value="VPBank">VPBank</button>
                                <button type="button" class="bank-option" data-value="TPBank">TPBank</button>
                                <button type="button" class="bank-option" data-value="HDBank">HDBank</button>
                                <button type="button" class="bank-option" data-value="VIB">VIB</button>
                                <button type="button" class="bank-option" data-value="SHB">SHB</button>
                                <button type="button" class="bank-option" data-value="OCB">OCB</button>
                                <button type="button" class="bank-option" data-value="MSB">MSB</button>
                                <button type="button" class="bank-option" data-value="SeABank">SeABank</button>
                                <button type="button" class="bank-option" data-value="LPBank">LPBank</button>
                                <button type="button" class="bank-option" data-value="Eximbank">Eximbank</button>
                                <button type="button" class="bank-option" data-value="Nam A Bank">Nam A Bank</button>
                                <button type="button" class="bank-option" data-value="PVcomBank">PVcomBank</button>
                                <button type="button" class="bank-option" data-value="Bac A Bank">Bac A Bank</button>
                                <button type="button" class="bank-option" data-value="ABBANK">ABBANK</button>
                                <button type="button" class="bank-option" data-value="KienlongBank">KienlongBank</button>
                                <button type="button" class="bank-option" data-value="VietBank">VietBank</button>
                                <button type="button" class="bank-option" data-value="Saigonbank">Saigonbank</button>
                                <button type="button" class="bank-option" data-value="BaoViet Bank">BaoViet Bank</button>
                                <button type="button" class="bank-option" data-value="NCB">NCB</button>
                                <button type="button" class="bank-option" data-value="PGBank">PGBank</button>
                                <button type="button" class="bank-option" data-value="CIMB">CIMB</button>
                                <button type="button" class="bank-option" data-value="UOB">UOB</button>
                                <button type="button" class="bank-option" data-value="Shinhan Bank">Shinhan Bank</button>
                                <button type="button" class="bank-option" data-value="Woori Bank">Woori Bank</button>
                                <button type="button" class="bank-option" data-value="HSBC">HSBC</button>
                                <button type="button" class="bank-option" data-value="Standard Chartered">Standard Chartered</button>
                            </div>
                        </div>
                        <c:if test="${not empty errors.bankName}">
                            <span class="field-error">${errors.bankName}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="accountNumber">S&#7889; t&#224;i kho&#7843;n</label>
                        <input type="text"
                               id="accountNumber"
                               name="accountNumber"
                               class="form-control ${not empty errors.accountNumber ? 'input-error' : ''}"
                               placeholder="Nh&#7853;p s&#7889; t&#224;i kho&#7843;n"
                               inputmode="numeric"
                               maxlength="50"
                               value="${oldInput.accountNumber}"
                               required>
                        <c:if test="${not empty errors.accountNumber}">
                            <span class="field-error">${errors.accountNumber}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="accountHolderName">T&#234;n ch&#7911; t&#224;i kho&#7843;n</label>
                        <input type="text"
                               id="accountHolderName"
                               name="accountHolderName"
                               class="form-control ${not empty errors.accountHolderName ? 'input-error' : ''}"
                               placeholder="NGUYEN VAN A"
                               value="${oldInput.accountHolderName}"
                               required>
                        <c:if test="${not empty errors.accountHolderName}">
                            <span class="field-error">${errors.accountHolderName}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <input type="hidden"
                               id="isDefault"
                               name="isDefault"
                               value="${oldInput.isDefault == 'true' ? 'true' : 'false'}">
                        <label class="checkbox-control" for="isDefaultCheck">
                            <input type="checkbox"
                                   id="isDefaultCheck"
                                   ${oldInput.isDefault == 'true' ? 'checked' : ''}>
                            <span class="checkbox-box"></span>
                            <span>&#272;&#7863;t l&#224;m t&#224;i kho&#7843;n m&#7863;c &#273;&#7883;nh</span>
                        </label>
                        <c:if test="${not empty errors.isDefault}">
                            <span class="field-error">${errors.isDefault}</span>
                        </c:if>
                    </div>

                    <c:if test="${not empty errors.system}">
                        <div class="inline-error">${errors.system}</div>
                    </c:if>

                    <button type="submit" class="save-button btn btn-dark" id="saveButton">
                        G&#7917;i m&#227; x&#225;c th&#7921;c
                    </button>
                </form>
            </section>

            <aside class="business-card card shadow-sm">
                <h2>Quy t&#7855;c nghi&#7879;p v&#7909;</h2>
                <ul>
                    <li>Ch&#7881; l&#432;u bank_name, account_number, account_holder_name, is_default &#273;&#7875; tr&#225;nh ph&#225;t sinh d&#7919; li&#7879;u kh&#244;ng c&#7847;n thi&#7871;t.</li>
                    <li>C&#225;c tr&#432;&#7901;ng b&#7855;t bu&#7897;c ph&#7843;i &#273;&#432;&#7907;c ki&#7875;m tra tr&#432;&#7899;c khi g&#7917;i.</li>
                    <li>Ng&#432;&#7901;i b&#225;n ph&#7843;i x&#225;c th&#7921;c m&#227; OTP Gmail tr&#432;&#7899;c khi l&#432;u t&#224;i kho&#7843;n nh&#7853;n ti&#7873;n.</li>
                    <li>C&#7853;p nh&#7853;t th&#224;nh c&#244;ng s&#7869; &#273;&#432;&#7907;c ghi nh&#7853;n trong c&#417; s&#7903; d&#7919; li&#7879;u.</li>
                </ul>
            </aside>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }

    const form = document.getElementById('payoutAccountForm');
    const bankSearch = document.getElementById('bankSearch');
    const bankName = document.getElementById('bankName');
    const bankDropdown = document.getElementById('bankDropdown');
    const bankToggle = document.getElementById('bankToggle');
    const bankOptions = Array.from(document.querySelectorAll('.bank-option'));
    const accountNumber = document.getElementById('accountNumber');
    const accountHolderName = document.getElementById('accountHolderName');
    const isDefault = document.getElementById('isDefault');
    const isDefaultCheck = document.getElementById('isDefaultCheck');
    const saveButton = document.getElementById('saveButton');
    const otpCode = document.getElementById('otpCode');

    function openBankDropdown() {
        bankDropdown.classList.add('is-open');
    }

    function closeBankDropdown() {
        bankDropdown.classList.remove('is-open');
    }

    function selectBank(value) {
        bankName.value = value;
        bankSearch.value = value;
        closeBankDropdown();
    }

    function syncBankByExactText() {
        const keyword = bankSearch.value.trim().toLowerCase();
        const exact = bankOptions.find(function (option) {
            return option.dataset.value.toLowerCase() === keyword;
        });
        bankName.value = exact ? exact.dataset.value : '';
    }

    bankSearch.addEventListener('input', function () {
        const keyword = this.value.trim().toLowerCase();
        bankOptions.forEach(function (option) {
            option.classList.toggle(
                'is-hidden',
                !option.dataset.value.toLowerCase().includes(keyword)
            );
        });
        syncBankByExactText();
        openBankDropdown();
    });

    bankSearch.addEventListener('focus', openBankDropdown);

    bankToggle.addEventListener('click', function () {
        if (bankDropdown.classList.contains('is-open')) {
            closeBankDropdown();
        } else {
            openBankDropdown();
            bankSearch.focus();
        }
    });

    bankOptions.forEach(function (option) {
        option.addEventListener('click', function () {
            selectBank(this.dataset.value);
        });
    });

    document.addEventListener('click', function (event) {
        if (!event.target.closest('.bank-combobox')) {
            closeBankDropdown();
        }
    });

    accountNumber.addEventListener('input', function () {
        this.value = this.value.replace(/\D/g, '');
    });

    if (otpCode) {
        otpCode.addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').slice(0, 6);
        });
    }

    accountHolderName.addEventListener('input', function () {
        let value = this.value.toUpperCase();
        value = value.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        value = value.replace(/[\u0111\u0110]/g, 'D');
        this.value = value;
    });

    isDefaultCheck.addEventListener('change', function () {
        isDefault.value = this.checked ? 'true' : 'false';
    });

    function clearClientErrors() {
        document.querySelectorAll('.field-error.client').forEach(function (item) {
            item.remove();
        });
        document.querySelectorAll('.input-error').forEach(function (item) {
            item.classList.remove('input-error');
        });
    }

    function showClientError(input, message) {
        input.classList.add('input-error');
        const error = document.createElement('span');
        error.className = 'field-error client';
        error.textContent = message;
        input.closest('.form-group').appendChild(error);
    }

    form.addEventListener('submit', function (event) {
        clearClientErrors();
        let valid = true;

        if (!bankName.value) {
            showClientError(bankSearch, 'Vui l\u00f2ng ch\u1ecdn ng\u00e2n h\u00e0ng trong danh s\u00e1ch.');
            valid = false;
        }

        if (!/^\d{6,50}$/.test(accountNumber.value.trim())) {
            showClientError(accountNumber, 'S\u1ed1 t\u00e0i kho\u1ea3n ch\u1ec9 g\u1ed3m 6 \u0111\u1ebfn 50 ch\u1eef s\u1ed1.');
            valid = false;
        }

        if (!accountHolderName.value.trim()) {
            showClientError(accountHolderName, 'Vui l\u00f2ng nh\u1eadp t\u00ean ch\u1ee7 t\u00e0i kho\u1ea3n.');
            valid = false;
        }

        if (!valid) {
            event.preventDefault();
            return;
        }

        saveButton.disabled = true;
        saveButton.textContent = '\u0110ang g\u1eedi m\u00e3...';
    });

    <c:if test="${not empty popupMessage}">
    Swal.fire({
        title: '${popupType == "success" ? "Thành công" : "Lỗi"}',
        text: '${popupMessage}',
        icon: '${popupType == "success" ? "success" : "error"}',
        confirmButtonColor: '#000000'
    });
    </c:if>
</script>
</body>
</html>
