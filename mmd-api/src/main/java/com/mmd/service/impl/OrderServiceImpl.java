package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.TimeUtil;
import com.mmd.dao.MassagerMapper;
import com.mmd.dao.NightTrafficFeeMapper;
import com.mmd.dao.OrderMapper;
import com.mmd.domain.dto.input.OrderQueryInputDTO;
import com.mmd.domain.dto.order.OrderDTO;
import com.mmd.domain.dto.order.ProductDTO;
import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.*;
import com.mmd.persistence.OrderPersistence;
import com.mmd.service.OrderService;
import com.mmd.service.ProductService;
import com.mmd.utils.Distance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 16:20
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final NightTrafficFeeMapper nightTrafficFeeMapper;
    private final ProductService productService;
    private final MassagerMapper massagerMapper;
    @Override
    public void createCoupon(String uid, String type, String title, String comment, String money, String minPrice, Integer expireDay) {
        createCouponLimitProduct(uid, type, title, comment, money, minPrice, expireDay, null);
    }

    @Override
    public void createCouponLimitProduct(String uid, String type, String title, String comment, String money, String minPrice, Integer expireDay, String pid) {
        Coupon coupon = new Coupon();
        coupon.setUid(uid);
        coupon.setTitle(title);
        coupon.setComment(comment);
        coupon.setType(type);
        coupon.setMoney(money);
        coupon.setMinPrice(minPrice);
        Long nowStamp = Calendar.getInstance().getTimeInMillis() / 1000;
        coupon.setCreatedAt(nowStamp.toString());
        coupon.setExpireAt((nowStamp + expireDay * 24 * 60 * 60 - 1) + "");
        coupon.setExpireDay(expireDay.toString());
        coupon.setState("1");
        coupon.setPid(pid);
        orderMapper.createCoupon(coupon);
    }

    @Override
    public OrderDTO findOrderDetailById(String orderId) {
        return orderMapper.findOrderDetailById(orderId);
    }

    @Override
    public Map commentInfo(List<Integer>  ids) {
        Map result = new HashMap();
        int t = orderMapper.queryProductTotalComment(ids);
        result.put("total", t);
        if (t == 0) {
            result.put("hpl", "0%");
            return result;
        }
        BigDecimal total = new BigDecimal(t);
        BigDecimal good = new BigDecimal(orderMapper.queryProductGoodComment(ids) * 100);
        result.put("hpl", good.divide(total, 2, BigDecimal.ROUND_HALF_UP).toString() + "%");
        return result;
    }
    @Override
    public String userDefaultSex(String uid, Integer pid) {
        return orderMapper.queryUserLastSelectSexByPid(uid, pid);
    }

    @Override
    public int userDefaultLevel(String uid, Integer pid) {
        Integer level = orderMapper.queryUserLastSelectLevelByPid(uid, pid);
        return level == null ? 1 : level;
    }

    @Override
    public List<OrderDTO> findOrdersOfMassagerWillDoInFuture(String tid) {
        return orderMapper.findOrdersOfMassagerWillDoInFuture(tid);
    }

    @Override
    public List<OrderDTO> findOrdersOfMassagerToday(String tid) {
        return orderMapper.findOrdersOfMassagerToday(tid);
    }

    @Override
    public int calInterval(Double lat, Double lng, Double lat1, Double lng1) {
        return calIntervalByDistance(Distance.getDistance(lng, lat, lng1, lat1));
    }
    @Override
    public int calIntervalByDistance (double distance) {
        if (distance < 5000) {
            //0km-5km：间隔1h 15min
            return 75;
        } else {
            //5km- ：间隔1.5h
            return 90;
        }
    }

    @Override
    public List<NightTrafficFee> getNightTrafficFee() {
        return nightTrafficFeeMapper.getNightTrafficFee();
    }

    @Override
    public Order findById(int id) {
        return this.orderMapper.FindOrderById(id);
    }

    @Override
    public String getLjxdByMassagerId(String id) {
        return orderMapper.getLjjdById(id);
    }

    @Override
    public IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZt(OrderQueryInputDTO orderQueryInputDTO) {
        OrderPersistence orderPersistence=new OrderPersistence();
        BeanUtils.copyProperties(orderQueryInputDTO,orderPersistence);
        IPage<com.mmd.domain.dto.profile.OrderDTO> ordersOfMassagerByZt = orderMapper.findOrdersOfMassagerByZt(orderQueryInputDTO.makePaging(), orderPersistence);
        return ordersOfMassagerByZt;
    }

    @Override
    public IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZtAndTid(OrderQueryInputDTO orderQueryInputDTO) {
        OrderPersistence orderPersistence=new OrderPersistence();
        BeanUtils.copyProperties(orderQueryInputDTO,orderPersistence);
        IPage<com.mmd.domain.dto.profile.OrderDTO> orders = orderMapper.findOrdersOfMassagerByZtAndTid(orderQueryInputDTO.makePaging(),
                orderPersistence);
        return orders;
    }

    @Override
    public IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersByZt(Page iPage, OrderPersistence orderPersistence) {
        return orderMapper.findOrdersByZt(iPage,orderPersistence);
    }

    @Override
    public int AddOrder(com.mmd.domain.dto.profile.OrderDTO order) {
        return orderMapper.AddOrder(order);
    }


    /**
     * 1.订单指派，更新tid、zt
     * 2.清除抢单表，status置'n'
     */
    @Override
    public Boolean takeOrder(String uid, String oid) {
        if (orderMapper.canTake(oid) == 0) {
            return false;
        }
        if (orderMapper.closeTake(oid) == 0) {
            return false;
        }
        orderMapper.takeOrder(uid, oid);
        return true;
    }

    @Override
    public Boolean UpdateOrderZt(String orderId, String ztNow, String ztTo) {
        if (this.orderMapper.UpdateOrderZt(orderId, ztNow, ztTo) == 1)
            return Boolean.valueOf(true);
        return Boolean.valueOf(false);
    }

    @Override
    public int deleteOrder(String orderId) {
        return orderMapper.deleteOrder(orderId);
    }

    @Override
    public int addDiagnosis(Diagnosis diagnosis) {
        return orderMapper.addDiagnosis(diagnosis);
    }

    @Override
    public List<DiagnosisOutputDTO> getDiagnosisList(String userId) {
        return orderMapper.getDiagnosisList(userId);
    }

    @Override
    public List<com.mmd.domain.dto.profile.OrderDTO> getOrdersOfMassager(String tid, String pid, String beginTime, String endTime) {
        return orderMapper.getOrdersOfMassager(tid, pid, beginTime, endTime);
    }

    @Override
    public int updateOrderJe(double je, String oid) {
        return  orderMapper.updateOrderJe(je, oid);
    }

    @Override
    public int updateOrder(String orderid, String paymenttype) {
        return orderMapper.updateOrder(orderid,paymenttype);
    }

    public List<OrderDTO> findOrdersOfMassagerWillDoInFuturea(String tid) {
        return orderMapper.findOrdersOfMassagerWillDoInFuture(tid);
    }

    @Override
    public void filterMassagers4Store(List<Massager> massagers, String serviceTime, long beginTime, long endTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (massagers == null || massagers.size() == 0) {
            return;
        }
        Iterator<Massager> it = massagers.iterator();
        while (it.hasNext()) {
            boolean hasRemoved = false;
            Massager massager = it.next();
            //根据订单排除推拿师
            List<OrderDTO> list = findOrdersOfMassagerWillDoInFuturea(massager.getId());
            for (int j = 0; j < list.size(); j++) {
                String date = TimeUtil.TimeConvert(list.get(j).getRq()) + " " + list.get(j).getSj();
                ProductDTO p = productService.findById(list.get(j).getPid());
                long orderBeginTime = sdf.parse(date).getTime() - 30 * 60 * 1000;
                long orderEndTime = sdf.parse(date).getTime() + (Integer.parseInt(p.getFzsj()) * list.get(j).getSl() + 30) * 60 * 1000;
                if ((beginTime < orderEndTime && beginTime >= orderBeginTime)
                        || (endTime > orderBeginTime && endTime <= orderEndTime)
                        || (beginTime <= orderBeginTime && endTime >= orderEndTime)) {
                    it.remove();
                    hasRemoved = true;
                    break;
                }
            }
            if (hasRemoved) {
                continue;
            }
            //根据占时间表排除
            List<TakeUpTime> takeUpTimes = getTimeRecord(massager.getId());
            for (int k = 0; k < takeUpTimes.size(); k++) {
                TakeUpTime takeUpTime = takeUpTimes.get(k);
                if ("y".equals(takeUpTime.getStatus()) && "y".equals(takeUpTime.isThisDayOk(new Date(beginTime)))) {
                    if ("y".equals(takeUpTime.getIsEveryWeek()) || Long.parseLong(takeUpTime.getCreatetime()) + 7 * 24 * 3600 > beginTime / 1000) {
                        Long takeBeginTime = sdf.parse(serviceTime.substring(0, 10).trim() + " " + takeUpTime.getBeginTime()).getTime();
                        Long takeEndTime = sdf.parse(serviceTime.substring(0, 10).trim() + " " + takeUpTime.getEndTime()).getTime();
                        if ((beginTime < takeEndTime && beginTime >= takeBeginTime)
                                || (endTime > takeBeginTime && endTime <= takeEndTime)
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

    @Override
    public void updateOrderAfterBalancePay(String oid, double settle, String zt) {
        orderMapper.updateOrderAfterBalancePay(oid, settle, zt);
    }

    @Override
    public String queryAidByOid(String oid) {
        return orderMapper.queryAidByOid(oid);
    }

    public List<TakeUpTime> getTimeRecord(String tid) {
        return massagerMapper.getTimeRecord(tid);
    }
    public MassagerTime findMassagerTimeByRqAndTid(long rq, int tid) {
        return massagerMapper.findMassagerTimeByRqAndTid(rq, tid);
    }
}
