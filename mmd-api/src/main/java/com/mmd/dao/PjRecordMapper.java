package com.mmd.dao;

import com.mmd.entity.PjRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 评价处理记录 Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface PjRecordMapper extends BaseMapper<PjRecord> {

    int addCommentRecord(@Param("pid") String pid, @Param("uid") String uid, @Param("type") Integer type);
}
