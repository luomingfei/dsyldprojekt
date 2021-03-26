package com.mmd.entity;

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
@TableName("fyt_tns_pj")
public class TnsPj {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会员ID
     */
    private Integer uid;

    /**
     * 推拿师id
     */
    private Integer tid;

    /**
     * 评价类型 1好评 2中评 3差评
     */
    private Integer pjlx;

    /**
     * 时间
     */
    private Integer createtime;

    private String pjnr;

    private Integer oid;

    private Integer pjlevel;

    private Integer skill;

    private Integer attitude;

    @TableField("onTime")
    private Integer onTime;

    /**
     * 支持数
     */
    private Integer support;

    private Integer updatetime;



}
