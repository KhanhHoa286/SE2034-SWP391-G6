<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Chi tiết Đơn hàng - MODA Admin</title>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        :root {
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --sidebar-bg: #111827;
            --sidebar-text: #9ca3af;
            --sidebar-text-hover: #ffffff;
            --sidebar-item-active: #5850ec;
            --sidebar-item-hover: #1f2937;
            --text-primary: #0f172a;
            --text-secondary: #475569;
            --text-muted: #64748b;
            --border-color: #e2e8f0;
            --success: #10b981;
            --success-bg: #ecfdf5;
            --success-text: #047857;
            --danger: #ef4444;
            --danger-bg: #fef2f2;
            --danger-text: #b91c1c;
            --warning: #f59e0b;
            --warning-bg: #fffbeb;
            --warning-text: #b45309;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --sidebar-w: 260px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; -webkit-font-smoothing: antialiased; }
        a { text-decoration: none; color: inherit; }
        ul { list-style: none; }

        .app-container { display: flex; min-height: 100vh; }

        /* Sidebar Styles */
        .sidebar-wrapper { width: var(--sidebar-w); background-color: var(--sidebar-bg); flex-shrink: 0; position: sticky; top: 0; height: 100vh; z-index: 100; }
        .sidebar { display: flex; flex-direction: column; height: 100%; padding: 24px 16px; }
        .sidebar-brand { padding: 12px 8px 32px 8px; }
        .sidebar-brand-name { font-size: 17px; font-weight: 700; color: #ffffff; letter-spacing: -0.01em; display: block; }
        .sidebar-subtitle { font-size: 11px; color: #4b5563; font-weight: 500; margin-top: 2px; display: block; text-transform: uppercase; letter-spacing: 0.05em; }
        .sidebar-nav { display: flex; flex-direction: column; gap: 6px; flex: 1; }
        .menu-item a { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; color: var(--sidebar-text); font-size: 14px; font-weight: 500; transition: all 0.2s; }
        .menu-item a:hover { color: var(--sidebar-text-hover); background-color: var(--sidebar-item-hover); }
        .menu-item.active a { color: #ffffff; background-color: var(--sidebar-item-active); box-shadow: 0 4px 12px rgba(88, 80, 236, 0.25); }
        .menu-icon { width: 20px; height: 20px; stroke-width: 2px; flex-shrink: 0; }

        /* Main Content */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }

        .topbar { display: flex; align-items: center; justify-content: flex-end; gap: 16px; padding-bottom: 8px; }
        .topbar-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-color); box-shadow: var(--shadow-sm); }

        /* Page Header */
        .page-header { display: flex; justify-content: space-between; align-items: center; }
        .header-info h1 { font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.02em; margin-bottom: 4px; }
        .header-info p { font-size: 14px; color: var(--text-muted); }
        .btn-back { background-color: #ffffff; border: 1px solid var(--border-color); color: var(--text-primary); padding: 10px 16px; border-radius: 8px; font-weight: 500; display: inline-flex; align-items: center; gap: 6px; cursor: pointer; transition: all 0.2s; }
        .btn-back:hover { background-color: #f8fafc; }

        /* Details Layout */
        .detail-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 24px; }
        .card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; box-shadow: var(--shadow-sm); padding: 24px; }
        .card-title { font-size: 16px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
        .card-icon { color: var(--text-muted); }
        
        .info-list { display: flex; flex-direction: column; gap: 16px; }
        .info-item { display: flex; justify-content: space-between; font-size: 14px; }
        .info-label { color: var(--text-muted); font-weight: 500; }
        .info-value { color: var(--text-primary); font-weight: 600; text-align: right; max-width: 60%; word-break: break-word; }
        
        .badge { display: inline-flex; align-items: center; padding: 4px 10px; border-radius: 9999px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.02em; }
        .badge.status-success { background-color: var(--success-bg); color: var(--success-text); }
        .badge.status-delivering { background-color: #eff6ff; color: #1d4ed8; }
        .badge.status-canceled { background-color: var(--danger-bg); color: var(--danger-text); }
        .badge.status-pending { background-color: var(--warning-bg); color: var(--warning-text); }

        .sub-order-card { background-color: #ffffff; border: 1px solid var(--border-color); border-radius: 8px; overflow: hidden; margin-bottom: 20px; }
        .sub-order-header { padding: 16px 20px; background-color: #f8fafc; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; }
        .shop-info { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 15px; color: var(--text-primary); }
        
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { color: var(--text-secondary); font-weight: 600; padding: 12px 20px; border-bottom: 1px solid var(--border-color); font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; }
        .custom-table td { padding: 16px 20px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); vertical-align: middle; }
        .custom-table tr:last-child td { border-bottom: none; }
        
        .product-cell { display: flex; align-items: center; gap: 12px; }
        .product-img { width: 48px; height: 48px; border-radius: 6px; object-fit: cover; border: 1px solid var(--border-color); }
        .product-name { font-weight: 500; color: var(--text-primary); margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
        .product-variant { font-size: 12px; color: var(--text-muted); }

        .sub-order-footer { padding: 16px 20px; background-color: #f8fafc; border-top: 1px solid var(--border-color); text-align: right; }
        .sub-order-total { font-size: 15px; font-weight: 600; color: var(--text-primary); }
        .sub-order-total span { font-size: 18px; color: var(--danger); margin-left: 8px; }

        @media (max-width: 1024px) { .detail-grid { grid-template-columns: 1fr; } }
        @media (max-width: 768px) { .app-container { flex-direction: column; } .sidebar-wrapper { width: 100%; height: auto; } .main-content { padding: 16px; } }
    </style>

    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="app-container">
    <aside class="sidebar-wrapper">
        <div class="sidebar">
            <div class="sidebar-brand" style="padding-bottom: 24px;">
                <span class="sidebar-brand-name" style="font-size: 1.25rem; font-weight: 700; color: #ffffff;">MODA Admin</span>
                <span class="sidebar-subtitle" style="display: block; font-size: 0.75rem; color: #9ca3af; margin-top: 4px;">Bảng điều khiển siêu cấp</span>
            </div>
            <ul class="sidebar-nav">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/dashboard/overview">
                        <i data-lucide="layout-dashboard" class="menu-icon"></i>
                        <span>Tổng quan</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/user-management">
                        <i data-lucide="users" class="menu-icon"></i>
                        <span>Người dùng</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/seller-applications">
                        <i data-lucide="store" class="menu-icon"></i>
                        <span>Người bán</span>
                    </a>
                </li>
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/admin/orders">
                        <i data-lucide="shopping-cart" class="menu-icon"></i>
                        <span>Đơn hàng hệ thống</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/products">
                        <i data-lucide="package" class="menu-icon"></i>
                        <span>Danh sách sản phẩm</span>
                    </a>
                </li>
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/admin/finance/view-finance.jsp">
                        <i data-lucide="credit-card" class="menu-icon"></i>
                        <span>Tài chính</span>
                    </a>
                </li>
            </ul>
            <div style="margin-top: auto;">
                <ul class="sidebar-nav">
                    <li class="menu-item">
                        <a href="${pageContext.request.contextPath}/logout">
                            <i data-lucide="log-out" class="menu-icon"></i>
                            <span>Đăng xuất</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </aside>

    <main class="main-content">
        <div class="topbar">
            <div class="topbar-actions">
                <img src="https://res.cloudinary.com/dej5mxdrt/image/upload/v1780061324/OIP_dbbjuo.jpg" alt="Avatar" class="topbar-avatar" />
            </div>
        </div>

        <section class="page-header">
            <div class="header-info">
                <h1>Chi tiết Đơn hàng: #ĐH-${orderDetail.masterOrder.masterOrderId}</h1>
                <p>Đặt lúc ${formattedOrderDate}</p>
            </div>
            <button onclick="history.back()" class="btn-back">
                <i data-lucide="arrow-left" style="width:16px;height:16px;"></i>
                Quay lại danh sách
            </button>
        </section>

        <div class="detail-grid">
            <div class="left-col">
                <c:choose>
                    <c:when test="${not empty orderDetail.subOrders}">
                        <c:forEach var="sub" items="${orderDetail.subOrders}">
                            <div class="sub-order-card">
                                <div class="sub-order-header">
                                    <div class="shop-info">
                                        <i data-lucide="store" style="width:18px;height:18px;color:var(--text-muted);"></i>
                                        <span>Shop: <c:out value="${sub.shop.shopName}"/></span>
                                    </div>
                                    <c:choose>
                                        <c:when test="${sub.subOrder.status.name() == 'DELIVERED'}">
                                            <span class="badge status-success">Thành công</span>
                                        </c:when>
                                        <c:when test="${sub.subOrder.status.name() == 'SHIPPING'}">
                                            <span class="badge status-delivering">Đang giao</span>
                                        </c:when>
                                        <c:when test="${sub.subOrder.status.name() == 'CANCELLED'}">
                                            <span class="badge status-canceled">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge status-pending">Chờ xử lý</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="table-responsive">
                                    <table class="custom-table">
                                        <thead>
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <th>Đơn giá</th>
                                                <th>SL</th>
                                                <th style="text-align: right;">Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${sub.items}">
                                                <tr>
                                                    <td>
                                                        <div class="product-cell">
                                                            <img src="${item.product.thumbnailUrl != null ? item.product.thumbnailUrl : 'https://placehold.co/100x100?text=No+Image'}" alt="Product" class="product-img">
                                                            <div>
                                                                <div class="product-name">${item.product.productName}</div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td><fmt:formatNumber value="${item.orderItem.priceAtPurchase}" type="number" maxFractionDigits="0"/> VND</td>
                                                    <td>${item.orderItem.quantity}</td>
                                                    <td style="text-align: right; font-weight: 600;">
                                                        <fmt:formatNumber value="${item.subTotal}" type="number" maxFractionDigits="0"/> VND
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="sub-order-footer">
                                    <div class="sub-order-total">
                                        Tổng phụ: <span><fmt:formatNumber value="${sub.subOrder.totalAmount}" type="number" maxFractionDigits="0"/> VND</span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="card" style="text-align: center; padding: 40px; color: var(--text-muted);">
                            <i data-lucide="package-x" style="width: 48px; height: 48px; margin-bottom: 16px; opacity: 0.5;"></i>
                            <p>Không tìm thấy sản phẩm nào trong đơn hàng này.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="right-col">
                <div class="card" style="margin-bottom: 24px;">
                    <div class="card-title">
                        <i data-lucide="user" class="card-icon" style="width:20px;height:20px;"></i>
                        Thông tin khách hàng
                    </div>
                    <div class="info-list">
                        <div class="info-item">
                            <span class="info-label">Khách hàng</span>
                            <span class="info-value">${orderDetail.customerName}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Số điện thoại</span>
                            <span class="info-value">${orderDetail.customerPhone}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Email</span>
                            <span class="info-value">${orderDetail.customerEmail}</span>
                        </div>
                        <div class="info-item" style="flex-direction: column; gap: 8px;">
                            <span class="info-label">Địa chỉ giao hàng</span>
                            <span class="info-value" style="max-width: 100%; text-align: left; font-weight: 500; line-height: 1.5;">
                                ${orderDetail.masterOrder.shippingAddress}
                            </span>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-title">
                        <i data-lucide="credit-card" class="card-icon" style="width:20px;height:20px;"></i>
                        Thông tin thanh toán
                    </div>
                    <div class="info-list">
                        <div class="info-item">
                            <span class="info-label">Phương thức</span>
                            <span class="info-value" style="text-transform: uppercase;">${orderDetail.masterOrder.paymentMethod}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Trạng thái TT</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${orderDetail.masterOrder.paymentStatus.name() == 'PAID'}">
                                        <span style="color: var(--success-text);">Đã thanh toán</span>
                                    </c:when>
                                    <c:when test="${orderDetail.masterOrder.paymentStatus.name() == 'REFUNDED'}">
                                        <span style="color: var(--danger-text);">Đã hoàn tiền</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: var(--warning-text);">Chưa thanh toán</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div style="border-top: 1px dashed var(--border-color); margin: 8px 0;"></div>
                        <div class="info-item" style="font-size: 16px;">
                            <span class="info-label" style="color: var(--text-primary); font-weight: 600;">Tổng cộng</span>
                            <span class="info-value" style="color: var(--danger); font-size: 20px;">
                                <fmt:formatNumber value="${orderDetail.masterOrder.totalAmount}" type="number" maxFractionDigits="0"/> VND
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </main>
</div>

<script>
    lucide.createIcons();
</script>

</body>
</html>
