package com.mmd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.entity.SalaryBill;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmd.entity.SalaryField;
import com.mmd.persistence.SalaryBillsPersistence;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface SalaryBillService extends IService<SalaryBill> {
    IPage<SalaryBill> getSalaryBills(IPage iPage, SalaryBillsPersistence salaryBillsPersistence);

    List<SalaryField> getSalaryField();
}
