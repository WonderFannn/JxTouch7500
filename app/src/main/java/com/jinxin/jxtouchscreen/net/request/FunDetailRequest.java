package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.model.FunDetail;
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
 * 设备请求
 */
public class FunDetailRequest extends BaseJsonRequest<FunDetail> {
	public FunDetailRequest(ICallback<FunDetail> callback) {
		super(callback);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String password = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, "");
		String account = AppUtil.getCurrentAccount();
		String updateTime = SPM.getStr(account, NetConstant.KEY_FUN_DETAIL_LIST, NetConstant.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", password);
		serviceContent.put("account", account);
		serviceContent.put("updateTime", updateTime);
		return serviceContent;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_FUN_MANAGER,
				NetConstant.TRD_USER_DEVICE, NetConstant.ACTION_REQUEST,
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
				List<FunDetail> funDetails = JSON.parseArray(jo1.toString(), FunDetail.class);
				getCallback().onReceive(funDetails);
			} else
				getCallback().onError(jo.getString("rspDesc"));

			// 存更新时间
			String processTime = response.getString("processTime");
			String account = AppUtil.getCurrentAccount();
			SPM.saveStr(account,
					NetConstant.KEY_FUN_DETAIL_LIST, processTime);
		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
