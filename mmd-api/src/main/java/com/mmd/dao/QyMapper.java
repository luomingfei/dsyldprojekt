package com.mmd.dao;

import com.mmd.entity.Qy;
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
public interface QyMapper extends BaseMapper<Qy> {
    Integer getServiceArea(@Param("city") String city, @Param("district") String district);
}
