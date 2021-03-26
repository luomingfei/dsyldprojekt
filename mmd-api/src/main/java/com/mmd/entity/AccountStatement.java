package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流水实体
 */
@Data
@Accessors(chain = true)
@TableName("fyt_account_statement")
public class AccountStatement {

    private Integer id;
    /**
     * 操作类型：1.微信在线支付，2.支付宝在线支付，3.微信退款，4.触宝支付, 5.触宝退款，11.余额支付，12.充值，13.提现，14.后台修改，15.退款
     */
    private Integer operatetype;
    private String operator;
    private Integer oid;
    private Long operatetime;
    private String realchange;
    private String giftchange;
    /**
     * 支付宝、微信等的交易号
     */
    private String realbalance;
    /**
     * 支付宝、微信等的交易号
     */
    private String giftbalance;
    /**
     * 支付宝、微信等的交易号
     */
    private Integer status;
    /**
     * 支付宝、微信等的交易号
     */
    private String tradeno;
    private String aid;
    private String rid;
}
