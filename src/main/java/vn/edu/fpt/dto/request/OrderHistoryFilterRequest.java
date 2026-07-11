package vn.edu.fpt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryFilterRequest {
    private String fromDate;
    private String toDate;
    private String status;
    private int pageNumber = 1;
}
