package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.domain.dto.output.ManagerOutputDTO;
import com.mmd.entity.ManagerEntity;
import com.mmd.persistence.ManagerPersistence;

/**
 * <p>
 *用户表 服务类
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
public interface ManagerService extends IService<ManagerEntity> {

    IPage<ManagerOutputDTO> page(IPage page, ManagerPersistence managerPersistence);

}
