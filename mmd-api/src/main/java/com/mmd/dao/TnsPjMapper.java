package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.massagist.EstimationsDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.domain.dto.profile.operateDTO;
import com.mmd.entity.Estimation;
import com.mmd.entity.TnsPj;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.persistence.EstimationPersistence;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsPjMapper extends BaseMapper<TnsPj> {
    EstimationDTO findPjByOrderId(@Param("id") int paramInt);

    IPage<EstimationDTO> getEstimationsByMassagerId(Page page, @Param("Estimations") EstimationsDTO estimationsDTO);

    IPage<EstimationDTO> getEstimationsByMassagerIdAndType(Page page, @Param("Estimations") EstimationsDTO estimationsDTO);

    String getPjLevelById(@Param("Id") String id);

    String getPjNumById(@Param("Id") String id);

    String getHplById(@Param("Id") String id);

    Map getMassagerParam(@Param("tid") String tid);

    IPage<Estimation> getEstimationsByMassagerId2(Page page, @Param("estimations") EstimationPersistence estimationPersistence);

    IPage<Estimation> getEstimationsByPidAndType(Page page, @Param("estimations") EstimationPersistence estimationPersistence);

    int queryPjRecord(@Param("pid") int pid, @Param("uid") int uid);

    List<Map<String, Object>> findPjNum(@Param("tid") String paramString);

    List<Map<String, Object>> commentNum(@Param("pids") List<Integer> pids);

    List<Map<String, Integer>> queryMassagerComments(@Param("tid") String id);

    int updatePjSupport(String id);

    Estimation getEstimationByOid(@Param("oid") String oid);

    Integer getGoodCount(@Param("tid") String tid, @Param("beginTime") String beginTime,
                         @Param("endTime") String endTime);
}
