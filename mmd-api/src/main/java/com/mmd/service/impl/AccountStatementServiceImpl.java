package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.TimeUtil;
import com.mmd.dao.AccountMapper;
import com.mmd.domain.dto.input.AccountStatementQueryInputDTO;
import com.mmd.entity.Account;
import com.mmd.entity.AccountStatement;
import com.mmd.dao.AccountStatementMapper;
import com.mmd.service.AccountStatementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 账户流水表 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class AccountStatementServiceImpl extends ServiceImpl<AccountStatementMapper, AccountStatement> implements AccountStatementService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Integer createAccountStatement(AccountStatement as) {
        this.baseMapper.createAccountStatement(as);
        return as.getId();
    }

    @Override
    public void updateTradeNo(Integer id, String prepay_id) {
        baseMapper.updateTradeNo(id,prepay_id);
    }

    @Override
    public AccountStatement updateAndQueryAccountStatement(String aid) {
        baseMapper.updateAccountStatementStatus(aid, 2);
        return baseMapper.queryAccountStatement(aid);
    }

    @Override
    public List<Map<String, String>> page(IPage page, AccountStatementQueryInputDTO accountStatementQueryInputDTO) throws ParseException {
        String beginTime = "";
        String endTime = "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(sdf.parse(accountStatementQueryInputDTO.getBeginDay()));
        beginTime = (calendar.getTimeInMillis() / 1000) + "";
        calendar.setTime(sdf.parse(accountStatementQueryInputDTO.getEndDay()));
        endTime = (calendar.getTimeInMillis() / 1000 + 3600 * 24) + "";

        List<Map<String, String>> result = new ArrayList<>();
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",accountStatementQueryInputDTO.getUid());
        Account account = accountMapper.selectOne(queryWrapper);
        if (account == null) {
            return null;
        }
        IPage<AccountStatement> accountStatementIPage = baseMapper.queryAccountStatement4Account(page,account.getId(),beginTime,endTime);
        List<AccountStatement> statements = accountStatementIPage.getRecords();
        for (AccountStatement statement : statements) {
            if (statement.getRealchange() == null && statement.getGiftchange() == null) {
                continue;
            }
            Double money = Double.parseDouble(statement.getRealchange())
                    + Double.parseDouble(statement.getGiftchange()),
                    balance = Double.parseDouble(statement.getRealbalance())
                            + Double.parseDouble(statement.getGiftbalance());

            Map<String, String> sub = new HashMap<>();
            switch (statement.getOperatetype()) {
                case 11:
                    sub.put("type", "0");
                    sub.put("info", "支付");
                    sub.put("money", money.toString());
                    sub.put("balance", balance.toString());
                    sub.put("time", TimeUtil.long2String(statement.getOperatetime()));
                    break;
                case 12:
                    sub.put("type", "1");
                    sub.put("info", "充值");
                    sub.put("money", money.toString());
                    sub.put("balance", balance.toString());
                    sub.put("time", TimeUtil.long2String(statement.getOperatetime()));
                    break;
                case 13:
                    sub.put("type", "0");
                    sub.put("info", "提现");
                    sub.put("money", money.toString());
                    sub.put("balance", balance.toString());
                    sub.put("time", TimeUtil.long2String(statement.getOperatetime()));
                    break;
                case 14:
                    sub.put("type", "2");
                    sub.put("info", "异常处理");
                    sub.put("money", money.toString());
                    sub.put("balance", balance.toString());
                    sub.put("time", TimeUtil.long2String(statement.getOperatetime()));
                    break;
                case 15:
                    sub.put("type", "1");
                    sub.put("info", "退款");
                    sub.put("money", money.toString());
                    sub.put("balance", balance.toString());
                    sub.put("time", TimeUtil.long2String(statement.getOperatetime()));
                    break;
            }
            result.add(sub);
        }
        return result;
    }

    @Override
    public AccountStatement queryAccountStatementInCreate(String aid) {
        return baseMapper.queryAccountStatementInCreate(aid);
    }

    @Override
    public int updateAccountStatement4Recharge(AccountStatement as) {
        return baseMapper.updateAccountStatement4Recharge(as);
    }

    @Override
    public boolean updateAccountStatement(String aid, Integer status) {
        if (baseMapper.updateAccountStatementStatus(aid, status) > 0) {
            return true;
        }
        return false;
    }

}
