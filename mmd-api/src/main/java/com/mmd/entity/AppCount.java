package com.mmd.entity;

import com.mmd.domain.dto.profile.OrderDTO;

import java.util.List;

public class AppCount {
	private String xmmc;
	private int sl;
	private double time;
	private double xxmoney;
	private double money;
	private double payment;
	private String pid;
	private double settlemoney;
	private List<OrderDTO> list;

	public List<OrderDTO> getList() {
		return list;
	}

	public void setList(List<OrderDTO> list) {
		this.list = list;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	public String getXmmc() {
		return xmmc;
	}

	public void setXmmc(String xmmc) {
		this.xmmc = xmmc;
	}

	public int getSl() {
		return sl;
	}

	public void setSl(int sl) {
		this.sl = sl;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getXxmoney() {
		return xxmoney;
	}

	public void setXxmoney(double xxmoney) {
		this.xxmoney = xxmoney;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getSettlemoney() {
		return settlemoney;
	}

	public void setSettlemoney(double settlemoney) {
		this.settlemoney = settlemoney;
	}
}
