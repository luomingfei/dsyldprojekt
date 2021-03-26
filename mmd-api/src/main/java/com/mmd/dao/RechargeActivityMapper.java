package com.mmd.dao;

import com.mmd.entity.RechargeActivity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 充值活动表 Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface RechargeActivityMapper extends BaseMapper<RechargeActivity> {
    RechargeActivity queryRechargeByRid(@Param("rid") String rid);
}
