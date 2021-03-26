package com.mmd.utils.baiduAPI;

import com.mmd.utils.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

public class BaiduAPI extends BaseAPI {
	private static String ak = "bTmpGWp05fQCL0vUZZqwU5qZ";
	private static String geotable_id = "122374";

	/**
	 * 根据关键字搜索
     */
	public static PlaceSuggestion getPlaceSuggestion(String query, String region) {
		HttpUriRequest httpUriRequest = RequestBuilder.get()
				.setUri("http://api.map.baidu.com/place/v2/suggestion?query=" + query + "&region=" + region
						+ "&output=json&ak=" + ak + "")
				.build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, PlaceSuggestion.class);

	}

	/**
	 * @param location
	 *            like 39.983424,116.322987
	 * @return
	 */
	public static Geocoder geocoder(String location) {
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(
				"http://api.map.baidu.com/geocoder/v2/?ak=" + ak + "&location=" + location + "&output=json&pois=1")
				.build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, Geocoder.class);
	}

	public static GeocoderByAddress geocoderByAddress(String address) {
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(
				"http://api.map.baidu.com/geocoder/v2/?ak=" + ak + "&address=" + address + "&output=json&pois=1")
				.build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, GeocoderByAddress.class);
	}

	public static BaiduIP IpAPI(String ip) {
		HttpUriRequest httpUriRequest = RequestBuilder.get()
				.setUri("http://api.map.baidu.com/location/ip?ak=" + ak + "&ip=" + ip + "&coor=bd09ll").build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, BaiduIP.class);
	}

	/**
	 * @param coords
	 *            like 114.21892734521,29.575429778924;
	 *            114.21892734521,29.575429778924
	 * @return
	 */
	public static IpConv geoconv(String coords) {
		HttpUriRequest httpUriRequest = RequestBuilder.get()
				.setUri("http://api.map.baidu.com/geoconv/v1/?coords=" + coords + "&from=1&to=5&ak=" + ak + "").build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, IpConv.class);
	}

	public static GeosearchNearby geosearchNearby(String location, int radius) {
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("http://api.map.baidu.com/geosearch/v3/nearby?ak="
				+ ak + "&geotable_id=" + geotable_id + "&location=" + location + "&radius=" + radius + "").build();
		return LocalHttpClient.executeJsonResultWithNoConv(httpUriRequest, GeosearchNearby.class);
	}
}
