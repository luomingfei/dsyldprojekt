package com.mmd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.entity.VerificationCode;

/**
 * <p>
 * 验证码 服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface VerificationCodeService extends IService<VerificationCode> {

    VerificationCode getMaxCodeByMobile(String mobile);

    int getMobileCount(String mobile);

    int getIpCount(String ip);

}
