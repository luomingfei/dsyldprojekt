package com.mmd.dao;

import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.Diagnosis;
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
public interface DiagnosisMapper extends BaseMapper<Diagnosis> {
    List<Diagnosis> getDiagnosisListByOid(@Param("orderId") String orderId);

    Integer getDiagnosisCount(@Param("tid") String tid, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    DiagnosisOutputDTO findDiagnosisById(@Param("id") String id);
}
