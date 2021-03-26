package com.mmd.dao;

import com.mmd.entity.City;
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
public interface CityMapper extends BaseMapper<City> {

    Integer findCityById(String cityId);

    City selectByCityId(@Param("id") String cityId);
}
