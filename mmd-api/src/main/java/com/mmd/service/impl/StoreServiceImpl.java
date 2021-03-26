package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.ProductMapper;
import com.mmd.dao.StoreMapper;
import com.mmd.domain.dto.input.StoreQueryInputDTO;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.domain.dto.output.StoreOutputDTO;
import com.mmd.domain.dto.output.TnsOutputDTO;
import com.mmd.entity.Store;
import com.mmd.service.StoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Store queryStoreById(String sid) {
        return baseMapper.queryStoreById(sid);
    }

    @Override
    public IPage<StoreOutputDTO> page(IPage page,StoreQueryInputDTO storeQueryInputDTO) {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "0");
        queryWrapper.eq(!StringUtils.isEmpty(storeQueryInputDTO.getCity()),"city", storeQueryInputDTO.getCity());
        queryWrapper.like(!StringUtils.isEmpty(storeQueryInputDTO.getName().trim()),"name", storeQueryInputDTO.getName());
        IPage<StoreOutputDTO> iPage = this.baseMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    public List<TnsOutputDTO> page(Integer id) {
        List<TnsOutputDTO> list = new ArrayList<>();
        Store store = this.baseMapper.selectById(id);
        if (store != null) {
            String pids = store.getPids();
            String managerId = String.valueOf(store.getManagerId());
            // 获取当前门店所有技师
            List<Integer> tnsIds = this.baseMapper.selectTnsIds(id);
            if (tnsIds != null && tnsIds.size() != 0) {
                tnsIds.forEach(
                        l -> {
                            TnsOutputDTO tns = this.baseMapper.findByMassagerId(String.valueOf(l));
                            if(tns!=null){
                                if (StringUtils.isNotBlank(managerId) && Objects.equals(managerId, l)) {
                                    // 当前技师为店长
                                    tns.setFlag(true);
                                }
                                list.add(tns);
                            }
                        }
                );
            }
        }
        return list;
    }

    @Override
    public List<ProductOutputDTO> getProduct(Integer id) {
        Store store = this.baseMapper.selectById(id);
        List<ProductOutputDTO> list = new ArrayList<>();
        if (store != null) {
            String pids = store.getPids();
            if (StringUtils.isNotBlank(pids)) {
                // 项目
                String[] pid = pids.split(",");
                Arrays.stream(pid).forEach(
                        l -> {
                            ProductOutputDTO pro = productMapper.findByIdTwo(Integer.parseInt(l));
                            if(pro!=null){
                                list.add(pro);
                            }
                        }
                );
            }
        }
        return list;
    }

    @Override
    public List<Store> getStores(Integer pid, Integer tid,Integer cityId) {
        List<Store> stores = null;
        if (tid == null || tid == 0) {
            //对项目下单
            stores = baseMapper.getStores4Product(cityId, pid);
        } else {
            //对技师下单
            stores = baseMapper.getStores4Massager(cityId, tid);
        }
        return stores;
    }

    @Override
    public List<Integer> getStoreTidsBySid(Integer sid) {
        return baseMapper.getStoreTidsBySid(sid);
    }
}
