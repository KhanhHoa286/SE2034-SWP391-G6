<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shipping Configuration - MODA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/seller.css?v=20260611c">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/seller/edit-shipping-settings.css?v=20260611c">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>
<div class="seller-page-shell">
    <%
        request.setAttribute("activePage", "shop");
    %>
    <%@ include file="/seller/taskbar-seller.jsp" %>

    <main class="seller-page-main">
        <header class="top-header">
            <div class="profile-section">
                <span>Seller Center</span>
            </div>
        </header>
        <section class="seller-page-content">
            <h1>Shipping Configuration</h1>
            <p>Configure shipping settings for the shop.</p>
        </section>
    </main>
</div>
<script>
    lucide.createIcons();
</script>
</body>
</html>
