package com.mmd.dao;

import com.mmd.entity.MassagerTime;
import com.mmd.entity.OrderInfo;
import com.mmd.entity.TakeUpTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MassagerMapper {
    List<OrderInfo> queryMassageOrderByDay(@Param("tid") String tid, @Param("stampOfDay") Long stampOfDay);

    List<TakeUpTime> getTimeRecord(@Param("tid") String tid);

    OrderInfo queryMassageOrderBylastDay(@Param("tid") String tid, @Param("stampOfDay") Long stampOfDay,boolean day);

    MassagerTime findMassagerTimeByRqAndTid(@Param("rq") long rq, @Param("tid") int tid);
}
