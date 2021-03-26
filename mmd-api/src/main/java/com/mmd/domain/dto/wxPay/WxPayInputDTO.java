package com.mmd.domain.dto.wxPay;

import lombok.Data;

/**
 * <p>
 * 微信支付dto
 * </p>
 *
 * @author dsc
 * @since 2020年4月14日
 */
@Data
public class WxPayInputDTO {

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

    private String type;


}
