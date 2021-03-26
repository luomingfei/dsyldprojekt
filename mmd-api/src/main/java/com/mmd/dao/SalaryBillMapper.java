package com.mmd.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mmd.entity.SalaryBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.SalaryField;
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
public interface SalaryBillMapper extends BaseMapper<SalaryBill> {
    IPage<SalaryBill> getProductData(IPage page, @Param("tid") String tid);
    List<SalaryField> getSalaryField();
}
