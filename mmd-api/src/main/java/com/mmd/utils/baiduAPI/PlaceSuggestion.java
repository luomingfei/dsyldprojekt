package com.mmd.utils.baiduAPI;

import java.util.List;

public class PlaceSuggestion extends BaseResult {
	private List<Result> result;

	public List<Result> getResult() {
		return result;
	}

	public void setResult(List<Result> result) {
		this.result = result;
	}
}
