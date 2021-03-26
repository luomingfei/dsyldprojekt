package com.mmd.domain.dto.order;

import lombok.Data;

@Data
public class PromotionDTO {
    private int id;
    private int pid;
    //优惠金额
    private double discount;
    //每个用户可享受次数
    private int times;
    //数量
    private int count;
    //剩余数量
    private int restcount;
    //是否限制新用户
    private String isnew;
    //开始时间
    private long begintime;
    //结束时间
    private long endtime;
    //状态
    private String status;
    //创建时间
    private long createtime;
}
