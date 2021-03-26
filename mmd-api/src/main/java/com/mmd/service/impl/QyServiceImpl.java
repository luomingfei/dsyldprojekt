package com.mmd.service.impl;

import com.mmd.entity.Qy;
import com.mmd.dao.QyMapper;
import com.mmd.service.QyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class QyServiceImpl extends ServiceImpl<QyMapper, Qy> implements QyService {

    @Override
    public boolean isServiceArea(String city, String district) {
        if (baseMapper.getServiceArea(city, district) == null)
            return false;
        return true;
    }
}
