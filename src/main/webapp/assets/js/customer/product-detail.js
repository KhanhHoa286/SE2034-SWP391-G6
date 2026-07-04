  /**
   * HoaNK - HE195013
   * Date:
   * Description: Viết ajax hỗ trợ thay đổi ảnh sản phẩm, chọn màu sắc, kích cỡ và lấy tồn kho tương ứng với ajax
   *              API Thêm vào giỏ hàng, cập nhật link mua theo từng biến thể
   */
// thay đổi ảnh
    function changeImage(element) {
    // 1. Lấy cái ảnh to nhất ra
    let mainImage = document.getElementById("zoom-image");
    // 2. Thay đổi đường dẫn src của ảnh to bằng đường dẫn của ảnh phụ vừa click
    mainImage.src = element.src;
    // 3. (Tùy chọn UX) Xóa class 'active' ở tất cả ảnh phụ cũ và nạp vào ảnh phụ mới click
    let thumbnails = document.querySelectorAll(".product-gallery__thumb");
    thumbnails.forEach(thumb => thumb.classList.remove("active"));

    element.classList.add("active");
}
    // hàm chọn màu sắc
    function selectColor(button) {
    // 1. Gỡ bỏ class active của tất cả các nút màu cũ và gán cho nút vừa bấm
    document.querySelectorAll('.color-list').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');

    const colorId = button.getAttribute('data-color-id');
    document.getElementById('hidden-color-id').value = colorId;

    getVariantStock();
}

    // hàm chọn kích cơ
    function selectSize(button) {
    document.querySelectorAll('.size-list').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');

    const sizeId = button.getAttribute('data-size-id');
    document.getElementById('hidden-size-id').value = sizeId;

    getVariantStock();
}

    // ajax cho việc lấy tồn kho khi chọn size và color
    function getVariantStock() {
    // lấy thẻ chưas data
        const dataConfig = document.getElementById("data-helper");
    // kiểm tra seller
    const checkSeller = dataConfig.getAttribute("data-check-seller") === 'true';
    // constext path
        const contextPath = dataConfig.getAttribute("data-context-path");
    // lâấy ra 3 tham số để gửi đi
    const productId = document.getElementById("hidden-product-id").value;
    const sizeId = document.getElementById("hidden-size-id").value;
    const colorId = document.getElementById("hidden-color-id").value;
    const productSeller = document.getElementById("product-seller");
    // lấy 2 nút ấn và thẻ thẻ hiện thị số lượng
    const stockDisplay = document.getElementById("stock-display");
    const addToCart = document.getElementById("add-to-cart");
    const addOrder = document.getElementById("add-order");
    //
    if (checkSeller) {
    productSeller.innerHTML = "* Sản phẩm thuộc shop! Không thể mua!"
    addToCart.disabled = true;
    addOrder.classList.add('disabled');
    return;
}
    // bắn dữ liệu đi
    axios.get(contextPath+"/api/get-variant-stock", {
    params: {
    product_id: productId,
    size_id: sizeId,
    color_id: colorId
}
}).then(response => {
    if(parseInt(response.data) > 0) {
    stockDisplay.innerHTML = 'Còn lại: <strong class="text-success">' + response.data + '</strong> sản phẩm có sẵn';
    if(!checkSeller) {
    addToCart.disabled = false;
    addOrder.classList.remove('disabled'); // nếu còn thif cho thao tác
}
}else{
    stockDisplay.innerHTML = `<strong class="text-danger">Tạm hết hàng</strong> cho phân loại này`;
    addToCart.disabled = true;
    addOrder.classList.add('disabled'); // nếu hết hàng thì khóa 2 nút
}
})
    .catch(error=> {
    console.error("Lỗi lấy kho:", error);
    stockDisplay.innerText = "Không thể lấy thông tin tồn kho";
})
}
    getVariantStock();


    // xử lí nút thêm  vào giỏ và mua ngay
  function cartAndBuyNow(type) {
      const sizeId = document.getElementById("hidden-size-id").value;
      const colorId = document.getElementById("hidden-color-id").value;
      const productId = document.getElementById("hidden-product-id").value;
      const quantity = parseInt(document.getElementById("hidden-quantity").value); // Chuyển về số nguyên

      const dataConfig = document.getElementById("data-helper");
      const contextPath = dataConfig.getAttribute("data-context-path");

      const cartOvorQuantity = document.getElementById("cart-over-quantity");
      const addToCartSuccess = document.getElementById("add-to-cart-success");

      // khi khahcs hàng chọn nút mua ngay
      if ('BUY_NOW' === type) {
          axios.get(contextPath + "/api/get-variant-stock", {
              params: {
                  product_id: productId,
                  size_id: sizeId,
                  color_id: colorId
              }
          })
              .then(response => {
                  // số lượng trong kho
              const stockAvailable = parseInt(response.data);

              if (quantity > stockAvailable) {
                  //nếu số lượng gõ mua > kho =>báo lỗi
                  cartOvorQuantity.innerText = "* Số lượng sản phẩm này trong giỏ đã vượt quá giới hạn tồn kho!!";
                  if (addToCartSuccess)
                      addToCartSuccess.innerText = "";
              } else {
                  //nếu kho vật lý đủ hàng -> Cho bay thẳng sang trang thanh toán luôn!
                  cartOvorQuantity.innerText = "";
                  window.location.href = `${contextPath}/customer/add-order?type=DETAILS_PRODUCT`
                      + `&product_id=${productId}`
                      + `&size_id=${sizeId}`
                      + `&color_id=${colorId}`
                      + `&quantity=${quantity}`;
              }
          }).catch(error => {
              console.error("Lỗi kiểm tra kho khi mua ngay:", error);
          });
          return;
      }

      //nếu khách hàng hconj thêm vào giỏ hàng type = CART
      if ('CART' === type) {
          const params = new URLSearchParams();
          params.set('product_id', productId);
          params.set('color_id', colorId);
          params.set('size_id', sizeId);
          params.set('quantity', quantity);
          const cartCount = document.getElementById("cart-count");

          axios.post(contextPath + "/api/customer/add-to-cart", params)
              .then(response => {
                  if (response.data === "INVALID_VARIANT") {
                      window.location.href = contextPath + "/product-detail?pid=" + productId;
                      return;
                  }
                  if (response.data === "OVER_STOCK") {
                      cartOvorQuantity.innerText = "* Số lượng sản phẩm này trong giỏ đã vượt quá giới hạn tồn kho!";
                      if (addToCartSuccess) addToCartSuccess.innerText = "";
                  } else {
                      cartOvorQuantity.innerText = "";
                      addToCartSuccess.innerText = "Thêm sản phẩm thành công vào giỏ hàng!";
                      if (cartCount)
                          cartCount.innerText = response.data;
                  }
              }).catch(error => {
              if (error.response && error.response.status === 401) {
                  window.location.href = contextPath + "/login";
              }
          });
      }
  }