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
 * 
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("fyt_tns_photo")
public class TnsPhoto extends BaseEntity<TnsPhoto> {

    private static final long serialVersionUID=1L;


    private Integer tid;

    private String url;

    /**
     * 照片展示状态：y展示，n不展示
     */
    private String status;




}
