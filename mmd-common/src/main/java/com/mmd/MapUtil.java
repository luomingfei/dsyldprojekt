package com.mmd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class MapUtil {

	private static Logger log = LoggerFactory.getLogger("MapUtil");

	/**
	 * Map key 排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> order(Map<String, String> map) {
		HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, String> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}

	/**
	 * 转换对象为map
	 * 
	 * @param object
	 * @param ignore
	 * @return
	 */
	public static Map<String, String> objectToMap(Object object, String... ignore) {
		Map<String, String> tempMap = new LinkedHashMap<String, String>();
		for (Field f : object.getClass().getDeclaredFields()) {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			boolean ig = false;
			if (ignore != null && ignore.length > 0) {
				for (String i : ignore) {
					if (i.equals(f.getName())) {
						ig = true;
						break;
					}
				}
			}
			if (ig) {
				continue;
			} else {
				Object o = null;
				try {
					o = f.get(object);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				tempMap.put(f.getName(), o == null ? "" : o.toString());
			}
		}
		return tempMap;
	}

	/**
	 * url 参数串连
	 * 
	 * @param map
	 * @param keyLower
	 * @param valueUrlencode
	 * @return
	 */
	public static String mapJoin(Map<String, String> map, boolean keyLower, boolean valueUrlencode) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : map.keySet()) {
			if (map.get(key) != null && !"".equals(map.get(key))) {
				try {
					String temp = (key.endsWith("_") && key.length() > 1) ? key.substring(0, key.length() - 1) : key;
					stringBuilder.append(keyLower ? temp.toLowerCase() : temp)
							.append("=").append(valueUrlencode
									? URLEncoder.encode(map.get(key), "utf-8").replace("+", "%20") : map.get(key))
							.append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		if (stringBuilder.length() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		return stringBuilder.toString();
	}

	/**
	 * 简单 xml 转换为 Map
	 * 
	 * @param strXML
	 * @return
	 */
	public static Map<String, String> xmlToMap(String strXML) throws ParserConfigurationException, IOException, SAXException {
		Map data = new HashMap<>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		String FEATURE = null;
		try {
			FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			documentBuilderFactory.setFeature(FEATURE, true);

			FEATURE = "http://xml.org/sax/features/external-general-entities";
			documentBuilderFactory.setFeature(FEATURE, false);

			FEATURE = "http://xml.org/sax/features/external-parameter-entities";
			documentBuilderFactory.setFeature(FEATURE, false);

			FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			documentBuilderFactory.setFeature(FEATURE, false);

			documentBuilderFactory.setXIncludeAware(false);
			documentBuilderFactory.setExpandEntityReferences(false);

		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException was thrown. The feature '" +
					FEATURE + "' is probably not supported by your XML processor.");

		}
		DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
		org.w3c.dom.Document doc = documentBuilder.parse(stream);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		for (int idx=0; idx<nodeList.getLength(); ++idx) {
			Node node = nodeList.item(idx);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				org.w3c.dom.Element element = (org.w3c.dom.Element) node;
				data.put(element.getNodeName(), element.getTextContent());
			}
		}
		try {
			stream.close();
		}
		catch (Exception ex) {

		}
		return data;
	}

}
