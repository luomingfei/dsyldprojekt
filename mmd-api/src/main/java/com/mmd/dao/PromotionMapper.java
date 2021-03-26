package com.mmd.dao;

import com.mmd.entity.Promotion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface PromotionMapper extends BaseMapper<Promotion> {
    Promotion findOnSalePromotionByPid(@Param("pid") String pid);
}
