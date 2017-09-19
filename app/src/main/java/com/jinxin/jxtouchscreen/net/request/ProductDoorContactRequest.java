package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.ProductDoorContact;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import java.util.List;

/**
 * Created by XTER on 2016/12/30.
 * 门磁数据请求
 */
public class ProductDoorContactRequest extends BaseJsonRequest<ProductDoorContact> {
	private String code;

	public ProductDoorContactRequest(ICallback<ProductDoorContact> callback,String type) {
		super(callback);
		this.code = type;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_DOOR_CONTACT_STATUS,
				NetConstant.TRD_USER_DEVICE, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String _secretKey =SPM.getStr(SPM.CONSTANT,NetConstant.KEY_SECRETKEY,"");
		String _account = AppUtil.getCurrentAccount();
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("code", code);
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
				List<ProductDoorContact> productDoorContacts = JSON.parseArray(jo1.toString(), ProductDoorContact.class);
				getCallback().onReceive(productDoorContacts);
			} else
				getCallback().onError(jo.getString("rspDesc"));
		}
	}

	@Override
	public void onError(String error) {

	}
}
