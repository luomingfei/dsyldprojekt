package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmd.domain.dto.input.OrderQueryInputDTO;
import com.mmd.domain.dto.order.OrderDTO;
import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.Diagnosis;
import com.mmd.entity.Massager;
import com.mmd.entity.NightTrafficFee;
import com.mmd.entity.Order;
import com.mmd.persistence.OrderPersistence;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 16:20
 */
public interface OrderService {
    void createCoupon(String uid, String type, String title, String comment, String money, String minPrice, Integer expireDay);

    void createCouponLimitProduct(String uid, String type, String title, String comment, String money, String minPrice, Integer expireDay, String pid);

    OrderDTO findOrderDetailById(String orderId);

    Map commentInfo(List<Integer>  ids);

    String userDefaultSex (String uid, Integer pid);

    int userDefaultLevel (String uid, Integer pid);

    List<OrderDTO> findOrdersOfMassagerWillDoInFuture(String tid);

    List<OrderDTO> findOrdersOfMassagerToday(String tid);

    int calInterval(Double lat, Double lng, Double lat1, Double lng1);

    int calIntervalByDistance (double distance);

    List<NightTrafficFee> getNightTrafficFee();

    Order findById(int id);

    String getLjxdByMassagerId(String id);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZt(OrderQueryInputDTO orderQueryInputDTO);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZtAndTid(OrderQueryInputDTO orderQueryInputDTO);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersByZt(Page iPage, OrderPersistence orderPersistence);

    int AddOrder(com.mmd.domain.dto.profile.OrderDTO order);

    Boolean takeOrder(String uid, String oid);

    Boolean UpdateOrderZt(String orderId, String ztNow, String ztTo);

    int deleteOrder(String orderId);

    int addDiagnosis(Diagnosis diagnosis);

    List<DiagnosisOutputDTO> getDiagnosisList(String userId);

    List<com.mmd.domain.dto.profile.OrderDTO> getOrdersOfMassager(String tid, String pid, String beginTime, String endTime);

    int updateOrderJe(double je, String oid);

    int updateOrder(String orderid, String paymenttype);

    void filterMassagers4Store(List<Massager> storeMassagers, String serviceTime, long beginTime, long endTime) throws ParseException;

    void updateOrderAfterBalancePay(String oid, double settle, String zt);

    String queryAidByOid(String oid);

}
