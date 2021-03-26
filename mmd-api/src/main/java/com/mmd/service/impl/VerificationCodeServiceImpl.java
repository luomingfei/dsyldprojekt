package com.mmd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.VerificationCodeMapper;
import com.mmd.entity.VerificationCode;
import com.mmd.service.VerificationCodeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 验证码 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements VerificationCodeService {


    @Override
    public VerificationCode getMaxCodeByMobile(String mobile) {
        return baseMapper.getMaxCodeByMobile(mobile);
    }

    @Override
    public int getMobileCount(String mobile) {
        return baseMapper.getMobileCount(mobile);
    }

    @Override
    public int getIpCount(String ip) {
        return baseMapper.getIpCount(ip);
    }
}
