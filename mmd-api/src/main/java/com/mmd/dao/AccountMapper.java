package com.mmd.dao;

import com.mmd.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.AccountTemp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 账户表 Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AccountMapper extends BaseMapper<Account> {

    int createAccount(@Param("a") Account account);

    Account queryAccountByAid(@Param("aid") String aid);

    int updateAccountBalance(@Param("t") AccountTemp temp);
}
