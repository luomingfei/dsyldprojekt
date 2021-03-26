package com.mmd.service;

import com.mmd.entity.LevelInfo;
import com.mmd.entity.MassagerTime;
import com.mmd.entity.TnsSj;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsSjService extends IService<TnsSj> {
    MassagerTime findMassagerTimeByRqAndTid(long rq, int tid);
    List<LevelInfo> queryLevelInfoByPid(String pid);
    boolean massagerLeave(MassagerTime time);
}
