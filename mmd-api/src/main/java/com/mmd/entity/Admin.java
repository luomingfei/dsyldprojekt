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
@Accessors(chain = true)
@TableName("fyt_admin")
public class Admin  {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String nickname;

    /**
     * 用户码密
     */
    private String password;

    /**
     * 最后登录时间
     */
    private Integer lastlogintime;

    /**
     * 最后登录IP
     */
    private String lastloginip;

    /**
     * 登录次数
     */
    private Integer logincount;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Integer createtime;

    /**
     * 更新时间
     */
    private Integer updatetime;

    /**
     * 态状,1启用，0禁用
     */
    private Boolean status;

    /**
     * 城市ID
     */
    private Integer cityid;

    /**
     * 是否管理员
     */
    private String isadmin;

    private String precinct;

}
