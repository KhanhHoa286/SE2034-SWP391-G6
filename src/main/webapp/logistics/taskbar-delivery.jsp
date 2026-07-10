<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String activeAttr = (String) request.getAttribute("activePage");
    if (activeAttr == null) activeAttr = "";

    boolean isListActive = activeAttr.isEmpty() || activeAttr.equals("delivery-list");
    boolean isMyOrdersActive = activeAttr.equals("delivery-my-orders");
%>

<aside class="sidebar">
    <div class="sidebar-top">
        <div class="sidebar-brand-box">
            <div class="brand-logo-icon">
                <i data-lucide="truck"></i>
            </div>
            <div class="brand-logo-text">
                <h2 class="brand-title">MODA</h2>
                <span class="brand-subtitle">GIAO HÀNG</span>
            </div>
        </div>

        <ul class="sidebar-menu">
            <li class="sidebar-item <%= isListActive ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/logistics/delivery/list" class="sidebar-link">
                    <i data-lucide="clipboard-list"></i>
                    <span>Danh sách đơn</span>
                </a>
            </li>

            <li class="sidebar-item <%= isMyOrdersActive ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/logistics/delivery/my-orders" class="sidebar-link">
                    <i data-lucide="list-checks"></i>
                    <span>Đơn vận chuyển của tôi</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout" class="logout-link">
            <i data-lucide="log-out"></i>
            <span>Đăng xuất</span>
        </a>
    </div>
</aside>
