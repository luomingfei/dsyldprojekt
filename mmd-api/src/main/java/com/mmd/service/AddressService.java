package com.mmd.service;

import com.mmd.domain.dto.order.AddressDTO;
import com.mmd.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.entity.Order;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface AddressService extends IService<Address> {

    AddressDTO findAddressById(int did);

    void addAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(String userId, String did);

    List<Address> queryUserAddress(String userId);

    List<Order> findOrdersOfMassagerWillDoInFuture(String tid);

    int addAddressNew(Address address);

    int AddAddress(Address paramAddress);

    void UpdateAddress(Address address);

    Address queryAddressById(int did);

}
