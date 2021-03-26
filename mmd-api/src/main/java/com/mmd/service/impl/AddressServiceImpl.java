package com.mmd.service.impl;

import com.mmd.domain.dto.order.AddressDTO;
import com.mmd.entity.Address;
import com.mmd.dao.AddressMapper;
import com.mmd.entity.Order;
import com.mmd.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
private final AddressMapper addressMapper;
    @Override
    public AddressDTO findAddressById(int did) {
        return addressMapper.findAddressById(did);
    }

    @Override
    public void addAddress(Address address) {
        addressMapper.addAddress(address);
    }

    @Override
    public void updateAddress(Address address) {
        addressMapper.updateAddress(address);
    }

    @Override
    public void deleteAddress(String userId, String did) {
        addressMapper.deleteAddress(userId, did);
    }

    @Override
    public List<Address> queryUserAddress(String userId) {
        return addressMapper.queryUserAddress(userId);
    }

    @Override
    public List<Order> findOrdersOfMassagerWillDoInFuture(String tid) {
        return baseMapper.findOrdersOfMassagerWillDoInFuture(tid);
    }

    @Override
    public int addAddressNew(Address address) {
        return baseMapper.addAddressNew(address);
    }

    @Override
    public int AddAddress(Address paramAddress) {
        return baseMapper.AddAddress(paramAddress);
    }

    @Override
    public void UpdateAddress(Address address) {
        baseMapper.updateAddress(address);
    }

    @Override
    public Address queryAddressById(int did) {
        return baseMapper.queryAddressById(did);
    }
}
