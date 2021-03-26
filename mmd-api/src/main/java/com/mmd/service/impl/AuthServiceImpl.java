package com.mmd.service.impl;

import com.mmd.domain.dto.user.UserInfoDTO;
import com.mmd.domain.dto.user.UserLoginDTO;
import com.mmd.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qiWen.xue
 * @date 2020-03-16 10:41
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {

    @Override
    public UserInfoDTO login(UserLoginDTO loginDTO, String openId, String unionId,String username) {

        // TODO 此处username为手机号码


        return null;
    }
}
