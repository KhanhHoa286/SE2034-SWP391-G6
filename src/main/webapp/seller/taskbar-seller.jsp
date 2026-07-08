<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String activeAttr = (String) request.getAttribute("activePage");
  if (activeAttr == null) activeAttr = "";

  if (activeAttr.isEmpty()) {
    String requestUri = request.getRequestURI();
    if (requestUri.contains("sellerDashboard") || requestUri.contains("view-seller-dashboard")) {
      activeAttr = "dashboard";
    } else if (requestUri.contains("add-shop") || requestUri.contains("view-shop") || requestUri.contains("edit-shop")) {
      activeAttr = "add-shop";
    } else if (requestUri.contains("list-seller-products")) {
      activeAttr = "products";
    } else if (requestUri.contains("/seller/orders") || requestUri.contains("list-seller-orders")) {
      activeAttr = "orders";
    } else if (requestUri.contains("/seller/customers") || requestUri.contains("list-customers")) {
      activeAttr = "customers";
    } else if (requestUri.contains("edit-shipping-settings")) {
      activeAttr = "shipping-settings";
    }
  }

  boolean isDashboardActive = activeAttr.equals("dashboard");
  boolean isAddShopActive = activeAttr.equals("add-shop") || activeAttr.equals("view-shop") || activeAttr.equals("edit-shop");
  boolean isProductsActive = activeAttr.equals("products");
  boolean isOrdersActive = activeAttr.equals("orders");
  boolean isCustomersActive = activeAttr.equals("customers");
  boolean isVouchersActive = activeAttr.equals("vouchers");
  boolean isShippingSettingsActive = activeAttr.equals("shipping-settings");
%>
<aside class="sidebar">
  <div class="sidebar-top">
    <div class="sidebar-brand-box">
      <div class="brand-logo-icon">
        <i data-lucide="store"></i>
      </div>
      <div class="brand-logo-text">
        <a class="brand-title" href="${pageContext.request.contextPath}/home">MODA</a>
        <span class="brand-subtitle">QUẢN TRỊ HỆ THỐNG</span>
      </div>
    </div>

    <ul class="sidebar-menu">
      <li class="sidebar-item <%= isDashboardActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/sellerDashboard" class="sidebar-link">
          <i data-lucide="layout-dashboard"></i>
          <span>Tổng quan</span>
        </a>
      </li>

      <li class="sidebar-item <%= isAddShopActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/view-shop" class="sidebar-link">
          <i data-lucide="store"></i>
          <span>Quản lý cửa hàng</span>
        </a>
      </li>

      <li class="sidebar-item <%= isProductsActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/list-seller-products" class="sidebar-link">
          <i data-lucide="package"></i>
          <span>Quản lý sản phẩm</span>
        </a>
      </li>

      <li class="sidebar-item <%= isOrdersActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/seller/orders" class="sidebar-link">
          <i data-lucide="shopping-cart"></i>
          <span>Quản lý đơn hàng</span>
        </a>
      </li>

      <li class="sidebar-item <%= isCustomersActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/seller/customers" class="sidebar-link">
          <i data-lucide="users"></i>
          <span>Khách hàng</span>
        </a>
      </li>

      <li class="sidebar-item <%= isVouchersActive ? "active" : "" %>">
        <a href="#" class="sidebar-link">
          <i data-lucide="ticket"></i>
          <span>Khuyến mãi</span>
        </a>
      </li>

      <li class="sidebar-item <%= isShippingSettingsActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/edit-shipping-settings" class="sidebar-link">
          <i data-lucide="settings"></i>
          <span>Cài đặt giao hàng</span>
        </a>
      </li>
    </ul>
  </div>
</aside>
