package com.mmd.service;

import com.mmd.entity.Qy;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface QyService extends IService<Qy> {
    boolean isServiceArea(String city, String district);
}
