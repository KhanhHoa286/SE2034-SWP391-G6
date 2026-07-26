function selectPayment(method) {
    document.getElementById('pm-cod').classList.toggle('selected', method === 'cod');
    document.getElementById('pm-bank').classList.toggle('selected', method === 'bank_transfer');
    document.getElementById('bankInfoBox').classList.toggle('active', method === 'bank_transfer');
}

window.addEventListener("pageshow", function (event) {
    if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
        window.location.reload();
    }
});

