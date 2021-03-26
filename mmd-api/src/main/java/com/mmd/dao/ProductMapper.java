package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.input.ProductQueryInputDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface ProductMapper extends BaseMapper<Product> {

    IPage<ProductOutputDTO> getProductData(IPage page,@Param("pro") ProductQueryInputDTO productPersistence);

    List<Promotion> findOnSalePromotion();

    List<LevelInfo> queryLevelInfoByPid(@Param("pid") Long pid);

    Integer getLjxdById(@Param("id") Long id);

    List<NumPromotion> findNumPromotionsByPid(@Param("pid") Long pid);

    ProductDTO findById(@Param("id") int paramInt);

    ProductOutputDTO findByIdTwo(@Param("id") int paramInt);

    List<ProductDTO> findProductsByMassagerId(@Param("cityId") Integer cityid,
                                           @Param("sid") String sid);

    List<ProductTime> queryProductTimeList(@Param("pid") int pid);

    List<String> findIdsByCity(@Param("city") String city);
}
