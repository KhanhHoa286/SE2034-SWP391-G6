
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Chi tiết đơn hàng</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/view-order.css">
</head>
<body class="profile-body">

<!-- Include Header -->
<jsp:include page="/common/header.jsp" />

<div class="profile-layout">
    <!-- Sidebar -->
    <jsp:include page="/common/customer-sidebar.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>

    <!-- Main Content -->
    <main class="profile-main">
        <div class="profile-container">
            <!-- Back to orders -->
            <a href="javascript:history.back()" class="back-link"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI</a>

            <!-- Order Header -->
            <div class="order-detail-header">
                <div class="order-title-group">
                    <h1 class="order-id">ĐƠN HÀNG #MODA${subOrderDetail.subOrderId}</h1>
                    <p class="order-date">Đặt ngày ${subOrderDetail.dateFormatted}</p>
                </div>
                <div class="order-status-group">
                    <span class="status-badge status-shipping">${subOrderDetail.statusOrder.displayName}</span>
                </div>
            </div>

            <hr class="header-divider">

            <div class="order-content-row">
                <!-- Left Column: Products -->
                <div class="order-products-col">
                    <h2 class="section-title">SẢN PHẨM TRONG ĐƠN</h2>

                    <!-- Product 1 -->
                    <c:forEach items="${subOrderDetail.shopOrders}" var="shopOrder">
                    <div class="product-card">
                        <div class="product-brand"><i class="fa-solid fa-store"></i> ${shopOrder.shopName}</div>
                        <c:forEach items="${shopOrder.items}" var="item">
                        <div class="product-details-wrap">
                            <div class="product-img-box">
                                <div class="img-placeholder">
                                    <img src="${item.thumbnail}" alt="${item.productName}">
                                    </div>
                            </div>
                            <div class="product-info">
                                <div class="product-name-price">
                                    <h3 class="product-name">${item.productName}</h3>
                                    <span class="product-price"><fmt:formatNumber type="currency" maxFractionDigits="0" value="${item.discountPrice}"></fmt:formatNumber> </span>
                                </div>
                                <div class="product-meta">
                                    <p>Màu: ${item.colorName}</p>
                                    <p>Kích thước: ${item.sizeName}</p>
                                    <p>Số lượng: ${item.quantity}</p>
                                </div>
                                <div class="product-actions">
                                    <c:if test="${subOrderDetail.statusOrder == 'DELIVERED'}">
                                    <a href="${pageContext.request.contextPath}/product-detail?pid=${item.productId}" class="action-link">Mua lại</a>
                                    </c:if>
                                    <c:if test="${item.reviewed == false && subOrderDetail.statusOrder == 'DELIVERED'}">
                                    <a href="${pageContext.request.contextPath}/customer/add-product-review?product_id=${item.productId}&order_item_id=${item.orderItemId}&sub_order_id=${item.subOrderId}" class="action-link">Viết đánh giá</a>
                                    </c:if>
                                    <c:if test="${item.reviewed == true && subOrderDetail.statusOrder == 'DELIVERED'}">
                                        <a href="${pageContext.request.contextPath}/product-review?product_id=${item.productId}" class="action-link">Xem đánh giá</a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        </c:forEach>
                        <div class="shop-subtotal">
                            <span>Tạm tính đơn hàng:</span>
                            <span class="price"><fmt:formatNumber type="currency" value="${shopOrder.shopTotal}" maxFractionDigits="0"></fmt:formatNumber> </span>
                        </div>
                    </div>
                    </c:forEach>

                </div>

                <!-- Right Column: Info & Summary -->
                <div class="order-info-col">

                    <!-- Delivery Info -->
                    <div class="info-card">
                        <h3 class="card-title">THÔNG TIN NHẬN HÀNG</h3>
                        <div class="info-group">
                            <label>Người nhận</label>
                            <p><strong>${subOrderDetail.receiverName}</strong><br>${subOrderDetail.receiverPhone}</p>
                        </div>
                        <div class="info-group">
                            <label>Địa chỉ</label>
                            <p>${subOrderDetail.shippingAddress}</p>
                        </div>
                    </div>

                    <!-- Payment Info -->
                    <div class="info-card">
                        <h3 class="card-title">Thanh toán</h3>
                        <div class="info-group">
                            <label>Phương thức</label>
                            <p>${subOrderDetail.paymentMethod.displayName}</p>
                        </div>
                        <div class="info-group">
                            <label>Trạng thái</label>
                            <p>${subOrderDetail.paymentStatus.displayName}</p>
                        </div>
                    </div>

                    <!-- Order Summary -->
                    <div class="summary-card">
                        <h3 class="card-title">TỔNG KẾT ĐƠN HÀNG</h3>
                        <hr class="summary-divider">
                        <div class="summary-row total">
                            <span>Tổng cộng</span>
                            <span><fmt:formatNumber value="${subOrderDetail.totalAllShop}" maxFractionDigits="0" type="currency"></fmt:formatNumber></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Include Footer -->
<jsp:include page="/common/footer.jsp" />

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    window.addEventListener("pageshow", function (event) {
        if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
            window.location.reload();
        }
    });
</script>
</body>
</html>
