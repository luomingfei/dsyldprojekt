package com.mmd.service;

import com.mmd.entity.Promotion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface PromotionService extends IService<Promotion> {
    Promotion findOnSalePromotionByPid(String pid);
}
