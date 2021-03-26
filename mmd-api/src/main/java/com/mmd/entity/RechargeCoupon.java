package com.mmd.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.mmd.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@Accessors(chain = true)
@TableName("fyt_recharge_coupon")
public class RechargeCoupon{

    private static final long serialVersionUID=1L;

    private Integer id;

    private Integer rid;

    private BigDecimal minPrice;

    private BigDecimal money;

    private Integer expireDay;

    private Integer count;

    private String pids;

    private String products;

    private String comment;


}
