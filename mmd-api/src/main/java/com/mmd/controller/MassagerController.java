package com.mmd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.input.SalaryBillsQueryInputDTO;
import com.mmd.domain.dto.massagist.TsrgetsDTO;
import com.mmd.domain.dto.order.LevelInfoDTO;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.order.ProductSexDTO;
import com.mmd.entity.*;
import com.mmd.persistence.SalaryBillsPersistence;
import com.mmd.service.*;
import com.mmd.utils.Distance;
import com.mmd.utils.baiduAPI.BaiduAPI;
import com.mmd.utils.baiduAPI.Geocoder;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/1 21:32
 */
@RestController
@RequestMapping({ "/v1/{cityId}/massager" })
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MassagerController {

    private static Logger log = LoggerFactory.getLogger(MassagerController.class);
    private final TnsService tnsService;
    private final ProductSexService productSexService;
    private final TnsSjService tnsSjService;
    private final SalaryBillService salaryBillService;
    private final TakeuptimeService takeuptimeService;
    private final AddressService addressService;
    private final ProductService productService;
    private final OrderService orderService;
    private final TnsPjService tnsPjService;

    /**
     * 按摩师列表
     */
    @ApiOperation(value = "按摩师列表", notes = "按摩师列表")
    @RequestMapping(value = { "/targets" }, method = RequestMethod.GET)
    public GlobalResult targets(TsrgetsDTO tsrgetsDTO) throws IOException {
        String city = "上海";
        if (tsrgetsDTO.getLng() == null || tsrgetsDTO.getLat() == null) {
            try {
                Geocoder geocoder = BaiduAPI.geocoder(tsrgetsDTO.getLat().toString() + "," + tsrgetsDTO.getLng().toString());
                StringBuilder sb = new StringBuilder(geocoder.getResult().getAddressComponent().getCity());
                if (sb.indexOf("市") > 0) {
                    sb.setLength(sb.length() - 1);
                }
                city = sb.toString();
            } catch (Exception e) {
                log.error("获取城市信息失败：({},{})", tsrgetsDTO.getLng(), tsrgetsDTO.getLat());
            }
        }
        IPage<Massager> massagers=null;
        List<Massager>massagerList=null;
        //排序方式为评价数
        if ("comment".equals(tsrgetsDTO.getSort())) {
            massagers = tnsService.queryMassager4Comment (tsrgetsDTO.makePaging(), city, tsrgetsDTO.getKeyword(), tsrgetsDTO.getType());
            massagerList=massagers.getRecords();
            Distance.batchTransMassagers(massagerList);
            Distance.batchCalMassagerDistance(massagerList, tsrgetsDTO.getLng(), tsrgetsDTO.getLat());
            for (Massager massager : massagerList) {
                massager.setLjxd(orderService.getLjxdByMassagerId(massager.getId()));
                Map<String, String> info = tnsPjService.getCommentInfo(massager.getId());
                massager.setPj(info.get("pj"));
                massager.setPj_num(info.get("total"));
                massager.setHpl(info.get("hpl"));
            }
            massagers.setRecords(massagerList);
            return GlobalResult.ok(massagers);
        }
        if ("distance".equals(tsrgetsDTO.getSort())) {
            massagers = tnsService.queryMassager4Distance (tsrgetsDTO.makePaging(),city, tsrgetsDTO.getKeyword(), tsrgetsDTO.getType());

        }

                return  GlobalResult.ok();
    }


    /**
     * 按摩师的项目列表
     */
    @GetMapping("/{massagerId}/products")
    @ApiOperation(value = "按摩师的项目列表",notes = "按摩师的项目列表",response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityId", paramType = "path", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "massagerId", paramType = "path", dataType = "String", dataTypeClass = String.class)
    })
    public GlobalResult listOfProducts(@PathVariable("cityId") String cityId, @PathVariable("massagerId") String massagerId){
        log.debug("/v1/{}/massager/{}/products", cityId, massagerId);
        List<ProductDTO> productDTOS = tnsService.findProductsByMassagerId(Integer.valueOf(cityId), massagerId);
        List<ProductSex> productSexList = productSexService.findProductSexByCityId(cityId);
        for (ProductDTO productDTO : productDTOS) {
            productDTO.setLevelInfos(tnsSjService.queryLevelInfoByPid(productDTO.getId()));
            for (ProductSex productSex : productSexList) {
                if(productDTO.getId().equals(productSex.getPid()+"")) {
                    ProductSexDTO productSexDTO=new ProductSexDTO();
                    BeanUtils.copyProperties(productSex,productSexDTO);
                    productDTO.setProductSex(productSexDTO);
                    break;
                }
            }
        }
        if (productDTOS.size() > 0) {
            return GlobalResult.ok(productDTOS);
        }
        return GlobalResult.ok();
    }

    /**
     * 推拿师的工资单列表
     *
     * @return
     */
    @RequestMapping(value = "/{tid}/getSalaryBills", method = RequestMethod.GET)
    @ApiOperation(value = "推拿师的工资单列表",notes = "推拿师的工资单列表",response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "tid",paramType = "path",dataType = "String"),
            @ApiImplicitParam(value = "index",paramType = "query",dataType = "Integer"),
            @ApiImplicitParam(value = "limit",paramType = "query",dataType = "Integer")
    })
    public GlobalResult getSalaryBills(@PathVariable String tid,
                                           @RequestParam(value = "index", required = true) Integer index,
                                           @RequestParam(value = "limit", required = true) Integer limit) {
        log.debug("/v1/{}/massager/{}/getSalaryBills&index={}&limit={}", new Object[] { 1, tid, index, limit });
        SalaryBillsQueryInputDTO salaryBillsQueryInputDTO=new SalaryBillsQueryInputDTO();
        salaryBillsQueryInputDTO.setTid(tid);
        salaryBillsQueryInputDTO.setPageSize(limit.toString());
        salaryBillsQueryInputDTO.setCurrentPage(index.toString());
        SalaryBillsPersistence salaryBillsPersistence=new SalaryBillsPersistence();
        BeanUtils.copyProperties(salaryBillsQueryInputDTO,salaryBillsPersistence);
        IPage<SalaryBill> salaryBills = salaryBillService.getSalaryBills(salaryBillsQueryInputDTO.makePaging(), salaryBillsPersistence);
        return GlobalResult.ok(salaryBills);
    }

    /**
     * 推拿师新增占时间
     */
    @RequestMapping(value = "/addTakeUpTime", method = RequestMethod.POST)
    @ApiOperation(value = "推拿师新增占时间",notes = "推拿师新增占时间",response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "tid",paramType = "query",dataType = "int"),
            @ApiImplicitParam(value = "title",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "beginTime",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "endTime",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isMonOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isTuesOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isWedOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isFriOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isSatOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isSunOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isEveryWeek",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "status",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "createtime",paramType = "query",dataType = "String")

    })
    public GlobalResult addTakeUpTime(TakeUpTime takeUpTime) {
        return GlobalResult.ok(takeuptimeService.addTakeUpTime(takeUpTime));
    }

    /**
     * 推拿师删除占时间
     */
    @RequestMapping(value = "/deleteTakeUpTime", method = RequestMethod.POST)
    @ApiOperation(value = "推拿师删除占时间",notes = "推拿师删除占时间",response = GlobalResult.class)
    @ApiImplicitParam(value = "id",paramType = "query",dataType = "int")
    public GlobalResult deleteTakeUpTime(int id) {
        return GlobalResult.ok(takeuptimeService.deleteTakeUpTime(id));
    }

    /**
     * 推拿师修改占时间
     */
    @RequestMapping(value = "/updateTakeUpTime", method = RequestMethod.POST)
    @ApiOperation(value = "推拿师修改占时间",notes = "推拿师修改占时间",response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "id",paramType = "query",dataType = "int"),
            @ApiImplicitParam(value = "title",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "beginTime",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "endTime",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isMonOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isTuesOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isWedOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isThursOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isFriOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isSatOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isSunOk",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "isEveryWeek",paramType = "query",dataType = "String"),
            @ApiImplicitParam(value = "status",paramType = "query",dataType = "String"),
    })
    public GlobalResult updateTakeUpTime(TakeUpTime takeUpTime) {
        return GlobalResult.ok(takeuptimeService.updateTakeUpTime(takeUpTime));
    }

    /**
     * 推拿师请假
     */
    @RequestMapping(value = "/massagerLeave", method = RequestMethod.POST)
    @ApiOperation(value = "推拿师请假",notes = "推拿师请假",response = GlobalResult.class)
    public GlobalResult massagerLeave(@RequestBody MassagerTime massagerTime) {
        return GlobalResult.ok(tnsSjService.massagerLeave(massagerTime));
    }

    /**
     * 根据日期获得推拿师的时间安排
     */
    @RequestMapping(value = "/getMassagerTimeByRq", method = RequestMethod.POST)
    @ApiOperation(value = "根据日期获得推拿师的时间安排",notes = "根据日期获得推拿师的时间安排",response = GlobalResult.class)
    public GlobalResult getMassagerTimeByRq(@RequestBody MassagerTime timeParam) {
        MassagerTime massagerTime = tnsSjService.findMassagerTimeByRqAndTid(timeParam.getRq(), timeParam.getTid());
        List<Time> tlist = new ArrayList<>();
        int beginDot = 0;
        int endDot = 95;
        for (int j = beginDot; j <= endDot; j++) {
            switch (j % 4) {
                case 0:
                    tlist.add(new Time(j / 4 + ":00", ""));break;
                case 1:
                    tlist.add(new Time(j / 4 + ":15", ""));break;
                case 2:
                    tlist.add(new Time(j / 4 + ":30", ""));break;
                case 3:
                    tlist.add(new Time(j / 4 + ":45", ""));break;
            }
        }

        List<Order> orderList = new ArrayList<>();
        if (timeParam.getTid() != 0) {
            orderList = addressService.findOrdersOfMassagerWillDoInFuture(timeParam.getTid() + "");
        }
        for (int k = 0; k < orderList.size(); k++) {
            if (Long.parseLong(orderList.get(k).getRq().toString()) >= timeParam.getRq()&&
                    Long.parseLong(orderList.get(k).getRq().toString()) < timeParam.getRq() + (24 * 60 * 60)) {
                ProductDTO productDTO = this.productService.findById(orderList.get(k).getPid());
                double sj = orderList.get(k).getSl() * Integer.parseInt(productDTO.getFzsj());
                int orderSj = Integer.parseInt(orderList.get(k).getSj().replace(":", ""));
                for (int q = 0; q < tlist.size(); q++) {
                    int tlistSj = Integer.parseInt(tlist.get(q).getTime().replace(":", ""));
                    if (orderSj == tlistSj) {
                        for (int p = 0; p < Math.ceil(sj / 15); p++) {
                            if (tlist.size() > p + q && p + q >= 0) {
                                tlist.get(q + p).setState("alreadyDate");
                            }
                        }
                    }
                }
            }
            if (Long.parseLong(orderList.get(k).getRq().toString()) < timeParam.getRq()&&
                    Long.parseLong(orderList.get(k).getRq().toString()) >= timeParam.getRq()- (24 * 60 * 60)) {
                ProductDTO productDTO1 = this.productService.findById(orderList.get(k).getPid());
                double sj = orderList.get(k).getSl() * Integer.parseInt(productDTO1.getFzsj());
                String [] serviceTimeArr = orderList.get(k).getSj().split(":");
                Integer serviceTime = Integer.valueOf(serviceTimeArr[0]) * 4 + Integer.valueOf(serviceTimeArr[1])/15;
                Double exceed = serviceTime+Math.ceil(sj / 15)-96;
                if(exceed>0){
                    for (int q = 0; q < exceed.intValue(); q++) {
                        tlist.get(q).setState("alreadyDate");
                    }
                }
            }
        }

        List<TakeUpTime> takeUpTimes = takeuptimeService.getTimeRecord(timeParam.getTid()+"");
        if (takeUpTimes != null && takeUpTimes.size() > 0) {
            for (int t = 0; t < takeUpTimes.size(); t++) {
                TakeUpTime takeUpTime = takeUpTimes.get(t);
                if ("y".equals(takeUpTime.getStatus()) && "y".equals(takeUpTime.isThisDayOk(new Date(timeParam.getRq()*1000)))) {
                    if ("y".equals(takeUpTime.getIsEveryWeek()) || Long.parseLong(takeUpTime.getCreatetime())
                            + 7 * 24 * 3600 > timeParam.getRq()) {
                        for (int index = 0; index < tlist.size(); index++) {
                            if (Integer.parseInt(tlist.get(index).getTime().replace(":", "")) >= Integer
                                    .parseInt(takeUpTime.getBeginTime().replace(":", ""))
                                    && Integer.parseInt(tlist.get(index).getTime().replace(":", "")) <= Integer
                                    .parseInt(takeUpTime.getEndTime().replace(":", ""))) {
                                if (tlist.get(index).getState().equals(""))
                                    tlist.get(index).setState("leave");
                            }
                        }
                    }
                }
            }
        }

        if (massagerTime != null && massagerTime.getSj() != null && !"".equals(massagerTime.getSj())) {
            String[] sjArr = massagerTime.getSj().split(",");
            for (int j = 0; j < tlist.size(); j++) {
                for (int k = 0; k < sjArr.length; k++) {
                    if (sjArr[k].equals(tlist.get(j).getTime())) {
                        tlist.get(j).setState("leave");
                        break;
                    }
                }
            }
        }

        List<List<Time>> times = new ArrayList<>();
        List<Time> timeList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if ((calendar.getTimeInMillis()/1000 - calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 - calendar.get(Calendar.MINUTE) * 60
                - calendar.get(Calendar.SECOND)) == timeParam.getRq()) {
            // 转换当前时间
            int nowDot = 4 * calendar.get(Calendar.HOUR_OF_DAY);
            if(calendar.get(Calendar.MINUTE) >= 45) {
                nowDot += 3;
            } else if(calendar.get(Calendar.MINUTE) >= 30) {
                nowDot += 2;
            } else if(calendar.get(Calendar.MINUTE) >= 15) {
                nowDot += 1;
            }
            for (int j = tlist.size(); j >= 0; j--) {
                if(beginDot + j <= nowDot) {
                    tlist.remove(j);
                }
            }
        }

        for (int j = 0; j < tlist.size(); j++) {
            timeList.add(tlist.get(j));
            if ((j + 1) % 4 == 0) {
                times.add(timeList);
                timeList = new ArrayList<>();
            }
            if (j == tlist.size() - 1 && timeList.size() != 0) {
                times.add(timeList);
            }
        }
        return GlobalResult.ok(times);
    }


}
