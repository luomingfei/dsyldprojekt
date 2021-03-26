package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("fyt_promotion")
public class Promotion extends BaseEntity<Promotion> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品ID
     */
    private Integer pid;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 每个用户可享受次数
     */
    private Integer times;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 剩余数量
     */
    private Integer restcount;

    /**
     * 是否新用户
     */
    private String isnew;

    /**
     * 开始时间
     */
    private Integer begintime;

    /**
     * 结束时间
     */
    private Integer endtime;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Integer createtime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
