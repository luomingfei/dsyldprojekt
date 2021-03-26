package com.mmd.dao;

import com.mmd.entity.ExtraHours;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface ExtrahoursMapper extends BaseMapper<ExtraHours> {
    List<ExtraHours> getExtraHours(@Param("tid") String tid, @Param("beginTime") String beginTime,
                                   @Param("endTime") String endTime);
}
