package com.mmd.service;

import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.Diagnosis;
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
public interface DiagnosisService extends IService<Diagnosis> {
    List<Diagnosis> getDiagnosisListByOid(String orderId);

    Integer getDiagnosisCount(String tid, String beginTime, String endTime);

    DiagnosisOutputDTO findDiagnosisById(String id);
}
