package com.mmd.domain.dto.wxPay;

import lombok.Data;

@Data
public class CustomizeRechargeInputDTO {
    private Integer uid;
    private String type;
    private String money;
}
