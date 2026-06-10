<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Y&#234;u c&#7847;u r&#250;t ti&#7873;n - MODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611d">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/add-payout-request.css?v=20260611d">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="payout-request-shell">
    <%
        request.setAttribute("activePage", "finance");
        request.setAttribute("sellerSidebarClass", "sidebar");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="payout-main container-fluid">
        <nav class="payout-breadcrumb d-flex align-items-center">
            <span>T&#224;i ch&#237;nh ng&#432;&#7901;i b&#225;n</span>
            <span>/</span>
            <strong>Y&#234;u c&#7847;u r&#250;t ti&#7873;n</strong>
        </nav>

        <header class="payout-heading">
            <h1>Y&#234;u c&#7847;u r&#250;t ti&#7873;n</h1>
            <p>Nh&#7853;p s&#7889; ti&#7873;n mu&#7889;n r&#250;t t&#7915; v&#237; ng&#432;&#7901;i b&#225;n v&#7873; t&#224;i kho&#7843;n ng&#226;n h&#224;ng.</p>
        </header>

        <c:if test="${not empty popupMessage}">
            <div class="page-alert alert ${popupType == 'success' ? 'alert-success' : 'alert-danger'} d-flex align-items-center" role="alert">
                <i data-lucide="${popupType == 'success' ? 'check-circle' : 'alert-triangle'}"></i>
                <span>${popupMessage}</span>
            </div>
        </c:if>

        <section class="wallet-summary">
            <article class="card shadow-sm">
                <span>S&#7889; d&#432; kh&#7843; d&#7909;ng</span>
                <strong><fmt:formatNumber value="${availableBalance}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>S&#7889; ti&#7873;n c&#243; th&#7875; g&#7917;i y&#234;u c&#7847;u r&#250;t</small>
            </article>
            <article class="card shadow-sm">
                <span>S&#7889; d&#432; &#273;ang ch&#7901;</span>
                <strong><fmt:formatNumber value="${pendingBalance}" type="number" maxFractionDigits="0"/>&#273;</strong>
                <small>S&#7889; ti&#7873;n &#273;ang ch&#7901; qu&#7843;n tr&#7883; vi&#234;n x&#7917; l&#253;</small>
            </article>
        </section>

        <div class="payout-content-grid">
            <section class="payout-card card shadow-sm">
                <h2>Th&#244;ng tin y&#234;u c&#7847;u</h2>

                <form id="payoutRequestForm"
                      action="${pageContext.request.contextPath}/seller/finance/add-payout-request"
                      method="POST"
                      data-available="${availableBalance}"
                      data-min-amount="10000"
                      novalidate>
                    <div class="form-group">
                        <label for="payoutAccountId">T&#224;i kho&#7843;n nh&#7853;n ti&#7873;n</label>
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
                                            <c:if test="${account.isDefault}"> (M&#7863;c &#273;&#7883;nh)</c:if>
                                        </option>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <option value="">Ch&#432;a c&#243; t&#224;i kho&#7843;n nh&#7853;n ti&#7873;n</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                        <c:if test="${not empty errors.payoutAccountId}">
                            <span class="field-error">${errors.payoutAccountId}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="amount">S&#7889; ti&#7873;n</label>
                        <div class="amount-field ${not empty errors.amount ? 'input-error' : ''}">
                            <input type="text"
                                   id="amount"
                                   name="amount"
                                   class="amount-input"
                                   placeholder="T&#7889;i thi&#7875;u 10.000"
                                   inputmode="numeric"
                                   value="${oldInput.amount}"
                                   required>
                            <span>&#273;</span>
                        </div>
                        <c:if test="${not empty errors.amount}">
                            <span class="field-error">${errors.amount}</span>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="withdrawalNote">Ghi ch&#250; r&#250;t ti&#7873;n</label>
                        <textarea id="withdrawalNote"
                                  name="withdrawalNote"
                                  class="form-control textarea-control"
                                  rows="3"
                                  maxlength="255"
                                  placeholder="R&#250;t doanh thu">${oldInput.withdrawalNote}</textarea>
                    </div>

                    <div class="form-group confirm-group">
                        <label class="checkbox-control" for="confirm">
                            <input type="checkbox"
                                   id="confirm"
                                   name="confirm"
                                   value="true"
                                   ${param.confirm == 'true' ? 'checked' : ''}>
                            <span class="checkbox-box"></span>
                            <span>T&#244;i x&#225;c nh&#7853;n g&#7917;i y&#234;u c&#7847;u r&#250;t ti&#7873;n v&#224; &#273;&#7891;ng &#253; kh&#243;a s&#7889; ti&#7873;n n&#224;y trong v&#237;.</span>
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
                        G&#7917;i y&#234;u c&#7847;u
                    </button>

                    <c:if test="${empty payoutAccounts}">
                        <a class="link-action btn btn-outline-dark" href="${pageContext.request.contextPath}/seller/finance/add-payout-account">
                            Th&#234;m t&#224;i kho&#7843;n ng&#226;n h&#224;ng
                        </a>
                    </c:if>
                </form>
            </section>

            <aside class="business-card card shadow-sm">
                <h2>Quy t&#7855;c nghi&#7879;p v&#7909;</h2>
                <ul>
                    <li>S&#7889; ti&#7873;n y&#234;u c&#7847;u kh&#244;ng &#273;&#432;&#7907;c v&#432;&#7907;t qu&#225; s&#7889; d&#432; kh&#7843; d&#7909;ng trong v&#237;.</li>
                    <li>S&#7889; ti&#7873;n r&#250;t t&#7889;i thi&#7875;u l&#224; 10.000&#273;.</li>
                    <li>T&#224;i kho&#7843;n nh&#7853;n ti&#7873;n &#273;&#432;&#7907;c ch&#7885;n ph&#7843;i thu&#7897;c shop c&#7911;a ng&#432;&#7901;i b&#225;n hi&#7879;n t&#7841;i.</li>
                    <li>Ng&#432;&#7901;i b&#225;n ph&#7843;i x&#225;c nh&#7853;n tr&#432;&#7899;c khi g&#7917;i y&#234;u c&#7847;u.</li>
                    <li>Sau khi g&#7917;i, ti&#7873;n s&#7869; chuy&#7875;n t&#7915; s&#7889; d&#432; kh&#7843; d&#7909;ng sang s&#7889; d&#432; &#273;ang ch&#7901;.</li>
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
            showClientError(payoutAccount, 'Vui l\u00f2ng ch\u1ecdn t\u00e0i kho\u1ea3n nh\u1eadn ti\u1ec1n.');
            valid = false;
        }

        if (requestedAmount <= 0) {
            showClientError(amount.closest('.amount-field'), 'Vui l\u00f2ng nh\u1eadp s\u1ed1 ti\u1ec1n r\u00fat l\u1edbn h\u01a1n 0.');
            valid = false;
        } else if (requestedAmount < minPayoutAmount) {
            showClientError(amount.closest('.amount-field'), 'S\u1ed1 ti\u1ec1n r\u00fat t\u1ed1i thi\u1ec3u l\u00e0 10.000\u0111.');
            valid = false;
        } else if (requestedAmount > availableBalance) {
            showClientError(amount.closest('.amount-field'), 'S\u1ed1 ti\u1ec1n r\u00fat kh\u00f4ng \u0111\u01b0\u1ee3c l\u1edbn h\u01a1n s\u1ed1 d\u01b0 kh\u1ea3 d\u1ee5ng.');
            valid = false;
        }

        if (!confirmBox.checked) {
            showClientError(confirmBox.closest('.checkbox-control'), 'B\u1ea1n ph\u1ea3i x\u00e1c nh\u1eadn tr\u01b0\u1edbc khi g\u1eedi y\u00eau c\u1ea7u.');
            valid = false;
        }

        if (!valid) {
            event.preventDefault();
            return;
        }

        amount.value = String(requestedAmount);
        submitButton.disabled = true;
        submitButton.textContent = '\u0110ang g\u1eedi...';
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
