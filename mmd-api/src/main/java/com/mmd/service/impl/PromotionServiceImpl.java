package com.mmd.service.impl;

import com.mmd.entity.Promotion;
import com.mmd.dao.PromotionMapper;
import com.mmd.service.PromotionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {

    @Override
    public Promotion findOnSalePromotionByPid(String pid) {
        return baseMapper.findOnSalePromotionByPid(pid);
    }
}
