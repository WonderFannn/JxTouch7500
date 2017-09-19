package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.WHproductUnfrared;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import java.util.List;

/**
 * Created by XTER on 2017/01/10.
 *
 */
public class InfraedTranpondRequest extends BaseJsonRequest<WHproductUnfrared> {
	private int funId;

	public InfraedTranpondRequest(ICallback<WHproductUnfrared> callback, int funId) {
		super(callback);
		this.funId = funId;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.INFRARED_TRANSPORT_TEST_BIPCODE,
				NetConstant.TRD_USER_DEVICE_COMMAND, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String _secretKey = SPM.getStr(SPM.CONSTANT, NetConstant.KEY_SECRETKEY, "");
		String _account = AppUtil.getCurrentAccount();
		String _updateTime = SPM.getStr(_account, NetConstant.KEY_CUSTOMER_PATTERN_LIST, NetConstant.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("updateTime", _updateTime);
		serviceContent.put("fundIds", funId);
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
				List<WHproductUnfrared> wHproductUnfrareds = JSON.parseArray(jo1.toString(), WHproductUnfrared.class);
				getCallback().onReceive(wHproductUnfrareds);
			} else
				getCallback().onError(jo.getString("rspDesc"));

		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
