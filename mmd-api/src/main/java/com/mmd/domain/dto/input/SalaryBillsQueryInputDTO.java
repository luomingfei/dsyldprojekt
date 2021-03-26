package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SalaryBillsQueryInputDTO extends BaseQueryBean {
    private String tid;
}
