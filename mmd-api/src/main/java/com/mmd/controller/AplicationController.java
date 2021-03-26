package com.mmd.controller;

import com.mmd.TimeUtil;
import com.mmd.domain.dto.order.MassagerDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.profile.OrderDTO;
import com.mmd.entity.*;
import com.mmd.service.*;
import io.swagger.annotations.ApiOperation;
import javafx.application.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Validated
@RestController
@RequestMapping("/v1/app")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AplicationController {
    private final TnsService tnsService;
    private final ProductService productService;
    private final OrderService orderService;
    private final StoreService storeService;
    private final ExtrahoursService extrahoursService;
    private final TnsPjService tnsPjService;
    private final DiagnosisService diagnosisService;
    private final SalaryBillService salaryBillService;
    /**
     * 查询推拿师的工时，报酬等信息
     */
    @RequestMapping(value = "/{tid}/searchMoney", method = RequestMethod.GET)
    @ApiOperation(value = "查询推拿师的工时，报酬等信息",notes = "查询推拿师的工时，报酬等信息",response = GlobalResult.class)
    public GlobalResult searchMoney(@PathVariable String tid,
                                           @RequestParam(value = "beginDay", required = false) String beginDay,
                                           @RequestParam(value = "endDay", required = false) String endDay,
                                           @RequestParam(value = "sid", required = false) Integer sid) throws ParseException {
        Map<String, Object> result = new TreeMap<>();
        String beginTime = "";
        String endTime = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(sdf.parse(beginDay));
        beginTime = (calendar.getTimeInMillis() / 1000) + "";
        calendar.setTime(sdf.parse(endDay));
        endTime = (calendar.getTimeInMillis() / 1000 + 3600 * 24) + "";
        List<AppCount> resultList = new ArrayList<>();
        MassagerDTO massager = tnsService.findById(tid);
        List<String> pids = productService.findIdsByCity(massager.getCity());
        double sumtime = 0;
        double summoney = 0;
        double turnover = 0;
        double shoulderMmdTime = 0;
        double shoulderOtherTime = 0;
        double noShoulderMmdTime = 0;
        double noShoulderOtherTime = 0;
        List<Map<String, Object>> platforms = new ArrayList<>();
        for (String pid : pids) {
            ProductDTO product = productService.findById(Integer.parseInt(pid));
            List<OrderDTO> list = new ArrayList<OrderDTO>();
            if(sid == null) {
                list = orderService.getOrdersOfMassager(tid, pid, beginTime, endTime);
            } else {
                List<Integer> tids = storeService.getStoreTidsBySid(sid);
                for(Integer tidTemp : tids) {
                    list.addAll(orderService.getOrdersOfMassager(tidTemp+"", pid, beginTime, endTime));
                }
            }
            int sl = 0;
            double time = 0;
            double xxmoney = 0;
            double settlemoney = 0;
            double levelmoney = 0;
            for (int j = 0; j < list.size(); j++) {
                if(list.get(j).getSettleprice()!=null) {
                    String way = list.get(j).getWay();
                    if("APP(iOS)".equals(way) || "APP(Andorid)".equals(way) || "Wechat".equals(way) || "DingYue".equals(way)) {
                        settlemoney += Double.parseDouble(list.get(j).getSettleprice().toString());
                    } else {
                        settlemoney += Double.parseDouble(list.get(j).getSettleprice().toString()) * 0.9;
                    }
                }
                list.get(j).setCreateTime(TimeUtil.TimeConvertMin(list.get(j).getCreateTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                list.get(j).setRq(TimeUtil.TimeConvert(list.get(j).getRq()));
                sl += list.get(j).getSl();
                time += list.get(j).getSl() * Integer.parseInt(product.getFzsj());
                if ("xxpay".equals(list.get(j).getPaymenttype()) && list.get(j).getSettleprice() != null) {
                    xxmoney += Double.parseDouble(list.get(j).getSettleprice());
                }
                String way = list.get(j).getWay();
                if(product.getXmmc().contains("肩颈")) {
                    if("APP(iOS)".equals(way) || "APP(Andorid)".equals(way) || "Wechat".equals(way) || "DingYue".equals(way)) {
                        shoulderMmdTime += list.get(j).getSl() * Integer.parseInt(product.getFzsj());
                    } else {
                        shoulderOtherTime += list.get(j).getSl() * Integer.parseInt(product.getFzsj());
                    }
                } else {
                    if("APP(iOS)".equals(way) || "APP(Andorid)".equals(way) || "Wechat".equals(way) || "DingYue".equals(way)) {
                        noShoulderMmdTime += list.get(j).getSl() * Integer.parseInt(product.getFzsj());
                    } else {
                        noShoulderOtherTime += list.get(j).getSl() * Integer.parseInt(product.getFzsj());
                    }
                }
                if(list.get(j).getSettleprice() != null) {
                    turnover += Double.parseDouble(list.get(j).getSettleprice());
                    calPlatformTurnover(platforms, list.get(j));
                }
                if(list.get(j).getLevelmoney() != null) {
                    levelmoney += Double.parseDouble(list.get(j).getLevelmoney());
                }
            }
            if (sl != 0) {
                AppCount appCount = new AppCount();
                appCount.setXmmc(product.getXmmc());
                appCount.setSl(sl);
                appCount.setPayment(product.getSalary() == null ? 0 : Double.parseDouble(product.getSalary()));
                appCount.setMoney(time * appCount.getPayment() / 60.0 - xxmoney + levelmoney);
                appCount.setXxmoney(xxmoney);
                appCount.setTime(new BigDecimal(time / 60.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                appCount.setPid(pid);
                appCount.setList(list);
                appCount.setSettlemoney(settlemoney);
                sumtime += appCount.getTime();
                summoney += appCount.getMoney();
                resultList.add(appCount);
            }
        }
        // 推广
        double extraSumtime = 0;
        double extraSummoney = 0;
        List<ExtraHours> elist = extrahoursService.getExtraHours(tid, beginTime, endTime);
        for (int i = 0; i < elist.size(); i++) {
            ExtraHours extra = elist.get(i);
            extraSumtime += extra.getHours();
            extraSummoney += extra.getHours() * extra.getPrice();
        }
        // 好评
        double goodSummoney = 0;
        Integer goodCount = tnsPjService.getGoodCount(tid, beginTime, endTime);
        goodSummoney = goodCount * 10;
        double diagnosisSummoney = 0;
        Integer diagnosisCount = diagnosisService.getDiagnosisCount(tid, beginTime, endTime);
        diagnosisSummoney = diagnosisCount * 3;
        List<SalaryField> salaryFields = salaryBillService.getSalaryField();
        for (SalaryField salaryField : salaryFields) {
            switch (salaryField.getField()) {
                case "turnover":
                    if(salaryField.getIsshow() == 1) {
                        //扣点
                        result.put("platforms", calDeduct(platforms));
                        result.put("turnover", calTotal(platforms));
                    }
                    break;
                default:
                    break;
            }
        }
        result.put("result", resultList);
        result.put("goodCount", goodCount);
        result.put("goodSummoney", goodSummoney);
        result.put("diagnosisCount", diagnosisCount);
        result.put("diagnosisSummoney", diagnosisSummoney);
        result.put("extraSumtime", extraSumtime);
        result.put("extraSummoney", extraSummoney);
        result.put("shoulderMmdTime", new BigDecimal(shoulderMmdTime/60.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("shoulderOtherTime", new BigDecimal(shoulderOtherTime/60.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("noShoulderMmdTime", new BigDecimal(noShoulderMmdTime/60.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("noShoulderOtherTime", new BigDecimal(noShoulderOtherTime/60.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("sumtime", new BigDecimal(sumtime + extraSumtime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("summoney", summoney = summoney + extraSummoney + goodSummoney +diagnosisSummoney);
        return GlobalResult.ok(result);
    }
    private double calTotal (List<Map<String, Object>> platforms) {
        double total = 0;
        if (platforms == null || platforms.size() == 0) {
            return total;
        }
        for (Map platform : platforms) {
            total += Double.parseDouble(platform.get("turnover").toString());
        }
        return total;
    }
    private List<Map<String,Object>> calDeduct (List<Map<String, Object>> platforms) {

        for (Map platform : platforms) {
            if (platform.get("way").toString().equals("other")) {
                double prev = Double.parseDouble(platform.get("turnover").toString());
                platform.put("turnover", prev * 90 / 100);
                break;
            }
        }
        return platforms;
    }

    private void calPlatformTurnover (List<Map<String, Object>> platforms, OrderDTO order) {
        String platformCode = "other";
        String platformName = "其他";
        if (order.getWay() == null || "未知".equals(order.getWay()) || "Wechat".equals(order.getWay())
                || "APP(Andorid)".equals(order.getWay()) || "APP(iOS)".equals(order.getWay()) || "DingYue".equals(order.getWay())) {
            platformCode = "own";
            platformName = "魔魔达";
        }
        for (Map<String, Object> platform : platforms) {
            if (platform.get("way").toString().equals(platformCode)) {
                platform.put("turnover", Double.parseDouble(platform.get("turnover").toString()) + Double.parseDouble(order.getSettleprice()));
                return;
            }
        }
        Map<String, Object> platform = new HashMap<>();
        platform.put("way", platformCode);
        platform.put("turnover", order.getSettleprice());
        platform.put("name", platformName);
        platforms.add(platform);
    }
}
