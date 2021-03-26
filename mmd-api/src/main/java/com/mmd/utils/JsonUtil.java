package com.mmd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.alibaba.fastjson.JSON;
//TODO  用jackson改写相关代码或者使用fastjson中类似JsonView的方式改写controller

public class JsonUtil {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static <T> T parseObject(String json, Class<T> clazz) {
		try {
			return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJSONString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{}";
	}
}
