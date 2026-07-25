<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kiểm duyệt Sản phẩm - MODA Admin</title>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        :root {
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --sidebar-bg: #111827;
            --sidebar-text: #9ca3af;
            --sidebar-text-hover: #ffffff;
            --sidebar-item-active: #1f2937;
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
            --accent-purple: #5850ec;
            --font-main: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
            --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            --sidebar-w: 260px;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: var(--font-main); background-color: var(--bg-primary); color: var(--text-primary); line-height: 1.5; }
        a { text-decoration: none; color: inherit; }

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
        .menu-item.active a { color: #ffffff; background-color: var(--sidebar-item-active); box-shadow: 0 4px 12px rgba(88, 80, 236, 0.15); border-left: 3px solid var(--accent-purple); border-radius: 0 8px 8px 0; }
        .menu-icon { width: 20px; height: 20px; stroke-width: 2px; flex-shrink: 0; }

        /* Main Content */
        .main-content { flex: 1; padding: 24px 32px; display: flex; flex-direction: column; gap: 24px; overflow-x: hidden; }

        .topbar { display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid var(--border-color); padding-bottom: 16px; }
        .page-title-area { display: flex; align-items: center; gap: 12px; }
        .btn-back { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; border-radius: 50%; border: 1px solid var(--border-color); background-color: #ffffff; cursor: pointer; transition: all 0.2s; }
        .btn-back:hover { background-color: #f1f5f9; }
        .page-title { font-size: 20px; font-weight: 700; color: var(--text-primary); }

        .layout-grid { display: grid; grid-template-columns: 1fr 340px; gap: 24px; }
        .left-column { display: flex; flex-direction: column; gap: 24px; }
        .right-column { display: flex; flex-direction: column; gap: 24px; }

        .card { background-color: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 12px; padding: 24px; box-shadow: var(--shadow-sm); }

        /* Product Detail Layout */
        .product-detail-card { display: grid; grid-template-columns: 240px 1fr; gap: 24px; }
        .product-img-wrapper { border-radius: 8px; overflow: hidden; border: 1px solid var(--border-color); height: 320px; background-color: #f1f5f9; }
        .product-img { width: 100%; height: 100%; object-fit: cover; }
        .product-info-wrapper { display: flex; flex-direction: column; gap: 16px; }
        
        .code-price-row { display: flex; justify-content: space-between; align-items: flex-start; }
        .product-code-badge { font-size: 11px; font-weight: 700; color: var(--accent-purple); background-color: #eef2ff; padding: 4px 8px; border-radius: 4px; text-transform: uppercase; }
        .product-price { font-size: 24px; font-weight: 700; color: #5850ec; }
        
        .product-name-title { font-size: 22px; font-weight: 700; color: var(--text-primary); line-height: 1.3; }
        .product-cat { font-size: 13px; color: var(--text-muted); font-weight: 500; }
        .product-cat strong { color: var(--text-primary); }

        .section-divider { height: 1px; background-color: var(--border-color); margin: 4px 0; }
        
        .desc-title { font-size: 13px; font-weight: 700; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.05em; margin-bottom: 8px; }
        .desc-text { font-size: 14px; color: var(--text-secondary); line-height: 1.6; }
        
        .spec-list { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: var(--text-secondary); margin-top: 8px; list-style-type: disc; padding-left: 18px; }

        /* Shop Owner Card */
        .shop-card { background-color: #f8fafc; border: 1px solid var(--border-color); border-radius: 8px; padding: 16px; display: flex; align-items: center; justify-content: space-between; }
        .shop-info { display: flex; align-items: center; gap: 12px; }
        .shop-icon { width: 36px; height: 36px; border-radius: 6px; background-color: var(--sidebar-bg); color: #ffffff; display: flex; align-items: center; justify-content: center; }
        .shop-name-lbl { font-size: 11px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; }
        .shop-name-val { font-size: 14px; font-weight: 700; color: var(--text-primary); }
        .btn-view-shop { font-size: 13px; font-weight: 600; color: var(--accent-purple); }

        /* Lịch sử kiểm duyệt */
        .log-table-wrapper { overflow-x: auto; margin-top: 12px; }
        .log-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13px; }
        .log-table th { color: var(--text-muted); font-weight: 600; padding: 10px 16px; border-bottom: 1px solid var(--border-color); text-transform: uppercase; font-size: 11px; letter-spacing: 0.05em; background-color: #f8fafc; }
        .log-table td { padding: 12px 16px; border-bottom: 1px solid var(--border-color); vertical-align: middle; }
        .log-table tr:last-child td { border-bottom: none; }
        
        .badge-action { display: inline-flex; align-items: center; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: 700; text-transform: uppercase; }
        .badge-action.active, .badge-action.approved { background-color: #e6fcf5; color: #0ca678; }
        .badge-action.pending { background-color: #fff9db; color: #f08c00; }
        .badge-action.rejected, .badge-action.banned { background-color: #fff5f5; color: #e03131; }

        /* Right Column Styles */
        .status-header-box { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
        .status-lbl { font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; }
        .status-val-badge { display: inline-flex; align-items: center; padding: 4px 8px; border-radius: 6px; font-size: 11px; font-weight: 700; text-transform: uppercase; }
        .status-val-badge.pending { background-color: #fff9db; color: #f08c00; }
        .status-val-badge.active { background-color: #e6fcf5; color: #0ca678; }
        .status-val-badge.banned { background-color: #fff5f5; color: #e03131; }

        .textarea-note { width: 100%; height: 110px; border-radius: 8px; border: 1px solid var(--border-color); padding: 12px; font-family: var(--font-main); font-size: 13px; color: var(--text-primary); resize: none; margin-top: 8px; margin-bottom: 16px; outline: none; }
        .textarea-note:focus { border-color: var(--accent-purple); }

        .btn-submit-approve { display: flex; align-items: center; justify-content: center; gap: 8px; width: 100%; padding: 12px; border-radius: 8px; border: 1px solid #111827; background-color: #111827; color: #ffffff; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; margin-bottom: 12px; }
        .btn-submit-approve:hover { background-color: #1f2937; }

        .secondary-btn-group { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 20px; }
        .btn-secondary { display: flex; align-items: center; justify-content: center; gap: 6px; padding: 10px; border-radius: 8px; border: 1px solid var(--border-color); background-color: #ffffff; color: var(--text-primary); font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
        .btn-secondary:hover { background-color: #f1f5f9; }
        .btn-secondary.reject-btn { border-color: var(--danger); color: var(--danger); }
        .btn-secondary.reject-btn:hover { background-color: var(--danger-bg); }

        .switch-row { display: flex; justify-content: space-between; align-items: center; padding-top: 16px; border-top: 1px solid var(--border-color); margin-bottom: 16px; }
        .switch-info h4 { font-size: 14px; font-weight: 700; color: var(--text-primary); }
        .switch-info p { font-size: 12px; color: var(--text-muted); }
        
        /* Switch design */
        .switch { position: relative; display: inline-block; width: 44px; height: 24px; }
        .switch input { opacity: 0; width: 0; height: 0; }
        .slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #cbd5e1; transition: .3s; border-radius: 24px; }
        .slider:before { position: absolute; content: ""; height: 18px; width: 18px; left: 3px; bottom: 3px; background-color: white; transition: .3s; border-radius: 50%; }
        input:checked + .slider { background-color: var(--accent-purple); }
        input:checked + .slider:before { transform: translateX(20px); }

        .policy-box { background-color: #f8fafc; border: 1px dashed var(--border-color); border-radius: 8px; padding: 12px; font-size: 12px; color: var(--text-muted); line-height: 1.5; display: flex; gap: 8px; }

        /* Stats Box */
        .stat-details-list { display: flex; flex-direction: column; gap: 12px; font-size: 14px; }
        .stat-item { display: flex; justify-content: space-between; }
        .stat-lbl { color: var(--text-muted); font-weight: 500; }
        .stat-val { color: var(--text-primary); font-weight: 600; }
        .stat-val.blue-link { color: var(--accent-purple); cursor: pointer; }
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
                    <a href="${pageContext.request.contextPath}/admin/seller-management">
                        <i data-lucide="shopping-bag" class="menu-icon"></i>
                        <span>Người bán</span>
                    </a>
                </li>
                <li class="menu-item active">
                    <a href="${pageContext.request.contextPath}/admin/seller-applications">
                        <i data-lucide="store" class="menu-icon"></i>
                        <span>Duyệt đăng ký</span>
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
        <header class="topbar">
            <div class="page-title-area">
                <button onclick="window.history.back()" class="btn-back" title="Quay lại">
                    <i data-lucide="arrow-left" style="width:16px;height:16px;"></i>
                </button>
                <h1 class="page-title">Kiểm duyệt Sản phẩm</h1>
            </div>
        </header>

        <form action="${pageContext.request.contextPath}/admin/product/edit-status" method="POST" id="reviewForm">
            <input type="hidden" name="productId" value="${product.productId}">
            <input type="hidden" name="action" id="formAction" value="approve">

            <div class="layout-grid">
                <div class="left-column">
                    <section class="card product-detail-card">
                        <div class="product-img-wrapper">
                            <c:choose>
                                <c:when test="${not empty images}">
                                    <img src="${images[0].imageUrl}" alt="${product.productName}" class="product-img">
                                </c:when>
                                <c:otherwise>
                                    <div class="product-img" style="display:flex;align-items:center;justify-content:center;color:var(--text-muted);">
                                        <i data-lucide="image" style="width:48px;height:48px;"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="product-info-wrapper">
                            <div class="code-price-row">
                                <span class="product-code-badge">Mã SP: <c:out value="${product.productCode}"/></span>
                                <span class="product-price">
                                    <fmt:formatNumber value="${product.basePrice}" type="number"/> đ
                                </span>
                            </div>

                            <h2 class="product-name-title"><c:out value="${product.productName}"/></h2>
                            
                            <span class="product-cat">
                                Danh mục: <strong><c:out value="${categoryName != null ? categoryName : 'Thời trang'}"/></strong>
                            </span>

                            <div class="section-divider"></div>

                            <h3 class="desc-title">Mô tả sản phẩm</h3>
                            <p class="desc-text"><c:out value="${product.description}"/></p>

                            <div class="section-divider"></div>

                            <h3 class="desc-title">Thông tin cửa hàng</h3>
                            <div class="shop-card">
                                <div class="shop-info">
                                    <div class="shop-icon">
                                        <i data-lucide="store" style="width:18px;height:18px;"></i>
                                    </div>
                                    <div>
                                        <span class="shop-name-lbl">Cửa hàng</span>
                                        <h4 class="shop-name-val"><c:out value="${shopName != null ? shopName : 'MODA Partner'}"/></h4>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/admin/seller-applications/view?id=${product.sellerId}" class="btn-view-shop">
                                    Chi tiết Shop →
                                </a>
                            </div>
                        </div>
                    </section>

                    <section class="card">
                        <h3 class="desc-title" style="margin-bottom:12px;">Lịch sử kiểm duyệt sản phẩm</h3>
                        <div class="log-table-wrapper">
                            <table class="log-table">
                                <thead>
                                <tr>
                                    <th>Thời gian</th>
                                    <th>Người thực hiện</th>
                                    <th>Hành động</th>
                                    <th>Chi tiết / Lý do</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="log" items="${logs}">
                                    <tr>
                                        <td><c:out value="${log.createdAt}"/></td>
                                        <td style="font-weight:600;"><c:out value="${log.actorName}"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.action == 'ACTIVE' || log.action == 'APPROVED'}">
                                                    <span class="badge-action approved">Đã duyệt</span>
                                                </c:when>
                                                <c:when test="${log.action == 'PENDING'}">
                                                    <span class="badge-action pending">Tạo mới</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-action rejected">Từ chối</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${log.note}"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>

                <div class="right-column">
                    <section class="card" style="padding:20px;">
                        <div class="status-header-box">
                            <span class="status-lbl">Trạng thái hiện tại</span>
                            <c:choose>
                                <c:when test="${product.status == 'ACTIVE'}">
                                    <span class="status-val-badge active">Đang bán</span>
                                </c:when>
                                <c:when test="${product.status == 'PENDING'}">
                                    <span class="status-val-badge pending">Chờ duyệt</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-val-badge banned">Từ chối</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <label class="status-lbl" style="display:block;">Ghi chú kiểm duyệt</label>
                        <textarea class="textarea-note" name="note" placeholder="Nhập lý do từ chối hoặc yêu cầu chỉnh sửa..."></textarea>

                        <button type="button" onclick="submitForm('approve')" class="btn-submit-approve">
                            <i data-lucide="check-circle" style="width:16px;height:16px;"></i>
                            Duyệt sản phẩm
                        </button>

                        <div class="secondary-btn-group">
                            <button type="button" onclick="submitForm('request-edit')" class="btn-secondary">
                                <i data-lucide="edit" style="width:14px;height:14px;"></i>
                                Yêu cầu sửa
                            </button>
                            <button type="button" onclick="submitForm('reject')" class="btn-secondary reject-btn">
                                <i data-lucide="x-circle" style="width:14px;height:14px;"></i>
                                Từ chối
                            </button>
                        </div>

                        <div class="switch-row">
                            <div class="switch-info">
                                <h4>Ẩn khỏi hệ thống</h4>
                                <p>Sẽ không hiển thị trên website</p>
                            </div>
                            <label class="switch">
                                <input type="checkbox" name="isHidden" ${product.status == 'HIDDEN' ? 'checked' : ''}>
                                <span class="slider"></span>
                            </label>
                        </div>

                        <div class="policy-box">
                            <i data-lucide="info" style="width:16px;height:16px;color:var(--text-muted);flex-shrink:0;margin-top:2px;"></i>
                            <span>Việc duyệt sản phẩm đồng nghĩa với việc cam kết sản phẩm tuân thủ đầy đủ các chính sách của MODA.</span>
                        </div>
                    </section>

                    <section class="card" style="padding:20px;">
                        <div class="stat-details-list">
                            <div class="stat-item">
                                <span class="stat-lbl">Ngày tạo:</span>
                                <span class="stat-val"><c:out value="${fn:substring(product.createdAt, 0, 10)}"/></span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-lbl">Tồn kho:</span>
                                <span class="stat-val blue-link"><c:out value="${product.stockQuantity}"/> sản phẩm</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-lbl">Phí sàn (15%):</span>
                                <span class="stat-val">
                                    <fmt:formatNumber value="${product.basePrice * 0.15}" type="number"/> đ
                                </span>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </form>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
        }
    });

    function submitForm(action) {
        document.getElementById('formAction').value = action;
        document.getElementById('reviewForm').submit();
    }
</script>
</body>
</html>
