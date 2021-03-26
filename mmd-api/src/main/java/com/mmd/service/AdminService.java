package com.mmd.service;

import com.mmd.domain.dto.comment.CustomService;
import com.mmd.entity.Admin;
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
public interface AdminService extends IService<Admin> {
    List<CustomService> findCustomServices();
}
