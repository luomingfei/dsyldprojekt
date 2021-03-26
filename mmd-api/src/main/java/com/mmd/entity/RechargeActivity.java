package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 充值活动表
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@Accessors(chain = true)
@TableName("fyt_recharge_activity")
public class RechargeActivity {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("realMoney")
    private Integer realMoney;

    @TableField("giftMoney")
    private Integer giftMoney;

    @TableField("info")
    private String info;

    @TableField("status")
    private Integer status;

    @TableField("createTime")
    private Integer createTime;

}
