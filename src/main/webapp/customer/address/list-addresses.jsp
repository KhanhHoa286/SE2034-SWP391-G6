<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>MODA - Sổ địa chỉ</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">

    <!-- Font Awesome nếu header chung đang dùng -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Bootstrap nếu header chung đang dùng -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">

    <!-- CSS layout sidebar/profile dùng chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css?v=4">

    <!-- CSS riêng màn sổ địa chỉ -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/list-addresses.css?v=1">
</head>

<body class="profile-body address-book-body">

<jsp:include page="/common/header.jsp" />

<div class="profile-layout address-book-layout">

    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="addresses" />
    </jsp:include>

    <main class="profile-main address-book-main">
        <div class="address-book-container">

            <header class="address-book-header">
                <div>
                    <h1>Sổ địa chỉ</h1>
                    <p>Quản lý địa chỉ nhận hàng và thanh toán của bạn để đặt hàng nhanh hơn.</p>
                </div>
            </header>

            <section class="address-book-grid">

                <c:choose>
                    <c:when test="${empty addresses}">
                        <div class="address-empty-card">
                            <span class="material-symbols-outlined">location_off</span>
                            <h2>Chưa có địa chỉ giao hàng</h2>
                            <p>Bạn chưa thêm địa chỉ nhận hàng nào cho tài khoản này.</p>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <c:forEach items="${addresses}" var="address">
                            <article class="address-card ${address.isDefault ? 'address-card--default' : ''}">

                                <div class="address-card-main">

                                    <div class="address-card-top">
                                        <c:if test="${address.isDefault}">
                                            <span class="address-default-badge">Mặc định</span>
                                        </c:if>

                                        <span class="material-symbols-outlined address-card-icon">
                                            ${address.isDefault ? 'home' : 'location_on'}
                                        </span>
                                    </div>

                                    <h2>
                                        <c:out value="${address.receiverName}" />
                                    </h2>

                                    <p class="address-line">
                                        <span class="material-symbols-outlined">phone</span>
                                        <c:out value="${address.receiverPhone}" />
                                    </p>

                                    <p class="address-line address-line--location">
                                        <span class="material-symbols-outlined">location_on</span>
                                        <span>
                                            <c:out value="${address.streetAddress}" />
                                            <c:if test="${not empty address.ward.pathWithType}">
                                                , <c:out value="${address.ward.pathWithType}" />
                                            </c:if>
                                        </span>
                                    </p>

                                </div>

                                <div class="address-card-actions">
                                    <a href="${pageContext.request.contextPath}/customer/addresses/edit?id=${address.addressId}" class="address-action address-action--edit">
                                        <span class="material-symbols-outlined">edit</span>
                                        Chỉnh sửa
                                    </a>

                                    <a href="javascript:void(0);" class="address-action address-action--delete">
                                        <span class="material-symbols-outlined">delete</span>
                                        Xóa
                                    </a>
                                </div>

                            </article>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <a class="address-add-card" href="${pageContext.request.contextPath}/customer/addresses/add">
                    <span class="address-add-icon">
                        <span class="material-symbols-outlined">add</span>
                    </span>
                    <span>Thêm địa chỉ giao hàng</span>
                </a>

            </section>

        </div>
    </main>

</div>

<jsp:include page="/common/footer.jsp" />

</body>
</html>