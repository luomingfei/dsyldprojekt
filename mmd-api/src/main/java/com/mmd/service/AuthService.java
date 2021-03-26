package com.mmd.service;

import com.mmd.domain.dto.user.UserInfoDTO;
import com.mmd.domain.dto.user.UserLoginDTO;

/**
 * @author qiWen.xue
 * @date 2020-03-16 10:41
 */
public interface AuthService {
    /**
     * 登录
     *
     * @param loginDTO
     * @param openId
     * @param unionId
     * @return UserInfoDTO
     * @author qiWen.xue
     * @date 2020-03-16 10:43
     */
    UserInfoDTO login(UserLoginDTO loginDTO, String openId, String unionId,String username);
}
