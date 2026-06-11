<%
    String contextPath = request.getContextPath();
    String activeAttr = (String) request.getAttribute("activePage");
    String requestUri = request.getRequestURI();
    String sidebarClass = (String) request.getAttribute("sellerSidebarClass");

    if (activeAttr == null) {
        activeAttr = "";
    }
    if (sidebarClass == null || sidebarClass.trim().isEmpty()) {
        sidebarClass = "sidebar";
    }

    if (activeAttr.isEmpty()) {
        if (requestUri.contains("sellerDashboard") || requestUri.contains("view-seller-dashboard")) {
            activeAttr = "dashboard";
        } else if (requestUri.contains("/seller/shop/")
                || requestUri.contains("add-shop")
                || requestUri.contains("view-shop")
                || requestUri.contains("edit-shop")
                || requestUri.contains("/seller/config/")) {
            activeAttr = "shop";
        } else if (requestUri.contains("/seller/product/")) {
            activeAttr = "products";
        } else if (requestUri.contains("/seller/order/")) {
            activeAttr = "orders";
        } else if (requestUri.contains("/seller/customer_mgt/")) {
            activeAttr = "customers";
        } else if (requestUri.contains("/seller/voucher/")) {
            activeAttr = "vouchers";
        } else if (requestUri.contains("/seller/finance/")) {
            activeAttr = "finance";
        } else if (requestUri.contains("/logistics/delivery/")) {
            activeAttr = "delivery";
        }
    }

    boolean isDashboardActive = "dashboard".equals(activeAttr);
    boolean isShopActive = "shop".equals(activeAttr);
    boolean isProductsActive = "products".equals(activeAttr);
    boolean isOrdersActive = "orders".equals(activeAttr);
    boolean isCustomersActive = "customers".equals(activeAttr);
    boolean isVouchersActive = "vouchers".equals(activeAttr);
    boolean isFinanceActive = "finance".equals(activeAttr);
    boolean isDeliveryActive = "delivery".equals(activeAttr);
%>

<aside class="<%= sidebarClass %> seller-taskbar">
    <div class="sidebar-top">
        <div class="sidebar-brand-box">
            <div class="brand-logo-icon">
                <i data-lucide="store"></i>
            </div>
            <div class="brand-logo-text">
                <h2 class="brand-title">MODA</h2>
                <span class="brand-subtitle">SELLER CENTER</span>
            </div>
        </div>

        <ul class="sidebar-menu">
            <li class="sidebar-item <%= isDashboardActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/sellerDashboard">
                    <i data-lucide="layout-grid"></i>
                    <span>T&#7893;ng quan</span>
                </a>
            </li>

            <li class="sidebar-item <%= isShopActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/view-shop">
                    <i data-lucide="store"></i>
                    <span>Qu&#7843;n l&#253; c&#7917;a h&#224;ng</span>
                </a>
            </li>

            <li class="sidebar-item <%= isProductsActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/seller/product/list-seller-products.jsp">
                    <i data-lucide="package"></i>
                    <span>Qu&#7843;n l&#253; s&#7843;n ph&#7849;m</span>
                </a>
            </li>

            <li class="sidebar-item <%= isOrdersActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/seller/order/list-seller-orders.jsp">
                    <i data-lucide="shopping-cart"></i>
                    <span>Qu&#7843;n l&#253; &#273;&#417;n h&#224;ng</span>
                </a>
            </li>

            <li class="sidebar-item <%= isCustomersActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/seller/customer_mgt/list-customers.jsp">
                    <i data-lucide="users"></i>
                    <span>Kh&#225;ch h&#224;ng</span>
                </a>
            </li>

            <li class="sidebar-item <%= isVouchersActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/seller/voucher/list-seller-voucher.jsp">
                    <i data-lucide="badge-percent"></i>
                    <span>Khuy&#7871;n m&#227;i</span>
                </a>
            </li>

            <li class="sidebar-item <%= isFinanceActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/seller/finance/view-wallet">
                    <i data-lucide="wallet-cards"></i>
                    <span>T&#224;i ch&#237;nh</span>
                </a>
            </li>

            <li class="sidebar-item <%= isDeliveryActive ? "active" : "" %>">
                <a class="sidebar-link"
                   href="<%= contextPath %>/logistics/delivery/list-deliveries.jsp">
                    <i data-lucide="truck"></i>
                    <span>Giao h&#224;ng</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="sidebar-footer">
        <a href="<%= contextPath %>/public/auth/login.jsp" class="logout-link">
            <i data-lucide="log-out"></i>
            <span>&#272;&#259;ng xu&#7845;t</span>
        </a>
    </div>
</aside>
