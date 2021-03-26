package com.mmd.domain.dto.alipay;

import lombok.Data;

/**
 * <p>
 * 阿里dto
 * </p>
 *
 * @author dsc
 * @since 2020年4月14日
 */
@Data
public class AliPayInputDTO {

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 优惠卷
     */
    private Integer cid;

    /**
     * 项目名称
     */
    private String name;

}
