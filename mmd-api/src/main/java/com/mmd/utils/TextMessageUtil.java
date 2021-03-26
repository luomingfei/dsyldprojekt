package com.mmd.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Random;

public class TextMessageUtil {
	public static String URL = "http://smssh1.253.com/msg/";
	public static String ACCOUNT = "N3253537";
	public static String PWD = "7ytajqpJmfff75";

	/**
	 * 暂不提供使用，HttpBatchSendSM包含了单发
	 */
//	public static String send(String mobile, String msg, boolean needstatus,
//			String product, String extno) throws Exception {
//		return sendSMS("HttpSendSM", mobile, msg, needstatus, product, extno);
//	}

	public static String batchSend(String mobile, String msg, boolean needstatus, String product, String extno) throws Exception {
		return sendSMS("HttpBatchSendSM", mobile, msg, needstatus, product, extno);
	}

	/**
	 * @param uri
	 * @param mobile 手机号码，多个号码使用","分割
	 * @param msg 短信内容
	 * @param needstatus 是否需要状态报告，需要true，不需要false
	 * @return 返回值定义参见HTTP协议文档
	 * @throws Exception
	 */
	public static String sendSMS(String uri, String mobile, String msg, boolean needstatus, String product, String extno) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod();
		try {
			URI base = new URI(URL, false);
			method.setURI(new URI(base, uri, false));
			method.setQueryString(new NameValuePair[] {
					new NameValuePair("account", ACCOUNT),
					new NameValuePair("pswd", PWD),
					new NameValuePair("mobile", mobile),
					new NameValuePair("needstatus", String.valueOf(needstatus)),
					new NameValuePair("msg", msg),
					new NameValuePair("product", product),
					new NameValuePair("extno", extno), });
			int result = client.executeMethod(method);
			if (result == HttpStatus.SC_OK) {
				InputStream in = method.getResponseBodyAsStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				return URLDecoder.decode(baos.toString(), "UTF-8");
			} else {
				throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
			}
		} finally {
			method.releaseConnection();
		}

	}

	public static String getCode() {
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < 6; i++)
			result.append(array[i]);
		return result.toString();
	}

	public static boolean sendText2Massager4NewOrder (String phone, String oid) {
		if (phone != null && phone.trim().length() == 11) {
			String msg = "亲，您有新的订单，单号：" + oid + "，请及时确认！如有任何疑问，请致电：4006233816";
			boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
			String product = null;// 产品ID
			String extno = null;// 扩展码
			try {
				batchSend(phone.trim(), msg, needstatus, product, extno);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
