package com.mmd.service;

import com.mmd.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.entity.AccountTemp;

/**
 * <p>
 * 账户表 服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AccountService extends IService<Account> {

    Account myAccount(String uid);

    boolean createAccount(Account account);

    Account queryAccountByAid(String aid);

    boolean updateAccountBalance(AccountTemp temp);
}
