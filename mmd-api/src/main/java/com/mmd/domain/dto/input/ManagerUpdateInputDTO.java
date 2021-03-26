package com.mmd.domain.dto.input;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 用户信息修改dto
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
@Data
@Accessors(chain = true)
public class ManagerUpdateInputDTO implements Serializable {

    /**
     * 主键
     */
    @NotNull(message="主键ID不能为空")
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 修改人
     */
    private String updateBy;

}
