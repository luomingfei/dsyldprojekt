package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@TableName("fyt_store")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Store  {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String photo;

    @TableField(value = "coord")
    private String coord;

    private Integer status;

    private String info;

    private String phone;

    private Integer cover;

    private String address;

    private String city;

    private Integer level;

    private String pids;

    /**
     * 店长ID（关联技师ID）
     */
    @TableField("managerId")
    private Integer managerId;

}
