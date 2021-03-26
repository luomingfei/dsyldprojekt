package com.mmd.pay.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.mmd.pay.config.AliPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class AlipayUtil {

    @Autowired
    private AliPayConfig hylAliPayConfig;

    public AlipayClient buildAliClient() throws AlipayApiException {
        //构造client
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId(hylAliPayConfig.getApp_id());
        //设置应用私钥
        certAlipayRequest.setPrivateKey(hylAliPayConfig.getMerchant_private_key());
        //设置请求格式，固定值json
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset(hylAliPayConfig.getCharset());
        //设置签名类型
        certAlipayRequest.setSignType(hylAliPayConfig.getSign_type());
        //设置应用公钥证书路径
        certAlipayRequest.setCertPath(hylAliPayConfig.getApp_cert_path());
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertPath(hylAliPayConfig.getAlipay_cert_path());
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertPath(hylAliPayConfig.getAlipay_root_cert_path());
        //构造client
        AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
        return alipayClient;
    }

    /**
     * 校验签名 证书模式
     *
     * @param request 请求对象
     * @return 是 /否
     * @throws AlipayApiException 支付异常
     */
    public boolean rsaCheckV1(HttpServletRequest request) throws AlipayApiException {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCertCheckV1(Map<String, String> params, String publicKeyCertPath, String charset,String signType)
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());//解决 no such provider: BC异常
        boolean flag = AlipaySignature.rsaCertCheckV1(params, hylAliPayConfig.getAlipay_cert_path(), "UTF-8", hylAliPayConfig.getSign_type());
        return flag;
    }

}
