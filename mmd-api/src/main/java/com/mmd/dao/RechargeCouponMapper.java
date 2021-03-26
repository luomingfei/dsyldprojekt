package com.mmd.dao;

import com.mmd.entity.RechargeCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface RechargeCouponMapper extends BaseMapper<RechargeCoupon> {

    List<RechargeCoupon> getRechargeCouponByRid(@Param("rid") Integer rid);


}
