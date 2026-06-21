<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<aside class="profile-sidebar">
    <div class="profile-sidebar__head">
        <h2>Tài khoản của tôi</h2>
        <p>Quản lý sở thích của bạn</p>
    </div>

    <nav class="profile-sidebar__nav">

        <a class="profile-side-link ${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/profile">
            <span class="material-symbols-outlined">dashboard</span>
            <span>Bảng điều khiển</span>
        </a>

        <a class="profile-side-link ${param.active == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/profile">
            <span class="material-symbols-outlined">person</span>
            <span>Hồ sơ</span>
        </a>

        <a class="profile-side-link ${param.active == 'addresses' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/addresses">
            <span class="material-symbols-outlined">location_on</span>
            <span>Địa chỉ</span>
        </a>

        <a class="profile-side-link ${param.active == 'orders' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/order-list">
            <span class="material-symbols-outlined">shopping_cart</span>
            <span>Đơn hàng</span>
        </a>

<%--        <a class="profile-side-link ${param.active == 'wishlist' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/wishlist">--%>
<%--            <span class="material-symbols-outlined">favorite</span>--%>
<%--            <span>Danh sách yêu thích</span>--%>
<%--        </a>--%>

        <a class="profile-side-link ${param.active == 'seller' ? 'active' : ''}" href="${pageContext.request.contextPath}/seller-register">
            <span class="material-symbols-outlined">storefront</span>
            <span>Đăng ký người bán</span>
        </a>

        <div class="profile-sidebar__logout">
            <!-- Chưa có LogoutServlet thì để tạm không đi đâu để tránh 404 -->
            <a class="profile-side-link" href="javascript:void(0);">
                <span class="material-symbols-outlined">logout</span>
                <span>Đăng xuất</span>
            </a>
        </div>

    </nav>
</aside>
