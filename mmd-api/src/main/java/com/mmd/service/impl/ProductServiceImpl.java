package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.ProductMapper;
import com.mmd.domain.dto.input.ProductQueryInputDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.*;
import com.mmd.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<ProductOutputDTO> page(IPage page, ProductQueryInputDTO productPersistence) {
        return this.baseMapper.getProductData(page, productPersistence);
    }

    @Override
    public List<Promotion> findOnSalePromotion() {
        return this.baseMapper.findOnSalePromotion();
    }

    @Override
    public List<LevelInfo> queryLevelInfoByPid(Long pid) {
        return this.baseMapper.queryLevelInfoByPid(pid);
    }

    @Override
    public Integer getLjxdById(Long id) {
        return this.baseMapper.getLjxdById(id);
    }

    @Override
    public List<NumPromotion> findNumPromotionsByPid(Long pid) {
        return this.baseMapper.findNumPromotionsByPid(pid);
    }

    @Override
    public ProductDTO findById(int pid) {
        return baseMapper.findById(pid);
    }

    @Override
    public List<ProductTime> queryProductTimeList(int pid) {
        return  baseMapper.queryProductTimeList(pid);
    }

    @Override
    public List<String> findIdsByCity(String city) {
        return baseMapper.findIdsByCity(city);
    }
}
