package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 验证码
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@Accessors(chain = true)
@TableName("fyt_verification_code")
public class VerificationCode {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String verificationCode;

    /**
     * 验证码
     */
    private String ip;

    /**
     * 创建时间
     */
    private Date createDate;

}
