package com.mmd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.ProductMapper;
import com.mmd.dao.ProductSexMapper;
import com.mmd.entity.Product;
import com.mmd.entity.ProductSex;
import com.mmd.service.ProductService;
import com.mmd.service.ProductSexService;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSexServiceImpl extends ServiceImpl<ProductSexMapper, ProductSex> implements ProductSexService {
    @Override
    public List<ProductSex> findProductSexByCityId(String cityId) {
        return baseMapper.findProductSexByCityId(cityId);
    }

    @Override
    public ProductSex findProductSexByPid(String pid) {
        return baseMapper.findProductSexByPid(pid);
    }
}
