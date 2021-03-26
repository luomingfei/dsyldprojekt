package com.mmd.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.domain.dto.output.ProductOutputDTO;
import com.mmd.entity.SalaryBill;
import com.mmd.dao.SalaryBillMapper;
import com.mmd.entity.SalaryField;
import com.mmd.persistence.SalaryBillsPersistence;
import com.mmd.service.SalaryBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SalaryBillServiceImpl extends ServiceImpl<SalaryBillMapper, SalaryBill> implements SalaryBillService {

    @Override
    public IPage<SalaryBill> getSalaryBills(IPage iPage, SalaryBillsPersistence salaryBillsPersistence) {
        IPage<SalaryBill> productData = baseMapper.getProductData(iPage, salaryBillsPersistence.getTid());
        return productData;
    }

    @Override
    public List<SalaryField> getSalaryField() {
        return baseMapper.getSalaryField();
    }
}
