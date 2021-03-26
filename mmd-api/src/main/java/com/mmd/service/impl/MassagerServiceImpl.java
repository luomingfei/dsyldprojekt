package com.mmd.service.impl;

import com.mmd.TimeUtil;
import com.mmd.dao.MassagerMapper;
import com.mmd.entity.*;
import com.mmd.service.MassagerService;
import com.mmd.service.OrderService;
import com.mmd.utils.Distance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/1 21:41
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MassagerServiceImpl implements MassagerService {
    private final MassagerMapper massagerMapper;
    private final OrderService orderService;
    @Override
    public List<TakeUpTime> getTimeRecord(String tid) {
        return massagerMapper.getTimeRecord(tid);
    }
    @Override
    public void filterMassagers(List<Massager> massagers, String serviceTime, long beginTime, long endTime, double lat, double lng, Integer did) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (massagers == null || massagers.size() == 0) {
            return;
        }
        Iterator<Massager> it = massagers.iterator();
        while (it.hasNext()) {
            //根据订单排除推拿师
            if (!isOrderTimeOkay(it.next(), beginTime, endTime, lat, lng, did)) {
                it.remove();
            }
        }
        //技师距离可能已变更，此处需重新排序
        Collections.sort(massagers);
        it = massagers.iterator();
        while (it.hasNext()) {
            boolean hasRemoved = false;
            Massager massager = it.next();
            //根据占时间表排除
            List<TakeUpTime> takeUpTimes = getTimeRecord(massager.getId());
            for (int k = 0; k < takeUpTimes.size(); k++) {
                TakeUpTime takeUpTime = takeUpTimes.get(k);
                if ("y".equals(takeUpTime.getStatus()) && "y".equals(takeUpTime.isThisDayOk(new Date(beginTime)))) {
                    if ("y".equals(takeUpTime.getIsEveryWeek()) || Long.parseLong(takeUpTime.getCreatetime()) + 7 * 24 * 3600 > beginTime / 1000) {
                        Long takeBeginTime = sdf.parse(serviceTime.substring(0, 10).trim() + " " + takeUpTime.getBeginTime()).getTime();
                        Long takeEndTime = sdf.parse(serviceTime.substring(0, 10).trim() + " " + takeUpTime.getEndTime()).getTime();
                        if ((beginTime <= takeEndTime && beginTime >= takeBeginTime)
                                || (endTime >= takeBeginTime && endTime <= takeEndTime)
                                || (beginTime <= takeBeginTime && endTime >= takeEndTime)) {
                            it.remove();
                            hasRemoved = true;
                            break;
                        }
                    }
                }
            }
            if (hasRemoved) {
                continue;
            }
            //根据请假时间排除推拿师
            String rq = serviceTime.substring(0, 10).trim();
            long rqNum = TimeUtil.strToTime(rq);
            MassagerTime massagerTime = findMassagerTimeByRqAndTid(rqNum / 1000, Integer.parseInt(massager.getId()));
            if (massagerTime != null && massagerTime.getSj() != null && !"".equals(massagerTime.getSj())) {
                String[] sjArr = massagerTime.getSj().split(",");
                for (int j = 0; j < sjArr.length; j++) {
                    Long time = sdf.parse(rq + " " + sjArr[j]).getTime();
                    if (time >= beginTime && time <= endTime) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }
    public MassagerTime findMassagerTimeByRqAndTid(long rq, int tid) {
        return massagerMapper.findMassagerTimeByRqAndTid(rq, tid);
    }
    /**
     * 根据订单判断某位技师是否可选
     * 1.查询技师预约当天订单
     * 2.获取技师当天上下笔订单（上笔订单的结束时间不早于预约时间2h，下笔订单的开始时间不晚于预约时段的2h）
     * 3.根据上下笔订单判断是否可选
     *      3.1 前后均无订单，技师地址取固定地址，判断预约时间与当前时间的间隔是否可行
     *      3.2 前后均有订单，技师地址取订单地址，分别计算前后订单当前是否可行，订单时间是否冲突
     *      3.3 只有前有订单，技师地址取订单地址，计算当前是否可行，订单时间是否冲突
     *      3.4 只有后有订单，技师地址取订单地址，计算当前是否可行，订单时间是否冲突
     */
    public boolean isOrderTimeOkay (Massager massager, long beginTime, long endTime, double lat, double lng, Integer did) {
        //1
        Long stampOfDay = TimeUtil.long2long(beginTime) / 1000;
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            OrderInfo orderInfo = massagerMapper.queryMassageOrderBylastDay(massager.getId(),stampOfDay-(24 * 60 * 60),true);
//            if(orderInfo!=null){
//                String begin = TimeUtil.long2String(orderInfo.getRq()) + " " + orderInfo.getSj();
//                long beginStamp = sdf.parse(begin).getTime();
//                long sjcha = beginTime - beginStamp - orderInfo.getSl() * orderInfo.getFzsj() * 60 * 1000;
//                if (!(sjcha >= (2 * 60 * 60 * 1000))) {
//                    return false;
//                }
//            }
//            OrderInfo orderInfo1 = massagerMapper.queryMassageOrderBylastDay(massager.getId(),stampOfDay+(24 * 60 * 60),false);
//            if(orderInfo1!=null){
//                String begin = TimeUtil.long2String(orderInfo1.getRq()) + " " + orderInfo1.getSj();
//                long beginStamp = sdf.parse(begin).getTime();
//                long sjcha =  beginStamp - endTime;
//                if(!(sjcha >= (2 * 60 * 60 * 1000))){
//                    return false;
//                }
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
        //2
        List<OrderInfo> orders = massagerMapper.queryMassageOrderByDay(massager.getId(), stampOfDay);
        OrderInfo last = null, next = null;
        try {
            Map<String, OrderInfo> result = getLastAndNextOrderInfo(orders, beginTime, endTime);
            if (result != null) {
                last = result.get("last");
                next = result.get("next");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        //3
        Double d1 = calOrderInfoDetail(lat, lng, last, did);
        Double d2 = calOrderInfoDetail(lat, lng, next, did);
        //3.1
        if (last == null && next == null) {
            //前后均无订单，距离采用技师地址,只需考虑当前是否支持
            if (isNowOkay(beginTime, massager.getDistance())) {
                return true;
            }
            return false;
        }
        //3.2
        if (last != null && next != null) {
            //前后均有订单，判断技师是否有空，距离采用较近的订单地址
            if (!isNowOkay(beginTime, d1)) {
                return false;
            }
            if (!isNowOkay(beginTime, d2)) {
                return false;
            }
            massager.setDistance(d1 > d2 ? d2 : d1);
            if ((beginTime >= last.getBegin() && beginTime < last.getEnd() + last.getInterval())
                    || (endTime > next.getBegin() - next.getInterval())) {
                return false;
            }
            return true;
        }
        //3.3
        if (next == null) {
            //前有订单，判断技师是否有空，距离采用订单地址
            massager.setDistance(d1);
            if (!isNowOkay(beginTime, massager.getDistance())) {
                return false;
            }
            if (beginTime >= last.getBegin() && beginTime < last.getEnd() + last.getInterval()) {
                return false;
            }
            return true;
        }
        //3.4
        //后有订单，判断技师是否有空，距离采用订单地址
        massager.setDistance(d2);
        if (!isNowOkay(beginTime, massager.getDistance())) {
            return false;
        }
        if (endTime > next.getBegin() - next.getInterval()) {
            return false;
        }

        return true;
    }

    public Map<String, OrderInfo> getLastAndNextOrderInfo (List<OrderInfo> orders, long beginTime, long endTime) throws ParseException {
        if (orders.size() == 0) {
            return null;
        }
        OrderInfo last = null, next = null;
        long subOfLast = -1, subOfNext =  -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (OrderInfo order : orders) {
            String begin = TimeUtil.long2String(order.getRq()) + " " + order.getSj();
            long beginStamp = sdf.parse(begin).getTime();
            order.setBegin(beginStamp);
            order.setEnd(beginStamp + order.getSl() * order.getFzsj() * 60 * 1000);
            if (beginStamp <= beginTime) {
                //已有订单比预约时间早或等
                if (subOfLast < 0) {
                    //第一个早于预约时间的订单
                    last = order;
                    subOfLast = beginTime - beginStamp;
                } else {
                    long sub = beginTime - beginStamp;
                    if (sub < subOfLast) {
                        last = order;
                        subOfLast = sub;
                    }
                }
            } else {
                //已有订单比预约时间晚
                if (subOfNext < 0) {
                    //第一个晚于预约时间的订单
                    next = order;
                    subOfNext = beginStamp - beginTime;
                } else {
                    long sub = beginStamp - beginTime;
                    if (sub < subOfNext) {
                        next = order;
                        subOfNext = sub;
                    }
                }
            }
        }
        //对于上一笔订单，此单结束时间比预约时间早两小时以上则不计算在内
        if (last != null) {
            if ((subOfLast - last.getSl() * last.getFzsj() * 60 * 1000) > (2 * 60 * 60 * 1000)) {
                last = null;
            }
        }
        //对于下一笔订单，此单开始时间比预约结束时间晚两小时以上则不计算在内
        if (next != null) {
            if ((subOfNext - (endTime - beginTime)) > (2 * 60 * 60 * 1000)) {
                next = null;
            }
        }
        Map<String, OrderInfo> result = new HashMap<>();
        result.put("last", last);
        result.put("next", next);
        return result;
    }
    /**
     * 计算订单距离，时间间隔
     */
    private Double calOrderInfoDetail (double lat, double lng, OrderInfo info, Integer did) {
        if (info == null) {
            return null;
        }
        if (did != null && did.intValue() == info.getDid().intValue()) {
            info.setDistance(0.0);
            info.setInterval(0);
            return info.getDistance();
        }
        //计算距离
        info.setDistance(Distance.getDistance(lng, lat, info.getLng(), info.getLat()));
        //计算间隔时间
        info.setInterval(orderService.calInterval(lat, lng, info.getLat(), info.getLng()) * 60 * 1000);
        return info.getDistance();
    }
    private boolean isNowOkay (long beginTime, double distance) {
        if (beginTime - Calendar.getInstance().getTimeInMillis()
                < orderService.calIntervalByDistance(distance) * 60 * 1000) {
            return false;
        }
        return true;
    }
}
