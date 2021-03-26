package com.mmd.service.impl;

import com.mmd.entity.City;
import com.mmd.dao.CityMapper;
import com.mmd.service.CityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Override
    public boolean findCityById(String cityId) {
        City city = this.baseMapper.selectByCityId(cityId);
        return city != null ? true : false;
    }
}
