package com.mmd.service.impl;

import com.mmd.dao.TakeUpTimeMapper;
import com.mmd.entity.TakeUpTime;
import com.mmd.service.TakeuptimeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
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
public class TakeuptimeServiceImpl extends ServiceImpl<TakeUpTimeMapper, TakeUpTime> implements TakeuptimeService {

    @Override
    public List<TakeUpTime> getTimeRecord(String tid) {
        return baseMapper.getTimeRecord(tid);
    }

    @Override
    public boolean addTakeUpTime(TakeUpTime takeUpTime) {
        return baseMapper.addTakeUpTime(takeUpTime) > 0 ? true : false;
    }

    @Override
    public boolean deleteTakeUpTime(int id) {

        return baseMapper.deleteTakeUpTime(id) > 0 ? true : false;
    }

    @Override
    public boolean updateTakeUpTime(TakeUpTime takeUpTime) {
        return baseMapper.updateById(takeUpTime) > 0 ? true : false;
    }
}
