package com.mmd.utils.baiduAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressComponent {
	private String adcode = null;
	private String country = null;
	private String province = null;
	private String city = null;
	private String district = null;
	private String street = null;
	private String street_number = null;
	private String country_code = null;
	private String direction = null;
	private String distance = null;
}
