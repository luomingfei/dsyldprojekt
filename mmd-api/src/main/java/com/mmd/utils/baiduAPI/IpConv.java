package com.mmd.utils.baiduAPI;

import lombok.Data;

import java.util.List;
@Data
public class IpConv extends BaseResult {
	private int status;
	private List<Point> result;
}
