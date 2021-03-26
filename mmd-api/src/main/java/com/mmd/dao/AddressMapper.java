package com.mmd.dao;

import com.mmd.domain.dto.order.AddressDTO;
import com.mmd.entity.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AddressMapper extends BaseMapper<Address> {
    AddressDTO findAddressById(@Param("id") int paramInt);

    void addAddress(@Param("a") Address address);

    void updateAddress(@Param("a") Address address);

    void deleteAddress(@Param("uid") String userId, @Param("id") String did);

    List<Address> queryUserAddress(@Param("uid") String userId);

    List<Order> findOrdersOfMassagerWillDoInFuture(@Param("tid") String tid);

    int addAddressNew(@Param("a") Address address);

    int AddAddress(@Param("a") Address paramAddress);

    Address queryAddressById(@Param("did") int did);
}
