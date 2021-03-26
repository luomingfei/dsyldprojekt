package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.input.AccountStatementQueryInputDTO;
import com.mmd.entity.AccountStatement;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 账户流水表 服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AccountStatementService extends IService<AccountStatement> {

    Integer createAccountStatement(AccountStatement as);

    void updateTradeNo(Integer id, String prepay_id);

    AccountStatement updateAndQueryAccountStatement(@Param("aid") String aid);

    List<Map<String, String>> page(IPage page, AccountStatementQueryInputDTO accountStatementQueryInputDTO) throws ParseException;

    AccountStatement queryAccountStatementInCreate(String aid);

    int updateAccountStatement4Recharge(AccountStatement as);

    boolean updateAccountStatement(String aid, Integer status);

}
