package com.jinxin.jxtouchscreen.net.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.jinxin.jxtouchscreen.util.L;

import java.io.UnsupportedEncodingException;

/**
 * Created by XTER on 2016/9/20.
 * 利用fastjson代替volley中原有json解析
 */
public class FastJsonRequest extends JsonRequest<JSONObject> {

	private final Response.Listener<JSONObject> mListener;

	public FastJsonRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody, listener, errorListener);
		mListener = listener;
		L.i(url+"?"+requestBody);
	}

	public FastJsonRequest(String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		this(requestBody == null ? Method.GET : Method.POST, url, requestBody, listener, errorListener);
	}

	@Override
	public String getBodyContentType() {
		return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers,PROTOCOL_CHARSET));
			return Response.success(JSON.parseObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}
}
