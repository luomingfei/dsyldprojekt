package com.mmd.service.impl;

import com.mmd.entity.ExtraHours;
import com.mmd.dao.ExtrahoursMapper;
import com.mmd.service.ExtrahoursService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class ExtrahoursServiceImpl extends ServiceImpl<ExtrahoursMapper, ExtraHours> implements ExtrahoursService {

    @Override
    public List<ExtraHours> getExtraHours(String tid, String beginTime, String endTime) {
        return baseMapper.getExtraHours(tid, beginTime, endTime);
    }
}
