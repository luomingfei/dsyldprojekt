package com.mmd.utils.baiduAPI;

import lombok.Data;

import java.util.List;
@Data
public class GeosearchNearby {
	private int status;
	private int size;
	private int total;
	private List<Contents> contents;
}
