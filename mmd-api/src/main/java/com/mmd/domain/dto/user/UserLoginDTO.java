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
public class UserLoginDTO {
    /**
     * code
     */
    @NotEmpty(message="code不能为空")
    private String code;

    /**
     * 用户微信名
     */
    @NotEmpty(message="用户微信名不能为空")
    private String nickname;

    /**
     * 头像
     */
    @NotEmpty(message="头像不能为空")
    private String avatarUrl;

    /**
     * 用户手机号加密数据
     */
    @NotEmpty(message="用户手机号加密数据不能为空")
    private String encryptedData;

    /**
     * 用户手机号加密算法的初始向量
     */
    @NotEmpty(message="用户手机号加密算法的初始向量不能为空")
    private String iv;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 省份
     */
    private String province;
}
