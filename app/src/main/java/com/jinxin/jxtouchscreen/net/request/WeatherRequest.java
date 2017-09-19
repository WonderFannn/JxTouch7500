package com.jinxin.jxtouchscreen.net.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.model.WeatherInfo;
import com.jinxin.jxtouchscreen.net.base.BaseRequest;
import com.jinxin.jxtouchscreen.net.base.ICallback;

/**
 * Created by XTER on 2016/9/20.
 * 天气请求
 */
public class WeatherRequest extends BaseRequest<WeatherInfo> {
	public WeatherRequest(ICallback<WeatherInfo> callback) {
		super(callback);
	}

	@Override
	public void onResponse(JSONObject response) {
		if (response != null && response.getString("error").equals("0")) {
			try {
				JSONArray ja = response.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data");
				WeatherInfo weatherInfo = JSON.parseObject(ja.getString(0), WeatherInfo.class);
				getCallback().onReceive(weatherInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(String error) {
		getCallback().onError(error);
	}
}
