package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.massagist.PhotoDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.entity.LevelRefer;
import com.mmd.entity.Massager;
import com.mmd.entity.Product;
import com.mmd.entity.Tns;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsService extends IService<Tns> {

    MassagerDTO findById(String s);

    EstimationDTO findPjByOrderId(int id);

    List<ProductDTO> findProductsByMassagerId(Integer cityId, String massagerId);

    List<PhotoDTO> findPhotosOfMassager(String massagerId);

    List<Massager> findByCityAndKeyword(String cityId, String keyword, String type,Integer tid) throws Exception;

    List<Massager> getMyMassagers4JiaShiNolevelNosex(String uid);

    List<Massager> getMyMassagers4JiaShiNolevelSex(String uid, String sex);

    List<Massager> getMyMassagers4JiaShiLevelNosex(String uid, int level);

    List<Massager> getMyMassagers4JiaShiLevelSex(String uid, int level, String sex);

    List<Massager> getMyMassagersNolevelNosex(String userId, int pid, double lat, double lng, long beginTime);

    List<Massager> getNearbyMassagersNolevelNosex(String city, int pid, double lat, double lng, long beginTime);

    List<Massager> getMyMassagersNolevelSex(String userId, int pid, double lat, double lng, long beginTime,
                                            String sex);

    List<Massager> getNearbyMassagersNolevelSex(String city, int pid, double lat, double lng, long beginTime,
                                                String sex);

    List<Massager> getMyMassagersLevelNosex(String userId, int pid, double lat, double lng, Integer level,
                                            long beginTime);

    List<Massager> getNearbyMassagersLevelNosex(String city, int pid, double lat, double lng, Integer level,
                                                long beginTime);

    List<Massager> getMyMassagersLevelSex(String userId, int pid, double lat, double lng, Integer level,
                                          long beginTime, String sex);

    List<Massager> getNearbyMassagersLevelSex(String city, int pid, double lat, double lng, Integer level,
                                              long beginTime, String sex);

    LevelRefer queryLevelRefer(int lid, int pid);

    IPage<Massager> queryMassager4Comment(Page makePaging, String city, String keyword, String type);

    IPage<Massager> queryMassager4Distance(Page makePaging, String city, String keyword, String type);

    List<Massager> getStoreMassagers(int pid, int sid, int level, String massagerSex);
}
