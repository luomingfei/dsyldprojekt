package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.input.CouponQueryInputDTO;
import com.mmd.domain.dto.order.CouponDTO;
import com.mmd.domain.dto.output.CouponOutputDTO;
import com.mmd.entity.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.persistence.CouponPersistence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface CouponMapper extends BaseMapper<Coupon> {

    CouponDTO getCouponByOid(String s);

    IPage<CouponOutputDTO> findCouponsById(Page page, @Param("userId") String userId);

    List<CouponOutputDTO> findCouponsCanUse(@Param("uid") String uid, @Param("pid") String pid, @Param("zje") double zje);

    Coupon getCouponById(Integer id);

    int updateCouponAfterPay(@Param("cid") String cid, @Param("oid") String oid);
}
