package com.mmd.domain.dto.input;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 用户信息新增dto
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
@Data
@Accessors(chain = true)
public class ManagerAddInputDTO implements Serializable {

    /**
     * 用户名
     */
    @NotEmpty(message="用户名不能为空")
    private String userName;

    /**
     * 密码
     */
    @NotEmpty(message="密码不能为空")
    private String password;

    /**
     * 真实姓名
     */
    @NotEmpty(message="真实姓名不能为空")
    private String realName;

    /**
     * 手机号
     */
    @NotEmpty(message="手机号不能为空")
    private String phone;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

}
