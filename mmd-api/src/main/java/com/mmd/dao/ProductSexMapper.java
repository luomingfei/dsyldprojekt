package com.mmd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.ProductSex;
import com.mmd.entity.TakeUpTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductSexMapper extends BaseMapper<ProductSex> {
    List<ProductSex> findProductSexByCityId(@Param("cityId") String cityId);

    ProductSex findProductSexByPid(@Param("pid") String pid);
}
