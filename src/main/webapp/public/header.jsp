<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!-- Header -->
<header class="header">
    <div class="container header__inner">
        <a href="view-home-page.jsp" class="header__logo">MODA</a>

        <nav class="header__nav d-none d-md-block">
            <ul>
                <li><a href="view-home-page.jsp">Trang chủ</a></li>
                <li><a href="list-products.jsp">Nữ</a></li>
                <li><a href="list-products.jsp">Nam</a></li>
                <li><a href="list-products.jsp">Phụ kiện</a></li>
                <li><a href="list-products.jsp" class="sale-link">Khuyến mãi</a></li>
            </ul>
        </nav>

        <div class="header__icons">
            <div class="header__search">
                <input type="text" placeholder="Tìm kiếm..." class="header__search-input">
                <i class="fa-solid fa-magnifying-glass header__icon"></i>
            </div>
            <i class="fa-regular fa-heart header__icon"></i>
            <a href="view-cart.jsp" class="header__icon header__icon-cart" style="color: inherit; text-decoration: none;">
                <i class="fa-solid fa-bag-shopping"></i>
                <span class="cart-count">3</span>
            </a>
            <img src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=100&auto=format&fit=crop" alt="User Avatar" class="header__avatar">
        </div>
    </div>
</header>
