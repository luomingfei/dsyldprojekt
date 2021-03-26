package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class EstimationQueryInputDTO extends BaseQueryBean {
    private Integer uid;
    private Integer type;
    private Integer pid;
    private List<Integer> pids;
}
