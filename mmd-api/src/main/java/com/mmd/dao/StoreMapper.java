package com.mmd.dao;

import com.mmd.domain.dto.output.TnsOutputDTO;
import com.mmd.entity.Product;
import com.mmd.entity.Store;
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
public interface StoreMapper extends BaseMapper<Store> {
    Store queryStoreById(@Param("sid") String sid);

    List<Integer> selectTnsIds(@Param("sid") Integer id);

    TnsOutputDTO findByMassagerId(String valueOf);

    List<Integer> getStoreTidsBySid(@Param("sid") Integer sid);

    List<Store> getStores4Product(@Param("cid") Integer cid, @Param("pid") Integer pid);

    List<Store> getStores4Massager(@Param("cid") Integer cid, @Param("tid") Integer tid);
}
