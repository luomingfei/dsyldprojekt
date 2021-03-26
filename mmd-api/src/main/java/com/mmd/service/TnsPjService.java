package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.massagist.EstimationsDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.domain.dto.profile.operateDTO;
import com.mmd.entity.Estimation;
import com.mmd.entity.Massager;
import com.mmd.entity.TnsPj;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.persistence.EstimationPersistence;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsPjService extends IService<TnsPj> {

    IPage<EstimationDTO> getEstimationsByMassagerId(Page makePaging, EstimationsDTO estimationsDTO);

    List<Massager> filterMassagersByDistance(List<Massager> massager, Double lat, Double lng, int i, long i1);

    String getPjLevelById(String id);

    String getPjNumById(String id);

    String getHplById(String id);

    EstimationDTO findPjByOrderId(int id);

    Map getMassagerParam(String tid);

    IPage<Estimation> getEstimationsByPid(Page makePaging, EstimationPersistence estimationPersistence);

    List<Map<String, Object>> findPjNum(String massagerId);

    List<Map<String, Object>> commentNum(List<Integer> pids);

    Map<String, String> getCommentInfo(String id);

    boolean operateComment(operateDTO operateDTO);

    Estimation getEstimationByOid(String oid);

    Integer getGoodCount(String tid, String beginTime, String endTime);
}
