<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm tài khoản nhận tiền - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/add-payout-account.css">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<jsp:include page="/public/header.jsp"/>

<div class="payout-account-shell">
    <aside class="payout-sidebar">
        <div class="sidebar-block">
            <h2>Seller Center</h2>
            <nav class="seller-nav">
                <a href="${pageContext.request.contextPath}/seller/dashboard/view-seller-dashboard.jsp">
                    <i data-lucide="layout-grid"></i>
                    <span>Tổng quan</span>
                </a>
                <a class="active" href="${pageContext.request.contextPath}/seller/finance/add-payout-account">
                    <i data-lucide="wallet-cards"></i>
                    <span>Ví tiền</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/order/list-seller-orders.jsp">
                    <i data-lucide="archive"></i>
                    <span>Đơn hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/customer_mgt/list-customers.jsp">
                    <i data-lucide="users"></i>
                    <span>Khách hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/product/list-seller-products.jsp">
                    <i data-lucide="clipboard-check"></i>
                    <span>Sản phẩm</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/voucher/list-seller-voucher.jsp">
                    <i data-lucide="badge-percent"></i>
                    <span>Khuyến mãi</span>
                </a>
                <a href="${pageContext.request.contextPath}/public/shop/view-shop.jsp">
                    <i data-lucide="store"></i>
                    <span>Hồ sơ shop</span>
                </a>
                <a href="${pageContext.request.contextPath}/seller/config/edit-shipping-settings.jsp">
                    <i data-lucide="truck"></i>
                    <span>Cấu hình giao hàng</span>
                </a>
            </nav>
        </div>

        <div class="sidebar-block account-block">
            <p>Tài khoản của tôi</p>
            <nav class="seller-nav">
                <a href="${pageContext.request.contextPath}/home">
                    <i data-lucide="shopping-bag"></i>
                    <span>Về trang mua sắm</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer/order/list-orders.jsp">
                    <i data-lucide="receipt-text"></i>
                    <span>Đơn mua của tôi</span>
                </a>
                <a href="${pageContext.request.contextPath}/customer/account/view-profile.jsp">
                    <i data-lucide="circle-user-round"></i>
                    <span>Hồ sơ cá nhân</span>
                </a>
            </nav>
        </div>

        <div class="sidebar-footer">
            <a href="#">
                <i data-lucide="circle-help"></i>
                <span>Hỗ trợ</span>
            </a>
            <a href="#">
                <i data-lucide="log-out"></i>
                <span>Đăng xuất</span>
            </a>
        </div>
    </aside>

    <main class="payout-main container-fluid">
        <nav class="payout-breadcrumb d-flex align-items-center">
            <span>Seller Finance</span>
            <span>/</span>
            <strong>Add Payout Account</strong>
        </nav>

        <header class="payout-heading">
            <h1>Add Payout Account</h1>
            <p>Link bank account information to receive seller revenue.</p>
        </header>

        <c:if test="${not empty popupMessage}">
            <div class="page-alert alert ${popupType == 'success' ? 'alert-success' : 'alert-danger'} d-flex align-items-center" role="alert">
                <i data-lucide="${popupType == 'success' ? 'check-circle' : 'alert-triangle'}"></i>
                <span>${popupMessage}</span>
            </div>
        </c:if>

        <div class="payout-content-grid">
            <section class="payout-card card shadow-sm">
                <h2>Input Information</h2>

                <c:if test="${otpRequired}">
                    <div class="otp-verification-card">
                        <div>
                            <h3>Xác thực Gmail</h3>
                            <p>Mã xác thực đã được gửi tới <strong>${verifiedEmail}</strong>. Mã có hiệu lực trong 10 phút.</p>
                        </div>
                        <form class="otp-form"
                              action="${pageContext.request.contextPath}/seller/finance/add-payout-account"
                              method="POST"
                              novalidate>
                            <input type="hidden" name="action" value="verifyOtp">
                            <div class="form-group">
                                <label for="otpCode">Mã xác thực</label>
                                <input type="text"
                                       id="otpCode"
                                       name="otpCode"
                                       class="form-control otp-input ${not empty errors.otpCode ? 'input-error' : ''}"
                                       placeholder="Nhập mã 6 số"
                                       maxlength="6"
                                       inputmode="numeric"
                                       autocomplete="one-time-code"
                                       required>
                                <c:if test="${not empty errors.otpCode}">
                                    <span class="field-error">${errors.otpCode}</span>
                                </c:if>
                            </div>
                            <button type="submit" class="verify-button btn btn-dark">
                                Verify & Save Account
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
                        <label for="bankName">Tên ngân hàng</label>
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
                                       placeholder="Chọn hoặc tìm ngân hàng..."
                                       value="${oldInput.bankName}"
                                       autocomplete="off">
                                <button type="button" class="combo-toggle" id="bankToggle" aria-label="Mở danh sách ngân hàng">
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
                        <label for="accountNumber">Số tài khoản</label>
                        <input type="text"
                               id="accountNumber"
                               name="accountNumber"
                               class="form-control ${not empty errors.accountNumber ? 'input-error' : ''}"
                               placeholder="Nhập số tài khoản"
                               inputmode="numeric"
                               maxlength="50"
                               value="${oldInput.accountNumber}"
                               required>
                        <c:if test="${not empty errors.accountNumber}">
                            <span class="field-error">${errors.accountNumber}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="accountHolderName">Tên chủ tài khoản</label>
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
                            <span>Đặt làm tài khoản mặc định</span>
                        </label>
                        <c:if test="${not empty errors.isDefault}">
                            <span class="field-error">${errors.isDefault}</span>
                        </c:if>
                    </div>

                    <c:if test="${not empty errors.system}">
                        <div class="inline-error">${errors.system}</div>
                    </c:if>

                    <button type="submit" class="save-button btn btn-dark" id="saveButton">
                        Send Verification Code
                    </button>
                </form>
            </section>

            <aside class="business-card card shadow-sm">
                <h2>Business Rule</h2>
                <ul>
                    <li>Chỉ lưu bank_name, account_number, account_holder_name, is_default để tránh phình database.</li>
                    <li>Required fields must be validated before submit.</li>
                    <li>Seller must verify Gmail OTP before the payout account is saved.</li>
                    <li>Successful updates are recorded in database.</li>
                </ul>
            </aside>
        </div>
    </main>
</div>

<jsp:include page="/public/footer.jsp"/>

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
        value = value.replace(/[đĐ]/g, 'D');
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
            showClientError(bankSearch, 'Vui lòng chọn ngân hàng trong danh sách.');
            valid = false;
        }

        if (!/^\d{6,50}$/.test(accountNumber.value.trim())) {
            showClientError(accountNumber, 'Số tài khoản chỉ gồm 6 đến 50 chữ số.');
            valid = false;
        }

        if (!accountHolderName.value.trim()) {
            showClientError(accountHolderName, 'Vui lòng nhập tên chủ tài khoản.');
            valid = false;
        }

        if (!valid) {
            event.preventDefault();
            return;
        }

        saveButton.disabled = true;
        saveButton.textContent = 'Sending code...';
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
