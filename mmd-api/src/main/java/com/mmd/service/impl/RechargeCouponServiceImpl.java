package com.mmd.service.impl;

import com.mmd.entity.RechargeCoupon;
import com.mmd.dao.RechargeCouponMapper;
import com.mmd.service.RechargeCouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class RechargeCouponServiceImpl extends ServiceImpl<RechargeCouponMapper, RechargeCoupon> implements RechargeCouponService {

    @Override
    public List<RechargeCoupon> queryRechargeCoupons(String rid) {
        return baseMapper.getRechargeCouponByRid(Integer.valueOf(rid));
    }
}
