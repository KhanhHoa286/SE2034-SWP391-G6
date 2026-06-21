package vn.edu.fpt.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryFilterResponse {
    private List<OrderHistoryResponse> orderHistoryResponseList = new ArrayList<>();
    private int currentPage;
    private int totalPage;
}
