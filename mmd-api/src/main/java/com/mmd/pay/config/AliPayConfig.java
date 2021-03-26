package com.mmd.pay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/***
 * 支付宝支付参数配置
 * @author dsccc
 * @since 2020年5月18日
 */

@Data
@Component
public class AliPayConfig {
    
    /***
     * 应用ID, APPID
     */
    @Value("${ALI.APPID}")
    private String app_id;
    /**
     * 商户私钥，PKCS8格式RSA2私钥
     */
    @Value("${ALI.PRIVATEKEY}")
    private String merchant_private_key;
    /**``
     * 支付宝公钥
     */
    @Value("${ALI.PUBLICKEY}")
    private String alipay_public_key;
    /**
     * 签名方式
     */
    @Value("${ALI.SIGN}")
    private String sign_type = "RSA2";
    /**
     *字符编码格式
     */
    protected String charset = "utf-8";
    /**
     * 支付宝网关
     */
    @Value("${ALI.GATEWAY_URL}")
    private String gateway_url;

    /**
     * 设置应用公钥证书路径
     */
    @Value("${ALI.APP_CERT_PATH}")
    private String app_cert_path;

    /**
     * 设置支付宝公钥证书路径
     */
    @Value("${ALI.ALIPAY_CERT_PATH}")
    private String alipay_cert_path;

    /**
     * 设置支付宝根证书路径
     */
    @Value("${ALI.ALIPAY_ROOT_CERT_PATH}")
    private String alipay_root_cert_path;

}
