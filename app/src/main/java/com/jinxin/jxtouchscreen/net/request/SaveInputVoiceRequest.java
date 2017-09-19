package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

/**
 * Created by XTER on 2017/01/10.
 *
 */
public class SaveInputVoiceRequest extends BaseJsonRequest<String> {
	private String source;
	private String gatewayWhId;
	private String address485;

	public SaveInputVoiceRequest(ICallback<String> callback, String source, String gatewayWhId, String address485) {
		super(callback);
		this.source = source;
		this.gatewayWhId = gatewayWhId;
		this.address485 = address485;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_SAVE_INPUT_VOICE,
				NetConstant.TRD_SAVE_INPUT_VOICE, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String _secretKey = SPM.getStr(SPM.CONSTANT, NetConstant.KEY_SECRETKEY, "");
		String _account = AppUtil.getCurrentAccount();
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("source", source);
		serviceContent.put("gatewayWhId", gatewayWhId);
		serviceContent.put("address485", address485);
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
				JSONArray jo1 = response.getJSONArray("serviceContent");
				getCallback().onReceive(jo1.toString());
			} else
				getCallback().onError(jo.getString("rspDesc"));
		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
