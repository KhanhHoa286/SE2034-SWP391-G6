<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  // Nhận diện trang đang hoạt động qua attribute "activePage" được Servlet đặt
  String activeAttr = (String) request.getAttribute("activePage");
  if (activeAttr == null) activeAttr = "";

  // Nếu không có attribute, fallback sang tự detect qua URI
  if (activeAttr.isEmpty()) {
    String requestUri = request.getRequestURI();
    if (requestUri.contains("sellerDashboard") || requestUri.contains("view-seller-dashboard")) {
      activeAttr = "dashboard";
    } else if (requestUri.contains("add-shop") || requestUri.contains("view-shop") || requestUri.contains("edit-shop")) {
      activeAttr = "add-shop";
    }
  }

  boolean isDashboardActive = activeAttr.equals("dashboard");
  boolean isAddShopActive   = activeAttr.equals("add-shop") || activeAttr.equals("view-shop") || activeAttr.equals("edit-shop");
%>
<!-- Sidebar (Thanh tác vụ Seller Center) -->
<aside class="sidebar">
  <div class="sidebar-top">
    <!-- Thương hiệu Seller Center -->
    <div class="sidebar-brand-box">
      <div class="brand-logo-icon">
        <i data-lucide="store"></i>
      </div>
      <div class="brand-logo-text">
        <h2 class="brand-title">MODA</h2>
        <span class="brand-subtitle">QUẢN TRỊ HỆ THỐNG</span>
      </div>
    </div>

    <!-- Menu Chức năng -->
    <ul class="sidebar-menu">

      <!-- Tổng quan (demo: link vô hiệu hóa) -->
      <!-- Tổng quan -->
      <li class="sidebar-item <%= isDashboardActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/sellerDashboard" class="sidebar-link">
          <i data-lucide="layout-dashboard"></i>
          <span>Tổng quan</span>
        </a>
      </li>

      <!-- Quản lý cửa hàng (demo: link vô hiệu hóa) -->
      <!-- Quản lý cửa hàng -->
      <li class="sidebar-item <%= isAddShopActive ? "active" : "" %>">
        <a href="${pageContext.request.contextPath}/view-shop" class="sidebar-link">
          <i data-lucide="store"></i>
          <span>Quản lý cửa hàng</span>
        </a>
      </li>

      <!-- Quản lý sản phẩm -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="package"></i>
          <span>Quản lý sản phẩm</span>
        </a>
      </li>

      <!-- Quản lý đơn hàng -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="shopping-cart"></i>
          <span>Quản lý đơn hàng</span>
        </a>
      </li>

      <!-- Khách hàng -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="users"></i>
          <span>Khách hàng</span>
        </a>
      </li>

      <!-- Khuyến mãi -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="ticket"></i>
          <span>Khuyến mãi</span>
        </a>
      </li>

      <!-- Tài chính -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="wallet"></i>
          <span>Tài chính</span>
        </a>
      </li>

      <!-- Giao hàng -->
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="truck"></i>
          <span>Giao hàng</span>
        </a>
      </li>
    </ul>
  </div>
</aside>