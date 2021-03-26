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
@TableName("fyt_address")
public class Address {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("addressInfomation")
    private String addressInfomation;

    @TableField("addressDetail")
    private String addressDetail;

    private String contact;

    private Integer uid;

    /**
     * 门牌号
     */
    private String mph;

    private String phone;

    private String lng;

    private String lat;

    /**
     * 地址类型：0 隐藏；1 正常；2 到店
     */
    private Integer type;

    /**
     * 创建来源
     */
    private String way;


}
