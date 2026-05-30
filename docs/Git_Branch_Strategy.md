# Task Mapping by Branch (E-Commerce System)

Tài liệu này ghi rõ nhiệm vụ chính tương ứng với từng nhánh (branch) Git cần tạo từ nhánh `main` (hoặc `develop`), được thiết kế **dành riêng cho cấu trúc dự án Thương mại điện tử (E-Commerce)** của nhóm bạn. 

Dựa vào các module đã quét (Admin, Customer, Seller, Delivery, Product, Order, Wallet, vv.), dưới đây là danh sách phân chia nhánh hợp lý nhất:

## 🌿 Danh sách nhánh và nhiệm vụ

| Branch | Nhiệm vụ chính | Ghi chú |
| :--- | :--- | :--- |
| `chore/project-setup` | Chuẩn hoá `pom.xml`, `.gitignore`, `ConnectDB.properties`, `web.xml`. Thiết lập kiến trúc MVC nền tảng. | Chuẩn bị nền tảng ban đầu, loại bỏ file rác. |
| `feature/database-connection` | Viết/Cập nhật các hàm dùng chung tại `DBContext.java`, khởi tạo các file script SQL của Database. | Core kết nối để các nhóm DAO có thể gọi tới. |
| `feature/common-utilities` | Hoàn thiện thư mục `common/`: `EmailUtils.java`, `PasswordUtils.java` (BCrypt), `UploadImage.java` (Cloudinary). | Nền tảng gửi mail, hash mật khẩu và lưu trữ ảnh. |
| `feature/auth-system` | Xây dựng luồng Đăng ký / Đăng nhập / Quên mật khẩu; Quản lý Session; Phân quyền (Admin, Customer, Seller, Delivery). | Phục vụ các file trong thư mục `public/auth/`. |
| `feature/customer-ui` | Phát triển giao diện phía người mua: Trang chủ, chi tiết sản phẩm, giỏ hàng, thông tin cá nhân. | UI cho thư mục `customer/` và `public/home/`. |
| `feature/seller-ui` | Phát triển giao diện cho Kênh người bán: Dashboard, quản lý shop, danh sách sản phẩm, đăng sản phẩm mới. | UI cho thư mục `seller/`. |
| `feature/admin-ui` | Phát triển giao diện cho Quản trị viên: Dashboard tổng quan, duyệt đăng ký shop, quản lý chiết khấu. | UI cho thư mục `admin/`. |
| `feature/delivery-ui` | Phát triển giao diện cho Logistics: Nhận đơn hàng, cập nhật trạng thái vận chuyển (`edit-delivery-status.jsp`). | UI cho thư mục `logistics/`. |
| `feature/product-management` | Backend (Controller + DAO) xử lý nghiệp vụ sản phẩm: Đăng sản phẩm, upload ảnh, phân loại Category, biến thể (Size, Color). | Tương tác với `Product`, `ProductVariant`, `Category`. |
| `feature/cart-checkout` | Backend xử lý Giỏ hàng, Đặt hàng: Sinh `MasterOrder`, tách `SubOrder` cho từng shop, tính tổng tiền. | Tương tác với `CartItem`, `MasterOrder`, `SubOrder`. |
| `feature/voucher-system` | Xây dựng logic Khuyến mãi: Tạo voucher (`VoucherStatus`), áp dụng mã giảm giá khi Checkout. | Nghiệp vụ `Voucher` và `UserVoucher`. |
| `feature/wallet-payout` | Xây dựng logic Ví người bán & Rút tiền: `SellerWallet`, xử lý `PayoutRequest`, hoa hồng (Commission). | Tính năng Tài chính của Admin và Seller. |
| `test/unit-and-integration` | Viết Unit Test (Junit 3.8.1 theo `pom.xml`) cho các logic tính toán quan trọng (tính giá, mã hóa mk, luồng đặt hàng). | Đảm bảo code chạy đúng trước khi merge. |
| `docs/update-rds` | Cập nhật tài liệu yêu cầu (RDS/SRS), Database Schema Diagram (ERD), API/Flowchart. | Đồng bộ tiến độ code và tài liệu. |

## 💡 Hướng Dẫn Quy Trình Git Nhóm (Git Workflow)
1. **Lấy code mới nhất:** Luôn chạy `git pull origin main` (hoặc `develop`) trước khi tạo nhánh mới.
2. **Tạo nhánh:** Cắt nhánh từ main/develop: 
   `git checkout -b feature/tên-nhánh`
3. **Commit Code:** Ghi commit rõ ràng, chia nhỏ commit theo từng chức năng hoàn thiện. (vd: `feat(auth): thêm logic mã hóa mật khẩu BCrypt`)
4. **Push & Pull Request (PR):** 
   - `git push -u origin feature/tên-nhánh`
   - Lên Github tạo PR (Pull Request).
   - Chỉ định ít nhất 1 thành viên khác trong nhóm vào review code trước khi **Merge**.
5. **Xử lý Xung đột (Conflict):** Người tạo PR phải chịu trách nhiệm rebase/merge để giải quyết conflict trước khi code được gộp vào nhánh chính.

---
*Ghi chú: Việc chia nhánh rất chi tiết giúp nhóm nhiều thành viên có thể code song song các module Controller, UI, và DAO mà không bị dẫm chân lên nhau (conflict).*
