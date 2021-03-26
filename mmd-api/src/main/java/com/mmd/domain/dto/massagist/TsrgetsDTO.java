package com.mmd.domain.dto.massagist;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;

@Data
public class TsrgetsDTO extends BaseQueryBean {
    private String keyword;
    private String type;
    private Double lng;
    private Double lat;
    private String sorts;

}
