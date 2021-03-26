package com.mmd.service.impl;

import com.mmd.domain.dto.comment.CustomService;
import com.mmd.entity.Admin;
import com.mmd.dao.AdminMapper;
import com.mmd.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public List<CustomService> findCustomServices() {
        return baseMapper.findCustomServices();
    }
}
