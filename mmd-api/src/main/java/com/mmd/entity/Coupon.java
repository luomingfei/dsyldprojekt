package com.mmd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 16:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

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
