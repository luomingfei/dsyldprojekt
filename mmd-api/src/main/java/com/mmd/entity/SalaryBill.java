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
//@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("fyt_salary_bill")
//extends BaseEntity<SalaryBill>
public class SalaryBill  {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer tid;

    @TableField("beginDate")
    private Integer beginDate;

    @TableField("endDate")
    private Integer endDate;

    private Integer status;

    @TableField("orderTime")
    private BigDecimal orderTime;

    @TableField("orderMoney")
    private BigDecimal orderMoney;

    /**
     * 推广时长
     */
    @TableField("extraTime")
    private BigDecimal extraTime;

    /**
     * 推广收入
     */
    @TableField("extraMoney")
    private BigDecimal extraMoney;

    private BigDecimal reward;

    /**
     * 诊断奖励
     */
    @TableField("diagnosisMoney")
    private BigDecimal diagnosisMoney;

    /**
     * 评论奖励
     */
    @TableField("commentMoney")
    private BigDecimal commentMoney;

    @TableField("commentRemark")
    private String commentRemark;

    /**
     * 补贴
     */
    private BigDecimal subsidy;

    @TableField("subsidyRemark")
    private String subsidyRemark;

    /**
     * 总工资
     */
    @TableField("sumSalary")
    private BigDecimal sumSalary;

    /**
     * 扣费
     */
    private BigDecimal deposit;

    @TableField("depositRemark")
    private String depositRemark;

    /**
     * 创建时间
     */
    private Integer createtime;

    /**
     * 技师确认时间
     */
    private Integer confirmtime;



}
