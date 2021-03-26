package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.ManagerMapper;
import com.mmd.domain.dto.output.ManagerOutputDTO;
import com.mmd.entity.ManagerEntity;
import com.mmd.persistence.ManagerPersistence;
import com.mmd.service.ManagerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, ManagerEntity> implements ManagerService {

    @Override
    public IPage<ManagerOutputDTO> page(IPage page, ManagerPersistence managerPersistence) {
        String userName = managerPersistence.getUserName();
        String realName = managerPersistence.getRealName();
        QueryWrapper<ManagerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(userName), "user_name", userName)
                .like(!StringUtils.isEmpty(realName), "real_name", realName);
        IPage<ManagerOutputDTO> managerOutputDTOIPage = baseMapper.selectPage(page, queryWrapper);
        return managerOutputDTOIPage;
    }

}
