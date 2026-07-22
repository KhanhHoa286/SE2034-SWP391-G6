<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private String sellerToastText(Object value) {
    if (value == null) {
      return "";
    }
    return value.toString()
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }

  private Long sellerToastExpiry(Object value) {
    if (value instanceof Long) {
      return (Long) value;
    }
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    if (value != null) {
      try {
        return Long.parseLong(value.toString());
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }
%>
  <% String activeAttr=(String) request.getAttribute("activePage"); if (activeAttr==null) activeAttr="" ; if
    (activeAttr.isEmpty()) { String requestUri=request.getRequestURI(); if (requestUri.contains("sellerDashboard") ||
    requestUri.contains("view-seller-dashboard")) { activeAttr="dashboard" ; } else if (requestUri.contains("add-shop")
    || requestUri.contains("view-shop") || requestUri.contains("edit-shop")) { activeAttr="add-shop" ; } else if
    (requestUri.contains("list-seller-products")) { activeAttr="products" ; } else if
    (requestUri.contains("/seller/orders") || requestUri.contains("list-seller-orders")) { activeAttr="orders" ; } else
    if (requestUri.contains("/seller/customers") || requestUri.contains("list-customers")) { activeAttr="customers" ; }
    } boolean isDashboardActive=activeAttr.equals("dashboard"); boolean isAddShopActive=activeAttr.equals("add-shop") ||
    activeAttr.equals("view-shop") || activeAttr.equals("edit-shop"); boolean
    isProductsActive=activeAttr.equals("products"); boolean isOrdersActive=activeAttr.equals("orders"); boolean
    isCustomersActive=activeAttr.equals("customers");

    long sellerToastNow = System.currentTimeMillis();

    String pendingToastMessage = sellerToastText(session.getAttribute("sellerPendingOrderToastMessage"));
    Object pendingToastId = session.getAttribute("sellerPendingOrderToastSubOrderId");
    Long pendingToastExpiresAt = sellerToastExpiry(session.getAttribute("sellerPendingOrderToastExpiresAt"));
    boolean showPendingOrderToast = !pendingToastMessage.isEmpty() && pendingToastId != null
        && pendingToastExpiresAt != null && pendingToastExpiresAt > sellerToastNow;
    if (!showPendingOrderToast && pendingToastExpiresAt != null) {
      session.removeAttribute("sellerPendingOrderToastMessage");
      session.removeAttribute("sellerPendingOrderToastSubOrderId");
      session.removeAttribute("sellerPendingOrderToastExpiresAt");
      session.removeAttribute("sellerPendingOrderToastAnimated");
    }
    long pendingToastTtl = showPendingOrderToast ? Math.max(0L, pendingToastExpiresAt - sellerToastNow) : 0L;
    boolean animatePendingOrderToast = showPendingOrderToast
        && !Boolean.TRUE.equals(session.getAttribute("sellerPendingOrderToastAnimated"));
    if (animatePendingOrderToast) {
      session.setAttribute("sellerPendingOrderToastAnimated", true);
    }

    String assignedToastMessage = sellerToastText(session.getAttribute("sellerAssignedDeliveryToastMessage"));
    Object assignedToastId = session.getAttribute("sellerAssignedDeliveryToastSubOrderId");
    Long assignedToastExpiresAt = sellerToastExpiry(session.getAttribute("sellerAssignedDeliveryToastExpiresAt"));
    boolean showAssignedDeliveryToast = !assignedToastMessage.isEmpty() && assignedToastId != null
        && assignedToastExpiresAt != null && assignedToastExpiresAt > sellerToastNow;
    if (!showAssignedDeliveryToast && assignedToastExpiresAt != null) {
      session.removeAttribute("sellerAssignedDeliveryToastMessage");
      session.removeAttribute("sellerAssignedDeliveryToastSubOrderId");
      session.removeAttribute("sellerAssignedDeliveryToastExpiresAt");
      session.removeAttribute("sellerAssignedDeliveryToastAnimated");
    }
    long assignedToastTtl = showAssignedDeliveryToast ? Math.max(0L, assignedToastExpiresAt - sellerToastNow) : 0L;
    boolean animateAssignedDeliveryToast = showAssignedDeliveryToast
        && !Boolean.TRUE.equals(session.getAttribute("sellerAssignedDeliveryToastAnimated"));
    if (animateAssignedDeliveryToast) {
      session.setAttribute("sellerAssignedDeliveryToastAnimated", true);
    }
    %>
    <style>
      .seller-session-toast-stack {
        position: fixed;
        top: 24px;
        right: 24px;
        z-index: 2000;
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 10px;
        pointer-events: none;
      }

      .seller-session-toast {
        min-width: 300px;
        max-width: 420px;
        display: inline-flex;
        align-items: center;
        gap: 10px;
        padding: 14px 18px;
        border: 1px solid;
        font-size: 14px;
        font-weight: 800;
        line-height: 1.35;
        text-decoration: none !important;
        box-shadow: 0 18px 42px rgba(15, 23, 42, 0.16);
        pointer-events: auto;
      }

      .seller-session-toast-animate {
        animation: sellerSessionToastIn 180ms ease-out both;
      }

      .seller-session-toast:hover,
      .seller-session-toast:focus {
        transform: translateY(-1px);
        text-decoration: none !important;
      }

      .seller-session-toast svg {
        width: 18px;
        height: 18px;
        flex: 0 0 auto;
      }

      .seller-session-toast-warning,
      .seller-session-toast-warning:hover,
      .seller-session-toast-warning:focus,
      .seller-session-toast-warning:visited {
        background: #fffbeb;
        border-color: #f59e0b;
        color: #78350f !important;
      }

      .seller-session-toast-warning svg,
      .seller-session-toast-warning span {
        color: #78350f !important;
        stroke: #f59e0b !important;
      }

      .seller-session-toast-dark,
      .seller-session-toast-dark:hover,
      .seller-session-toast-dark:focus,
      .seller-session-toast-dark:visited {
        background: #000;
        border-color: #000;
        color: #fff !important;
      }

      .seller-session-toast-dark svg,
      .seller-session-toast-dark span {
        color: #fff !important;
        stroke: #fff !important;
      }

      .seller-session-toast.is-hiding {
        animation: sellerSessionToastOut 220ms ease-in forwards;
      }

      @keyframes sellerSessionToastIn {
        from {
          opacity: 0;
          transform: translateY(-8px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }

      @keyframes sellerSessionToastOut {
        to {
          opacity: 0;
          transform: translateY(-8px);
        }
      }

      @media (max-width: 768px) {
        .seller-session-toast-stack {
          left: 16px;
          right: 16px;
          align-items: stretch;
        }

        .seller-session-toast {
          min-width: 0;
          max-width: none;
        }
      }
    </style>
    <% if (showPendingOrderToast || showAssignedDeliveryToast) { %>
      <div class="seller-session-toast-stack" aria-live="polite">
        <% if (showPendingOrderToast) { %>
          <a class="seller-session-toast seller-session-toast-warning<%= animatePendingOrderToast ? " seller-session-toast-animate" : "" %>"
             href="<%= request.getContextPath() %>/seller/order/view?subOrderId=<%= pendingToastId %>"
             data-toast-ttl="<%= pendingToastTtl %>"
             aria-label="<%= pendingToastMessage %>">
            <i data-lucide="alert-triangle"></i>
            <span><%= pendingToastMessage %></span>
          </a>
        <% } %>
        <% if (showAssignedDeliveryToast) { %>
          <a class="seller-session-toast seller-session-toast-dark<%= animateAssignedDeliveryToast ? " seller-session-toast-animate" : "" %>"
             href="<%= request.getContextPath() %>/seller/order/status?subOrderId=<%= assignedToastId %>"
             data-toast-ttl="<%= assignedToastTtl %>"
             aria-label="<%= assignedToastMessage %>">
            <i data-lucide="truck"></i>
            <span><%= assignedToastMessage %></span>
          </a>
        <% } %>
      </div>
      <script>
        (function () {
          document.querySelectorAll('.seller-session-toast[data-toast-ttl]').forEach(function (toast) {
            var ttl = Number(toast.getAttribute('data-toast-ttl')) || 0;
            window.setTimeout(function () {
              toast.classList.add('is-hiding');
              window.setTimeout(function () {
                var stack = toast.closest('.seller-session-toast-stack');
                toast.remove();
                if (stack && stack.children.length === 0) {
                  stack.remove();
                }
              }, 220);
            }, Math.max(ttl, 0));
          });
        })();
      </script>
    <% } %>
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
          <li class="sidebar-item <%= isDashboardActive ? " active" : "" %>">
            <a href="${pageContext.request.contextPath}/sellerDashboard" class="sidebar-link">
              <i data-lucide="layout-dashboard"></i>
              <span>Tổng quan</span>
            </a>
          </li>

          <li class="sidebar-item <%= isAddShopActive ? " active" : "" %>">
            <a href="${pageContext.request.contextPath}/view-shop" class="sidebar-link">
              <i data-lucide="store"></i>
              <span>Quản lý cửa hàng</span>
            </a>
          </li>

          <li class="sidebar-item <%= isProductsActive ? " active" : "" %>">
            <a href="${pageContext.request.contextPath}/list-seller-products" class="sidebar-link">
              <i data-lucide="package"></i>
              <span>Quản lý sản phẩm</span>
            </a>
          </li>

          <li class="sidebar-item <%= isOrdersActive ? " active" : "" %>">
            <a href="${pageContext.request.contextPath}/seller/orders" class="sidebar-link">
              <i data-lucide="shopping-cart"></i>
              <span>Quản lý đơn hàng</span>
            </a>
          </li>

          <li class="sidebar-item <%= isCustomersActive ? " active" : "" %>">
            <a href="${pageContext.request.contextPath}/seller/customers" class="sidebar-link">
              <i data-lucide="users"></i>
              <span>Khách hàng</span>
            </a>
          </li>
        </ul>
      </div>
    </aside>
