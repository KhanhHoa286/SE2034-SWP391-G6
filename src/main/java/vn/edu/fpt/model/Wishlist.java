package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    private Integer userId;
    private User user;

    private Integer productId;
    private Product product;

    private LocalDateTime addedAt;

}