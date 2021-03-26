package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.entity.AccountStatement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 账户流水表 Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AccountStatementMapper extends BaseMapper<AccountStatement> {

    void createAccountStatement(@Param("as") AccountStatement as);

    void updateTradeNo(@Param("id") Integer id, @Param("tradeno") String prepay_id);

    int updateAccountStatementStatus(@Param("aid") String aid, @Param("status") int status);

    AccountStatement queryAccountStatement(@Param("aid") String aid);

    Page<AccountStatement> queryAccountStatement4Account(IPage page, @Param("aid") Integer aid,@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    AccountStatement queryAccountStatementInCreate(@Param("aid") String aid);

    int updateAccountStatement4Recharge(@Param("as") AccountStatement as);
}