package com.mmd.utils.baiduAPI;

import lombok.Data;

@Data
public class Contents {
	private String uid;
	private String geotable_id;
	private String title;
	private String address;
	private String province;
	private String city;
	private String district;
	private int coord_type;
	private String[] location;
	private int distance;
	private int weight;
	private String tags;
	private String icon_style_id;
	private String create_time;
	private int type;
}
