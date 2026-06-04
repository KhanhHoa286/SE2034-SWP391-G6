<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Danh sách sản phẩm</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/products.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/public/header.jsp" />

<main class="container">
    <form action="product-list" method="get">
        <%--Gửi dữ liệu search về bên contrller--%>
        <input type="hidden" name="type" value="${type}">
        <input type="hidden" name="text_search" value="${textSearch}">

        <div class="page-header">
            <h1 class="page-title" style="margin-bottom: 0;">TẤT CẢ SẢN PHẨM</h1>

          <%--Sắp xếp theo mới nhất, giá giảm, giắ tăng--%>
            <div style="display:flex; justify-content: flex-end;">
                <div class="sort-wrapper">
                    <span>Sắp xếp theo:</span>
                    <select class="sort-select" name="sort_by" onchange="this.form.submit()">
                        <option value="" ${sortBy eq '' || empty sortBy ? 'selected' : ''}>Mới nhất</option>
                        <option value="low_price" ${sortBy eq 'low_price' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                        <option value="high_price" ${sortBy eq 'high_price' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="row mt-4">
           <%--Hiển thị category theo cha con--%>
            <aside class="sidebar col-lg-3 mb-4">
                <div class="sidebar__widget">
                    <h3 class="sidebar__widget-title">Danh mục</h3>
                    <select class="form-select mb-3" name="cid" onchange="this.form.submit()">
                        <option value="">Tất cả danh mục</option>
                        <c:forEach items="${categoryList}" var="parentCate">
                            <option value="${parentCate.categoryId}" ${parentCate.categoryId eq categoryId ? 'selected' : ''} style="font-weight: bold;">
                                    ${parentCate.categoryName}
                            </option>

                            <c:forEach items="${parentCate.listChildCategory}" var="childCate">
                                <option value="${childCate.categoryId}" ${childCate.categoryId eq categoryId ? 'selected' : ''}>
                                    &nbsp;&nbsp;— ${childCate.categoryName}
                                </option>
                            </c:forEach>
                        </c:forEach>
                    </select>
                </div>

                <%--Lọc theo mức giá từ -> đến--%>
                <div class="sidebar__widget">
                    <h3 class="sidebar__widget-title">Mức giá</h3>
                    <div class="price-range-inputs">
                        <input type="number" min="0" placeholder="Từ" class="price-input" name="price_from" value="${priceFrom}">
                        <span>-</span>
                        <input type="number" min="0" placeholder="Đến" class="price-input" name="price_to" value="${priceTo}">
                    </div>
                    <button class="apply-btn" type="submit">Áp dụng</button>
                </div>

                <div class="sidebar__widget">
                    <h3 class="sidebar__widget-title">Tỉnh thành</h3>
                    <select class="form-select mb-3" name="province_id" onchange="this.form.submit()">
                        <option value="">Tất cả tỉnh thành</option>
                        <c:forEach items="${provinceList}" var="province">
                            <option value="${province.id}" ${province.id eq provinceId ? 'selected' : ''}>
                                    ${province.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </aside>

             <%--Load lên danh sách sản phẩm --%>
            <div class="products-list col-lg-9">
                <div class="row g-4">
                    <c:if test="${empty listProductFilter}">
                        <div class="col-12 text-center py-5">
                            <p class="text-muted fs-5">Không tìm thấy sản phẩm nào phù hợp với bộ lọc.</p>
                        </div>
                    </c:if>

                    <c:forEach items="${listProductFilter}" var="product">
                        <article class="product-card col-6 col-md-4 col-lg-3">
                            <a href="product-detail?pid=${product.productId}" style="color:inherit; text-decoration:none;">
                                <div class="product-card__img-wrapper">
                                    <c:if test="${product.discountPercentage > 0}">
                                        <span class="product-card__badge">-${product.discountPercentage}%</span>
                                    </c:if>
                                    <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                                </div>
                            </a>
                            <div class="product-card__info">
                                <div class="product-card__brand">
                                    <span>${product.shopName}</span>
                                    <span class="location"><i class="fa-solid fa-location-dot"></i> ${product.provinceName}</span>
                                </div>
                                <a href="product-detail?pid=${product.productId}" style="color:inherit; text-decoration:none;">
                                    <h3 class="product-card__title">${product.productName}</h3>
                                </a>
                                <div class="product-card__price">
                                    <c:if test="${product.discountPercentage > 0}">
                                    <span class="product-card__price-current">
                                        <fmt:formatNumber value="${product.finalPrice.doubleValue()}" type="currency" maxFractionDigits="0"/>
                                    </span>
                                    </c:if>
                                    <span class="${product.discountPercentage > 0 ? 'product-card__price-old' : 'product-card__price-current'}">
                                    <fmt:formatNumber value="${product.basePrice.doubleValue()}" type="currency" maxFractionDigits="0"/>
                                </span>
                                </div>
                                <div style="font-size: 0.85rem; color: #777; margin-top: 4px;">Số lượng: ${product.totalStock}</div>
                            </div>
                        </article>
                    </c:forEach>
                </div>

                <%--Setup dữ liệu để gửi về bên controoler--%>
                <c:set var="filterPayload" value="&type=${type}&cid=${categoryId}&text_search=${textSearch}&province_id=${provinceId}&sort_by=${sortBy}&price_from=${priceFrom}&price_to=${priceTo}" />

                <%--Tính toán phân trang --%>
                <c:if test="${totalPages > 1}">
                    <div class="moda-pagination">
                        <c:if test="${page > 1}">
                            <a href="product-list?page=${page - 1}${filterPayload}" class="moda-page-link">
                                <i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="product-list?page=${i}${filterPayload}" class="moda-page-num ${i eq page ? 'active' : ''}">
                                    ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${page < totalPages}">
                            <a href="product-list?page=${page + 1}${filterPayload}" class="moda-page-link">
                                SAU &nbsp; <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>

            </div>
        </div>
    </form> </main>

<jsp:include page="/public/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>