package com.mmd.dao;

import com.mmd.domain.dto.comment.CustomService;
import com.mmd.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AdminMapper extends BaseMapper<Admin> {
    List<CustomService> findCustomServices();
}
