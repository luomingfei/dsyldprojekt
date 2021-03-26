package com.mmd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.TakeUpTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TakeUpTimeMapper extends BaseMapper<TakeUpTime> {
    List<TakeUpTime> getTimeRecord(@Param("tid") String tid);

    int addTakeUpTime(@Param("t") TakeUpTime takeUpTime);

    int deleteTakeUpTime(@Param("id") int id);
}
