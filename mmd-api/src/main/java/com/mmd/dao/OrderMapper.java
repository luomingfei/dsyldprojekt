package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.order.OrderDTO;
import com.mmd.domain.dto.output.DiagnosisOutputDTO;
import com.mmd.entity.Coupon;
import com.mmd.entity.Diagnosis;
import com.mmd.entity.Order;
import com.mmd.persistence.OrderPersistence;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 16:26
 */
public interface OrderMapper {
    void createCoupon(@Param("c") Coupon coupon);

    OrderDTO findOrderDetailById(String orderId);

    int queryProductTotalComment(@Param("ids") List<Integer>  ids);

    int queryProductGoodComment(@Param("ids") List<Integer>  ids);

    String queryUserLastSelectSexByPid(@Param("uid") String uid, @Param("pid") Integer pid);

    Integer queryUserLastSelectLevelByPid(@Param("uid") String uid, @Param("pid") Integer pid);

    List<OrderDTO> findOrdersOfMassagerWillDoInFuture(@Param("tid") String tid);

    List<OrderDTO> findOrdersOfMassagerToday(@Param("tid") String tid);

    Order FindOrderById(@Param("id") int id);

    String getLjjdById(@Param("Id")String id);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZt(IPage iPage, OrderPersistence orderPersistence);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersOfMassagerByZtAndTid(IPage iPage,@Param("o") OrderPersistence orderPersistence);

    IPage<com.mmd.domain.dto.profile.OrderDTO> findOrdersByZt(IPage iPage,
                                                              @Param("o") OrderPersistence orderPersistence);

    int canTake(@Param("oid") String oid);

    int closeTake(@Param("oid") String oid);

    int takeOrder(@Param("uid") String uid, @Param("oid") String oid);

    int AddOrder(@Param("o") com.mmd.domain.dto.profile.OrderDTO paramOrder);

    int UpdateOrderZt(@Param("orderId") String paramString1, @Param("ztNow") String paramString2,
                      @Param("ztTo") String paramString3);

    int deleteOrder(@Param("orderId") String orderId);

    int addDiagnosis(@Param("d") Diagnosis diagnosis);

    List<DiagnosisOutputDTO> getDiagnosisList(@Param("userId") String userId);

    List<com.mmd.domain.dto.profile.OrderDTO> getOrdersOfMassager(@Param("tid") String tid, @Param("pid") String pid,
                                                                  @Param("beginTime") String beginTime, @Param("endTime") String endTime);


    int updateOrderJe(@Param("je") double je, @Param("oid") String oid);

    int updateOrder(@Param("orderid") String paramString1, @Param("paymenttype") String paramString2);

    int updateOrderAfterBalancePay(@Param("oid") String oid, @Param("settle") double settle, @Param("zt") String zt);

    String queryAidByOid(@Param("oid") String oid);
}
