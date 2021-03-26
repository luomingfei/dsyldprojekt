package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.TimeUtil;
import com.mmd.dao.*;
import com.mmd.domain.dto.massagist.PhotoDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.entity.City;
import com.mmd.entity.LevelRefer;
import com.mmd.entity.Massager;
import com.mmd.entity.Tns;
import com.mmd.service.TnsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.utils.Distance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TnsServiceImpl extends ServiceImpl<TnsMapper, Tns> implements TnsService {
    private final TnsPjMapper tnsPjMapper;
    private final TnsMapper tnsMapper;
    private final ProductMapper productMapper;
    private final TnsPhotoMapper tnsPhotoMapper;
    private final CityMapper cityMapper;

    @Override
    public MassagerDTO findById(String s) {
        return this.baseMapper.findById(s);
    }

    @Override
    public EstimationDTO findPjByOrderId(int id) {
        return tnsPjMapper.findPjByOrderId(id);
    }

    @Override
    public List<ProductDTO> findProductsByMassagerId(Integer cityId, String massagerId) {
        String sid = tnsMapper.findSidByMassagerId(massagerId);
        List<ProductDTO> products = new ArrayList<ProductDTO>();
        if (sid != null && !"".equals(sid)) {
            products = productMapper.findProductsByMassagerId(cityId, sid);
        }
        return products;
    }

    @Override
    public List<PhotoDTO> findPhotosOfMassager(String massagerId) {
        MassagerDTO massagerDTO = tnsMapper.findById(massagerId);
        PhotoDTO photoDTO = new PhotoDTO();
        photoDTO.setId(0);
        photoDTO.setTid(Integer.parseInt(massagerId));
        photoDTO.setStatus("y");
        photoDTO.setUrl(massagerDTO.getTx());
        List<PhotoDTO> result = tnsPhotoMapper.findPhotosOfMassager(massagerId);
        result.add(0, photoDTO);
        return result;
    }

    @Override
    public List<Massager> findByCityAndKeyword(String cityId, String keyword, String type,Integer tid) throws Exception {
        City city = cityMapper.selectByCityId(cityId);
        if (city == null) {
            throw new Exception("没有该城市");
        }

        List<Massager> massagerDTOS = tnsMapper.findByCityIdAndKeyword(cityId, keyword, type,tid);

        return massagerDTOS;
    }

    @Override
    public List<Massager> getMyMassagers4JiaShiNolevelNosex(String uid) {
        return tnsMapper.getMyMassagers4JiaShiNolevelNosex(uid);
    }

    @Override
    public List<Massager> getMyMassagers4JiaShiNolevelSex(String uid, String sex) {
        return tnsMapper.getMyMassagers4JiaShiNolevelSex(uid, sex);
    }

    @Override
    public List<Massager> getMyMassagers4JiaShiLevelNosex(String uid, int level) {
        return tnsMapper.getMyMassagers4JiaShiLevelNosex(uid, level);
    }

    @Override
    public List<Massager> getMyMassagers4JiaShiLevelSex(String uid, int level, String sex) {
        return tnsMapper.getMyMassagers4JiaShiNolevelSex(uid, sex);
    }

    @Override
    public List<Massager> getMyMassagersNolevelNosex(String userId, int pid, double lat, double lng, long beginTime) {
        List<Massager> massagers = baseMapper.getMyMassagersNolevelNosex(userId, pid);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getNearbyMassagersNolevelNosex(String city, int pid, double lat, double lng, long beginTime) {
        List<Massager> massagers = baseMapper.getAvailableMassagersNolevelNosexCityAndPid(city, pid);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getMyMassagersNolevelSex(String userId, int pid, double lat, double lng, long beginTime, String sex) {
        List<Massager> massagers = baseMapper.getMyMassagersNolevelSex(userId, pid, sex);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getNearbyMassagersNolevelSex(String city, int pid, double lat, double lng, long beginTime, String sex) {
        List<Massager> massagers = baseMapper.getAvailableMassagersNolevelSex(city, pid, sex);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getMyMassagersLevelNosex(String userId, int pid, double lat, double lng, Integer level, long beginTime) {
        List<Massager> massagers = tnsMapper.getMyMassagersLevelNosex( pid,Integer.valueOf(userId), level);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getNearbyMassagersLevelNosex(String city, int pid, double lat, double lng, Integer level, long beginTime) {
        List<Massager> massagers = baseMapper.getAvailableMassagersLevelNosex(city, pid, level);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getMyMassagersLevelSex(String userId, int pid, double lat, double lng, Integer level, long beginTime, String sex) {
        List<Massager> massagers = baseMapper.getMyMassagersLevelSex(userId, pid, level, sex);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public List<Massager> getNearbyMassagersLevelSex(String city, int pid, double lat, double lng, Integer level, long beginTime, String sex) {
        List<Massager> massagers = baseMapper.getAvailableMassagersLevelSex(city, pid, level, sex);
        Distance.batchTransMassagers(massagers);
        return filterMassagersByDistance(massagers, lat, lng, 0, beginTime);
    }

    @Override
    public LevelRefer queryLevelRefer(int lid, int pid) {
        return baseMapper.queryLevelRefer(lid, pid);
    }

    @Override
    public IPage<Massager> queryMassager4Comment(Page makePaging, String city, String keyword, String type) {

        if (type == null || "".equals(type) || "all".equals(type)) {
       return tnsMapper.queryMassager4Comment(makePaging,city, keyword, type );
        } else {
       return tnsMapper.queryMassager4CommentByType(makePaging,city, keyword, type);
        }
    }

    @Override
    public IPage<Massager> queryMassager4Distance(Page makePaging, String city, String keyword, String type) {
        if (type == null || "".equals(type) || "all".equals(type)) {
          return tnsMapper.queryMassager4Distance(makePaging,city, keyword, type);
        } else {
          return tnsMapper.queryMassager4DistanceByType(makePaging,city, keyword, type);
        }
    }

    @Override
    public List<Massager> getStoreMassagers(int pid, int sid, int level, String massagerSex) {
        List<Massager> storeMassagers = new ArrayList<Massager>();
        if (massagerSex != null) {
            massagerSex = "male".equals(massagerSex) ? "男" : "女";
        }
        if (level == 0 && (massagerSex == null || "".equals(massagerSex))) {
            storeMassagers = tnsMapper.getStoreMassagersNolevelNosex(pid, sid);
        } else if (level != 0 && (massagerSex == null || "".equals(massagerSex))) {
            storeMassagers = tnsMapper.getStoreMassagersLevelNosex(pid, sid, level);
        } else if (level == 0 && (massagerSex != null && !"".equals(massagerSex))) {
            storeMassagers = tnsMapper.getStoreMassagersNolevelSex(pid, sid, massagerSex);
        } else if (level != 0 && (massagerSex != null && !"".equals(massagerSex))) {
            storeMassagers = tnsMapper.getStoreMassagersLevelSex(pid, sid, level, massagerSex);
        }
        return storeMassagers;
    }

    public List<Massager> filterMassagersByDistance(List<Massager> massagers, double lat, double lng, int defaultRange,
                                                    long beginTime) {
        if (massagers != null && massagers.size() > 0) {
            for (int i = 0; i < massagers.size(); i++) {
                String[] location = massagers.get(i).getLocation().split(",");
                double distance = Distance.getDistance(lng, lat, Double.parseDouble(location[0]),
                        Double.parseDouble(location[1]));
                int range = (int) (massagers.get(i).getOrderrange() * 1000);
                if (massagers.get(i).getLaterDot() != null && massagers.get(i).getLaterRange() != null
                        && massagers.get(i).getLaterDot() != -1) {
                    if (TimeUtil.getTimeDot(beginTime) >= massagers.get(i).getLaterDot()) {
                        range = (int) (massagers.get(i).getLaterRange() * 1000);
                    }
                }
                if (range == 0 && defaultRange > 0) {
                    range = defaultRange;
                }
                if (range == 0) {
                    // 来自已用过技师，且未设置接单范围
                    massagers.get(i).setDistance(distance);
                    continue;
                }
                if (distance > range) {
                    massagers.remove(i--);
                } else {
                    massagers.get(i).setDistance(distance);
                }
            }
            // 根据距离排序
            Collections.sort(massagers);
        }
        return massagers;
}}
