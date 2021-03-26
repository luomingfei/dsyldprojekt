package com.mmd.dao;

import com.mmd.entity.LevelInfo;
import com.mmd.entity.MassagerTime;
import com.mmd.entity.TnsSj;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsSjMapper extends BaseMapper<TnsSj> {
    MassagerTime findMassagerTimeByRqAndTid(@Param("rq") long rq, @Param("tid") int tid);

    List<LevelInfo> queryLevelInfoByPid(@Param("pid") String pid);

    int addMassagerTime(@Param("time") MassagerTime massagerTime);

    int updateMassagerTime(@Param("time") MassagerTime massagerTime);
}
