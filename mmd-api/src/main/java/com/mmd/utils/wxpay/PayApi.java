package com.mmd.utils.wxpay;

import com.mmd.MapUtil;
import com.mmd.domain.dto.wxPay.SecapiPayRefund;
import com.mmd.domain.dto.wxPay.SecapiPayRefundResult;
import com.mmd.domain.dto.wxPay.Unifiedorder;
import com.mmd.domain.dto.wxPay.UnifiedorderResult;
import com.mmd.utils.LocalHttpClient;
import com.mmd.utils.XMLConverUtil;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.nio.charset.Charset;
import java.util.Map;

public class PayApi {

    protected static Header xmlHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());

    protected static final String MCH_URI = "https://api.mch.weixin.qq.com";

    /**
     * 统一下单
     *
     * @param unifiedorder
     * @param key
     * @return
     */
    public static UnifiedorderResult payUnifiedorder(Unifiedorder unifiedorder, String key) {
        Map<String, String> map = MapUtil.objectToMap(unifiedorder);
        if (key != null) {
            String sign = SignatureUtil.generateSign(map, key);
            unifiedorder.setSign(sign);
        }
        String unifiedorderXML = XMLConverUtil.convertToXML(unifiedorder);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(xmlHeader)
                .setUri(MCH_URI + "/pay/unifiedorder")
                .setEntity(new StringEntity(unifiedorderXML, Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.executeXmlResult(httpUriRequest, UnifiedorderResult.class);
    }

    /**
     * 申请退款
     *
     * 注意：
     *	1.交易时间超过半年的订单无法提交退款；
     *	2.微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额。
     * @param secapiPayRefund
     * @param key 商户支付密钥
     * @return
     */
    public static SecapiPayRefundResult secapiPayRefund(SecapiPayRefund secapiPayRefund, String key){
        Map<String,String> map = MapUtil.objectToMap( secapiPayRefund);
        String sign = SignatureUtil.generateSign(map,key);
        secapiPayRefund.setSign(sign);
        String secapiPayRefundXML = XMLConverUtil.convertToXML( secapiPayRefund);
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(xmlHeader)
                .setUri(MCH_URI + "/secapi/pay/refund")
                .setEntity(new StringEntity(secapiPayRefundXML,Charset.forName("utf-8")))
                .build();
        return LocalHttpClient.keyStoreExecuteXmlResult(secapiPayRefund.getMch_id(),httpUriRequest,SecapiPayRefundResult.class);
    }

}
