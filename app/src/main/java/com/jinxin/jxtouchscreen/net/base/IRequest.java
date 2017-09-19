package com.jinxin.jxtouchscreen.net.base;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by XTER on 2016/9/19.
 * 请求接口
 */
public interface IRequest {
	String getRequestText() throws JSONException;

	void onResponse(JSONObject response) throws JSONException;

	void onError(String error);
}
