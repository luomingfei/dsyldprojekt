package com.mmd.persistence;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class OrderPersistence implements Serializable {
    private String userId;
    private String zt;
    private Integer tid;
}
