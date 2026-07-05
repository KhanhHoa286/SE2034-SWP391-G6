/**
 * HoaNK - HE195013
 * Date: 26/6/2026
 * Description: Cập nhât trạng thái đơn hàng và thanh toán khi khách hàng nhấn đã nhận được hàng
 */
function updateStatusOrder(contextPath,subOrderId,paymentMethod,masterOrderId,btn) {
    //
    const params = new URLSearchParams();
    params.set('sub_order_id', subOrderId);
    params.set('payment_method', paymentMethod);
    params.set('master_order_id', masterOrderId);
    axios.post(contextPath + "/api/customer/update-status-order", params)
        .then(response => {
            const data = response.data;
            if(data.status === 'SUCCESS'){
                btn.disabled = true;
                btn.innerText = "Đã nhận hàng";
                btn.classList.add('update-status-order');
                // cập nhât trạng thái đang giao => đã nhận
                window.location.reload();
            }
        })
        .catch(error => {
            console.log("Update thất bại!", error);
        })
}

window.addEventListener("pageshow", function (event) {
    if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
        window.location.reload();
    }
});