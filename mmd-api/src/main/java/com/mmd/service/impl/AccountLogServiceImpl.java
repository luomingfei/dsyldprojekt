package com.mmd.service.impl;

import com.mmd.entity.AccountLog;
import com.mmd.dao.AccountLogMapper;
import com.mmd.service.AccountLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class AccountLogServiceImpl extends ServiceImpl<AccountLogMapper, AccountLog> implements AccountLogService {
    @Override
    public void saveAccountLog(AccountLog log) {
        baseMapper.saveAccountLog(log);
    }
}
