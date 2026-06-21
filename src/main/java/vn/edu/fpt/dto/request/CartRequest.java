package vn.edu.fpt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {
    private Integer productId;
    private Integer colorId;
    private Integer sizeId;
    private Integer quantity;
}
