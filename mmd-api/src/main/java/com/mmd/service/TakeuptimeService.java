package com.mmd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.entity.TakeUpTime;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TakeuptimeService extends IService<TakeUpTime> {
    List<TakeUpTime> getTimeRecord(String tid);

    boolean addTakeUpTime(TakeUpTime takeUpTime);

    boolean deleteTakeUpTime(int id);

    boolean updateTakeUpTime(TakeUpTime takeUpTime);
}
