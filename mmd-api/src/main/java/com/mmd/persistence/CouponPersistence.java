package com.mmd.persistence;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CouponPersistence implements Serializable {
    private  String userId;
}
