package com.mmd.utils.baiduAPI;

import lombok.Data;

@Data
public class Geocoder extends BaseResult {
	private int status;
	private GeocoderResult result;
}
