/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Viết ajax hỗ trợ thêm,giảm, số lượng sản phẩm vào giỏ hàng
 *                               Xóa items trong giỏ hàng
 */
// lấy ra thẻ chứa tổng tiền
const newAllShopTotal = document.getElementById("new-all-shop-total");
// + - items trong giỏ hàng ( <0 báo lỗi, vượt quá số lượng kho báo lỗi
function updateItemQuantity(contextPath, cartItemId, variantId,shopId,action,btn) {
    // tìm khối cha gần nhất bao cả span lỗi
    const container = btn.closest(".cart-item__actions");
    // lấy ra cha của khối shopTtotal
    const vendorGroup = btn.closest(".vendor-group");
    let shopTotal = null;
    if(vendorGroup) {
         shopTotal = vendorGroup.querySelector(".shopTotal");
    }

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
    params.set('shop_id', shopId);
    // axios
    axios.post(contextPath + "/api/customer/update-cart", params)
        .then(response => {
            const data = response.data;
            if(data.status === "SUCCESS") {
                quantityInput.value = quantityItem;
                shopTotal.innerText = data.newShopTotal;
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

/**
 * HoaNK - Xóa 1 items trong giỏ hàng
 */
function removeAnItem(contextPath, cartItemId,shopId, btn) {
    const params = new URLSearchParams();
    params.set("cart_item_id", cartItemId);
    params.set("shop_id", shopId);
    // lấy ra cha của khối shopTtotal
    const vendorGroup = btn.closest(".vendor-group");
    let shopTotal = null;
    if(vendorGroup) {
        shopTotal = vendorGroup.querySelector(".shopTotal");
    }

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
                }else{
                    shopTotal.innerText = data.newShopTotal;
                }
                // xét lại số lượng trên header sau khi xóa thành công
                if(parseInt(cartCount.innerText) > 0) {
                cartCount.innerText = parseInt(cartCount.innerText) - 1;
                }

                shopTotal.innerText = data.newShopTotal;
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

/**
 * HoaNK - quét qua các nuút checkbox để edit giá tiền chung và giá tiền riêng từng shop
 */
function editTotalPrice() {

}