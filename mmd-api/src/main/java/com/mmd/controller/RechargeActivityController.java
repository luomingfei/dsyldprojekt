package com.mmd.controller;


import com.mmd.domain.dto.output.RechargeActivityOutputDTO;
import com.mmd.entity.GlobalResult;
import com.mmd.entity.RechargeActivity;
import com.mmd.service.RechargeActivityService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 充值活动表 前端控制器
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/rechargeActivity")
public class RechargeActivityController {

    private final RechargeActivityService rechargeActivityService;

    @GetMapping("/getRechargeActivity")
    @ApiOperation(value = "充值活动列表", notes = "充值活动列表")
    public GlobalResult getRechargeActivity () {
        List<RechargeActivity> rechargeActivities =  rechargeActivityService.getRechargeActivity();
        return GlobalResult.ok(rechargeActivities);
    }

    @GetMapping("/getRechargeActivity/{rid}")
    @ApiOperation(value = "充值活动详情", notes = "充值活动详情")
    public GlobalResult getRechargeActivityDetails (@PathVariable Integer rid) {
        RechargeActivityOutputDTO rechargeActivityOutputDTO =  rechargeActivityService.getRechargeActivityDetails(rid);
        return GlobalResult.ok(rechargeActivityOutputDTO);
    }

}

