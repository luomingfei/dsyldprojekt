package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderQueryInputDTO extends BaseQueryBean {
    private String userId;
    private String zt;
    private Integer tid;
}
