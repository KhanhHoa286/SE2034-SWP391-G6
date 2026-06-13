/**
 * HoaNK - HE195013
 * Date:
 * Description: Viết ajax hỗ trợ  thêm xóa sản phẩm yêu thích
 */
function toggleWishlist(productId, contextPath) {
    //lấy hình trái tim
    const wishlistHeart = document.getElementById("wishlist-heart-"+productId);
    const wishlistCount = document.getElementById("wishlist-count");
    // ajax
    axios.post(contextPath + "/api/customer/toggle-wishlist", null, {
        params: {
            product_id: productId
        }
    })
        .then(response => {
            if(wishlistCount) {
                let currentCount = parseInt(wishlistCount.innerText) || 0;
                // nếu là inserted thì thêm active còn khác tức là deleted thì xóa
                if (response.data == 'INSERTED') {
                    wishlistCount.innerText = currentCount + 1; // nếu thêm mới + 1
                } else if (response.data === "DELETED") {
                    wishlistCount.innerText = currentCount > 0 ? currentCount - 1 : 0; // nếu trừ đi thì check nhỏ nhất là 0
                } else if (response.data === "UNAUTHORIZED") { // chưa đang nhập thì ko cho thêm
                    alert("Vui lòng đăng nhập để thêm sản phẩm vào danh sách yêu thích!");
                    window.location.href = contextPath + "/login";
                }
            }
            wishlistHeart.classList.toggle("active", response.data === "INSERTED");
        })
        .catch(error => {
            console.error("Lỗi hệ thống khi toggle wishlist:", error);
        });
}