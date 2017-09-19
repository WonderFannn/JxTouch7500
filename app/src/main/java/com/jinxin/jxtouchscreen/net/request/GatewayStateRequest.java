package com.jinxin.jxtouchscreen.net.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by XTER on 2017/01/11.
 *
 */
public class GatewayStateRequest extends StringRequest {


	public GatewayStateRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}
}
