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
@TableName("fyt_tns")
public class Tns  {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String xm;

    /**
     * 性别
     */
    private String xb;

    /**
     * 位置
     */
    private String wz;

    /**
     * 头像
     */
    private String tx;

    /**
     * 服务区域
     */
    private String fwqy;

    /**
     * 工作经验
     */
    private String gzjy;

    /**
     * y显示,n隐藏,m满约
     */
    private String status;

    private Integer createtime;

    /**
     * 年龄
     */
    private String age;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 城市
     */
    private String city;

    /**
     * 位置坐标信息
     */
    private String location;

    private String cylx;

    private String sfsm;

    private String openid;

    private String sid;

    private Integer ljxd;

    /**
     * 每小时工资
     */
    private Integer hoursalary;

    /**
     * 保底工资
     */
    private Integer minsalary;

    /**
     * 0:兼职 1：全职
     */
    private String type;

    private String password;

    /**
     * 推拿师星级
     */
    private Integer level;

    private Integer uid;

    /**
     * 接单范围，单位公里，0表示不限制
     */
    private BigDecimal orderrange;

    @TableField("laterDot")
    private Integer laterDot;

    @TableField("laterRange")
    private BigDecimal laterRange;

    /**
     * 是否黑名单
     */
    private Integer isblack;

    private String profession;

    @TableField("inRemark")
    private String inRemark;

    @TableField("isRecommend")
    private Integer isRecommend;



}
