package com.mmd.entity;

import lombok.Data;

@Data
public class ExtraHours {
	private int id;
	private int tid;
	private int createtime;
	private double hours;
	private double price;
	private int servicetime;

}
