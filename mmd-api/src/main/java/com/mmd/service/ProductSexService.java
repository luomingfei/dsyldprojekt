package com.mmd.service;

import com.mmd.entity.ProductSex;

import java.util.List;

/**
 * dm
 */
public interface ProductSexService {
    List<ProductSex> findProductSexByCityId(String cityId);

    ProductSex findProductSexByPid(String pid);
}
