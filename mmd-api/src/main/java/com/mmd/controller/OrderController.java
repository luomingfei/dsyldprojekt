package com.mmd.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmd.TimeUtil;
import com.mmd.domain.dto.input.OrderQueryInputDTO;
import com.mmd.domain.dto.order.*;
import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.*;
import com.mmd.persistence.OrderPersistence;
import com.mmd.service.*;
import com.mmd.utils.Coord;
import com.mmd.utils.Distance;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单接口
 *
 * @author tomwang
 */
@Validated
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {
    private static Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductSexService productSexService;
    private final TnsService tnsService;
    private final AddressService addressService;
    private final CouponService couponService;
    private final TakeuptimeService takeuptimeService;
    private final TnsSjService tnsSjService;
    private final StoreService storeService;
    private final PromotionService promotionService;
    private final TnsPjService tnsPjService;
    private final DiagnosisService diagnosisService;
    private final UserService userService;

    @RequestMapping(value = {"/diagnosisDetail"}, method = {RequestMethod.GET})
    @ApiOperation(value = "诊断书详情", notes = "诊断书详情", response = GlobalResult.class)
    public GlobalResult getDiagnosisDetail(@RequestParam(value = "id", required = false) String id) {
        DiagnosisOutputDTO diagnosis = diagnosisService.findDiagnosisById(id);
        diagnosis.setCreatetime(TimeUtil.TimeConvert(diagnosis.getCreatetime()));
        return GlobalResult.ok(diagnosis);
    }


    //订单信息接口
    @GetMapping("/{orderId}/detail")
    @ApiOperation(value = "订单信息接口", notes = "订单信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = false),
            @ApiImplicitParam(name = "orderId", value = "应该是订单id", required = true)}
    )
    public GlobalResult detail(String userId, @PathVariable String orderId) {
        OrderDTO o = orderService.findOrderDetailById(orderId);
        OrderShowDTO order = new OrderShowDTO();
        if (o != null) {
            // 是否可以取消
            if ((o.getZt().equals("1")) || (o.getZt().equals("2"))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dstr = TimeUtil.TimeConvert(o.getRq()) + " " + o.getSj();
                Date date = null;
                try {
                    date = sdf.parse(dstr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date.getTime() < System.currentTimeMillis() - 3600000L) {
                    order.setCancelflag(0);
                } else {
                    order.setCancelflag(1);
                }
            }
            order.setId(o.getId());
            order.setOrder_sj(TimeUtil.TimeConvert(o.getRq()) + " " + o.getSj());
            order.setMoney(o.getZje());
            order.setPid(o.getPid());
            ProductDTO p = this.productService.findById(o.getPid());
            if (p != null) {
                order.setProduct_name(p.getXmmc());
                order.setTp(p.getTp());
            } else {
                order.setProduct_name("该项目已删除");
            }
            if ((o.getTid() == 0) || (this.tnsService.findById(o.getTid() + "") == null)) {
                order.setTns_name("未指派");
            } else {
                MassagerDTO m = this.tnsService.findById(o.getTid() + "");
                order.setTns_phone(m.getPhone());
                order.setTns_name(m.getXm());
            }
            order.setYjjtmoney(o.getYjjtmoeny());
            order.setZt(o.getZt());
            order.setCreatetime(TimeUtil.TimeConvertMin(o.getCreateTime()));
            order.setPaymenttype(o.getPaymenttype());
            order.setTid(o.getTid());
            if (this.tnsService.findPjByOrderId(o.getId()) == null) {
                order.setSfpj(0);
            } else {
                order.setSfpj(1);
            }
            order.setProcuct_sl(o.getSl());
            order.setProductType(p.getType());
            AddressDTO a = addressService.findAddressById(o.getDid());
            if (a != null) {
                order.setContact(a.getContact());
                order.setPhone(a.getPhone());
                order.setAddress(a.getAddressDetail());
                order.setMph(a.getMph());
                order.setLat(Double.valueOf(a.getLat()));
                order.setLng(Double.valueOf(a.getLng()));
            }
            order.setRemark(o.getRemark());
            order.setPromotionmoney(o.getPromotionmoney());
            order.setLevelmoney(o.getLevelmoney());
            order.setMassagelevel(o.getMassagelevel());
            order.setMassagersex(o.getMassagersex());
            order.setSexmoney(o.getSexmoney());
            ;
            order.setSettleprice(o.getSettleprice());
            order.setUid(o.getUid());
            CouponDTO coupon = this.couponService.getCouponByOid(o.getId() + "");
            if (coupon != null && coupon.getId() != 0) {
                order.setCouponMoney(coupon.getMoney());
            } else {
                order.setCouponMoney("0");
            }
        }
        return GlobalResult.ok(order);
    }

    /**
     * 获得推拿师的时间安排表
     */
    @GetMapping("/getTimetable")
    @ApiOperation(value = "获得推拿师的时间安排表", notes = "获得推拿师的时间安排表", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tid", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pid", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sl", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "lat", paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "lng", paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "did", paramType = "query", dataType = "int")
    })
    public GlobalResult getTimetable(@RequestParam(value = "tid", required = false) String tid,
                                     @RequestParam(value = "pid", required = false) int pid,
                                     @RequestParam(value = "sl", required = false) int sl,
                                     @RequestParam(value = "lat", required = false) Double lat,
                                     @RequestParam(value = "lng", required = false) Double lng,
                                     @RequestParam(value = "did", required = false) Integer did) {
        ProductDTO productDTO = productService.findById(pid);
        List<ProductTime> times = productService.queryProductTimeList(pid);
        List<Timetable> dayList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        String dayName;
        String nickDate;
        // 转换当前时间
        int nowDot = 4 * now.get(Calendar.HOUR_OF_DAY);
        nowDot += TimeUtil.getMinuteDot(now);
        // 默认为9:00-21:30
        int defaultBeginDot = productDTO.getMinTime() == null ? 36 : productDTO.getMinTime() * 2;
        int defaultEndDot = productDTO.getMaxTime() == null ? 86 : productDTO.getMaxTime() * 2;
        // 2,5--17:30
        if (productDTO.getSaleType().equals("2") || productDTO.getSaleType().equals("5")) {
            defaultEndDot = 70;
        }
        // 3,6--18:30
        if (productDTO.getSaleType().equals("3") || productDTO.getSaleType().equals("6")) {
            defaultEndDot = 74;
        }
        // 4,7--19:30
        if (productDTO.getSaleType().equals("4") || productDTO.getSaleType().equals("7")) {
            defaultEndDot = 78;
        }
        //技师限制夜间接单范围后，根据距离判断此字段
        int massagerDot = 9999;
        String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        List<OrderDTO> orderList = new ArrayList<>();
        List<TakeUpTime> takeUpTimes = new ArrayList<>();
        //计算当天的初始间隔时间
        int defaultBeginInterval = 6;
        if (productDTO.getXmmc().contains("加时通")) {
            defaultBeginInterval = 1;
        } else if (tid != null && !"0".equals(tid)) {
            MassagerDTO massagerDTO = tnsService.findById(tid);
            //获取技师时间表时，查询订单，请假表等信息
            orderList = orderService.findOrdersOfMassagerWillDoInFuture(tid);
            //BeanUtils.copyProperties(orders,orderList);
            takeUpTimes = takeuptimeService.getTimeRecord(tid);
            //根据距离计算技师时间表的初始间隔时间
            List<OrderDTO> orders1 = orderService.findOrdersOfMassagerToday(tid);
            OrderDTO lastOrder = getLastOrder(orders1, productDTO.getFzsj());
            String[] location = massagerDTO.getLocation().split(",");
            Coord coord = Distance.fromMars2BD(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
            double distance;
            if (lng == null || lat == null) {
                distance = 10000;
            } else {
                distance = Distance.getDistance(lng, lat, coord.getLng(), coord.getLat());
            }
            if (lastOrder != null) {
                if (did != null && did.intValue() == lastOrder.getDid()) {
                    distance = 0;
                    defaultBeginInterval = 1;
                } else if (lat != null && lng != null) {
                    defaultBeginInterval = orderService.calInterval(lat, lng, lastOrder.getLat(), lastOrder.getLng()) / 15 + 1;
                }
            } else {
                defaultBeginInterval = orderService.calIntervalByDistance(distance) / 15 + 1;
            }
            if (massagerDTO.getLaterDot() != null && massagerDTO.getLaterRange() != null
                    && distance > massagerDTO.getLaterRange() * 1000) {
                massagerDot = massagerDTO.getLaterDot() * 2 - 1;
            }
        }


        List<NightTrafficFee> nightTrafficFees = orderService.getNightTrafficFee();
        for (int i = 0; i < 7; i++) {
            long targetDayMilliseconds = now.getTimeInMillis() + 1000 * 60 * 60 * 24 * i;
            Calendar targetDay = Calendar.getInstance();
            targetDay.setTimeInMillis(targetDayMilliseconds);
            //项目特殊时间限定
            int beginDot = defaultBeginDot;
            int endDot = defaultEndDot;
            for (ProductTime time : times) {
                if (time.getDayOfWeek() == targetDay.get(Calendar.DAY_OF_WEEK)) {
                    beginDot = time.getBegin() * 2;
                    endDot = time.getEnd() * 2;
                    break;
                }
            }
            if (massagerDot < endDot) {
                endDot = massagerDot;
            }
            dayName = dayNames[targetDay.get(Calendar.DAY_OF_WEEK) - 1];
            if (i == 0) {
                nickDate = "今天";
            } else if (i == 1) {
                nickDate = "明天";
            } else {
                nickDate = (targetDay.get(Calendar.MONTH) + 1) + "月" + targetDay.get(Calendar.DAY_OF_MONTH) + "日";
            }
            List<Time> tlist = new ArrayList<>();
            for (int j = 0; j <= 95; j++) {
                if (j % 4 == 0) {
                    tlist.add(new Time(j / 4 + ":00", ""));
                } else if (j % 4 == 1) {
                    tlist.add(new Time(j / 4 + ":15", ""));
                } else if (j % 4 == 2) {
                    tlist.add(new Time(j / 4 + ":30", ""));
                } else if (j % 4 == 3) {
                    tlist.add(new Time(j / 4 + ":45", ""));
                }
            }

            for (int j = 0; j <= 95; j++) {
                for (NightTrafficFee nightTrafficFee : nightTrafficFees) {
                    if (j + Math.ceil(Integer.parseInt(productDTO.getFzsj()) * sl / 15) >= nightTrafficFee.getDot()) {
                        tlist.get(j).setState("nightFee");
                    }
                }
                if (j < beginDot) {
                    tlist.get(j).setState("notShow");
                    continue;
                }
                if (j > endDot) {
                    tlist.get(j).setState("notShow");
                    continue;
                }
                if (i == 0) {
                    //当前时间在9点之前的，当天订单只能下10:30之后的
//                    if (nowDot < 36 && j < 42) {
//                        tlist.get(j).setState("notShow");
//                        continue;
//                    }
                    // 9点之后订单预留一定时间
                    if(productDTO.getType()==1){
                        defaultBeginInterval = 1;
                    }
                    if (j < nowDot + defaultBeginInterval) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                }
                if (i == 1) {
                    if (nowDot > 88 && j < 0) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                    if (j < 1 && nowDot == 91) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                    if (j < 2 && nowDot == 92) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                    if (j < 3 && nowDot == 93) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                    if (j < 4 && nowDot == 94) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                    if (j < 5 && nowDot == 95) {
                        tlist.get(j).setState("notShow");
                        continue;
                    }
                }
            }
            // 根据推拿师的订单修改时间表的状态
            for (int k = 0; k < orderList.size(); k++) {
                int dot = 6;//默认90min
                if (did != null && did == orderList.get(k).getDid()) {
                    //相同地址下单则取消时间间隔
                    dot = 1;
                } else if (lat != null && lng != null) {
                    dot = orderService.calInterval(lat, lng, orderList.get(k).getLat(), orderList.get(k).getLng()) / 15;
                } else if(productDTO.getType()==1){
                    dot = 1;
                }
                if (Long.parseLong(orderList.get(k).getRq()) < (targetDayMilliseconds / 1000) &&
                        Long.parseLong(orderList.get(k).getRq()) > ((targetDayMilliseconds / 1000) - (24 * 60 * 60))) {
                    ProductDTO productDTO1 = this.productService.findById(orderList.get(k).getPid());
                    double sj = orderList.get(k).getSl() * Integer.parseInt(productDTO1.getFzsj());
                    for (int q = 0; q < tlist.size(); q++) {
                        if (orderList.get(k).getSj().equals(tlist.get(q).getTime())) {
                            for (int p = 1 - dot; p < Math.ceil(sj / 15) + dot; p++) {
                                if (tlist.size() > p + q && p + q >= 0 && !tlist.get(q + p).getState().equals("notShow")) {
                                    tlist.get(q + p).setState("alreadyDate");
                                }
                            }
                        }
                    }
                }
                if (Long.parseLong(orderList.get(k).getRq()) < ((targetDayMilliseconds / 1000) - (24 * 60 * 60)) &&
                        Long.parseLong(orderList.get(k).getRq()) > ((targetDayMilliseconds / 1000) - (24 * 60 * 60 * 2))) {
                    ProductDTO productDTO1 = this.productService.findById(orderList.get(k).getPid());
                    double sj = orderList.get(k).getSl() * Integer.parseInt(productDTO1.getFzsj());
                    String [] serviceTimeArr = orderList.get(k).getSj().split(":");
                    Integer serviceTime = Integer.valueOf(serviceTimeArr[0]) * 4 + Integer.valueOf(serviceTimeArr[1])/15;
                    Double exceed = serviceTime+dot+Math.ceil(sj / 15)-96;
                    if(exceed>0){
                        for (int q = 0; q < exceed.intValue(); q++) {
                            tlist.get(q).setState("alreadyDate");
                        }
                    }
                }

                if (Long.parseLong(orderList.get(k).getRq()) < ((targetDayMilliseconds / 1000) + (24 * 60 * 60)) &&
                        Long.parseLong(orderList.get(k).getRq()) > ((targetDayMilliseconds / 1000))) {
                    String [] serviceTimeArr = orderList.get(k).getSj().split(":");
                    Integer serviceTime = Integer.valueOf(serviceTimeArr[0]) * 4 + Integer.valueOf(serviceTimeArr[1])/15;
                    Integer exceed = serviceTime-dot;
                    if(exceed<0){
                        for (int q = 0; q < Math.abs(exceed); q++) {
                            tlist.get(95 - q).setState("alreadyDate");
                        }
                    }
                }

            }
            // 根据占时间表修改时间表状态
            if (takeUpTimes != null && takeUpTimes.size() > 0) {
                for (int t = 0; t < takeUpTimes.size(); t++) {
                    TakeUpTime takeUpTime = takeUpTimes.get(t);
                    if ("y".equals(takeUpTime.getStatus()) && "y".equals(takeUpTime.isThisDayOk(targetDay.getTime()))) {
                        if ("y".equals(takeUpTime.getIsEveryWeek()) || Long.parseLong(takeUpTime.getCreatetime()) + 7 * 24 * 3600 > targetDay.getTimeInMillis() / 1000) {
                            for (int index = 0; index < tlist.size(); index++) {
                                if (Integer.parseInt(tlist.get(index).getTime().replace(":", "")) >= Integer.parseInt(takeUpTime.getBeginTime().replace(":", ""))
                                        && Integer.parseInt(tlist.get(index).getTime().replace(":", "")) <= Integer.parseInt(takeUpTime.getEndTime().replace(":", ""))) {
                                    if (!tlist.get(index).getState().equals("notShow"))
                                        tlist.get(index).setState("alreadyDate");
                                }
                            }
                        }
                    }
                }
            }
            // 根据推拿师请假表修改状态
            MassagerTime massagerTime = null;
            if (tid != null && !"0".equals(tid)) {
                massagerTime = tnsSjService.findMassagerTimeByRqAndTid(targetDayMilliseconds / 1000
                                - (targetDay.get(Calendar.HOUR_OF_DAY) * 60 * 60 + targetDay.get(Calendar.MINUTE) * 60
                                + targetDay.get(Calendar.SECOND)),
                        Integer.parseInt(tid));
            }
            if (massagerTime != null && massagerTime.getSj() != null && !"".equals(massagerTime.getSj())) {
                String[] sjArr = massagerTime.getSj().split(",");
                for (int j = 0; j < tlist.size(); j++) {
                    for (int k = 0; k < sjArr.length; k++) {
                        if (sjArr[k].equals(tlist.get(j).getTime()) && !"notShow".equals(tlist.get(j).getState())) {
                            tlist.get(j).setState("alreadyDate");
                            break;
                        }
                    }
                }
            }
            double sj = sl * Integer.parseInt(productDTO.getFzsj());
            // 根据客户选择的产品和数量修改时间表状态
            for (int q = 0; q < tlist.size(); q++) {
                if (tlist.get(q).getState().equals("alreadyDate")) {
                    for (double p = Math.ceil(sj / 15); p > 0; p--) {
                        if (q - p >= 0 && !tlist.get((int) (q - p)).getState().equals("alreadyDate")
                                && !tlist.get((int) (q - p)).getState().equals("notShow")) {
                            tlist.get((int) (q - p)).setState("canNotClick");
                        }
                    }
                }
            }
            Iterator<Time> timeInte = tlist.iterator();
            while (timeInte.hasNext()) {
                Time time = timeInte.next();
                if (("notShow").equals(time.getState())) {
                    timeInte.remove();
                }
            }
            Timetable timetable = new Timetable((targetDay.get(Calendar.YEAR)) + "-"
                    + (targetDay.get(Calendar.MONTH) + 1) + "-" + targetDay.get(Calendar.DAY_OF_MONTH),
                    nickDate + " " + dayName, tlist);
            if (timetable.getNickDate().contains("今天")) {
                if (timetable.getTimetable().size() > 0) {
                    dayList.add(timetable);
                }
            } else {
                dayList.add(timetable);
            }
        }
        return GlobalResult.ok(dayList);
    }

    /**
     * 获得推拿师的请假表展示
     */
    @GetMapping("/getSchedule")
    @ApiOperation(value = "获得推拿师的请假表展示", notes = "获得推拿师的请假表展示", response = GlobalResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "tid", paramType = "query", dataType = "String")
    })
    public GlobalResult getSchedule(@RequestParam(value = "tid", required = false) String tid) {
        List<Timetable> dayList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        String dayName;
        String nickDate;
        List<OrderDTO> orderList = new ArrayList<>();
        if (tid != null && !"0".equals(tid)) {
            orderList = orderService.findOrdersOfMassagerWillDoInFuture(tid);
        }
        String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        for (int i = 0; i < 7; i++) {
            int beginDot = 0;
            int endDot = 95;
            long targetDayMilliseconds = now.getTimeInMillis() + 1000 * 60 * 60 * 24 * i;
            Calendar targetDay = Calendar.getInstance();
            targetDay.setTimeInMillis(targetDayMilliseconds);
            nickDate = targetDay.get(Calendar.MONTH) + 1 + "月" + targetDay.get(Calendar.DAY_OF_MONTH) + "日";
            dayName = dayNames[targetDay.get(Calendar.DAY_OF_WEEK) - 1];
            if (i == 0) {
                nickDate = "今天";
            } else if (i == 1) {
                nickDate = "明天";
            }
            List<Time> tlist = new ArrayList<Time>();
            for (int j = beginDot; j <= endDot; j++) {
                switch (j % 4) {
                    case 0:
                        tlist.add(new Time(j / 4 + ":00", ""));
                        break;
                    case 1:
                        tlist.add(new Time(j / 4 + ":15", ""));
                        break;
                    case 2:
                        tlist.add(new Time(j / 4 + ":30", ""));
                        break;
                    case 3:
                        tlist.add(new Time(j / 4 + ":45", ""));
                        break;
                }
            }

            // 根据推拿师的订单修改时间表的状态
            for (int k = 0; k < orderList.size(); k++) {
                if (Long.parseLong(orderList.get(k).getRq()) < (targetDayMilliseconds / 1000) &&
                        Long.parseLong(orderList.get(k).getRq()) > ((targetDayMilliseconds / 1000) - (24 * 60 * 60))) {
                    Product product1 = new Product();
                    ProductDTO productDTO = this.productService.findById(orderList.get(k).getPid());
                    BeanUtils.copyProperties(productDTO, product1);
                    double sj = orderList.get(k).getSl() * Integer.parseInt(product1.getFzsj());
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
            }
            // 根据占时间表修改时间表状态
            List<TakeUpTime> takeUpTimes = takeuptimeService.getTimeRecord(tid);
            if (takeUpTimes != null && takeUpTimes.size() > 0) {
                for (int t = 0; t < takeUpTimes.size(); t++) {
                    TakeUpTime takeUpTime = takeUpTimes.get(t);
                    if ("y".equals(takeUpTime.getStatus()) && "y".equals(takeUpTime.isThisDayOk(targetDay.getTime()))) {
                        if ("y".equals(takeUpTime.getIsEveryWeek()) || Long.parseLong(takeUpTime.getCreatetime())
                                + 7 * 24 * 3600 > targetDay.getTimeInMillis() / 1000) {
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
            // 根据推拿师请假表修改状态
            MassagerTime massagerTime = tnsSjService.findMassagerTimeByRqAndTid(
                    targetDayMilliseconds / 1000 - (targetDay.get(Calendar.HOUR_OF_DAY) * 60 * 60
                            + targetDay.get(Calendar.MINUTE) * 60 + targetDay.get(Calendar.SECOND)),
                    Integer.parseInt(tid));
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
            // 转换当前时间
            int nowDot = 4 * now.get(Calendar.HOUR_OF_DAY);
            if (now.get(Calendar.MINUTE) >= 45) {
                nowDot += 3;
            } else if (now.get(Calendar.MINUTE) >= 30) {
                nowDot += 2;
            } else if (now.get(Calendar.MINUTE) >= 15) {
                nowDot += 1;
            }
            if (i == 0) {
                for (int j = tlist.size(); j >= 0; j--) {
                    if (beginDot + j <= nowDot) {
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
            Timetable timetable = new Timetable(TimeUtil.TimeConvert(targetDayMilliseconds / 1000 + ""), times);
            dayList.add(timetable);
        }
        return GlobalResult.ok(dayList);
    }


    private OrderDTO getLastOrder(List<OrderDTO> orders, String fzsj) {
        if (orders == null) {
            return null;
        }
        long nowStamp = Calendar.getInstance().getTimeInMillis() / 1000;
        for (OrderDTO order : orders) {
            long time = TimeUtil.strToTime(TimeUtil.long2String(Long.parseLong(order.getRq().toString())) + " " + order.getSj()
                    , "yyyy-MM-dd HH:mm") / 1000 + order.getSl() * Integer.parseInt(fzsj) * 60;
            if (nowStamp - time > 0 && nowStamp - time < 7200) {
                //上笔订单取，结束时间在现在之前两小时以内的
                return order;
            }
        }
        return null;
    }

    /**
     * 抢单
     */
    @RequestMapping(value = {"/takeOrder"}, method = RequestMethod.POST)
    @ApiOperation(value = "抢单", notes = "抢单", response = GlobalResult.class)
    public GlobalResult takeOrder(@RequestBody Map<String, Object> map) {
        return GlobalResult.ok(orderService.takeOrder(map.get("uid").toString(), map.get("oid").toString()));
    }

    /**
     * 新增订单
     */
    @RequestMapping(value = {"/add/{cityId}"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增订单", notes = "新增订单", response = GlobalResult.class)
    public GlobalResult addOrder(@PathVariable String cityId, @RequestBody Map<String, Object> map)
            throws ParseException {
        try {
            if (userService.isBlackUser(map.get("userId").toString()) || StringUtils.isEmpty(map.get("userId").toString())) {
                return GlobalResult.errorMsg("账号异常!");
            }
        } catch (Exception e) {
            return GlobalResult.errorMsg("服务器异常!");
        }

        log.debug("/v1/{}/order/add", cityId);
        Map<String, Object> result = new TreeMap<String, Object>();

        com.mmd.domain.dto.profile.OrderDTO order = new com.mmd.domain.dto.profile.OrderDTO();
        if ((map.get("tid") != null) && (map.get("tid") != "")) {
            order.setTid(Integer.parseInt(map.get("tid").toString()));
        } else {
            order.setTid(0);
        }
        order.setPid(Integer.parseInt(map.get("pid").toString()));
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(map.get("rq").toString());
        order.setRq(date.getTime() / 1000 + "");
        order.setSj(map.get("sj").toString());
        Integer sl = Integer.parseInt(map.get("sl").toString());
        order.setSl(sl);
        ProductDTO p = this.productService.findById(order.getPid());

        order.setUid(map.get("userId").toString());
        if (!StringUtils.isEmpty(map.get("recommendid"))) {
            order.setRecommendid(map.get("recommendid").toString());
        }
        if ((map.get("sid") != null) && (map.get("sid") != "")) {
            order.setSid(Integer.parseInt(map.get("sid").toString()));
            Store store = storeService.queryStoreById(map.get("sid").toString());
            Address address = new Address();
            address.setAddressDetail(store.getAddress());
            String[] coord = store.getCoord().split(",");
            address.setLng(coord[0]);
            address.setLat(coord[1]);
            address.setUid(Integer.valueOf(map.get("userId").toString()));
            address.setPhone(map.get("phone").toString());
            address.setType(2);
            this.addressService.addAddressNew(address);
            order.setDid(address.getId());
        } else {
            order.setSid(null);
            if (map.get("addressDetail") == null) {
                order.setDid(Integer.parseInt(map.get("addressId").toString()));
            } else {
                Address address = new Address();
                address.setAddressDetail(map.get("addressDetail").toString());
                address.setUid((int) map.get("userId"));
                address.setMph(map.get("mph").toString());
                address.setPhone(map.get("phone").toString());
                if (Integer.parseInt(map.get("addressId").toString()) == 0) {
                    this.addressService.AddAddress(address);
                    order.setDid(address.getId());
                } else {
                    address.setId(Integer.parseInt(map.get("addressId").toString()));
                    this.addressService.UpdateAddress(address);
                    order.setDid(Integer.parseInt(map.get("addressId").toString()));
                }
            }
        }
        order.setRemark(map.get("remark").toString());
        order.setCreateTime((System.currentTimeMillis() + "").substring(0, 10));
        // 判断是否应缴夜间交通费
        order.setYjjtmoeny("0.00");
        if (p.getType() != 1) {
            List<NightTrafficFee> nightTrafficFees = orderService.getNightTrafficFee();
            String time = order.getSj();
            int minutes = Integer.parseInt(time.substring(0, time.indexOf(":"))) * 60
                    + Integer.parseInt(time.substring(time.indexOf(":") + 1, time.length()));
            for (NightTrafficFee nightTrafficFee : nightTrafficFees) {
                if (minutes + order.getSl() * Integer.parseInt(p.getFzsj()) >= nightTrafficFee.getDot() * 15) {
                    order.setYjjtmoeny(nightTrafficFee.getFee() + "");
                }
            }
        }

        // 计算订单金额
        order.setJe(p.getJg());
        order.setZje(order.getSl() * Double.parseDouble(order.getJe()) + "");
        //计算星级额外收费
        Double levelMoney = 0.0;
        if (map.get("massageLevel") != null) {
            int level = Integer.parseInt(map.get("massageLevel").toString());
            order.setMassagelevel(level);
            LevelRefer refer = tnsService.queryLevelRefer(level, order.getPid());
            if (refer != null) {
                levelMoney = sl * refer.getExtra();
            }
        } else {
            order.setMassagelevel(1);
        }
        order.setLevelmoney(levelMoney.toString());
        // 计算订单享受优惠金额
        Promotion promotion = this.promotionService.findOnSalePromotionByPid(p.getId());
        if (promotion != null) {
            OrderQueryInputDTO orderQueryInputDTO = new OrderQueryInputDTO();
            orderQueryInputDTO.setZt("(1,2,3,4,5,7,9)");
            orderQueryInputDTO.setUserId(map.get("userId").toString());
            orderQueryInputDTO.setPageSize(5 + "");
            orderQueryInputDTO.setCurrentPage(1 + "");
            OrderPersistence orderPersistence = new OrderPersistence();
            BeanUtils.copyProperties(orderQueryInputDTO, orderPersistence);
            int orderCount =
                    (int) orderService.findOrdersByZt(orderQueryInputDTO.makePaging(), orderPersistence).getSize();
            double zje = order.getSl() * Double.parseDouble(order.getJe());
            double discount = 0;
            if ("y".equals(promotion.getIsnew()) && orderCount > 0) {

            } else {
                for (int i = 1; i <= promotion.getTimes(); i++) {
                    if (promotion.getDiscount().intValue() * i > zje) {
                        break;
                    }
                    discount = promotion.getDiscount().intValue() * i;
                }
            }
            order.setPromotionmoney(discount + "");
        } else {
            order.setPromotionmoney("0");
        }
        //计算性别费用
        String sexMoney = "0";
        if (map.get("massagerSex") != null) {
            ProductSex productSex = productSexService.findProductSexByPid(order.getPid() + "");
            if (productSex != null) {
                String massagerSex = map.get("massagerSex").toString();
                if ("male".equals(massagerSex)) {
                    order.setMassagersex("male");
                    sexMoney = productSex.getMale() * sl + "";
                }
                if ("famale".equals(massagerSex)) {
                    order.setMassagersex("famale");
                    sexMoney = productSex.getFamale() * sl + "";
                }
            }
        }
        order.setSexmoney(sexMoney);
        //计算数量优惠
        List<NumPromotion> numPromotions = productService.findNumPromotionsByPid((long) order.getPid());
        double numdiscount = 0;
        for (NumPromotion numPromotion : numPromotions) {
            if (sl >= numPromotion.getNumber()) {
                numdiscount = Double.valueOf(numPromotion.getDiscount());
            }
        }
        order.setNumdiscount(numdiscount + "");

        if (map.get("way") != null) {
            order.setWay(map.get("way").toString());
        } else {
            order.setWay("未知");
        }
        // TODO 校验时间是否可下单

        int ret = this.orderService.AddOrder(order);
        if (ret > 0) {
            result.put("orderid", Integer.valueOf(order.getId()));
            result.put("name", p.getXmmc() + "*" + order.getSl());
            result.put("yjjtmoney", order.getYjjtmoeny());
            result.put("je", order.getZje());
            result.put("promotionmoney", order.getPromotionmoney());
            result.put("massagelevel", map.get("massageLevel") == null ? 1 : map.get("massageLevel"));
            result.put("levelmoney", levelMoney);
            result.put("massagersex", order.getMassagersex());
            result.put("sexmoney", order.getSexmoney() == null ? 0 : order.getSexmoney());
            result.put("numdiscount", numdiscount);
        } else {
            result.put("orderid", 0);
        }
        return GlobalResult.ok(result);
    }

    @RequestMapping(value = {"/commentDetail"}, method = {RequestMethod.GET})
    public GlobalResult getCommentByOid(String oid) {
        return GlobalResult.ok(tnsPjService.getEstimationByOid(oid));
    }

    /**
     * "写诊断书"
     *
     * @param map
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = {"/addDiagnosis"}, method = {RequestMethod.POST})
    @ApiOperation(value = "写诊断书", notes = "写诊断书", response = GlobalResult.class)
    public GlobalResult addDiagnosis(@RequestBody Map<String, Object> map) throws ParseException {
        Map<String, Object> result = new TreeMap<>();
        if (map.get("oid") == null || map.get("uid") == null || map.get("tid") == null) {
            result.put("success", false);
            result.put("errMsg", "数据不能为空！");
            return GlobalResult.ok(result);
        }
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setOid(Integer.parseInt(map.get("oid").toString()));
        diagnosis.setUid(Integer.parseInt(map.get("uid").toString()));
        diagnosis.setTid(Integer.parseInt(map.get("tid").toString()));
        diagnosis.setSymptom(map.get("symptom").toString());
        diagnosis.setSuggestion(map.get("suggestion").toString());
        diagnosis.setChat(Integer.parseInt(map.get("chat").toString()));
        diagnosis.setStrength(Integer.parseInt(map.get("strength").toString()));
        if (map.get("health") != null) {
            diagnosis.setHealth(map.get("health").toString());
        }
        if (map.get("body") != null) {
            diagnosis.setBody(map.get("body").toString());
        }
        if (map.get("product") != null) {
            diagnosis.setProduct(map.get("product").toString());
        }
        if (map.get("other") != null) {
            diagnosis.setOther(map.get("other").toString());
        }
        if (orderService.addDiagnosis(diagnosis) > 0) {
            result.put("success", true);
        }
        return GlobalResult.ok(result);
    }

    /**
     * 获取诊断历史
     *
     * @param userId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/diagnosisList"}, method = {RequestMethod.GET})
    @ApiOperation(value = "获取诊断历史", notes = "获取诊断历史", response = GlobalResult.class)
    public GlobalResult getDiagnosisList(@RequestParam(value = "userId", required = false) String userId) throws IOException {
        List<DiagnosisOutputDTO> diagnosisList = orderService.getDiagnosisList(userId);
        for (DiagnosisOutputDTO diagnosis : diagnosisList) {
            diagnosis.setCreatetime(TimeUtil.TimeConvert(diagnosis.getCreatetime()));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(diagnosisList);
        List<Object> list = objectMapper.readValue(json, new TypeReference<ArrayList<Object>>() {
        });
        return GlobalResult.ok(list);
    }

}
