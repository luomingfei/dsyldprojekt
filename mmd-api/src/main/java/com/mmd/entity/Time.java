package com.mmd.entity;

import lombok.Data;

@Data
public class Time {
	private String time;
	private String state;
	private String select;

	public Time(String ti, String st) {
		this.time = ti;
		this.state = st;
	}
}
