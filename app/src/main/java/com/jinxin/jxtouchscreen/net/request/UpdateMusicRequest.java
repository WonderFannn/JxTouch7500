package com.jinxin.jxtouchscreen.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import java.util.List;

/**
 * Created by XTER on 2017/01/10.
 * 更新音乐
 */
public class UpdateMusicRequest extends BaseJsonRequest<Music> {
	private String source ;
	private String whId ;

	public UpdateMusicRequest(ICallback<Music> callback,String source,String whId) {
		super(callback);
		this.source=source;
		this.whId = whId;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_UPDATE_MUSIC_LIST,
				NetConstant.TRD_UPDATE_MUSIC_LIST, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TRUE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		String _secretKey = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, "");
		String _account = AppUtil.getCurrentAccount();

		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("source", source);
		serviceContent.put("whId", whId);
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
				List<Music> musicList = JSON.parseArray(jo1.toString(), Music.class);
				getCallback().onReceive(musicList);
			} else
				getCallback().onError(jo.getString("rspDesc"));

		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
