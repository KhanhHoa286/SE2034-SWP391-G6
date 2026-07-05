/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Viết ajax hỗ trợ thêm,giảm, số lượng sản phẩm vào giỏ hàng
 *                               Xóa items trong giỏ hàng
 */
// lấy ra thẻ chứa tổng tiền
const newAllShopTotal = document.getElementById("new-all-shop-total");
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
            const data = response.data;
            if(data.status === "SUCCESS") {
                quantityInput.value = quantityItem;
                // shopTotal.innerText = data.newShopTotal;
                newAllShopTotal.innerText = data.newAllShopTotal;
            }
            if(data.status === "OVER_STOCK") {
                spanError.innerText = "* Vượt quá số lượng biến thể trong kho!";
            }
            if(data.status === "INVALID_STOCK"){
                spanError.innerText = "* Số lượng biến thể phải > 0";
                quantityInput.value = 1;
            }
        })
        .catch(error => {
            console.error("Update quantity item cart thất bại!", error);
        })
}


 // Xóa 1 items trong giỏ hàng

function removeAnItem(contextPath, cartItemId, btn) {
    const params = new URLSearchParams();
    params.set("cart_item_id", cartItemId);

    axios.post(contextPath + "/api/customer/delete-cart-item", params)
        .then(response => {
            const data = response.data;
            if(data.status === "SUCCESS") {
                // nếu thành công thì lấy khối cha chưa nguyên cái cart items
                const item_cart_container = btn.closest(".cart-item");
                // lấy ra khối shop chứa các khối cart items
                const item_shop_container = btn.closest(".vendor-group");
                // lấy ra số items trên header
                const cartCount = document.getElementById("cart-count");
                // xóa 1 ô items
                item_cart_container.remove();
                // xóa ô shop nếu bên trong không còn sản phẩm nào
                const quantityItemInShopCart = item_shop_container.querySelectorAll(".cart-item");
                if(quantityItemInShopCart.length === 0) {
                    item_shop_container.remove();
                }
                // xét lại số lượng trên header sau khi xóa thành công
                if(parseInt(cartCount.innerText) > 0) {
                cartCount.innerText = parseInt(cartCount.innerText) - 1;
                }
                newAllShopTotal.innerText = data.newAllShopTotal;
            }

            if(data.status === "UNAUTHORIZED") {
                alert("Phiên đăng nhập đã hết hạn vui lòng đăng nhập lại!");
                window.location.href = contextPath + "/login";
            }
        })
        .catch(error => {
            console.log("Xóa cart items thất bại!", error);
        })
}



 //cập nhật biến thể khi tích checkbox
function getListCheckbox(contextPath,cartItemId,btn) {
    const checkoutError = document.getElementById("checkout-error");
    const params = new URLSearchParams();
    const isSelected = btn.checked; // trả về true false
    params.set('cart_item_id', cartItemId);
    params.set('is_selected', isSelected);

    axios.post(contextPath + "/api/customer/update-checkbox", params)
        .then(response =>{
            const data = response.data;

            newAllShopTotal.innerText = data.totalPriceAllShop;
            updateCheckoutButtonLink(contextPath);
        })
        .catch(error => {
            console.log("Thêm biến thể giỏ hàng thất bại!", error);
        })
}


// Lấy tất cả những sản phâẩm được checkbox trong giỏ hàng
function getCheckedCartItemIds() {
    const checkedCheckboxes = document.querySelectorAll(".cart-item__checkbox:checked");
    const idsArray = Array.from(checkedCheckboxes).map(checkbox => checkbox.value);
    return idsArray.join(",");
}

//
function goToCheckout(contextPath) {
    const cartItemIds = getCheckedCartItemIds();
    const checkoutError = document.getElementById("checkout-error");
    if (!cartItemIds) {
        checkoutError.innerText = "* Vui lòng tích chọn ít nhất 1 sản phẩm...";
        return;
    }
    checkoutError.innerText = "";
    window.location.href = contextPath + "/customer/add-order?type=CART&list_cart_item_id=" + cartItemIds;
}

// cập nhật link cho thẻ a sang trang thanh toán
function updateCheckoutButtonLink(contextPath) {
    const cartItemIds = getCheckedCartItemIds();
    const checkoutBtn = document.querySelector(".checkout-btn");

    if (checkoutBtn) {
        // Cập nhật lại href động liên tục
        checkoutBtn.setAttribute("href", contextPath + "/customer/add-order?type=CART&list_cart_item_id=" + cartItemIds);
    }
}

window.addEventListener("pageshow", function (event) {
    if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
        window.location.reload();
    }
});