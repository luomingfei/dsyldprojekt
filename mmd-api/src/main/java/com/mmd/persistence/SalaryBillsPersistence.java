package com.mmd.persistence;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SalaryBillsPersistence implements Serializable {
    private String tid;
}
