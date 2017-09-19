package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.model.User;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

/**
 * Created by Administrator on 2016/12/23.
 * 登录请求
 */
public class LoginRequest extends BaseJsonRequest<User> {
	private String account;
	private String password;

	public LoginRequest(ICallback<User> callback, String account, String password) {
		super(callback);
		this.account = account;
		this.password = password;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_ACCOUNT_MANAGER,
				NetConstant.TRD_ACCOUNT_LOGIN, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", account);
		serviceContent.put("password", password);
		return serviceContent;
	}

	@Override
	public void onResponse(JSONObject response) throws JSONException {
		if (response != null && response.containsKey("response")) {
			L.d(response.toString());
			JSONObject jo = response.getJSONObject("response");
			if (jo.getString("rspCode").equalsIgnoreCase("0000")) {
				if(TextUtils.isEmpty(response.getString("serviceContent")))
					return;
				JSONObject jo1 = response.getJSONObject("serviceContent");
				User user = JSON.parseObject(jo1.toString(), User.class);
				getCallback().onReceive(user);
			} else
				getCallback().onError(jo.getString("rspDesc"));
		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
