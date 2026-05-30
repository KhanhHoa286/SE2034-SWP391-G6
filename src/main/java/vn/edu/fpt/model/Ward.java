package vn.edu.fpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {

    private Integer id;

    private Integer provinceId;
    private Province province;

    private String name;
    private String slug;
    private String type;
    private String nameWithType;
    private String path;
    private String pathWithType;

}