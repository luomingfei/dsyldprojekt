package com.mmd.service;

import com.mmd.entity.ExtraHours;
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
public interface ExtrahoursService extends IService<ExtraHours> {
    List<ExtraHours> getExtraHours(String tid, String beginTime, String endTime);
}
