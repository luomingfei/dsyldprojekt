package com.mmd.service;

import com.mmd.domain.dto.output.RechargeActivityOutputDTO;
import com.mmd.entity.RechargeActivity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 充值活动表 服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface RechargeActivityService extends IService<RechargeActivity> {

    List<RechargeActivity> getRechargeActivity();

    RechargeActivityOutputDTO getRechargeActivityDetails(Integer rid);

    RechargeActivity queryRechargeByRid(String rid);

}
