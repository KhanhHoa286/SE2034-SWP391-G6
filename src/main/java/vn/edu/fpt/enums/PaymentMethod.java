package vn.edu.fpt.enums;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    BANK("Thanh toán qua ngân hàng");

     private final String displayName;

     PaymentMethod(String displayName) {
         this.displayName = displayName;
     }

     public String getDisplayName(){
         return this.displayName;
     }

}
