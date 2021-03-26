package com.mmd.utils.baiduAPI;

import lombok.Data;

@Data
public class Content {
	private String address;
	private Address_detail address_detail;
	private Point point;

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Address_detail getAddress_detail() {
		return address_detail;
	}

	public void setAddress_detail(Address_detail address_detail) {
		this.address_detail = address_detail;
	}
}
