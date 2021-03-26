package com.mmd.utils.baiduAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocoderResult {
	private Location location = new Location();
	private String formatted_address = null;
	private String business = null;
	private AddressComponent addressComponent = new AddressComponent();
	private List<Pois> pois = new ArrayList<Pois>();
	private List<Object> poiRegions = new ArrayList<>();
	private String sematic_description = null;
	private int cityCode = 0;
}
