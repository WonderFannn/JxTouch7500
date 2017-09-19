package com.jinxin.jxtouchscreen.net.base;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by XTER on 2016/9/22.
 * 基类
 */
public abstract class BaseJsonRequest<T> extends BaseRequest<T> {
	public BaseJsonRequest(ICallback<T> callback) {
		super(callback);
	}

	@Override
	public String getRequestText() throws JSONException {
		Header header = createRequestHeader();
		JSONObject serviceContent = createRequestBody();
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("serviceContent", serviceContent);

		jsonObj.put("origDomain", header.getOrigDomain());
		jsonObj.put("homeDomain", header.getHomeDomain());
		jsonObj.put("bipCode", header.getBipCode());
		jsonObj.put("bipVer", header.getBipVer());
		jsonObj.put("activityCode", header.getActivityCode());
		jsonObj.put("processTime", header.getProcessTime());
		jsonObj.put("testFlag", header.getTestFlag());
		jsonObj.put("actionCode", header.getActionCode());
		jsonObj.put("response", null);

		return "$data=" + jsonObj.toString();
	}
}
