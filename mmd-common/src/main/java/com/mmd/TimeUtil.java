package com.mmd;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {
	/**
	 * 10位的时间戳转成yyyy-MM-dd
	 */
	public static String TimeConvert(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(Integer.parseInt(time) * 1000L));
		return date;
	}

	public static String date2String (Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 10位的时间戳转成yyyy-MM-dd HH:mm
	 */
	public static String TimeConvertMin(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date(Integer.parseInt(time) * 1000L));
		return date;
	}

	public static long strToTime(String rq) {
		return strToTime(rq, "yyyy-MM-dd");
	}

	public static long strToTime (String rq, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date;
		long timeStemp = 0;
		try {
			date = simpleDateFormat.parse(rq);
			timeStemp = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStemp;
	}

	public static Date string2Date (String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static long timeStrToLong(String sj) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date;
		long timeStemp = 0;
		try {
			date = simpleDateFormat.parse(sj);
			timeStemp = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeStemp+8*60*60*1000;
	}
	
	public static String timestampToStr(long timestamp) {
		 return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp * 1000));
	}

	public static int getMinuteDot (Calendar calendar) {
		int minute = calendar.get(Calendar.MINUTE);
		if (minute < 15) {
			return 0;
		} else if (minute < 30) {
			return 1;
		} else if (minute < 45) {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * 获取当天0点的时间戳，参数time含毫秒数
     */
	public static Long long2long (Long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time));
		try {
			return sdf.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取当天0点的字符串，参数time不含毫秒数
	 */
	public static String long2String (Long time) {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time * 1000));
	}

	public static int getTimeDot (long stamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(stamp);
		int dot = calendar.get(Calendar.HOUR_OF_DAY) * 2;
		return calendar.get(Calendar.MINUTE) >= 30 ? dot + 1 : dot;
	}
	
	/**
	 * 10位的时间戳转成yyyy-MM-dd
	 */
	public static String timestampToMMdd(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
		String date = sdf.format(new Date(Integer.parseInt(time) * 1000L));
		return date;
	}
}
