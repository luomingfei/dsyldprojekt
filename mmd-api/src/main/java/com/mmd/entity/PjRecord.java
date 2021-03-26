package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.mmd.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 评价处理记录
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("fyt_pj_record")
public class PjRecord extends BaseEntity<PjRecord> {

    private static final long serialVersionUID = 1L;

    private Integer pid;

    private Integer uid;

    /**
     * 0：无效，1：赞，2：踩
     */
    private Integer type;



}
