package com.jinxin.jxtouchscreen.net;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.net.base.FastJsonRequest;
import com.jinxin.jxtouchscreen.net.base.IRequest;

/**
 * Created by XTER on 2016/9/19.
 * 网络管理
 */
public class HttpManager {
	private static HttpManager httpManager;
	private RequestQueue requestQueue;

	private HttpManager() {
		requestQueue = getRequestQueue();
	}

	private RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(BaseApp.getContext());
		}
		return requestQueue;
	}

	public static synchronized HttpManager getInstance() {
		if (httpManager == null) {
			httpManager = new HttpManager();
		}
		return httpManager;
	}

	public void addRequest(Request request) {
		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);
	}

	public void addRequest(final IRequest request) {
		FastJsonRequest fastRequest = new FastJsonRequest(NetConstant.HTTP_ACCESSPATH, request.getRequestText(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					request.onResponse(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
			}
		});
		fastRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(fastRequest);
	}

	public void addRequest(String url, final IRequest request) {
		FastJsonRequest fastRequest = new FastJsonRequest(url, request.getRequestText(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					request.onResponse(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
			}
		});
		fastRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(fastRequest);
	}

}
