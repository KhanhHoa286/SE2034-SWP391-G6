package vn.edu.fpt.dto.request;

import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.util.ParamUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {
    private Integer shopId;
    private String type;
    private Integer cid;
    private String textSearch;
    private Integer provinceId;
    private String sortBy;
    private int page;
    private int pageSize = 8;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;

    public static ProductFilterRequest fromRequest(HttpServletRequest request) {
        ProductFilterRequest filter = new ProductFilterRequest();

        filter.setType(request.getParameter("type") != null ? request.getParameter("type") : request.getParameter("gender"));
        filter.setSortBy(request.getParameter("sort_by"));
        filter.setTextSearch(request.getParameter("text_search"));
        filter.setCid(ParamUtil.getInteger(request, "cid"));
        filter.setProvinceId(ParamUtil.getInteger(request, "province_id"));
        filter.setPriceFrom(ParamUtil.getBigDecimal(request, "price_from"));
        filter.setPriceTo(ParamUtil.getBigDecimal(request, "price_to"));

        Integer p = ParamUtil.getInteger(request, "page");
        filter.setPage((p != null && p > 0) ? p : 1);

        return filter;
    }
}