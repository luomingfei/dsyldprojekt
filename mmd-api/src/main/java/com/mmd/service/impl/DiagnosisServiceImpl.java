package com.mmd.service.impl;

import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.Diagnosis;
import com.mmd.dao.DiagnosisMapper;
import com.mmd.service.DiagnosisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
public class DiagnosisServiceImpl extends ServiceImpl<DiagnosisMapper, Diagnosis> implements DiagnosisService {

    @Override
    public List<Diagnosis> getDiagnosisListByOid(String orderId) {
        return baseMapper.getDiagnosisListByOid(orderId);
    }

    @Override
    public Integer getDiagnosisCount(String tid, String beginTime, String endTime) {
        return baseMapper.getDiagnosisCount(tid, beginTime, endTime);
    }

    @Override
    public DiagnosisOutputDTO findDiagnosisById(String id) {
        return baseMapper.findDiagnosisById(id);
    }
}
