package com.mmd.domain.dto.order;

import lombok.Data;

@Data
public class CouponDTO {
    private int id;
    private String title;
    private String type;
    private String createdAt;
    private String expireAt;
    private String money;
    private String uid;
    private String oid;
    private String pid;
    private String comment;
    private String minPrice;
    private String state;
    private String code;
    private String expireDay;
    private String tid;
    private Integer begin;
    private Integer end;
    private Integer beginDay;
    private Integer endDay;
}
