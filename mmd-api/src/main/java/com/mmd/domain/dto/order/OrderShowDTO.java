package com.mmd.domain.dto.order;

import lombok.Data;

@Data
public class OrderShowDTO {
    private int id;
    private int tid;
    private int sfpj;
    private String tns_name;
    private String tns_phone;
    private String tp;
    private int pid;
    private String product_name;
    private String money;
    private String zt;
    private String createtime;
    private String paymenttype;
    private int procuct_sl;
    private String contact;
    private String phone;
    private String address;
    private String remark;
    private int cancelflag;
    private String order_sj;
    private String mph;
    private String yjjtmoney;
    private String promotionmoney;
    private String settleprice;
    private String couponMoney;
    private String levelmoney;
    private Integer massagelevel;
    private String city;
    private Integer productType;
    private Integer complete;
    private String uid;
    private int isDiagnosis;
    private Double lat;
    private Double lng;
    private String massagersex;
    private String sexmoney;
    private String numdiscount;
}
