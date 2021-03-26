package com.mmd.domain.dto.wxPay;

import lombok.Data;

/**
 * 支付 JS 请求
 * @author LiYi
 *
 */
@Data
public class PayJsRequest {

	private String appId;

	private String timeStamp;

	private String nonceStr;

	private String package_;

	private String signType;

	private String paySign;

}
