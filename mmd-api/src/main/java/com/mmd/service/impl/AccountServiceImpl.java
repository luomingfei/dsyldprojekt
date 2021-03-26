package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmd.entity.Account;
import com.mmd.dao.AccountMapper;
import com.mmd.entity.AccountTemp;
import com.mmd.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 账户表 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Override
    public Account myAccount(String uid) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        Account account = baseMapper.selectOne(queryWrapper);
        return account;
    }

    @Override
    public boolean createAccount(Account account) {
        if (baseMapper.createAccount(account) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Account queryAccountByAid(String aid) {
        return baseMapper.queryAccountByAid(aid);
    }

    @Override
    public boolean updateAccountBalance(AccountTemp temp) {
        if (baseMapper.updateAccountBalance(temp) > 0) {
            return true;
        }
        return false;
    }

}
