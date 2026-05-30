# Kế Hoạch Triển Khai Frontend & Giao Diện (E-Commerce System)

Đây là bản kế hoạch tổng quan cho dự án thương mại điện tử (E-Commerce), dựa trên nền tảng **Java Servlet/JSP, JSTL** và kiến trúc MVC hiện tại. Bản kế hoạch này giúp chuẩn hóa cấu trúc thư mục, luồng xử lý và giao diện cho các nhóm người dùng khác nhau (Customer, Seller, Admin, Delivery).

## 🗂 Cây Thư Mục Dự Án (Directory Tree)

```text
SE2034-SWP391-SU26-G6/
├── pom.xml                        # Quản lý dependencies (JSP/Servlet, JSTL, Cloudinary, BCrypt...)
├── .gitignore
└── src/
    └── main/
        ├── java/vn/edu/fpt/
        │   ├── common/            # Tiện ích chung (DBContext, UploadImage, EmailUtils...)
        │   ├── controller/        # Servlet Controllers (chia theo admin, customer, seller, delivery)
        │   ├── dao/               # Data Access Object (Truy xuất DB)
        │   ├── dto/               # Request/Response Data Transfer Objects
        │   ├── enums/             # Trạng thái hệ thống (Status, Role, Gender)
        │   └── model/             # Các Entity tương ứng với các bảng trong DB
        │
        └── webapp/                # Thư mục gốc chứa Frontend (JSP/HTML/CSS/JS)
            ├── index.html         # Trang điều hướng mặc định
            ├── admin/             # Màn hình Quản trị viên (Duyệt shop, Quản lý chiết khấu...)
            ├── customer/          # Màn hình Khách hàng (Giỏ hàng, Checkout, Đơn hàng...)
            ├── seller/            # Màn hình Người bán (Đăng sản phẩm, Quản lý shop...)
            ├── logistics/         # Màn hình Giao hàng (Cập nhật trạng thái giao hàng)
            ├── public/            # Màn hình chung (Đăng nhập, Đăng ký, Danh sách sản phẩm)
            ├── common/            # Component dùng chung (header.jsp, footer.jsp, sidebar)
            ├── assets/            # File tĩnh
            │   └── css/           # CSS chia theo từng Actor (admin, customer, seller, public)
            └── WEB-INF/           # Cấu hình an toàn, không thể truy cập trực tiếp từ URL
                ├── web.xml        # Cấu hình Servlet & Mapping
                └── ConnectDB.properties
```

---

## 🎯 Các Phân Hệ Chính (Actors)

### 1. Customer (Người mua hàng)
- **Thư mục:** `webapp/customer/` & `webapp/public/`
- **File tiêu biểu:** `list-products.jsp`, `view-product.jsp`, `list-cart-items.jsp`, `add-order.jsp`, `view-dashboard.jsp`
- **Chức năng:**
  - Tìm kiếm & xem sản phẩm (Public)
  - Thêm vào giỏ hàng, áp dụng Voucher
  - Thanh toán (Checkout) và theo dõi trạng thái đơn hàng (SubOrder)
  - Đăng ký trở thành Seller (`add-shop-profile.jsp`)

### 2. Seller (Kênh Người Bán)
- **Thư mục:** `webapp/seller/`
- **File tiêu biểu:** `view-seller-dashboard.jsp`, `add-product.jsp`, `list-seller-orders.jsp`
- **Chức năng:**
  - Dashboard thống kê doanh thu, đơn hàng
  - Đăng sản phẩm mới, upload ảnh (tích hợp Cloudinary)
  - Quản lý Voucher của Shop
  - Cập nhật trạng thái đơn hàng, yêu cầu rút tiền (Payout Request)

### 3. Admin (Quản trị hệ thống)
- **Thư mục:** `webapp/admin/`
- **File tiêu biểu:** `view-system-overview.jsp`, `list-seller-applications.jsp`, `list-payout-requests.jsp`
- **Chức năng:**
  - Tổng quan hệ thống (System Overview)
  - Duyệt yêu cầu mở Shop (Shop Application)
  - Quản lý tỷ lệ hoa hồng (Commission Rate)
  - Duyệt yêu cầu rút tiền từ Seller

### 4. Logistics (Giao Hàng)
- **Thư mục:** `webapp/logistics/`
- **File tiêu biểu:** `list-deliveries.jsp`, `edit-delivery-status.jsp`
- **Chức năng:**
  - Nhận danh sách đơn hàng cần giao
  - Cập nhật quá trình giao hàng (Delivery Status)

---

## 🔧 Hướng Dẫn Phát Triển (JSP + Servlets)

### Cấu Trúc JSP + JSTL
- Sử dụng **JSTL** cho các logic hiển thị: `<c:forEach>`, `<c:if>`, `<c:choose>`.
- Khai báo JSTL ở đầu trang JSP:
  ```jsp
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
  ```
- Tái sử dụng giao diện (Header, Footer, Sidebar) bằng `<jsp:include>`:
  ```jsp
  <jsp:include page="../common/header.jsp" />
  ```

### Quy Trình Tạo Mới Chức Năng
1. **Tạo Controller (Servlet):**
   `src/main/java/vn/edu/fpt/controller/customer/CartController.java`
2. **Khai Báo Routing:** (Sử dụng Annotation `@WebServlet("/cart")`)
3. **Chuyển Tiếp Tới JSP:**
   ```java
   request.setAttribute("cartItems", itemsList);
   request.getRequestDispatcher("/customer/cart/list-cart-items.jsp").forward(request, response);
   ```
4. **Tạo JSP Template:**
   `webapp/customer/cart/list-cart-items.jsp`
5. **Gắn CSS/JS tĩnh:**
   Lưu vào `webapp/assets/css/customer/cart.css` và reference bằng đường dẫn tuyệt đối hoặc EL:
   ```html
   <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/cart.css">
   ```

---

## 📱 Responsive Design
- **Mobile-first approach:** Sử dụng Bootstrap 5 hoặc CSS Grid/Flexbox.
- **Breakpoints cơ bản:**
  - Dưới 768px: Mobile (Ẩn Sidebar bằng menu Hamburger, Grid 1 cột)
  - 768px - 1024px: Tablet (Grid 2 cột)
  - 1024px trở lên: Desktop (Hiển thị đầy đủ Sidebar, Grid 3-4 cột)

---

## 🎨 Color & Theme Guidelines
*(Nên đặt thành các CSS Variables trong một file base.css)*

```css
:root {
  --primary-color: #f53d2d;       /* Đỏ cam đặc trưng của E-commerce (Shopee-style) */
  --secondary-color: #ff6633;     /* Cam nhạt */
  --success-color: #28a745;       /* Xanh lá (Hoàn thành/Thành công) */
  --danger-color: #dc3545;        /* Đỏ (Lỗi/Hủy đơn) */
  --warning-color: #ffc107;       /* Vàng (Đang chờ xử lý) */
  --light-bg: #f5f5f5;            /* Xám nhạt (Nền trang) */
  --text-dark: #333333;           /* Chữ tối màu */
  --text-muted: #757575;          /* Chữ nhạt/Mô tả */
}
```

---

## 🚀 Tiếp Theo (Next Steps)
1. **[UI/UX]** Hoàn thiện các file CSS trong `assets/css/` cho đồng bộ giao diện.
2. **[Backend]** Xây dựng luồng Authenticate/Authorize (Bộ lọc Filter để check quyền Admin, Seller, Customer).
3. **[Integrations]** Tích hợp đầy đủ SDK Cloudinary (`UploadImage.java`) để upload hình ảnh sản phẩm/avatar.
4. **[Checkout]** Hoàn thiện logic tính tổng tiền, phí vận chuyển và áp dụng Voucher.

---

## 📝 Git Workflow
- **Branch:** `feature/ui-dashboard` (Ví dụ cho tạo giao diện)
- **Commit Message:** `feat: Thêm giao diện Dashboard cho Seller`
- **Push:** `git push -u origin feature/ui-dashboard`
- **Tạo Pull Request (PR):** Review code và test kĩ trước khi merge vào nhánh `main`.
