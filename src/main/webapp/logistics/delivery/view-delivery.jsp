<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Delivery - MODA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/view-delivery.css?v=20260611c">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-page-shell">
    <%
        request.setAttribute("activePage", "delivery-view");
    %>
    <%@ include file="/logistics/taskbar-delivery.jsp" %>

    <main class="seller-page-main">
        <header class="top-header">
            <div class="profile-section">
                <span>Trung tâm giao hàng</span>
            </div>
        </header>
        <section class="seller-page-content">
            <h1>Chi tiết đơn giao</h1>
            <p>Màn chi tiết đơn giao sẽ được hoàn thiện ở chức năng View Delivery.</p>
        </section>
    </main>
</div>
<script>
    lucide.createIcons();
</script>
</body>
</html>
