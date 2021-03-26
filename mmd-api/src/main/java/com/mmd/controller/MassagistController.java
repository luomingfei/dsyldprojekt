package com.mmd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.massagist.EstimationsDTO;
import com.mmd.domain.dto.massagist.ListbnbDTO;
import com.mmd.domain.dto.massagist.MassagistDTO;
import com.mmd.domain.dto.massagist.PhotoDTO;
import com.mmd.domain.dto.order.EstimationDTO;
import com.mmd.entity.GlobalResult;
import com.mmd.entity.Massager;
import com.mmd.service.*;
import com.mmd.utils.Distance;
import com.mmd.utils.PageUtil;
import com.mmd.utils.baiduAPI.BaiduAPI;
import com.mmd.utils.baiduAPI.Location;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tomwang
 * 按摩师控制器
 */
@Validated
@RestController
@RequestMapping("/massagist")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MassagistController {
    private final TnsTagsService tnsTagsService;
    private final TnsService tnsService;
    private final TnsPjService tnsPjService;
    private final OrderService orderService;
    private static Logger log = LoggerFactory.getLogger(MassagistController.class);

    /**
     * 按摩师标签
     */
    @ApiOperation(value = "按摩师标签findTagsAndCount", notes = "按摩师标签")
    @ApiImplicitParam(name = "massagerId", value = "massagerId", required = true, dataType = "String")
    @GetMapping("/{massagerId}/findTagsAndCount")
    public GlobalResult findTagsAndCount(@PathVariable String massagerId) {
        List<MassagistDTO> list = tnsTagsService.massagistdtolist(massagerId, null);
        return GlobalResult.ok(list);
    }

    /**
     * 按摩师标签
     */
    @ApiOperation(value = "按摩师标签findTagsAndCount", notes = "按摩师标签")
    @ApiImplicitParam(name = "massagerId", value = "数据库tid", required = true, dataType = "massagerId")
    @GetMapping("/{massagerId}/tags")
    public GlobalResult tagsOfMassager(@PathVariable String massagerId) {
        List<MassagistDTO> list = tnsTagsService.findTagsOfMassager(massagerId);
        return GlobalResult.ok(list);
    }

    /**
     * 按摩师照片
     */
    @ApiOperation(value = "按摩师照片", notes = "按摩师照片", response = GlobalResult.class)
    @ApiImplicitParam(name = "massagerId", value = "massagerId", required = true, paramType = "path", dataType = "String", dataTypeClass = String.class)
    @GetMapping("/{massagerId}/photos")
    public GlobalResult photosOfMassager(@PathVariable String massagerId) {
        List<PhotoDTO> photoDTOS = tnsService.findPhotosOfMassager(massagerId);
        return GlobalResult.ok(photoDTOS);
    }

    /**
     * 根据按摩师id查按摩师评价列表
     */
    @PostMapping("/estimations")
    @ApiOperation(value = "根据按摩师id查按摩师评价列表", notes = "根据按摩师id查按摩师评价列表", response = ResponseEntity.class)
    @ApiImplicitParam(name = "estimationsDTO", value = "查按摩师评价列表信息", required = true, paramType = "body", dataType = "EstimationsDTO", dataTypeClass = EstimationsDTO.class)
    public GlobalResult listOfEstimationById(@RequestBody EstimationsDTO estimationsDTO) {
        IPage<EstimationDTO> estimationDTOIPage = tnsPjService.getEstimationsByMassagerId(estimationsDTO.makePaging(), estimationsDTO);
        return GlobalResult.ok(estimationDTOIPage);
    }


    /**
     * 按摩师列表
     */
    @PostMapping("/list")
    public GlobalResult listBnb(@RequestBody ListbnbDTO listbnbDTO) throws Exception {
        if (listbnbDTO.getType() == null || "".equals(listbnbDTO.getType())) {
            listbnbDTO.setType("(0,1,2)");
        }
        List<Massager> massagers = tnsService.findByCityAndKeyword(listbnbDTO.getCityId(), listbnbDTO.getKeyword(), listbnbDTO.getType(),listbnbDTO.getTid());
        Distance.batchTransMassagers(massagers);
        if (listbnbDTO.getLat() != null && listbnbDTO.getLng() != null) {
            tnsPjService.filterMassagersByDistance(massagers, listbnbDTO.getLat(), listbnbDTO.getLng(), 0, 0);
        } else if (listbnbDTO.getAddress() != null && listbnbDTO.getAddress() != "") {
            Location l = BaiduAPI.geocoderByAddress(listbnbDTO.getAddress().trim().replaceAll(" ", "")).getResult().getLocation();
            tnsPjService.filterMassagersByDistance(massagers, l.getLat(), l.getLng(), 0, 0);
        }
        for (int i = massagers.size() - 1; i >= 0; i--) {
            Double distance = massagers.get(i).getDistance();
            Double range = massagers.get(i).getOrderrange();
            if (distance == null || range == null || range == 0) {
                continue;
            }
            if (distance > range * 1000) {
                massagers.remove(i);
            }
        }
        int index = Integer.valueOf(listbnbDTO.getCurrentPage());
        int limit = Integer.valueOf(listbnbDTO.getPageSize());
        PageUtil page = new PageUtil(massagers, limit);
        if (index > page.getTotalPages()) {
            return GlobalResult.ok(new ArrayList<>());
        } else {
            massagers = page.getObjects(index);
            for (int i = 0; i < massagers.size(); i++) {
                Massager massager = massagers.get(i);
                massager.setLjxd(orderService.getLjxdByMassagerId(massager.getId()));
                massager.setPj(tnsPjService.getPjLevelById(massager.getId()));
                massager.setPj_num(tnsPjService.getPjNumById(massager.getId()));
                if(listbnbDTO.getTid()!=null && listbnbDTO.getTid()!=0){
                    massager.setHpl(tnsPjService.getHplById(massager.getId()));
                }
                massagers.set(i, massager);
            }
        }
        return GlobalResult.ok(massagers);
    }

    /**
     * 查询按摩师评价数目
     */
    @PostMapping("/{massagerId}/pjnum")
    @ApiOperation(value = "查询按摩师评价数目",notes = "查询按摩师评价数目",response = GlobalResult.class)
    public GlobalResult listOfEstimationById(@PathVariable String massagerId) {
        log.debug("/v1/{massagerId}/pjnum", massagerId);
        List<Map<String, Object>> pjNum = tnsPjService.findPjNum(massagerId);
        return GlobalResult.ok(pjNum);
    }
}
