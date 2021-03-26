package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.input.CouponQueryInputDTO;
import com.mmd.domain.dto.order.CouponDTO;
import com.mmd.domain.dto.output.CouponOutputDTO;
import com.mmd.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.persistence.CouponPersistence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface CouponService extends IService<Coupon> {

    CouponDTO getCouponByOid(String s);

    IPage<CouponOutputDTO> findCouponsByUserId(Page page, CouponPersistence couponPersistence);

    List<CouponOutputDTO> findCouponsCanUse(String uid, String pid, double zje, String oid);

    Coupon getCouponById(Integer id);

    int updateCouponAfterPay(String cid, String oid);
}
