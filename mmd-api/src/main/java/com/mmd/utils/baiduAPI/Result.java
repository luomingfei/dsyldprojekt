package com.mmd.utils.baiduAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Result {
	private String name;
	private Location location;
	private String uid;
	private String city;
	private String district;
	private String business;
	private int cityid;
}
