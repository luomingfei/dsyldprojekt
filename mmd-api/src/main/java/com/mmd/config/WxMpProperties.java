package com.mmd.config;


import com.mmd.JsonUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * wechat mp properties
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {


        /**
         * 设置微信公众号的appid
         */
        private String appid;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;


    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
