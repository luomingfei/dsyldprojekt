package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.TimeUtil;
import com.mmd.dao.PjRecordMapper;
import com.mmd.domain.dto.massagist.EstimationsDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.domain.dto.profile.operateDTO;
import com.mmd.entity.Estimation;
import com.mmd.entity.Massager;
import com.mmd.entity.TnsPj;
import com.mmd.dao.TnsPjMapper;
import com.mmd.persistence.EstimationPersistence;
import com.mmd.service.TnsPjService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.utils.Distance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.*;

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
public class TnsPjServiceImpl extends ServiceImpl<TnsPjMapper, TnsPj> implements TnsPjService {
    private final TnsPjMapper tnsPjMapper;
    private final PjRecordMapper pjRecordMapper;

    @Override
    public IPage<EstimationDTO> getEstimationsByMassagerId(Page page, EstimationsDTO estimationsDTO) {
        IPage<EstimationDTO> estimationDTOIPage = null;
        if(estimationsDTO.getType()==null){
            estimationDTOIPage = tnsPjMapper.getEstimationsByMassagerId(page, estimationsDTO);
        }else{
            estimationDTOIPage = tnsPjMapper.getEstimationsByMassagerIdAndType(page,estimationsDTO);
        }
        if (estimationDTOIPage.getRecords().size() > 0) {
            for (int i = 0; i < estimationDTOIPage.getRecords().size(); i++) {
                EstimationDTO e = estimationDTOIPage.getRecords().get(i);
                e.setCreatetime(TimeUtil.TimeConvertMin(e.getCreatetime()));
                if (e.getUsername() == null || e.getUsername().length() < 11) {
                    e.setUsername("13****" + (int) (Math.random() * 100000));
                } else {
                    e.setUsername(e.getUsername().substring(0, 2) + "****" + e.getUsername().substring(6, 11));
                }
                e.setSkill(e.getSkill() == 0 ? 5 : e.getSkill());
                e.setAttitude(e.getAttitude() == 0 ? 5 : e.getAttitude());
                e.setOnTime(e.getOnTime() == 0 ? 5 : e.getOnTime());
                if (estimationsDTO.getUid() != null) {
                    e.setLike(tnsPjMapper.queryPjRecord(e.getId(), estimationsDTO.getUid()) > 0 ? true : false);
                }
                estimationDTOIPage.getRecords().set(i, e);
            }
        }
        return estimationDTOIPage;
    }

    @Override
    public List<Massager> filterMassagersByDistance(List<Massager> massagers, Double lat, Double lng, int defaultRange, long beginTime) {
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
    }

    @Override
    public String getPjLevelById(String id) {
        if (this.tnsPjMapper.getPjLevelById(id) == null) {
            return "0";
        }
        return transPjLevel(Double.parseDouble(tnsPjMapper.getPjLevelById(id)));
    }

    @Override
    public String getPjNumById(String id) {

        return tnsPjMapper.getPjNumById(id);
    }

    @Override
    public String getHplById(String id) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(2);
        if (Integer.parseInt(tnsPjMapper.getPjNumById(id)) != 0) {
            return format.format(Double.parseDouble(tnsPjMapper.getHplById(id))
                    / Double.parseDouble(tnsPjMapper.getPjNumById(id)));
        }
        return "0%";


    }

    @Override
    public EstimationDTO findPjByOrderId(int id) {
        return baseMapper.findPjByOrderId(id);
    }

    @Override
    public Map getMassagerParam(String tid) {
        Map massagerParam = this.baseMapper.getMassagerParam(tid);
        // 技师平均评价分数
        Object avg = massagerParam.get("avg");
        if (avg == null) {
            massagerParam.put("pj", 0);
        } else {
            double avgNum = Double.parseDouble(avg.toString());
            if (avgNum >= 4.75) {
                massagerParam.put("pj", 5);
            } else if (avgNum > 4.25) {
                massagerParam.put("pj", 4.5);
            } else if (avgNum > 3.75) {
                massagerParam.put("pj", 4);
            } else if (avgNum > 3.25) {
                massagerParam.put("pj", 3.5);
            } else if (avgNum > 2.75) {
                massagerParam.put("pj", 3);
            } else if (avgNum > 2.25) {
                massagerParam.put("pj", 2.5);
            } else if (avgNum > 1.75) {
                massagerParam.put("pj", 2);
            } else if (avgNum > 1.25) {
                massagerParam.put("pj", 1.5);
            } else if (avgNum > 0.75) {
                massagerParam.put("pj", 1);
            } else {
                massagerParam.put("pj", 0.5);
            }
        }
        //好评率
        int count = Integer.parseInt(massagerParam.get("count").toString());
        int hp = Integer.parseInt(massagerParam.get("hp").toString());
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(2);
        if (count == 0) {
            massagerParam.put("hpl", "0%");
        } else {
            massagerParam.put("hpl", format.format(hp / count));
        }
        return massagerParam;
    }

    @Override
    public IPage<Estimation> getEstimationsByPid(Page makePaging,
                                                 EstimationPersistence estimationPersistence) {
        IPage<Estimation> estimations;
        if (estimationPersistence.getType() == null) {
            estimations = baseMapper.getEstimationsByMassagerId2(makePaging, estimationPersistence);
        } else {
            estimations = baseMapper.getEstimationsByPidAndType(makePaging, estimationPersistence);
        }
        if (estimations.getSize() > 0) {
            List<Estimation> estimationList = estimations.getRecords();
            for (int i = 0; i < estimationList.size(); i++) {
                Estimation e = estimationList.get(i);
                e.setCreatetime(TimeUtil.TimeConvertMin(e.getCreatetime()));
                if (e.getUsername() == null || e.getUsername().length() < 11) {
                    e.setUsername("13****" + (int) (Math.random() * 100000));
                } else {
                    e.setUsername(e.getUsername().substring(0, 2) + "****" + e.getUsername().substring(6, 11));
                }
                e.setSkill(e.getSkill() == 0 ? 5 : e.getSkill());
                e.setAttitude(e.getAttitude() == 0 ? 5 : e.getAttitude());
                e.setOnTime(e.getOnTime() == 0 ? 5 : e.getOnTime());
                if (estimationPersistence.getUid() != null) {
                    e.setLike(tnsPjMapper.queryPjRecord(e.getId(), estimationPersistence.getUid()) > 0 ? true : false);
                }
                estimationList.set(i, e);
            }
            estimations.setRecords(estimationList);
        }
        return estimations;
    }

    @Override
    public List<Map<String, Object>> findPjNum(String massagerId) {
        return baseMapper.findPjNum(massagerId);
    }

    @Override
    public List<Map<String, Object>> commentNum(List<Integer> pids) {
        return baseMapper.commentNum(pids);
    }

    @Override
    public Map<String, String> getCommentInfo(String id) {
        Map<String, String> result = new HashMap<>();
        int total = 0, totalLevel = 0, good = 0;
        List<Map<String, Integer>> comments = tnsPjMapper.queryMassagerComments(id);
        total = comments.size();
        for (Map<String, Integer> comment : comments) {
            if (comment.get("pjlx") == 1) {
                good++;
            }
            totalLevel += comment.get("pjlevel");
        }
        result.put("total", total + "");
        result.put("pj", total == 0 ? "0" : transPjLevel(totalLevel * 1.0 / total));
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(2);
        result.put("hpl", total == 0 ? "0.00" : format.format(good * 1.0 / total));
        return result;
    }

    @Override
    public boolean operateComment(operateDTO operateDTO) {
        if (tnsPjMapper.updatePjSupport(operateDTO.getId()) > 0) {
            if (pjRecordMapper.addCommentRecord(operateDTO.getId(), operateDTO.getUserId(), operateDTO.getType()) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Estimation getEstimationByOid(String oid) {
        return tnsPjMapper.getEstimationByOid(oid);
    }

    @Override
    public Integer getGoodCount(String tid, String beginTime, String endTime) {
        return tnsPjMapper.getGoodCount(tid, beginTime, endTime) == null ? 0
                : tnsPjMapper.getGoodCount(tid, beginTime, endTime);
    }

    public String transPjLevel(double level) {
        if (level > 4.8D) {
            return "5";
        }
        if (level > 4.3D) {
            return "4.5";
        }
        if (level > 3.8D) {
            return "4";
        }
        if (level > 3.3D) {
            return "3.5";
        }
        if (level > 2.8D) {
            return "3";
        }
        if (level > 2.3D) {
            return "2.5";
        }
        if (level > 1.8D) {
            return "2";
        }
        if (level > 1.3D) {
            return "1.5";
        }
        return "1";
    }
}
