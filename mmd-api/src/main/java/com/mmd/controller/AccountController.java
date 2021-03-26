package com.mmd.controller;


import com.mmd.domain.dto.input.AccountStatementQueryInputDTO;
import com.mmd.entity.Account;
import com.mmd.entity.GlobalResult;
import com.mmd.service.AccountService;
import com.mmd.service.AccountStatementService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 账户表 前端控制器
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final AccountStatementService accountStatementService;

    @GetMapping("/myAccount/{userId}")
    @ApiOperation(value = "账户信息", notes = "账户信息")
    public GlobalResult myAccount (@PathVariable String userId) {
        Account account = accountService.myAccount(userId);
        if (account == null || account.getStatus() == 1) {
            return GlobalResult.ok(account);
        }
        return GlobalResult.errorMsg("账号异常");
    }

    @ApiOperation(value = "分页查询充值记录", notes = "根据多条件分页查询充值信息")
    @ApiImplicitParam(name = "accountStatementQueryInputDTO", value = "充值信息页查询参数信息", required = true, paramType = "body", dataType = "AccountStatementQueryInputDTO", dataTypeClass = AccountStatementQueryInputDTO.class)
    @PostMapping("/page")
    public GlobalResult pageList(@RequestBody AccountStatementQueryInputDTO accountStatementQueryInputDTO) {
        List<Map<String, String>> accountStatementIPage = null;
        try {
            accountStatementIPage = accountStatementService.page(accountStatementQueryInputDTO.makePaging(), accountStatementQueryInputDTO);
        } catch (ParseException e) {
            return GlobalResult.errorMsg("日期错误");
        }
        return GlobalResult.ok(accountStatementIPage);
    }

}

