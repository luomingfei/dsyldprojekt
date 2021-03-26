package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmd.dao.RechargeCouponMapper;
import com.mmd.domain.dto.output.RechargeActivityOutputDTO;
import com.mmd.entity.RechargeActivity;
import com.mmd.dao.RechargeActivityMapper;
import com.mmd.entity.RechargeCoupon;
import com.mmd.service.RechargeActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 充值活动表 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class RechargeActivityServiceImpl extends ServiceImpl<RechargeActivityMapper, RechargeActivity> implements RechargeActivityService {

    @Autowired
    private RechargeCouponMapper rechargeCouponMapper;

    @Override
    public List<RechargeActivity> getRechargeActivity() {
        QueryWrapper<RechargeActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","1");
        queryWrapper.orderByAsc("realMoney");
        List<RechargeActivity> activities = baseMapper.selectList(queryWrapper);
        return activities;
    }

    @Override
    public RechargeActivityOutputDTO getRechargeActivityDetails(Integer rid) {
        RechargeActivity rechargeActivity = baseMapper.selectById(rid);
        RechargeActivityOutputDTO rechargeActivityOutputDTO = new RechargeActivityOutputDTO();
        BeanUtils.copyProperties(rechargeActivity,rechargeActivityOutputDTO);
        rechargeActivityOutputDTO.setCoupons(rechargeCouponMapper.getRechargeCouponByRid(rid));
        return rechargeActivityOutputDTO;
    }

    @Override
    public RechargeActivity queryRechargeByRid(String rid) {
        return baseMapper.queryRechargeByRid(rid);
    }

}
