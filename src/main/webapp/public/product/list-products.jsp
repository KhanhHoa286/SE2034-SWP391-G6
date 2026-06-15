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

<jsp:include page="/common/header.jsp" />

<main class="container">
    <div class="breadcrumb" style="margin-top: 5%">
                <%-- Lưới bảo hiểm: Nếu khách bấm trực tiếp trên Header -> Mặc định quay về trang chủ --%>
                <a href="javascript:history.back()" class="text-dark text-decoration-none">
                    <i class="fa-solid fa-chevron-left"></i> QUAY LẠI
                </a>
    </div>

    <form action="product-list" method="get">
        <%--Gửi dữ liệu search về bên contrller--%>
        <input type="hidden" name="type" value="${filter.type}">
        <input type="hidden" name="text_search" value="${filter.textSearch}">

        <div class="page-header">
            <h1 class="page-title" style="margin-bottom: 0;">
                <c:choose>
                    <%-- KHỐI 1: NẾU CÓ TỪ KHÓA TÌM KIẾM --%>
                    <c:when test="${not empty filter.textSearch}">
                        TẤT CẢ SẢN PHẨM '${filter.textSearch}'
                    </c:when>

                    <c:otherwise>
                        TẤT CẢ SẢN PHẨM
                        <c:choose>
                            <c:when test="${filter.type eq 'NU'}"> NỮ</c:when>
                            <c:when test="${filter.type eq 'NAM'}"> NAM</c:when>
                            <c:when test="${filter.type eq 'UNISEX'}"> UNISEX</c:when>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </h1>

          <%--Sắp xếp theo mới nhất, giá giảm, giắ tăng--%>
            <div style="display:flex; justify-content: flex-end;">
                <div class="sort-wrapper">
                    <span>Sắp xếp theo:</span>
                    <select class="sort-select" name="sort_by" onchange="this.form.submit()">
                        <option value="" ${filter.sortBy eq '' || empty filter.sortBy ? 'selected' : ''}>Mới nhất</option>
                        <option value="low_price" ${filter.sortBy eq 'low_price' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                        <option value="high_price" ${filter.sortBy eq 'high_price' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
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
                            <option value="${parentCate.categoryId}" ${parentCate.categoryId eq filter.cid ? 'selected' : ''} style="font-weight: bold;">
                                    ${parentCate.categoryName}
                            </option>

                            <c:forEach items="${parentCate.listChildCategory}" var="childCate">
                                <option value="${childCate.categoryId}" ${childCate.categoryId eq filter.cid ? 'selected' : ''}>
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
                        <input type="number" min="0" placeholder="Từ" class="price-input" name="price_from" value="${filter.priceFrom}">
                        <span>-</span>
                        <input type="number" min="0" placeholder="Đến" class="price-input" name="price_to" value="${filter.priceTo}">
                    </div>
                    <button class="apply-btn" type="submit">Áp dụng</button>
                </div>

                <div class="sidebar__widget">
                    <h3 class="sidebar__widget-title">Tỉnh thành</h3>
                    <select class="form-select mb-3" name="province_id" onchange="this.form.submit()">
                        <option value="">Tất cả tỉnh thành</option>
                        <c:forEach items="${provinceList}" var="province">
                            <option value="${province.id}" ${province.id eq filter.provinceId ? 'selected' : ''}>
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
                        <%@ include file="/public/product/product-card.jsp" %>
                    </c:forEach>
                </div>

                <%--Setup dữ liệu để gửi về bên controoler--%>
                <c:set var="filterPayload" value="&type=${filter.type}&cid=${filter.cid}&text_search=${filter.textSearch}&province_id=${filter.provinceId}&sort_by=${filter.sortBy}&price_from=${filter.priceFrom}&price_to=${filter.priceTo}" />

                <%--Tính toán phân trang --%>
                <c:if test="${totalPages > 1}">
                    <div class="moda-pagination">
                        <c:if test="${filter.page > 1}">
                            <a href="${pageContext.request.contextPath}/product-list?page=${filter.page - 1}${filterPayload}" class="moda-page-link">
                                <i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/product-list?page=${i}${filterPayload}" class="moda-page-num ${i eq filter.page ? 'active' : ''}">
                                    ${i}
                            </a>
                        </c:forEach>

                        <c:if test="${filter.page < totalPages}">
                            <a href="${pageContext.request.contextPath}/product-list?page=${filter.page + 1}${filterPayload}" class="moda-page-link">
                                SAU &nbsp; <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </form> </main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>