<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Payout Request - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/add-payout-request.css">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<jsp:include page="/public/header.jsp"/>

<div class="payout-request-shell">
    <aside class="payout-sidebar">
        <div class="sidebar-block">
            <h2>Seller Center</h2>
            <nav class="seller-nav">
                <a href="${pageContext.request.contextPath}/seller/dashboard/view-seller-dashboard.jsp">
                    <i data-lucide="layout-grid"></i>
                    <span>Tổng quan</span>
                </a>
                <a class="active" href="${pageContext.request.contextPath}/seller/finance/view-wallet">
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
                <a href="${pageContext.request.contextPath}/seller/shop/view-shop.jsp">
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
            <strong>Add Payout Request</strong>
        </nav>

        <header class="payout-heading">
            <h1>Add Payout Request</h1>
            <p>Seller enters the amount to withdraw from wallet to bank card.</p>
        </header>

        <c:if test="${not empty popupMessage}">
            <div class="page-alert alert ${popupType == 'success' ? 'alert-success' : 'alert-danger'} d-flex align-items-center" role="alert">
                <i data-lucide="${popupType == 'success' ? 'check-circle' : 'alert-triangle'}"></i>
                <span>${popupMessage}</span>
            </div>
        </c:if>

        <section class="wallet-summary">
            <article class="card shadow-sm">
                <span>Available Balance</span>
                <strong><fmt:formatNumber value="${availableBalance}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Số tiền có thể gửi yêu cầu rút</small>
            </article>
            <article class="card shadow-sm">
                <span>Pending Balance</span>
                <strong><fmt:formatNumber value="${pendingBalance}" type="number" maxFractionDigits="0"/>đ</strong>
                <small>Số tiền đang chờ admin xử lý</small>
            </article>
        </section>

        <div class="payout-content-grid">
            <section class="payout-card card shadow-sm">
                <h2>Input Information</h2>

                <form id="payoutRequestForm"
                      action="${pageContext.request.contextPath}/seller/finance/add-payout-request"
                      method="POST"
                      data-available="${availableBalance}"
                      data-min-amount="10000"
                      novalidate>
                    <div class="form-group">
                        <label for="payoutAccountId">Payout Account</label>
                        <select id="payoutAccountId"
                                name="payoutAccountId"
                                class="form-control form-select ${not empty errors.payoutAccountId ? 'input-error' : ''}"
                                required>
                            <c:choose>
                                <c:when test="${not empty payoutAccounts}">
                                    <c:forEach var="account" items="${payoutAccounts}">
                                        <option value="${account.accountId}"
                                                ${not empty oldInput.payoutAccountId
                                                        ? (oldInput.payoutAccountId == account.accountId ? 'selected' : '')
                                                        : (account.isDefault ? 'selected' : '')}>
                                            ${account.bankName} - ${account.accountNumber} - ${account.accountHolderName}
                                            <c:if test="${account.isDefault}"> (Mặc định)</c:if>
                                        </option>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <option value="">Chưa có tài khoản nhận tiền</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                        <c:if test="${not empty errors.payoutAccountId}">
                            <span class="field-error">${errors.payoutAccountId}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="amount">Amount</label>
                        <div class="amount-field ${not empty errors.amount ? 'input-error' : ''}">
                            <input type="text"
                                   id="amount"
                                   name="amount"
                                   class="amount-input"
                                   placeholder="Tối thiểu 10.000"
                                   inputmode="numeric"
                                   value="${oldInput.amount}"
                                   required>
                            <span>đ</span>
                        </div>
                        <c:if test="${not empty errors.amount}">
                            <span class="field-error">${errors.amount}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="withdrawalNote">Withdrawal Note</label>
                        <textarea id="withdrawalNote"
                                  name="withdrawalNote"
                                  class="form-control textarea-control"
                                  rows="3"
                                  maxlength="255"
                                  placeholder="Revenue withdrawal">${oldInput.withdrawalNote}</textarea>
                    </div>

                    <div class="form-group confirm-group">
                        <label class="checkbox-control" for="confirm">
                            <input type="checkbox"
                                   id="confirm"
                                   name="confirm"
                                   value="true"
                                   ${param.confirm == 'true' ? 'checked' : ''}>
                            <span class="checkbox-box"></span>
                            <span>Tôi xác nhận gửi yêu cầu rút tiền và đồng ý khóa số tiền này trong ví.</span>
                        </label>
                        <c:if test="${not empty errors.confirm}">
                            <span class="field-error">${errors.confirm}</span>
                        </c:if>
                    </div>

                    <c:if test="${not empty errors.system}">
                        <div class="inline-error">${errors.system}</div>
                    </c:if>

                    <button type="submit"
                            class="submit-button btn btn-dark"
                            id="submitButton"
                            ${empty payoutAccounts ? 'disabled' : ''}>
                        Submit Request
                    </button>

                    <c:if test="${empty payoutAccounts}">
                        <a class="link-action btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-account">
                            Add Bank Account
                        </a>
                    </c:if>
                </form>
            </section>

            <aside class="business-card card shadow-sm">
                <h2>Business Rule</h2>
                <ul>
                    <li>Requested amount must not exceed available wallet balance.</li>
                    <li>Minimum payout amount is 10.000đ.</li>
                    <li>Selected payout account must belong to the current seller shop.</li>
                    <li>Seller must confirm before the request can be submitted.</li>
                    <li>After submission, money moves from available balance to pending balance.</li>
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

    const form = document.getElementById('payoutRequestForm');
    const payoutAccount = document.getElementById('payoutAccountId');
    const amount = document.getElementById('amount');
    const confirmBox = document.getElementById('confirm');
    const submitButton = document.getElementById('submitButton');
    const availableBalance = Number(form.dataset.available || 0);
    const minPayoutAmount = Number(form.dataset.minAmount || 10000);

    function parseMoney(value) {
        return Number(String(value).replace(/[^\d]/g, '')) || 0;
    }

    function formatMoney(value) {
        const digits = String(value).replace(/[^\d]/g, '');
        return digits.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }

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

    amount.addEventListener('input', function () {
        this.value = formatMoney(this.value);
    });

    form.addEventListener('submit', function (event) {
        clearClientErrors();
        let valid = true;
        const requestedAmount = parseMoney(amount.value);

        if (!payoutAccount.value) {
            showClientError(payoutAccount, 'Vui lòng chọn tài khoản nhận tiền.');
            valid = false;
        }

        if (requestedAmount <= 0) {
            showClientError(amount.closest('.amount-field'), 'Vui lòng nhập số tiền rút lớn hơn 0.');
            valid = false;
        } else if (requestedAmount < minPayoutAmount) {
            showClientError(amount.closest('.amount-field'), 'Số tiền rút tối thiểu là 10.000đ.');
            valid = false;
        } else if (requestedAmount > availableBalance) {
            showClientError(amount.closest('.amount-field'), 'Số tiền rút không được lớn hơn số dư khả dụng.');
            valid = false;
        }

        if (!confirmBox.checked) {
            showClientError(confirmBox.closest('.checkbox-control'), 'Bạn phải xác nhận trước khi gửi yêu cầu.');
            valid = false;
        }

        if (!valid) {
            event.preventDefault();
            return;
        }

        amount.value = String(requestedAmount);
        submitButton.disabled = true;
        submitButton.textContent = 'Submitting...';
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
