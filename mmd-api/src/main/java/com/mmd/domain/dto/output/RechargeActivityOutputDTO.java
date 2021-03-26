package com.mmd.domain.dto.output;


import com.mmd.entity.RechargeCoupon;
import lombok.Data;

import java.util.List;


@Data
public class RechargeActivityOutputDTO {

    private Integer id;

    private Integer realMoney;

    private Integer giftMoney;

    private String info;

    private Integer status;

    private Integer createTime;

    private List<RechargeCoupon> coupons;

}
