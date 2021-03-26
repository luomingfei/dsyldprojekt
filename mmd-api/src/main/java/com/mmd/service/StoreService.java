package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.domain.dto.input.StoreQueryInputDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.domain.dto.output.StoreOutputDTO;
import com.mmd.domain.dto.output.TnsOutputDTO;
import com.mmd.entity.Store;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface StoreService extends IService<Store> {
    Store queryStoreById(String sid);

    IPage<StoreOutputDTO> page(IPage page,StoreQueryInputDTO storeQueryInputDTO);

    List<Integer> getStoreTidsBySid(Integer sid);

    List<TnsOutputDTO> page(Integer id);

    List<ProductOutputDTO> getProduct(Integer id);

    List<Store> getStores(Integer pid, Integer tid,Integer cid);
}
