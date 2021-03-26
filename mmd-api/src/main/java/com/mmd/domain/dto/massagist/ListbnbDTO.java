package com.mmd.domain.dto.massagist;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;

@Data
public class ListbnbDTO extends BaseQueryBean {
    private String cityId;
    private String keyword;
    private String type;
    private String address;
    private Double lng;
    private Double lat;
    private Integer tid;

}
