package com.mmd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.output.TnsOutputDTO;
import com.mmd.entity.LevelRefer;
import com.mmd.entity.Massager;
import com.mmd.entity.Tns;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsMapper extends BaseMapper<Tns> {
    MassagerDTO findById(@Param("massagerId") String paramString);
    TnsOutputDTO findByMassagerId(@Param("massagerId") String paramString);

    String findSidByMassagerId(@Param("massagerId") String paramString);

    List<Massager> findByCityIdAndKeyword(@Param("city") String cityId, @Param("keyword") String keyword, @Param("type") String type,@Param("tid") Integer tid);

    List<Massager> getMyMassagers4JiaShiNolevelNosex(@Param("uid") String uid);

    List<Massager> getMyMassagers4JiaShiNolevelSex(@Param("uid") String uid, @Param("sex") String sex);

    List<Massager> getMyMassagers4JiaShiLevelNosex(@Param("uid") String uid, @Param("level") int level);

    List<Massager> getMyMassagersNolevelNosex(@Param("uid") String userId, @Param("pid") int pid);

    List<Massager> getAvailableMassagersNolevelNosexCityAndPid(@Param("city") String city, @Param("pid") int pid);

    List<Massager> getMyMassagersNolevelSex(@Param("uid") String userId, @Param("pid") int pid, @Param("sex") String sex);

    List<Massager> getAvailableMassagersNolevelSex(@Param("city") String city, @Param("pid") int pid, @Param("sex") String sex);

    List<Massager> getMyMassagersLevelNosex(@Param("pid") int pid, @Param("uid") Integer userId,  @Param("level") int level);

    List<Massager> getAvailableMassagersLevelNosex(@Param("city") String city, @Param("pid") int pid, @Param("level") int level);

    List<Massager> getMyMassagersLevelSex(@Param("uid") String userId, @Param("pid") int pid, @Param("level") int level, @Param("sex") String sex);

    List<Massager> getAvailableMassagersLevelSex(@Param("city") String city, @Param("pid") int pid, @Param("level") int level, @Param("sex") String sex);

    LevelRefer queryLevelRefer(@Param("lid") int lid, @Param("pid") int pid);

    IPage<Massager> queryMassager4Comment(Page makePaging, @Param("city")String city, @Param("keyword")String keyword, @Param("type")String type);

    IPage<Massager> queryMassager4CommentByType(Page makePaging, @Param("city")String city, @Param("keyword")String keyword,  @Param("type")String type);

    IPage<Massager> queryMassager4Distance(Page makePaging,@Param("city")String city, @Param("keyword")String keyword, @Param("type")String type);

    IPage<Massager> queryMassager4DistanceByType(Page makePaging,@Param("city")String city, @Param("keyword")String keyword, @Param("type")String type);

    List<Massager> getStoreMassagersNolevelNosex(@Param("pid") int pid, @Param("sid") int sid);

    List<Massager> getStoreMassagersLevelNosex(@Param("pid") int pid, @Param("sid") int sid, @Param("level") int level);

    List<Massager> getStoreMassagersNolevelSex(@Param("pid") int pid, @Param("sid") int sid, @Param("sex") String sex);

    List<Massager> getStoreMassagersLevelSex(@Param("pid") int pid, @Param("sid") int sid, @Param("level") int level, @Param("sex") String sex);
}
