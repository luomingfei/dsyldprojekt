package com.mmd.domain.dto.wxPay;

import lombok.Data;

@Data
public class RechargeInputDTO {
    private Integer rid;
    private Integer uid;
    private Integer cid;
    private String type;
}
