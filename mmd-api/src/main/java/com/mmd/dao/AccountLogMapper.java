package com.mmd.dao;

import com.mmd.entity.AccountLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AccountLogMapper extends BaseMapper<AccountLog> {
    int saveAccountLog(@Param("l") AccountLog log);
}
