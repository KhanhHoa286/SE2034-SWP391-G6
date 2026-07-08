function selectPayment(method) {
    document.getElementById('pm-cod').classList.toggle('selected', method === 'cod');
    document.getElementById('pm-bank').classList.toggle('selected', method === 'bank_transfer');
    if(method === 'bank_transfer') {
     const pmBank = document.getElementById('pm-bank');
     const userId = pmBank.dataset.userId;
     addPaymentContent(userId);
    }
    document.getElementById('bankInfoBox').classList.toggle('active', method === 'bank_transfer');
}

function addPaymentContent(userId){
    const  payMentContent = document.getElementById("paymentContent");
    const hiddenTransferCode = document.getElementById("hidden-transaction-code");
    //
    const now = new Date();

    // Tự động thêm số 0 phía trước nếu số < 10 (Ví dụ: 5 giây thành "05")
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    stringContent = `MODA_${userId}_${hours}${minutes}${seconds}`;
        payMentContent.innerText = stringContent;
      hiddenTransferCode.value = stringContent;
}

window.addEventListener("pageshow", function (event) {
    if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
        window.location.reload();
    }
});

