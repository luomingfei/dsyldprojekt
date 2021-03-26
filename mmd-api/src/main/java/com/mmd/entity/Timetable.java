package com.mmd.entity;

import java.util.List;

public class Timetable {
	private String rq;
	private String nickDate;
	private List<Time> timetable;
	private List<List<Time>> schedule;

	public Timetable(String r, String n, List<Time> t) {
		rq = r;
		nickDate = n;
		timetable = t;
	}
	
	public Timetable(String r, List<List<Time>> s) {
		rq = r;
		schedule = s;
	}
	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getNickDate() {
		return nickDate;
	}

	public void setNickDate(String nickDate) {
		this.nickDate = nickDate;
	}

	public List<Time> getTimetable() {
		return timetable;
	}

	public void setTimetable(List<Time> timetable) {
		this.timetable = timetable;
	}

	public List<List<Time>> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<List<Time>> schedule) {
		this.schedule = schedule;
	}
}
