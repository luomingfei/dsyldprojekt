package com.mmd.utils.baiduAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Pois {
	private String addr = null;
	private String cp = null;
	private String direction = null;
	private String distance = null;
	private String name = null;
	private String poiType = null;
	private Point point = new Point();
	private String tag = null;
	private String tel = null;
	private String uid = null;
	private String zip = null;
}
