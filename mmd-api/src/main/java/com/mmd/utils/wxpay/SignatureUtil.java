package com.mmd.utils.wxpay;

import java.util.Arrays;
import java.util.Map;

import com.mmd.MapUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

public class SignatureUtil {

	/**
	 * 生成 package 字符串
	 * @param map
	 * @param paternerKey
	 * @return
	 */
	public static String generatePackage(Map<String, String> map,String paternerKey){
		String sign = generateSign(map,paternerKey);
		Map<String,String> tmap = MapUtil.order(map);
		String s2 = MapUtil.mapJoin(tmap,false,true);
		return s2+"&sign="+sign;
	}

	/**
	 * 生成sign MD5 加密 toUpperCase
	 * @param map
	 * @param paternerKey
	 * @return
	 */
	public static String generateSign(Map<String, String> map,String paternerKey){
		Map<String, String> tmap = MapUtil.order(map);
		if(tmap.containsKey("sign")){
			tmap.remove("sign");
		}
		String str = MapUtil.mapJoin(tmap, false, false);
		return DigestUtils.md5Hex(str+"&key="+paternerKey).toUpperCase();
	}

	/**
	 * 生成 paySign
	 * @param map
	 * @param paternerKey
	 * @return
	 */
	public static String generatePaySign(Map<String, String> map,String paySignKey){
		if(paySignKey!=null){
			map.put("appkey",paySignKey);
		}
		Map<String, String> tmap = MapUtil.order(map);
		String str = MapUtil.mapJoin(tmap,true,false);
		return DigestUtils.shaHex(str);
	}




	/**
	 * 生成事件消息接收签名
	 * @param token
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static String generateEventMessageSignature(String token, String timestamp,String nonce) {
		String[] array = new String[]{token,timestamp,nonce};
		Arrays.sort(array);
		String s = StringUtils.arrayToDelimitedString(array, "");
		return DigestUtils.shaHex(s);
	}

}
