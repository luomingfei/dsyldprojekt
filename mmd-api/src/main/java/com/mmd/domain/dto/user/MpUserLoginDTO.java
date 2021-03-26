package com.mmd.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author qiWen.xue
 * @date 2020-03-09 20:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpUserLoginDTO {

    @NotEmpty(message="验证码不能为空")
    private String code;

    @NotEmpty(message="openid不能为空")
    private String openid;

    @NotEmpty(message="手机号不能为空")
    private String mobile;


}
