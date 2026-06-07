<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MODA - Cửa hàng</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public/view-shop.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%--<jsp:include page="/public/header.jsp" />--%>

<main class="container">
    <!-- Quay lại trang trước đó  -->
    <div class="breadcrumb" style="margin-top: 5%">
        <a href="javascript:history.back()" class="text-dark text-decoration-none"><i class="fa-solid fa-chevron-left"></i> QUAY LẠI</a>
    </div>

    <form action="shop" method="get">
        <input type="hidden" name="shop_id" value="${shopInfo.shopId}">
    <!-- Thông tin shop -->
    <div class="vendor-banner">
        <div class="vendor-info">
            <div class="vendor-avatar">
                <img src="${shopInfo.logoUrl}" alt="${shopInfo.shopName}">
            </div>
            <div class="vendor-details">
                <h1>${shopInfo.shopName}</h1>
                <span class="location"><i class="fa-solid fa-location-dot" style="color:red"></i> ${shopInfo.fullAddress}</span>
            </div>
        </div>

        <%--Sắp xếp sản phẩm--%>
        <div class="sort-wrapper">
                <span>Sắp xếp theo:</span>
                <select class="sort-select" name="sort_by" onchange="this.form.submit()">
                    <option value="" ${sortBy eq '' || empty sortBy ? 'selected' : ''}>Mới nhất</option>
                    <option value="low_price" ${sortBy eq 'low_price' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                    <option value="high_price" ${sortBy eq 'high_price' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
            </select>
        </div>
    </div>

    <!-- Cấu trúc Layout (Tái sử dụng từ products.html) -->
    <div class="row mt-4">
        <aside class="sidebar col-lg-3 mb-4">
          <%-- search--%>
            <div class="header__search" style="margin-bottom: 35px;width: 100%">
                    <input type="text" placeholder="Tìm kiếm trong shop..." class="header__search-input" style="width: 90%;" name="text_search" value="${textSearch}">
                    <button type="submit" style="border: 0px;background: transparent;"><i class="fa-solid fa-magnifying-glass header__icon"></i></button>
            </div>
            <!-- Giới tính -->
            <div class="sidebar__widget">
                <h3 class="sidebar__widget-title">Giới tính</h3>
                <div class="mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="gender" id="genderNam" value="NAM" ${gender eq 'NAM' ? 'checked' : ''} onchange="this.form.submit()">
                        <label class="form-check-label" for="genderNam">Nam</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="gender" id="genderNu" value="NU" ${gender eq 'NU' ? 'checked' : ''} onchange="this.form.submit()">
                        <label class="form-check-label" for="genderNu">Nữ</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="gender" id="genderUnisex" value="UNISEX" ${gender eq 'UNISEX' ? 'checked' : ''} onchange="this.form.submit()">
                        <label class="form-check-label" for="genderUnisex">Unisex</label>
                    </div>
                </div>
            </div>

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
        </aside>

        <!-- Danh sách sản phẩm -->
        <div class="products-list col-lg-9">
            <div class="row g-4">
                <c:if test="${empty listProductFilter}">
                    <div class="col-12 text-center py-5">
                        <p class="text-muted fs-5">Không tìm thấy sản phẩm nào phù hợp với bộ lọc.</p>
                    </div>
                </c:if>
                <!-- Product Item 1 -->
                <c:forEach items="${listProductFilter}" var="product">
                <article class="product-card col-6 col-md-4 col-lg-3">
                    <a href="product-detail?pid=${product.productId}&gender=${product.gender}&final_price=${product.finalPrice}" style="color:inherit; text-decoration:none;">
                        <div class="product-card__img-wrapper">
                            <c:if test="${product.discountPercentage > 0}">
                                <span class="product-card__badge">-${product.discountPercentage}%</span>
                            </c:if>
                            <img src="${product.thumbnailUrl}" alt="${product.productName}" class="product-card__img">
                        </div>
                    </a>
                    <button class="product-card__favorite ${product.liked == true ? 'active' : ''}" type="button" onclick="toggleWishlist(${product.productId}, '${pageContext.request.contextPath}')"><i class="fa-regular fa-heart"></i></button>
                    <div class="product-card__info">
                        <a href="product-detail?pid=${product.productId}&gender=${product.gender}&final_price=${product.finalPrice}&final_price=${product.finalPrice}" style="color:inherit; text-decoration:none;">
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

            <!-- Pagination -->
            <%--Setup dữ liệu để gửi về bên controoler--%>
            <c:set var="filterPayload" value="&shop_id=${shopInfo.shopId}&type=${type}&cid=${categoryId}&text_search=${textSearch}&province_id=${provinceId}&sort_by=${sortBy}&price_from=${priceFrom}&price_to=${priceTo}" />

            <%--Tính toán phân trang --%>
            <c:if test="${totalPages > 1}">
                <div class="moda-pagination">
                    <c:if test="${page > 1}">
                        <a href="shop?page=${page - 1}${filterPayload}" class="moda-page-link">
                            <i class="fa-solid fa-chevron-left"></i> &nbsp; TRƯỚC
                        </a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="shop?page=${i}${filterPayload}" class="moda-page-num ${i eq page ? 'active' : ''}">
                                ${i}
                        </a>
                    </c:forEach>

                    <c:if test="${page < totalPages}">
                        <a href="shop?page=${page + 1}${filterPayload}" class="moda-page-link">
                            SAU &nbsp; <i class="fa-solid fa-chevron-right"></i>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
    </form>
</main>

<jsp:include page="/public/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>


