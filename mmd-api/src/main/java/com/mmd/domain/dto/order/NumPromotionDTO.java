package com.mmd.domain.dto.order;

import lombok.Data;

@Data
public class NumPromotionDTO {
    private int id;
    private int pid;
    //数量
    private int number;
    //优惠金额
    private double discount;
    //状态
    private String status;
    //创建时间
    private long createtime;
}
