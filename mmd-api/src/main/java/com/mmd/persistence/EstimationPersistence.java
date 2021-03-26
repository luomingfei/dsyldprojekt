package com.mmd.persistence;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class EstimationPersistence implements Serializable {
    private Integer uid;
    private Integer type;
    private Integer pid;
    private List<Integer> pids;
}
