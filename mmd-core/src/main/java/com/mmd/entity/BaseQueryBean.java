package com.mmd.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 *  查询基础bean
 * </p>
 *
 * @author dsccc
 * @since 2020年3月24日
 */
@Data
public class BaseQueryBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String currentPage = "1";
    private String pageSize = "10";
    private String sort = "";
    private String order = "asc";

    public Page makePaging() {
        Page paging = new Page();
        if (!StringUtils.isEmpty(currentPage)) {
            paging.setCurrent(Integer.valueOf(currentPage));
        }
        if (!StringUtils.isEmpty(pageSize)) {
            paging.setSize(Integer.valueOf(pageSize));
        }
        if (!StringUtils.isEmpty(sort)) {
            if (!StringUtils.isEmpty(order)) {
                if (order.equalsIgnoreCase("true") || order.equalsIgnoreCase("asc")) {
                    paging.setAsc(sort);
                } else {
                    paging.setDesc(sort);
                }
            } else {
                paging.setAsc(sort);
            }
        }
        return paging;
    }

}
