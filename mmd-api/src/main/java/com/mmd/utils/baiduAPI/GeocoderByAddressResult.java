package com.mmd.utils.baiduAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocoderByAddressResult {
	private Location location;
	private int precise;
	private int confidence;
	private String level;
}
