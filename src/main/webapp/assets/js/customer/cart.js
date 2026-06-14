/**
 * HoaNK - HE195013
 * Date: 11/06/2026
 * Description: Viết ajax hỗ trợ thêm sản phẩm vào giỏ hàng
 */
function cart() {
    // lấy ra bộ data bao gồm sizeid, colorid, pid, contextpaht
    const sizeId = document.getElementById("hidden-size-id");
    const colorId = document.getElementById("hidden-color-id");
    const productId = document.getElementById("hidden-product-id");
    const quantity = document.getElementById("hidden-quantity");
    //lấy thẻ chứa data để lấy context path
    const dataConfig = document.getElementById("data-helper");
    const contextPath = dataConfig.getAttribute("data-context-path");
    // tạo đối tượng url search param để đóng gói dữ liệu
    const params = new URLSearchParams();
    params.set('productId', productId.value);
    params.set('colorId', colorId.value);
    params.set('sizeId', sizeId.value);
    params.set('quantity', quantity.value);

    // lấy ra thẻ chứa số lượng của giỏ hàng
    const cartCount = document.getElementById("cart-count");
    const cartOvorQuantity = document.getElementById("cart-over-quantity");
    const addToCartSuccess = document.getElementById("add-to-cart-success");
    axios.post(contextPath + "/api/add-to-cart", params)
        .then(response => {
            if(response.data === "OVER_STOCK") {
                cartOvorQuantity.innerText = "* Số lượng sản phẩm này trong giỏ đã vượt quá giới hạn tồn kho!"
                addToCartSuccess.innerText = "";
            }else {
                cartOvorQuantity.innerText = "";
                addToCartSuccess.innerText = "Thêm sản phẩm thành công vào giỏ hàng!";
                cartCount.innerText = response.data; // gán số lượng trong giỏ hàng len text đọng hiển thị số lượng
            }
        })
        .catch(error => {
            console.error("Thêm vào giỏ hàng thất bại",error);
        })
}
