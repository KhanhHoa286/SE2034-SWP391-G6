# Kế Hoạch Triển Khai Lớp Truy Xuất Dữ Liệu (DAO Layer Plan)

Tài liệu này quy định kiến trúc và phân công nhiệm vụ cho tầng truy cập cơ sở dữ liệu (DAO). Việc phân chia các class DAO một cách logic giúp hệ thống dễ mở rộng, tái sử dụng code và thuận lợi cho việc chia task làm việc nhóm.

---

## 🏗 1. Danh Sách Các Class DAO Cần Triển Khai

Dựa trên cấu trúc Database (chứa trong `vn.edu.fpt.model`), dưới đây là danh sách 7 nhóm DAO cốt lõi cần được thiết kế:

### Nhóm 1: Tài Khoản & Định Danh (Auth & Users)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `UserDAO` | `User`, `Role`, `EmailVerification` | Đăng nhập/Đăng ký; Lấy thông tin user; Phân quyền; Hash/Check mật khẩu; Lưu token quên mật khẩu. |
| `AddressDAO` | `Address`, `Province`, `Ward` | Lấy danh sách Tỉnh/Huyện/Xã; Thêm/Sửa/Xóa địa chỉ giao hàng của người dùng. |

### Nhóm 2: Người Bán & Cửa Hàng (Seller Center)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `ShopDAO` | `Shop`, `ShopApplication` | Insert đơn đăng ký bán hàng; Admin cập nhật trạng thái duyệt (Approve/Reject); Seller sửa thông tin Shop. |

### Nhóm 3: Sản Phẩm (Catalog)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `ProductDAO` | `Product`, `ProductVariant`, `ProductImage`, `ProductStatusLog`, `Color`, `Size` | Lọc, tìm kiếm, phân trang sản phẩm (Pagination); Join bảng để lấy thuộc tính Size/Color; Thêm ảnh. |
| `CategoryDAO` | `Category` | Lấy danh sách ngành hàng (Menu động); Phân cấp danh mục cha/con. |
| `ReviewDAO` | `ProductReview` | Thêm bình luận; Đếm số lượng sao (Rating); Lọc Review theo sao. |

### Nhóm 4: Mua Sắm & Giỏ Hàng
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `CartDAO` | `CartItem` | Thêm vào giỏ; Tăng/giảm số lượng sản phẩm; Xóa item khi đã hoàn tất đặt hàng. |
| `WishlistDAO` | `Wishlist` | Toggle Yêu thích/Bỏ yêu thích; Lấy danh sách Wishlist của 1 User. |

### Nhóm 5: Đơn Hàng & Vận Chuyển (Orders & Logistics)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `OrderDAO` | `MasterOrder`, `SubOrder`, `OrderItem` | Insert transaction đặt hàng; Thống kê doanh thu theo `SubOrder` cho từng Shop; Đổi trạng thái đơn. |
| `DeliveryDAO` | `Delivery`, `DeliveryLog` | Xử lý việc giao nhận; Ghi log (nhật ký) thời gian trạng thái vận chuyển. |

### Nhóm 6: Khuyến Mãi (Marketing)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `VoucherDAO` | `Voucher`, `UserVoucher` | Generate mã giảm giá; Trừ số lượng lượt dùng Voucher; Kiểm tra điều kiện áp dụng mã. |

### Nhóm 7: Tài Chính & Ví (Finance)
| Tên Class DAO | Các Entity Xử Lý | Nhiệm vụ chính (CRUD & Logic DB) |
| :--- | :--- | :--- |
| `WalletDAO` | `SellerWallet`, `PayoutRequest`, `CommissionConfig` | Cộng tiền vào ví Seller khi hoàn thành đơn; Admin lấy % hoa hồng; Xử lý Yêu cầu rút tiền (Trừ tiền ví). |

---

## 📐 2. Kiến Trúc Lớp DAO (Design Pattern)

Để code gọn gàng, hệ thống DAO sẽ áp dụng cấu trúc **BaseDAO (hoặc DBContext)**.
Tất cả các class DAO ở trên đều phải kế thừa (`extends`) từ `DBContext` chứa kết nối CSDL chung.

**Ví dụ quy tắc đặt tên & kế trúc:**
```java
package vn.edu.fpt.dao;

import vn.edu.fpt.common.DBContext;
import vn.edu.fpt.model.User;

public class UserDAO extends DBContext {
    
    // Tìm kiếm người dùng bằng email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                // Map ResultSet to User Entity
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}
```

---

## 🚦 3. Lộ Trình Code DAO (Roadmap)
Để không bị kẹt nghiệp vụ (vì các bảng ràng buộc Khóa ngoại - Foreign Key với nhau), nhóm cần phân công viết code DAO theo thứ tự sau:

1. **Giai đoạn 1 (Nền tảng):** `UserDAO`, `CategoryDAO` (Vì đây là 2 bảng độc lập nhất, các bảng khác đều nối vào).
2. **Giai đoạn 2 (Người bán & SP):** `ShopDAO`, `AddressDAO`, `ProductDAO` (SP cần Shop và Category).
3. **Giai đoạn 3 (Giỏ hàng & Khuyến mãi):** `CartDAO`, `VoucherDAO`, `WishlistDAO`.
4. **Giai đoạn 4 (Checkout & Giao hàng):** `OrderDAO`, `DeliveryDAO` (Cần tất cả các DAO trước đó).
5. **Giai đoạn 5 (Tài chính):** `WalletDAO`, `ReviewDAO` (Khâu hậu mãi).

> **Lưu ý:** Chỉ viết các câu query `SELECT, INSERT, UPDATE, DELETE` tại tầng này. Tuyệt đối không kiểm tra logic kinh doanh (ví dụ: giỏ hàng lớn hơn 0, check mật khẩu độ dài 8 ký tự...) tại tầng DAO, mà hãy để các Controller xử lý.
