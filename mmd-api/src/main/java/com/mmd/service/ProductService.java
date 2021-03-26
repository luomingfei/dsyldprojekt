package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.domain.dto.input.ProductQueryInputDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.*;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface ProductService extends IService<Product> {

//    List<Product> findByCity(String cityId, int index,
//                             int limit, Integer isRecommend, String category);

    IPage<ProductOutputDTO> page(IPage page, ProductQueryInputDTO productPersistence);

    List<Promotion> findOnSalePromotion();

    List<LevelInfo> queryLevelInfoByPid(Long id);

    Integer getLjxdById(Long id);

    List<NumPromotion> findNumPromotionsByPid(Long id);

    ProductDTO findById(int pid);

    List<ProductTime> queryProductTimeList(int pid);

    List<String> findIdsByCity(String city);
}
