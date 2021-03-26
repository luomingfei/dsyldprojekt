package com.mmd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.VerificationCode;

/**
 * <p>
 * 验证码 Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {

    VerificationCode getMaxCodeByMobile(String mobile);

    int getMobileCount(String mobile);

    int getIpCount(String ip);

}
