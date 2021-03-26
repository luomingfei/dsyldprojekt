package com.mmd.service;

import com.mmd.entity.RechargeCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface RechargeCouponService extends IService<RechargeCoupon> {
    List<RechargeCoupon> queryRechargeCoupons(String rid);
}
