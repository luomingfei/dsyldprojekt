package com.mmd.domain.dto.massagist;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;

@Data
public class EstimationsDTO extends BaseQueryBean {
    private String cityId;
    private String massagerId;
    private Integer uid;
    private Integer type;
}
