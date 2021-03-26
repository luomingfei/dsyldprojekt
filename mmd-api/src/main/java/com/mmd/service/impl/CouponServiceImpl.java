package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.dao.OrderMapper;
import com.mmd.domain.dto.input.CouponQueryInputDTO;
import com.mmd.domain.dto.order.CouponDTO;
import com.mmd.domain.dto.output.CouponOutputDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.Coupon;
import com.mmd.dao.CouponMapper;
import com.mmd.entity.Order;
import com.mmd.persistence.CouponPersistence;
import com.mmd.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
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
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    private  final  CouponMapper couponMapper;
    private final OrderMapper orderMapper;

    @Override
    public CouponDTO getCouponByOid(String s) {
        return couponMapper.getCouponByOid(s);
    }

    @Override
    public IPage<CouponOutputDTO> findCouponsByUserId(Page page, CouponPersistence couponPersistence) {
        return baseMapper.findCouponsById(page, couponPersistence.getUserId());
    }

    @Override
    public List<CouponOutputDTO> findCouponsCanUse(String uid, String pid, double zje, String oid) {
        List<CouponOutputDTO> couponOutputDTOS = couponMapper.findCouponsCanUse(uid, pid, zje);
        if (oid != null) {
            Order order = orderMapper.FindOrderById(Integer.parseInt(oid));
            String[] sjArr = order.getSj().split(":");
            int serviceDot = Integer.parseInt(sjArr[0]) * 2 + (Integer.parseInt(sjArr[1]) < 30 ? 0 : 1);
            int serviceRq = Integer.parseInt(order.getRq().toString());
            Iterator<CouponOutputDTO> iterator = couponOutputDTOS.iterator();
            while (iterator.hasNext()) {
                CouponOutputDTO couponOutputDTO = iterator.next();
                if (couponOutputDTO.getBegin() > serviceDot || couponOutputDTO.getEnd() < serviceDot) {
                    iterator.remove();
                    continue;
                }
                if(couponOutputDTO.getBeginDay() != null && couponOutputDTO.getEndDay() != null) {
                    if(couponOutputDTO.getBeginDay() > serviceRq || couponOutputDTO.getEndDay() < serviceRq) {
                        iterator.remove();
                        continue;
                    }
                }
            }
        }
        return couponOutputDTOS;
    }

    @Override
    public Coupon getCouponById(Integer id) {
        return baseMapper.getCouponById(id);
    }

    @Override
    public int updateCouponAfterPay(String cid, String oid) {
        return baseMapper.updateCouponAfterPay(cid,oid);
    }


}
