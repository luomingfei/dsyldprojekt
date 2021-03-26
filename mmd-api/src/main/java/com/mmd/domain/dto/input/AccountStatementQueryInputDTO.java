package com.mmd.domain.dto.input;

import com.mmd.entity.BaseQueryBean;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * <p>
 * 账户信息查询dto
 * </p>
 *
 * @author dsc
 * @since 2020年2月13日
 */
@Data
@Accessors(chain = true)
public class AccountStatementQueryInputDTO extends BaseQueryBean {

    private Integer uid;

    private String beginDay;

    private String endDay;

}
