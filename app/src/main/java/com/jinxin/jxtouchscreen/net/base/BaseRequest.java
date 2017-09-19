package com.jinxin.jxtouchscreen.net.base;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by XTER on 2016/9/19.
 * 抽象基类
 */
public abstract class BaseRequest<T> implements IRequest {
	private ICallback<T> callback = null;

	public BaseRequest(ICallback<T> callback) {
		this.callback = callback;
	}

	protected ICallback<T> getCallback() {
		return callback;
	}

	/**
	 * 请求报文头
	 *
	 * @return header 请求头
	 */
	public Header createRequestHeader() {
		return null;
	}

	/**
	 * 请求报文对象
	 *
	 * @return jsonobject JSON对象
	 * @throws JSONException
	 */
	public JSONObject createRequestBody() throws JSONException {
		return null;
	}

	/**
	 * 请求报文内容
	 *
	 * @return string 请求报文内容
	 */
	@Override
	public String getRequestText() {
		return null;
	}

}
