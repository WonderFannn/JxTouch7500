package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.model.CloudSetting;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 * 设备配置请求
 */
public class CloudSettingRequest extends BaseJsonRequest<CloudSetting> {
	public CloudSettingRequest(ICallback<CloudSetting> callback) {
		super(callback);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String password = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, "");
		String account = AppUtil.getCurrentAccount();
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", password);
		serviceContent.put("account", account);
		return serviceContent;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_SYNC_CLOUD_SETTING,
				NetConstant.TRD_GET_USER_CLOUND_SETTING, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
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
				List<CloudSetting> cloudSettings = JSON.parseArray(jo1.toString(), CloudSetting.class);
				getCallback().onReceive(cloudSettings);
			} else
				getCallback().onError(jo.getString("rspDesc"));

			// 存更新时间
			String processTime = response.getString("processTime");
			String account = AppUtil.getCurrentAccount();
			SPM.saveStr(account,
					NetConstant.KEY_CLOUD_SETTING, processTime);
		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
