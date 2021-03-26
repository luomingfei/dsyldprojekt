package com.mmd.entity;

import lombok.Data;

import java.util.Date;
@Data
public class TakeUpTime{
	private int id;
	private int tid;
	private String title;
	private String beginTime;
	private String endTime;
	private String isMonOk;
	private String isTuesOk;
	private String isWedOk;
	private String isThursOk;
	private String isFriOk;
	private String isSatOk;
	private String isSunOk;
	private String status;
	private String isEveryWeek;
	private String createtime;

	/**
	 * @param date
	 *            时间戳
	 * @return
	 */
	public String isThisDayOk(Date date) {
		switch (date.getDay()) {
		case 0:
			return isSunOk;
		case 1:
			return isMonOk;
		case 2:
			return isTuesOk;
		case 3:
			return isWedOk;
		case 4:
			return isThursOk;
		case 5:
			return isFriOk;
		case 6:
			return isSatOk;
		default:
			return "n";
		}
	}
}
