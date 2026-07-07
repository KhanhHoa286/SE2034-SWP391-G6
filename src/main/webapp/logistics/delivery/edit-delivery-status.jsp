<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Delivery Status - MODA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/edit-delivery-status.css?v=20260611c">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-page-shell">
    <%
        request.setAttribute("activePage", "delivery-status");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="seller-page-main">
        <header class="top-header">
            <div class="profile-section">
                <span>Trung tâm giao hàng</span>
            </div>
        </header>
        <section class="seller-page-content">
            <h1>Cập nhật trạng thái giao hàng</h1>
            <p>Màn cập nhật trạng thái sẽ được hoàn thiện ở chức năng Edit Delivery Status.</p>
        </section>
    </main>
</div>
<script>
    lucide.createIcons();
</script>
</body>
</html>
