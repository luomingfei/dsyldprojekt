package com.mmd.utils;


import com.mmd.entity.Massager;

import java.util.List;

public class Distance {
	private static double EARTH_RADIUS = 6378137.0D;
	private static double PI = 3.141592653589793D;
	private static double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
	private static double A = 6378245.0;
	private static double EE = 0.00669342162296594323;

	public static double getRad(double d) {
		return d * PI / 180.0D;
	}

	public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
		double radLat1 = getRad(lat1);
		double radLat2 = getRad(lat2);
		double a = radLat1 - radLat2;
		double b = getRad(lng1) - getRad(lng2);
		double s = 2.0D * Math
				.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D)
						+ Math.cos(radLat1) * Math.cos(radLat2)
						* Math.pow(Math.sin(b / 2.0D), 2.0D)));
		s *= EARTH_RADIUS;
		s = Math.round(s * 10000.0D) / 10000.0D;
		return s;
	}

	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	/**
	 * 地球坐标转换为火星坐标
     */
	public static Coord formEarth2Mars(double lat, double lng) {
		double dLat = transformLat(lng - 105.0, lat - 35.0);
		double dLon = transformLon(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * PI;
		double magic = Math.sin(radLat);
		magic = 1 - EE * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
		dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
		return new Coord(lng + dLon, lat + dLat);
	}

	/**
	 * 百度坐标转换为火星坐标
	 */
	public static Coord fromBD2Mars (double lng, double lat)
	{
		double x = lng - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		return new Coord(z * Math.cos(theta), z * Math.sin(theta));
	}

	/**
	 * 火星坐标转换为百度坐标
	 */
	public static Coord fromMars2BD(double lng, double lat) {
		double x = lng, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
		Coord coord = new Coord();
		coord.setLng(z * Math.cos(theta) + 0.0065);
		coord.setLat(z * Math.sin(theta) + 0.006);
		return new Coord(z * Math.sin(theta) + 0.006,z * Math.cos(theta) + 0.0065);
	}

	/**
	 * 将技师坐标（火星坐标）统一转换为百度坐标
	 */
	public static void batchTransMassagers (List<Massager> massagers) {
		if (massagers == null) {
			return;
		}
		for (Massager massager : massagers) {
			try {
				String[] location = massager.getLocation().split(",");
				Coord coord = fromMars2BD(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
				massager.setLocation(coord.getLng() + "," + coord.getLat());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void batchCalMassagerDistance(List<Massager> massagers, Double lng, Double lat) {
		if (massagers == null || lng == null || lat == null) {
			return;
		}
		for (Massager massager : massagers) {
			if (massager.getLocation() == null || massager.getLocation().indexOf(",") < 0) {
				continue;
			}
			String[] coord = massager.getLocation().split(",");
			massager.setDistance(getDistance(lng, lat, Double.parseDouble(coord[0]), Double.parseDouble(coord[1])));
		}
	}
}
