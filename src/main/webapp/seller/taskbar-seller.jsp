<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  // Hệ thống tự động nhận diện trang đang chạy để active menu tương ứng
  String requestUri = request.getRequestURI();
  String pageName = requestUri.substring(requestUri.lastIndexOf("/") + 1);

  // Fallback qua thuộc tính activePage nếu nhúng động hoặc chuyển tiếp Servlet
  String activeAttr = (String) request.getAttribute("activePage");
  if (activeAttr == null) {
    activeAttr = "";
  }

  boolean isDashboardActive = pageName.equals("dashboard.jsp") || pageName.equals("") || activeAttr.equals("dashboard");
  boolean isAddShopActive = pageName.equals("add-shop.jsp") || activeAttr.equals("add-shop");
%>
<!-- Sidebar (Thanh tác vụ Seller Center) -->
<aside class="sidebar">
  <div class="sidebar-top">
    <!-- Thương hiệu Seller Center mới (Mockup 2) -->
    <div class="sidebar-brand-box">
      <div class="brand-logo-icon">
        <!-- Icon biểu trưng cho Cửa hàng tương tự mockup -->
        <i data-lucide="store"></i>
      </div>
      <div class="brand-logo-text">
        <h2 class="brand-title">MODA</h2>
        <span class="brand-subtitle">QUẢN TRỊ HỆ THỐNG</span>
      </div>
    </div>

    <!-- Menu Chức năng -->
    <ul class="sidebar-menu">
      <li class="sidebar-item <%= isDashboardActive ? "active" : "" %>">
        <a href="dashboard.jsp" class="sidebar-link">
          <i data-lucide="layout-dashboard"></i>
          <span>Tổng quan</span>
        </a>
      </li>
      <li class="sidebar-item <%= isAddShopActive ? "active" : "" %>">
        <a href="add-shop.jsp" class="sidebar-link">
          <i data-lucide="store"></i>
          <span>Quản lý cửa hàng</span>
        </a>
      </li>
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="package"></i>
          <span>Quản lý sản phẩm</span>
        </a>
      </li>
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="shopping-cart"></i>
          <span>Quản lý đơn hàng</span>
        </a>
      </li>
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="ticket"></i>
          <span>Khuyến mãi & Tài chính</span>
        </a>
      </li>
      <li class="sidebar-item">
        <a href="#" class="sidebar-link">
          <i data-lucide="truck"></i>
          <span>Giao hàng</span>
        </a>
      </li>
    </ul>
  </div>

  <!-- Nút Đăng xuất ở dưới cùng -->
  <div class="sidebar-footer">
    <a href="#" class="logout-link">
      <i data-lucide="log-out"></i>
      <span>Đăng xuất</span>
    </a>
  </div>
</aside>
