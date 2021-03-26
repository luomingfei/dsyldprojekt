package com.mmd.service.impl;

import com.mmd.entity.LevelInfo;
import com.mmd.entity.MassagerTime;
import com.mmd.entity.TnsSj;
import com.mmd.dao.TnsSjMapper;
import com.mmd.service.TnsSjService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class TnsSjServiceImpl extends ServiceImpl<TnsSjMapper, TnsSj> implements TnsSjService {

    public MassagerTime findMassagerTimeByRqAndTid(long rq, int tid) {
        return baseMapper.findMassagerTimeByRqAndTid(rq, tid);
    }

    @Override
    public List<LevelInfo> queryLevelInfoByPid(String pid) {
        return baseMapper.queryLevelInfoByPid(pid);
    }

    @Override
    public boolean massagerLeave(MassagerTime time) {
        MassagerTime massagerTime = baseMapper.findMassagerTimeByRqAndTid(time.getRq(), time.getTid());
        int ret = 0;
        if (massagerTime == null) {
            ret = baseMapper.addMassagerTime(time);
        } else {
            ret = baseMapper.updateMassagerTime(time);
        }
        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }
}
