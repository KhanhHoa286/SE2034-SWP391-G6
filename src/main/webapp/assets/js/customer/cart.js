/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Viết ajax hỗ trợ thêm,giảm, số lượng sản phẩm vào giỏ hàng
 *                               Xóa items trong giỏ hàng
 */

// + - items trong giỏ hàng ( <0 báo lỗi, vượt quá số lượng kho báo lỗi
function updateItemQuantity(contextPath, cartItemId, variantId,action,btn) {
    // tìm khối cha gần nhất bao cả span lỗi
    const container = btn.closest(".cart-item__actions");

    // lấy số lượng trong ô nhập và thẻ lỗi
    const quantityInput = container.querySelector(".qty-input");
    let quantity = parseInt(quantityInput.value);
    let quantityItem = (action === 'increase') ? quantity + 1 : quantity - 1;
    // nếu số lượng bị trừ xuống < 1 thì dừng luôn
    if(quantityItem < 1) return;

    const spanError = container.querySelector(".stock-error");
    spanError.innerText = "";
    // tạo đối tượng param
    const params = new URLSearchParams();
    params.set('cart_item_id', cartItemId);
    params.set('quantity_item', quantityItem);
    params.set('variant_id', variantId);
    // axios
    axios.post(contextPath + "/api/customer/update-cart", params)
        .then(response => {
            const message = response.data;
            if(message === "SUCCESS") {
                quantityInput.value = quantityItem;
            }
            if(message === "OVER_STOCK") {
                spanError.innerText = "* Vượt quá số lượng biến thể trong kho!";
            }
            if(message === "INVALID_STOCK"){
                spanError.innerText = "* Số lượng biến thể phải > 0";
                quantityInput.value = 1;
            }
        })
        .catch(error => {
            console.error("Update quantity item cart thất bại!", error);
        })
}